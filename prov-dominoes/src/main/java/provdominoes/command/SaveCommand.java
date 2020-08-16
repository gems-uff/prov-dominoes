package provdominoes.command;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.la4j.matrix.sparse.CRSMatrix;

import javafx.scene.Group;
import processor.Cell;
import provdominoes.arch.MatrixOperationsCPU;
import provdominoes.boundary.App;
import provdominoes.domain.Configuration;
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
		long startTime = 0;
		long endTime = 0;
		if (Configuration.telemetry) {
			startTime = System.nanoTime();
		}
		boolean success = true;
		Group piece = App.getArea().getData().getPieces().get(index);
		x = piece.getTranslateX();
		y = piece.getTranslateY();
		this.savedDominoes = App.getArea().getData().getDominoes().get(index).cloneNoMatrix();
		try {
			CRSMatrix matrix = null;
			if (Configuration.isGPUProcessing()) {
				matrix = new CRSMatrix(savedDominoes.getMat().getMatrixDescriptor().getNumRows(),
						savedDominoes.getMat().getMatrixDescriptor().getNumCols());
				ArrayList<Cell> clls = savedDominoes.getMat().getData();
				for (Cell c : clls) {
					matrix.set(c.row, c.col, c.value);
				}
			} else {
				this.savedDominoes.setMat(new MatrixOperationsCPU(savedDominoes.getDescriptor()));
				matrix = App.getArea().getData().getDominoes().get(index).getCrsMatrix();
			}
			this.savedDominoes.setCrsMatrix(matrix);
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
		if (Configuration.telemetry) {
			endTime = System.nanoTime();
			long timeElapsed = endTime - startTime;
			double d = timeElapsed / 1000000d;
			DecimalFormat df = new DecimalFormat("#.##");
			df = new DecimalFormat("#.##");
			System.out.println("Time elapsed for saving "+intoId+" in Dominoes List in ms: " + df.format(d));
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
