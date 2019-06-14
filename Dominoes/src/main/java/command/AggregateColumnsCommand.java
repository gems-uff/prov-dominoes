package command;

import java.io.IOException;

import com.josericardojunior.domain.Dominoes;

import boundary.App;
import domain.Configuration;
import javafx.scene.Group;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class AggregateColumnsCommand extends AbstractCommand {

	private Group piece;
	private Dominoes oldDominoes;
	private double x;
	private double y;
	private int index;

	public AggregateColumnsCommand() {
		this.index = -1;
	}

	public AggregateColumnsCommand(int index) {
		this();
		this.index = index;
	}

	@Override
	protected boolean doIt() {
		this.oldDominoes = App.getArea().getData().getDominoes().get(index).cloneNoMatrix();
		this.piece = App.getArea().getData().getPieces().get(index);
		this.x = this.piece.getTranslateX();
		this.y = this.piece.getTranslateY();
		Dominoes toReduce = App.getArea().getData().getDominoes().get(index);
		boolean success = true;
		try {
			if (!toReduce.isRowAggregatable()) {
				Dominoes domino = control.Controller.reduceDominoes(toReduce);
				App.getArea().getData().getDominoes().set(index, domino);

				((Text) piece.getChildren().get(Dominoes.GRAPH_ID_ROW)).setText(domino.getIdRow());
				((Text) piece.getChildren().get(Dominoes.GRAPH_ID_ROW))
						.setFont(new Font(Dominoes.GRAPH_AGGREG_FONT_SIZE));

				if (Configuration.autoSave) {
					App.getArea().saveAndSendToList(piece);
				}
				App.getArea().getData().getMenuItemAggregateRow().get(index).setDisable(true);
			} else {
				success = false;
				System.err.println(
						"this domino is already aggregate by " + toReduce.getMat().getMatrixDescriptor().getRowType());
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
		return AGGREGATE_COLUMNS_COMMAND + "(" + this.oldDominoes.getId() + ")";
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