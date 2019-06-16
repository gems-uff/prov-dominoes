package command;

import boundary.App;
import javafx.scene.Group;

public class CommandFactory {

	private static CommandFactory factory = null;
	private int pieceCounter = 1;

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

	public AddCommand add(String trigram) {
		AddCommand add = new AddCommand(trigram);
		add.setKey("p" + getInstance().getPieceCounter());
		return add;
	}

	public MoveCommand move(Group piece, double oldX, double oldY) {
		MoveCommand move = new MoveCommand(App.getArea().getData().getPieces().indexOf(piece), piece.getTranslateX(),
				piece.getTranslateY());
		move.setOldX(oldX);
		move.setOldY(oldY);
		return move;
	}

	public AbstractCommand remove(Group piece) {
		return new RemoveCommand(App.getArea().getData().getPieces().indexOf(piece));
	}

	public AbstractCommand save(Group piece) {
		return new SaveCommand(App.getArea().getData().getPieces().indexOf(piece));
	}

	public AbstractCommand transpose(Group piece) {
		return new TransposeCommand(App.getArea().getData().getPieces().indexOf(piece));
	}

	public AbstractCommand multiply() {
		MultiplyCommand mul = new MultiplyCommand();
		mul.setKey("p" + getInstance().getPieceCounter());
		return mul;
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

	public AbstractCommand transitiveClosure(Group piece) {
		return new TransitiveClosureCommand(App.getArea().getData().getPieces().indexOf(piece));
	}

	public AbstractCommand filterBinarize(Group piece) {
		return new BinarizeCommand(App.getArea().getData().getPieces().indexOf(piece));
	}

	public AbstractCommand filterInvert(Group piece) {
		return new InvertCommand(App.getArea().getData().getPieces().indexOf(piece));
	}

	public AbstractCommand filterDiagonal(Group piece) {
		return new DiagonalizeCommand(App.getArea().getData().getPieces().indexOf(piece));
	}

	public AbstractCommand filterUpperDiagonal(Group piece) {
		return new UpperDiagonalCommand(App.getArea().getData().getPieces().indexOf(piece));
	}

	public AbstractCommand filterLowerDiagonal(Group piece) {
		return new LowerDiagonalCommand(App.getArea().getData().getPieces().indexOf(piece));
	}

	public int getPieceCounter() {
		return pieceCounter++;
	}

	public void setPieceCounter(int pieceCounter) {
		this.pieceCounter = pieceCounter;
	}

	public AbstractCommand load(String provFilePath) {
		return new LoadCommand(provFilePath);
	}

	public AbstractCommand undo(int count) {
		return new UndoCommand(count);
	}

}