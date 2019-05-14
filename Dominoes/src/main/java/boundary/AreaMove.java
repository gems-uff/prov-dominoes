package boundary;

import java.io.IOException;
import java.util.ArrayList;

import com.josericardojunior.domain.Dominoes;

import command.CommandFactory;
import domain.Configuration;
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
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class AreaMove extends Pane {

	private MoveData data;

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
	}

	/**
	 * Add a new Domino in Area Move in position x = 0, y = 0
	 *
	 * @param domino The Domino information
	 */
	public Group add(Dominoes domino,int index) {
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

		data.getMenuItemAggregateRow()
				.add(new MenuItem("Aggregate by " + domino.getMat().getMatrixDescriptor().getRowType()));
		data.getMenuItemAggregateCol()
				.add(new MenuItem("Aggregate by " + domino.getMat().getMatrixDescriptor().getColType()));
		MenuItem menuItemConfidence = new MenuItem("Confidence");

		MenuItem menuItemZScore = new MenuItem("Z-Score");
		MenuItem menuItemSaveInList = new MenuItem("Save");
		MenuItem menuItemViewGraph = new MenuItem("Graph");
		MenuItem menuItemViewMatrix = new MenuItem("Matrix");
		MenuItem menuItemViewChart = new MenuItem("Bar Chart");
		MenuItem menuItemViewLineChart = new MenuItem("Line Chart");
		MenuItem menuItemViewTree = new MenuItem("Tree");
		MenuItem menuItemClose = new MenuItem("Close");

		Menu menuOperate = new Menu("Operations");
		Menu menuView = new Menu("Views");

		Group group = domino.drawDominoes();
		group.getChildren().get(Dominoes.GRAPH_HISTORIC).setVisible(Configuration.visibilityHistoric);

		group.setTranslateX(thisTranslateX);
		group.setTranslateY(thisTranslateY);
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
			group.setTranslateY(thisTranslateY);
			group.setTranslateX(thisTranslateX);
		}

		addOnLists(domino, group, indexList);

		// if (!domino.getIdRow().equals(domino.getIdCol())) {
		// menuItemViewGraph.setDisable(true);
		// }

		group.setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				cursorProperty().set(Cursor.OPEN_HAND);
			}
		});
		group.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				double offsetX = event.getSceneX() - data.getSrcSceneX();
				double offsetY = event.getSceneY() - data.getSrcSceneY();
				double newTranslateX = data.getSrcTranslateX() + offsetX;
				double newTranslateY = data.getSrcTranslateY() + offsetY;

				// detect move out
				boolean detecMoveOutX = false;
				boolean detecMoveOutY = false;
				if (newTranslateX < data.getBackground().getX()) {
					((Group) (event.getSource())).setTranslateX(data.getBackground().getX());

					detecMoveOutX = true;
				}
				if (newTranslateY < data.getBackground().getY()) {
					((Group) (event.getSource())).setTranslateY(data.getBackground().getY());
					detecMoveOutY = true;
				}
				if (newTranslateX + ((Group) (event.getSource())).prefWidth(-1) > data.getBackground().getX()
						+ data.getBackground().getWidth()) {
					((Group) (event.getSource())).setTranslateX(data.getBackground().getX()
							+ data.getBackground().getWidth() - ((Group) (event.getSource())).prefWidth(-1));
					detecMoveOutX = true;
				}
				if (newTranslateY + ((Group) (event.getSource())).prefHeight(-1) > data.getBackground().getY()
						+ data.getBackground().getHeight()) {
					((Group) (event.getSource())).setTranslateY(data.getBackground().getY()
							+ data.getBackground().getHeight() - ((Group) (event.getSource())).prefHeight(-1));
					detecMoveOutY = true;
				}

				if (!detecMoveOutX) {
					((Group) (event.getSource())).setTranslateX(newTranslateX);
				}
				if (!detecMoveOutY) {
					((Group) (event.getSource())).setTranslateY(newTranslateY);
				}

				// detect multiplication
				int index = data.getPieces().indexOf(group);

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
		group.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				data.setSrcSceneX(event.getSceneX());
				data.setSrcSceneY(event.getSceneY());
				data.setSrcTranslateX(((Group) (event.getSource())).getTranslateX());
				data.setSrcTranslateY(((Group) (event.getSource())).getTranslateY());

				group.toFront();
				cursorProperty().set(Cursor.CLOSED_HAND);
			}
		});
		group.setOnMouseReleased(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				cursorProperty().set(Cursor.OPEN_HAND);
				App.getCommandManager().invokeCommand(new CommandFactory().multiply());
			}
		});
		group.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				cursorProperty().set(Cursor.DEFAULT);
			}
		});
		group.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {

					if (mouseEvent.getClickCount() == 2) {
						if (!data.isTransposing()) {
							System.out.println("transposing");
							App.getCommandManager().invokeCommand(new CommandFactory().transpose(group));
						}
					}
				}
			}
		});
		group.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent e) {
				if (e.getButton() == MouseButton.SECONDARY) {
					minimenu.show(group, e.getScreenX(), e.getScreenY());
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
					System.out.println("saving");
					try {
						saveAndSendToList(group);
						close(group);
					} catch (IOException ex) {
						System.out.println(ex.getMessage());
					}
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemClose.getText())) {
					System.out.println("closing");
					App.getCommandManager().invokeCommand(new CommandFactory().remove(group));
				}
			}
		});
		int index = data.getDominoes().indexOf(domino);
		menuOperate.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (((MenuItem) event.getTarget()).getText().equals(menuItemTranspose.getText())) {
					if (!data.isTransposing()) {
						System.out.println("transposing");
						App.getCommandManager().invokeCommand(new CommandFactory().transpose(group));
					}
				} else if (((MenuItem) event.getTarget()).getText()
						.equals(data.getMenuItemAggregateRow().get(index).getText())) {
					try {
						reduceColumns(group);
					} catch (IOException ex) {
						System.err.println(ex.getMessage());
					}

				} else if (((MenuItem) event.getTarget()).getText()
						.equals(data.getMenuItemAggregateCol().get(index).getText())) {
					try {
						reduceLines(group);
					} catch (IOException ex) {
						System.err.println(ex.getMessage());
					}

				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemConfidence.getText())) {
					try {
						confidence(group);
					} catch (IOException e) {
						System.err.println(e.getMessage());
					}
				}
			}
		});
		menuView.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				if (((MenuItem) event.getTarget()).getText().equals(menuItemViewGraph.getText())) {
					drawGraph(domino);
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemViewMatrix.getText())) {
					drawMatrix(domino);
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemViewChart.getText())) {
					drawChart(domino);
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemViewTree.getText())) {
					drawTree(domino);
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemViewLineChart.getText())) {
					drawLineChart(domino);
				}
			}
		});

		menuOperate.getItems().addAll(menuItemTranspose, data.getMenuItemAggregateRow().get(index),
				data.getMenuItemAggregateCol().get(index), menuItemConfidence, menuItemZScore);
		menuView.getItems().addAll(menuItemViewChart, /* menuItemViewLineChart, */
				menuItemViewGraph, menuItemViewMatrix/* , menuItemViewTree */);
		minimenu.getItems().addAll(menuOperate, menuView, menuItemSaveInList, menuItemClose);
		return group;
	}

	private void addOnLists(Dominoes domino, Group group, int index) {
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
	private void close(Group group) {
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

			if (d1.getIdRow().equals(d2.getIdCol()) && !d1.getIdRow().contains(Dominoes.AGGREG_TEXT) && d1.getMat()
					.getMatrixDescriptor().getNumRows() == d2.getMat().getMatrixDescriptor().getNumCols()) {

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

			if (d1.getIdRow().equals(d2.getIdCol()) && !d1.getIdRow().contains(Dominoes.AGGREG_TEXT) && d1.getMat()
					.getMatrixDescriptor().getNumRows() == d2.getMat().getMatrixDescriptor().getNumCols()) {

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

			if (d1.getIdCol().equals(d2.getIdRow()) && !d1.getIdCol().contains(Dominoes.AGGREG_TEXT) && d1.getMat()
					.getMatrixDescriptor().getNumCols() == d2.getMat().getMatrixDescriptor().getNumRows()) {

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

			if (d1.getIdCol().equals(d2.getIdRow()) && !d1.getIdCol().contains(Dominoes.AGGREG_TEXT) && d1.getMat()
					.getMatrixDescriptor().getNumCols() == d2.getMat().getMatrixDescriptor().getNumRows()) {

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

			control.Controller.saveMatrix(this.data.getDominoes().get(i));

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
		control.Controller.saveMatrix(this.data.getDominoes().get(this.data.getPieces().indexOf(group)));

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
		this.data.getBackground().setHeight(height);

		this.setMinWidth(width - data.getPadding());
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

	/**
	 * This function is responsible for summing up all lines in a column
	 *
	 * @param piece The piece to animate
	 */
	private void reduceLines(Group piece) throws IOException {

		int index = this.data.getPieces().indexOf(piece);
		Dominoes toReduce = this.data.getDominoes().get(index);
		if (!toReduce.isRowAggregatable()) {
			Dominoes domino = control.Controller.reduceDominoes(toReduce);
			this.data.getDominoes().set(index, domino);

			((Text) piece.getChildren().get(Dominoes.GRAPH_ID_ROW)).setText(domino.getIdRow());
			((Text) piece.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFont(new Font(Dominoes.GRAPH_AGGREG_FONT_SIZE));

			if (Configuration.autoSave) {
				this.saveAndSendToList(piece);
			}

			this.data.getMenuItemAggregateRow().get(index).setDisable(true);

		} else {
			System.err.println(
					"this domino is already aggregate by " + toReduce.getMat().getMatrixDescriptor().getRowType());
		}

	}

	/**
	 * This function is responsible for summing up all columns in a line
	 *
	 * @param piece The piece to animate
	 */
	private void reduceColumns(Group piece) throws IOException {

		int index = this.data.getPieces().indexOf(piece);
		Dominoes toReduce = this.data.getDominoes().get(index);

		if (!toReduce.isColAggregatable()) {
			toReduce.transpose();
			Dominoes domino = control.Controller.reduceDominoes(toReduce);
			domino.transpose();
			this.data.getDominoes().set(index, domino);

			domino.drawDominoes();

			((Text) piece.getChildren().get(Dominoes.GRAPH_ID_COL)).setText(domino.getIdCol());
			((Text) piece.getChildren().get(Dominoes.GRAPH_ID_COL)).setFont(new Font(Dominoes.GRAPH_AGGREG_FONT_SIZE));

			if (Configuration.autoSave) {
				this.saveAndSendToList(piece);
			}

			this.data.getMenuItemAggregateCol().get(index).setDisable(true);
		} else {
			System.err.println(
					"this domino is already aggregate by " + toReduce.getMat().getMatrixDescriptor().getColType());
		}

	}

	/**
	 * This function is responsible for calculating the confidence on a matrix
	 *
	 * @param piece The piece to animate
	 * @throws IOException
	 */
	private void confidence(Group piece) throws IOException {

		int index = this.data.getPieces().indexOf(piece);
		Dominoes toConfidence = this.data.getDominoes().get(index);
		Dominoes domino = control.Controller.confidence(toConfidence);

		remove(index);
		add(domino, piece.getTranslateX(), piece.getTranslateY(), -1);

		if (Configuration.autoSave) {
			this.saveAndSendToList(piece);
		}

	}

	private void drawGraph(Dominoes domino) {
		App.drawGraph(domino);
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

	private void drawTree(Dominoes domino) {
		App.drawTree(domino);
	}

	public MoveData getData() {
		return data;
	}

	public void setData(MoveData data) {
		this.data = data;
	}

}
