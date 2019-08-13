package com.josericardojunior.arch;

import java.util.ArrayList;
import java.util.List;

import org.la4j.matrix.functor.MatrixProcedure;
import org.la4j.matrix.sparse.CRSMatrix;

public class MatrixOperationsCPU implements MatrixOperations {

	private CRSMatrix data;

	private MatrixDescriptor matrixDescriptor;

	public MatrixDescriptor getMatrixDescriptor() {
		return matrixDescriptor;
	}

	public MatrixOperationsCPU(MatrixDescriptor _matrixDescriptor) {

		matrixDescriptor = _matrixDescriptor;

		data = new CRSMatrix(matrixDescriptor.getNumRows(), matrixDescriptor.getNumCols());
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

	/*
	 * public float getElement(String row, String col){
	 * 
	 * int _colIndex = matrixDescriptor.getColElementIndex(col);
	 * 
	 * float[] rowData = getRow(row); return rowData[_colIndex]; }
	 */

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

	public void Debug() {

		ArrayList<Cell> cells = getNonZeroData();

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

	public void ExportCSV(String filename) {

		StringBuffer out = new StringBuffer();

		for (int j = 0; j < matrixDescriptor.getNumCols(); j++) {
			out.append(";");
			out.append(matrixDescriptor.getColumnAt(j));
		}
		out.append("\n");

		/*
		 * for (int i = 0; i < matrixDescriptor.getNumRows(); i++){
		 * 
		 * float[] rowData = getRow(matrixDescriptor.getRowAt(i));
		 * 
		 * out.append(matrixDescriptor.getRowAt(i) + ";");
		 * 
		 * for (int j = 0; j < matrixDescriptor.getNumCols(); j++){
		 * out.append(rowData[j] + ";"); } out.append("\n"); }
		 * 
		 * File f = new File(filename); try { f.createNewFile();
		 * 
		 * FileWriter fw = new FileWriter(f.getAbsoluteFile()); BufferedWriter bw = new
		 * BufferedWriter(fw); bw.write(out.toString()); bw.close(); } catch
		 * (IOException e) { // TODO Auto-generated catch block e.printStackTrace(); }
		 */
	}

	public StringBuffer ExportCSV() {

		StringBuffer out = new StringBuffer();

		for (int j = 0; j < matrixDescriptor.getNumCols(); j++) {
			out.append(";");
			out.append(matrixDescriptor.getColumnAt(j));
		}
		out.append("\n");

		/*
		 * for (int i = 0; i < matrixDescriptor.getNumRows(); i++){ float[] rowData =
		 * getRow(matrixDescriptor.getRowAt(i)); out.append(matrixDescriptor.getRowAt(i)
		 * + ";");
		 * 
		 * for (int j = 0; j < matrixDescriptor.getNumCols(); j++){
		 * out.append(rowData[j] + ";"); } out.append("\n"); }
		 */

		return out;
	}

	public float findMinValue() {

		return (float) data.min();
	}

	public float findMaxValue() {

		return (float) data.max();
	}

	@Override
	public void setData(ArrayList<Cell> cells) {
		for (Cell cell : cells) {
			data.set(cell.row, cell.col, cell.value);
			;
		}

	}

	public void setData(CRSMatrix matrix) {
		this.data = matrix;
	}

	@Override
	public ArrayList<Cell> getNonZeroData() {

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
	public MatrixOperations reduceRows(boolean useGPU) {
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getColType(),
				this.matrixDescriptor.getRowType());

		_newDescriptor.AddRowDesc("SUM");

		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));

		MatrixOperations reduced = new MatrixOperationsCPU(_newDescriptor);

		float[] rowSum = new float[this.matrixDescriptor.getNumCols()];
		ArrayList<Cell> nz = getNonZeroData();

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
		List<Cell> nonZeros = getNonZeroData();

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
	public MatrixOperations meanAndSD(boolean useGPU) {
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
	public MatrixOperations standardScore(boolean useGPU) {

		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getColType(),
				this.matrixDescriptor.getRowType());

		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));

		for (int i = 0; i < this.matrixDescriptor.getNumRows(); i++)
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));

		MatrixOperations _standardScore = new MatrixOperationsCPU(_newDescriptor);

		float meanCol[] = new float[_standardScore.getMatrixDescriptor().getNumCols()];
		float sdCol[] = new float[_standardScore.getMatrixDescriptor().getNumCols()];
		float values[] = new float[_standardScore.getMatrixDescriptor().getNumCols()];
		int numElements[] = new int[_standardScore.getMatrixDescriptor().getNumCols()];

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
				values[col] += value;
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

				_data.add(_cell);
			}
		});

		_standardScore.setData(_data);

		return _standardScore;
	}

	public MatrixOperations binarize(boolean useGPU) {
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getColType(),
				this.matrixDescriptor.getRowType());
		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));
		for (int i = 0; i < this.matrixDescriptor.getNumRows(); i++)
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
		MatrixOperationsCPU _binarizeFilter = new MatrixOperationsCPU(_newDescriptor);

		int V = data.rows();
		double[][] filterMatrix = new double[V][V];
		for (int i = 0; i < filterMatrix.length; i++) {
			for (int j = 0; j < filterMatrix.length; j++) {
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

	public MatrixOperations invert(boolean useGPU) {
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getColType(),
				this.matrixDescriptor.getRowType());
		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));
		for (int i = 0; i < this.matrixDescriptor.getNumRows(); i++)
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
		MatrixOperationsCPU _binarizeFilter = new MatrixOperationsCPU(_newDescriptor);
		int V = data.rows();
		double[][] filterMatrix = new double[V][V];
		for (int i = 0; i < filterMatrix.length; i++) {
			for (int j = 0; j < filterMatrix.length; j++) {
				if (data.get(i, j) > 0) {
					filterMatrix[i][j] = 0.00;
				} else {
					filterMatrix[i][j] = 1.00;
				}
			}
		}
		_binarizeFilter.setData(new CRSMatrix(filterMatrix));
		return _binarizeFilter;
	}

	public MatrixOperations diagonalize(boolean useGPU) {
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
	public MatrixOperations upperDiagonal(boolean useGPU) {
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
	public MatrixOperations lowerDiagonal(boolean useGPU) {
		MatrixDescriptor _newDescriptor = new MatrixDescriptor(this.matrixDescriptor.getColType(),
				this.matrixDescriptor.getRowType());
		for (int i = 0; i < this.matrixDescriptor.getNumCols(); i++)
			_newDescriptor.AddColDesc(this.matrixDescriptor.getColumnAt(i));
		for (int i = 0; i < this.matrixDescriptor.getNumRows(); i++)
			_newDescriptor.AddRowDesc(this.matrixDescriptor.getRowAt(i));
		MatrixOperationsCPU _upperDiagonal = new MatrixOperationsCPU(_newDescriptor);
		double[][] filterMatrix = new double[data.rows()][data.columns()];
		for (int j = 0; j < data.columns(); j++) {
			for (int i = j; i < data.rows(); i++) {
					filterMatrix[i][j] = data.get(i, j);
			}
		}
		_upperDiagonal.setData(new CRSMatrix(filterMatrix));
		return _upperDiagonal;
	}

  
	// Transitive closure of graph[][] using Floyd Warshall algorithm
	public MatrixOperations transitiveClosure(boolean useGPU) {
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
					if ((steps[i][j] != 0) || ((steps[i][k] != 0) && (steps[k][j] != 0))) {
						if ((steps[i][j] == 0.00)) {
							if (i != j && (steps[i][k] != 0) && (steps[k][j] != 0)) {
								double v = (i == k ? 0 : steps[i][k]) + (k == j ? 0 : steps[k][j]);
								steps[i][j] = v != 0.00 ? v : steps[i][j];
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

	public static void main(String args[]) {

		ArrayList<Cell> cells1 = new ArrayList<>();
		cells1.add(new Cell(0, 0, 0));
		cells1.add(new Cell(0, 1, 1));
		cells1.add(new Cell(0, 2, 0));
		cells1.add(new Cell(0, 3, 0));
		cells1.add(new Cell(1, 0, 0));
		cells1.add(new Cell(1, 1, 0));
		cells1.add(new Cell(1, 2, 0));
		cells1.add(new Cell(1, 3, 1));
		cells1.add(new Cell(2, 0, 0));
		cells1.add(new Cell(2, 1, 0));
		cells1.add(new Cell(2, 2, 0));
		cells1.add(new Cell(2, 3, 0));
		cells1.add(new Cell(3, 0, 1));
		cells1.add(new Cell(3, 1, 0));
		cells1.add(new Cell(3, 2, 1));
		cells1.add(new Cell(3, 3, 0));

		ArrayList<Cell> cells2 = new ArrayList<>();
		cells2.add(new Cell(0, 0, 1));
		cells2.add(new Cell(0, 1, 0));
		cells2.add(new Cell(0, 2, 0));
		cells2.add(new Cell(0, 3, 1));
		cells2.add(new Cell(1, 0, 1));
		cells2.add(new Cell(1, 1, 0));
		cells2.add(new Cell(1, 2, 0));
		cells2.add(new Cell(1, 3, 1));
		cells2.add(new Cell(2, 0, 1));
		cells2.add(new Cell(2, 1, 0));
		cells2.add(new Cell(2, 2, 0));
		cells2.add(new Cell(2, 3, 0));
		cells2.add(new Cell(3, 0, 0));
		cells2.add(new Cell(3, 1, 0));
		cells2.add(new Cell(3, 2, 0));
		cells2.add(new Cell(3, 3, 1));

		ArrayList<Cell> cells3 = new ArrayList<>();
		cells3.add(new Cell(0, 0, 1));
		cells3.add(new Cell(1, 0, 0));
		cells3.add(new Cell(2, 0, 0));
		cells3.add(new Cell(3, 0, 0));
		cells3.add(new Cell(4, 0, 0));
		cells3.add(new Cell(5, 0, 0));
		cells3.add(new Cell(6, 0, 1));
		cells3.add(new Cell(7, 0, 0));
		cells3.add(new Cell(8, 0, 0));

		cells3.add(new Cell(0, 0, 0));
		cells3.add(new Cell(1, 1, 0));
		cells3.add(new Cell(2, 1, 0));
		cells3.add(new Cell(3, 1, 1));
		cells3.add(new Cell(4, 1, 0));
		cells3.add(new Cell(5, 1, 0));
		cells3.add(new Cell(6, 1, 0));
		cells3.add(new Cell(7, 1, 0));
		cells3.add(new Cell(8, 1, 0));

		cells3.add(new Cell(0, 2, 1));
		cells3.add(new Cell(1, 2, 0));
		cells3.add(new Cell(2, 2, 0));
		cells3.add(new Cell(3, 2, 0));
		cells3.add(new Cell(4, 2, 0));
		cells3.add(new Cell(5, 2, 0));
		cells3.add(new Cell(6, 2, 0));
		cells3.add(new Cell(7, 2, 0));
		cells3.add(new Cell(8, 2, 0));

		cells3.add(new Cell(0, 3, 0));
		cells3.add(new Cell(1, 3, 0));
		cells3.add(new Cell(2, 3, 0));
		cells3.add(new Cell(3, 3, 1));
		cells3.add(new Cell(4, 3, 0));
		cells3.add(new Cell(5, 3, 0));
		cells3.add(new Cell(6, 3, 0));
		cells3.add(new Cell(7, 3, 0));
		cells3.add(new Cell(8, 3, 0));

		cells3.add(new Cell(0, 4, 0));
		cells3.add(new Cell(1, 4, 0));
		cells3.add(new Cell(2, 4, 1));
		cells3.add(new Cell(3, 4, 0));
		cells3.add(new Cell(4, 4, 1));
		cells3.add(new Cell(5, 4, 0));
		cells3.add(new Cell(6, 4, 0));
		cells3.add(new Cell(7, 4, 0));
		cells3.add(new Cell(8, 4, 0));

		cells3.add(new Cell(0, 5, 0));
		cells3.add(new Cell(1, 5, 1));
		cells3.add(new Cell(2, 5, 0));
		cells3.add(new Cell(3, 5, 0));
		cells3.add(new Cell(4, 5, 0));
		cells3.add(new Cell(5, 5, 1));
		cells3.add(new Cell(6, 5, 0));
		cells3.add(new Cell(7, 5, 0));
		cells3.add(new Cell(8, 5, 0));

		cells3.add(new Cell(0, 6, 0));
		cells3.add(new Cell(1, 6, 0));
		cells3.add(new Cell(2, 6, 0));
		cells3.add(new Cell(3, 6, 0));
		cells3.add(new Cell(4, 6, 0));
		cells3.add(new Cell(5, 6, 0));
		cells3.add(new Cell(6, 6, 1));
		cells3.add(new Cell(7, 6, 0));
		cells3.add(new Cell(8, 6, 1));

		cells3.add(new Cell(0, 7, 0));
		cells3.add(new Cell(1, 7, 0));
		cells3.add(new Cell(2, 7, 1));
		cells3.add(new Cell(3, 7, 0));
		cells3.add(new Cell(4, 7, 0));
		cells3.add(new Cell(5, 7, 1));
		cells3.add(new Cell(6, 7, 0));
		cells3.add(new Cell(7, 7, 1));
		cells3.add(new Cell(8, 7, 0));

		cells3.add(new Cell(0, 8, 1));
		cells3.add(new Cell(1, 8, 0));
		cells3.add(new Cell(2, 8, 0));
		cells3.add(new Cell(3, 8, 0));
		cells3.add(new Cell(4, 8, 0));
		cells3.add(new Cell(5, 8, 0));
		cells3.add(new Cell(6, 8, 0));
		cells3.add(new Cell(7, 8, 1));
		cells3.add(new Cell(8, 8, 1));

		MatrixDescriptor desc1 = new MatrixDescriptor("T1", "T2");
		desc1.AddRowDesc("R1");
		desc1.AddRowDesc("R2");
		desc1.AddRowDesc("R3");
		desc1.AddRowDesc("R4");
		desc1.AddColDesc("C1");
		desc1.AddColDesc("C2");
		desc1.AddColDesc("C3");
		desc1.AddColDesc("C4");

		MatrixDescriptor desc3 = new MatrixDescriptor("T1", "T2");
		desc3.AddRowDesc("R1");
		desc3.AddRowDesc("R2");
		desc3.AddRowDesc("R3");
		desc3.AddRowDesc("R4");
		desc3.AddRowDesc("R5");
		desc3.AddRowDesc("R6");
		desc3.AddRowDesc("R7");
		desc3.AddRowDesc("R8");
		desc3.AddRowDesc("R9");
		desc3.AddColDesc("C1");
		desc3.AddColDesc("C2");
		desc3.AddColDesc("C3");
		desc3.AddColDesc("C4");
		desc3.AddColDesc("C5");
		desc3.AddColDesc("C6");
		desc3.AddColDesc("C7");
		desc3.AddColDesc("C8");
		desc3.AddColDesc("C9");

		try {
			MatrixOperations mat1 = new MatrixOperationsCPU(desc1);
			mat1.setData(cells1);
			// mat1.Debug();

			MatrixDescriptor desc2 = new MatrixDescriptor("T1", "T2");
			desc2.AddRowDesc("R1");
			desc2.AddRowDesc("R2");
			desc2.AddRowDesc("R3");
			desc2.AddRowDesc("R4");
			desc2.AddColDesc("C1");
			desc2.AddColDesc("C2");
			desc2.AddColDesc("C3");
			desc2.AddColDesc("C4");
			MatrixOperations mat2 = new MatrixOperationsCPU(desc2);
			mat2.setData(cells2);
			// mat2.Debug();

			MatrixOperations mat3 = new MatrixOperationsCPU(desc3);
			mat3.setData(cells3);

			// MatrixOperations meanstd = mat2.standardScore(false);
			// meanstd.Debug();
			System.out.println(mat3);
			System.out.println(mat3.transitiveClosure(false));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public boolean isEmpty() {
		System.out.println(
				matrixDescriptor.getRowType() + ":" + matrixDescriptor.getColType() + " density = " + data.density());
		return data.density() == 0.0;
	}
}