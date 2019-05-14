package command;

import boundary.App;
import javafx.scene.Group;

public class CommandFactory {

	public UndoCommand undo() {
		return new UndoCommand();
	}

	public RedoCommand redo() {
		return new RedoCommand();
	}

	public AbstractCommand add(Group piece) {
		return new AddCommand(piece);
	}

	public AbstractCommand remove(Group piece) {
		return new RemoveCommand(App.getArea().getData().getPieces().indexOf(piece));
	}

	public TransposeCommand transpose(Group piece) {
		return new TransposeCommand(App.getArea().getData().getPieces().indexOf(piece));
	}

	public AbstractCommand multiply() {
		return new MultiplyCommand();
	}

	
	/*
	public AggregateRowCommand aggRow(Group piece) {
		return new AggregateRowCommand(piece);
	}
	public AggregateColumnCommand aggColumn(Group piece) {
		return new AggregateColumnCommand(piece);
	}
	public ConfidenceCommand confidence(Group piece) {
		return new ConfidenceCommand(piece);
	}
	public ZScoreCommand zscore(Group piece) {
		return new ZScoreCommand(piece);
	}
	*/
}
