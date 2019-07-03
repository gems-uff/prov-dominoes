/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import domain.Configuration;

/**
 *
 * @author Daniel
 */
public class ConfigurationFile {

	private String path = "configuration.properties";

	/**
	 * This Functions is used to load the basic configuration of system
	 *
	 * @throws IOException
	 * @throws Exception
	 */
	public void loadConfigurationFile() throws IOException, Exception {

		File file = new File(path);

		if (!file.exists()) {
			file.createNewFile();
			resetConfiguration(file);
			return;
		}

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			int amount = 1;
			String line = br.readLine();
			String separator = "=";
			String nameVariable = null;
			String valueVariable = null;
			int firstSeparator = 0;
			while (amount <= Configuration.amount && line != null) {
				firstSeparator = line.indexOf(separator);
				nameVariable = line.substring(0, firstSeparator).trim().toLowerCase();
				valueVariable = line.substring(firstSeparator + 1).trim().toLowerCase();
				if (nameVariable.compareTo("fullscreen") == 0) {
					if (valueVariable.compareTo("false") == 0) {
						Configuration.fullscreen = false;
					} else if (valueVariable.compareTo("true") == 0) {
						Configuration.fullscreen = true;
					}
				} else if (nameVariable.compareTo("autosave") == 0) {
					if (valueVariable.compareTo("false") == 0) {
						Configuration.autoSave = false;
					} else if (valueVariable.compareTo("true") == 0) {
						Configuration.autoSave = true;
					}
				} else if (nameVariable.compareTo("visibilityhistoric") == 0) {
					if (valueVariable.compareTo("false") == 0) {
						Configuration.visibilityHistoric = false;
					} else if (valueVariable.compareTo("true") == 0) {
						Configuration.visibilityHistoric = true;
					}
				} else if (nameVariable.compareTo("visibilitytype") == 0) {
					if (valueVariable.compareTo("false") == 0) {
						Configuration.visibilityType = false;
					} else if (valueVariable.compareTo("true") == 0) {
						Configuration.visibilityType = true;
					}
				} else if (nameVariable.compareTo("resizable") == 0) {
					if (valueVariable.compareTo("false") == 0) {
						Configuration.resizable = false;
					} else if (valueVariable.compareTo("true") == 0) {
						Configuration.resizable = true;
					}
				}
				if (nameVariable.compareTo("telemetry") == 0) {
					if (valueVariable.compareTo("false") == 0) {
						Configuration.telemetry = false;
					} else if (valueVariable.compareTo("true") == 0) {
						Configuration.telemetry = true;
					}
				} else if (nameVariable.compareTo("automaticcheck") == 0) {
					if (valueVariable.compareTo("false") == 0) {
						Configuration.automaticCheck = false;
					} else if (valueVariable.compareTo("true") == 0) {
						Configuration.automaticCheck = true;
					}
				} else if (nameVariable.compareTo("lastdirectory") == 0) {
					Configuration.lastDirectory = valueVariable;
				} else if (nameVariable.compareTo("width") == 0 && isDouble(valueVariable)) {
					Configuration.width = Double.parseDouble(valueVariable);
				} else if (nameVariable.compareTo("height") == 0 && isDouble(valueVariable)) {
					Configuration.height = Double.parseDouble(valueVariable);
				} else if (nameVariable.compareTo("listwidth") == 0 && isDouble(valueVariable)) {
					Configuration.listWidth = Double.parseDouble(valueVariable);
				} else if (nameVariable.compareTo("GPUDevice") == 0) {
					Configuration.gpuDevice = Integer.parseInt(valueVariable);
				}
				line = br.readLine();
				amount++;
			}

		} catch (IOException ex) {
			throw new IOException(ex.getMessage());
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	private void resetConfiguration(File file) throws IOException, Exception {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			bw.write("fullscreen=false\r\n" + "autoSave=false\r\n" + "visibilityHistoric=true\r\n"
					+ "visibilityType=true\r\n" + "resizable=true\r\n" + "automaticCheck=false\r\n" + "width=1000.0\r\n"
					+ "height=600.0\r\n" + "listWidth=130.0\r\n" + "telemetry=false\r\n" + "GPUDevice=0\r\n"
					+ "lastDirectory=.");

		} catch (IOException ex) {
			throw new IOException(ex.getMessage());
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	public void updateConfiguration() throws IOException, Exception {
		File file = new File(path);
		file.delete();
		file.createNewFile();
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			bw.write("fullscreen=" + Configuration.fullscreen + "\r\n" + "autoSave=" + Configuration.autoSave + "\r\n"
					+ "visibilityHistoric=true\r\n" + "visibilityType=true\r\n" + "resizable=true\r\n"
					+ "automaticCheck=false\r\n" + "width=1000.0\r\n" + "height=600.0\r\n" + "listWidth=130.0\r\n"
					+ "telemetry=false\r\n" + "GPUDevice=0\r\n" + "lastDirectory=" + Configuration.lastDirectory);

		} catch (IOException ex) {
			throw new IOException(ex.getMessage());
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	private boolean isDouble(String valueVariable) {
		boolean result = true;
		try {
			Double.parseDouble(valueVariable);
		} catch (NumberFormatException ex) {
			result = false;
		}
		return result;
	}
}
