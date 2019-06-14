package command;

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

	public CommandManager() {
		super();
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
		if (newCommand instanceof Undo) {
			undo();
		} else if (newCommand instanceof Redo) {
			redo();
		} else {
			if (lastCommand instanceof Undo) {
				this.redoList.clear();
			}
			if (newCommand instanceof MultiplyCommand) {
				this.history.removeFirst();
			}
			if (newCommand.doIt()) {
				System.out.println(newCommand.getName());
				addToHistory(newCommand);
			} else {
				history.clear();
			}
		}
		this.uptadeMenu();
		this.previousCommand = this.lastCommand;
		this.lastCommand = newCommand;
		if (!reproducing) {
			String id = App.getTopPane().addCommand(newCommand);
			this.lastCommand.setId(id);
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
			menu.getMenuEditUndo().setDisable(true);
		} else {
			menu.getMenuEditUndo().setDisable(false);
		}
		if (redoList.size() == 0) {
			menu.getMenuEditRedo().setDisable(true);
		} else {
			menu.getMenuEditRedo().setDisable(false);
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
			System.out.println("UNDO[" + undoCommand.getName() + "]");
			undoCommand.undoIt();
			redoList.addFirst(undoCommand);
		}
	}

	private void redo() {
		if (redoList.size() > 0) {
			AbstractCommand redoCommand = (AbstractCommand) redoList.removeFirst();
			System.out.println("REDO[" + redoCommand.getName() + "]");
			redoCommand.doIt();
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

	public void reproduce(LinkedList<AbstractCommand> cmds) {
		App.getCommandManager().getRedoList().clear();
		App.getCommandManager().getHistory().clear();
		App.getCommandManager().uptadeMenu();
		App.getArea().clear();
		for (AbstractCommand cmd : cmds) {
			invokeCommand(cmd, true);
		}
	}

}
