package command;

import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;

import boundary.App;
import boundary.DominoesMenuBar;
import boundary.HistoricNodeCommand;
import control.ScriptController;

public class CommandManager {

	private LinkedList<AbstractCommand> history;
	private LinkedList<AbstractCommand> redoList;
	private AbstractCommand lastCommand;
	private AbstractCommand previousCommand;
	private DominoesMenuBar menu;
	private ScriptController scriptController;

	public CommandManager() {
		super();
		this.scriptController = new ScriptController();
		this.history = new LinkedList<>();
		this.redoList = new LinkedList<>();
	}

	public CommandManager(DominoesMenuBar menu) {
		this();
		this.menu = menu;
	}

	public void invokeCommand(AbstractCommand command) {
		invokeCommand(command, false);
	}

	public void invokeCommand(AbstractCommand newCommand, boolean reproducing) {
		if (newCommand != null) {
			if (newCommand instanceof Undo) {
				undo();
				generateCommandId(newCommand, reproducing);
			} else if (newCommand instanceof Redo) {
				generateCommandId(newCommand, reproducing);
				redo();
			} else {
				if (newCommand.doIt()) {
					System.out.println(newCommand.getName());
					addToHistory(newCommand);
					generateCommandId(newCommand, reproducing);
				} else {
					history.clear();
				}
			}
			this.uptadeMenu();
			this.previousCommand = this.lastCommand;
			this.lastCommand = newCommand;
		}
	}

	private void generateCommandId(AbstractCommand cmd, boolean reproducing) {
		if (!reproducing) {
			this.scriptController.addToScript(cmd);
			String id = App.getTopPane().addCommand(cmd);
			cmd.setId(id);
		}
	}

	public AbstractCommand getPreviousCommand() {
		return previousCommand;
	}

	public void setPreviousCommand(AbstractCommand previousCommand) {
		this.previousCommand = previousCommand;
	}

	public AbstractCommand getLastCommand() {
		return lastCommand;
	}

	public void setLastCommand(AbstractCommand lastCommand) {
		this.lastCommand = lastCommand;
	}

	public void uptadeMenu() {
		if (history.size() == 0) {
			menu.getEditMenuUndo().setDisable(true);
		} else {
			menu.getEditMenuUndo().setDisable(false);
		}
		if (redoList.size() == 0) {
			menu.getEditMenuRedo().setDisable(true);
		} else {
			menu.getEditMenuRedo().setDisable(false);
		}
	}

	private void addToHistory(AbstractCommand command) {
		this.history.addFirst(command);
	}

	private void undo() {
		if (history.size() > 0) {
			AbstractCommand undoCommand = (AbstractCommand) history.removeFirst();
			undoCommand.undoIt();
			System.out.println("UNDO(" + undoCommand.getName() + ")");
			redoList.addFirst(undoCommand);
		}
	}

	private void redo() {
		if (redoList.size() > 0) {
			AbstractCommand redoCommand = (AbstractCommand) redoList.removeFirst();
			redoCommand.doIt();
			System.out.println("REDO(" + redoCommand.getName() + ")");
			addToHistory(redoCommand);
		}

	}

	public LinkedList<AbstractCommand> getHistory() {
		return history;
	}

	public void setHistory(LinkedList<AbstractCommand> history) {
		this.history = history;
	}

	public LinkedList<AbstractCommand> getRedoList() {
		return redoList;
	}

	public void setRedoList(LinkedList<AbstractCommand> redoList) {
		this.redoList = redoList;
	}

	public void reproduce(LinkedList<AbstractCommand> cmds) throws IOException {
		clear(false);
		for (AbstractCommand cmd : cmds) {
			invokeCommand(cmd, true);
		}
	}

	public void clear(boolean script) throws IOException {
		App.getCommandManager().getRedoList().clear();
		App.getCommandManager().getHistory().clear();
		App.getCommandManager().uptadeMenu();
		if (script) {
			App.getCommandManager().getScriptController().clear();
		}
		App.getArea().clear();
	}

	public ScriptController getScriptController() {
		return scriptController;
	}

	public void setScriptController(ScriptController scriptController) {
		this.scriptController = scriptController;
	}

	public int getScriptFromGraph(StringWriter sw, HistoricNodeCommand root, int undoCount) {
		if (root != null) {
			AbstractCommand cmd = root.getCommand();
			invokeCommand(cmd, true);
			sw.append(cmd.getName() + "\n");
			if (root.getChildren().size() > 0) {
				for (HistoricNodeCommand child : root.getChildren()) {
					if (undoCount > 0) {
						if (undoCount==1) {
							sw.append("UNDO()\n");
						} else {
							sw.append("UNDO(" + undoCount + ")\n");
						}
						undoCount = 0;
					}
					undoCount = getScriptFromGraph(sw, child, undoCount);
					undoCount++;
				}
			}
		}
		return undoCount;
	}

}
