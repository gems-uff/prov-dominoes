package provdominoes.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import provdominoes.domain.Configuration;

public class ConfigurationFile {

	private String path = "configuration.properties";

	public void loadConfigurationFile() throws IOException, Exception {

		File file = new File(path);

		if (!file.exists()) {
			file.createNewFile();
			resetConfiguration(file);
			return;
		}

		try (BufferedReader br = new BufferedReader(new FileReader(file))) {
			String line = br.readLine();
			String separator = "=";
			String nameVariable = null;
			String valueVariable = null;
			int firstSeparator = 0;
			while (line != null) {
				firstSeparator = line.indexOf(separator);
				nameVariable = line.substring(0, firstSeparator).trim();
				valueVariable = line.substring(firstSeparator + 1).trim();
				if (nameVariable.compareTo("fullScreen") == 0) {
					if (valueVariable.compareTo("false") == 0) {
						Configuration.fullScreen = false;
					} else if (valueVariable.compareTo("true") == 0) {
						Configuration.fullScreen = true;
					}
				}
				if (nameVariable.compareTo("graphHistory") == 0) {
					if (valueVariable.compareTo("false") == 0) {
						Configuration.visibilityGraphHistory = false;
					} else if (valueVariable.compareTo("true") == 0) {
						Configuration.visibilityGraphHistory = true;
					}
				}
				if (nameVariable.compareTo("autoSave") == 0) {
					if (valueVariable.compareTo("false") == 0) {
						Configuration.autoSave = false;
					} else if (valueVariable.compareTo("true") == 0) {
						Configuration.autoSave = true;
					}
				}
				if (nameVariable.compareTo("showCellValues") == 0) {
					if (valueVariable.compareTo("false") == 0) {
						Configuration.showCellValues = false;
					} else if (valueVariable.compareTo("true") == 0) {
						Configuration.showCellValues = true;
					}
				}
				if (nameVariable.compareTo("visibilityHistoric") == 0) {
					if (valueVariable.compareTo("false") == 0) {
						Configuration.visibilityHistoric = false;
					} else if (valueVariable.compareTo("true") == 0) {
						Configuration.visibilityHistoric = true;
					}
				}
				if (nameVariable.compareTo("visibilityType") == 0) {
					if (valueVariable.compareTo("false") == 0) {
						Configuration.visibilityType = false;
					} else if (valueVariable.compareTo("true") == 0) {
						Configuration.visibilityType = true;
					}
				}
				if (nameVariable.compareTo("resizable") == 0) {
					if (valueVariable.compareTo("false") == 0) {
						Configuration.resizable = false;
					} else if (valueVariable.compareTo("true") == 0) {
						Configuration.resizable = true;
					}
				}
				if (nameVariable.compareTo("defaultFactory") == 0) {
					if (valueVariable.compareTo("false") == 0) {
						Configuration.defaultFactory = false;
					} else if (valueVariable.compareTo("true") == 0) {
						Configuration.defaultFactory = true;
					}
				}
				if (nameVariable.compareTo("telemetry") == 0) {
					if (valueVariable.compareTo("false") == 0) {
						Configuration.telemetry = false;
					} else if (valueVariable.compareTo("true") == 0) {
						Configuration.telemetry = true;
					}
				}
				if (nameVariable.compareTo("automaticCheck") == 0) {
					if (valueVariable.compareTo("false") == 0) {
						Configuration.automaticCheck = false;
					} else if (valueVariable.compareTo("true") == 0) {
						Configuration.automaticCheck = true;
					}
				}
				if (nameVariable.compareTo("deriveInfluence") == 0) {
					if (valueVariable.compareTo("false") == 0) {
						Configuration.deriveInfluence = false;
					} else if (valueVariable.compareTo("true") == 0) {
						Configuration.deriveInfluence = true;
					}
				}
				if (nameVariable.compareTo("tuning") == 0) {
					if (valueVariable.compareTo("false") == 0) {
						Configuration.tuning = false;
					} else if (valueVariable.compareTo("true") == 0) {
						Configuration.tuning = true;
					}
				}
				if (nameVariable.compareTo("width") == 0 && isDouble(valueVariable)) {
					Configuration.width = Double.parseDouble(valueVariable);
				}
				if (nameVariable.compareTo("height") == 0 && isDouble(valueVariable)) {
					Configuration.height = Double.parseDouble(valueVariable);
				}
				if (nameVariable.compareTo("listWidth") == 0 && isDouble(valueVariable)) {
					Configuration.listWidth = Double.parseDouble(valueVariable);
				}
				if (nameVariable.compareTo("defaultProcessing") == 0) {
					Configuration.defaultProcessing = valueVariable.toUpperCase();
				}
				if (nameVariable.compareTo("gpuDevice") == 0) {
					Configuration.gpuDevice = Integer.parseInt(valueVariable);
				}
				if (nameVariable.compareTo("lastDirectory") == 0) {
					Configuration.lastDirectory = valueVariable;
				}
				if (nameVariable.compareTo("autoOpen") == 0) {
					Configuration.autoOpen = valueVariable;
				}
				line = br.readLine();
			}

		} catch (IOException ex) {
			throw new IOException(ex.getMessage());
		} catch (Exception ex) {
			throw new Exception(ex.getMessage());
		}
	}

	private void resetConfiguration(File file) throws IOException, Exception {
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			bw.write("fullScreen=false\r\n" + "graphHistory=false\r\n" + "autoSave=false\r\n"
					+ "showCellValues=false\r\n" + "visibilityHistoric=true\r\n" + "visibilityType=true\r\n"
					+ "resizable=true\r\n" + "automaticCheck=false\r\n" + "deriveInfluence=false\r\n"
					+ "tuning=false\r\n" + "width=1000.0\r\n" + "height=600.0\r\n" + "listWidth=128.0\r\n"
					+ "telemetry=false\r\n" + "defaultFactory=true\r\n" + "defaultProcessing=CPU\r\n"
					+ "gpuDevice=0\r\n" + "lastDirectory=samples\\\r\n" + "autoOpen=");

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
			bw.write("fullScreen=" + Configuration.fullScreen + "\r\n" + "graphHistory="
					+ Configuration.visibilityGraphHistory + "\r\n" + "autoSave=" + Configuration.autoSave + "\r\n"
					+ "showCellValues=" + Configuration.showCellValues + "\r\n" + "visibilityHistoric="
					+ Configuration.visibilityHistoric + "\r\n" + "visibilityType=" + Configuration.visibilityType
					+ "\r\n" + "resizable=" + Configuration.resizable + "\r\n" + "automaticCheck="
					+ Configuration.automaticCheck + "\r\n" + "deriveInfluence=" + Configuration.deriveInfluence
					+ "\r\n" + "tuning=" + Configuration.tuning + "\r\n" + "width=" + Configuration.width + "\r\n"
					+ "height=" + Configuration.height + "\r\n" + "listWidth=" + Configuration.listWidth + "\r\n"
					+ "telemetry=" + Configuration.telemetry + "\r\n" + "defaultFactory=" + Configuration.defaultFactory
					+ "\r\n" + "defaultProcessing=" + Configuration.defaultProcessing + "\r\n" + "gpuDevice="
					+ Configuration.gpuDevice + "\r\n" + "lastDirectory=" + Configuration.lastDirectory + "\r\n"
					+ "autoOpen=" + Configuration.autoOpen);

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
