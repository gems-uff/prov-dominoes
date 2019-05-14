package command;

import com.josericardojunior.domain.Dominoes;

import boundary.App;
import javafx.scene.Group;

public class AddCommand extends AbstractCommand {

	private Group piece;
	private Dominoes addedDominoes;
	private int index;

	public AddCommand() {
		this.index = -1;
	}

	public AddCommand(Group group) {
		super();
		this.piece = group;
	}

	public Group getPiece() {
		return piece;
	}

	public void setPiece(Group piece) {
		this.piece = piece;
	}

	@Override
	protected boolean doIt() {
		boolean success = true;
		try {
			if (index == -1) {
				Dominoes auxDomino = App.getList().getDominoes().get(App.getList().getPieces().indexOf(piece));
				this.addedDominoes = auxDomino.cloneNoMatrix();
				this.addedDominoes.setSourceIndex(App.getList().getPieces().indexOf(piece));
				this.piece = App.copyToArea(addedDominoes, index);
				this.index = App.getArea().getData().getPieces().indexOf(piece);
			} else {
				Dominoes auxDomino = App.getList().getDominoes().get(App.getList().getPieces().indexOf(piece));
				this.addedDominoes = auxDomino.cloneNoMatrix();
				this.addedDominoes.setSourceIndex(App.getList().getPieces().indexOf(piece));
				App.getArea().add(addedDominoes, index);
			}
		} catch (Exception e) {
			System.err.println(e.getMessage());
			success = false;
		}

		return success;
	}

	@Override
	protected boolean undoIt() {
		Group p = App.getArea().getData().getPieces().get(index);
		return App.getArea().closePiece(p);
	}

}
