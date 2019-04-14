package command;

import com.josericardojunior.domain.Dominoes;

import boundary.App;
import javafx.scene.Group;

public class AddCommand extends AbstractCommand {

	private Group piece;
	private Dominoes addedDominoes;

	public AddCommand() {
		
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
			Dominoes auxDomino = App.getList().getDominoes().get(App.getList().getPieces().indexOf(piece));
			this.addedDominoes = auxDomino.cloneNoMatrix();
			this.piece = App.copyToArea(addedDominoes);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			success = false;
		}

		return success;
	}

	protected boolean doIt(Dominoes d) {
		boolean success = true;
		try {
			this.piece = App.copyToArea(d.cloneNoMatrix());
		} catch (Exception e) {
			System.err.println(e.getMessage());
			success = false;
		}
		return success;
	}

	@Override
	protected boolean undoIt() {
		return new RemoveCommand().doIt(this.addedDominoes);
	}

}
