package provdominoes.command;

import javafx.scene.Group;
import javafx.scene.text.Text;
import provdominoes.boundary.App;
import provdominoes.domain.Configuration;
import provdominoes.domain.Dominoes;

public class SortColumnValuesCommand extends AbstractCommand {

	private Group piece;
	private double x;
	private double y;
	private Dominoes oldDominoes;
	private int index;

	public SortColumnValuesCommand() {
		this.index = -1;
	}

	public SortColumnValuesCommand(int index) {
		this();
		this.index = index;
	}

	@Override
	protected boolean doIt() {
		boolean success = true;
		this.oldDominoes = App.getArea().getData().getDominoes().get(index).cloneNoMatrix();
		this.piece = App.getArea().getData().getPieces().get(index);
		x = this.piece.getTranslateX();
		y = this.piece.getTranslateY();
		try {
			Dominoes toSortColumn = App.getArea().getData().getDominoes().get(index);
			toSortColumn.transpose();
			Dominoes domino = provdominoes.control.Controller.sortDefaultDimensionValues(toSortColumn);
			domino.transpose();
			domino.setType(Dominoes.TYPE_SORT_COLUMN_VALUES);

			App.getArea().getData().getDominoes().set(index, domino);

			domino.drawDominoes();
			if (piece.getChildren().get(Dominoes.GRAPH_TYPE) instanceof Group) {
				Group g = (Group) piece.getChildren().get(Dominoes.GRAPH_TYPE);
				g.setTranslateX(70);
				((Text) g.getChildren().get(1)).setText(Dominoes.TYPE_SORT_COLUMN_VALUES_CODE);
			} else if (piece.getChildren().get(Dominoes.GRAPH_TYPE) instanceof Text) {
				((Text) piece.getChildren().get(Dominoes.GRAPH_TYPE)).setText(Dominoes.TYPE_SORT_COLUMN_VALUES_CODE);
			}
			if (Configuration.autoSave) {
				App.getArea().saveAndSendToList(piece);
			}
		} catch (Exception e) {
			App.alertException(e, "Error trying to sort by column values!");
			e.printStackTrace();
			success = false;
		}

		return success;
	}

	@Override
	protected boolean undoIt() {
		boolean result = false;
		Group p = App.getArea().getData().getPieces().get(this.index);
		App.getArea().closePiece(p);
		App.getArea().add(this.oldDominoes, x, y, this.index);
		result = true;
		return result;
	}

	@Override
	public String getName() {
		return SORT_COLUMN_VALUES_COMMAND + "(" + this.oldDominoes.getId() + ")";
	}

	private String id;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public Group getPiece() {
		return piece;
	}

	public void setPiece(Group piece) {
		this.piece = piece;
	}

}
