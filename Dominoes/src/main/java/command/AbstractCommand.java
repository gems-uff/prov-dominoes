package command;

import javafx.scene.Group;

public abstract class AbstractCommand {

	public static final String TRANSPOSE_COMMAND = "TRANSPOSE";
	public static final String ADD_COMMAND = "ADD";
	public static final String REMOVE_COMMAND = "REMOVE";

	protected abstract Group getPiece();

	protected abstract boolean doIt();

	protected abstract boolean undoIt();

}
