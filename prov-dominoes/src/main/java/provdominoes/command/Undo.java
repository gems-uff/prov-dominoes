package provdominoes.command;

public interface Undo {
	
	public void updateCommandManager(CommandManager cmd, boolean reproducing);

}
