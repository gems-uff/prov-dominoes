package provdominoes.command;

import java.io.IOException;

import javafx.scene.Group;
import provdominoes.arch.MatrixOperations;
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
		this.savedDominoes = App.getArea().getData().getDominoes().get(index).cloneNoMatrix();
		try {
			this.savedDominoes.setMat(MatrixOperations.configureOperation(savedDominoes.getCrsMatrix(),
					savedDominoes.getDescriptor(), false));
			this.prevId = savedDominoes.getId();
			intoId = App.getArea().saveAndSendToList(piece, savedDominoes);
		} catch (IOException e) {
			success = false;
			e.printStackTrace();
			App.alertException(e, "Failed trying to save piece!");
		} catch (Exception e) {
			success = false;
			e.printStackTrace();
			App.alertException(e, "Failed trying to save piece!");
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

	public String getIntoId() {
		return intoId;
	}

	public void setIntoId(String intoId) {
		this.intoId = intoId;
	}

	public Dominoes getSavedDominoes() {
		return savedDominoes;
	}

	public void setSavedDominoes(Dominoes savedDominoes) {
		this.savedDominoes = savedDominoes;
	}

}
