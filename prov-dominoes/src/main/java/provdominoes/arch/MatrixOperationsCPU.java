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
import provdominoes.command.TextFilterData;
import provdominoes.util.Prov2DominoesUtil;
import provdominoes.util.SortIgnoreCase;

public class MatrixOperationsCPU implements MatrixOperations {

	private CRSMatrix data;
	private String[][] underlyingElements;
	private MatrixDescriptor matrixDescriptor;

	public MatrixOperationsCPU(MatrixDescriptor _matrixDescriptor) {

		matrixDescriptor = _matrixDescriptor;

		data = new CRSMatrix(matrixDescriptor.getNumRows(), matrixDescriptor.getNumCols());
	}

	public MatrixDescriptor getMatrixDescriptor() {
		return matrixDescriptor;
	}

	@Override
	public int getMemUsed() {
		return matrixDescriptor.getNumCols() * matrixDescriptor.getNumRows() * (Float.SIZE / 8);
	}

	@Override
	public String toString() {
		return this.data.toString();
	}

	public void finalize() {
	}

	public String[][] getUnderlyingElements() {
		return this.underlyingElements;
	}

	@Override
	public MatrixOperations subtract(MatrixOperations other) throws Exception {
		MatrixDescriptor otherDescriptor = other.getMatrixDescriptor();

		if (matrixDescriptor.getNumRows() != otherDescriptor.getNumRows()
				&& matrixDescriptor.getNumCols() != otherDescriptor.getNumCols())
			throw new Exception("Matrix cannot be subtracted!");

		CRSMatrix crsData = Prov2DominoesUtil.cells2Matrix(this.getData(), this.getMatrixDescriptor().getNumRows(),
				this.getMatrixDescriptor().getNumCols());

		MatrixOperationsCPU otherCPU = (MatrixOperationsCPU) other;
		otherCPU = (MatrixOperationsCPU) otherCPU.sortEqualTo(this.getMatrixDescriptor());
		CRSMatrix otherData = Prov2DominoesUtil.cells2Matrix(otherCPU.getData(),
				otherCPU.getMatrixDescriptor().getNumRows(), otherCPU.getMatrixDescriptor().getNumCols());

		CRSMatrix crsResult = (CRSMatrix) crsData.subtract(otherData);
		MatrixOperationsCPU result = new MatrixOperationsCPU(otherCPU.getMatrixDescriptor());
		result.setData(crsResult);
		result.setUnderlyingElements(otherCPU.getUnderlyingElements());
		return result;
	}

	@Override
	public MatrixOperations sum(MatrixOperations other) throws Exception {
		MatrixDescriptor otherDescriptor = other.getMatrixDescriptor();

		if (matrixDescriptor.getNumRows() != otherDescriptor.getNumRows()
				&& matrixDescriptor.getNumCols() != otherDescriptor.getNumCols())
			throw new Exception("Matrix cannot be added!");

		CRSMatrix crsData = Prov2DominoesUtil.cells2Matrix(this.getData(), this.getMatrixDescriptor().getNumRows(),
				this.getMatrixDescriptor().getNumCols());

		MatrixOperationsCPU otherCPU = (MatrixOperationsCPU) other;
		otherCPU = (MatrixOperationsCPU) otherCPU.sortEqualTo(this.getMatrixDescriptor());
		CRSMatrix otherData = Prov2DominoesUtil.cells2Matrix(otherCPU.getData(),
				otherCPU.getMatrixDescriptor().getNumRows(), otherCPU.getMatrixDescriptor().getNumCols());

		CRSMatrix crsResult = (CRSMatrix) crsData.add(otherData);
		MatrixOperationsCPU result = new MatrixOperationsCPU(otherCPU.getMatrixDescriptor());
		result.setData(crsResult);
		result.setUnderlyingElements(otherCPU.getUnderlyingElements());
		return result;
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

		MatrixOperationsCPU result = new MatrixOperationsCPU(resultDesc);

		MatrixOperationsCPU otherJava = (MatrixOperationsCPU) other;

		this.data = Prov2DominoesUtil.cells2Matrix(this.sortColumns().getData(), this.matrixDescriptor.getNumRows(),
				this.matrixDescriptor.getNumCols());
		other.setData(otherJava.sortRows().getData());

		List<String> cols = new ArrayList<>(getMatrixDescriptor().getColumnsDesc());
		Collections.sort(cols, new SortIgnoreCase());

		result.data = mult(data, otherJava.data, cols);
		return result;
	}

	private CRSMatrix mult(CRSMatrix matrix1, CRSMatrix matrix2, List<String> cols) {
		CRSMatrix result = new CRSMatrix(matrix1.rows(), matrix2.columns());
		this.underlyingElements = new String[matrix1.rows()][matrix2.columns()];
		for (int row = 0; row < matrix1.rows(); row++) {
			for (int col = 0; col < matrix2.columns(); col++) {
				result.set(row, col, multCells(matrix1, matrix2, row, col, cols));
			}
		}
		return result;
	}

	private double multCells(CRSMatrix matrix1, CRSMatrix matrix2, int row, int col, List<String> cols) {
		double cell = 0.0;
		for (int i = 0; i < matrix1.columns(); i++) {
			cell += matrix1.get(row, i) * matrix2.get(i, col);
			if (matrix1.get(row, i) != 0.0 && matrix2.get(i, col) != 0.0) {
				if (this.underlyingElements[row][col] == null) {
					this.underlyingElements[row][col] = cols.get(i);
				} else {
					if (this.underlyingElements[row][col].chars().filter(ch -> ch == ',').count()>=3) {
						if (!this.underlyingElements[row][col].contains("...")) {
							this.underlyingElements[row][col] += "...";
						}
					} else {
						this.underlyingElements[row][col] += ", " + cols.get(i);
					}
				}
			}
		}
		return cell;
	}

	public MatrixOperations multiplyOld(MatrixOperations other, boolean useGPU) throws Exception {
		MatrixDescriptor otherDescriptor = other.getMatrixDescriptor();

		if (matrixDescriptor.getNumCols() != otherDescriptor.getNumRows())
			throw new Exception("Matrix cannot be multiplied!");

		MatrixDescriptor resultDesc = new MatrixDescriptor(matrixDescriptor.getRowType(), otherDescriptor.getColType());

		for (int i = 0; i < matrixDescriptor.getNumRows(); i++)
			resultDesc.AddRowDesc(matrixDescriptor.getRowAt(i));

		for (int i = 0; i < otherDescriptor.getNumCols(); i++)
			resultDesc.AddColDesc(otherDescriptor.getColumnAt(i));

		MatrixOperationsCPU result = new MatrixOperationsCPU(resultDesc);

		MatrixOperationsCPU otherJava = (MatrixOperationsCPU) other;

		this.data = Prov2DominoesUtil.cells2Matrix(this.sortColumns().getData(), this.matrixDescriptor.getNumRows(),
				this.matrixDescriptor.getNumCols());
		other.setData(otherJava.sortRows().getData());
		result.data = (CRSMatrix) data.multiply(otherJava.data);

		return result;
	}

	public MatrixOperations transpose() {
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getColType(),
				this.matrixDescriptor.getRowType());

		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getColumnAt(i));

		for (int i = 0; i < this.matrixDescriptor.getNumRows(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getRowAt(i));

		MatrixOperationsCPU transpose = new MatrixOperationsCPU(_newDescriptor);
		transpose.data = (CRSMatrix) data.transpose();

		return transpose;
	}

	public void debug() {

		ArrayList<Cell> cells = getData();

		int currentLine = -1;

		for (int i = 0; i < cells.size(); i++) {
			Cell cell = cells.get(i);

			if (currentLine != cell.row) {
				System.out.println();
				currentLine = cell.row;
			}

			System.out.print(cell.value + "\t");
		}
	}

	public float findMinValue() {

		return (float) data.min();
	}

	public float findMaxValue() {

		return (float) data.max();
	}

	@Override
	public void setData(ArrayList<Cell> cells) {
		this.data = Prov2DominoesUtil.cells2Matrix(cells, matrixDescriptor.getNumRows(), matrixDescriptor.getNumCols());
	}

	public void setData(CRSMatrix matrix) {
		this.data = matrix;
	}

	@Override
	public MatrixOperations aggregateDimension(boolean useGPU) {
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getColType(),
				this.matrixDescriptor.getRowType());

		_newDescriptor.AddRowDesc("SUM");

		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));

		MatrixOperations reduced = new MatrixOperationsCPU(_newDescriptor);

		float[] rowSum = new float[this.matrixDescriptor.getNumCols()];
		ArrayList<Cell> nz = getData();

		for (Cell c : nz) {
			rowSum[c.col] += c.value;
		}

		ArrayList<Cell> resCells = new ArrayList<Cell>();

		for (int i = 0; i < rowSum.length; i++) {
			if (Math.abs(rowSum[i]) > 0) {
				resCells.add(new Cell(0, i, rowSum[i]));
			}
		}
		reduced.setData(resCells);

		return reduced;
	}

	@Override
	public MatrixOperations confidence(boolean useGPU) {
		List<Cell> nonZeros = getData();

		ArrayList<Cell> newValues = new ArrayList<Cell>();

		for (Cell cell : nonZeros) {
			Cell c = new Cell();
			c.row = cell.row;
			c.col = cell.col;

			float diagonal = (float) data.get(c.row, c.row);

			if (diagonal > 0)
				c.value = cell.value / diagonal;

			newValues.add(c);
		}

		MatrixOperations confidenceM = new MatrixOperationsCPU(getMatrixDescriptor());
		confidenceM.setData(newValues);

		return confidenceM;
	}

	@Override
	public MatrixOperations meanAndSD() {
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getColType(),
				this.matrixDescriptor.getRowType());

		_newDescriptor.AddRowDesc("MEAN");
		_newDescriptor.AddRowDesc("SD");

		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));

		MatrixOperations meanSD = new MatrixOperationsCPU(_newDescriptor);

		float meanCol[] = new float[meanSD.getMatrixDescriptor().getNumCols()];
		float sdCol[] = new float[meanSD.getMatrixDescriptor().getNumCols()];
		float values[] = new float[meanSD.getMatrixDescriptor().getNumCols()];
		int numElements[] = new int[meanSD.getMatrixDescriptor().getNumCols()];

		data.eachNonZero(new MatrixProcedure() {

			@Override
			public void apply(int row, int col, double value) {
				numElements[col]++;
				values[col] += value;
			}
		});

		for (int i = 0; i < values.length; i++)
			meanCol[i] = values[i] / (float) numElements[i];

		data.eachNonZero(new MatrixProcedure() {

			@Override
			public void apply(int row, int col, double value) {
				sdCol[col] += (value - meanCol[col]) * (value - meanCol[col]);
			}
		});

		for (int i = 0; i < sdCol.length; i++)
			sdCol[i] = (float) Math.sqrt((double) (sdCol[i] * (1.0f / (float) numElements[i])));

		ArrayList<Cell> _data = new ArrayList<>();
		for (int i = 0; i < sdCol.length; i++) {
			Cell cMean = new Cell(0, i, meanCol[i]);
			Cell cStd = new Cell(1, i, sdCol[i]);
			_data.add(cMean);
			_data.add(cStd);
		}

		meanSD.setData(_data);

		return meanSD;
	}

	@Override
	public MatrixOperations standardScoreDense() {

		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getRowType(),
				this.matrixDescriptor.getColType());

		for (int i = 0; i < this.matrixDescriptor.getNumRows(); i++)
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));

		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));

		MatrixOperations _standardScore = new MatrixOperationsCPU(_newDescriptor);

		float meanCol[] = new float[_standardScore.getMatrixDescriptor().getNumCols()];
		float sdCol[] = new float[_standardScore.getMatrixDescriptor().getNumCols()];
		float values[] = new float[_standardScore.getMatrixDescriptor().getNumCols()];
		int numElements[] = new int[_standardScore.getMatrixDescriptor().getNumCols()];

		data.each(new MatrixProcedure() {

			@Override
			public void apply(int row, int col, double value) {
				numElements[col]++;
				values[col] += value;
			}
		});

		for (int i = 0; i < values.length; i++)
			meanCol[i] = values[i] / (float) numElements[i];

		data.each(new MatrixProcedure() {

			@Override
			public void apply(int row, int col, double value) {
				sdCol[col] += (value - meanCol[col]) * (value - meanCol[col]);
				values[col] += value;
			}
		});

		for (int i = 0; i < sdCol.length; i++)
			sdCol[i] = (float) Math.sqrt((double) (sdCol[i] * (1.0f / (float) numElements[i])));

		ArrayList<Cell> _data = new ArrayList<>();
		data.each(new MatrixProcedure() {

			@Override
			public void apply(int row, int col, double value) {
				Cell _cell = new Cell();
				_cell.col = col;
				_cell.row = row;
				_cell.value = ((float) value - meanCol[col]) / sdCol[col];
				_data.add(_cell);
			}
		});

		_standardScore.setData(_data);

		return _standardScore;
	}

	@Override
	public MatrixOperations standardScoreSparse() {

		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getRowType(),
				this.matrixDescriptor.getColType());

		for (int i = 0; i < this.matrixDescriptor.getNumRows(); i++)
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));

		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));

		MatrixOperations _standardScore = new MatrixOperationsCPU(_newDescriptor);

		float meanCol[] = new float[_standardScore.getMatrixDescriptor().getNumCols()];
		float sdCol[] = new float[_standardScore.getMatrixDescriptor().getNumCols()];
		float sumCol[] = new float[_standardScore.getMatrixDescriptor().getNumCols()];
		int numElements[] = new int[_standardScore.getMatrixDescriptor().getNumCols()];

		data.eachNonZero(new MatrixProcedure() {

			@Override
			public void apply(int row, int col, double value) {
				numElements[col]++;
				sumCol[col] += value;
			}
		});

		for (int i = 0; i < sumCol.length; i++)
			meanCol[i] = sumCol[i] / (float) numElements[i];

		data.eachNonZero(new MatrixProcedure() {

			@Override
			public void apply(int row, int col, double value) {
				sdCol[col] += (value - meanCol[col]) * (value - meanCol[col]);
				sumCol[col] += value;
			}
		});

		for (int i = 0; i < sdCol.length; i++)
			sdCol[i] = (float) Math.sqrt((double) (sdCol[i] * (1.0f / (float) numElements[i])));

		ArrayList<Cell> _data = new ArrayList<>();
		data.eachNonZero(new MatrixProcedure() {

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

		_standardScore.setData(_data);

		return _standardScore;
	}

	public MatrixOperations binarize() {
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getColType(),
				this.matrixDescriptor.getRowType());
		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));
		for (int i = 0; i < this.matrixDescriptor.getNumRows(); i++)
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
		MatrixOperationsCPU _binarizeFilter = new MatrixOperationsCPU(_newDescriptor);

		int V = data.rows();
		int C = data.columns();
		double[][] filterMatrix = new double[V][C];
		for (int i = 0; i < V; i++) {
			for (int j = 0; j < C; j++) {
				if (data.get(i, j) > 0) {
					filterMatrix[i][j] = 1.00;
				} else {
					filterMatrix[i][j] = 0.00;
				}
			}
		}

		_binarizeFilter.setData(new CRSMatrix(filterMatrix));
		return _binarizeFilter;
	}

	public MatrixOperations invert() {
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getColType(),
				this.matrixDescriptor.getRowType());
		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));
		for (int i = 0; i < this.matrixDescriptor.getNumRows(); i++)
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
		MatrixOperationsCPU _invertFilter = new MatrixOperationsCPU(_newDescriptor);
		int V = data.rows();
		int C = data.columns();
		double[][] filterMatrix = new double[V][C];
		for (int i = 0; i < V; i++) {
			for (int j = 0; j < C; j++) {
				if (data.get(i, j) > 0) {
					filterMatrix[i][j] = 0.00;
				} else {
					filterMatrix[i][j] = 1.00;
				}
			}
		}
		_invertFilter.setData(new CRSMatrix(filterMatrix));
		return _invertFilter;
	}

	public MatrixOperations highPassFilter(double d) {
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getColType(),
				this.matrixDescriptor.getRowType());
		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));
		for (int i = 0; i < this.matrixDescriptor.getNumRows(); i++)
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
		MatrixOperationsCPU _twentyFilter = new MatrixOperationsCPU(_newDescriptor);
		double[][] filterMatrix = new double[matrixDescriptor.getNumRows()][matrixDescriptor.getNumCols()];
		for (int i = 0; i < matrixDescriptor.getNumRows(); i++) {
			for (int j = 0; j < matrixDescriptor.getNumCols(); j++) {
				if (data.get(i, j) > d) {
					filterMatrix[i][j] = data.get(i, j);
				} else {
					filterMatrix[i][j] = 0.00;
				}
			}
		}
		_twentyFilter.setData(new CRSMatrix(filterMatrix));
		return _twentyFilter;
	}

	@Override
	public MatrixOperations lowPassFilter(double d) {
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getColType(),
				this.matrixDescriptor.getRowType());
		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));
		for (int i = 0; i < this.matrixDescriptor.getNumRows(); i++)
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
		MatrixOperationsCPU _twentyFilter = new MatrixOperationsCPU(_newDescriptor);
		double[][] filterMatrix = new double[matrixDescriptor.getNumRows()][matrixDescriptor.getNumCols()];
		for (int i = 0; i < matrixDescriptor.getNumRows(); i++) {
			for (int j = 0; j < matrixDescriptor.getNumCols(); j++) {
				if (data.get(i, j) < d) {
					filterMatrix[i][j] = data.get(i, j);
				} else {
					filterMatrix[i][j] = 0.00;
				}
			}
		}
		_twentyFilter.setData(new CRSMatrix(filterMatrix));
		return _twentyFilter;
	}

	public MatrixOperations filterColumnText(TextFilterData t) {
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getRowType(),
				this.matrixDescriptor.getColType());
		for (int i = 0; i < this.matrixDescriptor.getNumRows(); i++)
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));
		MatrixOperationsCPU _columnFilter = new MatrixOperationsCPU(_newDescriptor);
		double[][] filterMatrix = new double[matrixDescriptor.getNumRows()][matrixDescriptor.getNumCols()];
		for (int i = 0; i < matrixDescriptor.getNumRows(); i++) {
			for (int j = 0; j < matrixDescriptor.getNumCols(); j++) {
				if ((!t.isRegularExpression() && contains(false, j, t)) || (t.isRegularExpression()
						&& this.matrixDescriptor.getColumnAt(j).matches(t.getExpression()))) {
					filterMatrix[i][j] = data.get(i, j);
				} else {
					filterMatrix[i][j] = 0.00;
				}
			}
		}
		_columnFilter.setData(new CRSMatrix(filterMatrix));
		return _columnFilter;
	}

	public MatrixOperations filterRowText(TextFilterData t) {
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getRowType(),
				this.matrixDescriptor.getColType());
		for (int i = 0; i < this.matrixDescriptor.getNumRows(); i++)
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));
		MatrixOperationsCPU _rowFilter = new MatrixOperationsCPU(_newDescriptor);
		double[][] filterMatrix = new double[matrixDescriptor.getNumRows()][matrixDescriptor.getNumCols()];
		for (int i = 0; i < matrixDescriptor.getNumRows(); i++) {
			for (int j = 0; j < matrixDescriptor.getNumCols(); j++) {
				if ((!t.isRegularExpression() && contains(true, i, t))
						|| (t.isRegularExpression() && this.matrixDescriptor.getRowAt(i).matches(t.getExpression()))) {
					filterMatrix[i][j] = data.get(i, j);
				} else {
					filterMatrix[i][j] = 0.00;
				}
			}
		}
		_rowFilter.setData(new CRSMatrix(filterMatrix));
		return _rowFilter;
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

	public MatrixOperations diagonalize() {
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getColType(),
				this.matrixDescriptor.getRowType());
		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));
		for (int i = 0; i < this.matrixDescriptor.getNumRows(); i++)
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
		MatrixOperationsCPU _diagonalizeFilter = new MatrixOperationsCPU(_newDescriptor);
		double[][] filterMatrix = new double[data.rows()][data.columns()];
		for (int i = 0; i < data.rows(); i++) {
			for (int j = 0; j < data.columns(); j++) {
				if (i == j && data.get(i, j) > 0) {
					filterMatrix[i][j] = data.get(i, j);
				} else {
					filterMatrix[i][j] = 0.00;
				}
			}
		}
		_diagonalizeFilter.setData(new CRSMatrix(filterMatrix));
		return _diagonalizeFilter;
	}

	@Override
	public MatrixOperations upperDiagonal() {
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getColType(),
				this.matrixDescriptor.getRowType());
		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));
		for (int i = 0; i < this.matrixDescriptor.getNumRows(); i++)
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
		MatrixOperationsCPU _upperDiagonal = new MatrixOperationsCPU(_newDescriptor);
		double[][] filterMatrix = new double[data.rows()][data.columns()];
		for (int i = 0; i < data.rows(); i++) {
			for (int j = i; j < data.columns(); j++) {
				filterMatrix[i][j] = data.get(i, j);
			}
		}
		_upperDiagonal.setData(new CRSMatrix(filterMatrix));
		return _upperDiagonal;
	}

	@Override
	public MatrixOperations lowerDiagonal() {
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getColType(),
				this.matrixDescriptor.getRowType());
		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));
		for (int i = 0; i < this.matrixDescriptor.getNumRows(); i++)
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
		MatrixOperationsCPU _lowerDiagonal = new MatrixOperationsCPU(_newDescriptor);
		double[][] filterMatrix = new double[data.rows()][data.columns()];
		for (int j = 0; j < data.columns(); j++) {
			for (int i = j; i < data.rows(); i++) {
				filterMatrix[i][j] = data.get(i, j);
			}
		}
		_lowerDiagonal.setData(new CRSMatrix(filterMatrix));
		return _lowerDiagonal;
	}

	public MatrixOperations trim() {
		MatrixOperationsCPU result = null;
		ArrayList<Cell> newMatrix = new ArrayList<>();
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getRowType(),
				this.matrixDescriptor.getColType());
		boolean[] rowsFilled = new boolean[data.rows()];
		boolean[] colsFilled = new boolean[data.columns()];
		data.eachNonZero(new MatrixProcedure() {
			@Override
			public void apply(int row, int col, double value) {
				rowsFilled[row] = true;
				colsFilled[col] = true;
			}
		});
		int di = 0;
		int dj = 0;
		boolean colAdded = false;
		for (int i = 0; i < data.rows(); i++) {
			if (!rowsFilled[i]) {
				di++;
				continue;
			}
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
			for (int j = 0; j < data.columns(); j++) {
				if (!colsFilled[j]) {
					dj++;
					continue;
				}
				Double v = data.get(i, j);
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
		return result;
	}

	// Transitive closure of graph[][] using Floyd Warshall algorithm
	public MatrixOperations transitiveClosure() {
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getColType(),
				this.matrixDescriptor.getRowType());
		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));
		for (int i = 0; i < this.matrixDescriptor.getNumRows(); i++)
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
		MatrixOperationsCPU _transitiveClosure = new MatrixOperationsCPU(_newDescriptor);
		/*
		 * reach[][] will be the output matrix that will finally have the shortest
		 * distances between every pair of vertices
		 */
		int V = data.rows();
		double steps[][] = new double[V][V];
		double reach[][] = new double[V][V];
		int i, j, k;

		/*
		 * Initialize the solution matrix same as input graph matrix. Or we can say the
		 * initial values of shortest distances are based on shortest paths considering
		 * no intermediate vertex.
		 */
		for (i = 0; i < V; i++)
			for (j = 0; j < V; j++) {
				steps[i][j] = data.get(i, j) > 0 ? 1.00 : 0.00;
				reach[i][j] = data.get(i, j) > 0 ? 1.00 : 0.00;
				if (i == j) {
					steps[i][j] = 1.00;
					reach[i][j] = 1.00;
				}
			}

		/*
		 * Add all vertices one by one to the set of intermediate vertices. ---> Before
		 * start of a iteration, we have reachability values for all pairs of vertices
		 * such that the reachability values consider only the vertices in set {0, 1, 2,
		 * .. k-1} as intermediate vertices. ----> After the end of a iteration, vertex
		 * no. k is added to the set of intermediate vertices and the set becomes {0, 1,
		 * 2, .. k}
		 */
		for (k = 0; k < V; k++) {
			// Pick all vertices as source one by one
			for (i = 0; i < V; i++) {
				// Pick all vertices as destination for the
				// above picked source
				for (j = 0; j < V; j++) {
					// If vertex k is on a path from i to j,
					// then make sure that the value of reach[i][j] is 1
					if (((steps[i][k] != 0.00) && (steps[k][j] != 0.00))) {
						if (i != j) { // ignorar próprio nó (i=j)
							double distIK = (i == k ? 0.00 : steps[i][k]);
							double distKJ = (k == j ? 0.00 : steps[k][j]);
							if (steps[i][j] == 0.00) { // caso em que não foi calculado steps entre IJ ainda
								steps[i][j] = distIK + distKJ;
							} else if (distIK + distKJ < steps[i][j]) { // atualizar se novos steps forem menores
								steps[i][j] = distIK + distKJ;
							}
						}
					}
					if (steps[i][j] > 0.00)
						reach[i][j] = 1.00 / steps[i][j];
				}
			}
		}
		_transitiveClosure.setData(new CRSMatrix(reach));
		return _transitiveClosure;
	}

	@Override
	public MatrixOperations sortEqualTo(MatrixDescriptor matrixDescriptor) {
		MatrixOperationsCPU result = new MatrixOperationsCPU(matrixDescriptor);
		CRSMatrix crsResult = new CRSMatrix(matrixDescriptor.getNumRows(), matrixDescriptor.getNumCols());
		CRSMatrix oldCRS = Prov2DominoesUtil.cells2Matrix(this.getData(), this.getMatrixDescriptor().getNumRows(),
				this.getMatrixDescriptor().getNumCols());
		String[][] updatedUnderlying = this.underlyingElements;
		for (int i = 0; i < crsResult.rows(); i++) {
			for (int j = 0; j < crsResult.columns(); j++) {
				int oldI = this.getMatrixDescriptor().getRowElementIndex(matrixDescriptor.getRowAt(i));
				int oldJ = this.getMatrixDescriptor().getColElementIndex(matrixDescriptor.getColumnAt(j));
				crsResult.set(i, j, oldCRS.get(oldI, oldJ));
				if (updatedUnderlying != null) {
					updatedUnderlying[i][j] = this.underlyingElements[oldI][oldJ];
				}

			}
		}
		result.setData(crsResult);
		result.setUnderlyingElements(updatedUnderlying);
		return result;
	}

	@Override
	public MatrixOperations sortRows() {
		MatrixDescriptor _matrixDescriptor = new MatrixDescriptor(matrixDescriptor.getRowType(),
				matrixDescriptor.getColType());
		List<String> rowsDesc = new ArrayList<>(matrixDescriptor.getRowsDesc());
		Map<String, Integer> rowsIndexes = new HashMap<>();
		String[][] updatedUnderlying = this.underlyingElements;
		for (int i = 0; i < rowsDesc.size(); i++) {
			rowsIndexes.put(rowsDesc.get(i), i);
		}
		Collections.sort(rowsDesc, new SortIgnoreCase());
		_matrixDescriptor.setRowsDesc(rowsDesc);
		_matrixDescriptor.setColumnsDesc(matrixDescriptor.getColumnsDesc());

		MatrixOperationsCPU result = new MatrixOperationsCPU(_matrixDescriptor);

		double[][] matrix = new double[data.rows()][data.columns()];
		for (int i = 0; i < data.rows(); i++) {
			for (int j = 0; j < data.columns(); j++) {
				matrix[i][j] = data.get(rowsIndexes.get(rowsDesc.get(i)), j);
				if (updatedUnderlying != null) {
					updatedUnderlying[i][j] = this.underlyingElements[rowsIndexes.get(rowsDesc.get(i))][j];
				}
			}
		}

		result.setData(new CRSMatrix(matrix));
		result.setUnderlyingElements(updatedUnderlying);
		return result;
	}

	@Override
	public MatrixOperations sortColumns() {
		MatrixDescriptor _matrixDescriptor = new MatrixDescriptor(matrixDescriptor.getRowType(),
				matrixDescriptor.getColType());
		List<String> colsDesc = new ArrayList<>(matrixDescriptor.getColumnsDesc());
		Map<String, Integer> colsIndexes = new HashMap<>();
		String[][] updatedUnderlying = this.underlyingElements;
		for (int i = 0; i < colsDesc.size(); i++) {
			colsIndexes.put(colsDesc.get(i), i);
		}
		Collections.sort(colsDesc, new SortIgnoreCase());
		_matrixDescriptor.setColumnsDesc(colsDesc);
		_matrixDescriptor.setRowsDesc(matrixDescriptor.getRowsDesc());

		MatrixOperationsCPU result = new MatrixOperationsCPU(_matrixDescriptor);

		double[][] matrix = new double[data.rows()][data.columns()];
		for (int i = 0; i < data.rows(); i++) {
			for (int j = 0; j < data.columns(); j++) {
				matrix[i][j] = data.get(i, colsIndexes.get(colsDesc.get(j)));
				if (updatedUnderlying != null) {
					updatedUnderlying[i][j] = this.underlyingElements[i][colsIndexes.get(colsDesc.get(j))];
				}
			}
		}

		result.setData(new CRSMatrix(matrix));
		result.setUnderlyingElements(updatedUnderlying);
		return result;
	}

	@Override
	public MatrixOperations sortColumnFirst() {
		MatrixOperationsCPU result = null;
		ArrayList<Cell> newMatrix = new ArrayList<>();
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getRowType(),
				this.matrixDescriptor.getColType());
		String[][] updatedUnderlying = this.underlyingElements;
		int[] rowInserted = new int[data.rows()];
		int[] colInserted = new int[data.columns()];
		for (int i = 0; i < colInserted.length; i++) {
			colInserted[i] = -1;
		}
		for (int i = 0; i < rowInserted.length; i++) {
			rowInserted[i] = -1;
		}
		for (int i = 0; i < data.rows(); i++) {
			for (int j = 0; j < data.columns(); j++) {
				Double v = data.get(i, j);
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
					if (updatedUnderlying != null) {
						updatedUnderlying[rowInserted[i]][colInserted[j]] = this.underlyingElements[i][j];
					}
				}
			}
		}
		for (int i = 0; i < rowInserted.length; i++) {
			if (rowInserted[i] == -1) {
				_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
				for (int j = 0; j < _newDescriptor.getNumCols(); j++) {
					newMatrix.add(new Cell(_newDescriptor.getNumRows() - 1, j, 0));
					if (updatedUnderlying != null) {
						updatedUnderlying[_newDescriptor.getNumRows() - 1][j] = null;
					}
				}
			}
		}

		for (int i = 0; i < colInserted.length; i++) {
			if (colInserted[i] == -1) {
				_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));
				for (int j = 0; j < _newDescriptor.getNumRows(); j++) {
					newMatrix.add(new Cell(j, _newDescriptor.getNumCols() - 1, 0));
					if (underlyingElements != null) {
						updatedUnderlying[j][_newDescriptor.getNumCols() - 1] = null;
					}
				}
			}
		}

		result = new MatrixOperationsCPU(_newDescriptor);
		result.setData(
				Prov2DominoesUtil.cells2Matrix(newMatrix, _newDescriptor.getNumRows(), _newDescriptor.getNumCols()));
		result.setUnderlyingElements(updatedUnderlying);
		return result;
	}

	@Override
	public MatrixOperations sortRowFirst() {
		MatrixOperationsCPU result = null;
		ArrayList<Cell> newMatrix = new ArrayList<>();
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getRowType(),
				this.matrixDescriptor.getColType());
		String[][] updatedUnderlying = this.underlyingElements;
		int[] rowInserted = new int[data.rows()];
		int[] colInserted = new int[data.columns()];
		for (int i = 0; i < colInserted.length; i++) {
			colInserted[i] = -1;
		}
		for (int i = 0; i < rowInserted.length; i++) {
			rowInserted[i] = -1;
		}
		for (int j = 0; j < data.columns(); j++) {
			for (int i = 0; i < data.rows(); i++) {
				Double v = data.get(i, j);
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
					if (updatedUnderlying != null) {
						updatedUnderlying[rowInserted[i]][colInserted[j]] = this.underlyingElements[i][j];
					}
				}
			}
		}

		for (int i = 0; i < colInserted.length; i++) {
			if (colInserted[i] == -1) {
				_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));
				for (int j = 0; j < _newDescriptor.getNumRows(); j++) {
					newMatrix.add(new Cell(j, _newDescriptor.getNumCols() - 1, 0));
					if (updatedUnderlying != null) {
						updatedUnderlying[j][_newDescriptor.getNumCols() - 1] = null;
					}
				}
			}
		}

		for (int i = 0; i < rowInserted.length; i++) {
			if (rowInserted[i] == -1) {
				_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
				for (int j = 0; j < _newDescriptor.getNumCols(); j++) {
					newMatrix.add(new Cell(_newDescriptor.getNumRows() - 1, j, 0));
					if (updatedUnderlying != null) {
						updatedUnderlying[_newDescriptor.getNumRows() - 1][j] = null;
					}
				}
			}
		}

		result = new MatrixOperationsCPU(_newDescriptor);
		result.setData(
				Prov2DominoesUtil.cells2Matrix(newMatrix, _newDescriptor.getNumRows(), _newDescriptor.getNumCols()));
		result.setUnderlyingElements(updatedUnderlying);
		return result;
	}

	@Override
	public boolean isEmpty() {
		System.out.println(
				matrixDescriptor.getRowType() + ":" + matrixDescriptor.getColType() + " density = " + data.density());
		return data.density() == 0.0;
	}

	@Override
	public ArrayList<Cell> getData() {
		ArrayList<Cell> cells = new ArrayList<Cell>();

		data.eachNonZero(new MatrixProcedure() {

			@Override
			public void apply(int row, int col, double value) {
				Cell cell = new Cell();
				cell.row = row;
				cell.col = col;
				cell.value = (float) value;
				cells.add(cell);
			}
		});

		return cells;
	}

	@Override
	public ArrayList<Cell> getAllData() {
		ArrayList<Cell> cells = new ArrayList<Cell>();

		data.each(new MatrixProcedure() {

			@Override
			public void apply(int row, int col, double value) {
				Cell cell = new Cell();
				cell.row = row;
				cell.col = col;
				cell.value = (float) value;
				cells.add(cell);
			}
		});

		return cells;
	}

	@Override
	public void setSparse(boolean isSparse) {
		// TODO Necessary only to GPU
	}

	public void setRows(int[] rows) {
		// TODO Auto-generated method stub

	}

	public void setCols(int[] cols) {
		// TODO Auto-generated method stub

	}

	public void setUnderlyingElements(String[][] underlyingElements) {
		this.underlyingElements = underlyingElements;
	}

}