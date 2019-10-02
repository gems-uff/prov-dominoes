package provdominoes.command;

import javafx.scene.Group;
import provdominoes.boundary.App;
import provdominoes.domain.Configuration;
import provdominoes.domain.Dominoes;

public class MultiplyCommand extends AbstractCommand {

	private String id;
	private String key;
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
					Dominoes resultOperation = provdominoes.control.Controller.multiply(d1, d2);
					this.resultDominoes = resultOperation;
					this.resultDominoes.setId(key);

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
			} catch (Exception e) {
				App.alertException(e, "Erro desconhecido ao calcular multiplicação das matrizes das peças!");
				result = false;
				e.printStackTrace();
			}
		}

		return result;
	}

	@Override
	protected boolean undoIt() {
		boolean result = false;
		updateDraggedPieceLastPosition();
		App.getArea().remove(App.getArea().getData().getPieces().get(indexResultDominoes));
		if (indexLeftDominoes < indexRightDominoes) {
			App.getArea().add(leftDominoes, xLeftDominoes, yLeftDominoes, indexLeftDominoes);
			App.getArea().add(rightDominoes, xRightDominoes, yRightDominoes, indexRightDominoes);
		} else {
			App.getArea().add(rightDominoes, xRightDominoes, yRightDominoes, indexRightDominoes);
			App.getArea().add(leftDominoes, xLeftDominoes, yLeftDominoes, indexLeftDominoes);
		}
		result = true;
		return result;
	}

	private void updateDraggedPieceLastPosition() {
		// Pega o comando anterior à multiplicação, o MoveCommand, e atualiza com a
		// posição anterior
		// ao arrasto da peça para multiplicação
		AbstractCommand comm = App.getCommandManager().getPreviousCommand();
		if (comm != null && comm instanceof MoveCommand) {
			MoveCommand move = (MoveCommand) comm;
			if (move.getIndex() == indexLeftDominoes) {
				xLeftDominoes = move.getOldX();
				yLeftDominoes = move.getOldY();
			} else {
				xRightDominoes = move.getOldX();
				yRightDominoes = move.getOldY();
			}
		}
	}

	protected Group getPiece() {
		return this.resultPiece;
	}
	
	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	@Override
	public String getName() {
		String base = MULTIPLY_COMMAND + "(" + leftDominoes.getId() + ", " + rightDominoes.getId() + ")";
		return key + " = " + base;
	}

	

}