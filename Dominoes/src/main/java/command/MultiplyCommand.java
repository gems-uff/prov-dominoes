package command;

import java.io.IOException;

import com.josericardojunior.domain.Dominoes;

import boundary.App;
import domain.Configuration;
import javafx.scene.Group;

public class MultiplyCommand extends AbstractCommand {

	private Dominoes leftDominoes;
	private int indexLeftDominoes;
	private double xLeftDominoes;
	private double yLeftDominoes;
	private Dominoes rightDominoes;
	private int indexRightDominoes;
	private double xRightDominoes;
	private double yRightDominoes;
	private Dominoes resultDominoes;
	private int indexResultDominoes;
	private Group resultPiece;

	public MultiplyCommand() {
		this.indexResultDominoes = -1;
		this.indexLeftDominoes = -1;
		this.indexRightDominoes = -1;
	}

	@Override
	protected boolean doIt() {
		boolean result = false;
		if (indexLeftDominoes != -1 && indexRightDominoes != -1) {
			App.getArea().getData().setIndexFirstOperatorMultiplication(indexLeftDominoes);
			App.getArea().getData().setIndexSecondOperatorMultiplication(indexRightDominoes);
		}
		if (App.getArea().getData().getIndexFirstOperatorMultiplication() != -1
				&& App.getArea().getData().getIndexSecondOperatorMultiplication() != -1) {
			Dominoes d1 = App.getArea().getData().getDominoes()
					.get(App.getArea().getData().getIndexFirstOperatorMultiplication());
			this.leftDominoes = d1;
			this.indexLeftDominoes = App.getArea().getData().getIndexFirstOperatorMultiplication();

			Dominoes d2 = App.getArea().getData().getDominoes()
					.get(App.getArea().getData().getIndexSecondOperatorMultiplication());
			this.rightDominoes = d2;
			this.indexRightDominoes = App.getArea().getData().getIndexSecondOperatorMultiplication();

			try {
				if (d1.getIdCol().equals(d2.getIdRow())) {
					Dominoes resultOperation = control.Controller.MultiplyMatrices(d1, d2);
					this.resultDominoes = resultOperation;

					xLeftDominoes = App.getArea().getData().getPieces()
							.get(App.getArea().getData().getDominoes().indexOf(d1)).getTranslateX();
					xRightDominoes = App.getArea().getData().getPieces()
							.get(App.getArea().getData().getDominoes().indexOf(d2)).getTranslateX();
					double x = (xLeftDominoes + xRightDominoes) / 2;

					yLeftDominoes = App.getArea().getData().getPieces()
							.get(App.getArea().getData().getDominoes().indexOf(d1)).getTranslateY();
					yRightDominoes = App.getArea().getData().getPieces()
							.get(App.getArea().getData().getDominoes().indexOf(d2)).getTranslateY();

					double y = (yLeftDominoes + yRightDominoes) / 2;

					if (App.getArea().remove(App.getArea().getData().getIndexFirstOperatorMultiplication())
							&& App.getArea().getData().getIndexSecondOperatorMultiplication() > App.getArea().getData()
									.getIndexFirstOperatorMultiplication()) {
						App.getArea().remove(App.getArea().getData().getIndexSecondOperatorMultiplication() - 1);
					} else {
						App.getArea().remove(App.getArea().getData().getIndexSecondOperatorMultiplication());
					}

					this.resultPiece = App.getArea().add(resultOperation, x, y, indexResultDominoes);
					this.indexResultDominoes = App.getArea().getData().getDominoes().indexOf(this.resultDominoes);

					if (Configuration.autoSave) {
						App.getArea().saveAndSendToList(App.getArea().getData().getPieces()
								.get(App.getArea().getData().getDominoes().indexOf(resultOperation)));
					}
				}
				App.getArea().getData().setIndexFirstOperatorMultiplication(-1);
				App.getArea().getData().setIndexSecondOperatorMultiplication(-1);
				result = true;
			} catch (IOException e) {
				result = false;
				e.printStackTrace();
			}
		}

		return result;
	}

	@Override
	protected boolean undoIt() {
		boolean result = false;
		// Remover result
		App.getArea().remove(App.getArea().getData().getPieces().get(indexResultDominoes));
		if(indexLeftDominoes<indexRightDominoes) {
			// Inserir left
			App.getArea().add(leftDominoes, xLeftDominoes, yLeftDominoes, indexLeftDominoes);
			// Inserir right
			App.getArea().add(rightDominoes, xRightDominoes, yRightDominoes, indexRightDominoes);
		} else {
			// Inserir right
			App.getArea().add(rightDominoes, xRightDominoes, yRightDominoes, indexRightDominoes);	
			// Inserir left
			App.getArea().add(leftDominoes, xLeftDominoes, yLeftDominoes, indexLeftDominoes);
		}
		result = true;
		return result;
	}

	@Override
	protected Group getPiece() {
		return this.resultPiece;
	}

}