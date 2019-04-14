package command;

import com.josericardojunior.domain.Dominoes;

import boundary.App;
import javafx.scene.Group;

public class RemoveCommand extends AbstractCommand {

	private Group piece;

	public RemoveCommand() {

	}

	public RemoveCommand(Group group) {
		super();
		this.piece = group;
	}

	public Group getPiece() {
		return piece;
	}

	public void setPiece(Group piece) {
		this.piece = piece;
	}

	@Override
	protected boolean doIt() {
		return App.getArea().closePiece(this.piece);
	}

	protected boolean doIt(Dominoes d) {
		int indexPieceToRemove = App.getArea().getData().getDominoes().indexOf(d);
		Group p = App.getArea().getData().getPieces().get(indexPieceToRemove);
		return App.getArea().closePiece(p);
	}

	@Override
	protected boolean undoIt() {
		throw new NoSuchMethodError();
	}

}
