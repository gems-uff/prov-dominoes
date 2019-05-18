package command;

public class UndoCommand extends AbstractCommand implements Undo {
	
	public boolean doIt() {
		throw new NoSuchMethodError();
	}
	
	public boolean undoIt() {
		throw new NoSuchMethodError();
	}

	@Override
	protected String getName() {
		return UNDO_COMMAND;
	}


}
