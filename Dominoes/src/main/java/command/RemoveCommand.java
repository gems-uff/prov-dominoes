package command;

import com.josericardojunior.domain.Dominoes;

import boundary.App;
import javafx.scene.Group;

public class RemoveCommand extends AbstractCommand {

	private Dominoes removedDominoes;
	private int pieceIndex;
	private int sourceIndex;

	public RemoveCommand() {
		this.pieceIndex = -1;
	}

	public RemoveCommand(int pieceIndex) {
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
		this.removedDominoes = App.getArea().getData().getDominoes().get(pieceIndex);
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

	@Override
	protected String getName() {
		return REMOVE_COMMAND + "(" + pieceIndex + "," + removedDominoes.getIdRow() + "|" + removedDominoes.getIdCol()
				+ ")";
	}

}
