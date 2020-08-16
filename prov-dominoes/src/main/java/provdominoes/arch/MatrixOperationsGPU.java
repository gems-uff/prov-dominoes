package provdominoes.arch;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.la4j.matrix.functor.MatrixProcedure;
import org.la4j.matrix.sparse.CRSMatrix;

import processor.Cell;
import processor.MatrixProcessor;
import provdominoes.command.TextFilterData;
import provdominoes.domain.Configuration;
import provdominoes.util.Prov2DominoesUtil;
import provdominoes.util.SortIgnoreCase;

public class MatrixOperationsGPU implements MatrixOperations {

	private long matPointer = 0;
	private int[] rows;
	private int[] cols;
	private float[] values;
	private CRSMatrix matrix;

	private MatrixDescriptor matrixDescriptor;

	public MatrixDescriptor getMatrixDescriptor() {
		return matrixDescriptor;
	}

	public MatrixOperationsGPU(MatrixDescriptor _matrixDescriptor) throws Exception {
		if (!Session.isSessionStarted())
			throw new Exception("Session is not started");

		matrixDescriptor = _matrixDescriptor;

		matPointer = MatrixProcessor.createSparseMatrix(matrixDescriptor.getNumRows(), matrixDescriptor.getNumCols());

		Session.register2DMatrix(this);
	}

	public MatrixOperationsGPU(MatrixDescriptor _matrixDescriptor, int[] rows, int[] cols) throws Exception {
		if (!Session.isSessionStarted())
			throw new Exception("Session is not started");

		matrixDescriptor = _matrixDescriptor;

		matPointer = MatrixProcessor.createDenseMatrix(_matrixDescriptor.getNumRows(), _matrixDescriptor.getNumCols());

		Session.register2DMatrix(this);
		this.rows = rows;
		this.cols = cols;
	}

	@Override
	public int getMemUsed() {
		return matrixDescriptor.getNumCols() * matrixDescriptor.getNumRows() * (Float.SIZE / 8);
	}

	public void finalize() {
		MatrixProcessor.deleteSparseMatrix(matPointer);
	}

	private MatrixOperationsGPU sortEqualTo(MatrixDescriptor md) throws Exception {
		Cell[] clls = MatrixProcessor.getDenseData(matPointer, rows, cols);
		matrix = Prov2DominoesUtil.cells2Matrix(Arrays.asList(clls), getMatrixDescriptor().getNumRows(),
				getMatrixDescriptor().getNumCols());
		int[] rws = new int[clls.length];
		int[] cls = new int[clls.length];
		float[] vls = new float[clls.length];
		for (int i = 0; i < md.getNumRows(); i++) {
			for (int j = 0; j < md.getNumCols(); j++) {
				int oldI = this.getMatrixDescriptor().getRowElementIndex(md.getRowAt(i));
				int oldJ = this.getMatrixDescriptor().getColElementIndex(md.getColumnAt(j));
				rws[i * md.getNumCols() + j] = i;
				cls[i * md.getNumCols() + j] = j;
				vls[i * md.getNumCols() + j] = new Double(matrix.get(oldI, oldJ)).floatValue();
			}
		}
		MatrixOperationsGPU result = new MatrixOperationsGPU(md, rws, cls);
		MatrixProcessor.setDenseData(result.matPointer, vls);
		return result;
	}

	@Override
	public MatrixOperations subtract(MatrixOperations other) throws Exception {
		long startTime = 0;
		long endTime = 0;
		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		MatrixDescriptor otherDescriptor = other.getMatrixDescriptor();
		if (matrixDescriptor.getNumRows() != otherDescriptor.getNumRows()
				|| matrixDescriptor.getNumCols() != otherDescriptor.getNumCols())
			throw new Exception("Matrix cannot be subtracted!");

		updateMatrix(true);
		MatrixOperationsGPU sub1 = new MatrixOperationsGPU(this.getMatrixDescriptor(), rows, cols);
		MatrixProcessor.setDenseData(sub1.matPointer, values);

		other.updateMatrix(true);
		MatrixOperationsGPU sub2 = new MatrixOperationsGPU(other.getMatrixDescriptor(), other.getRows(),
				other.getCols());
		MatrixProcessor.setDenseData(sub2.matPointer, other.getValues());
		if (!Configuration.tuning) {
			MatrixOperationsGPU temp = sub2.sortEqualTo(this.getMatrixDescriptor());
			MatrixProcessor.deleteDenseMatrix(sub2.matPointer);
			sub2.matPointer = temp.matPointer;
			sub2.setRows(temp.getRows());
			sub2.setCols(temp.getCols());
		}

		MatrixDescriptor resultDesc = new MatrixDescriptor(matrixDescriptor.getRowType(),
				matrixDescriptor.getColType());
		resultDesc.setRowsDesc(this.getMatrixDescriptor().getRowsDesc());
		resultDesc.setColumnsDesc(this.getMatrixDescriptor().getColumnsDesc());

		MatrixOperationsGPU subtract = new MatrixOperationsGPU(resultDesc, rows, cols);

		MatrixProcessor.subtract(sub1.matPointer, sub2.matPointer, resultDesc.getNumRows() * resultDesc.getNumCols(),
				subtract.matPointer);

		Cell[] clls = MatrixProcessor.getDenseData(subtract.matPointer, rows, cols);
		CRSMatrix resp = Prov2DominoesUtil.cells2Matrix(Arrays.asList(clls), resultDesc.getNumRows(),
				resultDesc.getNumCols());
		MatrixProcessor.deleteDenseMatrix(subtract.matPointer);
		MatrixProcessor.deleteDenseMatrix(sub1.matPointer);
		MatrixProcessor.deleteDenseMatrix(sub2.matPointer);

		subtract = new MatrixOperationsGPU(resultDesc);
		subtract.updateSparsePointer(resp);

		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for subtraction in ms: " + df.format(d));
		}
		return subtract;
	}

	@Override
	public MatrixOperations sum(MatrixOperations other) throws Exception {
		long startTime = 0;
		long endTime = 0;
		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		MatrixDescriptor otherDescriptor = other.getMatrixDescriptor();
		if (matrixDescriptor.getNumRows() != otherDescriptor.getNumRows()
				|| matrixDescriptor.getNumCols() != otherDescriptor.getNumCols())
			throw new Exception("Matrix cannot be added!");

		updateMatrix(true);
		MatrixOperationsGPU sum1 = new MatrixOperationsGPU(this.getMatrixDescriptor(), rows, cols);
		MatrixProcessor.setDenseData(sum1.matPointer, values);

		other.updateMatrix(true);
		MatrixOperationsGPU sum2 = new MatrixOperationsGPU(other.getMatrixDescriptor(), other.getRows(),
				other.getCols());
		MatrixProcessor.setDenseData(sum2.matPointer, other.getValues());
		if (!Configuration.tuning) {
			MatrixOperationsGPU temp = sum2.sortEqualTo(this.getMatrixDescriptor());
			MatrixProcessor.deleteDenseMatrix(sum2.matPointer);
			sum2.matPointer = temp.matPointer;
			sum2.setRows(temp.getRows());
			sum2.setCols(temp.getCols());
		}

		MatrixDescriptor resultDesc = new MatrixDescriptor(matrixDescriptor.getRowType(),
				matrixDescriptor.getColType());
		resultDesc.setRowsDesc(this.getMatrixDescriptor().getRowsDesc());
		resultDesc.setColumnsDesc(this.getMatrixDescriptor().getColumnsDesc());

		MatrixOperationsGPU sum = new MatrixOperationsGPU(resultDesc, rows, cols);

		MatrixProcessor.sum(sum1.matPointer, sum2.matPointer, resultDesc.getNumRows() * resultDesc.getNumCols(),
				sum.matPointer);

		Cell[] clls = MatrixProcessor.getDenseData(sum.matPointer, rows, cols);
		CRSMatrix resp = Prov2DominoesUtil.cells2Matrix(Arrays.asList(clls), resultDesc.getNumRows(),
				resultDesc.getNumCols());
		MatrixProcessor.deleteDenseMatrix(sum.matPointer);
		MatrixProcessor.deleteDenseMatrix(sum1.matPointer);
		MatrixProcessor.deleteDenseMatrix(sum2.matPointer);

		sum = new MatrixOperationsGPU(resultDesc);
		sum.updateSparsePointer(resp);

		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for addition in ms: " + df.format(d));
		}
		return sum;
	}

	public MatrixOperations multiply(MatrixOperations other) throws Exception {
		long startTime = 0;
		long endTime = 0;
		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		MatrixDescriptor otherDescriptor = other.getMatrixDescriptor();
		if (matrixDescriptor.getNumCols() != otherDescriptor.getNumRows())
			throw new Exception("Matrix cannot be multiplied!");

		MatrixDescriptor resultDesc = new MatrixDescriptor(matrixDescriptor.getRowType(), otherDescriptor.getColType());

		resultDesc.setRowsDesc(this.getMatrixDescriptor().getRowsDesc());
		resultDesc.setColumnsDesc(other.getMatrixDescriptor().getColumnsDesc());

		MatrixOperationsGPU result = new MatrixOperationsGPU(resultDesc);
		if (!Configuration.tuning) {
			MatrixOperations mult1 = new MatrixOperationsGPU(this.matrixDescriptor);
			MatrixOperations mult2 = new MatrixOperationsGPU(other.getMatrixDescriptor());
			updateMatrix(false);
			other.updateMatrix(false);
			((MatrixOperationsGPU) mult1).updateSparsePointer(matrix);
			mult1 = mult1.sortColumns();
			((MatrixOperationsGPU) mult2).updateSparsePointer(other.getMatrix());
			mult2 = mult2.sortRows();
			MatrixProcessor.multiply(((MatrixOperationsGPU) mult1).matPointer, ((MatrixOperationsGPU) mult2).matPointer,
					result.matPointer);
			MatrixProcessor.deleteSparseMatrix(((MatrixOperationsGPU) mult1).matPointer);
			MatrixProcessor.deleteSparseMatrix(((MatrixOperationsGPU) mult2).matPointer);
		} else {
			MatrixProcessor.multiply(matPointer, ((MatrixOperationsGPU) other).matPointer, result.matPointer);
		}

		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for multiplication in ms: " + df.format(d));
		}
		return result;
	}

	@Override
	public MatrixOperations transpose() throws Exception {
		long startTime = 0;
		long endTime = 0;
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getColType(),
				this.matrixDescriptor.getRowType());
		_newDescriptor.setRowsDesc(this.matrixDescriptor.getColumnsDesc());
		_newDescriptor.setColumnsDesc(this.matrixDescriptor.getRowsDesc());
		MatrixOperationsGPU transpose = null;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		transpose = new MatrixOperationsGPU(_newDescriptor);
		MatrixProcessor.transpose(matPointer, transpose.matPointer);
		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for transposition in ms: " + df.format(d));
		}
		return transpose;
	}

	@Override
	public MatrixOperations confidence() throws Exception {
		long startTime = 0;
		long endTime = 0;
		MatrixDescriptor _newDescriptor = this.matrixDescriptor;
		MatrixOperationsGPU confidence = null;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		confidence = new MatrixOperationsGPU(_newDescriptor);
		MatrixProcessor.confidence(matPointer, confidence.matPointer);
		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for confidence matrix generation in ms: " + df.format(d));
		}
		return confidence;
	}

	@Override
	public MatrixOperations transitiveClosure() throws Exception {
		long startTime = 0;
		long endTime = 0;
		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		MatrixDescriptor _newDescriptor = this.matrixDescriptor;
		MatrixOperationsGPU transitiveClosure = null;

		updateMatrix(true);

		MatrixOperationsGPU tc1 = new MatrixOperationsGPU(_newDescriptor, rows, cols);
		MatrixProcessor.setDenseData(tc1.matPointer, values);
		MatrixOperationsGPU tc2 = new MatrixOperationsGPU(_newDescriptor, rows, cols);
		MatrixProcessor.transitiveClosure(_newDescriptor.getNumRows(), tc1.matPointer, tc2.matPointer);
		Cell[] clls = MatrixProcessor.getDenseData(tc2.matPointer, rows, cols);
		CRSMatrix resp = Prov2DominoesUtil.cells2Matrix(Arrays.asList(clls), _newDescriptor.getNumRows(),
				_newDescriptor.getNumCols());
		transitiveClosure = new MatrixOperationsGPU(_newDescriptor);
		transitiveClosure.updateSparsePointer(resp);
		MatrixProcessor.deleteDenseMatrix(tc1.matPointer);
		MatrixProcessor.deleteDenseMatrix(tc2.matPointer);

		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for transitive closure in ms: " + df.format(d));
		}
		return transitiveClosure;
	}

	@Override
	public boolean isEmpty() {
		return matPointer == 0;
	}

	@Override
	public MatrixOperations binarize() throws Exception {
		long startTime = 0;
		long endTime = 0;
		MatrixDescriptor _newDescriptor = this.matrixDescriptor;
		MatrixOperationsGPU binarize = null;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		binarize = new MatrixOperationsGPU(_newDescriptor);
		MatrixProcessor.binarize(matPointer, binarize.matPointer);
		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for binarization in ms: " + df.format(d));
		}
		return binarize;
	}

	@Override
	public MatrixOperations invert() throws Exception {
		long startTime = 0;
		long endTime = 0;
		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		MatrixDescriptor _newDescriptor = this.matrixDescriptor;
		MatrixOperationsGPU invert = null;

		updateMatrix(true);

		MatrixOperationsGPU invert1 = new MatrixOperationsGPU(_newDescriptor, rows, cols);
		MatrixProcessor.setDenseData(invert1.matPointer, values);
		MatrixOperationsGPU invert2 = new MatrixOperationsGPU(_newDescriptor, rows, cols);
		MatrixProcessor.invert(_newDescriptor.getNumRows() * _newDescriptor.getNumCols(), invert1.matPointer,
				invert2.matPointer);
		Cell[] clls = MatrixProcessor.getDenseData(invert2.matPointer, rows, cols);
		CRSMatrix resp = Prov2DominoesUtil.cells2Matrix(Arrays.asList(clls), _newDescriptor.getNumRows(),
				_newDescriptor.getNumCols());
		invert = new MatrixOperationsGPU(_newDescriptor);
		invert.updateSparsePointer(resp);
		MatrixProcessor.deleteDenseMatrix(invert1.matPointer);
		MatrixProcessor.deleteDenseMatrix(invert2.matPointer);

		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for invertion in ms: " + df.format(d));
		}
		return invert;
	}

	@Override
	public MatrixOperations lowerDiagonal() throws Exception {
		long startTime = 0;
		long endTime = 0;
		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		MatrixDescriptor _newDescriptor = this.matrixDescriptor;
		MatrixOperationsGPU lowerDiagonal = null;

		updateMatrix(true);

		MatrixOperationsGPU lowerDiagonal1 = new MatrixOperationsGPU(_newDescriptor, rows, cols);
		MatrixProcessor.setDenseData(lowerDiagonal1.matPointer, values);
		MatrixOperationsGPU lowerDiagonal2 = new MatrixOperationsGPU(_newDescriptor, rows, cols);
		MatrixProcessor.lowerDiagonal(_newDescriptor.getNumRows(), lowerDiagonal1.matPointer,
				lowerDiagonal2.matPointer);
		Cell[] clls = MatrixProcessor.getDenseData(lowerDiagonal2.matPointer, rows, cols);
		CRSMatrix resp = Prov2DominoesUtil.cells2Matrix(Arrays.asList(clls), _newDescriptor.getNumRows(),
				_newDescriptor.getNumCols());
		lowerDiagonal = new MatrixOperationsGPU(_newDescriptor);
		lowerDiagonal.updateSparsePointer(resp);
		MatrixProcessor.deleteDenseMatrix(lowerDiagonal1.matPointer);
		MatrixProcessor.deleteDenseMatrix(lowerDiagonal2.matPointer);

		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for build lower triangular matrix in ms: " + df.format(d));
		}
		return lowerDiagonal;
	}

	@Override
	public MatrixOperations upperDiagonal() throws Exception {
		long startTime = 0;
		long endTime = 0;
		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		MatrixDescriptor _newDescriptor = this.matrixDescriptor;
		MatrixOperationsGPU upperDiagonal = null;

		updateMatrix(true);

		MatrixOperationsGPU upperDiagonal1 = new MatrixOperationsGPU(_newDescriptor, rows, cols);
		MatrixProcessor.setDenseData(upperDiagonal1.matPointer, values);
		MatrixOperationsGPU upperDiagonal2 = new MatrixOperationsGPU(_newDescriptor, rows, cols);
		MatrixProcessor.upperDiagonal(_newDescriptor.getNumRows(), upperDiagonal1.matPointer,
				upperDiagonal2.matPointer);
		Cell[] clls = MatrixProcessor.getDenseData(upperDiagonal2.matPointer, rows, cols);
		CRSMatrix resp = Prov2DominoesUtil.cells2Matrix(Arrays.asList(clls), _newDescriptor.getNumRows(),
				_newDescriptor.getNumCols());
		upperDiagonal = new MatrixOperationsGPU(_newDescriptor);
		upperDiagonal.updateSparsePointer(resp);
		MatrixProcessor.deleteDenseMatrix(upperDiagonal1.matPointer);
		MatrixProcessor.deleteDenseMatrix(upperDiagonal2.matPointer);

		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for build upper triangular matrix in ms: " + df.format(d));
		}
		return upperDiagonal;
	}

	@Override
	public MatrixOperations diagonalize() throws Exception {
		long startTime = 0;
		long endTime = 0;
		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		MatrixDescriptor _newDescriptor = this.matrixDescriptor;
		MatrixOperationsGPU diagonal = null;

		updateMatrix(true);

		MatrixOperationsGPU diagonal1 = new MatrixOperationsGPU(_newDescriptor, rows, cols);
		MatrixProcessor.setDenseData(diagonal1.matPointer, values);
		MatrixOperationsGPU diagonal2 = new MatrixOperationsGPU(_newDescriptor, rows, cols);
		MatrixProcessor.diagonalize(_newDescriptor.getNumRows(), diagonal1.matPointer, diagonal2.matPointer);
		Cell[] clls = MatrixProcessor.getDenseData(diagonal2.matPointer, rows, cols);
		CRSMatrix resp = Prov2DominoesUtil.cells2Matrix(Arrays.asList(clls), _newDescriptor.getNumRows(),
				_newDescriptor.getNumCols());
		diagonal = new MatrixOperationsGPU(_newDescriptor);
		diagonal.updateSparsePointer(resp);
		MatrixProcessor.deleteDenseMatrix(diagonal1.matPointer);
		MatrixProcessor.deleteDenseMatrix(diagonal2.matPointer);

		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for diagonalize the matrix in ms: " + df.format(d));
		}
		return diagonal;
	}

	// Pending GPU implementation
	@Override
	public MatrixOperations sortRows() throws Exception {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}

		updateMatrix(false);

		MatrixDescriptor _matrixDescriptor = new MatrixDescriptor(matrixDescriptor.getRowType(),
				matrixDescriptor.getColType());
		List<String> rowsDesc = new ArrayList<>(matrixDescriptor.getRowsDesc());
		Map<String, Integer> rowsIndexes = new HashMap<>();
		for (int i = 0; i < rowsDesc.size(); i++) {
			rowsIndexes.put(rowsDesc.get(i), i);
		}
		Collections.sort(rowsDesc, new SortIgnoreCase());
		_matrixDescriptor.setRowsDesc(rowsDesc);
		_matrixDescriptor.setColumnsDesc(matrixDescriptor.getColumnsDesc());

		double[][] mtx = new double[matrixDescriptor.getNumRows()][matrixDescriptor.getNumCols()];

		for (int i = 0; i < matrixDescriptor.getNumRows(); i++) {
			for (int j = 0; j < matrixDescriptor.getNumCols(); j++) {
				mtx[i][j] = matrix.get(rowsIndexes.get(rowsDesc.get(i)), j);
			}
		}
		MatrixOperationsGPU result = new MatrixOperationsGPU(_matrixDescriptor);
		result.updateSparsePointer(new CRSMatrix(mtx));
		result.updateMatrix(false);
		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for Sort by Rows Asc in ms: " + df.format(d));
		}
		return result;
	}

	@Override
	public MatrixOperations sortColumns() throws Exception {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}

		updateMatrix(false);

		MatrixDescriptor _matrixDescriptor = new MatrixDescriptor(matrixDescriptor.getRowType(),
				matrixDescriptor.getColType());
		List<String> colsDesc = new ArrayList<>(matrixDescriptor.getColumnsDesc());
		Map<String, Integer> colsIndexes = new HashMap<>();
		for (int i = 0; i < colsDesc.size(); i++) {
			colsIndexes.put(colsDesc.get(i), i);
		}
		Collections.sort(colsDesc, new SortIgnoreCase());
		_matrixDescriptor.setColumnsDesc(colsDesc);
		_matrixDescriptor.setRowsDesc(matrixDescriptor.getRowsDesc());

		MatrixOperationsGPU result = new MatrixOperationsGPU(_matrixDescriptor);

		double[][] mtx = new double[matrixDescriptor.getNumRows()][matrixDescriptor.getNumCols()];
		for (int i = 0; i < matrixDescriptor.getNumRows(); i++) {
			for (int j = 0; j < matrixDescriptor.getNumCols(); j++) {
				mtx[i][j] = matrix.get(i, colsIndexes.get(colsDesc.get(j)));
			}
		}

		result.updateSparsePointer(new CRSMatrix(mtx));
		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for Sort by Columns Asc in ms: " + df.format(d));
		}
		return result;
	}

	@Override
	public CRSMatrix getMatrix() {
		return matrix;
	}

	@Override
	public void setMatrix(CRSMatrix matrix) {
		this.matrix = matrix;
	}

	@Override
	// TODO: CPU
	public MatrixOperations standardScoreDense() {
		long startTime = 0;
		long endTime = 0;
		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getRowType(),
				this.matrixDescriptor.getColType());

		_newDescriptor.setRowsDesc(this.getMatrixDescriptor().getRowsDesc());
		_newDescriptor.setColumnsDesc(this.getMatrixDescriptor().getColumnsDesc());

		MatrixOperationsCPU result = new MatrixOperationsCPU(_newDescriptor);

		float meanCol[] = new float[result.getMatrixDescriptor().getNumCols()];
		float sdCol[] = new float[result.getMatrixDescriptor().getNumCols()];
		float values[] = new float[result.getMatrixDescriptor().getNumCols()];
		int numElements[] = new int[result.getMatrixDescriptor().getNumCols()];

		matrix.each(new MatrixProcedure() {

			@Override
			public void apply(int row, int col, double value) {
				numElements[col]++;
				values[col] += value;
			}
		});

		for (int i = 0; i < values.length; i++)
			meanCol[i] = values[i] / (float) numElements[i];

		matrix.each(new MatrixProcedure() {

			@Override
			public void apply(int row, int col, double value) {
				sdCol[col] += (value - meanCol[col]) * (value - meanCol[col]);
				values[col] += value;
			}
		});

		for (int i = 0; i < sdCol.length; i++)
			sdCol[i] = (float) Math.sqrt((double) (sdCol[i] * (1.0f / (float) numElements[i])));

		ArrayList<Cell> _matrix = new ArrayList<>();
		matrix.each(new MatrixProcedure() {

			@Override
			public void apply(int row, int col, double value) {
				Cell _cell = new Cell();
				_cell.col = col;
				_cell.row = row;
				_cell.value = ((float) value - meanCol[col]) / sdCol[col];
				_matrix.add(_cell);
			}
		});

		result.setData(_matrix);
		result.setUnderlyingElements(null);
		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for standard score dense in ms: " + df.format(d));
		}
		return result;
	}

	// TODO Pending GPU implementation
	@Override
	public MatrixOperations meanAndSD() {
		long startTime = 0;
		long endTime = 0;
		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getColType(),
				this.matrixDescriptor.getRowType());

		_newDescriptor.AddRowDesc("MEAN");
		_newDescriptor.AddRowDesc("SD");

		_newDescriptor.setColumnsDesc(this.matrixDescriptor.getColumnsDesc());

		MatrixOperationsCPU result = new MatrixOperationsCPU(_newDescriptor);

		float meanCol[] = new float[result.getMatrixDescriptor().getNumCols()];
		float sdCol[] = new float[result.getMatrixDescriptor().getNumCols()];
		float values[] = new float[result.getMatrixDescriptor().getNumCols()];
		int numElements[] = new int[result.getMatrixDescriptor().getNumCols()];

		matrix.eachNonZero(new MatrixProcedure() {

			@Override
			public void apply(int row, int col, double value) {
				numElements[col]++;
				values[col] += value;
			}
		});

		for (int i = 0; i < values.length; i++)
			meanCol[i] = values[i] / (float) numElements[i];

		matrix.eachNonZero(new MatrixProcedure() {

			@Override
			public void apply(int row, int col, double value) {
				sdCol[col] += (value - meanCol[col]) * (value - meanCol[col]);
			}
		});

		for (int i = 0; i < sdCol.length; i++)
			sdCol[i] = (float) Math.sqrt((double) (sdCol[i] * (1.0d / (double) numElements[i])));

		ArrayList<Cell> _data = new ArrayList<>();
		for (int i = 0; i < sdCol.length; i++) {
			Cell cMean = new Cell(0, i, meanCol[i]);
			Cell cStd = new Cell(1, i, sdCol[i]);
			_data.add(cMean);
			_data.add(cStd);
		}

		result.setData(_data);
		result.setUnderlyingElements(null);
		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for mean & standard deviation in ms: " + df.format(d));
		}
		return result;
	}

	// TODO Pending GPU implementation
	@Override
	public MatrixOperations trim() throws Exception {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}

		updateMatrix(false);

		MatrixOperationsGPU result = null;
		ArrayList<Cell> newMatrix = new ArrayList<>();
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getRowType(),
				this.matrixDescriptor.getColType());
		boolean[] rowsFilled = new boolean[matrix.rows()];
		boolean[] colsFilled = new boolean[matrix.columns()];
		matrix.eachNonZero(new MatrixProcedure() {
			@Override
			public void apply(int row, int col, double value) {
				rowsFilled[row] = true;
				colsFilled[col] = true;
			}
		});
		int di = 0;
		int dj = 0;
		boolean colAdded = false;
		for (int i = 0; i < matrix.rows(); i++) {
			if (!rowsFilled[i]) {
				di++;
				continue;
			}
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
			for (int j = 0; j < matrix.columns(); j++) {
				if (!colsFilled[j]) {
					dj++;
					continue;
				}
				Double v = matrix.get(i, j);
				if (!colAdded) {
					_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(j));
				}
				newMatrix.add(new Cell(i - di, j - dj, v.floatValue()));

			}
			dj = 0;
			colAdded = true;
		}

		result = new MatrixOperationsGPU(_newDescriptor);
		result.updateSparsePointer(
				Prov2DominoesUtil.cells2Matrix(newMatrix, _newDescriptor.getNumRows(), _newDescriptor.getNumCols()));
		Long cells = new Long(matrixDescriptor.getNumRows() * matrixDescriptor.getNumCols()
				- _newDescriptor.getNumRows() * _newDescriptor.getNumCols());
		Double p = new Double(cells.doubleValue() / (matrixDescriptor.getNumRows() * matrixDescriptor.getNumCols()));
		System.out
				.println("# cells removed: " + cells + ". % cells removed: " + (new DecimalFormat("##.##%").format(p)));
		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for TRIM in ms: " + df.format(d));
		}
		return result;
	}

	// TODO Pending GPU implementation
	@Override
	public MatrixOperations highPassFilter(double cutoff) throws Exception {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}

		updateMatrix(false);

		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getRowType(),
				this.matrixDescriptor.getColType());
		_newDescriptor.setRowsDesc(this.matrixDescriptor.getRowsDesc());
		_newDescriptor.setColumnsDesc(this.matrixDescriptor.getColumnsDesc());
		MatrixOperationsGPU result = new MatrixOperationsGPU(_newDescriptor);
		double[][] filterMatrix = new double[matrixDescriptor.getNumRows()][matrixDescriptor.getNumCols()];
		for (int i = 0; i < matrixDescriptor.getNumRows(); i++) {
			for (int j = 0; j < matrixDescriptor.getNumCols(); j++) {
				if (matrix.get(i, j) > cutoff) {
					filterMatrix[i][j] = matrix.get(i, j);
				} else {
					filterMatrix[i][j] = 0.00;
				}
			}
		}
		result.updateSparsePointer(new CRSMatrix(filterMatrix));
		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for HPF in ms: " + df.format(d));
		}
		return result;
	}

	// TODO Pending GPU implementation
	@Override
	public MatrixOperations lowPassFilter(double cutoff) throws Exception {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}

		updateMatrix(false);
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getRowType(),
				this.matrixDescriptor.getColType());
		_newDescriptor.setRowsDesc(this.matrixDescriptor.getRowsDesc());
		_newDescriptor.setColumnsDesc(this.matrixDescriptor.getColumnsDesc());
		MatrixOperationsGPU result = new MatrixOperationsGPU(_newDescriptor);
		double[][] filterMatrix = new double[matrixDescriptor.getNumRows()][matrixDescriptor.getNumCols()];
		for (int i = 0; i < matrixDescriptor.getNumRows(); i++) {
			for (int j = 0; j < matrixDescriptor.getNumCols(); j++) {
				if (matrix.get(i, j) < cutoff) {
					filterMatrix[i][j] = matrix.get(i, j);
				} else {
					filterMatrix[i][j] = 0.00;
				}
			}
		}
		result.updateSparsePointer(new CRSMatrix(filterMatrix));
		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for LPF in ms: " + df.format(d));
		}
		return result;
	}

	@Override
	public MatrixOperations filterColumnText(TextFilterData t) throws Exception {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}

		updateMatrix(false);

		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getRowType(),
				this.matrixDescriptor.getColType());
		_newDescriptor.setRowsDesc(this.matrixDescriptor.getRowsDesc());
		_newDescriptor.setColumnsDesc(this.matrixDescriptor.getColumnsDesc());
		MatrixOperationsGPU result = null;
		result = new MatrixOperationsGPU(_newDescriptor);
		double[][] filterMatrix = new double[matrixDescriptor.getNumRows()][matrixDescriptor.getNumCols()];
		for (int i = 0; i < matrixDescriptor.getNumRows(); i++) {
			for (int j = 0; j < matrixDescriptor.getNumCols(); j++) {
				if ((!t.isRegularExpression() && contains(false, j, t)) || (t.isRegularExpression()
						&& this.matrixDescriptor.getColumnAt(j).matches(t.getExpression()))) {
					filterMatrix[i][j] = matrix.get(i, j);
				} else {
					filterMatrix[i][j] = 0.00;
				}
			}
		}
		CRSMatrix mtx = new CRSMatrix(filterMatrix);
		result.updateSparsePointer(mtx);
		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for Word on Column Filter in ms: " + df.format(d));
		}
		return result;
	}

	public void updateMatrix(boolean denseToSparse) {
		CRSMatrix mtx = new CRSMatrix(getMatrixDescriptor().getNumRows(), getMatrixDescriptor().getNumCols());
		// Non-zero cells
		ArrayList<Cell> clls = getData();
		if (denseToSparse) {
			this.rows = new int[getMatrixDescriptor().getNumRows() * getMatrixDescriptor().getNumCols()];
			this.cols = new int[getMatrixDescriptor().getNumRows() * getMatrixDescriptor().getNumCols()];
			this.values = new float[getMatrixDescriptor().getNumRows() * getMatrixDescriptor().getNumCols()];
		}
		// Sets non-zero cells and indexes in the new matrix
		for (Cell c : clls) {
			mtx.set(c.row, c.col, c.value);
			if (denseToSparse) {
				int index = (getMatrixDescriptor().getNumCols()) * c.row + c.col;
				this.rows[index] = c.row;
				this.cols[index] = c.col;
				this.values[index] = c.value;
			}
		}
		// Updates sparse (zero) indexes not setted because only indexes of non-zero
		// cells were updated
		if (denseToSparse) {
			AtomicInteger index1 = new AtomicInteger();
			int[] rowZeroIndexes = Arrays.stream(rows).map(zi -> index1.getAndIncrement()).filter(zi -> rows[zi] == 0)
					.toArray();
			for (int i = 0; i < rowZeroIndexes.length; i++) {
				this.rows[rowZeroIndexes[i]] = rowZeroIndexes[i] / getMatrixDescriptor().getNumCols();
			}

			AtomicInteger index2 = new AtomicInteger();
			int[] colZeroIndexes = Arrays.stream(cols).map(zi -> index2.getAndIncrement()).filter(zi -> cols[zi] == 0)
					.toArray();
			for (int i = 0; i < colZeroIndexes.length; i++) {
				this.cols[colZeroIndexes[i]] = colZeroIndexes[i] % ((getMatrixDescriptor().getNumCols()));
			}

		}
		setMatrix(mtx);
	}

	private void updateSparsePointer(CRSMatrix mtx) {
		/*
		 * long startTime = 0; long endTime = 0; if (Configuration.telemetry) {
		 * startTime = System.nanoTime(); }
		 */
		List<Integer> rowsList = new ArrayList<>();
		List<Integer> colsList = new ArrayList<>();
		List<Float> valuesList = new ArrayList<>();
		mtx.eachNonZero(new MatrixProcedure() {
			@Override
			public void apply(int row, int col, double value) {
				rowsList.add(row);
				colsList.add(col);
				valuesList.add((float) value);
			}
		});

		rows = rowsList.stream().mapToInt(i -> i).toArray();
		cols = colsList.stream().mapToInt(j -> j).toArray();
		values = ArrayUtils.toPrimitive(valuesList.toArray(new Float[valuesList.size()]), 0.0F);

		MatrixProcessor.setSparseData(this.matPointer, rows, cols, values);
		/*
		 * if (Configuration.telemetry) { endTime = System.nanoTime(); long timeElapsed
		 * = endTime - startTime; double d = timeElapsed / 1000000d; DecimalFormat df =
		 * new DecimalFormat("#.##"); df = new DecimalFormat("#.##");
		 * System.out.println("Time elapsed for sparse update (CPU->DEVICE) in ms: " +
		 * df.format(d)); }
		 */
	}

	// TODO Pending GPU implementation
	@Override
	public MatrixOperations filterRowText(TextFilterData t) throws Exception {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}

		updateMatrix(false);

		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getRowType(),
				this.matrixDescriptor.getColType());
		_newDescriptor.setRowsDesc(this.matrixDescriptor.getRowsDesc());
		_newDescriptor.setColumnsDesc(this.matrixDescriptor.getColumnsDesc());
		MatrixOperationsGPU result = new MatrixOperationsGPU(_newDescriptor);
		double[][] filterMatrix = new double[matrixDescriptor.getNumRows()][matrixDescriptor.getNumCols()];
		for (int i = 0; i < matrixDescriptor.getNumRows(); i++) {
			for (int j = 0; j < matrixDescriptor.getNumCols(); j++) {
				if ((!t.isRegularExpression() && contains(true, i, t))
						|| (t.isRegularExpression() && this.matrixDescriptor.getRowAt(i).matches(t.getExpression()))) {
					filterMatrix[i][j] = matrix.get(i, j);
				} else {
					filterMatrix[i][j] = 0.00;
				}
			}
		}
		result.updateSparsePointer(new CRSMatrix(filterMatrix));
		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for Word on Row Filter in ms: " + df.format(d));
		}
		return result;
	}

	// TODO Pending GPU implementation
	@Override
	public MatrixOperations standardScoreSparse() throws Exception {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}

		updateMatrix(false);

		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getRowType(),
				this.matrixDescriptor.getColType());

		_newDescriptor.setRowsDesc(matrixDescriptor.getRowsDesc());
		_newDescriptor.setColumnsDesc(matrixDescriptor.getColumnsDesc());

		MatrixOperationsGPU result = new MatrixOperationsGPU(_newDescriptor);

		float meanCol[] = new float[result.getMatrixDescriptor().getNumCols()];
		float sdCol[] = new float[result.getMatrixDescriptor().getNumCols()];
		float sumCol[] = new float[result.getMatrixDescriptor().getNumCols()];
		int numElements[] = new int[result.getMatrixDescriptor().getNumCols()];

		matrix.eachNonZero(new MatrixProcedure() {

			@Override
			public void apply(int row, int col, double value) {
				numElements[col]++;
				sumCol[col] += value;
			}
		});

		for (int i = 0; i < sumCol.length; i++)
			meanCol[i] = sumCol[i] / (float) numElements[i];

		matrix.eachNonZero(new MatrixProcedure() {

			@Override
			public void apply(int row, int col, double value) {
				sdCol[col] += (value - meanCol[col]) * (value - meanCol[col]);
				sumCol[col] += value;
			}
		});

		for (int i = 0; i < sdCol.length; i++)
			sdCol[i] = (float) Math.sqrt((double) (sdCol[i] * (1.0f / (float) numElements[i])));

		CRSMatrix newMatrix = new CRSMatrix(matrix.rows(), matrix.columns());
		matrix.eachNonZero(new MatrixProcedure() {

			@Override
			public void apply(int row, int col, double value) {
				Cell c = new Cell();
				c.col = col;
				c.row = row;
				c.value = ((float) value - meanCol[col]) / sdCol[col];
				if (c.value == 0) {
					c.value = c.value / 0;
				}
				newMatrix.set(c.row, c.col, c.value);
			}
		});

		result.updateSparsePointer(newMatrix);
		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for standard score sparse in ms: " + df.format(d));
		}
		return result;
	}

	// TODO Pending GPU implementation
	@Override
	public MatrixOperations sortColumnFirst() throws Exception {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}

		updateMatrix(false);

		MatrixOperationsGPU result = null;
		CRSMatrix newMatrix = new CRSMatrix(matrix.rows(), matrix.columns());
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getRowType(),
				this.matrixDescriptor.getColType());
		int[] rowInserted = new int[matrix.rows()];
		int[] colInserted = new int[matrix.columns()];
		for (int i = 0; i < colInserted.length; i++) {
			colInserted[i] = -1;
		}
		for (int i = 0; i < rowInserted.length; i++) {
			rowInserted[i] = -1;
		}
		for (int i = 0; i < matrix.rows(); i++) {
			for (int j = 0; j < matrix.columns(); j++) {
				Double v = matrix.get(i, j);
				if (v != 0) {
					if (colInserted[j] == -1) {
						_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(j));
						colInserted[j] = _newDescriptor.getNumCols() - 1;
					}
					if (rowInserted[i] == -1) {
						_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
						rowInserted[i] = _newDescriptor.getNumRows() - 1;
					}
					newMatrix.set(rowInserted[i], colInserted[j], v.floatValue());
				}
			}
		}
		for (int i = 0; i < rowInserted.length; i++) {
			if (rowInserted[i] == -1) {
				_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
				for (int j = 0; j < _newDescriptor.getNumCols(); j++) {
					newMatrix.set(_newDescriptor.getNumRows() - 1, j, 0);
				}
			}
		}

		for (int i = 0; i < colInserted.length; i++) {
			if (colInserted[i] == -1) {
				_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));
				for (int j = 0; j < _newDescriptor.getNumRows(); j++) {
					newMatrix.set(j, _newDescriptor.getNumCols() - 1, 0);
				}
			}
		}

		result = new MatrixOperationsGPU(_newDescriptor);
		result.updateSparsePointer(newMatrix);
		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for Column First Sorting in ms: " + df.format(d));
		}
		return result;
	}

	// TODO Pending GPU implementation
	@Override
	public MatrixOperations sortRowFirst() throws Exception {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}

		updateMatrix(false);

		MatrixOperationsGPU result = null;
		CRSMatrix newMatrix = new CRSMatrix(matrix.rows(), matrix.columns());
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getRowType(),
				this.matrixDescriptor.getColType());
		int[] rowInserted = new int[matrix.rows()];
		int[] colInserted = new int[matrix.columns()];
		for (int i = 0; i < colInserted.length; i++) {
			colInserted[i] = -1;
		}
		for (int i = 0; i < rowInserted.length; i++) {
			rowInserted[i] = -1;
		}
		for (int j = 0; j < matrix.columns(); j++) {
			for (int i = 0; i < matrix.rows(); i++) {
				Double v = matrix.get(i, j);
				if (v != 0) {
					if (rowInserted[i] == -1) {
						_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
						rowInserted[i] = _newDescriptor.getNumRows() - 1;
					}
					if (colInserted[j] == -1) {
						_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(j));
						colInserted[j] = _newDescriptor.getNumCols() - 1;
					}
					newMatrix.set(rowInserted[i], colInserted[j], v.floatValue());
				}
			}
		}

		for (int i = 0; i < colInserted.length; i++) {
			if (colInserted[i] == -1) {
				_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));
				for (int j = 0; j < _newDescriptor.getNumRows(); j++) {
					newMatrix.set(j, _newDescriptor.getNumCols() - 1, 0);
				}
			}
		}

		for (int i = 0; i < rowInserted.length; i++) {
			if (rowInserted[i] == -1) {
				_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
				for (int j = 0; j < _newDescriptor.getNumCols(); j++) {
					newMatrix.set(_newDescriptor.getNumRows() - 1, j, 0);
				}
			}
		}

		result = new MatrixOperationsGPU(_newDescriptor);
		result.updateSparsePointer(newMatrix);
		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for Row First Sorting in ms: " + df.format(d));
		}
		return result;
	}

	// TODO Pending GPU implementation
	@Override
	public MatrixOperations sortDefaultDimensionValues() throws Exception {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}

		updateMatrix(false);

		MatrixDescriptor _matrixDescriptor = new MatrixDescriptor(matrixDescriptor.getRowType(),
				matrixDescriptor.getColType());
		List<String> columnsDesc = new ArrayList<>(this.matrixDescriptor.getColumnsDesc());
		double[][] m = new double[matrix.rows()][matrix.columns()];
		for (int i = 0; i < matrix.rows(); i++) {
			for (int j = 0; j < matrix.columns(); j++) {
				m[i][j] = matrix.get(i, j);
			}
			Prov2DominoesUtil.sortWithLabels(m[i], columnsDesc);
		}
		_matrixDescriptor.setRowsDesc(this.matrixDescriptor.getRowsDesc());
		_matrixDescriptor.setColumnsDesc(columnsDesc);
		MatrixOperationsGPU result = null;
		result = new MatrixOperationsGPU(_matrixDescriptor);

		result.updateSparsePointer(new CRSMatrix(m));

		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for Default Dimension Sort in ms: " + df.format(d));
		}
		return result;
	}

	// TODO Pending GPU implementation
	@Override
	public MatrixOperations sortByRowGroup() throws Exception {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}

		updateMatrix(false);

		MatrixDescriptor _matrixDescriptor = new MatrixDescriptor(matrixDescriptor.getRowType(),
				matrixDescriptor.getColType());
		_matrixDescriptor.setColumnsDesc(matrixDescriptor.getColumnsDesc());
		List<String> oldRows = new ArrayList<>(matrixDescriptor.getRowsDesc());
		double[] rowsCount = new double[oldRows.size()];

		MatrixOperationsGPU result = null;

		CRSMatrix crsResult = new CRSMatrix(matrix.rows(), matrix.columns());
		for (int i = 0; i < matrix.rows(); i++) {
			for (int j = 0; j < matrix.columns(); j++) {
				if (matrix.get(i, j) > 0) {
					rowsCount[i]++;
				}
			}
		}
		List<String> newRows = new ArrayList<>(oldRows);
		Prov2DominoesUtil.quickSort(rowsCount, 0, rowsCount.length - 1, newRows);
		_matrixDescriptor.setRowsDesc(newRows);
		int index = 0;
		for (String row : newRows) {
			int oldIndex = oldRows.indexOf(row);
			for (int j = 0; j < matrix.columns(); j++) {
				crsResult.set(index, j, matrix.get(oldIndex, j));
			}
			index++;
		}

		result = new MatrixOperationsGPU(_matrixDescriptor);
		result.updateSparsePointer(crsResult);

		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for Sort by Descending Row Group in ms: " + df.format(d));
		}
		return result;
	}

	// TODO Pending GPU implementation
	@Override
	public MatrixOperations sortByColumnGroup() throws Exception {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}

		updateMatrix(false);

		MatrixDescriptor _matrixDescriptor = new MatrixDescriptor(matrixDescriptor.getRowType(),
				matrixDescriptor.getColType());
		_matrixDescriptor.setRowsDesc(new ArrayList<>(matrixDescriptor.getRowsDesc()));
		List<String> oldColumns = new ArrayList<>(matrixDescriptor.getColumnsDesc());
		_matrixDescriptor.setColumnsDesc(oldColumns);
		double[] columnsCount = new double[oldColumns.size()];

		MatrixOperationsGPU result = null;
		result = new MatrixOperationsGPU(_matrixDescriptor);

		CRSMatrix crsResult = new CRSMatrix(matrix.rows(), matrix.columns());
		for (int i = 0; i < matrix.columns(); i++) {
			for (int j = 0; j < matrix.rows(); j++) {
				if (matrix.get(j, i) > 0) {
					columnsCount[i]++;
				}
			}
		}
		List<String> newColumns = new ArrayList<>(oldColumns);
		Prov2DominoesUtil.quickSort(columnsCount, 0, columnsCount.length - 1, newColumns);
		result.getMatrixDescriptor().setColumnsDesc(newColumns);
		int index = 0;
		for (String column : newColumns) {
			int oldIndex = matrixDescriptor.getColumnsDesc().indexOf(column);
			for (int i = 0; i < matrix.rows(); i++) {
				crsResult.set(i, index, matrix.get(i, oldIndex));
			}
			index++;
		}
		result.updateSparsePointer(crsResult);

		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for Sort by Descending Column Group in ms: " + df.format(d));
		}
		return result;
	}

	// TODO Pending GPU implementation
	@Override
	public MatrixOperations aggregateDimension() throws Exception {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}

		updateMatrix(false);

		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getRowType(),
				this.matrixDescriptor.getColType());

		_newDescriptor.AddRowDesc("SUM");

		_newDescriptor.setColumnsDesc(this.matrixDescriptor.getColumnsDesc());


		float[] rowSum = new float[this.matrixDescriptor.getNumCols()];
		matrix.eachNonZero(new MatrixProcedure() {
			@Override
			public void apply(int row, int col, double value) {
				rowSum[col] += value;
			}
		});
		CRSMatrix result = new CRSMatrix(1, rowSum.length);
		for (int i = 0; i < rowSum.length; i++) {
			if (Math.abs(rowSum[i]) > 0) {
				result.set(0, i, rowSum[i]);
			}
		}
		MatrixOperationsGPU reduced = new MatrixOperationsGPU(_newDescriptor);
		reduced.updateSparsePointer(result);
		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for aggregate dimension in ms: " + df.format(d));
		}
		return reduced;
	}

	@Override
	public ArrayList<Cell> getData(boolean isSparse) {
		if (isSparse) {
			return getSparseData();
		}
		long startTime = 0;
		long endTime = 0;
		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		Cell[] nzList = MatrixProcessor.getDenseData(matPointer, rows, cols);
		ArrayList<Cell> cellList = new ArrayList<Cell>();

		for (Cell nz : nzList) {
			cellList.add(new Cell(nz.row, nz.col, nz.value));
		}
		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for mem-transfer (DEVICE->CPU) in ms: " + df.format(d));
		}
		this.setMatrix(Prov2DominoesUtil.cells2Matrix(cellList, rows.length, cols.length));
		return cellList;
	}

	private boolean contains(boolean isRow, int i, TextFilterData t) {
		if (isRow) {
			if (t.isCaseSensitive()) {
				return this.matrixDescriptor.getRowAt(i).contains(t.getExpression());
			} else {
				return StringUtils.containsIgnoreCase(this.matrixDescriptor.getRowAt(i), t.getExpression());
			}
		} else {
			if (t.isCaseSensitive()) {
				return this.matrixDescriptor.getColumnAt(i).contains(t.getExpression());
			} else {
				return StringUtils.containsIgnoreCase(this.matrixDescriptor.getColumnAt(i), t.getExpression());
			}
		}
	}

	@Override
	public void setData(ArrayList<Cell> cells) {
		/*
		 * long startTime = 0; long endTime = 0; if (Configuration.telemetry) {
		 * startTime = System.nanoTime(); }
		 */
		rows = new int[cells.size()];
		cols = new int[cells.size()];
		float[] data = new float[cells.size()];

		for (int i = 0; i < cells.size(); i++) {
			Cell cell = cells.get(i);

			rows[i] = cell.row;
			cols[i] = cell.col;
			data[i] = cell.value;
		}
		MatrixProcessor.setSparseData(matPointer, rows, cols, data);
		this.rows = null;
		this.cols = null;
		/*
		 * if (Configuration.telemetry) { endTime = System.nanoTime(); long timeElapsed
		 * = endTime - startTime; double d = timeElapsed / 1000000d; DecimalFormat df =
		 * new DecimalFormat("#.##"); df = new DecimalFormat("#.##"); System.out.
		 * println("Time elapsed for sparse mem-transfer (CPU->DEVICE) in ms: " +
		 * df.format(d)); }
		 */
	}

	private ArrayList<Cell> getSparseData() {
		/*
		 * long startTime = 0; long endTime = 0; if (Configuration.telemetry) {
		 * startTime = System.nanoTime(); }
		 */
		Cell[] nzList = MatrixProcessor.getSparseData(matPointer);
		ArrayList<Cell> cellList = new ArrayList<Cell>();

		for (Cell nz : nzList) {
			cellList.add(new Cell(nz.row, nz.col, nz.value));
		}
		/*
		 * if (Configuration.telemetry) { endTime = System.nanoTime(); long timeElapsed
		 * = endTime - startTime; double d = timeElapsed / 1000000d; DecimalFormat df =
		 * new DecimalFormat("#.##"); df = new DecimalFormat("#.##"); System.out.
		 * println("Time elapsed for sparse mem-transfer (DEVICE->CPU) in ms: " +
		 * df.format(d)); }
		 */
		return cellList;
	}
	
	public float findMinValue() {
		return MatrixProcessor.getMin(matPointer);
	}

	public float findMaxValue() {
		return MatrixProcessor.getMax(matPointer);
	}

	@Override
	public ArrayList<Cell> getData() {
		return getData(true);
	}

	public int[] getRows() {
		return this.rows;
	}

	public void setRows(int[] rows) {
		this.rows = rows;
	}

	public int[] getCols() {
		return this.cols;
	}

	public void setCols(int[] cols) {
		this.cols = cols;
	}

	public float[] getValues() {
		return values;
	}

	public void setValues(float[] values) {
		this.values = values;
	}

}