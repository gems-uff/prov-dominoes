package command;

import boundary.App;
import javafx.scene.Group;

public class CommandFactory {

	private static CommandFactory factory = null;

	public static CommandFactory getInstance() {
		if (factory == null) {
			factory = new CommandFactory();
		}
		return factory;
	}

	public UndoCommand undo() {
		return new UndoCommand();
	}

	public RedoCommand redo() {
		return new RedoCommand();
	}

	public AbstractCommand save(Group piece) {
		return new SaveCommand(App.getArea().getData().getPieces().indexOf(piece));
	}

	public AbstractCommand add(Group piece) {
		return new AddCommand(piece);
	}

	public AbstractCommand remove(Group piece) {
		return new RemoveCommand(App.getArea().getData().getPieces().indexOf(piece));
	}

	public AbstractCommand transpose(Group piece) {
		return new TransposeCommand(App.getArea().getData().getPieces().indexOf(piece));
	}

	public AbstractCommand multiply() {
		return new MultiplyCommand();
	}

	public AbstractCommand aggLines(Group piece) {
		return new AggregateLinesCommand(App.getArea().getData().getPieces().indexOf(piece));
	}

	public AbstractCommand aggColumns(Group piece) {
		return new AggregateColumnsCommand(App.getArea().getData().getPieces().indexOf(piece));
	}

	public AbstractCommand confidence(Group piece) {
		return new ConfidenceCommand(App.getArea().getData().getPieces().indexOf(piece));
	}

	public AbstractCommand zscore(Group piece) {
		return new ZScoreCommand(App.getArea().getData().getPieces().indexOf(piece));
	}

}
