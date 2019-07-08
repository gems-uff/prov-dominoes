package com.josericardojunior.domain;

import javafx.scene.control.Tooltip;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.ProvMatrix.Relation;

import com.josericardojunior.arch.MatrixOperations;
import com.josericardojunior.arch.MatrixOperationsGPU;

public final class Dominoes {
	public final static double GRAPH_WIDTH = 100;
	public final static double GRAPH_HEIGHT = 50;

	public static Color COLOR_BACK = new Color(1, 1, 1, 1);
	public static Color COLOR_BORDER = new Color(0.56, 0.56, 0.56, 1);
	public static Color COLOR_LINE = new Color(0.56, 0.76, 0.56, 1);
	public static Color COLOR_BORDER_DERIVED = new Color(0.36, 0.36, 0.36, 1);
	public static Color COLOR_NORMAL_FONT = new Color(0, 0, 0, 1);
	public static Color COLOR_NO_OPERATION_FONT = new Color(1, 0, 0, 1);
	public static Color COLOR_OPERATE_FONT = new Color(0, 1, 0, 1);
	public static Color COLOR_HISTORIC = new Color(0.86, 0.86, 0.86, 1);
	public static Color COLOR_INIVISIBLE = new Color(0, 0, 0, 0);
	public static Color COLOR_TYPE = COLOR_BORDER;

	public static String DEVICE_GPU = "GPU";
	public static String DEVICE_CPU = "CPU";

	/*
	 * This variables are used to know the sequence of the matrix information in the
	 * hour of to save/load in the .TXT format
	 */
	public final static int INDEX_TYPE = 0;
	public final static int INDEX_ID_ROW = 1;
	public final static int INDEX_ID_COL = 2;
	public final static int INDEX_HEIGHT = 3;
	public final static int INDEX_WIDTH = 4;
	public final static int INDEX_HIST = 5;
	public final static int INDEX_MATRIX = 6;

	public final static int INDEX_SIZE = 7;

	/*
	 * This variables are used to know the sequence of the elements, the Group
	 * (Graphicaly) relative to this Domino, in time of the insert
	 */
	public final static int GRAPH_BORDER = 0;
	public final static int GRAPH_FILL = 1;
	public final static int GRAPH_LINE = 2;
	public final static int GRAPH_HISTORIC = 3;
	public final static int GRAPH_TYPE = 4;
	public final static int GRAPH_TRANSPOSE_ID_ROW = 6;
	public final static int GRAPH_TRANSPOSE_ID_COL = 5;
	public final static int GRAPH_ID_ROW = 5;
	public final static int GRAPH_ID_COL = 6;
	public final static int GRAPH_NORMAL_FONT_SIZE = 15;
	public final static int GRAPH_AGGREG_FONT_SIZE = 12;

	private final static double GRAPH_ARC = 10;

	public final static int GRAPH_SIZE = 8;

	/*
	 * This variables are used to know the type of matrix
	 */
	public final static int TYPE_BASIC = 0;
	public final static int TYPE_DERIVED = 1;
	public final static int TYPE_SUPPORT = 2;
	public final static int TYPE_CONFIDENCE = 3;
	public final static int TYPE_LIFT = 4;
	private static final int TYPE_TRANSITIVE_CLOSURE = 5;
	private static final int TYPE_BINARIZED = 6;
	private static final int TYPE_INVERTED = 7;
	private static final int TYPE_DIAGONAL = 8;
	private static final int TYPE_UPPER_DIAGONAL = 9;
	private static final int TYPE_LOWER_DIAGONAL = 10;
	public final static String TYPE_BASIC_CODE = "B";
	public final static String TYPE_DERIVED_CODE = "D";
	public final static String TYPE_SUPPORT_CODE = "S";
	public final static String TYPE_CONFIDENCE_CODE = "C";
	public final static String TYPE_LIFT_CODE = "L";
	public final static String TYPE_TRANSITIVE_CLOSURE_CODE = "T";
	public final static String TYPE_DIAGONAL_CODE = "DG";
	public final static String TYPE_UPPER_DIAGONAL_CODE = "UDG";
	public final static String TYPE_LOWER_DIAGONAL_CODE = "LDG";

	public final static String AGGREG_TEXT = "/SUM ";
	public static final String TYPE_BINARIZED_CODE = "Z";
	public static final String TYPE_INVERTED_CODE = "I";

	private boolean rowIsAggragatable = false;
	private boolean colIsAggragatable = false;
	private Relation relation;
	private String id;
	private String idRow;
	private String idCol;
	private Historic historic;
	private int type;
	private MatrixOperations mat = null;
	private int sourceIndex;
	private String currentDevice = DEVICE_CPU;

	public Dominoes(String _device) {
		this.rowIsAggragatable = false;
		this.colIsAggragatable = false;
		currentDevice = _device;
	}

	/**
	 * Class build. The type, for default, is Basic.
	 *
	 * @param idRow - identifier row of the Dominos matrix
	 * @param idCol - identifier row of the Dominos matrix
	 * @param mat   - matrix2D
	 * @throws IllegalArgumentException - in case of invalid parameters
	 */
	public Dominoes(String idRow, String idCol, MatrixOperations mat, String _device) throws IllegalArgumentException {
		this.rowIsAggragatable = false;
		this.colIsAggragatable = false;
		this.setIdRow(idRow);
		this.setIdCol(idCol);

		this.setMat(mat);

		this.setHistoric(new Historic(idRow, idCol));

		this.type = Dominoes.TYPE_BASIC;
		this.currentDevice = _device;
	}

	public Dominoes(String idRow, String idCol, Relation relation, MatrixOperations mat, String _device)
			throws IllegalArgumentException {
		this(idRow, idCol, mat, _device);
		this.relation = relation;
	}

	/**
	 * Class build. The type, for default, is Derived
	 *
	 * @param type
	 * @param idRow    - identifier row of the Dominos matrix
	 * @param idCol    - identifier row of the Dominos matrix
	 * @param historic - The dominoes historic derivated
	 * @param mat      - matrix2D
	 * @throws IllegalArgumentException - in case of invalid parameters
	 */
	public Dominoes(int type, String idRow, String idCol, Historic historic, MatrixOperationsGPU mat, String _device)
			throws IllegalArgumentException {
		this.rowIsAggragatable = false;
		this.colIsAggragatable = false;
		this.setIdRow(idRow);
		this.setIdCol(idCol);

		this.setMat(mat);

		this.setHistoric(historic);
		if (type == Dominoes.TYPE_BASIC || (type != Dominoes.TYPE_DERIVED && type != Dominoes.TYPE_CONFIDENCE
				&& type != Dominoes.TYPE_SUPPORT && type != Dominoes.TYPE_LIFT
				&& type != Dominoes.TYPE_TRANSITIVE_CLOSURE && type != Dominoes.TYPE_BINARIZED
				&& type != Dominoes.TYPE_INVERTED && type != Dominoes.TYPE_DIAGONAL
				&& type != Dominoes.TYPE_UPPER_DIAGONAL && type != Dominoes.TYPE_LOWER_DIAGONAL)) {
			throw new IllegalArgumentException("Invalid argument.\nThe Type attribute not is defined or not is valid");
		}
		this.type = type;
		this.currentDevice = _device;
	}

	// /**
	// * Class build. The type, for default, is Derived
	// *
	// * @param type
	// * @param idRow - identifier row of the Dominos matrix
	// * @param idCol - identifier row of the Dominos matrix
	// * @throws IllegalArgumentException - in case of invalid parameters
	// */
	// public Dominoes(int type, String idRow, String idCol) throws
	// IllegalArgumentException {
	// this.setIdRow(idRow);
	// this.setIdCol(idCol);
	//
	// this.historic = new ArrayList<>();
	// this.historic.add(idRow);
	// this.historic.add(idCol);
	//
	// this.type = Dominoes.TYPE_BASIC;
	//
	// this.mat = null;
	// }

	/**
	 * Class Builder This function is used when the user to do a multiplication,
	 * this will return a new matrix with data according with a real multiplication.
	 * The type, for default, is the type of the first parameter
	 *
	 * @param firstOperator  The first matrix in this operation
	 * @param secondOperator The second matrix in this operation
	 * @param mat
	 * @return A new matrix
	 */
	/*
	 * public Dominoes(Dominoes firstOperator, Dominoes secondOperator, byte[][]
	 * mat) throws IllegalArgumentException { this.historic = new ArrayList<>();
	 * 
	 * this.historic.addAll(firstOperator.getHistoric());
	 * this.historic.addAll(this.historic.size(), secondOperator.getHistoric());
	 * 
	 * this.setIdRow(firstOperator.getIdRow());
	 * this.setIdCol(secondOperator.getIdCol());
	 * 
	 * this.setMat(mat); this.type = firstOperator.getType(); }
	 */

	/**
	 * From this Dominoes, this function will build a piece (graphically) respective
	 * to this dominoes
	 *
	 * @return - A javafx.scene.Group (Graphic) to draw in scene
	 */
	public Group drawDominoes() {

		Rectangle border = new Rectangle(GRAPH_WIDTH, GRAPH_HEIGHT);
		border.setFill(Dominoes.COLOR_BORDER);
		border.setArcHeight(Dominoes.GRAPH_ARC);
		border.setArcWidth(Dominoes.GRAPH_ARC);
		border.setX(0);
		border.setY(0);

		Rectangle back = new Rectangle(GRAPH_WIDTH - 2, GRAPH_HEIGHT - 2);
		back.setFill(Dominoes.COLOR_BACK);
		back.setArcHeight(Dominoes.GRAPH_ARC);
		back.setArcWidth(Dominoes.GRAPH_ARC);
		back.setX(border.getX() + 1);
		back.setY(border.getY() + 1);

		Rectangle line = new Rectangle(GRAPH_WIDTH / 2 - 1, border.getHeight() - back.getHeight(), 2,
				back.getHeight() - 2);
		line.setFill(Dominoes.COLOR_LINE);
		line.setArcHeight(Dominoes.GRAPH_ARC);
		line.setArcWidth(Dominoes.GRAPH_ARC);

		Text relationText = null;
		Text arrowText = null;
		//
		if (this.relation != null) {
			arrowText = new Text("\u21D2");
			arrowText.setFill(Dominoes.COLOR_NORMAL_FONT);
			arrowText.setFont(new Font("Times", 10));
			arrowText.toFront();
			arrowText.setX(GRAPH_WIDTH / 2 - 12);
			arrowText.setY(border.getHeight() - back.getHeight() + 10);

			relationText = new Text(this.relation.getAbbreviate());
			relationText.setFill(Dominoes.COLOR_NORMAL_FONT);
			relationText.setFont(new Font("Times", 10));
			relationText.toFront();
			relationText.setX(GRAPH_WIDTH / 2 - 20);
			relationText.setY(border.getHeight() - back.getHeight() + 33);
			relationText.setRotate(90);
		}

		Text idRow = new Text(this.getIdRow());
		idRow.setFill(Dominoes.COLOR_NORMAL_FONT);
		idRow.setX(5);
		idRow.setY(2 * Dominoes.GRAPH_HEIGHT / 5);
		idRow.toFront();
		if (this.getIdRow().startsWith(Dominoes.AGGREG_TEXT))
			idRow.setFont(new Font("Arial", Dominoes.GRAPH_AGGREG_FONT_SIZE));
		else
			idRow.setFont(new Font("Arial", Dominoes.GRAPH_NORMAL_FONT_SIZE));

		Text idCol = new Text(this.getIdCol());
		idCol.setFill(Dominoes.COLOR_NORMAL_FONT);
		idCol.setX(Dominoes.GRAPH_WIDTH / 2 + 5);
		idCol.setY(2 * Dominoes.GRAPH_HEIGHT / 5);
		idCol.toFront();
		if (this.getIdCol().startsWith(Dominoes.AGGREG_TEXT))
			idCol.setFont(new Font("Arial", Dominoes.GRAPH_AGGREG_FONT_SIZE));
		else
			idCol.setFont(new Font("Arial", Dominoes.GRAPH_NORMAL_FONT_SIZE));

		String auxHistoric = this.historic.toString();
		Text historic;
		if (auxHistoric.length() <= 24) {
			historic = new Text(auxHistoric);
		} else {
			historic = new Text(auxHistoric.substring(0, 24) + "...");
		}

		historic.setFont(new Font("Arial", 10));
		historic.setFill(Dominoes.COLOR_HISTORIC);
		historic.setX(2);
		historic.setY(3 * Dominoes.GRAPH_HEIGHT / 5);
		historic.setWrappingWidth(Dominoes.GRAPH_WIDTH - 2);
		historic.toFront();

		// Circle circle = new Circle(back.getX() + back.getWidth() / 2, back.getY() +
		// back.getHeight() / 2, 5, Dominoes.COLOR_TYPE);
		double radius = 5;
		double circlePadding = 2;
		double padding = 1;
		Circle circle = new Circle(0, 0, radius, Dominoes.COLOR_TYPE);

		Text type = new Text();
		type.setFill(Dominoes.COLOR_NORMAL_FONT);

		int z = 0;
		switch (this.getType()) {
		case Dominoes.TYPE_BASIC:
			type.setText(Dominoes.TYPE_BASIC_CODE);
			type.setFill(Dominoes.COLOR_INIVISIBLE);

			circle.setFill(Dominoes.COLOR_INIVISIBLE);

			historic.setFill(Dominoes.COLOR_INIVISIBLE);

			break;
		case Dominoes.TYPE_DERIVED:
			type.setText(Dominoes.TYPE_DERIVED_CODE);
			type.setFill(Dominoes.COLOR_INIVISIBLE);

			circle.setFill(Dominoes.COLOR_INIVISIBLE);

			border.setFill(Dominoes.COLOR_BORDER_DERIVED);
			back.setWidth(back.getWidth() - 2);
			back.setHeight(back.getHeight() - 2);
			back.setX(back.getX() + 1);
			back.setY(back.getY() + 1);

			line.setFill(Dominoes.COLOR_LINE);

			break;
		case Dominoes.TYPE_SUPPORT:
			type.setText(Dominoes.TYPE_SUPPORT_CODE);
			break;
		case Dominoes.TYPE_CONFIDENCE:
			type.setText(Dominoes.TYPE_CONFIDENCE_CODE);
			break;
		case Dominoes.TYPE_LIFT:
			type.setText(Dominoes.TYPE_LIFT_CODE);
			break;
		case Dominoes.TYPE_TRANSITIVE_CLOSURE:
			type.setText(Dominoes.TYPE_TRANSITIVE_CLOSURE_CODE);
			break;
		case Dominoes.TYPE_BINARIZED:
			type.setText(Dominoes.TYPE_BINARIZED_CODE);
			break;
		case Dominoes.TYPE_INVERTED:
			type.setText(Dominoes.TYPE_INVERTED_CODE);
			break;
		case Dominoes.TYPE_DIAGONAL: {
			type.setText(Dominoes.TYPE_DIAGONAL_CODE);
			z = 10;
			break;
		}
		case Dominoes.TYPE_UPPER_DIAGONAL: {
			type.setText(Dominoes.TYPE_UPPER_DIAGONAL_CODE);
			z = 17;
			break;
		}
		case Dominoes.TYPE_LOWER_DIAGONAL: {
			type.setText(Dominoes.TYPE_LOWER_DIAGONAL_CODE);
			z = 17;
			break;
		}
		}
		type.setX((circle.getCenterX() - circle.getRadius() / 2 - padding));
		type.setY(circle.getCenterY() + circle.getRadius() / 2 + padding);

		circle.toFront();
		type.toFront();

		Group groupType = new Group(circle, type);
		groupType.setTranslateX(border.getX() + border.getWidth() - (radius + circlePadding) - z);
		groupType.setTranslateY((radius + circlePadding));
		groupType.setAutoSizeChildren(true);

		Group domino = null;
		if (this.relation == null) {
			domino = new Group(border, back, line, historic, groupType, idRow, idCol);
			Tooltip.install(domino, new Tooltip(this.idRow + "x" + this.getIdCol()));
		} else {
			domino = new Group(border, back, line, historic, groupType, idRow, idCol, relationText, arrowText);
			Tooltip.install(domino,
					new Tooltip(this.idRow + " (" + this.relation.getDescription() + ") " + this.getIdCol()));
		}

		return domino;
	}

	/**
	 * User to obtain the complete historic this
	 *
	 * @return this Historic
	 */
	public Historic getHistoric() {
		return this.historic;
	}

	/**
	 * Used to obtain the Id Column this Domino
	 *
	 * @return Return the Id Column value
	 */
	public String getIdCol() {
		return this.idCol;
	}

	/**
	 * Used to obtain the Id Column this Domino
	 *
	 * @return Return the Id Row value
	 */
	public String getIdRow() {
		return this.idRow;
	}

	/**
	 * Used to obtain the Matrix this Domino
	 *
	 * @return Return the Matrix value
	 */
	public MatrixOperations getMat() {
		return this.mat;
	}

	/**
	 * Used to obtain the Type of Matrix
	 *
	 * @return Return the Type value
	 */
	public int getType() {
		return type;
	}

	public boolean isRowAggregatable() {
		return this.rowIsAggragatable;
	}

	public boolean isColAggregatable() {
		return this.colIsAggragatable;
	}

	/**
	 * Used to change the Historic of this Domino
	 *
	 * @param historic The Historic value
	 * @throws IllegalArgumentException
	 */
	private void setHistoric(Historic historic) {
		if (historic == null || historic.toString() == null || historic.toString().trim().equals("")
				|| (!historic.getFirstItem().equals(this.idRow) && !historic.getLastItem().equals(this.idCol))) {
			throw new IllegalArgumentException("Invalid argument.\nThe Historic attribute is null, void or invalid");
		}
		this.historic = historic;
	}

	/**
	 * Used to change the Id Column this Domino
	 *
	 * @param idCol The Id Column value
	 * @throws IllegalArgumentException
	 */
	private void setIdCol(String idCol) throws IllegalArgumentException {
		if (idCol == null || idCol.trim().equals("")) {
			throw new IllegalArgumentException("Invalid argument.\nThe IdCol attribute is null or void");
		}
		this.idCol = idCol;
	}

	/**
	 * Used to change the Id Row this Domino
	 *
	 * @param idRow The Id Row value
	 * @throws IllegalArgumentException
	 */
	private void setIdRow(String idRow) throws IllegalArgumentException {
		if (idRow == null || idRow.trim().equals("")) {
			throw new IllegalArgumentException("Invalid argument.\nThe IdRow attribute is null or void");
		}
		this.idRow = idRow;
	}

	/**
	 * Used to change the Matrix this Domino
	 *
	 * @param mat The Matrix value
	 * @throws IllegalArgumentException
	 */
	public void setMat(MatrixOperations mat) {
		if (mat == null) {
			throw new IllegalArgumentException("Invalid argument.\nThe Mat attribute is null");
		}

		this.mat = mat;
	}

	/**
	 * This function just invert the Historic
	 *
	 * @return the historic invert
	 */
	public void transpose() {

		if (!(this.type == Dominoes.TYPE_BASIC)) {
			this.type = Dominoes.TYPE_DERIVED;
			if (this.getIdRow().equals(this.getIdCol())) {
				this.type = Dominoes.TYPE_SUPPORT;
			}
		}
		this.getHistoric().reverse();
		this.setIdRow(this.getHistoric().getFirstItem());
		this.setIdCol(this.getHistoric().getLastItem());

		boolean swap = this.rowIsAggragatable;
		this.rowIsAggragatable = this.colIsAggragatable;
		this.colIsAggragatable = swap;

		MatrixOperations _newMat = mat.transpose();
		setMat(_newMat);
	}

	public void standardScore() {

		MatrixOperations _newMat = mat.standardScore(currentDevice.equalsIgnoreCase("GPU"));

		if (!(this.type == Dominoes.TYPE_BASIC)) {
			this.type = Dominoes.TYPE_DERIVED;
			if (this.getIdRow().equals(this.getIdCol())) {
				this.type = Dominoes.TYPE_SUPPORT;
			}
		}
		this.getHistoric().reverse();
		this.setIdRow(this.getHistoric().getFirstItem());
		this.setIdCol(this.getHistoric().getLastItem());

		boolean swap = this.rowIsAggragatable;
		this.rowIsAggragatable = this.colIsAggragatable;
		this.colIsAggragatable = swap;

		// IMatrix2D _newMat = mat.transpose();
		setMat(_newMat);
	}

	/**
	 * This function just invert the Historic
	 *
	 * @return the historic invert
	 */
	public void confidence() {
		MatrixOperations _newMat = mat.confidence(currentDevice.equalsIgnoreCase("GPU"));
		setMat(_newMat);
		this.type = Dominoes.TYPE_CONFIDENCE;
	}

	public void transitiveClosure() {
		MatrixOperations _newMat = mat.transitiveClosure(currentDevice.equalsIgnoreCase("GPU"));
		setMat(_newMat);
		this.type = Dominoes.TYPE_TRANSITIVE_CLOSURE;
	}

	public void binarize() {
		MatrixOperations _newMat = mat.binarize(currentDevice.equalsIgnoreCase("GPU"));
		setMat(_newMat);
		this.type = Dominoes.TYPE_BINARIZED;
	}

	public void invert() {
		MatrixOperations _newMat = mat.invert(currentDevice.equalsIgnoreCase("GPU"));
		setMat(_newMat);
		this.type = Dominoes.TYPE_INVERTED;
	}

	public void diagonalize() {
		MatrixOperations _newMat = mat.diagonalize(currentDevice.equalsIgnoreCase("GPU"));
		setMat(_newMat);
		this.type = Dominoes.TYPE_DIAGONAL;
	}

	public void upperDiagonal() {
		MatrixOperations _newMat = mat.upperDiagonal(currentDevice.equalsIgnoreCase("GPU"));
		setMat(_newMat);
		this.type = Dominoes.TYPE_UPPER_DIAGONAL;
	}

	public void lowerDiagonal() {
		MatrixOperations _newMat = mat.lowerDiagonal(currentDevice.equalsIgnoreCase("GPU"));
		setMat(_newMat);
		this.type = Dominoes.TYPE_LOWER_DIAGONAL;
	}

	/**
	 * This function reduce the lines of a matrix
	 *
	 * @return the historic invert
	 */
	public boolean reduceRows() {

		if (rowIsAggragatable) {
			return false;
		}

		rowIsAggragatable = true;

		if (!(this.type == Dominoes.TYPE_BASIC)) {
			this.type = Dominoes.TYPE_DERIVED;
			if (this.getIdRow().equals(this.getIdCol())) {
				this.type = Dominoes.TYPE_SUPPORT;
			}
		}
		this.setIdRow(Dominoes.AGGREG_TEXT + idRow);
		this.historic.reduceRow();

		MatrixOperations _newMat = mat.reduceRows(currentDevice.equalsIgnoreCase("GPU"));
		setMat(_newMat);

		_newMat.Debug();
		return true;
	}

	public Dominoes multiply(Dominoes dom) {

		Dominoes domResult = new Dominoes(dom.getDevice());

		domResult.type = Dominoes.TYPE_DERIVED;

		if (idRow.equals(dom.getIdCol())) {
			domResult.type = Dominoes.TYPE_SUPPORT;
		}

		try {
			domResult.setMat(getMat().multiply(dom.getMat(), currentDevice.equalsIgnoreCase("GPU")));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		domResult.historic = new Historic(this.getHistoric(), dom.getHistoric());

		domResult.setIdRow(getIdRow());
		domResult.setIdCol(dom.getIdCol());

		return domResult;
	}

	public boolean isSquare() {
		return getMat().getMatrixDescriptor().getNumRows() == getMat().getMatrixDescriptor().getNumCols();
	}

	public Dominoes cloneNoMatrix() {
		Dominoes cloned = new Dominoes(getIdRow(), getIdCol(), getRelation(), getMat(), getDevice());
		cloned.setType(this.type);
		cloned.setCurrentDevice(getCurrentDevice());
		cloned.setColIsAggragatable(colIsAggragatable);
		cloned.setRowIsAggragatable(rowIsAggragatable);
		if (getHistoric() != null) {
			cloned.setHistoric(getHistoric().clone());
		}
		cloned.setId(this.id);
		return cloned;
	}

	public String getDevice() {
		return currentDevice;
	}

	public Relation getRelation() {
		return relation;
	}

	public void setRelation(Relation relation) {
		this.relation = relation;
	}

	public int getSourceIndex() {
		return sourceIndex;
	}

	public void setSourceIndex(int sourceIndex) {
		this.sourceIndex = sourceIndex;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((idCol == null) ? 0 : idCol.hashCode());
		result = prime * result + ((idRow == null) ? 0 : idRow.hashCode());
		result = prime * result + ((relation == null) ? 0 : relation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Dominoes other = (Dominoes) obj;
		if (idCol == null) {
			if (other.idCol != null)
				return false;
		} else if (!idCol.equals(other.idCol))
			return false;
		if (idRow == null) {
			if (other.idRow != null)
				return false;
		} else if (!idRow.equals(other.idRow))
			return false;
		if (relation != other.relation)
			return false;
		return true;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isRowIsAggragatable() {
		return rowIsAggragatable;
	}

	public void setRowIsAggragatable(boolean rowIsAggragatable) {
		this.rowIsAggragatable = rowIsAggragatable;
	}

	public boolean isColIsAggragatable() {
		return colIsAggragatable;
	}

	public void setColIsAggragatable(boolean colIsAggragatable) {
		this.colIsAggragatable = colIsAggragatable;
	}

	public String getCurrentDevice() {
		return currentDevice;
	}

	public void setCurrentDevice(String currentDevice) {
		this.currentDevice = currentDevice;
	}

	public String getId() {
		if (this.id == null) {
			this.id = getRelation().getAbbreviate().replace(" ", "");
			if (getRelation() == Relation.RELATION_INFLUENCE) {
				this.id += "[" + this.getIdRow() + "," + this.getIdCol() + "]";
			}
		}
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

}
