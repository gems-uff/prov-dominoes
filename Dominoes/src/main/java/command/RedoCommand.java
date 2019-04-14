package command;

import javafx.scene.Group;

public class RedoCommand extends AbstractCommand implements Redo {
	
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
