package provdominoes.command;

import javafx.scene.Group;
import provdominoes.boundary.App;
import provdominoes.domain.Configuration;
import provdominoes.domain.Dominoes;

public class ConfidenceCommand extends AbstractCommand {

	private Group piece;
	private double x;
	private double y;
	private Dominoes oldDominoes;
	private int index;

	public ConfidenceCommand() {
		this.index = -1;
	}

	public ConfidenceCommand(int index) {
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
			Dominoes toConfidence = App.getArea().getData().getDominoes().get(index);
			Dominoes domino = provdominoes.control.Controller.confidence(toConfidence);

			App.getArea().remove(index);
			this.piece = App.getArea().add(domino, piece.getTranslateX(), piece.getTranslateY(), index);

			if (Configuration.autoSave) {
				App.getArea().saveAndSendToList(piece);
			}
		} catch (Exception e) {
			App.alertException(e, "Erro desconhecido ao tentar calcular confiança!");
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
		return CONFIDENCE_COMMAND + "(" + this.oldDominoes.getId() + ")";
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

}