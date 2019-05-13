package boundary;

import com.josericardojunior.arch.Session;
import com.josericardojunior.dao.DominoesSQLDao;

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
            
            DominoesSQLDao.openDatabase(Configuration.database);
            // call Application.launch()
            App.start();
           
            if (Configuration.processingUnit == Configuration.GPU_DEVICE)
            	Session.closeSection();
            
            DominoesSQLDao.closeDatabase();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }
}
