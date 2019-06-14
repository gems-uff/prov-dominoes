package boundary;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.josericardojunior.arch.Session;
import com.josericardojunior.dao.DominoesSQLDao;
import com.josericardojunior.domain.Dominoes;

import command.CommandManager;
import control.Controller;
import convertion.ProvMatrixFactory;
import domain.Configuration;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.ProvMatrix;
import util.Prov2DominoesUtil;

// Information fragment // opened question

public class App extends Application {

	private static CommandManager commandManager;

	public static Visual getVisual() {
		return tabbedMatrixGraphPane;
	}

	private static SplitPane mainPane;

	private static ActionHistoryGraphPane topPane;
	private static DominoesMenuBar menu;
	public static TimePane time;

	private static SplitPane bottomPane;
	private static ListViewDominoes pieceSelectorList;
	private static AreaMove movementCanvas;
	private static Visual tabbedMatrixGraphPane;

	private static Scene scene;
	private static Stage stage;


	private static GUIManager manager;

	@Override
	public void start(Stage primaryStage) throws Exception {
		try {

			App.stage = primaryStage;
			App.stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
			App.stage.centerOnScreen();
			App.stage.setTitle("Dominoes Interface [" + Configuration.processingUnit + "]");
			App.stage.setResizable(Configuration.resizable);

			App.menu = new DominoesMenuBar();
			App.commandManager = new CommandManager(menu);
			App.time = new TimePane();

			App.set();

			time.setButtomOnAction(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent arg0) {

					try {
						App.menu.load(Configuration.beginDate, Configuration.endDate);
						App.setTimelime();
					} catch (ParseException e) {
						e.printStackTrace();
					}
				}
			});

			if (Configuration.resizableTimeOnFullScreen) {
				App.fillTimeHistoricPointers();
			}

			if (!Configuration.automaticCheck) {
				App.clear();
			}

		} catch (Exception ex) {
			System.err.println(ex.getMessage());
		}

		time.setButtomOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent arg0) {
				App.load(Configuration.beginDate, Configuration.endDate);
			}
		});
		
		App.stage.show();

	}

	private static void fillTimeHistoricPointers() {
		App.setFullscreen(!stage.isFullScreen());
		App.setFullscreen(!stage.isFullScreen());
	}

	/**
	 * This function remove the element of the list and of the move area
	 *
	 * @param dominoes
	 *            Element to remove
	 * @param group
	 *            Element to remove
	 * @return true, in affirmative case
	 * @throws IOException
	 */
	public static boolean removeMatrix(Dominoes dominoes, Group group) throws IOException {
		boolean result = control.Controller.removeMatrix(dominoes);

		// if not removed both, then we have which resultMultiplication
		if (App.movementCanvas.remove(group)) {
			if (App.pieceSelectorList.remove(group)) {
				if (result) {
					return true;
				} else {
					App.movementCanvas.add(dominoes,-1);
					App.pieceSelectorList.add(dominoes);
					return false;
				}
			} else {
				App.movementCanvas.add(dominoes,-1);
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

	public static void setTimelime() {

		double min = 0, max = 0;

		Calendar beginDate = Calendar.getInstance();
		beginDate.setTime(Configuration.beginDate);
		Calendar endDate = Calendar.getInstance();
		endDate.setTime(Configuration.endDate);

		try {
			min = beginDate.get(Calendar.YEAR) * 12;
			min += beginDate.get(Calendar.MONTH);

			max = endDate.get(Calendar.YEAR) * 12;
			max += endDate.get(Calendar.MONTH);

		} catch (Exception e) {
			e.printStackTrace();
		}
		max = max - min;
		min = 0;

		// Set begin and end date
		try {
			if (Configuration.projName != null && Configuration.projName.length() > 0) {
				App.time.Configure(Configuration.beginDate, Configuration.endDate, Configuration.database,
						Configuration.projName);
				App.time.setVisible(true);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void load(Date being, Date end) {
		manager.run(new Runnable() {
			public void run() {
				Platform.runLater(new Runnable() {
					public void run() {
						try {
							App.menu.load(Configuration.beginDate, Configuration.endDate);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					}
				});
			}
		});
	}

	public static void LoadDominoesPieces() {

		if (Configuration.projName != null && Configuration.projName.length() > 0) {
			control.Controller.loadAllMatrices(Configuration.beginDate, Configuration.endDate, Configuration.projName);
			App.pieceSelectorList.Configure(Controller.resultLoadMatrices);
		}

		/*manager = GUIManager.getInstance(); JFXPanel pane = new JFXPanel();
		  pane.setScene(stage.getScene()); JFrame window = new JFrame();
		  window.add(pane); window.setAlwaysOnTop(true);
		  manager.setMainWindow(window);*/

	}

	/**
	 * Set the basic configuration of this Application
	 */
	public static void set() {
		App.pieceSelectorList = new ListViewDominoes(null);
		App.tabbedMatrixGraphPane = new Visual();
		App.movementCanvas = new AreaMove();

		App.scene = null;
		App.scene = new Scene(new Group());
		VBox back = new VBox();

		mainPane = new SplitPane();
		mainPane.setOrientation(Orientation.VERTICAL);

		bottomPane = new SplitPane();
		bottomPane.getItems().add(App.pieceSelectorList);
		bottomPane.getItems().add(App.movementCanvas);
		bottomPane.getItems().add(App.tabbedMatrixGraphPane);
		
		
		topPane = new ActionHistoryGraphPane();
		//topPane.setTop(App.projectInfoPanel);
		//topPane.setCenter(time);
		topPane.visibleProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldValue, Boolean newValue) {
				if (topPane.isVisible()) {
					mainPane.getItems().remove(bottomPane);

					mainPane.getItems().add(topPane);
					mainPane.getItems().add(bottomPane);

					bottomPane.setPrefHeight(Configuration.height / 2);
					topPane.setPrefHeight(Configuration.height / 2);

				} else {

					mainPane.getItems().remove(topPane);
					bottomPane.setPrefHeight(Configuration.height);
				}
			}
		});

		if (Configuration.visibilityTimePane)
			mainPane.getItems().add(topPane);

		mainPane.getItems().add(bottomPane);

		back.getChildren().addAll(menu, mainPane);

		App.scene.setRoot(back);
		App.stage.setScene(App.scene);
		// App.stage.show();

		App.setFullscreen(Configuration.fullscreen);

	}

	@Override
	public void stop() {
		// Destroy all remaining dominoes

	}

	/**
	 * This function is used to exit this program
	 *
	 * @param status
	 *            status required by the operating system
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
	 * This function remove all parts in this area move
	 */
	public static void clear() {
		pieceSelectorList.clear();
		movementCanvas.clear();
		tabbedMatrixGraphPane.ClearTabs();
	}

	/**
	 * This function adds in List a matrix specified
	 *
	 * @param dominoes
	 *            the matrix to be added
	 */
	public static void CopyToList(Dominoes dominoes) {
		App.pieceSelectorList.add(dominoes);
	}

	/**
	 * This function adds in Area a matrix specified
	 *
	 * @param dominoes
	 *            the matrix to be added
	 */
	public static Group copyToArea(Dominoes dominoes,int index) {
		return App.movementCanvas.add(dominoes,index);
	}

	/**
	 * This function is used to define the visibility of historic
	 *
	 * @param visibility
	 *            True to define visible the historic
	 */
	public static void setVisibleHistoric() {
		movementCanvas.setVisibleHistoric();
		pieceSelectorList.setVisibleHistoric();
	}

	/**
	 * This function is used to define the visibility of type
	 *
	 * @param visibility
	 *            True to define visible the type
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
	static void setFullscreen(boolean fullscreen) {
		Configuration.fullscreen = fullscreen;
		double padding = menu.getHeight();
		App.stage.setFullScreen(fullscreen);

		if (!fullscreen) {
			padding += 30;

			App.stage.setWidth(Configuration.width);
			App.stage.setHeight(Configuration.height);
			App.stage.centerOnScreen();
		}


		if (Configuration.visibilityTimePane) {
			// App.time.definitionSlider(stage);
		}
		App.pieceSelectorList.setSize(Configuration.listWidth, App.stage.getHeight() - padding);
		App.tabbedMatrixGraphPane.setSize(300, App.stage.getHeight() - padding);
		App.movementCanvas.setSize(400, App.stage.getHeight() - padding);
		stage.show();
	}

	static void changeVisibleTimePane() {
		Configuration.visibilityTimePane = !Configuration.visibilityTimePane;
		App.time.setVisible(Configuration.visibilityTimePane);
		App.topPane.setVisible(Configuration.visibilityTimePane);
	}

	public static void openProv() {
		try {
			FileChooser fileChooser = new FileChooser();
			File file = fileChooser.showOpenDialog(stage);
			if (file != null) {
				String provFilePath = file.getAbsolutePath();
				ProvMatrixFactory provFactory = new ProvMatrixFactory(provFilePath);
				List<ProvMatrix> provMatrixList = provFactory.buildMatrices();
				List<Dominoes> dominoesList = Prov2DominoesUtil.convert(provMatrixList);
				clear();
				Controller.resultLoadMatrices = dominoesList;
				pieceSelectorList.Configure(Controller.resultLoadMatrices);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	*
	*/
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

	static void drawTree(Dominoes domino) {
		tabbedMatrixGraphPane.addTabTree(domino);
	}

	static Stage getStage() {
		return App.stage;
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

	public static AreaMove getArea() {
		return movementCanvas;
	}

	public static void setArea(AreaMove area) {
		App.movementCanvas = area;
	}
	
	
    public static void main(String args[]){
    	Controller.args = args;
        
    	try {
            // read the configuration file
            control.Controller.loadConfiguration();
            
            if (Configuration.processingUnit == Configuration.GPU_DEVICE)
            	Session.startSession(Configuration.gpuDevice);
            
            DominoesSQLDao.openDatabase(Configuration.database);
            // call Application.launch()
            launch(args);
           
            if (Configuration.processingUnit == Configuration.GPU_DEVICE)
            	Session.closeSection();
            
           // DominoesSQLDao.closeDatabase();
        } catch (Exception ex) {
            System.err.println(ex.getMessage());
        }
    }

	public static ActionHistoryGraphPane getTopPane() {
		return topPane;
	}

	public static void setTopPane(ActionHistoryGraphPane topPane) {
		App.topPane = topPane;
	}
}
