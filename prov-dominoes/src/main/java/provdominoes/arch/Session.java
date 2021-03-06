package provdominoes.arch;

import java.util.ArrayList;
import java.util.List;

import processor.MatrixProcessor;

public class Session {

	private static boolean sessionStarted = false;
	private static List<MatrixOperationsGPU> matrices;
	private static int gpuMemoryUsed = 0;

	public static boolean isSessionStarted() {
		return sessionStarted;
	}

	public static float getMemUsed() {
		return gpuMemoryUsed;
	}

	public static void startSession(int deviceToUse) {
		sessionStarted = true;
		MatrixProcessor.resetGPU(deviceToUse);
		matrices = new ArrayList<MatrixOperationsGPU>();
	}

	public static void closeSection() {
		System.out.println("Releasing GPU Memory: "+ getMemUsed() + " KB...");
	}

	public static void debugInfo() {
		System.out.println("Memory used: " + getMemUsed() + " KB");
	}

	public static void register2DMatrix(MatrixOperationsGPU mat) {
		gpuMemoryUsed += mat.getMemUsed();
		matrices.add(mat);
	}

}
