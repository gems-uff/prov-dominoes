package provdominoes.command;

import javafx.scene.Group;
import provdominoes.boundary.App;
import provdominoes.domain.Dominoes;

public class RemoveCommand extends AbstractCommand {

	private Dominoes removedDominoes;
	private int pieceIndex;
	private int sourceIndex;
	private double x;
	private double y;

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
		this.x = this.getPiece().getTranslateX();
		this.y = this.getPiece().getTranslateY();
		return App.getArea().closePiece(this.getPiece());
	}

	@Override
	protected boolean undoIt() {
		boolean success = true;
		try {
			Dominoes auxDomino = App.getList().getDominoes().get(sourceIndex);
			App.getArea().add(auxDomino.cloneNoMatrix(), x, y, pieceIndex);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			success = false;
		}
		return success;
	}

	@Override
	public String getName() {
		return REMOVE_COMMAND + "(" + this.removedDominoes.getId() + ")";
	}
	
	private String id;
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

}
