package provdominoes.arch;

import java.util.ArrayList;

import org.la4j.matrix.sparse.CRSMatrix;

import processor.Cell;
import processor.MatrixProcessor;
import provdominoes.command.TextFilterData;
import provdominoes.domain.Configuration;
import provdominoes.util.Prov2DominoesUtil;

public interface MatrixOperations {

	
	public boolean isEmpty();

	public void finalize();

	public int getMemUsed();

	public String toString();

	public MatrixDescriptor getMatrixDescriptor();

	public ArrayList<Cell> getData();

	public void setData(ArrayList<Cell> cells);

	public float findMinValue();

	public float findMaxValue();

	public MatrixOperations transpose();

	public MatrixOperations sum(MatrixOperations other) throws Exception;
	public MatrixOperations subtract(MatrixOperations other) throws Exception;
	
	public MatrixOperations multiply(MatrixOperations other, boolean useGPU) throws Exception;

	public MatrixOperations aggregateDimension(boolean useGPU);

	public MatrixOperations confidence(boolean useGPU);

	public MatrixOperations standardScoreDense();

	public MatrixOperations meanAndSD();

	public MatrixOperations transitiveClosure();

	public MatrixOperations invert();

	public MatrixOperations binarize();
	

	public MatrixOperations diagonalize();

	public MatrixOperations lowerDiagonal();

	public MatrixOperations upperDiagonal();
	
	public MatrixOperations trim();
	public MatrixOperations highPassFilter(double d);
	public MatrixOperations lowPassFilter(double d);

	public MatrixOperations filterColumnText(TextFilterData t);
	public MatrixOperations filterRowText(TextFilterData t);
    
	public MatrixOperations sortColumns();
    
	public MatrixOperations sortRows();
    
	public MatrixOperations sortColumnFirst();
	
	public MatrixOperations sortRowFirst();
	
	public MatrixOperations standardScoreSparse();
	
	public ArrayList<Cell> getAllData();
	
	public MatrixOperations sortEqualTo(MatrixDescriptor matrixDescriptor);
	
	public static MatrixOperations configureOperation(CRSMatrix matrix, MatrixDescriptor descriptor, boolean isSparse)
			throws Exception {
		MatrixOperations mat = MatrixOperationsFactory
				.getMatrix2D(
						!MatrixProcessor.isLibSkipped() && MatrixProcessor.isGPUEnabled()
								&& Configuration.defaultProcessing.equals(Configuration.GPU_PROCESSING),
						descriptor, isSparse);
		mat.setData(Prov2DominoesUtil.matrix2Cells(matrix));
		return mat;
	}

	MatrixOperations sortDefaultDimensionValues();

	MatrixOperations sortByRowGroup();

	public MatrixOperations sortByColumnGroup();

	void setMatrix(CRSMatrix matrix);

	CRSMatrix getMatrix();
}
