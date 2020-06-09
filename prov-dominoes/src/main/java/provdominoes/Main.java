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

			App.start();

			if (!MatrixProcessor.isLibSkipped()) {
				if (Configuration.isGPUProcessing()) {
					Session.closeSection();
				}
			}

		} catch (Exception ex) {
			App.alertException(ex, ex.getMessage());
		}
	}
}
