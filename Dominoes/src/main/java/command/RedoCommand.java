package command;

public class RedoCommand extends AbstractCommand implements Redo {
	
	public boolean doIt() {
		throw new NoSuchMethodError();
	}
	
	public boolean undoIt() {
		throw new NoSuchMethodError();
	}

	@Override
	protected String getName() {
	return REDO_COMMAND;
	}

}
