package provdominoes.command;

import javafx.scene.Group;
import javafx.scene.control.Alert.AlertType;
import provdominoes.boundary.App;
import provdominoes.domain.Configuration;
import provdominoes.domain.Dominoes;

public class UpperTriangularCommand extends AbstractCommand {

	private String id;
	private Group piece;
	private double x;
	private double y;
	private Dominoes oldDominoes;
	private int index;

	public UpperTriangularCommand() {
		this.index = -1;
	}

	public UpperTriangularCommand(int index) {
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
			Dominoes toUpperDiagonal = App.getArea().getData().getDominoes().get(index);
			if (toUpperDiagonal.getMat().getMatrixDescriptor().getNumRows() == toUpperDiagonal.getMat()
					.getMatrixDescriptor().getNumCols()) {
				Dominoes domino = provdominoes.control.Controller.upperDiagonal(toUpperDiagonal);

				App.getArea().remove(index);
				this.piece = App.getArea().add(domino, piece.getTranslateX(), piece.getTranslateY(), index);

				if (Configuration.autoSave) {
					App.getArea().saveAndSendToList(piece);
				}
			} else {
				App.alert(AlertType.WARNING, "Piece Square Requirement", "Square Piece Required!",
						"This command is only possible for square pieces (same faces)!");
				success = false;
			}
		} catch (Exception e) {
			App.alertException(e, "Unknown error trying to filter upper triangular matrix!");
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
		return UPPER_TRIANGULAR_COMMAND + "(" + this.oldDominoes.getId() + ")";
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

}
