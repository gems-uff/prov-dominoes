package com.josericardojunior.arch;

import java.util.ArrayList;

public interface MatrixOperations {
	
	
	public void setData(ArrayList<Cell> cells);
	public ArrayList<Cell> getNonZeroData();
	
	public int getMemUsed();
	
	public MatrixOperations transpose();
	
	public MatrixDescriptor getMatrixDescriptor();
	
	public void finalize();
		
	public MatrixOperations multiply(MatrixOperations other, boolean useGPU) throws Exception;
	
	public MatrixOperations reduceRows(boolean useGPU);
	
	public MatrixOperations confidence(boolean useGPU);
	
	public MatrixOperations meanAndSD(boolean useGPU);
	
	public MatrixOperations standardScore(boolean useGPU);
	
	public MatrixOperations transitiveClosure(boolean useGPU);
	
	public void Debug();
	
	public void ExportCSV(String filename);
	
	public StringBuffer ExportCSV();
	
	public float findMinValue();
	
	public float findMaxValue();
	
	
}
