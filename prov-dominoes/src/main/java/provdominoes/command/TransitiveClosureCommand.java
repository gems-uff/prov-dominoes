package provdominoes.command;

import javafx.scene.Group;
import javafx.scene.control.Alert.AlertType;
import provdominoes.boundary.App;
import provdominoes.domain.Configuration;
import provdominoes.domain.Dominoes;

public class TransitiveClosureCommand extends AbstractCommand {

	private String id;
	private Group piece;
	private double x;
	private double y;
	private Dominoes oldDominoes;
	private int index;

	public TransitiveClosureCommand() {
		this.index = -1;
	}

	public TransitiveClosureCommand(int index) {
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
			Dominoes toClosure = App.getArea().getData().getDominoes().get(index);
			if (toClosure.getDescriptor().getNumRows() == toClosure.getDescriptor().getNumCols()) {
				Dominoes domino = provdominoes.control.Controller.transitiveClosure(toClosure);
				domino.setHasTransitiveValues(true);
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
			App.alertException(e, "Unwnown error trying to perform transitive closure!");
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
		return TRANSITIVE_CLOSURE_COMMAND + "(" + this.oldDominoes.getId() + ")";
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
