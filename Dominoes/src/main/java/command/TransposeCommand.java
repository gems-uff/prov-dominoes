package command;

import com.josericardojunior.domain.Dominoes;

import boundary.App;
import boundary.MoveData;
import javafx.animation.FillTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.RotateTransition;
import javafx.animation.SequentialTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.control.MenuItem;
import javafx.scene.paint.Color;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.util.Duration;

public class TransposeCommand extends AbstractCommand {

	private Group piece;
	private int pieceIndex;

	public TransposeCommand(int pieceIndex) {
		super();
		this.pieceIndex = pieceIndex;
	}

	@Override
	protected boolean doIt() {
		boolean success = true;
		MoveData data = App.getArea().getData();
		this.piece = App.getArea().getData().getPieces().get(pieceIndex);
		try {
			data.setTransposing(true);

			int duration = 500;

			double startAngle = piece.getRotate();

			MenuItem swapMenu = data.getMenuItemAggregateRow().get(pieceIndex);
			data.getMenuItemAggregateRow().set(pieceIndex, data.getMenuItemAggregateCol().get(pieceIndex));
			data.getMenuItemAggregateCol().set(pieceIndex, swapMenu);

			Dominoes domino = control.Controller
					.tranposeDominoes(data.getDominoes().get(data.getPieces().indexOf(piece)));
			Group swap = domino.drawDominoes();

			double swapFontSize = ((Text) piece.getChildren().get(Dominoes.GRAPH_ID_COL)).getFont().getSize();
			((Text) piece.getChildren().get(Dominoes.GRAPH_ID_COL))
					.setFont(((Text) piece.getChildren().get(Dominoes.GRAPH_ID_ROW)).getFont());
			((Text) piece.getChildren().get(Dominoes.GRAPH_ID_ROW)).setFont(new Font(swapFontSize));

			double translateX = ((Text) piece.getChildren().get(Dominoes.GRAPH_ID_ROW)).getX();
			((Text) piece.getChildren().get(Dominoes.GRAPH_ID_ROW))
					.setX(((Text) piece.getChildren().get(Dominoes.GRAPH_ID_COL)).getX());
			((Text) piece.getChildren().get(Dominoes.GRAPH_ID_COL)).setX(translateX);

			((Text) piece.getChildren().get(Dominoes.GRAPH_ID_ROW))
					.setText(((Text) swap.getChildren().get(Dominoes.GRAPH_ID_ROW)).getText());
			((Text) piece.getChildren().get(Dominoes.GRAPH_ID_COL))
					.setText(((Text) swap.getChildren().get(Dominoes.GRAPH_ID_COL)).getText());

			RotateTransition rtPiece = new RotateTransition(Duration.millis(duration));
			rtPiece.setFromAngle(startAngle);
			rtPiece.setToAngle(startAngle + 180);

			RotateTransition rtPieceRow = new RotateTransition(Duration.millis(duration));
			rtPieceRow.setFromAngle(rtPiece.getFromAngle());
			rtPieceRow.setToAngle(startAngle - 180);

			RotateTransition rtPieceCol = new RotateTransition(Duration.millis(duration));
			rtPieceCol.setFromAngle(rtPiece.getFromAngle());
			rtPieceCol.setToAngle(rtPieceRow.getToAngle());

			RotateTransition rtType = new RotateTransition(Duration.millis(duration));
			rtType.setFromAngle(rtPiece.getFromAngle());
			rtType.setToAngle(rtPiece.getToAngle());

			Color colorHistoric = (Color) ((Text) piece.getChildren().get(Dominoes.GRAPH_HISTORIC)).getFill();
			FillTransition ftHistoric1 = new FillTransition(Duration.millis(duration));
			ftHistoric1.setFromValue(colorHistoric);
			ftHistoric1.setToValue(Dominoes.COLOR_INIVISIBLE);

			FillTransition ftHistoric2 = new FillTransition(Duration.millis(duration));
			ftHistoric2.setFromValue(ftHistoric1.getToValue());
			ftHistoric2.setToValue(ftHistoric1.getFromValue());

			Group groupType = (Group) piece.getChildren().get(Dominoes.GRAPH_TYPE);
			Color colorType = (Color) ((Shape) groupType.getChildren().get(0)).getFill();
			FillTransition ftType1 = new FillTransition(Duration.millis(duration));
			ftType1.setFromValue(colorType);
			ftType1.setToValue(Dominoes.COLOR_INIVISIBLE);

			Color colorFontType = (Color) ((Text) groupType.getChildren().get(1)).getFill();
			FillTransition ftType2 = new FillTransition(Duration.millis(duration));
			ftType2.setFromValue(colorFontType);
			ftType2.setToValue(Dominoes.COLOR_INIVISIBLE);

			FillTransition ftType3 = new FillTransition(Duration.millis(duration));
			ftType3.setFromValue(ftType1.getToValue());
			ftType3.setToValue(ftType1.getFromValue());

			FillTransition ftType4 = new FillTransition(Duration.millis(duration));
			ftType4.setFromValue(ftType2.getToValue());
			ftType4.setToValue(ftType2.getFromValue());

			ParallelTransition transition1_1 = new ParallelTransition(
					new SequentialTransition(groupType.getChildren().get(0), ftType1));
			ParallelTransition transition1_2 = new ParallelTransition(
					new SequentialTransition(groupType.getChildren().get(1), ftType2));
			ParallelTransition transition1_3 = new ParallelTransition(piece.getChildren().get(Dominoes.GRAPH_HISTORIC),
					ftHistoric1);

			transition1_1.play();
			transition1_2.play();
			transition1_3.play();

			ParallelTransition transition2_1 = new ParallelTransition(piece, rtPiece);
			ParallelTransition transition2_2 = new ParallelTransition(piece.getChildren().get(Dominoes.GRAPH_ID_ROW),
					rtPieceRow);
			ParallelTransition transition2_3 = new ParallelTransition(piece.getChildren().get(Dominoes.GRAPH_ID_COL),
					rtPieceCol);

			if (!colorFontType.equals(Dominoes.COLOR_INIVISIBLE) || !colorHistoric.equals(Dominoes.COLOR_INIVISIBLE)) {
				transition2_1.setDelay(Duration.millis(duration));
				transition2_2.setDelay(Duration.millis(duration));
				transition2_3.setDelay(Duration.millis(duration));
			}

			transition2_1.play();
			transition2_2.play();
			transition2_3.play();

			ParallelTransition transition3_1 = new ParallelTransition(piece.getChildren().get(Dominoes.GRAPH_HISTORIC),
					ftHistoric2);
			ParallelTransition transition3_2 = new ParallelTransition(groupType.getChildren().get(0), ftType3);
			ParallelTransition transition3_3 = new ParallelTransition(groupType.getChildren().get(1), ftType4);

			if (!colorFontType.equals(Dominoes.COLOR_INIVISIBLE) || !colorHistoric.equals(Dominoes.COLOR_INIVISIBLE)) {
				transition3_1.setDelay(Duration.millis(2 * duration));
				transition3_2.setDelay(Duration.millis(2 * duration));
				transition3_3.setDelay(Duration.millis(2 * duration));
			}

			transition3_1.play();
			transition3_2.play();
			transition3_3.play();

			transition1_1.setOnFinished(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent arg0) {

					double x = ((Group) piece.getChildren().get(Dominoes.GRAPH_TYPE)).getTranslateX();
					double y = ((Group) piece.getChildren().get(Dominoes.GRAPH_TYPE)).getTranslateY();
					x = Math.abs(Dominoes.GRAPH_WIDTH - x);
					y = Math.abs(Dominoes.GRAPH_HEIGHT - y);
					((Group) piece.getChildren().get(Dominoes.GRAPH_TYPE)).setTranslateX(x);
					((Group) piece.getChildren().get(Dominoes.GRAPH_TYPE)).setTranslateY(y);

					((Text) piece.getChildren().get(Dominoes.GRAPH_HISTORIC)).setRotate(startAngle - 180);
					((Group) piece.getChildren().get(Dominoes.GRAPH_TYPE)).setRotate(startAngle - 180);

					((Text) piece.getChildren().get(Dominoes.GRAPH_HISTORIC))
							.setText(((Text) swap.getChildren().get(Dominoes.GRAPH_HISTORIC)).getText());
					((Text) ((Group) piece.getChildren().get(Dominoes.GRAPH_TYPE)).getChildren().get(1))
							.setText(((Text) ((Group) swap.getChildren().get(Dominoes.GRAPH_TYPE)).getChildren().get(1))
									.getText());

				}
			});

			transition3_3.setOnFinished(new EventHandler<ActionEvent>() {

				@Override
				public void handle(ActionEvent arg0) {
					data.setTransposing(false);

				}
			});
		} catch (Exception e) {
			System.err.println(e.getMessage());
			success = false;
		}

		return success;
	}

	@Override
	protected boolean undoIt() {
		return doIt();
	}

	public Group getPiece() {
		return piece;
	}

	public void setPiece(Group piece) {
		this.piece = piece;
	}

	@Override
	public String getName() {
		return TRANSPOSE_COMMAND + "(" + App.getArea().getData().getDominoes().get(pieceIndex).getId() + ")";
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
