package provdominoes.boundary;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.List;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import processor.MatrixProcessor;
import provdominoes.arch.Session;
import provdominoes.command.AbstractCommand;
import provdominoes.command.CommandFactory;
import provdominoes.command.CommandManager;
import provdominoes.control.Controller;
import provdominoes.domain.Configuration;
import provdominoes.domain.Dominoes;
import provdominoes.util.ConfigurationFile;

public class App extends Application {

	private static CommandManager commandManager;

	private static SplitPane mainPane;

	private static ActionHistoryGraphPane topPane;
	private static MainMenuBar menu;

	private static SplitPane bottomPane;
	private static ListViewDominoes pieceSelectorList;
	private static PieceCanvas movementCanvas;
	private static Visual tabbedMatrixGraphPane;

	private static Scene scene;
	private static Stage stage;

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {

			App.stage = primaryStage;
			App.stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
			App.stage.centerOnScreen();
			App.stage.setResizable(Configuration.resizable);

			App.menu = new MainMenuBar();

			App.commandManager = new CommandManager(menu);

			App.set(true);

			if (!Configuration.automaticCheck) {
				App.clear();
			}

		} catch (Exception ex) {
			alertException(ex, "Error starting Prov-Dominoes!");
		}
		String mode = null;
		String autoOpen = null;
		if (getParameters() != null) {
			mode = getParameters().getNamed().get("mode");
			autoOpen = getParameters().getNamed().get("script");
		}
		if (mode != null) {
			Configuration.defaultProcessing = mode;
			Configuration.tuning = true;
			Configuration.telemetry = true;
		}
		if (autoOpen != null) {
			Configuration.autoOpen = autoOpen;
		}
		if (!MatrixProcessor.isLibSkipped()) {
			if (Configuration.isGPUProcessing()) {
				if (Configuration.gpuDevice + 1 > MatrixProcessor.getDeviceCount()) {
					Configuration.gpuDevice = 0;
				}
				Session.startSession(Configuration.gpuDevice);
			}
		}
		App.stage.setTitle("Prov-Dominoes ["
				+ (Configuration.isGPUProcessing() ? Configuration.GPU_PROCESSING : Configuration.CPU_PROCESSING)
				+ "]");
		if (Configuration.autoOpen.length() > 0) {
			if (new File(Configuration.autoOpen).exists()) {

				if (Configuration.telemetry) {
					System.out.println("Telemetry is on...");
					long startTime = System.nanoTime();
					App.importScriptFromFile(new File(Configuration.autoOpen));
					long endTime = System.nanoTime();
					long timeElapsed = endTime - startTime;
					double d = timeElapsed / 1000000d;
					DecimalFormat df = new DecimalFormat("#.##");
					System.out.println("Time elapsed for script execution in ms: " + df.format(d));
				} else {
					App.importScriptFromFile(new File(Configuration.autoOpen));
				}
			} else {
				alertException(new Exception("Inexistent EPS script for auto open!"),
						"Inexistent EPS script for auto open!");
			}
		}
		if (mode == null) {
			App.stage.show();
		} else {
			Configuration.autoOpen = "";
			App.executeExit();
		}

	}

	public static Stage getStage() {
		return stage;
	}

	/**
	 * This function remove the element of the list and of the move area
	 *
	 * @param dominoes Element to remove
	 * @param group    Element to remove
	 * @return true, in affirmative case
	 * @throws IOException
	 */
	public static boolean removeMatrix(Dominoes dominoes, Group group) throws IOException {
		boolean result = provdominoes.control.Controller.removeMatrix(dominoes);

		// if not removed both, then we have which resultMultiplication
		if (App.movementCanvas.remove(group)) {
			if (App.pieceSelectorList.remove(group)) {
				if (result) {
					return true;
				} else {
					App.movementCanvas.add(dominoes, -1);
					App.pieceSelectorList.add(dominoes);
					return false;
				}
			} else {
				App.movementCanvas.add(dominoes, -1);
				return false;
			}
		}
		return false;

	}

	/**
	 * This functions is called when user want save each alteration
	 *
	 * @throws IOException
	 */
	public static void saveAll() throws IOException {
		App.movementCanvas.saveAllAndSendToList();
	}

	public static void alert(AlertType t, String title, String header, String message) {
		Alert alert = new Alert(t);
		alert.setTitle(title);
		alert.setHeaderText(header);
		alert.setContentText(message);
		alert.showAndWait();
	}

	public static void alertException(Exception e, String message) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Erro");
		alert.setHeaderText("Erro não identificado!");
		alert.setContentText(message);

		// Create expandable Exception.
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label("The exception stacktrace was:");

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);

		alert.showAndWait();
	}

	static void changeVisibleGraphHistory() {
		App.topPane.setVisible(Configuration.visibilityGraphHistory);
		set(false);
		setSize(Configuration.fullScreen);
		stage.show();
	}

	/**
	 * Set the basic configuration of this Application
	 */
	public static void set(boolean start) {
		if (start) {
			App.pieceSelectorList = new ListViewDominoes(null);
			App.tabbedMatrixGraphPane = new Visual();
			App.movementCanvas = new PieceCanvas();
			topPane = new ActionHistoryGraphPane();
		}

		App.scene = null;
		App.scene = new Scene(new Group());
		VBox back = new VBox();

		mainPane = new SplitPane();
		mainPane.setOrientation(Orientation.VERTICAL);

		bottomPane = new SplitPane();
		bottomPane.getItems().add(App.pieceSelectorList);
		bottomPane.getItems().add(App.movementCanvas);
		bottomPane.getItems().add(App.tabbedMatrixGraphPane);

		adjustTopPane();

		back.getChildren().addAll(menu, mainPane);

		App.scene.setRoot(back);
		App.stage.setScene(App.scene);
		stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				executeExit();
			}
		});
		App.setSize(Configuration.fullScreen);
	}

	private static void adjustTopPane() {
		topPane.visibleProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
				if (topPane.isVisible()) {
					mainPane.getItems().remove(bottomPane);
					mainPane.getItems().add(topPane);
					mainPane.getItems().add(bottomPane);

				} else {
					mainPane.getItems().remove(topPane);
				}
			}
		});

		if (Configuration.visibilityGraphHistory)
			mainPane.getItems().add(topPane);

		mainPane.getItems().add(bottomPane);
	}

	public static void executeExit() {
		Platform.setImplicitExit(false);
		try {
			Configuration.width = App.stage.getWidth();
			Configuration.height = App.stage.getHeight();
			if (App.pieceSelectorList.getWidth() != 0) {
				Configuration.listWidth = App.pieceSelectorList.getWidth();
			}
			new ConfigurationFile().updateConfiguration();
			if (Configuration.isGPUProcessing()) {
				Session.closeSection();
			}
		} catch (IOException e) {
			alert(AlertType.ERROR, "Erro de IO", "Falha na atualização de configuração",
					"Erro de IO ao tentar atualizar as configurações em configuration.properties");
			stage.close();
			System.exit(1);
		} catch (Exception e) {
			alert(AlertType.ERROR, "Erro de IO", "Falha na atualização de configuração",
					"Erro não identificado ao tentar atualizar as configurações em configuration.properties");
			stage.close();
			System.exit(1);
		}
		stage.close();
		System.exit(0);
	}

	@Override
	public void stop() {
		// Destroy all remaining dominoes

	}

	/**
	 * This function is used to exit this program
	 *
	 * @param status status required by the operating system
	 */
	@SuppressWarnings("unused")
	private static void exit(final int status) {
		System.exit(status);
	}

	/**
	 * This function is called to change the parts color
	 */
	static void changeColor() {
		App.pieceSelectorList.changeColor();
		App.movementCanvas.changeColor();
	}

	/**
	 * This function remove all parts in this Piece Canvas
	 * 
	 * @throws IOException
	 */
	public static void clear() throws IOException {
		pieceSelectorList.clear();
		movementCanvas.clear();
		tabbedMatrixGraphPane.clear();
		commandManager.clear(true);
	}

	/**
	 * This function adds in List a matrix specified
	 *
	 * @param dominoes the matrix to be added
	 */
	public static void CopyToList(Dominoes dominoes) {
		App.pieceSelectorList.add(dominoes);
	}

	/**
	 * This function adds in Area a matrix specified
	 *
	 * @param dominoes the matrix to be added
	 */
	public static Group copyToArea(Dominoes dominoes, int index) {
		return App.movementCanvas.add(dominoes, index);
	}

	/**
	 * This function is used to define the visibility of historic
	 *
	 * @param visibility True to define visible the historic
	 */
	public static void setVisibleHistoric() {
		movementCanvas.setVisibleHistoric();
		pieceSelectorList.setVisibleHistoric();
	}

	/**
	 * This function is used to define the visibility of type
	 *
	 * @param visibility True to define visible the type
	 */
	public static void setVisibleType() {
		movementCanvas.setVisibleType();
		pieceSelectorList.setVisibleType();
	}

	/**
	 * This function is used to make full screen in this Application
	 *
	 * @param fullscreen
	 */
	static void setSize(boolean fullscreen) {
		Configuration.fullScreen = fullscreen;
		double padding = menu.getHeight();
		App.stage.setFullScreen(fullscreen);

		if (!fullscreen) {
			padding += 30;

			App.stage.setWidth(Configuration.width);
			App.stage.setHeight(Configuration.height);
			App.stage.centerOnScreen();
		}

		App.pieceSelectorList.setSize(Configuration.listWidth, App.stage.getHeight() - padding);
		App.tabbedMatrixGraphPane.setSize(900, App.stage.getHeight() - padding);
		App.movementCanvas.setSize(400, App.stage.getHeight() - padding);
	}

	public static void openProv() {
		try {
			FileChooser fileChooser = new FileChooser();
			File folder = new File(Configuration.lastDirectory);
			if (folder.exists()) {
				fileChooser.setInitialDirectory(folder);
			}
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("W3C PROV-N (*.provn)", "*.provn"));
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("W3C PROV-XML (*.xml)", "*.xml"));
			List<File> files = fileChooser.showOpenMultipleDialog(stage);
			if (files != null) {
				String[] fileNames = new String[files.size()];
				App.clear();
				App.getTopPane().reset();
				String dir = "";
				int i = 0;
				for (File f : files) {
					if (f != null) {
						dir = f.getAbsolutePath().replace(f.getName(), "");
						fileNames[i] = f.getAbsolutePath();
						i++;
						Configuration.lastDirectory = dir;
					}
				}
				getCommandManager().invokeCommand(CommandFactory.getInstance().load(fileNames, dir));
			}
		} catch (Exception e) {
			alertException(e, "Erro ao tentar abrir arquivo PROV-N!");
		}

	}

	public static void importMatrices() {
		try {
			FileChooser fileChooser = new FileChooser();
			File folder = new File(Configuration.lastDirectory);
			if (folder.exists()) {
				fileChooser.setInitialDirectory(folder);
			}
			fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Matrix(ces)", "*.matrix"));
			List<File> files = fileChooser.showOpenMultipleDialog(stage);
			if (files != null) {
				String[] fileNames = new String[files.size()];
				App.clear();
				App.getTopPane().reset();
				String dir = "";
				int i = 0;
				for (File f : files) {
					if (f != null) {
						dir = f.getAbsolutePath().replace(f.getName(), "");
						fileNames[i] = f.getAbsolutePath();
						i++;
						Configuration.lastDirectory = dir;
					}
				}
				getCommandManager().invokeCommand(CommandFactory.getInstance().loadMatrices(fileNames, dir));
			}
		} catch (Exception e) {
			alertException(e, "Erro ao tentar abrir arquivo PROV-N!");
		}

	}

	public static void exportScript() {
		try {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setInitialDirectory(new File(Configuration.lastDirectory));
			fileChooser.getExtensionFilters()
					.add(new FileChooser.ExtensionFilter("Exploration Provenance Script (*.eps)", "*.eps"));
			fileChooser.setInitialFileName("commands");
			File file = fileChooser.showSaveDialog(stage);
			if (file != null) {
				file.delete();
				file.createNewFile();
				if (file != null) {
					FileWriter fw = new FileWriter(file);
					StringWriter sw = new StringWriter();
					getCommandManager().getScriptFromGraph(sw, getTopPane().getRootCommand(), 0);
					fw.write(sw.toString());
					fw.close();
				}
			}
		} catch (Exception e) {
			alertException(e, "Erro ao tentar exportar script");
		}

	}

	public static void importScriptFromFile(File file) {
		if (file != null) {
			try {
				getCommandManager().setDir(file.getAbsolutePath().replace(file.getName(), ""));
				Configuration.lastDirectory = file.getAbsolutePath().replace(file.getName(), "");
				App.clear();
				App.getTopPane().reset();
				FileReader fr = new FileReader(file);
				try (BufferedReader br = new BufferedReader(fr)) {
					String commandLine = br.readLine();
					while (commandLine != null) {
						if (commandLine.startsWith("#") || commandLine.startsWith("\\n")
								|| commandLine.startsWith("\\r\\n")) {
							commandLine = br.readLine();
							continue;
						}
						AbstractCommand cmd = getCommandManager().getScriptController().parseCommand(commandLine);
						if (cmd == null) {
							throw new Exception(
									"Invalid Command Exception! The script contains unrecognized command or invalid reference at this line: "
											+ commandLine);
						}
						getCommandManager().invokeCommand(cmd, false, true);
						commandLine = br.readLine();
					}
					App.getCommandManager().getRedoList().clear();
					App.getCommandManager().uptadeMenu();
					updatePieceCounter();
				}
				fr.close();
			} catch (Exception e) {
				alertException(e, "Erro ao tentar importar script!");
			}
		}
	}

	private static void updatePieceCounter() {
		List<Dominoes> domList = App.getArea().getData().getDominoes();
		int count = 0;
		for (Dominoes dominoes : domList) {
			if (dominoes.getId().substring(1).matches("\\d+")) {
				if (count < Integer.valueOf(dominoes.getId().substring(1))) {
					count = Integer.valueOf(dominoes.getId().substring(1));
				}
			}
		}
		CommandFactory.getInstance().setPieceCounter(count + 1);
	}

	public static void importScript() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setInitialDirectory(new File(Configuration.lastDirectory));
		fileChooser.getExtensionFilters()
				.add(new FileChooser.ExtensionFilter("Exploration Provenance Script (*.eps)", "*.eps"));
		File file = fileChooser.showOpenDialog(stage);
		importScriptFromFile(file);
	}

	public static void start() {
		launch(Controller.args);
	}

	static void drawGraph(Dominoes domino) {
		tabbedMatrixGraphPane.addTabGraph(domino);
	}

	static void drawCentralityGraph(Dominoes domino) {
		tabbedMatrixGraphPane.addTabCentralityGraph(domino);
	}

	static void drawMatrix(Dominoes domino) {
		tabbedMatrixGraphPane.addTabMatrix(domino);
	}

	static void drawChart(Dominoes domino) {
		tabbedMatrixGraphPane.addTabChart(domino);
	}

	static void drawLineChart(Dominoes domino) {
		tabbedMatrixGraphPane.addTabLineChart(domino);
	}

	public static void setStage(Stage stage) {
		App.stage = stage;
	}

	public static CommandManager getCommandManager() {
		return commandManager;
	}

	public static void setCommandManager(CommandManager commandManager) {
		App.commandManager = commandManager;
	}

	public static ListViewDominoes getList() {
		return pieceSelectorList;
	}

	public static void setList(ListViewDominoes list) {
		App.pieceSelectorList = list;
	}

	public static PieceCanvas getArea() {
		return movementCanvas;
	}

	public static void setArea(PieceCanvas area) {
		App.movementCanvas = area;
	}

	public static ActionHistoryGraphPane getTopPane() {
		return topPane;
	}

	public static void setTopPane(ActionHistoryGraphPane topPane) {
		App.topPane = topPane;
	}

	public static void main(String args[]) {
		Controller.args = args;

		try {
			// read the configuration file
			provdominoes.control.Controller.loadConfiguration();

			if (!MatrixProcessor.isLibSkipped()) {
				if (Configuration.isGPUProcessing()) {
					if (Configuration.gpuDevice + 1 > MatrixProcessor.getDeviceCount()) {
						Configuration.gpuDevice = 0;
					}
					Session.startSession(Configuration.gpuDevice);
				}
			}

			launch(args);

			if (!MatrixProcessor.isLibSkipped()) {
				if (Configuration.isGPUProcessing()) {
					Session.closeSection();
				}
			}

		} catch (Exception e) {
			alertException(e, "Erro desconhecido ao tentar iniciar aplicação!");
		}
	}

	public static Visual getVisual() {
		return tabbedMatrixGraphPane;
	}

	public static ListViewDominoes getPieceSelectorList() {
		return pieceSelectorList;
	}

	public static void setPieceSelectorList(ListViewDominoes pieceSelectorList) {
		App.pieceSelectorList = pieceSelectorList;
	}

	public static PieceCanvas getMovementCanvas() {
		return movementCanvas;
	}

	public static void setMovementCanvas(PieceCanvas movementCanvas) {
		App.movementCanvas = movementCanvas;
	}

	public static Visual getTabbedMatrixGraphPane() {
		return tabbedMatrixGraphPane;
	}

	public static void setTabbedMatrixGraphPane(Visual tabbedMatrixGraphPane) {
		App.tabbedMatrixGraphPane = tabbedMatrixGraphPane;
	}

	public static MainMenuBar getMenu() {
		return menu;
	}

	public static void setMenu(MainMenuBar menu) {
		App.menu = menu;
	}

}
