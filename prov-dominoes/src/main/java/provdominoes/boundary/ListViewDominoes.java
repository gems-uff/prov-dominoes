package provdominoes.boundary;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Shape;
import model.ProvRelation.Relation;
import provdominoes.command.CommandFactory;
import provdominoes.domain.Configuration;
import provdominoes.domain.Dominoes;

public class ListViewDominoes extends ListView<Group> {

	private ObservableList<Group> pieces;
	private ArrayList<Dominoes> dominoes;
	private int addedPieces;

	private boolean visibilityHistoric;

	/**
	 * This class builder initialize this list and your arrays with values defined
	 * in the parameter Array.
	 *
	 * @param array Values to initialize this list and your array
	 */
	public ListViewDominoes(ArrayList<Dominoes> array) {
		this.visibilityHistoric = true;

		this.pieces = FXCollections.observableList(new ArrayList<Group>());
		this.dominoes = new ArrayList<>();

		this.configure(array);
	}

	public void configure(List<Dominoes> array) {
		this.clear();

		if (array != null) {
			for (Dominoes dom : array) {
				// if (!dom.getMat().isEmpty()) {
				this.add(dom);
				// }
			}

		}

		this.setItems(this.pieces);
	}

	/**
	 * This function adds a Dominoes in the list
	 *
	 * @param domino The dominoes to resultMultiplication
	 * @return true in affirmative case
	 * @throws IllegalArgumentException
	 */
	public boolean add(Dominoes domino) throws IllegalArgumentException {

		boolean result = false;

		if (domino == null) {
			return result;
		}

		if (isAdded(domino)) {
			return result;
		}

		ContextMenu minimenu = new ContextMenu();
		MenuItem menuItemToAreaMove = new MenuItem("Copy To Piece Canvas");
		MenuItem menuItemRemove = new MenuItem("Remove");

		Group group = domino.drawDominoes();
		group.getChildren().get(Dominoes.GRAPH_HISTORIC).setVisible(visibilityHistoric);

		Tooltip tooltip = new Tooltip(domino.getDescriptor().getRowType() + " x " + domino.getDescriptor().getColType()
				+ " : " + domino.getDescriptor().getNumRows() + " x " + domino.getDescriptor().getNumCols());
		Tooltip.install(group, tooltip);

		group.setOnMouseEntered(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				cursorProperty().set(Cursor.OPEN_HAND);
			}
		});
		group.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				cursorProperty().set(Cursor.CLOSED_HAND);
			}
		});
		group.setOnMouseReleased(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (cursorProperty().get() == Cursor.CLOSED_HAND) {

					int indexTargetRelative = (event.getY() < 0)
							? (int) ((event.getY() + (-1) * (Dominoes.GRAPH_HEIGHT + 6)) / (Dominoes.GRAPH_HEIGHT + 6))
							: (int) (event.getY() / (Dominoes.GRAPH_HEIGHT + 6));

					if (/* this. */pieces == null) {
						return;
					}

					int indexSource = getSelectionModel().getSelectedIndex();

					moveItems(indexSource, indexTargetRelative);
				}
				cursorProperty().set(Cursor.OPEN_HAND);
			}
		});
		group.setOnMouseExited(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				if (cursorProperty().get() == Cursor.CLOSED_HAND) {

				} else {
					cursorProperty().set(Cursor.DEFAULT);
				}
			}
		});

		group.setOnMouseClicked(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {
				if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {
					if (mouseEvent.getClickCount() == 2) {
						int index = pieces.indexOf(group);
						Dominoes d = dominoes.get(index);
						String id = d.getId();
						if (d.getRelation() != null) {
							id = d.getRelation().getAbbreviate().replace(" ", "");
							if (d.getRelation() == Relation.RELATION_INFLUENCE) {
								id = id + "[" + d.getIdRow() + "," + d.getIdCol() + "]";
							}
						}
						App.getCommandManager().invokeCommand(new CommandFactory().add(id));
					}
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
				if (((MenuItem) event.getTarget()).getText().equals(menuItemToAreaMove.getText())) {
					int index = pieces.indexOf(group);
					Dominoes d = dominoes.get(index);
					String id = d.getId();
					if (d.getRelation() != null) {
						id = d.getRelation().getAbbreviate().replace(" ", "");
					}
					App.getCommandManager().invokeCommand(new CommandFactory().add(id));
				} else if (((MenuItem) event.getTarget()).getText().equals(menuItemRemove.getText())) {
					System.out.println("removing from piece list: "+ domino.getId());
					try {
						removeFromListAndArea(group);
					} catch (IOException ex) {
						App.alertException(ex, ex.getMessage());
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

		minimenu.getItems().addAll(menuItemToAreaMove, menuItemRemove);

		this.dominoes.add(domino);

		this.pieces.add(group);

		result = true;
		this.addedPieces++;
		return result;
	}

	/**
	 * This function checks if a domino parameters is added in the list
	 *
	 * @param domino The domino to check
	 * @return true, in affirmative case
	 * @throws IllegalArgumentException
	 */
	private boolean isAdded(Dominoes domino) throws IllegalArgumentException {
		if (domino == null) {
			throw new IllegalArgumentException("list not initialized");
		}
		for (Dominoes d : this.dominoes) {
			if ((d.getIdRow().equals(domino.getIdRow()) && d.getIdCol().equals(domino.getIdCol())
					&& domino.getHistoric().toString().equals(d.getHistoric().toString())
					&& domino.getRelation() == d.getRelation())
					&& domino.getId() .equals(d.getId())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * This function is called to change the parts color
	 */
	void changeColor() {
		for (Group group : this.pieces) {
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
		for (int i = 0; i < this.pieces.size(); i++) {
			this.pieces.get(i).setVisible(false);
		}
		this.pieces.removeAll(this.pieces);
		this.dominoes.removeAll(this.dominoes);
		this.addedPieces=0;
	}

	/**
	 * This function is used to move a selected domino in the list.
	 *
	 * @param indexSource         The selected index. The dominoes in this position
	 *                            will suffer a change in their position
	 * @param indexTargetRelative The position target.
	 */
	public void moveItems(int indexSource, int indexTargetRelative) {
		int indexTarget = indexSource + indexTargetRelative;
//        int indexTarget = indexTargetRelative;

		// catch index selected
		if (this.pieces == null || this.dominoes == null) {
			return;
		}

		if ((indexTarget < 0 || indexTarget >= this.pieces.size())
				|| (indexSource < 0 || indexSource >= this.pieces.size()) || (indexSource == indexTarget)) {
			return;
		}

		if (indexTarget > indexSource) {
			Group sourceGroup = new Group();
			sourceGroup = this.pieces.get(indexSource);
			Dominoes sourceDominoes = new Dominoes(
					(Configuration.isGPUProcessing() ? Configuration.GPU_PROCESSING : Configuration.CPU_PROCESSING));
			sourceDominoes = this.dominoes.get(indexSource);

			for (int i = indexSource; i < indexTarget; i++) {
				this.pieces.set(i, this.pieces.get(i + 1));
				this.dominoes.set(i, this.dominoes.get(i + 1));
			}

			this.pieces.set(indexTarget, sourceGroup);
			this.dominoes.set(indexTarget, sourceDominoes);

		} else if (indexTarget < indexSource) {
			Group sourceGroup = new Group();
			sourceGroup = this.pieces.get(indexSource);
			Dominoes sourceDominoes = new Dominoes(
					(Configuration.isGPUProcessing() ? Configuration.GPU_PROCESSING : Configuration.CPU_PROCESSING));
			sourceDominoes = this.dominoes.get(indexSource);

			for (int i = indexSource; i > indexTarget; i--) {
				this.pieces.set(i, this.pieces.get(i - 1));
				this.dominoes.set(i, this.dominoes.get(i - 1));
			}

			this.pieces.set(indexTarget, sourceGroup);
			this.dominoes.set(indexTarget, sourceDominoes);
		}
	}

	/**
	 * This Functions is used to define the moving area size
	 *
	 * @param width
	 * @param height
	 */
	void setSize(double width, double height) {
		this.setMinWidth(width);
		this.setPrefWidth(width);
		this.setMaxWidth(width);
		this.setPrefHeight(height);
	}

	/**
	 * This function is used to define the visibility of historic
	 *
	 * @param visibility True to define visible the historic
	 */
	void setVisibleHistoric() {
		boolean visibility = this.visibilityHistoric;
		for (Group group : pieces) {
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
		for (Group group : pieces) {
			group.getChildren().get(Dominoes.GRAPH_TYPE).setVisible(visibility);
		}
	}

	/**
	 * This function remove only a element this list.
	 *
	 * @param group element to remove
	 * @return true in affimative case
	 */
	public boolean remove(Group group) {
		int index = this.pieces.indexOf(group);
		if (index > -1) {
			group.setVisible(false);
			this.dominoes.remove(index);
			this.pieces.remove(index);
			return true;
		}
		return false;
	}
	
	public boolean remove(Dominoes d) {
		int index = this.dominoes.indexOf(d);
		Group group = pieces.get(index);
		if (index > -1) {
			group.setVisible(false);
			this.dominoes.remove(index);
			this.pieces.remove(index);
			return true;
		}
		return false;
	}

	/**
	 * This function remove the element of the list and of the move area
	 *
	 * @param group Element to remove
	 * @return true, in affirmative case
	 * @throws IOException
	 */
	private boolean removeFromListAndArea(Group group) throws IOException {
		return App.removeMatrix(this.dominoes.get(pieces.indexOf(group)), group);
	}

	public ObservableList<Group> getPieces() {
		return pieces;
	}

	public void setPieces(ObservableList<Group> pieces) {
		this.pieces = pieces;
	}

	public ArrayList<Dominoes> getDominoes() {
		return dominoes;
	}

	public void setDominoes(ArrayList<Dominoes> dominoes) {
		this.dominoes = dominoes;
	}

	public int getAddedPieces() {
		return addedPieces;
	}

	public void setAddedPieces(int addedPieces) {
		this.addedPieces = addedPieces;
	}

}
