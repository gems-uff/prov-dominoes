package com.josericardojunior.arch;

import java.util.ArrayList;

public interface MatrixOperations {
	
	
	public boolean isEmpty();
	public int getMemUsed();	
	public void setData(ArrayList<Cell> cells);
	public void finalize();		
	public void Debug();	
	public void ExportCSV(String filename);	
	public ArrayList<Cell> getNonZeroData();	
	public MatrixDescriptor getMatrixDescriptor();	
	public StringBuffer ExportCSV();	
	public String toString();
	public float findMinValue();	
	public float findMaxValue();	
	public MatrixOperations transpose();	
	public MatrixOperations multiply(MatrixOperations other, boolean useGPU) throws Exception;	
	public MatrixOperations reduceRows(boolean useGPU);	
	public MatrixOperations confidence(boolean useGPU);	
	public MatrixOperations meanAndSD(boolean useGPU);	
	public MatrixOperations standardScore(boolean useGPU);	
	
	public MatrixOperations transitiveClosure(boolean useGPU);
	public MatrixOperations invert(boolean useGPU);	
	public MatrixOperations binarize(boolean useGPU);
	public MatrixOperations diagonalize(boolean useGPU);
	public MatrixOperations lowerDiagonal(boolean useGPU);
	public MatrixOperations upperDiagonal(boolean useGPU);
	
	
}
