package com.josericardojunior.arch;

public class MatrixOperationsFactory {

	/**
     * This function is called to define the type of access to processing unit
     * @param type String with access type
     * @param parameter of the class builder
     * @return Access type
	 * @throws Exception 
     */
    public static MatrixOperations getMatrix2D(String type, MatrixDescriptor _matrixDescriptor) throws Exception{
        if (type.toUpperCase().equals("GPU")) {
            return new MatrixOperationsGPU(_matrixDescriptor);
        }else if (type.toUpperCase().equals("CPU")) {
            return new MatrixOperationsCPU(_matrixDescriptor);
        }
        return null;
    }
}
