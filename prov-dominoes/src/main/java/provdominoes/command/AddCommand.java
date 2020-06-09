package provdominoes.command;

import javafx.scene.Group;
import provdominoes.arch.MatrixOperations;
import provdominoes.arch.MatrixOperationsFactory;
import provdominoes.boundary.App;
import provdominoes.domain.Dominoes;
import provdominoes.util.Prov2DominoesUtil;

public class AddCommand extends AbstractCommand {

	private String key;
	private Group piece;
	private Dominoes addedDominoes;
	private int index;
	private String trigram;

	public AddCommand() {
		this.index = -1;
	}

	public AddCommand(String trigram) {
		this();
		this.trigram = trigram;
	}

	@Override
	protected boolean doIt() {
		boolean success = true;
		try {
			if (trigram != null) {
				for (Dominoes d : App.getList().getDominoes()) {
					if (d != null && cmd(d).equals(trigram)) {
						int index = App.getList().getDominoes().indexOf(d);
						this.piece = App.getList().getPieces().get(index);
						this.addedDominoes = App.getList().getDominoes().get(App.getList().getPieces().indexOf(piece))
								.cloneNoMatrix();
						this.addedDominoes.setId(this.key);
						MatrixOperations mat = MatrixOperationsFactory.getMatrix2D(false, addedDominoes.getDescriptor(), false);
						mat.setData(Prov2DominoesUtil.matrix2Cells(this.addedDominoes.getCrsMatrix()));
						this.addedDominoes.setMat(mat);
						//this.addedDominoes.setMat(MatrixOperations.configureOperation(addedDominoes.getCrsMatrix(), addedDominoes.getDescriptor(), false));
						break;
					}
				}
			}
			if (this.index == -1) {
				this.addedDominoes.setSourceIndex(App.getList().getPieces().indexOf(piece));
				Group newPiece = App.copyToArea(addedDominoes, this.index);
				this.index = App.getArea().getData().getPieces().indexOf(newPiece);
			} else {
				this.addedDominoes.setSourceIndex(App.getList().getPieces().indexOf(piece));
				App.getArea().add(addedDominoes, this.index);
			}
		} catch (Exception e) {
			App.alertException(e, e.getMessage());
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
		String base = ADD_COMMAND + "(" + trigram + ")";
		return addedDominoes.getId() + " = " + base;
	}

	public String getKey() {
		return this.key;
	}

	public void setKey(String key) {
		this.key = key;
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

	public Group getPiece() {
		return piece;
	}

	public void setPiece(Group piece) {
		this.piece = piece;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

}
