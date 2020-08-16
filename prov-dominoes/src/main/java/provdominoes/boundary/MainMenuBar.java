package provdominoes.boundary;

import java.io.IOException;
import java.util.Optional;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.stage.Stage;
import processor.MatrixProcessor;
import provdominoes.command.CommandFactory;
import provdominoes.domain.Configuration;
import provdominoes.util.ConfigurationFile;

public class MainMenuBar extends MenuBar {

	// ------DOMINOES MENU
	// ITENS-----------------------------------------------------
	private final Menu mainMenu;

	private final SeparatorMenuItem mainMenuSeparator;
	private final MenuItem mainMenuOpenProv;
	private final MenuItem mainMenuExportScript;
	private final MenuItem mainMenuImportScript;
	private final MenuItem mainMenuImportMatrix;
	private final MenuItem mainMenuExit;
	private final MenuItem editMenuUndo;
	private final MenuItem editMenuRedo;
	private final MenuItem editMenuLimpar;

	// ------EDIT MENU
	// ITENS---------------------------------------------------------
	private final Menu editMenu;

	private final CheckMenuItem mViewShowHistoric;
	private final CheckMenuItem nViewShowType;

	// ------COFIGURATION MENU
	// ITENS-------------------------------------------------

	// ------VIEW MENU ITENS----------------------------------------------------
	private final Menu mView;
	private final CheckMenuItem mViewFullscreen;
	private final CheckMenuItem mViewShowCellValues;
	public final CheckMenuItem mViewShowGraph;

	// ------PROCESSING ITENS----------------------------------------------------
	private final Menu mProcessing;
	private final CheckMenuItem mDeriveInfluence;
	private final CheckMenuItem mCpuProcessing;
	private final CheckMenuItem mGpuProcessing;
	private CheckMenuItem[] devices;

	private CheckMenuItem mTuning;
	private CheckMenuItem mTelemetry;

	public MainMenuBar() {
		this.setHeight(30);
		// ------DOMINOES MENU
		// ITENS-----------------------------------------------------
		this.mainMenu = new Menu("Prov-Dominoes");

		this.mainMenuOpenProv = new MenuItem("Open Prov-N...");
		this.mainMenuExportScript = new MenuItem("Export to script...");
		this.mainMenuImportScript = new MenuItem("Import from script...");
		this.mainMenuImportMatrix = new MenuItem("Import matrices...");
		this.mainMenuExit = new MenuItem("Exit");

		this.mainMenuSeparator = new SeparatorMenuItem();

		this.mainMenu.getItems().addAll(this.mainMenuOpenProv, this.mainMenuExportScript, this.mainMenuImportScript,
				this.mainMenuImportMatrix, this.mainMenuSeparator, this.mainMenuExit);

		this.mProcessing = new Menu("Processing");
		this.mTelemetry = new CheckMenuItem("Telemetry");
		this.mTuning = new CheckMenuItem("Tuning");
		this.mDeriveInfluence = new CheckMenuItem("Derive influence");
		this.mCpuProcessing = new CheckMenuItem("CPU");
		this.mGpuProcessing = new CheckMenuItem("GPU");

		this.mTuning.setSelected(Configuration.tuning);
		mTuning.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (((CheckMenuItem) e.getSource()).isSelected()) {
					Configuration.tuning = true;
				} else {
					Configuration.tuning = false;
				}
				try {
					new ConfigurationFile().updateConfiguration();
				} catch (IOException ex) {
					ex.printStackTrace();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		this.mTelemetry.setSelected(Configuration.telemetry);
		mTelemetry.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				if (((CheckMenuItem) e.getSource()).isSelected()) {
					Configuration.telemetry = true;
				} else {
					Configuration.telemetry = false;
				}
				try {
					new ConfigurationFile().updateConfiguration();
				} catch (IOException ex) {
					ex.printStackTrace();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		this.mDeriveInfluence.setSelected(Configuration.deriveInfluence);
		mDeriveInfluence.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (Configuration.deriveInfluence) {
					App.getMenu().getmDeriveInfluence().setSelected(false);
					Configuration.deriveInfluence = false;
				} else {
					App.getMenu().getmDeriveInfluence().setSelected(true);
					Configuration.deriveInfluence = true;
				}
			}
		});
		int deviceCount = 0;
		if (!MatrixProcessor.isLibSkipped()) {
			deviceCount = MatrixProcessor.getDeviceCount();
		}
		if (deviceCount > 0) {
			SeparatorMenuItem influenceSeparator = new SeparatorMenuItem();
			SeparatorMenuItem deviceSeparator = new SeparatorMenuItem();
			this.mProcessing.getItems().addAll(this.mTelemetry, this.mTuning, this.mDeriveInfluence, influenceSeparator,
					this.mCpuProcessing, this.mGpuProcessing, deviceSeparator);
			this.devices = new CheckMenuItem[deviceCount];
			for (int i = 0; i < devices.length; i++) {
				this.devices[i] = new CheckMenuItem("Device " + i);
				this.devices[i].setSelected(i == Configuration.gpuDevice);
				this.devices[i].setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.setTitle("Are you sure?");
						alert.setHeaderText("ATTENTION: Update of the processing mode!");
						alert.setContentText(
								"The application will be restarted in another processing mode and you will lose the current "
										+ "state of the application. OK to continue?");

						Optional<ButtonType> result = alert.showAndWait();
						if (result.get() == ButtonType.OK) {
							CheckMenuItem target = (CheckMenuItem) event.getTarget();
							for (int j = 0; j < devices.length; j++) {
								if (devices[j].equals(target)) {
									Configuration.defaultProcessing = Configuration.GPU_PROCESSING;
									Configuration.gpuDevice = j;
									try {
										new ConfigurationFile().updateConfiguration();
									} catch (IOException e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
								}
							}

							System.out.println("Restarting in GPU Mode (Device = " + Configuration.gpuDevice + ")...");
							App.getStage().close();
							Platform.runLater(() -> {
								try {
									new App().start(new Stage());
								} catch (Exception e) {
									System.err.println("Falha ao reiniciar Prov-Dominoes!");
									e.printStackTrace();
								}
							});
						} else {
							CheckMenuItem t = (CheckMenuItem) event.getTarget();
							if (mCpuProcessing.isSelected()) {
								t.setSelected(false);
							} else {
								t.setSelected(true);
							}
						}
					}
				});
				this.mProcessing.getItems().add(this.devices[i]);
				mGpuProcessing.setSelected(Configuration.defaultProcessing.equals(Configuration.GPU_PROCESSING));
				mGpuProcessing.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.setTitle("Are you sure?");
						alert.setHeaderText("ATTENTION: Update of the processing mode!");
						alert.setContentText(
								"The application will be restarted in another processing mode and you will lose the current state of the application. OK to continue?");

						Optional<ButtonType> result = alert.showAndWait();
						if (result.get() == ButtonType.OK) {
							try {
								Configuration.defaultProcessing = Configuration.GPU_PROCESSING;
								Configuration.gpuDevice = 0;
								new ConfigurationFile().updateConfiguration();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.out.println("Restarting in GPU Mode (Device = 0)...");
							App.getStage().close();
							Platform.runLater(() -> {
								try {
									new App().start(new Stage());
								} catch (Exception e) {
									System.err.println("Falha ao reiniciar Prov-Dominoes!");
									e.printStackTrace();
								}
							});
						} else {
							CheckMenuItem t = (CheckMenuItem) event.getTarget();
							if (mCpuProcessing.isSelected()) {
								t.setSelected(false);
							} else {
								t.setSelected(true);
							}
						}
					}
				});

				mCpuProcessing.setSelected(Configuration.defaultProcessing.equals(Configuration.CPU_PROCESSING));
				if (Configuration.defaultProcessing.equals(Configuration.CPU_PROCESSING)) {
					for (int k = 0; k < devices.length; k++) {
						devices[k].setSelected(false);
						devices[k].setDisable(true);
					}
				}
				mCpuProcessing.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						Alert alert = new Alert(AlertType.CONFIRMATION);
						alert.setTitle("Are you sure?");
						alert.setHeaderText("ATTENTION: Update of the processing mode!");
						alert.setContentText(
								"The application will be restarted in another processing mode and you will lose the current state of the application. OK to continue?");

						Optional<ButtonType> result = alert.showAndWait();
						if (result.get() == ButtonType.OK) {
							Configuration.defaultProcessing = Configuration.CPU_PROCESSING;
							try {
								new ConfigurationFile().updateConfiguration();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							System.out.println("Restarting in CPU Mode...");
							App.getStage().close();
							Platform.runLater(() -> {
								try {
									new App().start(new Stage());
								} catch (Exception e) {
									System.err.println("Falha ao reiniciar Prov-Dominoes!");
									e.printStackTrace();
								}
							});
						} else {
							CheckMenuItem t = (CheckMenuItem) event.getTarget();
							if (mGpuProcessing.isSelected()) {
								t.setSelected(false);
							} else {
								t.setSelected(true);
							}
						}
					}
				});
			}
		} else {
			this.mProcessing.getItems().addAll(this.mDeriveInfluence);
		}

		// ------EDIT MENU
		// ITENS---------------------------------------------------------
		this.editMenu = new Menu("Edit");

		this.editMenuUndo = new MenuItem("Undo");
		this.editMenuUndo.setDisable(true);
		this.editMenuRedo = new MenuItem("Redo");
		this.editMenuLimpar = new MenuItem("Clear");
		this.editMenuRedo.setDisable(true);

		this.editMenu.getItems().addAll(this.editMenuUndo, this.editMenuRedo, this.editMenuLimpar);

		// ------VIEW MENU ITENS----------------------------------------------------
		this.mViewShowGraph = new CheckMenuItem("View History Graph");
		this.mViewShowGraph.setSelected(Configuration.visibilityGraphHistory);
		this.mViewFullscreen = new CheckMenuItem("Full Screen");
		this.mViewFullscreen.setSelected(Configuration.fullScreen);

		this.mViewShowCellValues = new CheckMenuItem("Show Cell Values");
		this.mViewShowCellValues.setSelected(Configuration.showCellValues);
		this.mViewShowHistoric = new CheckMenuItem("Show Historic");
		this.mViewShowHistoric.setSelected(Configuration.visibilityHistoric);
		this.nViewShowType = new CheckMenuItem("Show Type");
		this.nViewShowType.setSelected(Configuration.visibilityType);

		this.mView = new Menu("View");
		this.mView.getItems().addAll(mViewFullscreen, mViewShowGraph, mViewShowCellValues,this.mViewShowHistoric, this.nViewShowType);

		// ------MENU
		// ITENS--------------------------------------------------------------
		if (deviceCount > 0) {
			this.getMenus().addAll(this.mainMenu, this.editMenu, mProcessing, mView);
		} else {
			this.getMenus().addAll(this.mainMenu, this.editMenu, mView);
		}

		// ------ADD
		// LISTENERS-----------------------------------------------------------
		// ----------DOMINOES MENU
		// ITENS-------------------------------------------------
		this.mainMenuOpenProv.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				App.openProv();
			}
		});
		this.mainMenuExportScript.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				App.exportScript();
			}
		});
		this.mainMenuImportScript.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				App.importScript();
			}
		});
		this.mainMenuImportMatrix.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				App.importMatrices();
			}
		});
		this.mainMenuExit.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				App.executeExit();
			}
		});

		// ----------EDIT MENU
		// ITENS-----------------------------------------------------

		this.editMenuUndo.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				App.getCommandManager().invokeCommand(CommandFactory.getInstance().undo(), false, false);
			}
		});

		this.editMenuRedo.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				App.getCommandManager().invokeCommand(CommandFactory.getInstance().redo(), false, false);
			}
		});

		this.editMenuLimpar.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				App.getCommandManager().getRedoList().clear();
				App.getCommandManager().getHistory().clear();
				App.getCommandManager().uptadeMenu();
				try {
					App.getCommandManager().clear(true);
				} catch (IOException e) {
					App.alertException(e, "Error trying to access EPS file");
				}
				App.getArea().clear();
				App.getTopPane().reset();
				;
			}
		});

		this.mViewShowHistoric.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				if (((CheckMenuItem) e.getSource()).isSelected()) {
					Configuration.visibilityHistoric = true;
					App.setVisibleHistoric();
				} else {
					Configuration.visibilityHistoric = false;
					App.setVisibleHistoric();
				}
			}
		});

		this.nViewShowType.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				if (((CheckMenuItem) e.getSource()).isSelected()) {
					Configuration.visibilityType = true;
					App.setVisibleType();
				} else {
					Configuration.visibilityType = false;
					App.setVisibleType();
				}
			}
		});

		// ----------CONFIGURATION MENU
		// ITENS------------------------------------------------
		this.mViewFullscreen.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {				
				if (((CheckMenuItem) e.getSource()).isSelected()) {
					Configuration.fullScreen = true;
					App.setSize(true);
				} else {
					Configuration.fullScreen = false;
					App.setSize(false);
				}
			}
		});
		this.mViewShowCellValues.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				if (((CheckMenuItem) e.getSource()).isSelected()) {
					Configuration.showCellValues = true;
				} else {
					Configuration.showCellValues = false;
				}

			}
		});
		mViewShowGraph.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent e) {
				if (((CheckMenuItem) e.getSource()).isSelected()) {					
					Configuration.visibilityGraphHistory = true;
				} else {
					Configuration.visibilityGraphHistory = false;
				}
				App.changeVisibleGraphHistory();
			}
		});
	}

	public MenuItem getEditMenuUndo() {
		return editMenuUndo;
	}

	public MenuItem getEditMenuRedo() {
		return editMenuRedo;
	}

	public CheckMenuItem[] getDevices() {
		return devices;
	}

	public void setDevices(CheckMenuItem[] devices) {
		this.devices = devices;
	}

	public CheckMenuItem getmCpuProcessing() {
		return mCpuProcessing;
	}

	public CheckMenuItem getmGpuProcessing() {
		return mGpuProcessing;
	}

	public CheckMenuItem getmDeriveInfluence() {
		return mDeriveInfluence;
	}

	public CheckMenuItem getmViewShowGraph() {
		return mViewShowGraph;
	}

	public CheckMenuItem getmTuning() {
		return mTuning;
	}

	public void setmTuning(CheckMenuItem mTuning) {
		this.mTuning = mTuning;
	}

}
