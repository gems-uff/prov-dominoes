package domain;

import java.util.Date;

public class Configuration {
	
	public static final String GPU_DEVICE = "GPU";
	public static final String CPU_DEVICE = "CPU";
	
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
    
    public static double width = 1000.0f;
    public static double height = 600.0f;
    public static double listWidth = 147.0f;
    
    public static String processingUnit = CPU_DEVICE;
    //public static String processingUnit =  MatrixProcessor.isGPUEnabled() ? GPU_DEVICE : CPU_DEVICE;
    public static int gpuDevice = 0;
    
	public static Date beginDate = null;
    public static Date endDate = null; 
    
    // not save/read file 
    public static double fullscreenWidth = Configuration.width;
    public static double fullscreenHeight = Configuration.height;
    
    public static boolean visibilityTimePane = true;
	public static boolean telemetry = false;
}