package provdominoes.arch;

import java.util.ArrayList;

import org.la4j.matrix.sparse.CRSMatrix;

import processor.Cell;
import provdominoes.command.TextFilterData;

public interface MatrixOperations {

	public boolean isEmpty();

	public void finalize();

	public int getMemUsed();

	public String toString();

	public MatrixDescriptor getMatrixDescriptor();

	public ArrayList<Cell> getData(boolean isSparse);
	
	public ArrayList<Cell> getData();

	public void setData(ArrayList<Cell> cells);

	public float findMinValue();

	public float findMaxValue();

	public MatrixOperations transpose() throws Exception;

	public MatrixOperations sum(MatrixOperations other) throws Exception;

	public MatrixOperations subtract(MatrixOperations other) throws Exception;

	public MatrixOperations multiply(MatrixOperations other) throws Exception;

	public MatrixOperations aggregateDimension() throws Exception;

	public MatrixOperations confidence() throws Exception;

	public MatrixOperations standardScoreDense() throws Exception;
	
	public MatrixOperations standardScoreSparse() throws Exception;

	public MatrixOperations meanAndSD() throws Exception;

	public MatrixOperations transitiveClosure() throws Exception;

	public MatrixOperations invert() throws Exception;

	public MatrixOperations binarize() throws Exception;

	public MatrixOperations diagonalize() throws Exception;
	
	public MatrixOperations lowerDiagonal() throws Exception;

	public MatrixOperations upperDiagonal() throws Exception;

	public MatrixOperations trim() throws Exception;

	public MatrixOperations highPassFilter(double d) throws Exception;

	public MatrixOperations lowPassFilter(double d) throws Exception;

	public MatrixOperations filterColumnText(TextFilterData t) throws Exception;

	public MatrixOperations filterRowText(TextFilterData t) throws Exception;

	public MatrixOperations sortColumns() throws Exception;

	public MatrixOperations sortRows() throws Exception;

	public MatrixOperations sortColumnFirst() throws Exception;

	public MatrixOperations sortRowFirst() throws Exception;

	public MatrixOperations sortDefaultDimensionValues() throws Exception;

	public MatrixOperations sortByRowGroup() throws Exception;

	public MatrixOperations sortByColumnGroup() throws Exception;

	public void setMatrix(CRSMatrix matrix);

	public CRSMatrix getMatrix();

	public void updateMatrix(boolean denseToSparse);

	public int[] getRows();

	public int[] getCols();

	public float[] getValues();
}
