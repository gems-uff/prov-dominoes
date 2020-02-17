package provdominoes.boundary;

import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import processor.MatrixProcessor;
import provdominoes.arch.Session;
import provdominoes.command.CommandFactory;
import provdominoes.domain.Configuration;

public class ProvDominoesMenuBar extends MenuBar {

	// ------DOMINOES MENU
	// ITENS-----------------------------------------------------
	private final Menu mainMenu;

	private final SeparatorMenuItem mainMenuSeparator;
	private final MenuItem mainMenuOpenProv;
	private final MenuItem mainMenuExportScript;
	private final MenuItem mainMenuImportScript;
	private final MenuItem mainMenuExit;
	private final MenuItem editMenuUndo;
	private final MenuItem editMenuRedo;
	private final MenuItem editMenuLimpar;

	// ------EDIT MENU
	// ITENS---------------------------------------------------------
	private final Menu editMenu;

	private final CheckMenuItem editMenuShowHistoric;
	private final CheckMenuItem editMenuShowType;

	// ------COFIGURATION MENU
	// ITENS-------------------------------------------------

	// ------VIEW MENU ITENS----------------------------------------------------
	private final Menu mView;
	private final CheckMenuItem mHistoryFullscreen;
	public final CheckMenuItem mHistoryShowGraph;

	// ------FACTORY MENU ITENS----------------------------------------------------
	private final Menu mFactory;
	private final CheckMenuItem mDefaultFactory;
	private final CheckMenuItem mExtendedFactory;

	// ------PROCESSING ITENS----------------------------------------------------
	private final Menu mProcessing;
	private final CheckMenuItem mDeriveInfluence;
	private final CheckMenuItem mCpuProcessing;
	private final CheckMenuItem mGpuProcessing;
	private CheckMenuItem[] devices;

	public ProvDominoesMenuBar() {
		this.setHeight(30);
		// ------DOMINOES MENU
		// ITENS-----------------------------------------------------
		this.mainMenu = new Menu("Prov-Dominoes");

		this.mainMenuOpenProv = new MenuItem("Open Prov-N...");
		this.mainMenuExportScript = new MenuItem("Export to script...");
		this.mainMenuImportScript = new MenuItem("Import from script...");
		this.mainMenuExit = new MenuItem("Exit");

		this.mainMenuSeparator = new SeparatorMenuItem();

		this.mainMenu.getItems().addAll(this.mainMenuOpenProv, this.mainMenuExportScript, this.mainMenuImportScript,
				this.mainMenuSeparator, this.mainMenuExit);

		this.mFactory = new Menu("Factory");
		this.mDefaultFactory = new CheckMenuItem("Default");
		this.mDefaultFactory.setSelected(Configuration.defaultFactory);
		this.mExtendedFactory = new CheckMenuItem("Extended");
		this.mExtendedFactory.setSelected(!Configuration.defaultFactory);
		this.mFactory.getItems().addAll(this.mDefaultFactory, this.mExtendedFactory);

		this.mProcessing = new Menu("Processing");
		this.mDeriveInfluence = new CheckMenuItem("Derive influence");
		this.mCpuProcessing = new CheckMenuItem("CPU");
		this.mGpuProcessing = new CheckMenuItem("GPU");

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
			this.mProcessing.getItems().addAll(this.mDeriveInfluence, influenceSeparator, this.mCpuProcessing,
					this.mGpuProcessing, deviceSeparator);
			this.devices = new CheckMenuItem[deviceCount];
			for (int i = 0; i < devices.length; i++) {
				this.devices[i] = new CheckMenuItem("Device " + i);
				this.devices[i].setSelected(i == Configuration.gpuDevice);
				this.devices[i].setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						CheckMenuItem target = (CheckMenuItem) event.getTarget();
						CheckMenuItem[] devices = App.getMenu().getDevices();
						for (int j = 0; j < devices.length; j++) {
							if (devices[j].equals(target)) {
								devices[j].setSelected(true);
								Configuration.defaultProcessing = Configuration.GPU_DEVICE;
								Configuration.gpuDevice = j;
								Session.closeSection();
								Session.startSession(Configuration.gpuDevice);
								MatrixProcessor.resetGPU(j);
								App.getStage().setTitle(
										"Prov-Dominoes [" + (Configuration.isGPUProcessing() ? Configuration.GPU_DEVICE
												: Configuration.CPU_DEVICE) + "]");
							} else {
								devices[j].setSelected(false);
							}
						}
					}
				});
				this.mProcessing.getItems().add(this.devices[i]);
				mGpuProcessing.setSelected(Configuration.defaultProcessing.equals(Configuration.GPU_DEVICE));
				mGpuProcessing.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						Configuration.defaultProcessing = Configuration.GPU_DEVICE;
						Session.closeSection();
						Session.startSession(Configuration.gpuDevice);
						App.getStage().setTitle(
								"Prov-Dominoes [" + (Configuration.isGPUProcessing() ? Configuration.GPU_DEVICE
										: Configuration.CPU_DEVICE) + "]");
						App.getMenu().getmGpuProcessing().setSelected(true);
						App.getMenu().getmCpuProcessing().setSelected(false);
						CheckMenuItem[] devices = App.getMenu().getDevices();
						for (int i = 0; i < devices.length; i++) {
							devices[i].setSelected(i == Configuration.gpuDevice);
							devices[i].setDisable(false);
						}
					}
				});

				mCpuProcessing.setSelected(Configuration.defaultProcessing.equals(Configuration.CPU_DEVICE));
				mCpuProcessing.setOnAction(new EventHandler<ActionEvent>() {
					@Override
					public void handle(ActionEvent event) {
						Configuration.defaultProcessing = Configuration.CPU_DEVICE;
						Session.closeSection();
						App.getStage().setTitle(
								"Prov-Dominoes [" + (Configuration.isGPUProcessing() ? Configuration.GPU_DEVICE
										: Configuration.CPU_DEVICE) + "]");
						App.getMenu().getmCpuProcessing().setSelected(true);
						App.getMenu().getmGpuProcessing().setSelected(false);
						CheckMenuItem[] devices = App.getMenu().getDevices();
						for (int i = 0; i < devices.length; i++) {
							devices[i].setSelected(false);
							devices[i].setDisable(true);
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

		this.editMenuShowHistoric = new CheckMenuItem("Show Historic");
		this.editMenuShowHistoric.setSelected(Configuration.visibilityHistoric);

		editMenuShowType = new CheckMenuItem("Show Type");
		editMenuShowType.setSelected(Configuration.visibilityType);

		this.editMenu.getItems().addAll(this.editMenuUndo, this.editMenuRedo, this.editMenuLimpar,
				this.editMenuShowHistoric, this.editMenuShowType);

		// ------VIEW MENU ITENS----------------------------------------------------
		this.mHistoryShowGraph = new CheckMenuItem("View History Graph");
		this.mHistoryShowGraph.setSelected(Configuration.visibilityGraphHistory);
		this.mHistoryFullscreen = new CheckMenuItem("Full Screen");
		this.mHistoryFullscreen.setSelected(Configuration.fullScreen);
		this.mView = new Menu("View");
		this.mView.getItems().addAll(mHistoryFullscreen, mHistoryShowGraph);

		// ------MENU
		// ITENS--------------------------------------------------------------
		if (deviceCount > 0) {
			this.getMenus().addAll(this.mainMenu, this.editMenu, mProcessing, mFactory, mView);
		} else {
			this.getMenus().addAll(this.mainMenu, this.editMenu, mFactory, mView);
		}
		// this.getMenus().addAll(this.mainMenu, this.mEdit, this.mConfiguration);

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
				App.getCommandManager().invokeCommand(CommandFactory.getInstance().undo(), false);
			}
		});

		this.editMenuRedo.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				App.getCommandManager().invokeCommand(CommandFactory.getInstance().redo(), false);
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
					App.alertException(e, "Erro ao acessar script de comandos");
				}
				App.getArea().clear();
				App.getTopPane().reset();
				;
			}
		});

		this.editMenuShowHistoric.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Configuration.visibilityHistoric = editMenuShowHistoric.isSelected();
				App.setVisibleHistoric();
			}
		});

		this.editMenuShowType.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Configuration.visibilityType = editMenuShowType.isSelected();
				App.setVisibleType();
			}
		});

		// ----------CONFIGURATION MENU
		// ITENS------------------------------------------------
		this.mHistoryFullscreen.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				App.setSize(mHistoryFullscreen.isSelected());
			}
		});
		mHistoryShowGraph.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				App.changeVisibleGraphHistory();
			}
		});
		// ----------FACTORY MENU
		// ITENS------------------------------------------------
		this.mDefaultFactory.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				App.getMenu().getmExtendedFactory().setSelected(false);
				Configuration.defaultFactory = true;
			}
		});
		this.mExtendedFactory.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				App.getMenu().getmDefaultFactory().setSelected(false);
				Configuration.defaultFactory = false;
			}
		});
	}

	public MenuItem getEditMenuUndo() {
		return editMenuUndo;
	}

	public MenuItem getEditMenuRedo() {
		return editMenuRedo;
	}

	public CheckMenuItem getmDefaultFactory() {
		return mDefaultFactory;
	}

	public CheckMenuItem getmExtendedFactory() {
		return mExtendedFactory;
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

	public CheckMenuItem getmHistoryShowGraph() {
		return mHistoryShowGraph;
	}

}
