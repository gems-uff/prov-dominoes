package command;

import javafx.scene.Group;

public class UndoCommand extends AbstractCommand implements Undo {
	
	public boolean doIt() {
		throw new NoSuchMethodError();
	}
	
	public boolean undoIt() {
		throw new NoSuchMethodError();
	}

	@Override
	protected Group getPiece() {
		throw new NoSuchMethodError();
	}

}
