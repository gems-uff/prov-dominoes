package command;

import com.josericardojunior.domain.Dominoes;

import boundary.App;
import domain.Configuration;
import javafx.scene.Group;

public class LowerDiagonalCommand extends AbstractCommand {

	private Group piece;
	private double x;
	private double y;
	private Dominoes oldDominoes;
	private int index;

	public LowerDiagonalCommand() {
		this.index = -1;
	}

	public LowerDiagonalCommand(int index) {
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
			Dominoes toLowerDiagonal = App.getArea().getData().getDominoes().get(index);
			Dominoes domino = control.Controller.lowerDiagonal(toLowerDiagonal);

			App.getArea().remove(index);
			this.piece = App.getArea().add(domino, piece.getTranslateX(), piece.getTranslateY(), index);

			if (Configuration.autoSave) {
				App.getArea().saveAndSendToList(piece);
			}
		} catch (Exception e) {
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
	protected String getName() {
		return LOWER_DIAGONAL_COMMAND + "(" + this.index + "," + this.oldDominoes.getIdRow() + "|"
				+ this.oldDominoes.getIdCol() + ")";
	}

}
