package command;

public abstract class AbstractCommand {

	public static final String UNDO_COMMAND = "UNDO";
	public static final String REDO_COMMAND = "REDO";
	public static final String TRANSPOSE_COMMAND = "TRANSPOSE";
	public static final String MULTIPLY_COMMAND = "MULTIPLY";
	public static final String ADD_COMMAND = "ADD";
	public static final String REMOVE_COMMAND = "REMOVE";

	protected abstract String getName();

	protected abstract boolean doIt();

	protected abstract boolean undoIt();

}
