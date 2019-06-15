package boundary;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;

import command.CommandFactory;
import domain.Configuration;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.ToggleGroup;

public class DominoesMenuBar extends MenuBar {

	//------DOMINOES MENU ITENS-----------------------------------------------------
	private final Menu mainMenu;
	private final Menu mainMenuSave;

	private final CheckMenuItem mainMenuSaveAutoSave;
	private final SeparatorMenuItem mainMenuSeparator;
	private final MenuItem mainMenuNew;
	private final MenuItem mainMenuOpenProv;
	private final MenuItem mainMenuExportScript;
	private final MenuItem mainMenuImportScript;
	private final MenuItem mainMenuLoadAll;
	private final MenuItem mainMenuExit;
	private final MenuItem mainMenuExitAndSave;
	private final MenuItem mainMenuSaveSaveAll;
	private final MenuItem editMenuUndo;
	private final MenuItem editMenuRedo;
	private final MenuItem editMenuLimpar;

	//------EDIT MENU ITENS---------------------------------------------------------
	private final Menu editMenu;

	private final CheckMenuItem editMenuShowHistoric;
	private final CheckMenuItem editMenuShowType;

	//------COFIGURATION MENU ITENS-------------------------------------------------
	private final Menu mConfiguration;
	private final CheckMenuItem mConfiguration_fullScreen;
	private final Menu mConfiguration_database;
	private final RadioMenuItem mConfiguration_database_accessTXT;
	private final RadioMenuItem mConfiguration_database_accessSQL;
	private final ToggleGroup mConfiguration_database_accessGroup;

	private final SeparatorMenuItem mConfiguration_separator;

	//------TIME MENU ITENS----------------------------------------------------
	private final Menu mTimeline;
	private final CheckMenuItem mTimeline_ShowTimeline;


	public DominoesMenuBar() {
		this.setHeight(30);
		//------DOMINOES MENU ITENS-----------------------------------------------------
		this.mainMenu = new Menu("Dominoes");

		this.mainMenuNew = new MenuItem("New");
		this.mainMenuOpenProv = new MenuItem("Open Prov-N...");
		this.mainMenuExportScript = new MenuItem("Export to script...");
		this.mainMenuImportScript = new MenuItem("Import from script...");
		this.mainMenuLoadAll = new MenuItem("Load All");
		this.mainMenuLoadAll.setDisable(true);
		this.mainMenuSave = new Menu("Save");
		this.mainMenuSaveSaveAll = new MenuItem("Save All");
		this.mainMenuSaveAutoSave = new CheckMenuItem("Auto Save");
		this.mainMenuSaveAutoSave.setSelected(Configuration.autoSave);
		this.mainMenuExit = new MenuItem("Exit");
		this.mainMenuExitAndSave = new MenuItem("Exit And Save");

		this.mainMenuSave.getItems().addAll(this.mainMenuSaveSaveAll, this.mainMenuSaveAutoSave);

		this.mainMenuSeparator = new SeparatorMenuItem();

		this.mainMenu.getItems().addAll(this.mainMenuNew, this.mainMenuOpenProv, this.mainMenuExportScript, this.mainMenuImportScript,this.mainMenuLoadAll, this.mainMenuSave,
				this.mainMenuSeparator, mainMenuExitAndSave, this.mainMenuExit);

		//------EDIT MENU ITENS---------------------------------------------------------
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

		this.editMenu.getItems().addAll(this.editMenuUndo, this.editMenuRedo, this.editMenuLimpar,this.editMenuShowHistoric, this.editMenuShowType);
		//        this.mEdit.getItems().addAll(this.mEdit_editMatrix, this.mEdit_showHistoric, this.mEdit_showType);

		//------CONFIGURATION MENU ITENS------------------------------------------------
		this.mConfiguration = new Menu("Configuration");

		this.mConfiguration_fullScreen = new CheckMenuItem("Full Screen");
		this.mConfiguration_fullScreen.setSelected(Configuration.fullscreen);
		this.mConfiguration_database = new Menu("Access Mode");
		this.mConfiguration_database_accessGroup = new ToggleGroup();
		this.mConfiguration_database_accessTXT = new RadioMenuItem("TXT Access");
		this.mConfiguration_database_accessSQL = new RadioMenuItem("SQL Access");
		this.mConfiguration_database_accessTXT.setToggleGroup(mConfiguration_database_accessGroup);
		this.mConfiguration_database_accessSQL.setToggleGroup(mConfiguration_database_accessGroup);
		this.mConfiguration_database_accessTXT.setSelected(true);

		this.mConfiguration_separator = new SeparatorMenuItem();

		this.mConfiguration_database.getItems().addAll(this.mConfiguration_database_accessTXT,
				this.mConfiguration_database_accessSQL);

		this.mConfiguration.getItems().addAll(this.mConfiguration_database, this.mConfiguration_separator,
				this.mConfiguration_fullScreen);

		//------TIME MENU ITENS----------------------------------------------------
		this.mTimeline_ShowTimeline = new CheckMenuItem("View Time");
		this.mTimeline_ShowTimeline.setSelected(Configuration.visibilityTimePane);
		this.mTimeline = new Menu("Time");
		this.mTimeline.getItems().addAll(mTimeline_ShowTimeline);

		//------MENU ITENS--------------------------------------------------------------
		this.getMenus().addAll(this.mainMenu, this.editMenu, this.mConfiguration, mTimeline);
		//        this.getMenus().addAll(this.mainMenu, this.mEdit, this.mConfiguration);

		if (!Configuration.automaticCheck || Configuration.endDate.compareTo(Configuration.beginDate) <= 0) {
			this.changeEnableDisble();
		}
		//------ADD LISTENERS-----------------------------------------------------------
		//----------DOMINOES MENU ITENS-------------------------------------------------
		this.mainMenuNew.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				try {
					App.clear();
				} catch (IOException e) {
					System.out.println("Erro ao tentar acessar arquivo de script!");
					e.printStackTrace();
				}
				changeEnableDisble();

			}
		});
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
		this.mainMenuLoadAll.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				App.load(Configuration.beginDate, Configuration.endDate);
			}
		});

		this.mainMenuSaveSaveAll.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					App.saveAll();
				} catch (IOException ex) {
					System.err.println(ex.getMessage());
				}
			}
		});

		this.mainMenuSaveAutoSave.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				Configuration.autoSave = /*this.*/mainMenuSaveAutoSave.isSelected();

			}
		});

		this.mainMenuExitAndSave.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				try {
					App.saveAll();
					System.exit(0);
				} catch (IOException ex) {
					System.err.println(ex.getMessage());
				}
			}
		});

		this.mainMenuExit.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				System.exit(0);
			}
		});

		//----------EDIT MENU ITENS-----------------------------------------------------

		this.editMenuUndo.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				App.getCommandManager().invokeCommand(CommandFactory.getInstance().undo(),false);
			}
		});

		this.editMenuRedo.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				App.getCommandManager().invokeCommand(CommandFactory.getInstance().redo(),false);
			}
		});
		
		this.editMenuLimpar.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				App.getCommandManager().getRedoList().clear();
				App.getCommandManager().getHistory().clear();
				App.getCommandManager().uptadeMenu();
				App.getArea().clear();
				App.getTopPane().reset();
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

		//----------CONFIGURATION MENU ITENS------------------------------------------------
		this.mConfiguration_fullScreen.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				App.setFullscreen(mConfiguration_fullScreen.isSelected());
			}
		});
		//----------TIME PANE MENU ITENS----------------------------------------------------
		mTimeline_ShowTimeline.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

				App.changeVisibleTimePane();

			}
		});
	}

	public void changeEnableDisble() {

		this.mainMenuNew.setDisable(!this.mainMenuNew.isDisable());
		this.mainMenuLoadAll.setDisable(!this.mainMenuLoadAll.isDisable());
		this.mainMenuSave.setDisable(!this.mainMenuSave.isDisable());
		this.mainMenuExit.setDisable(!this.mainMenuExit.isDisable());
		this.mainMenuExitAndSave.setDisable(!this.mainMenuExitAndSave.isDisable());

		//        this.mEdit.setDisable(!this.mEdit.isDisable());

		//        this.mConfiguration.setDisable(!this.mConfiguration.isDisable());

		//        this.mTimeline.setDisable(false);

	}

	public void load(Date begin, Date end) throws ParseException {
		changeEnableDisble();
		Configuration.automaticCheck = true;
		//App.checkout(begin, end);
		App.LoadDominoesPieces();

	}

	public MenuItem getEditMenuUndo() {
		return editMenuUndo;
	}

	public MenuItem getEditMenuRedo() {
		return editMenuRedo;
	}

}
