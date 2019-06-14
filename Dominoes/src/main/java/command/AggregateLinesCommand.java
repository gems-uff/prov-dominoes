package command;

import java.io.IOException;

import com.josericardojunior.domain.Dominoes;

import boundary.App;
import domain.Configuration;
import javafx.scene.Group;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class AggregateLinesCommand extends AbstractCommand {

	private Group piece;
	private Dominoes oldDominoes;
	private double x;
	private double y;
	private int index;

	public AggregateLinesCommand() {
		this.index = -1;
	}

	public AggregateLinesCommand(int index) {
		this();
		this.index = index;		
	}

	@Override
	protected boolean doIt() {
		boolean success = true;
		this.oldDominoes = App.getArea().getData().getDominoes().get(index).cloneNoMatrix();
		this.piece = App.getArea().getData().getPieces().get(index);
		this.x = this.piece.getTranslateX();
		this.y = this.piece.getTranslateY();
		Dominoes toReduce = App.getArea().getData().getDominoes().get(index);
		try {
			if (!toReduce.isColAggregatable()) {
				toReduce.transpose();
				Dominoes dominoes = control.Controller.reduceDominoes(toReduce);
				dominoes.transpose();
				App.getArea().getData().getDominoes().set(index, dominoes);

				dominoes.drawDominoes();

				((Text) piece.getChildren().get(Dominoes.GRAPH_ID_COL)).setText(dominoes.getIdCol());
				((Text) piece.getChildren().get(Dominoes.GRAPH_ID_COL))
						.setFont(new Font(Dominoes.GRAPH_AGGREG_FONT_SIZE));

				if (Configuration.autoSave) {
					App.getArea().saveAndSendToList(piece);
				}

				App.getArea().getData().getMenuItemAggregateCol().get(index).setDisable(true);
			} else {
				success = false;
				System.err.println("this domino is already aggregate by "
						+ toReduce.getMat().getMatrixDescriptor().getColType());
			}
		} catch (IOException e) {
			success = false;
			e.printStackTrace();
		}
		return success;
	}

	@Override
	protected boolean undoIt() {
		boolean result = false;
		Group p = App.getArea().getData().getPieces().get(this.index);
		App.getArea().closePiece(p);
		App.getArea().add(this.oldDominoes, x, y, this.index);		
		result = true;
		return result;
	}

	@Override
	public String getName() {
		return AGGREGATE_LINES_COMMAND + "(" + this.oldDominoes.getId() + ")";
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
