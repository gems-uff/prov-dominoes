package provdominoes.boundary;

import java.io.IOException;
import java.util.ArrayList;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.util.Duration;
import provdominoes.command.AddCommand;
import provdominoes.command.CommandFactory;
import provdominoes.command.MoveCommand;
import provdominoes.command.SaveCommand;
import provdominoes.command.SortRowFirstCommand;
import provdominoes.domain.Configuration;
import provdominoes.domain.Dominoes;

public class PieceCanvas extends Pane {

	private PieceCanvasState data;
	private MoveCommand currentMove;
	private CommandFactory commandFactory;

	/**
	 * Class builder with the dimension defined in parameters. here, will create too
	 * a background with white color
	 *
	 */
	public PieceCanvas() {
		super();
		this.data = new PieceCanvasState(-1, -1, 0, Configuration.width, false, new ArrayList<>(), new ArrayList<>());
		this.data.setBackground(new Rectangle());
		this.data.getBackground().setFill(new Color(1, 1, 1, 1));

		this.getChildren().addAll(data.getBackground());

		this.data.setDominoes(new ArrayList<>());
		this.data.setPieces(new ArrayList<>());
		this.commandFactory = new CommandFactory();
	}

	/**
	 * Add a new Domino in Piece Canvas in position x = 0, y = 0
	 *
	 * @param domino The Domino information
	 */
	public Group add(Dominoes domino, int index) {
		return this.add(domino, 0, 0, index);

	}

	/**
	 * Add a new Domino in Piece Canvas in position defined for parameters
	 *
	 * @param domino The Domino information
	 * @param x      The coordinate X of this new Domino
	 * @param y      The coordinate Y of this new Domino
	 */
	public Group add(Dominoes domino, double x, double y, int indexList) {
		double thisTranslateX = x;
		double thisTranslateY = y;

		ContextMenu minimenu = new ContextMenu();

		MenuItem menuItemAggregateRows = new MenuItem("Aggregate Row (" + domino.getIdRow() + ")");
		MenuItem menuItemAggregateColumns = new MenuItem("Aggregate Column (" + domino.getIdCol() + ")");
		MenuItem menuItemConfidence = new MenuItem("Confidence");

		MenuItem menuItemZScore = new MenuItem("Z-Score");
		MenuItem menuItemTransitiveClosure = new MenuItem("Transitive Closure");
		MenuItem menuItemTrim = new MenuItem("Trim");
		MenuItem menuItemBinarize = new MenuItem("Binarize");
		MenuItem menuItemInvert = new MenuItem("Invert");

		MenuItem menuItemSaveInList = new MenuItem("Save");
		MenuItem menuItemViewGraph = new MenuItem("Graph");
		MenuItem menuItemViewEigenCentrality = new MenuItem("Centrality Graph");
		MenuItem menuItemViewMatrix = new MenuItem("Matrix");
		MenuItem menuItemViewChart = new MenuItem("Bar Chart");
		MenuItem menuItemViewLineChart = new MenuItem("Line Chart");
		MenuItem menuItemClose = new MenuItem("Close");

		MenuItem menuItemDiagonalFilter = new MenuItem("Diagonalize");
		MenuItem menuItemUpperDiagonalFilter = new MenuItem("Upper Triangular");
		MenuItem menuItemLowerDiagonalFilter = new MenuItem("Lower Triangular");
		MenuItem menuItemHighPassFilter = new MenuItem("High-Pass Filter (HPF)");
		MenuItem menuItemLowPassFilter = new MenuItem("Low-Pass Filter (LPF)");
		MenuItem menuItemRowTextFilter = new MenuItem("Word on Row");
		MenuItem menuItemColumnTextFilter = new MenuItem("Word on Column");

		MenuItem menuItemSortRows = new MenuItem("Sort by Rows Asc");
		MenuItem menuItemSortColumns = new MenuItem("Sort by Columns Asc");
		MenuItem menuItemSortRowCount = new MenuItem("Sort by Row Count");
		MenuItem menuItemSortColumnCount = new MenuItem("Sort by Column Count");
		// MenuItem menuItemSortRowsFirst = new MenuItem("Sort by Row-First");
		// MenuItem menuItemSortColumnsFirst = new MenuItem("Sort by Column-First");
		// MenuItem menuItemSortRowValues = new MenuItem("Sort by Row Values");
		// MenuItem menuItemSortColumnValues = new MenuItem("Sort by Column Values");
		MenuItem menuItemSortCluster = new MenuItem("Sort by Cluster");
		// MenuItem menuItemSortClusterValues = new MenuItem("Sort by Cluster Values");

		Menu menuOperate = new Menu("Operations");
		Menu menuFilters = new Menu("Filters");
		Menu menuSorting = new Menu("Sorting");
		Menu menuView = new Menu("Visualizations");

		Group piece = domino.drawDominoes();
		piece.getChildren().get(Dominoes.GRAPH_HISTORIC).setVisible(Configuration.visibilityHistoric);

		piece.setTranslateX(thisTranslateX);
		piece.setTranslateY(thisTranslateY);
		if (!this.data.getPieces().isEmpty()) {
			for (Group g : this.data.getPieces()) {

				if (g.getTranslateY() + Dominoes.GRAPH_HEIGHT >= data.getBackground().prefHeight(-1)) {
					thisTranslateX += Dominoes.GRAPH_WIDTH;
					thisTranslateY = data.getBackground().getY();
					continue;
				}

				if ((thisTranslateX >= g.getTranslateX() && thisTranslateX < g.getTranslateX() + Dominoes.GRAPH_WIDTH
						&& thisTranslateY >= g.getTranslateY()
						&& thisTranslateY < g.getTranslateY() + Dominoes.GRAPH_HEIGHT)
						|| (thisTranslateX + Dominoes.GRAPH_WIDTH >= g.getTranslateX()
								&& thisTranslateX + Dominoes.GRAPH_WIDTH < g.getTranslateX() + Dominoes.GRAPH_WIDTH
								&& thisTranslateY >= g.getTranslateY()
								&& thisTranslateY < g.getTranslateY() + Dominoes.GRAPH_HEIGHT)
						|| (thisTranslateX >= g.getTranslateX()
								&& thisTranslateX < g.getTranslateX() + Dominoes.GRAPH_WIDTH
								&& thisTranslateY + Dominoes.GRAPH_HEIGHT >= g.getTranslateY()
								&& thisTranslateY + Dominoes.GRAPH_HEIGHT < g.getTranslateY() + Dominoes.GRAPH_HEIGHT)
						|| (thisTranslateX + Dominoes.GRAPH_WIDTH >= g.getTranslateX()
								&& thisTranslateX + Dominoes.GRAPH_WIDTH < g.getTranslateX() + Dominoes.GRAPH_WIDTH
								&& thisTranslateY + Dominoes.GRAPH_HEIGHT >= g.getTranslateY() && thisTranslateY
										+ Dominoes.GRAPH_HEIGHT < g.getTranslateY() + Dominoes.GRAPH_HEIGHT)) {

					thisTranslateY = g.getTranslateY() + Dominoes.GRAPH_HEIGHT;
				}
			}
			piece.setTranslateY(thisTranslateY);
			piece.setTranslateX(thisTranslateX);
		}

		addOnLists(domino, piece, indexList);

		// if (!domino.getIdRow().equals(domino.getIdCol())) {
		// menuItemViewGraph.setDisable(true);
		// }

		piece.setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				cursorProperty().set(Cursor.OPEN_HAND);
			}
		});

		piece.setOnDragDetected(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				int index = App.getArea().getData().getPieces().indexOf(piece);
				String identifier = App.getArea().getData().getDominoes().get(index).getId();
				currentMove = CommandFactory.getInstance().move(identifier, piece, piece.getTranslateX(),
						piece.getTranslateY());
			}
		});

		piece.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				// detect move out
				boolean detectMoveOutY = false;

				double offsetY = event.getSceneY() - data.getSrcSceneY();
				double newTranslateY = data.getSrcTranslateY() + offsetY;

				boolean detectMoveOutX = false;
				double offsetX = event.getSceneX() - data.getSrcSceneX();
				double newTranslateX = data.getSrcTranslateX() + offsetX;
				if (newTranslateX < data.getBackground().getX()) {
					((Group) (event.getSource())).setTranslateX(data.getBackground().getX());

					detectMoveOutX = true;
				}
				if (newTranslateY < data.getBackground().getY()) {
					((Group) (event.getSource())).setTranslateY(data.getBackground().getY());
					detectMoveOutY = true;
				}
				if (newTranslateX + ((Group) (event.getSource())).prefWidth(-1) > data.getBackground().getX()
						+ data.getBackground().getWidth()) {
					((Group) (event.getSource())).setTranslateX(data.getBackground().getX()
							+ data.getBackground().getWidth() - ((Group) (event.getSource())).prefWidth(-1));
					detectMoveOutX = true;
				}
				if (newTranslateY + ((Group) (event.getSource())).prefHeight(-1) > data.getBackground().getY()
						+ data.getBackground().getHeight()) {
					((Group) (event.getSource())).setTranslateY(data.getBackground().getY()
							+ data.getBackground().getHeight() - ((Group) (event.getSource())).prefHeight(-1));
					detectMoveOutY = true;
				}

				if (!detectMoveOutX) {
					((Group) (event.getSource())).setTranslateX(newTranslateX);
				}
				if (!detectMoveOutY) {
					((Group) (event.getSource())).setTranslateY(newTranslateY);
				}

				// detect multiplication or sum
				int index = data.getPieces().indexOf(piece);
				for (int j = 0; j < data.getPieces().size(); j++) {
					if (checkAndPrepareMultiplication(index, j)) {
						break;
					} else if (checkAndPrepareSubtraction(index, j)) {
						break;
					} else if (checkAndPrepareSum(index, j)) {
						break;
					}
				}
			}

		});
		piece.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				data.setSrcSceneX(event.getSceneX());
				data.setSrcSceneY(event.getSceneY());
				data.setSrcTranslateX(((Group) (event.getSource())).getTranslateX());
				data.setSrcTranslateY(((Group) (event.getSource())).getTranslateY());

				piece.toFront();
				cursorProperty().set(Cursor.CLOSED_HAND);
			}
		});
		piece.setOnMouseReleased(new EventHandler<MouseEvent>() {

			private void normalizePieceColors(Group piece, Group p2) {
				((Text) piece.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_NORMAL_FONT);
				((Text) piece.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_NORMAL_FONT);
				((Text) p2.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_NORMAL_FONT);
				((Text) p2.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_NORMAL_FONT);
			}

			@Override
			public void handle(MouseEvent event) {
				Group p2 = null;
				for (int j = 0; j < data.getPieces().size(); j++) {
					p2 = data.getPieces().get(j);
					boolean right = piece.getTranslateX() >= p2.getTranslateX() + Dominoes.GRAPH_WIDTH / 2;
					boolean left = p2.getTranslateX() >= piece.getTranslateX() + Dominoes.GRAPH_WIDTH / 2;
					boolean bottom = piece.getTranslateY() >= p2.getTranslateY() + Dominoes.GRAPH_HEIGHT / 2;
					boolean top = p2.getTranslateY() >= piece.getTranslateY() + Dominoes.GRAPH_HEIGHT / 2;
					boolean diagonal1 = right && bottom || left && top;
					boolean diagonal2 = left && bottom || right && top;
					if ((diagonal1 || diagonal2)) {
						normalizePieceColors(piece, p2);
					} else if (top) {
						normalizePieceColors(piece, p2);
					} else if (bottom) {
						normalizePieceColors(piece, p2);
					} else if (left) {
						normalizePieceColors(piece, p2);
					} else if (right) {
						normalizePieceColors(piece, p2);
					}
				}
				cursorProperty().set(Cursor.OPEN_HAND);
				if (App.getArea().getData().getCombination() == PieceCanvasState.COMBINATION_MULTIPLICATION
						&& App.getArea().getData().getIndexFirstOperatorCombination() != -1
						&& App.getArea().getData().getIndexSecondOperatorCombination() != -1) {
					App.getCommandManager().invokeCommand(commandFactory.multiply());
				} else if (App.getArea().getData().getCombination() == PieceCanvasState.COMBINATION_SUM
						&& App.getArea().getData().getIndexFirstOperatorCombination() != -1
						&& App.getArea().getData().getIndexSecondOperatorCombination() != -1) {
					App.getCommandManager().invokeCommand(commandFactory.sum());
				} else if (App.getArea().getData().getCombination() == PieceCanvasState.COMBINATION_SUBTRACTION
						&& App.getArea().getData().getIndexFirstOperatorCombination() != -1
						&& App.getArea().getData().getIndexSecondOperatorCombination() != -1) {
					App.getCommandManager().invokeCommand(commandFactory.subtract());
				} else if (piece != null && currentMove != null && piece.getTranslateX() != currentMove.getOldX()
						&& piece.getTranslateY() != currentMove.getOldY()) {
					int index = App.getArea().getData().getPieces().indexOf(piece);
					String identifier = App.getArea().getData().getDominoes().get(index).getId();
					MoveCommand move = CommandFactory.getInstance().move(identifier, piece, currentMove.getOldX(),
							currentMove.getOldY());
					App.getCommandManager().invokeCommand(move);
					currentMove = null;
				}
			}
		});
		piece.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				cursorProperty().set(Cursor.DEFAULT);
			}
		});
		// This value may need tuning:
		Duration maxTimeBetweenSequentialClicks = Duration.millis(200);

		PauseTransition clickTimer = new PauseTransition(maxTimeBetweenSequentialClicks);
		final IntegerProperty sequentialClickCount = new SimpleIntegerProperty(0);
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Provenance Piece Duplication");
		alert.setContentText("Are you sure you want to duplicate this provenance piece?");
		final Button button = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
		button.setOnAction(event -> {
			SaveCommand save = (SaveCommand) commandFactory.save(piece);
			App.getCommandManager().invokeCommand(save);
			AddCommand add = (AddCommand) commandFactory.add(save.getIntoId());
			App.getCommandManager().invokeCommand(add);
			App.getPieceSelectorList().remove(save.getSavedDominoes());

			MoveCommand move = CommandFactory.getInstance().move(add.getKey(), piece, piece.getTranslateX(),
					piece.getTranslateY());
			move.setX(piece.getTranslateX());
			move.setY(piece.getTranslateY() + Dominoes.GRAPH_HEIGHT + 20);

			App.getCommandManager().invokeCommand(move);
			event.consume();
		});
		clickTimer.setOnFinished(event -> {
			int count = sequentialClickCount.get();
			if (count == 2) {
				if (!data.isTransposing()) {
					App.getCommandManager().invokeCommand(commandFactory.transpose(piece));
				}
			}
			if (count == 3) {
				Platform.runLater(alert::showAndWait);
			}
			sequentialClickCount.set(0);
		});

		piece.setOnMouseClicked(event -> {
			if (event.getButton().equals(MouseButton.PRIMARY)) {
				sequentialClickCount.set(sequentialClickCount.get() + 1);
				clickTimer.playFromStart();
			}
		});
		piece.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (e.getButton() == MouseButton.SECONDARY) {
					minimenu.show(piece, e.getScreenX(), e.getScreenY());
				} else {
					minimenu.hide();
				}
			}
		});
		minimenu.addEventFilter(MouseEvent.MOUSE_RELEASED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				if (event.getButton() == MouseButton.SECONDARY) {
					event.consume();
				}
			}
		});
		minimenu.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (((MenuItem) event.getTarget()).getText().equals(menuItemSaveInList.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.save(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemClose.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.remove(piece));
				}
			}
		});
		menuOperate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (((MenuItem) event.getTarget()).getText().equals(menuItemAggregateRows.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.aggRows(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemAggregateColumns.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.aggColumns(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemConfidence.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.confidence(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemZScore.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.zscore(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemTransitiveClosure.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.transitiveClosure(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemInvert.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.invert(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemBinarize.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.binarize(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemTrim.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.trim(piece));
				}
			}
		});

		menuFilters.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (((MenuItem) event.getTarget()).getText().equals(menuItemDiagonalFilter.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.filterDiagonal(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemUpperDiagonalFilter.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.filterUpperDiagonal(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemLowerDiagonalFilter.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.filterLowerDiagonal(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemHighPassFilter.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.filterHighPassFilter(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemLowPassFilter.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.filterLowPassFilter(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemColumnTextFilter.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.filterColumnText(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemRowTextFilter.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.filterRowText(piece));
				}
			}
		});

		menuSorting.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (((MenuItem) event.getTarget()).getText().equals(menuItemSortRows.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.sortRows(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemSortColumns.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.sortColumns(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemSortRowCount.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.sortRowCount(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemSortColumnCount.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.sortColumnCount(piece));
					/*
					 * } else if (((MenuItem)
					 * event.getTarget()).getText().equals(menuItemSortColumnsFirst.getText())) {
					 * App.getCommandManager().invokeCommand(commandFactory.sortColumnFirst(piece));
					 * } else if (((MenuItem)
					 * event.getTarget()).getText().equals(menuItemSortRowsFirst.getText())) {
					 * App.getCommandManager().invokeCommand(commandFactory.sortRowFirst(piece));
					 */
					/*
					 * } else if (((MenuItem)
					 * event.getTarget()).getText().equals(menuItemSortRowValues.getText())) {
					 * App.getCommandManager().invokeCommand(commandFactory.sortRowValues(piece)); }
					 * else if (((MenuItem)
					 * event.getTarget()).getText().equals(menuItemSortColumnValues.getText())) {
					 * App.getCommandManager().invokeCommand(commandFactory.sortColumnValues(piece))
					 * ;
					 */
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemSortCluster.getText())) {
					SortRowFirstCommand sortRowFirst = (SortRowFirstCommand) commandFactory.sortRowFirst(piece);
					App.getCommandManager().invokeCommand(sortRowFirst);
					App.getCommandManager().invokeCommand(commandFactory.sortColumnFirst(sortRowFirst.getPiece()));
				} /*
					 * else if (((MenuItem)
					 * event.getTarget()).getText().equals(menuItemSortClusterValues.getText())) {
					 * SortRowValuesCommand sortRowValues = (SortRowValuesCommand)
					 * commandFactory.sortRowValues(piece);
					 * App.getCommandManager().invokeCommand(sortRowValues);
					 * App.getCommandManager().invokeCommand(commandFactory.sortColumnValues(
					 * sortRowValues.getPiece())); }
					 */
			}
		});

		menuView.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (((MenuItem) event.getTarget()).getText().equals(menuItemViewGraph.getText())) {
					drawGraph(domino);
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemViewEigenCentrality.getText())) {
					drawCentralityGraph(domino);
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemViewMatrix.getText())) {
					drawMatrix(domino);
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemViewChart.getText())) {
					drawChart(domino);
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemViewLineChart.getText())) {
					drawLineChart(domino);
				}
			}
		});

		menuOperate.getItems().addAll(menuItemAggregateRows, menuItemAggregateColumns, menuItemConfidence,
				menuItemZScore, menuItemTransitiveClosure, menuItemTrim, menuItemBinarize, menuItemInvert);
		menuFilters.getItems().addAll(menuItemDiagonalFilter, menuItemUpperDiagonalFilter, menuItemLowerDiagonalFilter,
				menuItemHighPassFilter, menuItemLowPassFilter, menuItemRowTextFilter, menuItemColumnTextFilter);
		menuView.getItems().addAll(menuItemViewChart, menuItemViewLineChart, menuItemViewEigenCentrality,
				menuItemViewMatrix);
		menuSorting.getItems().addAll(menuItemSortRows, menuItemSortColumns, menuItemSortRowCount,
				menuItemSortColumnCount,
				menuItemSortCluster/*
									 * menuItemSortRowsFirst, menuItemSortColumnsFirst, menuItemSortRowValues,
									 * menuItemSortColumnValues, , menuItemSortClusterValues
									 */);
		minimenu.getItems().addAll(menuView, menuOperate, menuFilters, menuSorting, menuItemSaveInList, menuItemClose);
		this.setVisibleType();
		return piece;
	}

	public void addOnLists(Dominoes domino, Group group, int index) {
		if (index == -1) {
			this.data.getPieces().add(group);
			this.data.getDominoes().add(domino);
		} else {
			this.data.getPieces().add(index, group);
			this.data.getDominoes().add(index, domino);
		}
		this.getChildren().add(group);
	}

	/**
	 * This function is called to change the parts color
	 */
	void changeColor() {
		for (Group group : this.data.getPieces()) {
			((Shape) group.getChildren().get(Dominoes.GRAPH_FILL)).setFill(Dominoes.COLOR_BACK);
			((Shape) group.getChildren().get(Dominoes.GRAPH_LINE)).setFill(Dominoes.COLOR_BORDER);
			((Shape) group.getChildren().get(Dominoes.GRAPH_BORDER)).setFill(Dominoes.COLOR_BORDER);
			((Shape) group.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_NORMAL_FONT);
			((Shape) group.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_NORMAL_FONT);
		}
	}

	/**
	 * This function remove all parts in this Piece Canvas
	 */
	public void clear() {
		if (this.data.getPieces() == null || this.data.getDominoes() == null)
			return;
		for (int i = 0; i < this.data.getPieces().size(); i++) {
			this.data.getPieces().get(i).setVisible(false);
		}
		this.data.getPieces().removeAll(this.data.getPieces());
		this.data.getDominoes().removeAll(this.data.getDominoes());

		// this.pieces = null;
		// this.dominoes = null;
	}

	/**
	 * This function is used to remove a element of this Piece Canvas
	 *
	 * @param group A specified element
	 */
	public void close(Group group) {
		// removing in Piece Canvas
		this.remove(group);
	}

	/**
	 * Just close the piece defined in the parameter
	 *
	 * @param group The piece to will be removed
	 */
	public boolean closePiece(Group group) {
		return remove(group);
	}

	/**
	 * To detect a multiplication will be used the interception between the pieces,
	 * detecting by left or detecting by right has different significates. All
	 * detectiong are ever in relation to index1 (left ou right)
	 *
	 * @param index1 - piece index one
	 * @param index2 - piece index two
	 */
	private boolean checkAndPrepareMultiplication(int index1, int index2) {

		Group g1 = this.data.getPieces().get(index1);
		Group g2 = this.data.getPieces().get(index2);
		Dominoes d1 = this.data.getDominoes().get(index1);
		Dominoes d2 = this.data.getDominoes().get(index2);

		int paddingToCoupling = 1;

		if ((g1.getTranslateX() >= g2.getTranslateX() + Dominoes.GRAPH_WIDTH / 2
				&& g1.getTranslateX() <= g2.getTranslateX() + Dominoes.GRAPH_WIDTH)
				&& (g1.getTranslateY() >= g2.getTranslateY()
						&& g1.getTranslateY() <= g2.getTranslateY() + Dominoes.GRAPH_HEIGHT)) {

			if (d1.getIdRow().equals(d2.getIdCol()) && !d1.getIdRow().contains(Dominoes.AGGREG_TEXT)
					&& d1.getMat().getMatrixDescriptor().getNumRows() == d2.getDescriptor().getNumCols()) {

				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_OPERATE_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_OPERATE_FONT);

				g1.setTranslateX(g2.getTranslateX() + Dominoes.GRAPH_WIDTH - paddingToCoupling);
				g1.setTranslateY(g2.getTranslateY());

				this.data.setCombination(PieceCanvasState.COMBINATION_MULTIPLICATION);
				this.data.setIndexFirstOperatorCombination(index2);
				this.data.setIndexSecondOperatorCombination(index1);

				return true;
			} else {
				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
			}

		} else if ((g1.getTranslateX() >= g2.getTranslateX() + Dominoes.GRAPH_WIDTH / 2
				&& g1.getTranslateX() <= g2.getTranslateX() + Dominoes.GRAPH_WIDTH)
				&& (g1.getTranslateY() + Dominoes.GRAPH_HEIGHT >= g2.getTranslateY()
						&& g1.getTranslateY() + Dominoes.GRAPH_HEIGHT <= g2.getTranslateY() + Dominoes.GRAPH_HEIGHT)) {

			if (d1.getIdRow().equals(d2.getIdCol()) && !d1.getIdRow().contains(Dominoes.AGGREG_TEXT)
					&& d1.getMat().getMatrixDescriptor().getNumRows() == d2.getDescriptor().getNumCols()) {

				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_OPERATE_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_OPERATE_FONT);

				g1.setTranslateX(g2.getTranslateX() + Dominoes.GRAPH_WIDTH - paddingToCoupling);
				g1.setTranslateY(g2.getTranslateY());

				this.data.setCombination(PieceCanvasState.COMBINATION_MULTIPLICATION);
				this.data.setIndexFirstOperatorCombination(index2);
				this.data.setIndexSecondOperatorCombination(index1);

				return true;

			} else {
				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
			}

		} else if ((g1.getTranslateX() + Dominoes.GRAPH_WIDTH >= g2.getTranslateX()
				&& g1.getTranslateX() + Dominoes.GRAPH_WIDTH <= g2.getTranslateX() + Dominoes.GRAPH_WIDTH / 2)
				&& (g1.getTranslateY() >= g2.getTranslateY()
						&& g1.getTranslateY() <= g2.getTranslateY() + Dominoes.GRAPH_HEIGHT)) {

			if (d1.getIdCol().equals(d2.getIdRow()) && !d1.getIdCol().contains(Dominoes.AGGREG_TEXT)
					&& d1.getMat().getMatrixDescriptor().getNumCols() == d2.getDescriptor().getNumRows()) {

				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_OPERATE_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_OPERATE_FONT);

				g1.setTranslateX(g2.getTranslateX() - Dominoes.GRAPH_WIDTH + paddingToCoupling);
				g1.setTranslateY(g2.getTranslateY());

				this.data.setCombination(PieceCanvasState.COMBINATION_MULTIPLICATION);
				this.data.setIndexFirstOperatorCombination(index1);
				this.data.setIndexSecondOperatorCombination(index2);

				return true;
			} else {
				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
			}

		} else if ((g1.getTranslateX() + Dominoes.GRAPH_WIDTH >= g2.getTranslateX()
				&& g1.getTranslateX() + Dominoes.GRAPH_WIDTH <= g2.getTranslateX() + Dominoes.GRAPH_WIDTH / 2)
				&& (g1.getTranslateY() + Dominoes.GRAPH_HEIGHT >= g2.getTranslateY()
						&& g1.getTranslateY() + Dominoes.GRAPH_HEIGHT <= g2.getTranslateY() + Dominoes.GRAPH_HEIGHT)) {

			if (d1.getIdCol().equals(d2.getIdRow()) && !d1.getIdCol().contains(Dominoes.AGGREG_TEXT)
					&& d1.getMat().getMatrixDescriptor().getNumCols() == d2.getDescriptor().getNumRows()) {

				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_OPERATE_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_OPERATE_FONT);

				g1.setTranslateX(g2.getTranslateX() - Dominoes.GRAPH_WIDTH + paddingToCoupling);
				g1.setTranslateY(g2.getTranslateY());

				this.data.setCombination(PieceCanvasState.COMBINATION_MULTIPLICATION);
				this.data.setIndexFirstOperatorCombination(index1);
				this.data.setIndexSecondOperatorCombination(index2);

				return true;
			} else {
				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
			}
		} else {
			this.data.setCombination(-1);
			this.data.setIndexFirstOperatorCombination(-1);
			this.data.setIndexSecondOperatorCombination(-1);
		}

		return false;
	}

	private boolean checkAndPrepareSubtraction(int index1, int index2) {

		Group g1 = this.data.getPieces().get(index1);
		Group g2 = this.data.getPieces().get(index2);
		Dominoes d1 = this.data.getDominoes().get(index1);
		Dominoes d2 = this.data.getDominoes().get(index2);

		int paddingToCoupling = 1;

		if ((g1.getTranslateY() >= g2.getTranslateY() + Dominoes.GRAPH_HEIGHT / 2
				&& g1.getTranslateY() <= g2.getTranslateY() + Dominoes.GRAPH_HEIGHT)
				&& (g1.getTranslateX() >= g2.getTranslateX()
						&& g1.getTranslateX() <= g2.getTranslateX() + Dominoes.GRAPH_WIDTH)) {

			if (d1.getIdRow().equals(d2.getIdRow()) && d1.getIdCol().equals(d2.getIdCol())
					&& d1.getMat().getMatrixDescriptor().getNumRows() == d2.getDescriptor().getNumRows()
					&& d1.getMat().getMatrixDescriptor().getNumCols() == d2.getDescriptor().getNumCols()) {

				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_OPERATE_FONT);
				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_OPERATE_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_OPERATE_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_OPERATE_FONT);

				g1.setTranslateY(g2.getTranslateY() + Dominoes.GRAPH_HEIGHT - paddingToCoupling);
				g1.setTranslateX(g2.getTranslateX());

				this.data.setCombination(PieceCanvasState.COMBINATION_SUBTRACTION);
				this.data.setIndexFirstOperatorCombination(index2);
				this.data.setIndexSecondOperatorCombination(index1);

				return true;
			} else {
				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
			}

		} else if ((g1.getTranslateY() >= g2.getTranslateY() + Dominoes.GRAPH_HEIGHT / 2
				&& g1.getTranslateY() <= g2.getTranslateY() + Dominoes.GRAPH_HEIGHT)
				&& (g1.getTranslateX() + Dominoes.GRAPH_WIDTH >= g2.getTranslateX()
						&& g1.getTranslateX() + Dominoes.GRAPH_WIDTH <= g2.getTranslateX() + Dominoes.GRAPH_WIDTH)) {

			if (d1.getIdRow().equals(d2.getIdRow()) && d1.getIdCol().equals(d2.getIdCol())
					&& d1.getMat().getMatrixDescriptor().getNumRows() == d2.getDescriptor().getNumRows()
					&& d1.getMat().getMatrixDescriptor().getNumCols() == d2.getDescriptor().getNumCols()) {

				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_OPERATE_FONT);
				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_OPERATE_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_OPERATE_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_OPERATE_FONT);

				g1.setTranslateY(g2.getTranslateY() + Dominoes.GRAPH_HEIGHT - paddingToCoupling);
				g1.setTranslateX(g2.getTranslateX());

				this.data.setCombination(PieceCanvasState.COMBINATION_SUBTRACTION);
				this.data.setIndexFirstOperatorCombination(index2);
				this.data.setIndexSecondOperatorCombination(index1);

				return true;

			} else {
				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
			}
		} else {
			this.data.setCombination(-1);
			this.data.setIndexFirstOperatorCombination(-1);
			this.data.setIndexSecondOperatorCombination(-1);
		}

		return false;
	}

	private boolean checkAndPrepareSum(int index1, int index2) {

		Group g1 = this.data.getPieces().get(index1);
		Group g2 = this.data.getPieces().get(index2);
		Dominoes d1 = this.data.getDominoes().get(index1);
		Dominoes d2 = this.data.getDominoes().get(index2);

		int paddingToCoupling = 1;

		if ((g1.getTranslateY() + Dominoes.GRAPH_HEIGHT >= g2.getTranslateY()
				&& g1.getTranslateY() + Dominoes.GRAPH_HEIGHT <= g2.getTranslateY() + Dominoes.GRAPH_HEIGHT / 2)
				&& (g1.getTranslateX() >= g2.getTranslateX()
						&& g1.getTranslateX() <= g2.getTranslateX() + Dominoes.GRAPH_WIDTH)) {

			if (d1.getIdRow().equals(d2.getIdRow()) && d1.getIdCol().equals(d2.getIdCol())
					&& d1.getMat().getMatrixDescriptor().getNumRows() == d2.getDescriptor().getNumRows()
					&& d1.getMat().getMatrixDescriptor().getNumCols() == d2.getDescriptor().getNumCols()) {

				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_OPERATE_FONT);
				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_OPERATE_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_OPERATE_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_OPERATE_FONT);

				g1.setTranslateY(g2.getTranslateY() - Dominoes.GRAPH_HEIGHT + paddingToCoupling);
				g1.setTranslateX(g2.getTranslateX());

				this.data.setCombination(PieceCanvasState.COMBINATION_SUM);
				this.data.setIndexFirstOperatorCombination(index1);
				this.data.setIndexSecondOperatorCombination(index2);

				return true;
			} else {
				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
			}

		} else if ((g1.getTranslateY() + Dominoes.GRAPH_HEIGHT >= g2.getTranslateY()
				&& g1.getTranslateY() + Dominoes.GRAPH_HEIGHT <= g2.getTranslateY() + Dominoes.GRAPH_HEIGHT / 2)
				&& (g1.getTranslateX() + Dominoes.GRAPH_WIDTH >= g2.getTranslateX()
						&& g1.getTranslateX() + Dominoes.GRAPH_WIDTH <= g2.getTranslateX() + Dominoes.GRAPH_WIDTH)) {

			if (d1.getIdRow().equals(d2.getIdRow()) && d1.getIdCol().equals(d2.getIdCol())
					&& d1.getMat().getMatrixDescriptor().getNumRows() == d2.getDescriptor().getNumRows()
					&& d1.getMat().getMatrixDescriptor().getNumCols() == d2.getDescriptor().getNumCols()) {

				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_OPERATE_FONT);
				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_OPERATE_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_OPERATE_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_OPERATE_FONT);

				g1.setTranslateY(g2.getTranslateY() - Dominoes.GRAPH_HEIGHT + paddingToCoupling);
				g1.setTranslateX(g2.getTranslateX());

				this.data.setCombination(PieceCanvasState.COMBINATION_SUM);
				this.data.setIndexFirstOperatorCombination(index1);
				this.data.setIndexSecondOperatorCombination(index2);

				return true;
			} else {
				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);

			}
		} else {
			this.data.setCombination(-1);
			this.data.setIndexFirstOperatorCombination(-1);
			this.data.setIndexSecondOperatorCombination(-1);
		}

		return false;
	}

	/**
	 * This function remove the matrix, in the piece and dominoes array, by the
	 * element
	 *
	 * @param group the element to remove
	 * @return True in affirmative case
	 */
	public boolean remove(Group group) {
		int index = -1;
		index = this.data.getPieces().indexOf(group);
		return remove(index);

	}

	/**
	 * This function remove the matrix, in the piece and dominoes array, by the
	 * index
	 *
	 * @param index the index to remove
	 * @return True in affirmative case
	 */
	public boolean remove(int index) {
		if (index > -1) {
			this.data.getPieces().get(index).setVisible(false);
			this.data.getDominoes().remove(index);
			this.data.getPieces().remove(index);
		}
		return true;
	}

	/**
	 * This function save all piece in AreaMove, remove and create a new matrix in
	 * the List
	 *
	 * @throws IOException
	 */
	public void saveAllAndSendToList() throws IOException {
		for (int i = 0; i < this.data.getDominoes().size(); i++) {

			provdominoes.control.Controller.saveMatrix(this.data.getDominoes().get(i));

			// adding in list
			App.CopyToList(this.data.getDominoes().get(i));

			this.data.getPieces().get(i).setVisible(false);
		}
		this.data.getDominoes().removeAll(this.data.getDominoes());
		this.data.getPieces().removeAll(this.data.getPieces());

	}

	/**
	 * This function save, remove and create a new matrix in the List
	 *
	 * @param group The matrix which will suffer with this operation
	 * @throws IOException
	 */
	public String saveAndSendToList(Group group) throws IOException {
		Dominoes d = this.data.getDominoes().get(this.data.getPieces().indexOf(group));
		d.setRelation(null);
		String id = "PL" + (App.getPieceSelectorList().getAddedPieces() + 1);
		d.setId(id);
		provdominoes.control.Controller.saveMatrix(d);

		// adding in list
		App.CopyToList(this.data.getDominoes().get(this.data.getPieces().indexOf(group)));
		return id;
	}

	public String saveAndSendToList(Group group, Dominoes d) throws IOException {
		d.setRelation(null);
		String id = "PL" + (App.getPieceSelectorList().getAddedPieces() + 1);
		d.setId(id);
		provdominoes.control.Controller.saveMatrix(d);

		// adding in list
		App.CopyToList(d);
		return id;
	}

	/**
	 * This Functions is used to define the moving area size
	 *
	 * @param width
	 * @param height
	 */
	public void setSize(double width, double height) {

		this.data.getBackground().setWidth(width + data.getPadding());
		this.data.getBackground().setHeight(height + 900);

		this.setMinWidth(width);
		this.setPrefWidth(width);
		this.setMaxWidth(width + data.getPadding());
		this.setPrefHeight(height);
	}

	/**
	 * This function is used to define the visibility of historic
	 *
	 * @param visibility True to define visible the historic
	 */
	void setVisibleHistoric() {
		boolean visibility = Configuration.visibilityHistoric;
		for (Group group : data.getPieces()) {
			group.getChildren().get(Dominoes.GRAPH_HISTORIC).setVisible(visibility);
		}

	}

	/**
	 * This function is used to define the visibility of type
	 *
	 * @param visibility True to define visible the type
	 */
	void setVisibleType() {
		boolean visibility = Configuration.visibilityType;
		for (Group group : data.getPieces()) {
			group.getChildren().get(Dominoes.GRAPH_TYPE).setVisible(visibility);
		}

	}

	private void drawGraph(Dominoes domino) {
		App.drawGraph(domino);
	}

	private void drawCentralityGraph(Dominoes domino) {
		App.drawCentralityGraph(domino);
	}

	private void drawMatrix(Dominoes domino) {
		App.drawMatrix(domino);
	}

	private void drawChart(Dominoes domino) {
		App.drawChart(domino);
	}

	private void drawLineChart(Dominoes domino) {
		App.drawLineChart(domino);
	}

	public PieceCanvasState getData() {
		return data;
	}

	public void setData(PieceCanvasState data) {
		this.data = data;
	}

}
