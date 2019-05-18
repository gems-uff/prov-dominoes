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

	public AddCommand(Group piece) {
		this();
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
				Group newPiece = App.copyToArea(addedDominoes, index);
				this.index = App.getArea().getData().getPieces().indexOf(newPiece);
			} else {
				Dominoes auxDomino = App.getList().getDominoes().get(App.getList().getPieces().indexOf(piece));
				this.addedDominoes = auxDomino.cloneNoMatrix();
				this.addedDominoes.setSourceIndex(App.getList().getPieces().indexOf(piece));
				App.getArea().add(addedDominoes, this.index);
			}
		} catch (Exception e) {
			e.printStackTrace();
			success = false;
		}

		return success;
	}

	@Override
	protected boolean undoIt() {
		Group p = App.getArea().getData().getPieces().get(this.index);
		return App.getArea().closePiece(p);
	}

	@Override
	protected String getName() {
		return ADD_COMMAND+"("+this.index+","+this.addedDominoes.getIdRow()+"|"+this.addedDominoes.getIdCol()+")";
	}

}
