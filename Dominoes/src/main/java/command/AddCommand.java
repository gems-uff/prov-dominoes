package command;

import com.josericardojunior.domain.Dominoes;

import boundary.App;
import javafx.scene.Group;

public class AddCommand extends AbstractCommand {

	private String key;
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
			Dominoes auxDomino = App.getList().getDominoes().get(App.getList().getPieces().indexOf(piece));
			this.addedDominoes = auxDomino.cloneNoMatrix();
			this.addedDominoes.setId(this.key);
			if (index == -1) {
				this.addedDominoes.setSourceIndex(App.getList().getPieces().indexOf(piece));
				Group newPiece = App.copyToArea(addedDominoes, index);
				this.index = App.getArea().getData().getPieces().indexOf(newPiece);
			} else {
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
	public String getName() {
		String base = ADD_COMMAND + "(" + this.addedDominoes.getRelation().getAbbreviate().replace(" ", "") + ")";
		return addedDominoes.getId() + " = " + base;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key=key;
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
