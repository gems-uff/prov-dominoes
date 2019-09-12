package boundary;

import arch.Session;

import control.Controller;
import domain.Configuration;

public class Main {

	public static void main(String args[]) {
		Controller.args = args;
		Main.init();
	}

	public static void init() {
		try {
			// read the configuration file
			Controller.loadConfiguration();

			if (Configuration.processingUnit == Configuration.GPU_DEVICE)
				Session.startSession(Configuration.gpuDevice);

			App.start();

			if (Configuration.processingUnit == Configuration.GPU_DEVICE)
				Session.closeSection();

		} catch (Exception ex) {
			App.alertException(ex, ex.getMessage());
		}
	}
}
