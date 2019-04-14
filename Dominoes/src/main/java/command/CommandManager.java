package command;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import boundary.DominoesMenuBar;
import javafx.scene.Group;

public class CommandManager {

	private int maxHistoryLength = 100;

	private LinkedList<AbstractCommand> history;
	private LinkedList<AbstractCommand> redoList;
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
		} else if (command instanceof RemoveCommand) {
			Group pieceToRemove = ((RemoveCommand) command).getPiece();
			List<AbstractCommand> cpyHistory = new ArrayList<>(history);
			for (Iterator<AbstractCommand> iterator = cpyHistory.iterator(); iterator.hasNext();) {
				AbstractCommand comm = (AbstractCommand) iterator.next();
				if (comm.getPiece() == pieceToRemove) {
					history.remove(comm);
				}
			}
			List<AbstractCommand> cpyRedoList = new ArrayList<>(redoList);
			for (Iterator<AbstractCommand> iterator = cpyRedoList.iterator(); iterator.hasNext();) {
				AbstractCommand comm = (AbstractCommand) iterator.next();
				if (comm.getPiece() == pieceToRemove) {
					redoList.remove(comm);
				}
			}
			command.doIt();
		} else if (!(command instanceof RemoveCommand) && command.doIt()) {
			addToHistory(command);
		} else {
			history.clear();
		}
		this.uptadeMenu();
	}

	private void uptadeMenu() {
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
			undoCommand.undoIt();
			if (!(undoCommand instanceof RemoveCommand) && !(undoCommand instanceof AddCommand)) {
				redoList.addFirst(undoCommand);
			} else {
				redoList.clear();
			}
		}
	}

	private void redo() {
		if (redoList.size() > 0) {
			AbstractCommand redoCommand = (AbstractCommand) redoList.removeFirst();
			redoCommand.doIt();
			if (!(redoCommand instanceof RemoveCommand)) {
				addToHistory(redoCommand);
			}
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
