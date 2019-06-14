package command;

import com.josericardojunior.domain.Dominoes;

import boundary.App;
import domain.Configuration;
import javafx.scene.Group;

public class ZScoreCommand extends AbstractCommand {

	private String id;
	private Group piece;
	private Dominoes oldDominoes;
	private double x;
	private double y;
	private int index;

	public ZScoreCommand() {
		this.index = -1;
	}

	public ZScoreCommand(int index) {
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
			Dominoes toStandardScore = App.getArea().getData().getDominoes().get(index);
			Dominoes domino = control.Controller.standardScore(toStandardScore);

			App.getArea().remove(index);
			App.getArea().add(domino, piece.getTranslateX(), piece.getTranslateY(), index);

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
	public String getName() {
		return ZSCORE_COMMAND + "(" + "," + this.oldDominoes.getId() + ")";
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
