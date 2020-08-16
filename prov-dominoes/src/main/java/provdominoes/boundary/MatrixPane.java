package provdominoes.boundary;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.imageio.ImageIO;

import org.la4j.matrix.functor.MatrixProcedure;
import org.la4j.matrix.sparse.CRSMatrix;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Tooltip;
import javafx.scene.image.WritableImage;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.FileChooser;
import processor.Cell;
import provdominoes.arch.MatrixDescriptor;
import provdominoes.domain.Configuration;
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
	private Group matrixGroup;;
	private Color cellColor = new Color(0, 0, 1.0f, 1.0f);

	private double max, min;
	private Group source;

	public MatrixPane(Dominoes domino) {

		this.setStyle("-fx-background-color: #B4B4B4");

		this.recHeaders = new ArrayList<>();
		this.recCells = new ArrayList<>();
		this.cells = new ArrayList<>();
		matrixGroup = new Group();

		MatrixDescriptor _descriptor = domino.getDescriptor();

		this.min = domino.getMat().findMinValue();
		this.max = domino.getMat().findMaxValue();

		double beginRowHead;
		double endRowHead;
		double beginColumnHead;
		double endColumnHead;

		double width;
		double height;

		double padding = 0;
		double cellSpace = 20;
		double charSpaceRow = (domino.getDescriptor().getRowsDesc().size() > 0
				&& domino.getDescriptor().getRowAt(0).equals("SUM")) ? 13.5 : 7;
		double charSpaceColumn = (domino.getDescriptor().getColumnsDesc().size() > 0
				&& domino.getDescriptor().getColumnAt(0).equals("SUM")) ? 10 : 7;
		double largerSizeRow = 0;
		double largerSizeColumn = 0;

		int _nRows = _descriptor.getNumRows();
		int _nCols = _descriptor.getNumCols();

		for (int i = 0; i < _nRows; i++) {
			if (domino.getDescriptor().getRowAt(i).length() > largerSizeRow) {
				largerSizeRow = domino.getDescriptor().getRowAt(i).length();
			}
		}

		beginRowHead = -1 * largerSizeRow * charSpaceRow;
		endRowHead = 0;

		for (int i = 0; i < _nCols; i++) {
			if (domino.getDescriptor().getColumnAt(i).length() > largerSizeColumn) {
				largerSizeColumn = domino.getDescriptor().getColumnAt(i).length();
			}
		}

		beginColumnHead = -1 * largerSizeColumn * charSpaceColumn;
		endColumnHead = 0;

		width = Math.abs(endRowHead - beginRowHead);
		height = cellSpace;

		// draw the label of the matrix row labels
		for (int i = 0; i < _nRows; i++) {
			largerSizeRow = domino.getDescriptor().getRowAt(i).length();
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

			String labelText = domino.getDescriptor().getRowAt(i);
			Text text = new Text(labelText);
			text.setFont(Font.font("Times", FontWeight.BOLD, 12));
			text.setTranslateX(beginRowHead + 2);
			text.setTranslateY(i * (cellSpace + padding) + padding + height - 3);
			if (i % 2 == 0) {
				text.setFill(Color.WHITE);
			} else {
				text.setFill(Color.BLACK);
			}
			text.toFront();
			Group rowLabel = new Group(cell, text);
			if (domino.getDescriptor().getRowsTooltips() != null
					&& domino.getDescriptor().getRowsTooltips().size() > i) {
				Tooltip.install(rowLabel, new Tooltip(domino.getDescriptor().getRowsTooltips().get(i)));
			}

			ContextMenu contextLabel = new ContextMenu();
			MenuItem copyToClipboard = new MenuItem("Copy to Clipboard");
			MenuItem gotoURL = new MenuItem("Go to URL");
			String urlMatch = "(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})";
			contextLabel.getItems().add(copyToClipboard);
			if (text.getText().matches(urlMatch)) {
				contextLabel.getItems().add(gotoURL);
			}
			rowLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					if (e.getButton() == MouseButton.SECONDARY) {
						contextLabel.show(cell, e.getScreenX(), e.getScreenY());
					} else {
						contextLabel.hide();
					}
				}
			});
			copyToClipboard.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					final ClipboardContent content = new ClipboardContent();
					content.putString(text.getText());
					Clipboard.getSystemClipboard().setContent(content);
				}
			});
			gotoURL.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
					if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
						try {
							desktop.browse(new URI(text.getText()));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
			matrixGroup.getChildren().add(rowLabel);
		}

		width = Math.abs(endColumnHead - beginColumnHead);
		height = cellSpace;
		// draw the label of the matrix column labels
		for (int i = 0; i < _nCols; i++) {
			Rectangle back = new Rectangle(width, height);
			back.setTranslateX(0);
			back.setTranslateY(-1);
			back.setFill(new Color(1, 1, 1, 1));

			Rectangle front = new Rectangle(width, height);
			front.setTranslateX(0);
			front.setTranslateY(-1);
			front.setFill(new Color(0, 0, 1, 0.5 + (0.2 * ((-1) * i % 2))));

			front.toFront();

			this.recHeaders.add(front);

			Group cell = new Group(back, front);

			Text text = new Text(domino.getDescriptor().getColumnAt(i));
			text.setFont(Font.font("Times", FontWeight.BOLD, 12));
			text.setTranslateX(endColumnHead + 2);
			text.setTranslateY(height - 7.0);

			if (i % 2 == 0) {
				text.setFill(Color.WHITE);
			} else {
				text.setFill(Color.BLACK);
			}
			text.toFront();

			Group columnLabel = new Group(cell, text);
			columnLabel.setTranslateX(1 + (i * (cellSpace + padding) + padding) + (height / 2 - width / 2));
			columnLabel.setTranslateY(((-1) * (cellSpace + padding)) - (width / 2 - height / 2));
			columnLabel.getTransforms().add(new Rotate(-90, width / 2.0f, height / 2.0f, 1.0f, Rotate.Z_AXIS));

			if (domino.getDescriptor().getColumnsTooltips() != null
					&& domino.getDescriptor().getColumnsTooltips().size() > i) {
				Tooltip.install(columnLabel, new Tooltip(domino.getDescriptor().getColumnsTooltips().get(i)));
			}

			ContextMenu contextLabel = new ContextMenu();
			MenuItem copyToClipboard = new MenuItem("Copy to Clipboard");
			MenuItem gotoURL = new MenuItem("Go to URL");
			String urlMatch = "(https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|www\\.[a-zA-Z0-9][a-zA-Z0-9-]+[a-zA-Z0-9]\\.[^\\s]{2,}|https?:\\/\\/(?:www\\.|(?!www))[a-zA-Z0-9]+\\.[^\\s]{2,}|www\\.[a-zA-Z0-9]+\\.[^\\s]{2,})";
			contextLabel.getItems().add(copyToClipboard);
			if (text.getText().matches(urlMatch)) {
				contextLabel.getItems().add(gotoURL);
			}
			columnLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				@Override
				public void handle(MouseEvent e) {
					if (e.getButton() == MouseButton.SECONDARY) {
						contextLabel.show(cell, e.getScreenX(), e.getScreenY());
					} else {
						contextLabel.hide();
					}
				}
			});
			copyToClipboard.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					final ClipboardContent content = new ClipboardContent();
					content.putString(text.getText());
					Clipboard.getSystemClipboard().setContent(content);
				}
			});
			gotoURL.setOnAction(new EventHandler<ActionEvent>() {
				@Override
				public void handle(ActionEvent event) {
					Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
					if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
						try {
							desktop.browse(new URI(text.getText()));
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			});
			matrixGroup.getChildren().add(columnLabel);
		}

		CRSMatrix matrix = domino.getCrsMatrix();
		List<Cell> nonZeros = new ArrayList<>();
		if (Configuration.isGPUProcessing()) {
			matrix = new CRSMatrix(domino.getMat().getMatrixDescriptor().getNumRows(),
					domino.getMat().getMatrixDescriptor().getNumCols());
			nonZeros = domino.getMat().getData();
			for (Cell c : nonZeros) {
				matrix.set(c.row, c.col, c.value);
			}
			domino.setCrsMatrix(matrix);
		} else {
			if (domino.getType() == Dominoes.TYPE_ZSCORE) {
				Cell[] clls = new Cell[matrix.cardinality()];
				List<Integer> range = IntStream.rangeClosed(0, matrix.cardinality()-1)
					    .boxed().collect(Collectors.toList());
				final Stack<Integer> s = new Stack<>();
				s.addAll(range);
				matrix.eachNonZero(new MatrixProcedure() {
					@Override
					public void apply(int row, int col, double value) {
						clls[s.pop()] = new Cell(row, col,
								new Double(value).floatValue());
					}
				});
				nonZeros = Arrays.asList(clls);
			}
		}
		for (int i = 0; i < matrix.rows(); i++) {
			for (int j = 0; j < matrix.columns(); j++) {

				final Cell _matCell = new Cell(i, j, new Double(matrix.get(i, j)).floatValue());

				if ((domino.getType() == Dominoes.TYPE_ZSCORE
						&& nonZeros.contains(new Cell(i, j, new Double(matrix.get(i, j)).floatValue())))
						|| domino.getType() != Dominoes.TYPE_ZSCORE) {

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
									front.setFill(new Color(cellColor.getRed(), cellColor.getGreen(),
											cellColor.getBlue(), (_matCell.value) / (max)));
								} else {
									front.setFill(new Color(cellColor.getBlue(), cellColor.getGreen(),
											cellColor.getRed(), (_matCell.value) / (min)));
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
					String cellParams = "";
					if (domino.getCellParams() != null) {
						String idx = domino.getRelation().getAbbreviate().replace(" ", "") + "(" + _matCell.row + ","
								+ _matCell.col + ")";
						if (domino.getCellParams().get(idx) != null) {
							cellParams = domino.getCellParams().get(idx);
						}
					}
					if (domino.getUnderlyingElements() != null
							&& domino.getUnderlyingElements()[_matCell.row][_matCell.col] != null) {
						Tooltip.install(cell, new Tooltip("(" + domino.getDescriptor().getRowAt(_matCell.row) + ", "
								+ domino.getDescriptor().getColumnAt(_matCell.col) + ") = "
								+ String.valueOf(_matCell.value) + " : "
								+ domino.getUnderlyingElements()[_matCell.row][_matCell.col] + "\n" + cellParams));
					} else {
						Tooltip.install(cell,
								new Tooltip("(" + domino.getDescriptor().getRowAt(_matCell.row) + ", "
										+ domino.getDescriptor().getColumnAt(_matCell.col) + ") = "
										+ String.valueOf(_matCell.value) + "\n" + cellParams));
					}
					// Show cell values...
					if (Configuration.showCellValues && _matCell.value > 0) {
						double average = (min + max) / 2;
						Text textValue = new Text("" + new Double(_matCell.value).intValue());
						if (_matCell.value >= 100) {
							textValue.setFont(Font.font("Times", FontWeight.BOLD, 10));
						}
						if (_matCell.value >= 1000) {
							textValue.setFont(Font.font("Times", FontWeight.BOLD, 9));
						}
						if (_matCell.value < 100) {
							textValue.setFont(Font.font("Times", FontWeight.BOLD, 12));
						}
						if (_matCell.value < 10) {
							textValue.setTranslateX(6);
						}
						if (_matCell.value >= 10) {
							textValue.setTranslateX(2.5);
						}
						if (_matCell.value >= 100) {
							textValue.setTranslateX(1.0);
						}
						if (_matCell.value >= 1000) {
							textValue.setTranslateX(-1.5);
						}
						textValue.setTranslateY(15);
						textValue.toFront();
						if (_matCell.value >= average) {
							textValue.setFill(Color.WHITE);
						} else {
							textValue.setFill(Color.BLACK);
						}
						cell.getChildren().add(textValue);
					}
					cell.setId("" + _matCell.row + "," + _matCell.col);
					matrixGroup.getChildren().add(cell);

					block = new Rectangle(40, 30);
					block.setFill(new Color(50 / 255.0, 75 / 255.0, 180.0 / 255.0, 0.5));
					block.setX(-40);
					block.setY(-30);
					block.toFront();
					Text text = new Text(
							domino.getDescriptor().getRowType() + " | " + domino.getDescriptor().getColType());

					text.setFont(Font.font("Times", FontWeight.BOLD, 11));
					text.setTranslateX(-38);
					text.setTranslateY(-06);
					text.setFill(Color.WHITE);
					text.toFront();

					Group cellBlock = new Group(block, text);
					String tooltip = domino.getDescriptor().getRowType() + " | " + domino.getDescriptor().getColType()
							+ "\n";
					int totalNonZero = Prov2DominoesUtil.getNonZeroTotal(domino.getCrsMatrix());
					tooltip += "Total (non zero): " + totalNonZero + "\n";
					double minNonZero = Prov2DominoesUtil.getNonZeroMin(domino.getCrsMatrix());
					tooltip += "Min (non zero): " + minNonZero + "\n";
					double averageNonZero = Prov2DominoesUtil.getNonZeroAverage(domino.getCrsMatrix());
					tooltip += "Average (non zero): " + averageNonZero + "\n";
					double sdNonZero = Prov2DominoesUtil.getNonZeroStandardScore(domino.getCrsMatrix(), averageNonZero);
					tooltip += "Z-Score (non zero): " + sdNonZero + "\n";
					double max = domino.getCrsMatrix().max();
					tooltip += "Max: " + max + "\n";
					Tooltip.install(cellBlock, new Tooltip(tooltip));
					matrixGroup.getChildren().add(cellBlock);

					this.recHeaders.add(front);

					// configure cell context menu...
					ContextMenu contextCell = new ContextMenu();

					Menu boundMenu = new Menu("Bound");
					Menu highlightVertical = new Menu("Vertical");
					Menu highlightHorizontal = new Menu("Horizontal");
					MenuItem drawLine = new MenuItem("Draw to line...");
					MenuItem undrawLine = new MenuItem("Undraw to line...");
					MenuItem exportPNG = new MenuItem("Export to PNG...");

					Menu unboundMenu = new Menu("Unbound");
					Menu removeHorizontal = new Menu("Horizontal");
					Menu removeVertical = new Menu("Vertical");
					Menu cellMenu = new Menu("Cell");
					Menu removeCellMenu = new Menu("Cell");

					MenuItem cellTop = new MenuItem("Top");
					MenuItem cellBottom = new MenuItem("Bottom");
					MenuItem cellLeft = new MenuItem("Left");
					MenuItem cellRight = new MenuItem("Right");

					MenuItem removeCellTop = new MenuItem("Top");
					MenuItem removeCellBottom = new MenuItem("Bottom");
					MenuItem removeCellLeft = new MenuItem("Left");
					MenuItem removeCellRight = new MenuItem("Right");

					MenuItem hightlightCell = new MenuItem("Cell");
					MenuItem hightlightTopRow = new MenuItem("Top Row");
					MenuItem hightlightBottomRow = new MenuItem("Bottom Row");
					MenuItem hightlightLeftColumn = new MenuItem("Left Column");
					MenuItem hightlightRightColumn = new MenuItem("Right Column");

					MenuItem removeCellHighlight = new MenuItem("Cell");
					MenuItem removeTopRow = new MenuItem("Top Row");
					MenuItem removeBottomRow = new MenuItem("Bottom Row");
					MenuItem removeLeftColumn = new MenuItem("Left Column");
					MenuItem removeRightColumn = new MenuItem("Right Column");

					cellMenu.getItems().addAll(cellTop, cellBottom, cellLeft, cellRight);
					removeCellMenu.getItems().addAll(removeCellTop, removeCellBottom, removeCellLeft, removeCellRight);

					highlightHorizontal.getItems().addAll(hightlightTopRow, hightlightBottomRow);
					highlightVertical.getItems().addAll(hightlightLeftColumn, hightlightRightColumn);
					boundMenu.getItems().addAll(hightlightCell, cellMenu, highlightHorizontal, highlightVertical);
					removeHorizontal.getItems().addAll(removeTopRow, removeBottomRow);
					removeVertical.getItems().addAll(removeLeftColumn, removeRightColumn);
					unboundMenu.getItems().addAll(removeCellHighlight, removeCellMenu, removeHorizontal,
							removeVertical);

					contextCell.getItems().addAll(boundMenu, unboundMenu, drawLine, undrawLine, exportPNG);
					drawLine.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							Group cellGroup = (Group) ((MenuItem) event.getSource()).getParentPopup().getOwnerNode();
							setSource(cellGroup);
						}
					});
					exportPNG.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							Group cellGroup = (Group) ((MenuItem) event.getSource()).getParentPopup().getOwnerNode();
							saveAsPng((Group) cellGroup.getParent());
						}

						public void saveAsPng(Group matrix) {
							WritableImage image = matrix.snapshot(new SnapshotParameters(), null);

							FileChooser fileChooser = new FileChooser();
							fileChooser.setInitialDirectory(new File(Configuration.lastDirectory));
							fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PNG", "*.png"));
							fileChooser.setInitialFileName("matrix");
							File file = fileChooser.showSaveDialog(App.getStage());

							try {
								if (file != null) {
									ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
								}
							} catch (IOException e) {
								e.printStackTrace();
								App.alertException(e, "Something wrong exporting matrix to PGN!");
							}
						}
					});
					boundMenu.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							double x = _matCell.col * (cellSpace + padding) + padding;
							double y = _matCell.row * (cellSpace + padding) + padding;
							if (((MenuItem) event.getTarget()).getText().equals(hightlightCell.getText())) {
								Line topRowCell = new Line(x + 2, y, x + cellSpace - 2, y);
								Group cellGroup = (Group) ((Menu) event.getSource()).getParentPopup().getOwnerNode();
								topRowCell.setId(cellGroup.getId() + "topRowCell");
								topRowCell.setStrokeWidth(4);
								topRowCell.setStrokeLineCap(StrokeLineCap.ROUND);
								matrixGroup.getChildren().add(topRowCell);

								Line bottomRowCell = new Line(x + 2, y + cellSpace - 2, x + cellSpace - 2,
										y + cellSpace - 2);
								bottomRowCell.setId(cellGroup.getId() + "bottomRowCell");
								bottomRowCell.setStrokeWidth(4);
								bottomRowCell.setStrokeLineCap(StrokeLineCap.ROUND);
								matrixGroup.getChildren().add(bottomRowCell);

								Line leftColumnCell = new Line(x, y, x, y + cellSpace - 2);
								leftColumnCell.setId(cellGroup.getId() + "leftColumnCell");
								leftColumnCell.setStrokeWidth(4);
								leftColumnCell.setStrokeLineCap(StrokeLineCap.ROUND);
								matrixGroup.getChildren().add(leftColumnCell);

								Line rightColumnCell = new Line(x + cellSpace, y, x + cellSpace, y + cellSpace - 2);
								rightColumnCell.setId(cellGroup.getId() + "rightColumnCell");
								rightColumnCell.setStrokeWidth(4);
								rightColumnCell.setStrokeLineCap(StrokeLineCap.ROUND);
								matrixGroup.getChildren().add(rightColumnCell);

							}
						}

					});

					unboundMenu.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							if (((MenuItem) event.getTarget()).getText().equals(removeCellHighlight.getText())) {
								Group nodeGroup = (Group) ((Menu) event.getSource()).getParentPopup().getOwnerNode();
								removeNode(nodeGroup, nodeGroup.getId() + "topRowCell");
								removeNode(nodeGroup, nodeGroup.getId() + "bottomRowCell");
								removeNode(nodeGroup, nodeGroup.getId() + "leftColumnCell");
								removeNode(nodeGroup, nodeGroup.getId() + "rightColumnCell");
							}
						}

						private void removeNode(Group cellGroup, String nodeId) {
							Group matrix = (Group) cellGroup.getParent();
							Line line = null;
							for (Node matrixNode : matrix.getChildren()) {
								if (matrixNode != null && matrixNode.getId() != null
										&& matrixNode.getId().equals(nodeId)) {
									line = (Line) matrixNode;
									break;
								}
							}
							if (line != null) {
								((Group) cellGroup.getParent()).getChildren().remove(line);
							}
						}
					});

					cellMenu.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							double x = _matCell.col * (cellSpace + padding) + padding;
							double y = _matCell.row * (cellSpace + padding) + padding;
							Group cellGroup = (Group) ((Menu) event.getSource()).getParentMenu().getParentPopup()
									.getOwnerNode();
							if (((MenuItem) event.getTarget()).getText().equals(cellTop.getText())) {
								Line topRowCell = new Line(x + 2, y, x + cellSpace - 2, y);
								topRowCell.setId(cellGroup.getId() + "topCell");
								topRowCell.setStrokeWidth(4);
								topRowCell.setStrokeLineCap(StrokeLineCap.ROUND);
								matrixGroup.getChildren().add(topRowCell);
							} else if (((MenuItem) event.getTarget()).getText().equals(cellBottom.getText())) {
								Line bottomRowCell = new Line(x + 2, y + cellSpace - 2, x + cellSpace - 2,
										y + cellSpace - 2);
								bottomRowCell.setId(cellGroup.getId() + "bottomCell");
								bottomRowCell.setStrokeWidth(4);
								bottomRowCell.setStrokeLineCap(StrokeLineCap.ROUND);
								matrixGroup.getChildren().add(bottomRowCell);
							} else if (((MenuItem) event.getTarget()).getText().equals(cellLeft.getText())) {
								Line leftColumnCell = new Line(x, y, x, y + cellSpace - 2);
								leftColumnCell.setId(cellGroup.getId() + "leftCell");
								leftColumnCell.setStrokeWidth(4);
								leftColumnCell.setStrokeLineCap(StrokeLineCap.ROUND);
								matrixGroup.getChildren().add(leftColumnCell);
							} else if (((MenuItem) event.getTarget()).getText().equals(cellRight.getText())) {
								Line rightColumnCell = new Line(x + cellSpace, y, x + cellSpace, y + cellSpace - 2);
								rightColumnCell.setId(cellGroup.getId() + "rightCell");
								rightColumnCell.setStrokeWidth(4);
								rightColumnCell.setStrokeLineCap(StrokeLineCap.ROUND);
								matrixGroup.getChildren().add(rightColumnCell);
							}
						}
					});

					removeCellMenu.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							if (((MenuItem) event.getTarget()).getText().equals(removeCellTop.getText())) {
								Group nodeGroup = (Group) ((Menu) event.getSource()).getParentMenu().getParentPopup()
										.getOwnerNode();
								removeNode(nodeGroup, nodeGroup.getId() + "topCell");
							} else if (((MenuItem) event.getTarget()).getText().equals(removeCellBottom.getText())) {
								Group nodeGroup = (Group) ((Menu) event.getSource()).getParentMenu().getParentPopup()
										.getOwnerNode();
								removeNode(nodeGroup, nodeGroup.getId() + "bottomCell");
							} else if (((MenuItem) event.getTarget()).getText().equals(removeCellLeft.getText())) {
								Group nodeGroup = (Group) ((Menu) event.getSource()).getParentMenu().getParentPopup()
										.getOwnerNode();
								removeNode(nodeGroup, nodeGroup.getId() + "leftCell");
							} else if (((MenuItem) event.getTarget()).getText().equals(removeCellRight.getText())) {
								Group nodeGroup = (Group) ((Menu) event.getSource()).getParentMenu().getParentPopup()
										.getOwnerNode();
								removeNode(nodeGroup, nodeGroup.getId() + "rightCell");
							}
						}

						private void removeNode(Group cellGroup, String nodeId) {
							Group matrix = (Group) cellGroup.getParent();
							Line line = null;
							for (Node matrixNode : matrix.getChildren()) {
								if (matrixNode != null && matrixNode.getId() != null
										&& matrixNode.getId().equals(nodeId)) {
									line = (Line) matrixNode;
									break;
								}
							}
							if (line != null) {
								((Group) cellGroup.getParent()).getChildren().remove(line);
							}
						}
					});

					highlightHorizontal.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							double y = _matCell.row * (cellSpace + padding) + padding;
							if (((MenuItem) event.getTarget()).getText().equals(hightlightTopRow.getText())) {
								Line topRow = new Line(2, y, (_nCols * (cellSpace + padding) + padding) - 2, y);
								Group cellGroup = (Group) ((Menu) event.getSource()).getParentMenu().getParentPopup()
										.getOwnerNode();
								topRow.setId(cellGroup.getId().split(",")[0] + "topRow");
								topRow.setStrokeWidth(4);
								topRow.setStrokeLineCap(StrokeLineCap.ROUND);
								matrixGroup.getChildren().add(topRow);
							} else if (((MenuItem) event.getTarget()).getText().equals(hightlightBottomRow.getText())) {
								Line bottomRow = new Line(2, y + cellSpace,
										(_nCols * (cellSpace + padding) + padding) - 2, y + cellSpace);
								Group cellGroup = (Group) ((Menu) event.getSource()).getParentMenu().getParentPopup()
										.getOwnerNode();
								bottomRow.setId(cellGroup.getId().split(",")[0] + "bottomRow");
								bottomRow.setStrokeWidth(4);
								bottomRow.setStrokeLineCap(StrokeLineCap.ROUND);
								matrixGroup.getChildren().add(bottomRow);
							}
						}

					});

					removeHorizontal.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							if (((MenuItem) event.getTarget()).getText().equals(removeTopRow.getText())) {
								Group nodeGroup = (Group) ((Menu) event.getSource()).getParentMenu().getParentPopup()
										.getOwnerNode();
								removeNode(nodeGroup, nodeGroup.getId().split(",")[0] + "topRow");
							} else if (((MenuItem) event.getTarget()).getText().equals(removeBottomRow.getText())) {
								Group nodeGroup = (Group) ((Menu) event.getSource()).getParentMenu().getParentPopup()
										.getOwnerNode();
								removeNode(nodeGroup, nodeGroup.getId().split(",")[0] + "bottomRow");
							}
						}

						private void removeNode(Group cellGroup, String nodeId) {
							Group matrix = (Group) cellGroup.getParent();
							Line line = null;
							for (Node matrixNode : matrix.getChildren()) {
								if (matrixNode != null && matrixNode.getId() != null
										&& matrixNode.getId().equals(nodeId)) {
									line = (Line) matrixNode;
									break;
								}
							}
							if (line != null) {
								((Group) cellGroup.getParent()).getChildren().remove(line);
							}
						}
					});

					highlightVertical.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							double x = _matCell.col * (cellSpace + padding) + padding;
							if (((MenuItem) event.getTarget()).getText().equals(hightlightLeftColumn.getText())) {
								Line leftColumn = new Line(x, 2, x, (_nRows * (cellSpace + padding) + padding) - 2);
								Group cellGroup = (Group) ((Menu) event.getSource()).getParentMenu().getParentPopup()
										.getOwnerNode();
								leftColumn.setId(cellGroup.getId().split(",")[1] + "leftColumn");
								leftColumn.setStrokeWidth(4);
								leftColumn.setStrokeLineCap(StrokeLineCap.ROUND);
								matrixGroup.getChildren().add(leftColumn);

							} else if (((MenuItem) event.getTarget()).getText()
									.equals(hightlightRightColumn.getText())) {
								Line rightColumn = new Line(x + cellSpace, 2, x + cellSpace,
										(_nRows * (cellSpace + padding) + padding) - 2);
								Group cellGroup = (Group) ((Menu) event.getSource()).getParentMenu().getParentPopup()
										.getOwnerNode();
								rightColumn.setId(cellGroup.getId().split(",")[1] + "rightColumn");
								rightColumn.setStrokeWidth(4);
								rightColumn.setStrokeLineCap(StrokeLineCap.ROUND);
								matrixGroup.getChildren().add(rightColumn);
							}
						}
					});

					removeVertical.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							if (((MenuItem) event.getTarget()).getText().equals(removeLeftColumn.getText())) {
								Group nodeGroup = (Group) ((Menu) event.getSource()).getParentMenu().getParentPopup()
										.getOwnerNode();
								removeNode(nodeGroup, nodeGroup.getId().split(",")[1] + "leftColumn");
							} else if (((MenuItem) event.getTarget()).getText().equals(removeRightColumn.getText())) {
								Group nodeGroup = (Group) ((Menu) event.getSource()).getParentMenu().getParentPopup()
										.getOwnerNode();
								removeNode(nodeGroup, nodeGroup.getId().split(",")[1] + "rightColumn");
							}
						}

						private void removeNode(Group cellGroup, String nodeId) {
							Group matrix = (Group) cellGroup.getParent();
							Line line = null;
							for (Node matrixNode : matrix.getChildren()) {
								if (matrixNode != null && matrixNode.getId() != null
										&& matrixNode.getId().equals(nodeId)) {
									line = (Line) matrixNode;
									break;
								}
							}
							if (line != null) {
								((Group) cellGroup.getParent()).getChildren().remove(line);
							}
						}
					});

					cell.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent e) {
							if (e.getButton() == MouseButton.SECONDARY) {
								contextCell.show(cell, e.getScreenX(), e.getScreenY());
							} else {
								contextCell.hide();
							}
						}
					});
					cell.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
						@Override
						public void handle(MouseEvent e) {
							if (e.getButton() == MouseButton.PRIMARY) {
								Group source = getSource();
								if (source != null) {
									Line line = null;
									double srcRowCell = Double.valueOf(source.getId().split(",")[0]);
									double srcColCell = Double.valueOf(source.getId().split(",")[1]);
									double x = srcColCell * (cellSpace + padding) + padding;
									double y = srcRowCell * (cellSpace + padding) + padding;

									double dstRowCell = Double.valueOf(cell.getId().split(",")[0]);
									double dstColCell = Double.valueOf(cell.getId().split(",")[1]);
									if (source.getTranslateX() == cell.getTranslateX()) {
										double diff = dstRowCell - srcRowCell;
										line = new Line(x + 10, y + 9 + ((diff > 0 ? 2 : 0)), x + 10,
												(y + (diff + 1) * cellSpace - 9) + (diff > 0 ? -2 : 0));

									} else if (source.getTranslateY() == cell.getTranslateY()) {
										double diff = dstColCell - srcColCell;
										line = new Line((x + (diff + 1) * cellSpace - 10), y + 9, x + 10, y + 9);
									}
									if (line != null) {
										line.setId(source.getId() + "-" + cell.getId() + "line");
										line.setStrokeWidth(4);
										line.setStrokeLineCap(StrokeLineCap.ROUND);
										matrixGroup.getChildren().add(line);
									}
								} else {
									source = null;
								}
							}
						}
					});
					undrawLine.setOnAction(new EventHandler<ActionEvent>() {
						@Override
						public void handle(ActionEvent event) {
							if (((MenuItem) event.getTarget()).getText().equals(undrawLine.getText())) {
								Group nodeGroup = (Group) ((MenuItem) event.getSource()).getParentPopup()
										.getOwnerNode();
								removeNode(nodeGroup, nodeGroup.getId());
							}
						}

						private void removeNode(Group cellGroup, String nodeId) {
							Group matrix = (Group) cellGroup.getParent();
							Line line = null;
							for (Node matrixNode : matrix.getChildren()) {
								if (matrixNode != null && matrixNode.getId() != null
										&& matrixNode.getId().endsWith("line")
										&& (matrixNode.getId().split("-")[0].equals(nodeId)
												|| matrixNode.getId().split("-")[1].contains(nodeId))) {
									line = (Line) matrixNode;
									break;
								}
							}
							if (line != null) {
								((Group) cellGroup.getParent()).getChildren().remove(line);
							}
						}
					});
				}

				this.setOnScroll(new EventHandler<ScrollEvent>() {

					@Override
					public void handle(ScrollEvent event) {
						double srcX = event.getX() - matrixGroup.getTranslateX() - matrixGroup.prefWidth(-1) / 2;
						double srcY = event.getY() - matrixGroup.getTranslateY() - matrixGroup.prefHeight(-1) / 2;
						double trgX = srcX;
						double trgY = srcY;

						double factor = 0.05;

						if (event.getDeltaY() < 0 && matrixGroup.getScaleX() > minZoom) {
							matrixGroup.setScaleX(matrixGroup.getScaleX() * (1 - factor));
							matrixGroup.setScaleY(matrixGroup.getScaleY() * (1 - factor));
							trgX = srcX * (1 - factor);
							trgY = srcY * (1 - factor);
						} else if (event.getDeltaY() > 0 && matrixGroup.getScaleX() < maxZoom) {
							matrixGroup.setScaleX(matrixGroup.getScaleX() * (1 + factor));
							matrixGroup.setScaleY(matrixGroup.getScaleY() * (1 + factor));
							trgX = srcX * (1 + factor);
							trgY = srcY * (1 + factor);
						}
						matrixGroup.setTranslateX(matrixGroup.getTranslateX() - (trgX - srcX));
						matrixGroup.setTranslateY(matrixGroup.getTranslateY() - (trgY - srcY));

					}
				});
				this.setOnMouseDragged(new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {

						double offsetX = event.getSceneX() - srcSceneX;
						double offsetY = event.getSceneY() - srcSceneY;
						double newTranslateX = srcTranslateX + offsetX;
						double newTranslateY = srcTranslateY + offsetY;

						matrixGroup.setTranslateX(newTranslateX);
						matrixGroup.setTranslateY(newTranslateY);

					}
				});
				this.setOnMousePressed(new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {

						srcSceneX = event.getSceneX();
						srcSceneY = event.getSceneY();
						srcTranslateX = matrixGroup.getTranslateX();
						srcTranslateY = matrixGroup.getTranslateY();

						cursorProperty().set(Cursor.CLOSED_HAND);
					}
				});
				this.setOnMouseReleased(new EventHandler<MouseEvent>() {

					@Override
					public void handle(MouseEvent event) {

						cursorProperty().set(Cursor.OPEN_HAND);
					}
				});

				this.getChildren().add(new FlowPane(matrixGroup));
			}
		}
	}

	protected Group getSource() {
		return this.source;
	}

	protected void setSource(Group cellGroup) {
		this.source = cellGroup;
	}

}
