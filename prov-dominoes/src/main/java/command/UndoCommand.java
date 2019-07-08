package command;

public class UndoCommand extends AbstractCommand implements Undo {

	private String id;
	private int count;

	public UndoCommand() {
		super();
		this.count = 1;
	}

	public UndoCommand(int count) {
		this();
		this.count = count;
	}

	public boolean undoIt() {
		throw new NoSuchMethodError();
	}

	public boolean doIt() {
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

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public void updateCommandManager(CommandManager cmd, boolean reproducing) {
		cmd.undo();
		cmd.generateCommandId(this, reproducing);
		
	}

}
