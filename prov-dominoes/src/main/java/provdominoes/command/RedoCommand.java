package provdominoes.command;

public class RedoCommand extends AbstractCommand implements Redo {
	
	private String id;
	
	public boolean doIt() {
		throw new NoSuchMethodError();
	}
	
	public boolean undoIt() {
		throw new NoSuchMethodError();
	}

	@Override
	public String getName() {
	return REDO_COMMAND;
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	public void updateCommandManager(CommandManager cmd, boolean reproducing, boolean scripting) {
		cmd.generateCommandId(this, reproducing, scripting);
		cmd.redo();
	}

}
