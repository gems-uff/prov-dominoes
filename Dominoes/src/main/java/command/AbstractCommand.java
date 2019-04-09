package command;

public abstract class AbstractCommand {
	
	public static final String TRANSPOSE_COMMAND = "TRANSPOSE";

	protected abstract boolean doIt();
	
	protected abstract boolean undoIt();

}
