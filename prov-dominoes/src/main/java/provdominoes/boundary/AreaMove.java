package provdominoes.boundary;

import java.io.IOException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
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
import provdominoes.command.CommandFactory;
import provdominoes.command.MoveCommand;
import provdominoes.domain.Configuration;
import provdominoes.domain.Dominoes;

public class AreaMove extends Pane {

	private MoveData data;
	private MoveCommand currentMove;
	private CommandFactory commandFactory;

	/**
	 * Class builder with the dimension defined in parameters. here, will create too
	 * a background with white color
	 *
	 */
	public AreaMove() {
		super();
		this.data = new MoveData(-1, -1, Configuration.width, false, new ArrayList<>(), new ArrayList<>());
		this.data.setBackground(new Rectangle());
		this.data.getBackground().setFill(new Color(1, 1, 1, 1));

		this.getChildren().addAll(data.getBackground());

		this.data.setDominoes(new ArrayList<>());
		this.data.setPieces(new ArrayList<>());
		this.commandFactory = new CommandFactory();
	}

	/**
	 * Add a new Domino in Area Move in position x = 0, y = 0
	 *
	 * @param domino The Domino information
	 */
	public Group add(Dominoes domino, int index) {
		return this.add(domino, 0, 0, index);

	}

	/**
	 * Add a new Domino in Area Move in position defined for parameters
	 *
	 * @param domino The Domino information
	 * @param x      The coordinate X of this new Domino
	 * @param y      The coordinate Y of this new Domino
	 */
	public Group add(Dominoes domino, double x, double y, int indexList) {
		double thisTranslateX = x;
		double thisTranslateY = y;

		ContextMenu minimenu = new ContextMenu();

		MenuItem menuItemTranspose = new MenuItem("Transpose");

		MenuItem aggByRow = new MenuItem("Aggregate by " + domino.getDescriptor().getRowType());
		if (!data.getMenuItemAggregateRow().contains(aggByRow)) {
			data.getMenuItemAggregateRow().add(aggByRow);
		}
		MenuItem aggByCol = new MenuItem("Aggregate by " + domino.getDescriptor().getColType());
		if (!data.getMenuItemAggregateCol().contains(aggByCol)) {
			data.getMenuItemAggregateCol().add(aggByCol);
		}
		MenuItem menuItemConfidence = new MenuItem("Confidence");

		MenuItem menuItemZScore = new MenuItem("Z-Score");
		MenuItem menuItemTransitiveClosure = new MenuItem("Transitive Closure");
		MenuItem menuItemSortRows = new MenuItem("Sort Rows Asc");
		MenuItem menuItemSortColumns = new MenuItem("Sort Columns Asc");
		MenuItem menuItemSaveInList = new MenuItem("Save");
		MenuItem menuItemViewGraph = new MenuItem("Graph");
		MenuItem menuItemViewEigenCentrality = new MenuItem("Centrality Graph");
		MenuItem menuItemViewMatrix = new MenuItem("Matrix");
		MenuItem menuItemViewChart = new MenuItem("Bar Chart");
		MenuItem menuItemViewLineChart = new MenuItem("Line Chart");
		MenuItem menuItemClose = new MenuItem("Close");

		MenuItem menuItemBinarizeFilter = new MenuItem("Binarize");
		MenuItem menuItemInvertFilter = new MenuItem("Invert");
		MenuItem menuItemDiagonalFilter = new MenuItem("Diagonalize");
		MenuItem menuItemUpperDiagonalFilter = new MenuItem("Upper Diagonal");
		MenuItem menuItemLowerDiagonalFilter = new MenuItem("Lower Diagonal");
		MenuItem menuItemTrimFilter = new MenuItem("Trim");
		MenuItem menuItemPercentFilter = new MenuItem("Percent");
		MenuItem menuItemRowTextFilter = new MenuItem("Word on Row");
		MenuItem menuItemColumnTextFilter = new MenuItem("Word on Column");

		Menu menuOperate = new Menu("Operations");
		Menu menuFilters = new Menu("Filters");
		Menu menuView = new Menu("Views");

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
				currentMove = CommandFactory.getInstance().move(piece, piece.getTranslateX(), piece.getTranslateY());
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

				// detect multiplication
				int index = data.getPieces().indexOf(piece);

				for (int j = 0; j < data.getPieces().size(); j++) {

					if (index != j && detectMultiplication(index, j)) {
						// menuItemReduceLines.setDisable(false);

						break;
					} else {
						// menuItemReduceLines.setDisable(true);
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

			@Override
			public void handle(MouseEvent event) {
				cursorProperty().set(Cursor.OPEN_HAND);
				if (App.getArea().getData().getIndexFirstOperatorMultiplication() != -1
						&& App.getArea().getData().getIndexSecondOperatorMultiplication() != -1) {
					App.getCommandManager().invokeCommand(commandFactory.multiply());
				} else if (piece != null && currentMove != null && piece.getTranslateX() != currentMove.getOldX()
						&& piece.getTranslateY() != currentMove.getOldY()) {
					MoveCommand move = CommandFactory.getInstance().move(piece, currentMove.getOldX(),
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
		piece.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {

					if (mouseEvent.getClickCount() == 2) {
						if (!data.isTransposing()) {
							App.getCommandManager().invokeCommand(commandFactory.transpose(piece));
						}
					}
				}
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
		int index = data.getDominoes().indexOf(domino);
		menuOperate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (((MenuItem) event.getTarget()).getText().equals(menuItemTranspose.getText())) {
					if (!data.isTransposing()) {
						App.getCommandManager().invokeCommand(commandFactory.transpose(piece));
					}
				} else if (((MenuItem) event.getTarget()).getText()
						.equals(data.getMenuItemAggregateRow().get(index).getText())) {
					App.getCommandManager().invokeCommand(commandFactory.aggLines(piece));
				} else if (((MenuItem) event.getTarget()).getText()
						.equals(data.getMenuItemAggregateCol().get(index).getText())) {
					App.getCommandManager().invokeCommand(commandFactory.aggColumns(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemConfidence.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.confidence(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemZScore.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.zscore(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemTransitiveClosure.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.transitiveClosure(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemSortRows.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.sortRows(piece));
				}  else if (((MenuItem) event.getTarget()).getText().equals(menuItemSortColumns.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.sortColumns(piece));
				}
			}
		});

		menuFilters.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (((MenuItem) event.getTarget()).getText().equals(menuItemBinarizeFilter.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.filterBinarize(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemInvertFilter.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.filterInvert(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemDiagonalFilter.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.filterDiagonal(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemUpperDiagonalFilter.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.filterUpperDiagonal(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemLowerDiagonalFilter.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.filterLowerDiagonal(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemTrimFilter.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.filterTrim(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemPercentFilter.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.filterPercent(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemColumnTextFilter.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.filterColumnText(piece));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemRowTextFilter.getText())) {
					App.getCommandManager().invokeCommand(commandFactory.filterRowText(piece));
				}
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

		menuOperate.getItems().addAll(menuItemTranspose, aggByRow, aggByCol, menuItemConfidence, menuItemZScore,
				menuItemTransitiveClosure, menuItemSortRows, menuItemSortColumns);
		menuFilters.getItems().addAll(menuItemBinarizeFilter, menuItemInvertFilter, menuItemDiagonalFilter,
				menuItemUpperDiagonalFilter, menuItemLowerDiagonalFilter, menuItemTrimFilter, menuItemPercentFilter,
				menuItemRowTextFilter, menuItemColumnTextFilter);
		menuView.getItems().addAll(menuItemViewChart, menuItemViewLineChart, menuItemViewGraph,
				menuItemViewEigenCentrality, menuItemViewMatrix);
		minimenu.getItems().addAll(menuOperate, menuFilters, menuView, menuItemSaveInList, menuItemClose);
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
	 * This function remove all parts in this area move
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
	 * This function is used to remove a element of this Area Move
	 *
	 * @param group A specified element
	 */
	public void close(Group group) {
		// removing in area move
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
	private boolean detectMultiplication(int index1, int index2) {

		Group g1 = this.data.getPieces().get(index1);
		Group g2 = this.data.getPieces().get(index2);
		Dominoes d1 = this.data.getDominoes().get(index1);
		Dominoes d2 = this.data.getDominoes().get(index2);

		int paddingToCoupling = 1;

		boolean detect = false;

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

				this.data.setIndexFirstOperatorMultiplication(index2);
				this.data.setIndexSecondOperatorMultiplication(index1);

				return true;
			} else {
				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
				detect = true;
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

				this.data.setIndexFirstOperatorMultiplication(index2);
				this.data.setIndexSecondOperatorMultiplication(index1);

				return true;

			} else {
				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
				detect = true;
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

				this.data.setIndexFirstOperatorMultiplication(index1);
				this.data.setIndexSecondOperatorMultiplication(index2);

				return true;
			} else {
				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
				detect = true;
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

				this.data.setIndexFirstOperatorMultiplication(index1);
				this.data.setIndexSecondOperatorMultiplication(index2);

				return true;
			} else {
				((Text) g1.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);
				((Text) g2.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_NO_OPERATION_FONT);

				detect = true;
			}
		}

		if (!detect) {
			((Text) g1.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_NORMAL_FONT);
			((Text) g1.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_NORMAL_FONT);
			((Text) g2.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFill(Dominoes.COLOR_NORMAL_FONT);
			((Text) g2.getChildren().get(Dominoes.GRAPH_ID_COL)).setFill(Dominoes.COLOR_NORMAL_FONT);

			this.data.setIndexFirstOperatorMultiplication(-1);
			this.data.setIndexSecondOperatorMultiplication(-1);
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
			this.data.getMenuItemAggregateRow().remove(index);
			this.data.getMenuItemAggregateCol().remove(index);
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
	public void saveAndSendToList(Group group) throws IOException {
		provdominoes.control.Controller.saveMatrix(this.data.getDominoes().get(this.data.getPieces().indexOf(group)));

		// adding in list
		App.CopyToList(this.data.getDominoes().get(this.data.getPieces().indexOf(group)));
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

	public MoveData getData() {
		return data;
	}

	public void setData(MoveData data) {
		this.data = data;
	}

}
