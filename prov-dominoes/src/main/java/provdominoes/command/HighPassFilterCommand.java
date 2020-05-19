package provdominoes.command;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.Optional;

import javafx.scene.Group;
import javafx.scene.control.TextInputDialog;
import provdominoes.boundary.App;
import provdominoes.domain.Configuration;
import provdominoes.domain.Dominoes;
import provdominoes.util.Prov2DominoesUtil;

public class HighPassFilterCommand extends AbstractCommand {

	private Group piece;
	private double x;
	private double y;
	private Dominoes oldDominoes;
	private int index;
	private double cutoff;

	public HighPassFilterCommand() {
		this.index = -1;
	}

	public HighPassFilterCommand(int index) {
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
			Dominoes toHPF = App.getArea().getData().getDominoes().get(index);
			int totalNonZero = Prov2DominoesUtil.getNonZeroTotal(toHPF.getCrsMatrix());
			double minNonZero = Prov2DominoesUtil.getNonZeroMin(toHPF.getCrsMatrix());
			double averageNonZero = Prov2DominoesUtil.getNonZeroAverage(toHPF.getCrsMatrix());
			double max = toHPF.getCrsMatrix().max();
			double sdNonZero = Prov2DominoesUtil.getNonZeroStandardScore(toHPF.getCrsMatrix(), averageNonZero);
			if (!super.isReproducing() && !super.isScripting()) {
				cutoff = getValue(totalNonZero, minNonZero, max, averageNonZero, sdNonZero);
			}
			if (cutoff != -1.0) {
				Dominoes domino = provdominoes.control.Controller.highPassFilter(toHPF, cutoff);

				App.getArea().remove(index);
				this.piece = App.getArea().add(domino, piece.getTranslateX(), piece.getTranslateY(), index);

				if (Configuration.autoSave) {
					App.getArea().saveAndSendToList(piece);
				}
			} else {
				success = false;
			}
		} catch (Exception e) {
			App.alertException(e, "Unknown error try to execute High-Pass Filter(cutoff: "
					+ NumberFormat.getInstance().format(cutoff) + " )!");
			e.printStackTrace();
			success = false;
		}
		super.setReproducing(false);
		super.setScripting(false);
		return success;
	}

	private double getValue(int totalNonZero, double minNonZero, double max, double averageNonZero, double sdNonZero) {
		Locale loc = Locale.getDefault();
		DecimalFormatSymbols symbols = new DecimalFormatSymbols(loc);
		char sep = symbols.getDecimalSeparator();
		double d = 0.0;
		TextInputDialog dialog = new TextInputDialog(
				"" + DecimalFormat.getInstance(Locale.getDefault()).format(minNonZero));
		dialog.setTitle("High-Pass Filter");
		dialog.setHeaderText(
				"Filter that passes cells with values higher than the cutoff value informed!\nTotal Non-zero: "
						+ DecimalFormat.getInstance(Locale.getDefault()).format(totalNonZero) + "\nNon-zero min: "
						+ DecimalFormat.getInstance(Locale.getDefault()).format(minNonZero) + "\nMax: " + DecimalFormat.getInstance(Locale.getDefault()).format(max)
						+ "\nNon-zero Average: "
						+ DecimalFormat.getInstance(Locale.getDefault()).format(averageNonZero)
						+ "\nNon-zero Standard Deviation: "
						+ DecimalFormat.getInstance(Locale.getDefault()).format(sdNonZero));
		dialog.setContentText("Cutoff value: (Use only the decimal separator. Decimal separator in your region: \""+sep+"\")");

		Optional<String> result = dialog.showAndWait();
		if (result.isPresent()) {
			try {
				if (result.get().matches("\\d+((" + sep + "\\d+)|$)")) {
					d = DecimalFormat.getInstance(Locale.getDefault()).parse(result.get()).doubleValue();
				} else {
					App.alertException(new NumberFormatException(), "Invalid Value! Use only decimal separator! (" + result.get() + ")");
					return -1;
				}
			} catch (ParseException e) {
				App.alertException(e, "Invalid Value! Use only decimal separator! (" + result.get() + ")");
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
		return HPF_COMMAND + "(" + this.oldDominoes.getId() + ", "
				+ NumberFormat.getInstance().format(cutoff).replace(",", ".") + ")";
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

	public void setCutoff(double percent) {
		this.cutoff = percent;
	}

}
