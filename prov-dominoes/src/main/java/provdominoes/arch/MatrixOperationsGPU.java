package provdominoes.arch;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
	private boolean isSparse;
	private CRSMatrix matrix;

	private MatrixDescriptor matrixDescriptor;

	public MatrixDescriptor getMatrixDescriptor() {
		return matrixDescriptor;
	}

	public MatrixOperationsGPU(MatrixDescriptor _matrixDescriptor, boolean isSparse) throws Exception {
		this.isSparse = isSparse;
		if (!Session.isSessionStarted())
			throw new Exception("Session is not started");

		matrixDescriptor = _matrixDescriptor;

		matPointer = MatrixProcessor.createMatrixData(matrixDescriptor.getNumRows(), matrixDescriptor.getNumCols(),
				isSparse);

		Session.register2DMatrix(this);
	}

	public MatrixOperationsGPU(MatrixDescriptor _matrixDescriptor, boolean isSparse, int[] rows, int[] cols)
			throws Exception {
		this(_matrixDescriptor, isSparse);
		this.rows = rows;
		this.cols = cols;
	}

	@Override
	public int getMemUsed() {
		return matrixDescriptor.getNumCols() * matrixDescriptor.getNumRows() * (Float.SIZE / 8);
	}

	public void finalize() {
		MatrixProcessor.deleteMatrixData(matPointer);
	}

	@Override
	public MatrixOperations subtract(MatrixOperations other) throws Exception {
		long startTime = 0;
		long endTime = 0;
		MatrixDescriptor otherDescriptor = other.getMatrixDescriptor();

		if (matrixDescriptor.getNumRows() != otherDescriptor.getNumRows()
				&& matrixDescriptor.getNumCols() != otherDescriptor.getNumCols())
			throw new Exception("Matrix cannot be added!");

		MatrixDescriptor resultDesc = new MatrixDescriptor(matrixDescriptor.getRowType(),
				matrixDescriptor.getColType());

		for (int i = 0; i < matrixDescriptor.getNumRows(); i++)
			resultDesc.AddRowDesc(matrixDescriptor.getRowAt(i));

		for (int i = 0; i < otherDescriptor.getNumCols(); i++)
			resultDesc.AddColDesc(matrixDescriptor.getColumnAt(i));

		MatrixOperationsGPU result = new MatrixOperationsGPU(resultDesc, false, rows, cols);

		this.setSparse(false);
		MatrixOperations temp1 = new MatrixOperationsCPU(getMatrixDescriptor());
		temp1.setData(Prov2DominoesUtil.matrix2Cells(matrix));
		if (!Configuration.tuning) {
			temp1 = (MatrixOperations) temp1.sortRows();
			temp1 = (MatrixOperations) temp1.sortColumns();
		}

		MatrixOperations temp2 = new MatrixOperationsCPU(other.getMatrixDescriptor());
		temp2.setData(Prov2DominoesUtil.matrix2Cells(other.getMatrix()));
		if (!Configuration.tuning) {
			temp2 = (MatrixOperations) temp2.sortRows();
			temp2 = (MatrixOperations) temp2.sortColumns();
		}

		CRSMatrix crs1 = Prov2DominoesUtil.cells2Matrix(temp1.getData(), temp1.getMatrixDescriptor().getNumRows(),
				temp1.getMatrixDescriptor().getNumCols());
		MatrixOperations tempg1 = (MatrixOperations) MatrixOperations.configureOperation(crs1,
				temp1.getMatrixDescriptor(), false);

		CRSMatrix crs2 = Prov2DominoesUtil.cells2Matrix(temp2.getData(), temp2.getMatrixDescriptor().getNumRows(),
				temp2.getMatrixDescriptor().getNumCols());
		MatrixOperations tempg2 = (MatrixOperations) MatrixOperations.configureOperation(crs2,
				temp2.getMatrixDescriptor(), false);

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		MatrixProcessor.subtract(((MatrixOperationsGPU) tempg1).matPointer, ((MatrixOperationsGPU) tempg2).matPointer,
				matrixDescriptor.getNumRows() * matrixDescriptor.getNumCols(), result.matPointer);
		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for subtraction in ms: " + df.format(d));
		}
		result.getMatrixDescriptor().setRowsDesc(temp2.getMatrixDescriptor().getRowsDesc());
		result.getMatrixDescriptor().setColumnsDesc(temp2.getMatrixDescriptor().getColumnsDesc());

		return result;
	}

	@Override
	public MatrixOperations sum(MatrixOperations other) throws Exception {
		long startTime = 0;
		long endTime = 0;
		MatrixDescriptor otherDescriptor = other.getMatrixDescriptor();

		if (matrixDescriptor.getNumRows() != otherDescriptor.getNumRows()
				&& matrixDescriptor.getNumCols() != otherDescriptor.getNumCols())
			throw new Exception("Matrix cannot be added!");

		MatrixDescriptor resultDesc = new MatrixDescriptor(matrixDescriptor.getRowType(),
				matrixDescriptor.getColType());

		for (int i = 0; i < matrixDescriptor.getNumRows(); i++)
			resultDesc.AddRowDesc(matrixDescriptor.getRowAt(i));

		for (int i = 0; i < otherDescriptor.getNumCols(); i++)
			resultDesc.AddColDesc(matrixDescriptor.getColumnAt(i));

		MatrixOperationsGPU result = new MatrixOperationsGPU(resultDesc, false, rows, cols);

		this.setSparse(false);
		MatrixOperations temp1 = new MatrixOperationsCPU(getMatrixDescriptor());
		temp1.setData(Prov2DominoesUtil.matrix2Cells(matrix));
		if (!Configuration.tuning) {
			temp1 = (MatrixOperations) temp1.sortRows();
			temp1 = (MatrixOperations) temp1.sortColumns();
		}

		((MatrixOperationsGPU) other).setSparse(false);
		MatrixOperations temp2 = new MatrixOperationsCPU(other.getMatrixDescriptor());
		temp2.setData(Prov2DominoesUtil.matrix2Cells(other.getMatrix()));
		if (!Configuration.tuning) {
			temp2 = (MatrixOperations) temp2.sortRows();
			temp2 = (MatrixOperations) temp2.sortColumns();
		}

		CRSMatrix crs1 = Prov2DominoesUtil.cells2Matrix(temp1.getData(), temp1.getMatrixDescriptor().getNumRows(),
				temp1.getMatrixDescriptor().getNumCols());
		MatrixOperations tempg1 = (MatrixOperations) MatrixOperations.configureOperation(crs1,
				temp1.getMatrixDescriptor(), false);

		CRSMatrix crs2 = Prov2DominoesUtil.cells2Matrix(temp2.getData(), temp2.getMatrixDescriptor().getNumRows(),
				temp2.getMatrixDescriptor().getNumCols());
		MatrixOperations tempg2 = (MatrixOperations) MatrixOperations.configureOperation(crs2,
				temp2.getMatrixDescriptor(), false);

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		MatrixProcessor.sum(((MatrixOperationsGPU) tempg1).matPointer, ((MatrixOperationsGPU) tempg2).matPointer,
				matrixDescriptor.getNumRows() * matrixDescriptor.getNumCols(), result.matPointer);
		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for sum in ms: " + df.format(d));
		}
		result.getMatrixDescriptor().setRowsDesc(temp2.getMatrixDescriptor().getRowsDesc());
		result.getMatrixDescriptor().setColumnsDesc(temp2.getMatrixDescriptor().getColumnsDesc());

		return result;
	}

	public void setSparse(boolean b) {
		this.isSparse = b;
	}

	public MatrixOperations multiply(MatrixOperations other, boolean useGPU) throws Exception {
		long startTime = 0;
		long endTime = 0;

		MatrixDescriptor otherDescriptor = other.getMatrixDescriptor();
		if (matrixDescriptor.getNumCols() != otherDescriptor.getNumRows())
			throw new Exception("Matrix cannot be multiplied!");

		MatrixDescriptor resultDesc = new MatrixDescriptor(matrixDescriptor.getRowType(), otherDescriptor.getColType());

		for (int i = 0; i < matrixDescriptor.getNumRows(); i++)
			resultDesc.AddRowDesc(matrixDescriptor.getRowAt(i));

		for (int i = 0; i < otherDescriptor.getNumCols(); i++)
			resultDesc.AddColDesc(otherDescriptor.getColumnAt(i));

		MatrixOperationsGPU result = new MatrixOperationsGPU(resultDesc, useGPU);

		if (Configuration.tuning) {
			ArrayList<Cell> cells = new ArrayList<Cell>();
			this.matrix.eachNonZero(new MatrixProcedure() {
				@Override
				public void apply(int row, int col, double value) {
					Cell cell = new Cell();
					cell.row = row;
					cell.col = col;
					cell.value = (float) value;
					cells.add(cell);
				}
			});
			ArrayList<Cell> otherCells = new ArrayList<Cell>();
			other.getMatrix().eachNonZero(new MatrixProcedure() {
				@Override
				public void apply(int row, int col, double value) {
					Cell cell = new Cell();
					cell.row = row;
					cell.col = col;
					cell.value = (float) value;
					otherCells.add(cell);
				}
			});
			setData(cells);
			other.setData(otherCells);
		} else {
			setData(this.sortColumns().getData());
			other.setData(other.sortRows().getData());
		}

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		MatrixProcessor.multiply(matPointer, ((MatrixOperationsGPU) other).matPointer, result.matPointer, useGPU);
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

	public MatrixOperations transpose() {
		long startTime = 0;
		long endTime = 0;
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getColType(),
				this.matrixDescriptor.getRowType());

		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getColumnAt(i));

		for (int i = 0; i < this.matrixDescriptor.getNumRows(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getRowAt(i));

		MatrixOperationsGPU transpose = null;

		try {
			transpose = new MatrixOperationsGPU(_newDescriptor, true);
			if (Configuration.telemetry) {
				startTime = System.nanoTime();
			}
			MatrixProcessor.transpose(matPointer, transpose.matPointer);
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for transposition in ms: " + df.format(d));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return transpose;
	}

	public float findMinValue() {
		return MatrixProcessor.getMin(matPointer);
	}

	public float findMaxValue() {
		return MatrixProcessor.getMax(matPointer);
	}

	@Override
	public void setData(ArrayList<Cell> cells) {
		long startTime = 0;
		long endTime = 0;
		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		rows = new int[cells.size()];
		cols = new int[cells.size()];
		float[] data = new float[cells.size()];

		for (int i = 0; i < cells.size(); i++) {
			Cell cell = cells.get(i);

			rows[i] = cell.row;
			cols[i] = cell.col;
			data[i] = cell.value;
		}
		if (isSparse) {
			MatrixProcessor.setData(matPointer, rows, cols, data);
			this.rows = null;
			this.cols = null;
		} else {
			MatrixProcessor.setData(matPointer, data);
		}
		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for" + (isSparse ? " sparse" : "") + " mem-transfer (CPU->DEVICE) in ms: "
					+ df.format(d));
		}
	}

	private ArrayList<Cell> getSparseData() {
		long startTime = 0;
		long endTime = 0;
		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		Cell[] nzList = MatrixProcessor.getSparseData(matPointer);
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
			System.out.println("Time elapsed for" + (isSparse ? " sparse" : "") + " mem-transfer (DEVICE->CPU) in ms: "
					+ df.format(d));
		}
		return cellList;
	}

	@Override
	public MatrixOperations aggregateDimension(boolean useGPU) {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getRowType(),
				this.matrixDescriptor.getColType());

		_newDescriptor.AddRowDesc("SUM");

		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));

		MatrixOperationsCPU reduced = new MatrixOperationsCPU(_newDescriptor);

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
		reduced.setData(result);
		reduced.setUnderlyingElements(null);
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
	public MatrixOperations confidence(boolean useGPU) {
		long startTime = 0;
		long endTime = 0;
		MatrixDescriptor _newDescriptor = this.matrixDescriptor;

		MatrixOperationsGPU confidence = null;

		try {
			confidence = new MatrixOperationsGPU(_newDescriptor, true);
			if (Configuration.telemetry) {
				startTime = System.nanoTime();
			}
			MatrixProcessor.confidence(matPointer, confidence.matPointer, useGPU);
			if (Configuration.telemetry) {
				endTime = System.nanoTime();
				long timeElapsed = endTime - startTime;
				double d = timeElapsed / 1000000d;
				DecimalFormat df = new DecimalFormat("#.##");
				df = new DecimalFormat("#.##");
				System.out.println("Time elapsed for confidence in ms: " + df.format(d));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return confidence;
	}

	@Override
	public MatrixOperations transitiveClosure() {
		MatrixDescriptor _newDescriptor = this.matrixDescriptor;
		MatrixOperationsGPU transitiveClosure = null;

		try {
			transitiveClosure = new MatrixOperationsGPU(_newDescriptor, false, rows, cols);
			long startTime = 0;
			long endTime = 0;
			if (Configuration.telemetry) {
				startTime = System.nanoTime();
			}
			MatrixProcessor.transitiveClosure(matrixDescriptor.getNumRows(), matPointer, transitiveClosure.matPointer);
			if (Configuration.telemetry) {
				endTime = System.nanoTime();
				long timeElapsed = endTime - startTime;
				double d = timeElapsed / 1000000d;
				DecimalFormat df = new DecimalFormat("#.##");
				System.out.println("Time elapsed for transitive closure in ms: " + df.format(d));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return transitiveClosure;
	}

	@Override
	public boolean isEmpty() {
		return matPointer == 0;
	}

	@Override
	public MatrixOperations binarize() {
		long startTime = 0;
		long endTime = 0;
		MatrixDescriptor _newDescriptor = this.matrixDescriptor;
		MatrixOperationsGPU binarize = null;

		try {
			if (Configuration.telemetry) {
				startTime = System.nanoTime();
			}
			binarize = new MatrixOperationsGPU(_newDescriptor, true);
			MatrixProcessor.binarize(matPointer, binarize.matPointer);
			if (Configuration.telemetry) {
				endTime = System.nanoTime();
				long timeElapsed = endTime - startTime;
				double d = timeElapsed / 1000000d;
				DecimalFormat df = new DecimalFormat("#.##");
				df = new DecimalFormat("#.##");
				System.out.println("Time elapsed for binarization in ms: " + df.format(d));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return binarize;
	}

	@Override
	public MatrixOperations invert() {
		long startTime = 0;
		long endTime = 0;
		MatrixDescriptor _newDescriptor = this.matrixDescriptor;
		MatrixOperationsGPU invert = null;
		try {
			this.isSparse = false;
			setData(Prov2DominoesUtil.matrix2Cells(matrix));
			invert = new MatrixOperationsGPU(_newDescriptor, false, rows, cols);
			if (Configuration.telemetry) {
				startTime = System.nanoTime();
			}
			MatrixProcessor.invert(_newDescriptor.getNumRows() * _newDescriptor.getNumCols(), matPointer,
					invert.matPointer);
			if (Configuration.telemetry) {
				endTime = System.nanoTime();
				long timeElapsed = endTime - startTime;
				double d = timeElapsed / 1000000d;
				DecimalFormat df = new DecimalFormat("#.##");
				df = new DecimalFormat("#.##");
				System.out.println("Time elapsed for invertion in ms: " + df.format(d));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return invert;
	}

	@Override
	public MatrixOperations lowerDiagonal() {
		long startTime = 0;
		long endTime = 0;
		MatrixDescriptor _newDescriptor = this.matrixDescriptor;
		MatrixOperationsGPU lowerDiagonal = null;
		try {
			this.isSparse = false;
			setData(Prov2DominoesUtil.matrix2Cells(matrix));
			lowerDiagonal = new MatrixOperationsGPU(_newDescriptor, false, rows, cols);
			if (Configuration.telemetry) {
				startTime = System.nanoTime();
			}
			MatrixProcessor.lowerDiagonal(this.matrixDescriptor.getNumRows(), matPointer, lowerDiagonal.matPointer);
			if (Configuration.telemetry) {
				endTime = System.nanoTime();
				long timeElapsed = endTime - startTime;
				double d = timeElapsed / 1000000d;
				DecimalFormat df = new DecimalFormat("#.##");
				df = new DecimalFormat("#.##");
				System.out.println("Time elapsed for build lower triangular matrix in ms: " + df.format(d));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return lowerDiagonal;
	}

	@Override
	public MatrixOperations upperDiagonal() {
		long startTime = 0;
		long endTime = 0;
		MatrixDescriptor _newDescriptor = this.matrixDescriptor;
		MatrixOperationsGPU upperDiagonal = null;
		try {
			this.isSparse = false;
			setData(Prov2DominoesUtil.matrix2Cells(matrix));
			upperDiagonal = new MatrixOperationsGPU(_newDescriptor, false, rows, cols);
			if (Configuration.telemetry) {
				startTime = System.nanoTime();
			}
			MatrixProcessor.upperDiagonal(this.matrixDescriptor.getNumRows(), matPointer, upperDiagonal.matPointer);
			if (Configuration.telemetry) {
				endTime = System.nanoTime();
				long timeElapsed = endTime - startTime;
				double d = timeElapsed / 1000000d;
				DecimalFormat df = new DecimalFormat("#.##");
				df = new DecimalFormat("#.##");
				System.out.println("Time elapsed for build upper triangular matrix in ms: " + df.format(d));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return upperDiagonal;
	}

	@Override
	public MatrixOperations diagonalize() {
		long startTime = 0;
		long endTime = 0;
		MatrixDescriptor _newDescriptor = this.matrixDescriptor;
		MatrixOperationsGPU diagonalize = null;
		try {
			this.isSparse = false;
			setData(Prov2DominoesUtil.matrix2Cells(matrix));
			diagonalize = new MatrixOperationsGPU(_newDescriptor, false, rows, cols);
			if (Configuration.telemetry) {
				startTime = System.nanoTime();
			}
			MatrixProcessor.diagonalize(this.matrixDescriptor.getNumRows(), matPointer, diagonalize.matPointer);
			if (Configuration.telemetry) {
				endTime = System.nanoTime();
				long timeElapsed = endTime - startTime;
				double d = timeElapsed / 1000000d;
				DecimalFormat df = new DecimalFormat("#.##");
				df = new DecimalFormat("#.##");
				System.out.println("Time elapsed for Diagonalize Filter in ms: " + df.format(d));
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return diagonalize;
	}

	@Override
	public ArrayList<Cell> getData() {
		if (isSparse) {
			return getSparseData();
		}
		long startTime = 0;
		long endTime = 0;
		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		Cell[] nzList = MatrixProcessor.getData(matPointer, rows, cols);
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
		return cellList;
	}

	public int[] getRows() {
		return rows;
	}

	public void setRows(int[] rows) {
		this.rows = rows;
	}

	public int[] getCols() {
		return cols;
	}

	public void setCols(int[] cols) {
		this.cols = cols;
	}

	@Override
	public MatrixOperations sortRows() {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
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

		MatrixOperationsCPU result = new MatrixOperationsCPU(_matrixDescriptor);

		double[][] matrix = new double[matrixDescriptor.getNumRows()][matrixDescriptor.getNumCols()];
		CRSMatrix oldMatrix = Prov2DominoesUtil.cells2Matrix(getData(), matrixDescriptor.getNumRows(),
				matrixDescriptor.getNumCols());
		for (int i = 0; i < matrixDescriptor.getNumRows(); i++) {
			for (int j = 0; j < matrixDescriptor.getNumCols(); j++) {
				matrix[i][j] = oldMatrix.get(rowsIndexes.get(rowsDesc.get(i)), j);
			}
		}

		result.setData(new CRSMatrix(matrix));
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
	public MatrixOperations sortColumns() {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
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

		MatrixOperationsCPU result = new MatrixOperationsCPU(_matrixDescriptor);

		double[][] matrix = new double[matrixDescriptor.getNumRows()][matrixDescriptor.getNumCols()];
		CRSMatrix oldMatrix = Prov2DominoesUtil.cells2Matrix(getData(), matrixDescriptor.getNumRows(),
				matrixDescriptor.getNumCols());
		for (int i = 0; i < matrixDescriptor.getNumRows(); i++) {
			for (int j = 0; j < matrixDescriptor.getNumCols(); j++) {
				matrix[i][j] = oldMatrix.get(i, colsIndexes.get(colsDesc.get(j)));
			}
		}

		result.setData(new CRSMatrix(matrix));
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
	public MatrixOperations sortEqualTo(MatrixDescriptor matrixDescriptor) {
		MatrixOperationsCPU result = new MatrixOperationsCPU(matrixDescriptor);
		CRSMatrix crsResult = new CRSMatrix(matrixDescriptor.getNumRows(), matrixDescriptor.getNumCols());
		CRSMatrix oldCRS = Prov2DominoesUtil.cells2Matrix(this.getData(), this.getMatrixDescriptor().getNumRows(),
				this.getMatrixDescriptor().getNumCols());
		for (int i = 0; i < crsResult.rows(); i++) {
			for (int j = 0; j < crsResult.columns(); j++) {
				crsResult.set(i, j,
						oldCRS.get(this.getMatrixDescriptor().getRowElementIndex(matrixDescriptor.getRowAt(i)),
								this.getMatrixDescriptor().getColElementIndex(matrixDescriptor.getColumnAt(j))));
			}
		}
		result.setData(crsResult);
		return result;
	}

	@Override
	public ArrayList<Cell> getAllData() {
		return getData();
	}

	@Override
	public CRSMatrix getMatrix() {
		return matrix;
	}

	@Override
	public void setMatrix(CRSMatrix matrix) {
		this.matrix = matrix;
	}

	// TODO Pending GPU implementation
	@Override
	public MatrixOperations standardScoreDense() {
		long startTime = 0;
		long endTime = 0;
		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getRowType(),
				this.matrixDescriptor.getColType());

		for (int i = 0; i < this.matrixDescriptor.getNumRows(); i++)
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));

		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));

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

		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));

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
	public MatrixOperations trim() {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		MatrixOperationsCPU result = null;
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

		result = new MatrixOperationsCPU(_newDescriptor);
		result.setData(
				Prov2DominoesUtil.cells2Matrix(newMatrix, _newDescriptor.getNumRows(), _newDescriptor.getNumCols()));
		Long cells = new Long(matrixDescriptor.getNumRows() * matrixDescriptor.getNumCols()
				- _newDescriptor.getNumRows() * _newDescriptor.getNumCols());
		Double p = new Double(cells.doubleValue() / (matrixDescriptor.getNumRows() * matrixDescriptor.getNumCols()));
		System.out
				.println("# cells removed: " + cells + ". % cells removed: " + (new DecimalFormat("##.##%").format(p)));
		result.setUnderlyingElements(null);
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
	public MatrixOperations highPassFilter(double cutoff) {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getRowType(),
				this.matrixDescriptor.getColType());
		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));
		for (int i = 0; i < this.matrixDescriptor.getNumRows(); i++)
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
		MatrixOperationsCPU result = new MatrixOperationsCPU(_newDescriptor);
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
		result.setData(new CRSMatrix(filterMatrix));
		result.setUnderlyingElements(null);
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
	public MatrixOperations lowPassFilter(double cutoff) {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getRowType(),
				this.matrixDescriptor.getColType());
		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));
		for (int i = 0; i < this.matrixDescriptor.getNumRows(); i++)
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
		MatrixOperationsCPU result = new MatrixOperationsCPU(_newDescriptor);
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
		result.setData(new CRSMatrix(filterMatrix));
		result.setUnderlyingElements(null);
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

	// TODO Pending GPU implementation
	@Override
	public MatrixOperations filterColumnText(TextFilterData t) {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getRowType(),
				this.matrixDescriptor.getColType());
		for (int i = 0; i < this.matrixDescriptor.getNumRows(); i++)
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));
		MatrixOperationsCPU result = new MatrixOperationsCPU(_newDescriptor);
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
		result.setData(new CRSMatrix(filterMatrix));
		result.setUnderlyingElements(null);
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

	// TODO Pending GPU implementation
	@Override
	public MatrixOperations filterRowText(TextFilterData t) {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getRowType(),
				this.matrixDescriptor.getColType());
		for (int i = 0; i < this.matrixDescriptor.getNumRows(); i++)
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));
		MatrixOperationsCPU result = new MatrixOperationsCPU(_newDescriptor);
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
		result.setData(new CRSMatrix(filterMatrix));
		result.setUnderlyingElements(null);
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
	public MatrixOperations standardScoreSparse() {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getRowType(),
				this.matrixDescriptor.getColType());

		for (int i = 0; i < this.matrixDescriptor.getNumRows(); i++)
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));

		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));

		MatrixOperationsCPU result = new MatrixOperationsCPU(_newDescriptor);

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

		ArrayList<Cell> _data = new ArrayList<>();
		matrix.eachNonZero(new MatrixProcedure() {

			@Override
			public void apply(int row, int col, double value) {
				Cell _cell = new Cell();
				_cell.col = col;
				_cell.row = row;
				_cell.value = ((float) value - meanCol[col]) / sdCol[col];
				if (_cell.value == 0) {
					_cell.value = _cell.value / 0;
				}
				_data.add(_cell);
			}
		});

		result.setData(_data);
		result.setUnderlyingElements(null);
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
	public MatrixOperations sortColumnFirst() {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		MatrixOperationsCPU result = null;
		ArrayList<Cell> newMatrix = new ArrayList<>();
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
					newMatrix.add(new Cell(rowInserted[i], colInserted[j], v.floatValue()));
				}
			}
		}
		for (int i = 0; i < rowInserted.length; i++) {
			if (rowInserted[i] == -1) {
				_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
				for (int j = 0; j < _newDescriptor.getNumCols(); j++) {
					newMatrix.add(new Cell(_newDescriptor.getNumRows() - 1, j, 0));
				}
			}
		}

		for (int i = 0; i < colInserted.length; i++) {
			if (colInserted[i] == -1) {
				_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));
				for (int j = 0; j < _newDescriptor.getNumRows(); j++) {
					newMatrix.add(new Cell(j, _newDescriptor.getNumCols() - 1, 0));
				}
			}
		}

		result = new MatrixOperationsCPU(_newDescriptor);
		result.setData(
				Prov2DominoesUtil.cells2Matrix(newMatrix, _newDescriptor.getNumRows(), _newDescriptor.getNumCols()));
		result.setUnderlyingElements(null);
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
	public MatrixOperations sortRowFirst() {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		MatrixOperationsCPU result = null;
		ArrayList<Cell> newMatrix = new ArrayList<>();
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
					newMatrix.add(new Cell(rowInserted[i], colInserted[j], v.floatValue()));
				}
			}
		}

		for (int i = 0; i < colInserted.length; i++) {
			if (colInserted[i] == -1) {
				_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));
				for (int j = 0; j < _newDescriptor.getNumRows(); j++) {
					newMatrix.add(new Cell(j, _newDescriptor.getNumCols() - 1, 0));
				}
			}
		}

		for (int i = 0; i < rowInserted.length; i++) {
			if (rowInserted[i] == -1) {
				_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
				for (int j = 0; j < _newDescriptor.getNumCols(); j++) {
					newMatrix.add(new Cell(_newDescriptor.getNumRows() - 1, j, 0));
				}
			}
		}

		result = new MatrixOperationsCPU(_newDescriptor);
		result.setData(
				Prov2DominoesUtil.cells2Matrix(newMatrix, _newDescriptor.getNumRows(), _newDescriptor.getNumCols()));
		result.setUnderlyingElements(null);
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
	public MatrixOperations sortDefaultDimensionValues() {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
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
		MatrixOperationsCPU result = new MatrixOperationsCPU(_matrixDescriptor);
		result.setData(new CRSMatrix(m));
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
	public MatrixOperations sortByRowGroup() {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		MatrixDescriptor _matrixDescriptor = new MatrixDescriptor(matrixDescriptor.getRowType(),
				matrixDescriptor.getColType());
		List<String> rowsDesc = new ArrayList<>(matrixDescriptor.getRowsDesc());
		double[] rowsCount = new double[rowsDesc.size()];

		MatrixOperationsCPU result = new MatrixOperationsCPU(_matrixDescriptor);
		result.getMatrixDescriptor().setColumnsDesc(matrixDescriptor.getColumnsDesc());

		CRSMatrix crsResult = new CRSMatrix(matrix.rows(), matrix.columns());
		for (int i = 0; i < matrix.rows(); i++) {
			for (int j = 0; j < matrix.columns(); j++) {
				if (matrix.get(i, j) > 0) {
					rowsCount[i]++;
				}
			}
		}
		List<String> newRows = new ArrayList<>(rowsDesc);
		Prov2DominoesUtil.quickSort(rowsCount, 0, rowsCount.length - 1, newRows);
		result.getMatrixDescriptor().setRowsDesc(newRows);
		int index = 0;
		for (String row : newRows) {
			int oldIndex = rowsDesc.indexOf(row);
			for (int j = 0; j < matrix.columns(); j++) {
				crsResult.set(index, j, matrix.get(oldIndex, j));
			}
			index++;
		}
		result.setData(crsResult);
		result.setUnderlyingElements(null);
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
	public MatrixOperations sortByColumnGroup() {
		long startTime = 0;
		long endTime = 0;

		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		MatrixDescriptor _matrixDescriptor = new MatrixDescriptor(matrixDescriptor.getRowType(),
				matrixDescriptor.getColType());
		List<String> columnsDesc = new ArrayList<>(matrixDescriptor.getColumnsDesc());
		double[] columnsCount = new double[columnsDesc.size()];

		MatrixOperationsCPU result = new MatrixOperationsCPU(_matrixDescriptor);
		result.getMatrixDescriptor().setRowsDesc(matrixDescriptor.getRowsDesc());

		CRSMatrix crsResult = new CRSMatrix(matrix.rows(), matrix.columns());
		for (int i = 0; i < matrix.columns(); i++) {
			for (int j = 0; j < matrix.rows(); j++) {
				if (matrix.get(j, i) > 0) {
					columnsCount[i]++;
				}
			}
		}
		List<String> newColumns = new ArrayList<>(columnsDesc);
		Prov2DominoesUtil.quickSort(columnsCount, 0, columnsCount.length - 1, newColumns);
		result.getMatrixDescriptor().setColumnsDesc(newColumns);
		int index = 0;
		for (String column : newColumns) {
			int oldIndex = columnsDesc.indexOf(column);
			for (int i = 0; i < matrix.rows(); i++) {
				crsResult.set(i, index, matrix.get(i, oldIndex));
			}
			index++;
		}
		result.setData(crsResult);
		result.setUnderlyingElements(null);
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
}