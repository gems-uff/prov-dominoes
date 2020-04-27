package provdominoes.arch;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.la4j.matrix.sparse.CRSMatrix;

import processor.Cell;
import processor.MatrixProcessor;
import provdominoes.command.TextFilterData;
import provdominoes.util.Prov2DominoesUtil;
import provdominoes.util.SortIgnoreCase;

public class MatrixOperationsGPU implements MatrixOperations {

	private long matPointer = 0;
	private int[] rows;
	private int[] cols;
	private boolean isSparse;

	private MatrixDescriptor matrixDescriptor;

	public MatrixDescriptor getMatrixDescriptor() {
		return matrixDescriptor;
	}

	public MatrixOperationsGPU(MatrixDescriptor _matrixDescriptor, boolean isSparse) throws Exception {
		this.isSparse = isSparse;
		if (!Session.isSessionStarted())
			throw new Exception("Session is not started");

		System.out.println("Creating matrix. Rows: " + _matrixDescriptor.getNumRows() + " Cols: "
				+ _matrixDescriptor.getNumCols());

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
		MatrixDescriptor otherDescriptor = other.getMatrixDescriptor();

		if (matrixDescriptor.getNumRows() != otherDescriptor.getNumRows()
				&& matrixDescriptor.getNumCols() != otherDescriptor.getNumCols())
			throw new Exception("Matrix cannot be added!");

		MatrixDescriptor resultDesc = new MatrixDescriptor(matrixDescriptor.getRowType(), matrixDescriptor.getColType());

		for (int i = 0; i < matrixDescriptor.getNumRows(); i++)
			resultDesc.AddRowDesc(matrixDescriptor.getRowAt(i));

		for (int i = 0; i < otherDescriptor.getNumCols(); i++)
			resultDesc.AddColDesc(matrixDescriptor.getColumnAt(i));

		System.out.println("Matrix 1: Rows: " + matrixDescriptor.getNumRows() + " Cols: "
				+ matrixDescriptor.getNumCols() + " Size: " + getMemUsed());
		System.out.println("Matrix 2: Rows: " + otherDescriptor.getNumRows() + " Cols: " + otherDescriptor.getNumCols()
				+ " Size: " + other.getMemUsed());

		System.out.println(
				"1) Operation: Sum - Memory use before this operation: " + getMemUsed() + other.getMemUsed() + " KB of GPU Memory.");

		MatrixOperationsGPU result = new MatrixOperationsGPU(resultDesc, false, rows, cols);

		System.out.println(
				"2) Operation: Sum - Memory use before this operation: " + getMemUsed() + other.getMemUsed() + " KB of GPU Memory.");
		
		
		this.setSparse(false);
		MatrixOperationsCPU temp1 = new MatrixOperationsCPU(getMatrixDescriptor());
		temp1.setData(this.getData());
		temp1 = (MatrixOperationsCPU) temp1.sortRows();
		temp1 = (MatrixOperationsCPU) temp1.sortColumns();
		
		other.setSparse(false);
		MatrixOperationsCPU temp2 = new MatrixOperationsCPU(other.getMatrixDescriptor());
		temp2.setData(other.getData());
		temp2 = (MatrixOperationsCPU) temp2.sortRows();
		temp2 = (MatrixOperationsCPU)temp2.sortColumns();

		CRSMatrix crs1 = Prov2DominoesUtil.cells2Matrix(temp1.getData(), temp1.getMatrixDescriptor().getNumRows(), temp1.getMatrixDescriptor().getNumCols());
		MatrixOperationsGPU tempg1 = (MatrixOperationsGPU) MatrixOperations.configureOperation(crs1, temp1.getMatrixDescriptor(), false);
		
		CRSMatrix crs2 = Prov2DominoesUtil.cells2Matrix(temp2.getData(), temp2.getMatrixDescriptor().getNumRows(), temp2.getMatrixDescriptor().getNumCols());
		MatrixOperationsGPU tempg2 = (MatrixOperationsGPU) MatrixOperations.configureOperation(crs2, temp2.getMatrixDescriptor(), false);
		
		MatrixProcessor.subtract(((MatrixOperationsGPU) tempg1).matPointer, ((MatrixOperationsGPU) tempg2).matPointer, matrixDescriptor.getNumRows()*matrixDescriptor.getNumCols(), result.matPointer);
		result.getMatrixDescriptor().setRowsDesc(temp2.getMatrixDescriptor().getRowsDesc());
		result.getMatrixDescriptor().setColumnsDesc(temp2.getMatrixDescriptor().getColumnsDesc());
		
		System.out.println("Updated memory use after operation: " + getMemUsed() + other.getMemUsed() + " KB of GPU Memory.");

		return result;
	}

	@Override
	public MatrixOperations sum(MatrixOperations other) throws Exception {
		MatrixDescriptor otherDescriptor = other.getMatrixDescriptor();

		if (matrixDescriptor.getNumRows() != otherDescriptor.getNumRows()
				&& matrixDescriptor.getNumCols() != otherDescriptor.getNumCols())
			throw new Exception("Matrix cannot be added!");

		MatrixDescriptor resultDesc = new MatrixDescriptor(matrixDescriptor.getRowType(), matrixDescriptor.getColType());

		for (int i = 0; i < matrixDescriptor.getNumRows(); i++)
			resultDesc.AddRowDesc(matrixDescriptor.getRowAt(i));

		for (int i = 0; i < otherDescriptor.getNumCols(); i++)
			resultDesc.AddColDesc(matrixDescriptor.getColumnAt(i));

		System.out.println("Matrix 1: Rows: " + matrixDescriptor.getNumRows() + " Cols: "
				+ matrixDescriptor.getNumCols() + " Size: " + getMemUsed());
		System.out.println("Matrix 2: Rows: " + otherDescriptor.getNumRows() + " Cols: " + otherDescriptor.getNumCols()
				+ " Size: " + other.getMemUsed());

		System.out.println(
				"1) Operation: Sum - Memory use before this operation: " + getMemUsed() + other.getMemUsed() + " KB of GPU Memory.");

		MatrixOperationsGPU result = new MatrixOperationsGPU(resultDesc, false, rows, cols);

		System.out.println(
				"2) Operation: Sum - Memory use before this operation: " + getMemUsed() + other.getMemUsed() + " KB of GPU Memory.");
		
		
		this.setSparse(false);
		MatrixOperationsCPU temp1 = new MatrixOperationsCPU(getMatrixDescriptor());
		temp1.setData(this.getData());
		temp1 = (MatrixOperationsCPU) temp1.sortRows();
		temp1 = (MatrixOperationsCPU) temp1.sortColumns();
		
		other.setSparse(false);
		MatrixOperationsCPU temp2 = new MatrixOperationsCPU(other.getMatrixDescriptor());
		temp2.setData(other.getData());
		temp2 = (MatrixOperationsCPU) temp2.sortRows();
		temp2 = (MatrixOperationsCPU)temp2.sortColumns();

		CRSMatrix crs1 = Prov2DominoesUtil.cells2Matrix(temp1.getData(), temp1.getMatrixDescriptor().getNumRows(), temp1.getMatrixDescriptor().getNumCols());
		MatrixOperationsGPU tempg1 = (MatrixOperationsGPU) MatrixOperations.configureOperation(crs1, temp1.getMatrixDescriptor(), false);
		
		CRSMatrix crs2 = Prov2DominoesUtil.cells2Matrix(temp2.getData(), temp2.getMatrixDescriptor().getNumRows(), temp2.getMatrixDescriptor().getNumCols());
		MatrixOperationsGPU tempg2 = (MatrixOperationsGPU) MatrixOperations.configureOperation(crs2, temp2.getMatrixDescriptor(), false);
		
		MatrixProcessor.sum(((MatrixOperationsGPU) tempg1).matPointer, ((MatrixOperationsGPU) tempg2).matPointer, matrixDescriptor.getNumRows()*matrixDescriptor.getNumCols(), result.matPointer);
		result.getMatrixDescriptor().setRowsDesc(temp2.getMatrixDescriptor().getRowsDesc());
		result.getMatrixDescriptor().setColumnsDesc(temp2.getMatrixDescriptor().getColumnsDesc());
		
		System.out.println("Updated memory use after operation: " + getMemUsed() + other.getMemUsed() + " KB of GPU Memory.");

		return result;
	}
	
	public void setSparse(boolean b) {
		this.isSparse = b;
	}

	public MatrixOperations multiply(MatrixOperations other, boolean useGPU) throws Exception {
		MatrixDescriptor otherDescriptor = other.getMatrixDescriptor();

		if (matrixDescriptor.getNumCols() != otherDescriptor.getNumRows())
			throw new Exception("Matrix cannot be multiplied!");

		MatrixDescriptor resultDesc = new MatrixDescriptor(matrixDescriptor.getRowType(), otherDescriptor.getColType());

		for (int i = 0; i < matrixDescriptor.getNumRows(); i++)
			resultDesc.AddRowDesc(matrixDescriptor.getRowAt(i));

		for (int i = 0; i < otherDescriptor.getNumCols(); i++)
			resultDesc.AddColDesc(otherDescriptor.getColumnAt(i));

		System.out.println("Matrix 1: Rows: " + matrixDescriptor.getNumRows() + " Cols: "
				+ matrixDescriptor.getNumCols() + " Size: " + getMemUsed());
		System.out.println("Matrix 2: Rows: " + otherDescriptor.getNumRows() + " Cols: " + otherDescriptor.getNumCols()
				+ " Size: " + other.getMemUsed());

		System.out.println(
				"1) Operation: Multiplication - Memory use before this operation: " + getMemUsed() + other.getMemUsed() + " KB of GPU Memory.");

		MatrixOperationsGPU result = new MatrixOperationsGPU(resultDesc, useGPU);

		System.out.println(
				"2) Operation: Multiplication - Memory use before this operation: " + getMemUsed() + other.getMemUsed() + " KB of GPU Memory.");
		setData(this.sortColumns().getData());
		other.setData(other.sortRows().getData());

		MatrixProcessor.multiply(matPointer, ((MatrixOperationsGPU) other).matPointer, result.matPointer, useGPU);
		System.out.println("Updated memory use after operation: " + getMemUsed() + other.getMemUsed() + " KB of GPU Memory.");

		return result;
	}

	public MatrixOperationsGPU transpose() {
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getColType(),
				this.matrixDescriptor.getRowType());

		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getColumnAt(i));

		for (int i = 0; i < this.matrixDescriptor.getNumRows(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getRowAt(i));

		MatrixOperationsGPU transpose = null;

		try {
			transpose = new MatrixOperationsGPU(_newDescriptor, true);
			System.out.println("Operation: Transposing - Memory use before this operation: " + getMemUsed() + " KB of GPU Memory.");
			MatrixProcessor.transpose(matPointer, transpose.matPointer);
			System.out.println("Updated memory use after operation: " + getMemUsed() + " KB of GPU Memory.");
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
	}

	private ArrayList<Cell> getSparseData() {

		Cell[] nzList = MatrixProcessor.getSparseData(matPointer);
		ArrayList<Cell> cellList = new ArrayList<Cell>();

		for (Cell nz : nzList) {
			cellList.add(new Cell(nz.row, nz.col, nz.value));
		}

		return cellList;
	}

	@Override
	public MatrixOperations aggregateDimension(boolean useGPU) {
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getColType(),
				this.matrixDescriptor.getRowType());

		_newDescriptor.AddRowDesc("SUM");

		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));

		MatrixOperationsGPU reduced = null;

		try {
			reduced = new MatrixOperationsGPU(_newDescriptor, true);
			System.out.println("Operation: Reduction - Memory use before this operation: " + getMemUsed() + " KB of GPU Memory.");
			MatrixProcessor.reduceDimension(matPointer, reduced.matPointer, useGPU);
			System.out.println("Updated memory use after operation: " + getMemUsed() + " KB of GPU Memory.");
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return reduced;
	}

	@Override
	public MatrixOperations confidence(boolean useGPU) {
		MatrixDescriptor _newDescriptor = this.matrixDescriptor;

		MatrixOperationsGPU confidence = null;

		try {
			System.out.println("Operation: Confidence - Memory use before this operation: " + getMemUsed() + " KB of GPU Memory.");
			confidence = new MatrixOperationsGPU(_newDescriptor, true);
			MatrixProcessor.confidence(matPointer, confidence.matPointer, useGPU);
			System.out.println("Updated memory use after operation: " + getMemUsed() + " KB of GPU Memory.");
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
			System.out.println("Operation: TransitiveClosure - Memory use before this operation: " + getMemUsed() + " KB of GPU Memory.");
			transitiveClosure = new MatrixOperationsGPU(_newDescriptor, false, rows, cols);
			MatrixProcessor.transitiveClosure(matrixDescriptor.getNumRows(), matPointer, transitiveClosure.matPointer);
			System.out.println("Updated memory use after operation: " + getMemUsed() + " KB of GPU Memory.");
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
		MatrixDescriptor _newDescriptor = this.matrixDescriptor;
		MatrixOperationsGPU binarize = null;

		try {
			System.out.println("Operation: Binarize - Memory use before this operation: " + getMemUsed() + " KB of GPU Memory.");
			binarize = new MatrixOperationsGPU(_newDescriptor, true);
			MatrixProcessor.binarize(matPointer, binarize.matPointer);
			System.out.println("Updated memory use after operation: " + getMemUsed() + " KB of GPU Memory.");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return binarize;
	}

	@Override
	public MatrixOperations invert() {
		MatrixDescriptor _newDescriptor = this.matrixDescriptor;
		MatrixOperationsGPU invert = null;
		try {
			System.out.println("Operation: Invert - Memory use before this operation: " + getMemUsed() + " KB of GPU Memory.");
			invert = new MatrixOperationsGPU(_newDescriptor, false, rows, cols);
			MatrixProcessor.invert(_newDescriptor.getNumRows() * _newDescriptor.getNumCols(), matPointer,
					invert.matPointer);
			System.out.println("Updated memory use after operation: " + getMemUsed() + " KB of GPU Memory.");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return invert;
	}

	@Override
	public MatrixOperations lowerDiagonal() {
		MatrixDescriptor _newDescriptor = this.matrixDescriptor;
		MatrixOperationsGPU lowerDiagonal = null;
		try {
			System.out.println("Operation: LowerDiagonal - Memory use before this operation: " + getMemUsed() + " KB of GPU Memory.");
			lowerDiagonal = new MatrixOperationsGPU(_newDescriptor, false, rows, cols);
			MatrixProcessor.lowerDiagonal(this.matrixDescriptor.getNumRows(), matPointer, lowerDiagonal.matPointer);
			System.out.println("Updated memory use after operation: " + getMemUsed() + " KB of GPU Memory.");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return lowerDiagonal;
	}

	@Override
	public MatrixOperations upperDiagonal() {
		MatrixDescriptor _newDescriptor = this.matrixDescriptor;
		MatrixOperationsGPU upperDiagonal = null;
		try {
			System.out.println("Operation: UpperDiagonal - Memory use before this operation: " + getMemUsed() + " KB of GPU Memory.");
			upperDiagonal = new MatrixOperationsGPU(_newDescriptor, false, rows, cols);
			MatrixProcessor.upperDiagonal(this.matrixDescriptor.getNumRows(), matPointer, upperDiagonal.matPointer);
			System.out.println("Updated memory use after operation: " + getMemUsed() + " KB of GPU Memory.");
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return upperDiagonal;
	}

	@Override
	public MatrixOperations diagonalize() {
		MatrixDescriptor _newDescriptor = this.matrixDescriptor;
		MatrixOperationsGPU diagonalize = null;
		try {
			System.out.println("Operation: Diagonalize - Memory use before this operation: " + getMemUsed() + " KB of GPU Memory.");
			diagonalize = new MatrixOperationsGPU(_newDescriptor, false, rows, cols);
			MatrixProcessor.diagonalize(this.matrixDescriptor.getNumRows() * this.matrixDescriptor.getNumCols(),
					matPointer, diagonalize.matPointer);
			System.out.println("Updated memory use after operation: " + getMemUsed() + " KB of GPU Memory.");
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
		Cell[] nzList = MatrixProcessor.getData(matPointer, rows, cols);
		ArrayList<Cell> cellList = new ArrayList<Cell>();

		for (Cell nz : nzList) {
			cellList.add(new Cell(nz.row, nz.col, nz.value));
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
		CRSMatrix oldMatrix = Prov2DominoesUtil.cells2Matrix(getData(), matrixDescriptor.getNumRows(), matrixDescriptor.getNumCols());
		for (int i = 0; i < matrixDescriptor.getNumRows(); i++) {
			for (int j = 0; j < matrixDescriptor.getNumCols(); j++) {
				matrix[i][j] = oldMatrix.get(rowsIndexes.get(rowsDesc.get(i)), j);
			}
		}

		result.setData(new CRSMatrix(matrix));
		return result;
	}

	@Override
	public MatrixOperations sortColumns() {
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
		CRSMatrix oldMatrix = Prov2DominoesUtil.cells2Matrix(getData(), matrixDescriptor.getNumRows(), matrixDescriptor.getNumCols());
		for (int i = 0; i < matrixDescriptor.getNumRows(); i++) {
			for (int j = 0; j < matrixDescriptor.getNumCols(); j++) {
				matrix[i][j] = oldMatrix.get(i, colsIndexes.get(colsDesc.get(j)));
			}
		}

		result.setData(new CRSMatrix(matrix));
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
				crsResult.set(i, j, oldCRS.get(this.getMatrixDescriptor().getRowElementIndex(matrixDescriptor.getRowAt(i)),
						this.getMatrixDescriptor().getColElementIndex(matrixDescriptor.getColumnAt(j))));
			}
		}
		result.setData(crsResult);
		return result;
	}
	
	@Override
	public MatrixOperations standardScoreDense() {
		// TODO Pending GPU implementation
		return null;
	}

	@Override
	public MatrixOperations meanAndSD() {
		// TODO Pending GPU implementation
		return null;
	}

	@Override
	public MatrixOperations trim() {
		// TODO Pending GPU implementation
		return null;
	}

	@Override
	public MatrixOperations highPassFilter(double d) {
		// TODO Pending GPU implementation
		return null;
	}

	@Override
	public MatrixOperations lowPassFilter(double d) {
		// TODO Pending GPU implementation
		return null;
	}

	@Override
	public MatrixOperations filterColumnText(TextFilterData t) {
		// TODO Pending GPU implementation
		return null;
	}

	@Override
	public MatrixOperations filterRowText(TextFilterData t) {
		// TODO Pending GPU implementation
		return null;
	}

	@Override
	public MatrixOperations standardScoreSparse() {
		// TODO Pending GPU implementation
		return null;
	}

	@Override
	public ArrayList<Cell> getAllData() {
		return getData();
	}

	@Override
	public MatrixOperations sortColumnFirst() {
		// TODO Pending GPU implementation
		return null;
	}

	@Override
	public MatrixOperations sortRowFirst() {
		// TODO Pending GPU implementation
		return null;
	}

	

}