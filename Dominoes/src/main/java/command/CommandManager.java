package command;

import java.util.LinkedList;

public class CommandManager {

	private int maxHistoryLength = 100;

	private LinkedList<AbstractCommand> history;
	private LinkedList<AbstractCommand> redoList;

	public CommandManager() {
		super();
		this.history = new LinkedList<>();
		this.redoList = new LinkedList<>();
	}

	public void invokeCommand(AbstractCommand command) {
		if (command instanceof Undo) {
			undo();
			return;
		}
		if (command instanceof Redo) {
			redo();
			return;
		}
		if (command.doIt()) {
			addToHistory(command);
		} else {
			history.clear();
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
			redoList.addFirst(undoCommand);
		}
	}

	private void redo() {
		if (redoList.size() > 0) {
			AbstractCommand redoCommand = (AbstractCommand) redoList.removeFirst();
			redoCommand.doIt();
			redoList.addFirst(redoCommand);
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
