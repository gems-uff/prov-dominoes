package provdominoes.command;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Optional;

import javafx.scene.Group;
import javafx.scene.control.TextInputDialog;
import provdominoes.boundary.App;
import provdominoes.domain.Configuration;
import provdominoes.domain.Dominoes;

public class PercentCommand extends AbstractCommand {

	private Group piece;
	private double x;
	private double y;
	private Dominoes oldDominoes;
	private int index;
	private double percent;

	public PercentCommand() {
		this.index = -1;
	}

	public PercentCommand(int index) {
		this();
		this.index = index;
	}

	@Override
	protected boolean doIt() {
		boolean success = true;
		this.oldDominoes = App.getArea().getData().getDominoes().get(index).cloneNoMatrix();
		this.piece = App.getArea().getData().getPieces().get(index);
		x = this.piece.getTranslateX();
		y = this.piece.getTranslateY();
		try {
			Dominoes toPercent = App.getArea().getData().getDominoes().get(index);
			if (!super.isReproducing()) {
				percent = getPercent();
			}
			if (percent != -1.0) {
				Dominoes domino = provdominoes.control.Controller.percent(toPercent, percent);

				App.getArea().remove(index);
				this.piece = App.getArea().add(domino, piece.getTranslateX(), piece.getTranslateY(), index);

				if (Configuration.autoSave) {
					App.getArea().saveAndSendToList(piece);
				}
			} else {
				success = false;
			}
		} catch (Exception e) {
			App.alertException(e, "Erro desconhecido ao efetuar filtro de" + NumberFormat.getInstance().format(percent)
					+ "% maiores!");
			e.printStackTrace();
			success = false;
		}
		super.setReproducing(false);
		return success;
	}

	private double getPercent() {
		double d = 0.0;
		TextInputDialog dialog = new TextInputDialog("%");
		dialog.setTitle("Percent Filter");
		dialog.setHeaderText("Filter cells that pass the test: CELL >= MAX_CELL x (1 - %)");
		dialog.setContentText("Please enter the % you want to filter:");

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			try {
				d = NumberFormat.getInstance().parse(result.get()).doubleValue();
			} catch (ParseException e) {
				App.alertException(e, "Valor inválido!");
				return -1;
			}
		} else {
			return -1;
		}
		return d;
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
		return PERCENT_COMMAND + "(" + this.oldDominoes.getId() + ", " + NumberFormat.getInstance().format(percent)
				+ ")";
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

	public void setPercent(double percent) {
		this.percent = percent;
	}

}
