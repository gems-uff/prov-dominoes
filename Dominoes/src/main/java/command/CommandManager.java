package command;

import java.util.LinkedList;

import boundary.DominoesMenuBar;

public class CommandManager {

	private int maxHistoryLength = 100;

	private LinkedList<AbstractCommand> history;
	private LinkedList<AbstractCommand> redoList;
	private AbstractCommand lastCommand;
	private AbstractCommand lastLastCommand;
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
		if (command instanceof Undo) {
			undo();
		} else if (command instanceof Redo) {
			redo();
		} else {
			if (lastCommand instanceof Undo) {
				this.redoList.clear();
			}
			if (command instanceof MultiplyCommand) {
				this.history.removeFirst();
			} 
			if (command.doIt()) {
				System.out.println(command.getName());
				addToHistory(command);
			} else {
				history.clear();
			}
		}
		this.uptadeMenu();
		this.lastLastCommand = this.lastCommand;
		this.lastCommand = command;
	}

	public AbstractCommand getLastLastCommand() {
		return lastLastCommand;
	}

	public void setLastLastCommand(AbstractCommand lastLastCommand) {
		this.lastLastCommand = lastLastCommand;
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

}
