package provdominoes.command;

import javafx.scene.Group;
import provdominoes.boundary.App;

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
		add.setKey("P" + getInstance().getPieceCounter());
		return add;
	}

	public MoveCommand move(String identifier, Group piece, double oldX, double oldY) {
		MoveCommand move = new MoveCommand(identifier, piece.getTranslateX(),
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
		mul.setKey("P" + getInstance().getPieceCounter());
		return mul;
	}
	
	public AbstractCommand sum() {
		SumCommand sum = new SumCommand();
		sum.setKey("P" + getInstance().getPieceCounter());
		return sum;
	}
	
	public AbstractCommand subtract() {
		SubtractCommand subtraction = new SubtractCommand();
		subtraction.setKey("P" + getInstance().getPieceCounter());
		return subtraction;
	}

	public AbstractCommand aggColumns(Group piece) {
		return new AggregateColumnsCommand(App.getArea().getData().getPieces().indexOf(piece));
	}

	public AbstractCommand aggRows(Group piece) {
		return new AggregateRowsCommand(App.getArea().getData().getPieces().indexOf(piece));
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

	public AbstractCommand binarize(Group piece) {
		return new BinarizeCommand(App.getArea().getData().getPieces().indexOf(piece));
	}

	public AbstractCommand invert(Group piece) {
		return new InvertCommand(App.getArea().getData().getPieces().indexOf(piece));
	}
	
	public AbstractCommand filterHighPassFilter(Group piece) {
		return new HighPassFilterCommand(App.getArea().getData().getPieces().indexOf(piece));
	}
	
	public AbstractCommand filterLowPassFilter(Group piece) {
		return new LowPassFilterCommand(App.getArea().getData().getPieces().indexOf(piece));
	}
	
	public AbstractCommand filterColumnText(Group piece) {
		return new ColumnTextCommand(App.getArea().getData().getPieces().indexOf(piece));
	}
	
	public AbstractCommand filterRowText(Group piece) {
		return new RowTextCommand(App.getArea().getData().getPieces().indexOf(piece));
	}

	public AbstractCommand filterDiagonal(Group piece) {
		return new DiagonalizeCommand(App.getArea().getData().getPieces().indexOf(piece));
	}

	public AbstractCommand filterUpperDiagonal(Group piece) {
		return new UpperTriangularCommand(App.getArea().getData().getPieces().indexOf(piece));
	}

	public AbstractCommand filterLowerDiagonal(Group piece) {
		return new LowerDiagonalCommand(App.getArea().getData().getPieces().indexOf(piece));
	}
	
	public AbstractCommand trim(Group piece) {
		return new TrimCommand(App.getArea().getData().getPieces().indexOf(piece));
	}
	
	public AbstractCommand sortRows(Group piece) {
		return new SortRowsCommand(App.getArea().getData().getPieces().indexOf(piece));
	}
	
	public AbstractCommand sortColumns(Group piece) {
		return new SortColumnsCommand(App.getArea().getData().getPieces().indexOf(piece));
	}
	
	
	public AbstractCommand sortRowCount(Group piece) {
		return new SortRowGroupCommand(App.getArea().getData().getPieces().indexOf(piece));
	}
	
	public AbstractCommand sortColumnCount(Group piece) {
		return new SortColumnGroupCommand(App.getArea().getData().getPieces().indexOf(piece));
	}
	
	public AbstractCommand sortColumnFirst(Group piece) {
		return new SortColumnFirstCommand(App.getArea().getData().getPieces().indexOf(piece));
	}
	
	public AbstractCommand sortRowFirst(Group piece) {
		return new SortRowFirstCommand(App.getArea().getData().getPieces().indexOf(piece));
	}
	
	public int getPieceCounter() {
		return pieceCounter++;
	}

	public void setPieceCounter(int pieceCounter) {
		this.pieceCounter = pieceCounter;
	}

	public AbstractCommand load(String[] filePaths, String dir) {
		return new LoadCommand(filePaths, dir);
	}
	
	public AbstractCommand loadMatrices(String[] filePaths, String dir) {
		return new LoadMatricesCommand(filePaths, dir);
	}

	public AbstractCommand undo(int count) {
		return new UndoCommand(count);
	}

	public AbstractCommand filterHighPass(Group piece, double p) {
		HighPassFilterCommand pc = new HighPassFilterCommand(App.getArea().getData().getPieces().indexOf(piece));
		pc.setCutoff(p);
		return pc;
	}
	
	public AbstractCommand filterLowPass(Group piece, double p) {
		LowPassFilterCommand pc = new LowPassFilterCommand(App.getArea().getData().getPieces().indexOf(piece));
		pc.setCutoff(p);
		return pc;
	}
	
	public AbstractCommand filterColumnText(Group piece, TextFilterData t) {
		ColumnTextCommand tc = new ColumnTextCommand(App.getArea().getData().getPieces().indexOf(piece));
		tc.setText(t);
		return tc;
	}
	
	public AbstractCommand filterRowText(Group piece, TextFilterData t) {
		RowTextCommand tc = new RowTextCommand(App.getArea().getData().getPieces().indexOf(piece));
		tc.setText(t);
		return tc;
	}

	public AbstractCommand sortRowValues(Group piece) {
		return new SortRowValuesCommand(App.getArea().getData().getPieces().indexOf(piece));
	}

	public AbstractCommand sortColumnValues(Group piece) {
		return new SortColumnValuesCommand(App.getArea().getData().getPieces().indexOf(piece));
	}

}