package boundary;

import com.josericardojunior.arch.Session;

import control.Controller;
import domain.Configuration;

/**
 *
 * @author Daniel
 */
public class Main {
    public static void main(String args[]){
    	Controller.args = args;
        Main.init();
    }
    
    public static void init(){
        try {
            // read the configuration file
            control.Controller.loadConfiguration();
            
            if (Configuration.processingUnit == Configuration.GPU_DEVICE)
            	Session.startSession(Configuration.gpuDevice);
            
            App.start();
           
            if (Configuration.processingUnit == Configuration.GPU_DEVICE)
            	Session.closeSection();
            
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
}
