package boundary;

import java.util.ArrayList;
import java.util.List;

import com.josericardojunior.domain.Dominoes;

import javafx.scene.Group;
import javafx.scene.control.MenuItem;
import javafx.scene.shape.Rectangle;

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
	private List<MenuItem> menuItemAggregateRow;
	private List<MenuItem> menuItemAggregateCol;

	public MoveData(int indexFirstOperatorMultiplication, int indexSecondOperatorMultiplication, double padding,
			boolean transposing, List<MenuItem> menuItemAggregateRow, List<MenuItem> menuItemAggregateCol) {
		this.indexFirstOperatorMultiplication = indexFirstOperatorMultiplication;
		this.indexSecondOperatorMultiplication = indexSecondOperatorMultiplication;
		this.padding = padding;
		this.transposing = transposing;
		this.menuItemAggregateRow = menuItemAggregateRow;
		this.menuItemAggregateCol = menuItemAggregateCol;
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

	public List<MenuItem> getMenuItemAggregateRow() {
		return menuItemAggregateRow;
	}

	public void setMenuItemAggregateRow(List<MenuItem> menuItemAggregateRow) {
		this.menuItemAggregateRow = menuItemAggregateRow;
	}

	public List<MenuItem> getMenuItemAggregateCol() {
		return menuItemAggregateCol;
	}

	public void setMenuItemAggregateCol(List<MenuItem> menuItemAggregateCol) {
		this.menuItemAggregateCol = menuItemAggregateCol;
	}
}