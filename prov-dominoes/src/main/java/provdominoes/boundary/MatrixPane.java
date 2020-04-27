package provdominoes.boundary;

import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import processor.Cell;
import provdominoes.arch.MatrixDescriptor;
import provdominoes.domain.Dominoes;
import provdominoes.util.Prov2DominoesUtil;

public class MatrixPane extends Pane {

	private double maxZoom = 2;
	private double minZoom = 0.05;

	private double srcSceneX;
	private double srcSceneY;
	private double srcTranslateX;
	private double srcTranslateY;

	private Rectangle block;
	private List<Rectangle> recHeaders;
	private List<Rectangle> recCells;
	private List<Float> cells;
	private Color cellColor = new Color(0, 0, 1.0f, 1.0f);

	private double max, min;

	public MatrixPane(Dominoes domino) {

		this.setStyle("-fx-background-color: #B4B4B4");

		this.recHeaders = new ArrayList<>();
		this.recCells = new ArrayList<>();
		this.cells = new ArrayList<>();

		Group group = new Group();
		MatrixDescriptor _descriptor = domino.getDescriptor();

		this.min = domino.getCrsMatrix().min();
		this.max = domino.getCrsMatrix().max();

		double beginRowHead;
		double endRowHead;
		double beginColumnHead;
		double endColumnHead;

		double width;
		double height;

		double padding = 0;
		double cellSpace = 20;
		double charSpace = 7;
		double largerSize = 0;

		int _nRows = _descriptor.getNumRows();
		int _nCols = _descriptor.getNumCols();

		for (int i = 0; i < _nRows; i++) {
			if (domino.getDescriptor().getRowAt(i).length() > largerSize) {
				largerSize = domino.getDescriptor().getRowAt(i).length();
			}
		}

		beginRowHead = -1 * largerSize * charSpace;
		endRowHead = 0;

		for (int i = 0; i < _nCols; i++) {
			if (domino.getDescriptor().getColumnAt(i).length() > largerSize) {
				largerSize = domino.getDescriptor().getColumnAt(i).length();
			}
		}

		beginColumnHead = -1 * largerSize * charSpace;
		endColumnHead = 0;

		width = Math.abs(endRowHead - beginRowHead);
		height = cellSpace;

		// draw the label of the matrix row/columns
		for (int i = 0; i < _nRows; i++) {
			largerSize = domino.getDescriptor().getRowAt(i).length();
			Rectangle back = new Rectangle(width, height);
			back.setFill(new Color(1, 1, 1, 1));
			back.setTranslateX(0);
			back.setTranslateY(0);
			back.toBack();

			Rectangle front = new Rectangle(width, height);
			front.setFill(new Color(0, 0, 1, 0.5 + (0.2 * ((-1) * i % 2))));
			front.setTranslateX(0);
			front.setTranslateY(0);
			front.toFront();

			Group cell = new Group(back, front);
			cell.setTranslateX(beginRowHead);
			cell.setTranslateY(i * (cellSpace + padding) + padding);

			Text text = new Text(domino.getDescriptor().getRowAt(i));
			text.setTranslateX(beginRowHead);
			text.setTranslateY(i * (cellSpace + padding) + padding + height);
			if (i % 2 == 0) {
				text.setFill(Color.WHITE);
			} else {
				text.setFill(Color.BLACK);
			}
			text.toFront();

			group.getChildren().add(new Group(cell, text));

		}

		width = Math.abs(endColumnHead - beginColumnHead);
		height = cellSpace;

		for (int i = 0; i < _nCols; i++) {
			Rectangle back = new Rectangle(width, height);
			back.setTranslateX(0);
			back.setTranslateY(0);
			back.setFill(new Color(1, 1, 1, 1));

			Rectangle front = new Rectangle(width, height);
			front.setTranslateX(0);
			front.setTranslateY(0);
			front.setFill(new Color(0, 0, 1, 0.5 + (0.2 * ((-1) * i % 2))));

			front.toFront();

			this.recHeaders.add(front);

			Group cell = new Group(back, front);

			Text text = new Text(domino.getDescriptor().getColumnAt(i));
			text.setTranslateX(endColumnHead);
			text.setTranslateY(height - 5.0);

			if (i % 2 == 0) {
				text.setFill(Color.WHITE);
			} else {
				text.setFill(Color.BLACK);
			}
			text.toFront();

			Group g = new Group(cell, text);
			g.setTranslateX(1 + (i * (cellSpace + padding) + padding) + (height / 2 - width / 2));
			g.setTranslateY(-1 + ((-1) * (cellSpace + padding)) - (width / 2 - height / 2));
			g.getTransforms().add(new Rotate(-90, width / 2.0f, height / 2.0f, 1.0f, Rotate.Z_AXIS));

			group.getChildren().add(g);
		}

		ArrayList<Cell> cells = null;
		if (domino.getType() == Dominoes.TYPE_ZSCORE) {
			cells = domino.getMat().getData();
		} else {
			cells = domino.getMat().getAllData();
		}

		for (Cell _matCell : cells) {
			Rectangle back = new Rectangle(cellSpace, cellSpace);
			Rectangle front = new Rectangle(cellSpace - 1, cellSpace - 1);
			back.setFill(new Color(1, 1, 1, 1));

			if (min < 0) {
				if (_matCell.value != 0.0) {
					if (Float.isNaN(_matCell.value)) {
						front.setFill(new Color(1, 1, 1, 1));
						_matCell.value = 0f;
					} else {
						if (_matCell.value > 0.0) {
							front.setFill(new Color(cellColor.getRed(), cellColor.getGreen(), cellColor.getBlue(),
									(_matCell.value) / (max)));
						} else {
							front.setFill(new Color(cellColor.getBlue(), cellColor.getGreen(), cellColor.getRed(),
									(_matCell.value) / (min)));
						}
					}
				} else {
					if (domino.getType() == Dominoes.TYPE_ZSCORE) {
						front.setFill(new Color(1, 1, 1, 1));
					} else {
						front.setFill(new Color(180.0 / 255.0, 180.0 / 255.0, 180.0 / 255.0, 1));
					}
				}
			} else {
				if (_matCell.value != 0.0) {
					if (Float.isNaN(_matCell.value)) {
						front.setFill(new Color(1, 1, 1, 1));
						_matCell.value = 0f;
					} else {
						front.setFill(new Color(cellColor.getRed(), cellColor.getGreen(), cellColor.getBlue(),
								(_matCell.value - min) / (max - min)));
					}
				} else {
					if (domino.getType() == Dominoes.TYPE_ZSCORE) {
						front.setFill(new Color(1, 1, 1, 1));
					} else {
						front.setFill(new Color(180.0 / 255.0, 180.0 / 255.0, 180.0 / 255.0, 1));
					}
				}
			}
			front.toFront();

			this.cells.add(_matCell.value);
			this.recCells.add(front);

			Group cell = new Group(back, front);
			cell.setTranslateX(_matCell.col * (cellSpace + padding) + padding);
			cell.setTranslateY(_matCell.row * (cellSpace + padding) + padding);

			Tooltip.install(cell, new Tooltip("(" + domino.getDescriptor().getRowAt(_matCell.row) + ", "
					+ domino.getDescriptor().getColumnAt(_matCell.col) + ") = " + String.valueOf(_matCell.value)));

			group.getChildren().add(cell);

			block = new Rectangle(40, 30);
			block.setFill(new Color(50 / 255.0, 75 / 255.0, 180.0 / 255.0, 0.5));
			block.setX(-39.5);
			block.setY(-30.5);
			block.toFront();
			Text text = new Text(domino.getDescriptor().getRowType() + " | " + domino.getDescriptor().getColType());

			text.setFont(Font.font("Times", FontWeight.BOLD, 11));
			text.setTranslateX(-38);
			text.setTranslateY(-06);
			text.setFill(Color.WHITE);
			text.toFront();

			Group cellBlock = new Group(block, text);
			String tooltip = domino.getDescriptor().getRowType() + " | " + domino.getDescriptor().getColType()+"\n";
			int totalNonZero = Prov2DominoesUtil.getNonZeroTotal(domino.getCrsMatrix());
			tooltip+= "Total (non zero): "+totalNonZero+"\n";
			double minNonZero = Prov2DominoesUtil.getNonZeroMin(domino.getCrsMatrix());
			tooltip+= "Min (non zero): "+minNonZero+"\n";
			double averageNonZero = Prov2DominoesUtil.getNonZeroAverage(domino.getCrsMatrix());
			tooltip+= "Average (non zero): "+averageNonZero+"\n";
			double sdNonZero = Prov2DominoesUtil.getNonZeroStandardScore(domino.getCrsMatrix(), averageNonZero);
			tooltip+= "Z-Score (non zero): "+sdNonZero+"\n";
			double max = domino.getCrsMatrix().max();
			tooltip+= "Max: "+max+"\n";
			Tooltip.install(cellBlock,new Tooltip(tooltip));
			group.getChildren().add(cellBlock);

			this.recHeaders.add(front);
		}

		this.setOnScroll(new EventHandler<ScrollEvent>() {

			@Override
			public void handle(ScrollEvent event) {
				double srcX = event.getX() - group.getTranslateX() - group.prefWidth(-1) / 2;
				double srcY = event.getY() - group.getTranslateY() - group.prefHeight(-1) / 2;
				double trgX = srcX;
				double trgY = srcY;

				double factor = 0.05;

				if (event.getDeltaY() < 0 && group.getScaleX() > minZoom) {
					group.setScaleX(group.getScaleX() * (1 - factor));
					group.setScaleY(group.getScaleY() * (1 - factor));
					trgX = srcX * (1 - factor);
					trgY = srcY * (1 - factor);
				} else if (event.getDeltaY() > 0 && group.getScaleX() < maxZoom) {
					group.setScaleX(group.getScaleX() * (1 + factor));
					group.setScaleY(group.getScaleY() * (1 + factor));
					trgX = srcX * (1 + factor);
					trgY = srcY * (1 + factor);
				}
				group.setTranslateX(group.getTranslateX() - (trgX - srcX));
				group.setTranslateY(group.getTranslateY() - (trgY - srcY));

			}
		});
		this.setOnMouseDragged(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				double offsetX = event.getSceneX() - srcSceneX;
				double offsetY = event.getSceneY() - srcSceneY;
				double newTranslateX = srcTranslateX + offsetX;
				double newTranslateY = srcTranslateY + offsetY;

				group.setTranslateX(newTranslateX);
				group.setTranslateY(newTranslateY);

			}
		});
		this.setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				srcSceneX = event.getSceneX();
				srcSceneY = event.getSceneY();
				srcTranslateX = group.getTranslateX();
				srcTranslateY = group.getTranslateY();

				cursorProperty().set(Cursor.CLOSED_HAND);
			}
		});
		this.setOnMouseReleased(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				cursorProperty().set(Cursor.OPEN_HAND);
			}
		});

		this.getChildren().add(new FlowPane(group));

	}

}
