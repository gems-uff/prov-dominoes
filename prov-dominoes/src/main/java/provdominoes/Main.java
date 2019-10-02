package provdominoes;

import processor.MatrixProcessor;
import provdominoes.arch.Session;
import provdominoes.boundary.App;
import provdominoes.control.Controller;
import provdominoes.domain.Configuration;

public class Main {

	public static void main(String args[]) {
		Controller.args = args;
		Main.init();
	}

	public static void init() {
		try {
			// read the configuration file
			Controller.loadConfiguration();

			if (Configuration.gpuDevice + 1 > MatrixProcessor.getDeviceCount()) {
				Configuration.gpuDevice = 0;
			}
			Session.startSession(Configuration.gpuDevice);

			App.start();

			if (Configuration.isGPUProcessing())
				Session.closeSection();

		} catch (Exception ex) {
			App.alertException(ex, ex.getMessage());
		}
	}
}
