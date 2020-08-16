package provdominoes.arch;

public class MatrixOperationsFactory {

	/**
	 * This function is called to define the type of access to processing unit
	 * 
	 * @param type      String with access type
	 * @param parameter of the class builder
	 * @return Access type
	 * @throws Exception
	 */
	public static MatrixOperations getMatrix2D(boolean isGPU, MatrixDescriptor _matrixDescriptor)
			throws Exception {
		if (isGPU) {
			return new MatrixOperationsGPU(_matrixDescriptor);
		} else {
			return new MatrixOperationsCPU(_matrixDescriptor);
		}
	}
}
