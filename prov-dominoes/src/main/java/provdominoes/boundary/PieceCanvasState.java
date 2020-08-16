package provdominoes.boundary;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.Group;
import javafx.scene.control.MenuItem;
import javafx.scene.shape.Rectangle;
import provdominoes.domain.Dominoes;

public class PieceCanvasState {
	
	public static final int COMBINATION_SUM = 1;
	public static final int COMBINATION_SUBTRACTION = 2;
	public static final int COMBINATION_MULTIPLICATION = 3;
	public static final int COMBINATION_TRANSPOSING = 4;

	private ArrayList<Dominoes> dominoes;
	private ArrayList<Group> pieces;
	private Rectangle background;
	private int combination = 0;
	private int indexFirstOperatorCombination;
	private int indexSecondOperatorCombination;
	private double srcSceneX;
	private double srcSceneY;
	private double srcTranslateX;
	private double srcTranslateY;
	private double padding;
	private boolean transposing;

	public PieceCanvasState(int indexFirstOperatorMultiplication, int indexSecondOperatorMultiplication,int combination, double padding,
			boolean transposing, List<MenuItem> menuItemAggregateRow, List<MenuItem> menuItemAggregateCol) {

		this.indexFirstOperatorCombination = indexFirstOperatorMultiplication;
		this.indexSecondOperatorCombination = indexSecondOperatorMultiplication;
		this.combination = combination;
		this.padding = padding;
		this.transposing = transposing;
	}

	public PieceCanvasState clone() {
		PieceCanvasState clone = new PieceCanvasState(background, indexFirstOperatorCombination, indexSecondOperatorCombination, combination,
				padding, transposing, srcSceneX, srcSceneY, srcTranslateX, srcTranslateY);

		this.dominoes = new ArrayList<>(dominoes);
		this.pieces = new ArrayList<>(pieces);
		return clone;
	}

	public PieceCanvasState(Rectangle background2, int indexFirstOperatorMultiplication2,
			int indexSecondOperatorMultiplication2, int combination2, double padding2, boolean transposing2, double srcSceneX2,
			double srcSceneY2, double srcTranslateX2, double srcTranslateY2) {
		this.background = background2;
		this.indexFirstOperatorCombination = indexFirstOperatorMultiplication2;
		this.indexSecondOperatorCombination = indexSecondOperatorMultiplication2;
		this.combination = combination2;
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

	public int getIndexFirstOperatorCombination() {
		return indexFirstOperatorCombination;
	}

	public void setIndexFirstOperatorCombination(int indexFirstOperatorMultiplication) {
		this.indexFirstOperatorCombination = indexFirstOperatorMultiplication;
	}

	public int getIndexSecondOperatorCombination() {
		return indexSecondOperatorCombination;
	}

	public void setIndexSecondOperatorCombination(int indexSecondOperatorMultiplication) {
		this.indexSecondOperatorCombination = indexSecondOperatorMultiplication;
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

	public Group getPiece(String identifier) {
		for (Dominoes d : dominoes) {
			if (d.getId().equals(identifier)) {
				return pieces.get(dominoes.indexOf(d));
			}
		}
		return null;
	}

	public int getCombination() {
		return combination;
	}

	public void setCombination(int combination) {
		this.combination = combination;
	}

}