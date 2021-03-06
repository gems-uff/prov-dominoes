package provdominoes.domain;

import java.util.Date;

import processor.MatrixProcessor;

public class Configuration {
	
	public static final String GPU_PROCESSING = "GPU";
	public static final String CPU_PROCESSING = "CPU";
	
    // to read file
	public static String lastDirectory = "samples\\";
	public static String autoOpen = "";
	public static boolean fullScreen = false;
    public static boolean autoSave = false;
    public static boolean visibilityHistoric = true;
    public static boolean visibilityType = true;
    public static boolean resizable = true;
    public static boolean automaticCheck = false;
    public static boolean resizableTimeOnFullScreen = false;
    public static boolean deriveInfluence = false;
    public static boolean tuning = false;
    
    public static double width = 1000.0;
    public static double height = 600.0;
    public static double listWidth = 128.0;
    
    public static String DATA_SEPARATOR = "	";
    public static String defaultProcessing = CPU_PROCESSING;
    public static int gpuDevice = 0;
    
	public static Date beginDate = null;
    public static Date endDate = null; 
    
    public static double fullscreenWidth = Configuration.width;
    public static double fullscreenHeight = Configuration.height;
    
    public static boolean visibilityGraphHistory = false;
	public static boolean telemetry = false;
	public static boolean showCellValues = false;
	
	public static boolean isGPUProcessing() {
		return !MatrixProcessor.isLibSkipped() && defaultProcessing.equals(GPU_PROCESSING) && MatrixProcessor.isGPUEnabled();
	}
	
	
}