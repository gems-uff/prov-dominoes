package command;

import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;

import boundary.App;
import boundary.DominoesMenuBar;

public class CommandManager {

	private int maxHistoryLength = 100;

	private LinkedList<AbstractCommand> history;
	private LinkedList<AbstractCommand> redoList;
	private AbstractCommand lastCommand;
	private AbstractCommand previousCommand;
	private DominoesMenuBar menu;
	private StringWriter script;

	public CommandManager() {
		super();
		this.script = new StringWriter();
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
				generateCommandId(newCommand, reproducing, false);
			} else if (newCommand instanceof Redo) {
				generateCommandId(newCommand, reproducing, false);
				redo();
			} else {
				if (lastCommand instanceof Undo) {
					this.redoList.clear();
				}
				if (newCommand instanceof MultiplyCommand && !this.history.isEmpty()) {
					this.history.removeFirst();
				}
				if (newCommand.doIt()) {
					System.out.println(newCommand.getName());
					addToHistory(newCommand);
					generateCommandId(newCommand, reproducing, true);
				} else {
					history.clear();
				}
			}
			this.uptadeMenu();
			this.previousCommand = this.lastCommand;
			this.lastCommand = newCommand;
		}
	}

	private void generateCommandId(AbstractCommand newCommand, boolean reproducing, boolean generateScript) {
		if (!reproducing) {
			if (generateScript) {
				addToScript(newCommand.getName());
			}
			String id = App.getTopPane().addCommand(newCommand);
			newCommand.setId(id);
		}
	}

	private void addToScript(String cmd) {
		this.script.append(cmd + "\n");
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
		if (history.size() > maxHistoryLength) {
			history.removeLast();
		}
	}

	private void undo() {
		if (history.size() > 0) {
			AbstractCommand undoCommand = (AbstractCommand) history.removeFirst();
			undoCommand.undoIt();
			System.out.println("UNDO(" + undoCommand.getName() + ")");
			addToScript("UNDO()");
			redoList.addFirst(undoCommand);
		}
	}

	private void redo() {
		if (redoList.size() > 0) {
			AbstractCommand redoCommand = (AbstractCommand) redoList.removeFirst();
			redoCommand.doIt();
			System.out.println("REDO(" + redoCommand.getName() + ")");
			addToScript("REDO()");
			addToHistory(redoCommand);
		}

	}

	public int getMaxHistoryLength() {
		return maxHistoryLength;
	}

	public void setMaxHistoryLength(int maxHistoryLength) {
		this.maxHistoryLength = maxHistoryLength;
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
			App.getCommandManager().clearScript();;
		}
		App.getArea().clear();
	}

	private void clearScript() throws IOException {
		this.script.flush();
		this.script.close();
		this.script = new StringWriter();
		
	}

	public StringWriter getScript() {
		return script;
	}

	public void setScript(StringWriter script) {
		this.script = script;
	}

}
