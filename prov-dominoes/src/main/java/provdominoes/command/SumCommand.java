package provdominoes.command;

import javafx.scene.Group;
import provdominoes.boundary.App;
import provdominoes.boundary.MoveData;
import provdominoes.domain.Configuration;
import provdominoes.domain.Dominoes;

public class SumCommand extends AbstractCommand {

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

	public SumCommand() {
		this.indexResultDominoes = -1;
		this.indexLeftDominoes = -1;
		this.indexRightDominoes = -1;
	}

	@Override
	protected boolean doIt() {
		boolean result = false;
		if (indexLeftDominoes != -1 && indexRightDominoes != -1) {
			App.getArea().getData().setCombination(MoveData.COMBINATION_SUM);
			App.getArea().getData().setIndexFirstOperatorCombination(indexLeftDominoes);
			App.getArea().getData().setIndexSecondOperatorCombination(indexRightDominoes);
		}
		if (App.getArea().getData().getCombination() == MoveData.COMBINATION_SUM && App.getArea().getData().getIndexFirstOperatorCombination() != -1
				&& App.getArea().getData().getIndexSecondOperatorCombination() != -1) {
			Dominoes d1 = App.getArea().getData().getDominoes()
					.get(App.getArea().getData().getIndexFirstOperatorCombination());
			this.leftDominoes = d1;
			this.indexLeftDominoes = App.getArea().getData().getIndexFirstOperatorCombination();

			Dominoes d2 = App.getArea().getData().getDominoes()
					.get(App.getArea().getData().getIndexSecondOperatorCombination());
			this.rightDominoes = d2;
			this.indexRightDominoes = App.getArea().getData().getIndexSecondOperatorCombination();

			try {
				if (d1.getIdRow().equals(d2.getIdRow()) && d1.getIdCol().equals(d2.getIdCol())) {
					Dominoes resultOperation = provdominoes.control.Controller.sum(d1, d2);
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

					if (App.getArea().remove(App.getArea().getData().getIndexFirstOperatorCombination())
							&& App.getArea().getData().getIndexSecondOperatorCombination() > App.getArea().getData()
									.getIndexFirstOperatorCombination()) {
						App.getArea().remove(App.getArea().getData().getIndexSecondOperatorCombination() - 1);
					} else {
						App.getArea().remove(App.getArea().getData().getIndexSecondOperatorCombination());
					}

					this.resultPiece = App.getArea().add(resultOperation, x, y, indexResultDominoes);
					this.indexResultDominoes = App.getArea().getData().getDominoes().indexOf(this.resultDominoes);

					if (Configuration.autoSave) {
						App.getArea().saveAndSendToList(App.getArea().getData().getPieces()
								.get(App.getArea().getData().getDominoes().indexOf(resultOperation)));
					}
				}
				App.getArea().getData().setCombination(-1);
				App.getArea().getData().setIndexFirstOperatorCombination(-1);
				App.getArea().getData().setIndexSecondOperatorCombination(-1);
				result = true;
			} catch (Exception e) {
				App.alertException(e, "Erro desconhecido ao calcular soma das matrizes das peças!");
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
			if (App.getArea().getData().getPieces()
					.indexOf(App.getArea().getData().getPiece(move.getIdentifier())) == indexLeftDominoes) {
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
		String base = SUM_COMMAND + "(" + leftDominoes.getId() + ", " + rightDominoes.getId() + ")";
		return key + " = " + base;
	}

}