package provdominoes.command;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.la4j.matrix.sparse.CRSMatrix;

import model.DefaultMatrix;
import model.ProvMatrix;
import provdominoes.boundary.App;
import provdominoes.control.Controller;
import provdominoes.domain.Configuration;
import provdominoes.domain.Dominoes;
import provdominoes.util.Prov2DominoesUtil;

public class LoadMatricesCommand extends AbstractCommand {

	private String[] filePaths;
	private String id;
	private String dir;

	public LoadMatricesCommand(String[] filePaths) {
		super();
		this.filePaths = filePaths;
	}

	public LoadMatricesCommand(String[] filePaths, String dir) {
		this(filePaths);
		this.dir = dir;
	}

	public LoadMatricesCommand() {
		super();
		this.filePaths = new String[1];
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	protected boolean doIt() {
		boolean result = true;
		try {
			boolean setup = false;
			List<ProvMatrix> provMatrixList = new ArrayList<>();
			for (int i = 0; i < filePaths.length; i++) {
				File file = new File(filePaths[i]);
				if (!setup) {
					this.dir = file.getAbsolutePath().replace(file.getName(), "");
					App.getCommandManager().setDir(this.dir);
				}
				provMatrixList.addAll(importMatrixFile(file));
			}
			List<Dominoes> dominoesList = Prov2DominoesUtil.convert(provMatrixList, null, null);
			App.getPieceSelectorList().clear();
			App.getMovementCanvas().clear();
			App.getTabbedMatrixGraphPane().clear();
			Controller.resultLoadMatrices = dominoesList;
			updateTitle();
			App.getPieceSelectorList().configure(Controller.resultLoadMatrices);
		} catch (IOException e) {
			result = false;
			App.alertException(e, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
			App.alertException(e, e.getMessage());
		}
		return result;
	}

	public List<ProvMatrix> importMatrixFile(File file) {
		List<ProvMatrix> matrices = new ArrayList<ProvMatrix>();
		try {
			if (file != null) {
				DefaultMatrix matrix = null;
				List<String[]> rows = new ArrayList<>();
				boolean start = false;
				FileReader fr = new FileReader(file);
				BufferedReader br = new BufferedReader(fr);
				String line = br.readLine();
				if (line != null) {
					line = line.replace("(", "[");
					line = line.replace(")", "]");
				}
				while (line != null) {
					if (line.contains("MATRIX") || line.contains(Configuration.DATA_SEPARATOR)) {
						if (line.equals("END MATRIX")) {
							if (matrix != null) {
								int z = 0;
								matrix.setMatrix(new CRSMatrix(matrix.getRowDescriptors().size(),
										matrix.getColumnDescriptors().size()));
								for (String[] row : rows) {
									matrix.addRowValues(z, row);
									z++;
								}
								matrices.add(matrix);
							}
						}
						if (line.equals("BEGIN MATRIX")) {
							start = true;
							matrix = new DefaultMatrix();
							rows = new ArrayList<>();
							line = br.readLine();
							if (line != null) {
								line = line.replace("(", "[");
								line = line.replace(")", "]");
							}
						}
						if (line.contains(Configuration.DATA_SEPARATOR)) {
							String[] row = line.split(Configuration.DATA_SEPARATOR);
							if (!start) {
								if (line.contains(Configuration.DATA_SEPARATOR)) {
									matrix.addRowDescriptor(row[0]);
									rows.add(row);
								}
							} else {
								matrix.setIdentifier(row[0].replace(" ", ""));
								String token = row[0].split("\\[")[1].replace("]", "");
								if (token.contains("/")) {
									matrix.setRowDimentionAbbreviate(token.split("/")[0].replace(" ", ""));
									matrix.setColumnDimentionAbbreviate(token.split("/")[1].replace(" ", ""));
								}
								List<String> list = new ArrayList<String>(Arrays.asList(row));
								list.remove(0);
								matrix.setColumnDescriptors(list);
								start = false;
							}
						}
					}
					line = br.readLine();
					if (line != null) {
						line = line.replace("(", "[");
						line = line.replace(")", "]");
					}
				}

				br.close();
				fr.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			App.alertException(e, "Error trying to import matrices!");
		}
		return matrices;
	}

	private void updateTitle() {
		if (filePaths != null && filePaths.length > 0) {
			String defaultTitle = "Prov-Dominoes ["
					+ (Configuration.isGPUProcessing() ? Configuration.GPU_PROCESSING : Configuration.CPU_PROCESSING) + "]";
			if (filePaths.length == 1) {
				App.getStage().setTitle(defaultTitle + " - " + filePaths[0]);
			} else {
				App.getStage().setTitle(defaultTitle + " - " + filePaths[0] + " ...");
			}
		}
	}

	@Override
	protected boolean undoIt() {
		boolean result = true;
		App.getPieceSelectorList().clear();
		App.getMovementCanvas().clear();
		App.getTabbedMatrixGraphPane().clear();
		App.getArea().clear();
		return result;
	}

	@Override
	public String getName() {
		String files = "";
		for (int i = 0; i < filePaths.length; i++) {
			files += filePaths[i] + ",";
		}
		if (filePaths.length > 0) {
			files = files.substring(0, files.length() - 1);
		}
		return LOAD_MATRIX_COMMAND + "(\"" + files + "\")";
	}

}