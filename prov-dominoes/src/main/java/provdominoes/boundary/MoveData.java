package provdominoes.boundary;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.control.MenuItem;
import javafx.scene.shape.Rectangle;
import provdominoes.domain.Dominoes;

public class MoveData {

	private ArrayList<Dominoes> dominoes;
	private ArrayList<Group> pieces;
	private Rectangle background;
	private int indexFirstOperatorMultiplication;
	private int indexSecondOperatorMultiplication;
	private double srcSceneX;
	private double srcSceneY;
	private double srcTranslateX;
	private double srcTranslateY;
	private double padding;
	private boolean transposing;
	private List<MenuItem> menuItemAggregateRows;
	private List<MenuItem> menuItemAggregateCols;

	public MoveData(int indexFirstOperatorMultiplication, int indexSecondOperatorMultiplication, double padding,
			boolean transposing, List<MenuItem> menuItemAggregateRow, List<MenuItem> menuItemAggregateCol) {

		this.indexFirstOperatorMultiplication = indexFirstOperatorMultiplication;
		this.indexSecondOperatorMultiplication = indexSecondOperatorMultiplication;
		this.padding = padding;
		this.transposing = transposing;
		this.menuItemAggregateRows = menuItemAggregateRow;
		this.menuItemAggregateCols = menuItemAggregateCol;
	}

	public MoveData clone() {
		MoveData clone = new MoveData(background, indexFirstOperatorMultiplication, indexSecondOperatorMultiplication,
				padding, transposing, srcSceneX, srcSceneY, srcTranslateX, srcTranslateY);

		this.dominoes = new ArrayList<>(dominoes);
		this.pieces = new ArrayList<>(pieces);
		this.menuItemAggregateRows = new ArrayList<>(menuItemAggregateRows);
		this.menuItemAggregateCols = new ArrayList<>(menuItemAggregateCols);
		return clone;
	}

	public MoveData(Rectangle background2, int indexFirstOperatorMultiplication2,
			int indexSecondOperatorMultiplication2, double padding2, boolean transposing2, double srcSceneX2,
			double srcSceneY2, double srcTranslateX2, double srcTranslateY2) {
		this.background = background2;
		this.indexFirstOperatorMultiplication = indexFirstOperatorMultiplication2;
		this.indexSecondOperatorMultiplication = indexSecondOperatorMultiplication2;
		this.padding = padding2;
		this.transposing = transposing2;
		this.srcSceneX = srcSceneX2;
		this.srcSceneY = srcSceneY2;
		this.srcTranslateX = srcTranslateX2;
		this.srcTranslateY = srcTranslateY2;
	}

	public ArrayList<Dominoes> getDominoes() {
		return dominoes;
	}

	public void setDominoes(ArrayList<Dominoes> dominoes) {
		this.dominoes = dominoes;
	}

	public ArrayList<Group> getPieces() {
		return pieces;
	}

	public void setPieces(ArrayList<Group> pieces) {
		this.pieces = pieces;
	}

	public Rectangle getBackground() {
		return background;
	}

	public void setBackground(Rectangle background) {
		this.background = background;
	}

	public int getIndexFirstOperatorMultiplication() {
		return indexFirstOperatorMultiplication;
	}

	public void setIndexFirstOperatorMultiplication(int indexFirstOperatorMultiplication) {
		this.indexFirstOperatorMultiplication = indexFirstOperatorMultiplication;
	}

	public int getIndexSecondOperatorMultiplication() {
		return indexSecondOperatorMultiplication;
	}

	public void setIndexSecondOperatorMultiplication(int indexSecondOperatorMultiplication) {
		this.indexSecondOperatorMultiplication = indexSecondOperatorMultiplication;
	}

	public double getSrcSceneX() {
		return srcSceneX;
	}

	public void setSrcSceneX(double srcSceneX) {
		this.srcSceneX = srcSceneX;
	}

	public double getSrcSceneY() {
		return srcSceneY;
	}

	public void setSrcSceneY(double srcSceneY) {
		this.srcSceneY = srcSceneY;
	}

	public double getSrcTranslateX() {
		return srcTranslateX;
	}

	public void setSrcTranslateX(double srcTranslateX) {
		this.srcTranslateX = srcTranslateX;
	}

	public double getSrcTranslateY() {
		return srcTranslateY;
	}

	public void setSrcTranslateY(double srcTranslateY) {
		this.srcTranslateY = srcTranslateY;
	}

	public double getPadding() {
		return padding;
	}

	public void setPadding(double padding) {
		this.padding = padding;
	}

	public boolean isTransposing() {
		return transposing;
	}

	public void setTransposing(boolean transposing) {
		this.transposing = transposing;
	}

	public List<MenuItem> getMenuItemAggregateRows() {
		return menuItemAggregateRows;
	}

	public void setMenuItemAggregateRows(List<MenuItem> menuItemAggregateRow) {
		this.menuItemAggregateRows = menuItemAggregateRow;
	}

	public List<MenuItem> getMenuItemAggregateCols() {
		return menuItemAggregateCols;
	}

	public void setMenuItemAggregateCols(List<MenuItem> menuItemAggregateCol) {
		this.menuItemAggregateCols = menuItemAggregateCol;
	}

	public Group getPiece(String identifier) {
		for (Dominoes d : dominoes) {
			if (d.getId().equals(identifier)) {
				return pieces.get(dominoes.indexOf(d));
			}
		}
		return null;
	}

}