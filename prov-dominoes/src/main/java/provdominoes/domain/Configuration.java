package provdominoes.domain;

import java.util.Date;

import processor.MatrixProcessor;

public class Configuration {
	
	public static final String GPU_PROCESSING = "GPU";
	public static final String CPU_PROCESSING = "CPU";
	
    // to read file
	public static String lastDirectory = ".";
	public static boolean fullScreen = false;
    public static boolean autoSave = false;
    public static boolean visibilityHistoric = true;
    public static boolean visibilityType = true;
    public static boolean resizable = true;
    public static boolean automaticCheck = false;
    public static boolean resizableTimeOnFullScreen = false;
    public static boolean defaultFactory = true;
    public static boolean deriveInfluence = false;
    
    public static double width = 1000.0f;
    public static double height = 600.0f;
    public static double listWidth = 128.0f;
    
    public static String DATA_SEPARATOR = "	";
    public static String defaultProcessing = GPU_PROCESSING;
    public static int gpuDevice = 0;
    
	public static Date beginDate = null;
    public static Date endDate = null; 
    
    // not save/read file 
    public static double fullscreenWidth = Configuration.width;
    public static double fullscreenHeight = Configuration.height;
    
    public static boolean visibilityGraphHistory = false;
	public static boolean telemetry = false;
	
	public static boolean isGPUProcessing() {
		return !MatrixProcessor.isLibSkipped() && defaultProcessing.equals(GPU_PROCESSING) && MatrixProcessor.isGPUEnabled();
	}
	
	
}