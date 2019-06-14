package command;

public class UndoCommand extends AbstractCommand implements Undo {

	private String id;

	public boolean doIt() {
		throw new NoSuchMethodError();
	}

	public boolean undoIt() {
		throw new NoSuchMethodError();
	}

	@Override
	public String getName() {
		return UNDO_COMMAND;
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
