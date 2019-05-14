package command;

import com.josericardojunior.domain.Dominoes;

import boundary.App;
import javafx.scene.Group;

public class RemoveCommand extends AbstractCommand {

	private int pieceIndex;
	private int sourceIndex;

	public RemoveCommand() {

	}

	public RemoveCommand(int pieceIndex) {
		super();
		this.pieceIndex = pieceIndex;
	}

	public Group getPiece() {
		return App.getArea().getData().getPieces().get(pieceIndex);
	}

	public void setPieceIndex(int pieceIndex) {
		this.pieceIndex = pieceIndex;
	}

	@Override
	protected boolean doIt() {
		this.sourceIndex = App.getArea().getData().getDominoes().get(pieceIndex).getSourceIndex();
		return App.getArea().closePiece(this.getPiece());
	}

	@Override
	protected boolean undoIt() {
		boolean success = true;
		try {
			Dominoes auxDomino = App.getList().getDominoes().get(sourceIndex);
			App.copyToArea(auxDomino.cloneNoMatrix(), pieceIndex);			
		} catch (Exception e) {
			System.err.println(e.getMessage());
			success = false;
		}
		return success;
	}

}
