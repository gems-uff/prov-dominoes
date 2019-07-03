package boundary;

import java.io.IOException;

import command.CommandFactory;
import domain.Configuration;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class DominoesMenuBar extends MenuBar {

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

	// ------TIME MENU ITENS----------------------------------------------------
	private final Menu mView;
	private final CheckMenuItem mHistoryFullscreen;
	private final CheckMenuItem mHistoryShowGraph;

	public DominoesMenuBar() {
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
		this.mHistoryShowGraph.setSelected(Configuration.visibilityTimePane);
		this.mHistoryFullscreen = new CheckMenuItem("Full Screen");
		this.mHistoryFullscreen.setSelected(Configuration.fullscreen);
		this.mView = new Menu("View");
		this.mView.getItems().addAll(mHistoryFullscreen, mHistoryShowGraph);

		// ------MENU
		// ITENS--------------------------------------------------------------
		this.getMenus().addAll(this.mainMenu, this.editMenu, mView);
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
					System.out.println("Erro ao acessar script de comandos");
					e.printStackTrace();
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
				App.setFullscreen(mHistoryFullscreen.isSelected());
			}
		});
		mHistoryShowGraph.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {

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

}
