package provdominoes.command;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.la4j.matrix.functor.MatrixProcedure;

import javafx.scene.Group;
import processor.Cell;
import provdominoes.arch.MatrixOperations;
import provdominoes.arch.MatrixOperationsFactory;
import provdominoes.boundary.App;
import provdominoes.domain.Configuration;
import provdominoes.domain.Dominoes;

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
		long startTime = 0;
		long endTime = 0;
		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
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
						MatrixOperations mat = MatrixOperationsFactory.getMatrix2D(Configuration.isGPUProcessing(),
								addedDominoes.getDescriptor());
						if (Configuration.isGPUProcessing()) {
							ArrayList<Cell> cells = new ArrayList<Cell>();
							this.addedDominoes.getCrsMatrix().eachNonZero(new MatrixProcedure() {
								@Override
								public void apply(int row, int col, double value) {
									Cell cell = new Cell();
									cell.row = row;
									cell.col = col;
									cell.value = (float) value;
									cells.add(cell);
								}
							});
							mat.setData(cells);
						} else {
							mat.setMatrix(this.addedDominoes.getCrsMatrix());
						}
						this.addedDominoes.setMat(mat);
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
		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for adding " + trigram + " on Canvas in ms: " + df.format(d));
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
