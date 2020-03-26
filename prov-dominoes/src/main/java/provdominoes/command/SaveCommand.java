package provdominoes.command;

import java.io.IOException;

import javafx.scene.Group;
import provdominoes.boundary.App;
import provdominoes.domain.Dominoes;

public class SaveCommand extends AbstractCommand {

	private Dominoes savedDominoes;
	private double x;
	private double y;
	private int index;
	private String prevId;
	private String intoId;

	public SaveCommand() {
		this.index = -1;
	}

	public SaveCommand(int index) {
		this();
		this.index = index;
	}

	@Override
	protected boolean doIt() {
		boolean success = true;
		Group piece = App.getArea().getData().getPieces().get(index);
		x = piece.getTranslateX();
		y = piece.getTranslateY();
		this.savedDominoes = App.getArea().getData().getDominoes().get(index);
		this.prevId = savedDominoes.getId();
		try {
			intoId = App.getArea().saveAndSendToList(piece);
			App.getArea().close(piece);
		} catch (IOException e) {
			success = false;
			e.printStackTrace();
		}
		return success;
	}

	@Override
	protected boolean undoIt() {
		boolean result = false;
		int listIndex = App.getList().getDominoes().indexOf(savedDominoes);
		App.getList().remove(App.getList().getPieces().get(listIndex));
		savedDominoes.setId(prevId);
		App.getArea().add(savedDominoes, x, y, index);
		result = true;
		return result;
	}

	@Override
	public String getName() {
		return SAVE_COMMAND + "(" + prevId + ") => " + intoId;
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
