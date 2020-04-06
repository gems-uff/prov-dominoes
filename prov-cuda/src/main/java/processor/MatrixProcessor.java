package processor;

import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import util.NativeUtils;

public class MatrixProcessor {

	private static final String ARCH_PROPERTIES_FILE = "arch.properties";
	private static final String LIB_ARCH_FILE = "lib.arch.file";
	private static final String LIB_ARCH_DIR = "lib.arch.dir";
	private static final String DIR_SEPARATOR = "/";
	private static boolean libSkipped = false;

	public native static void resetGPU(int deviceToUse);

	public native static boolean isGPUEnabled();

	public native static int getDeviceCount();

	public native static Cell[] getSparseData(long pointer);

	public native static Cell[] getData(long pointer, int rows[], int cols[]);

	public native static long createMatrixData(int rows, int cols, boolean isSparse);

	public native static boolean deleteMatrixData(long pointer);

	public native static void setData(long pointer, int rows[], int cols[], float values[]);

	public native static void setData(long pointer, float values[]);

	public native static void multiply(long m1, long m2, long result, boolean useGPU);

	public native static void transpose(long m1, long result);

	public native static void reduceDimension(long m1, long result, boolean useGPU);

	public native static void confidence(long m1, long result, boolean useGPU);

	public native static void mean(long pointer, long result, boolean useGPU);

	public native static void meanSD(long pointer, long result, boolean useGPU);

	public native static void standardScore(long pointer, long result, boolean useGPU);

	public native static void standardDeviation(long pointer, long result, boolean useGPU);

	public native static float getMin(long pointer);

	public native static float getMax(long pointer);

	public native static void binarize(long matrixPointer, long resultPointer);

	public native static void invert(int elements, long matrixPointer, long resultPointer);

	public native static void diagonalize(int vertices, long matrixPointer, long resultPointer);

	public native static void upperDiagonal(int vertices, long matrixPointer, long resultPointer);

	public native static void lowerDiagonal(int vertices, long matrixPointer, long resultPointer);

	public native static void transitiveClosure(int vertices, long matrixPointer, long resultPointer);

	public static boolean isLibSkipped() {
		return libSkipped;
	}

	static {
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		if (cl != null) {
			URL url = cl.getResource(ARCH_PROPERTIES_FILE);
			if (url == null) {
				url = cl.getResource(DIR_SEPARATOR + ARCH_PROPERTIES_FILE);
			}
			if (url != null) {
				InputStream in;
				try {
					in = url.openStream();
					Properties props = new Properties();
					props.load(in);
					NativeUtils.loadLibraryFromJar(DIR_SEPARATOR + props.getProperty(LIB_ARCH_DIR) + DIR_SEPARATOR
							+ props.getProperty(LIB_ARCH_FILE));
				} catch (UnsatisfiedLinkError e1) {
					libSkipped = true;
					System.out.println("[ATENÇÃO]: Não foi encontrado dispositivo GPU ativado com drivers CUDA. "
							+ "Modo CPU-only ativado!");
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

}
