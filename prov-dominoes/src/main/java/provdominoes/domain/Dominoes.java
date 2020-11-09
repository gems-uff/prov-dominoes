package provdominoes.domain;

import java.util.HashMap;

import org.la4j.matrix.sparse.CRSMatrix;

import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.ProvMatrix;
import model.ProvRelation.Relation;
import provdominoes.arch.MatrixDescriptor;
import provdominoes.arch.MatrixOperations;
import provdominoes.arch.MatrixOperationsCPU;
import provdominoes.command.TextFilterData;
import provdominoes.util.Prov2DominoesUtil;

public class Dominoes {
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
	public static final int TYPE_BASIC = 0;
	public static final int TYPE_DERIVED = 1;
	public static final int TYPE_SUPPORT = 2;
	public static final int TYPE_CONFIDENCE = 3;
	public static final int TYPE_LIFT = 4;
	public static final int TYPE_TRANSITIVE_CLOSURE = 5;
	public static final int TYPE_BINARIZED = 6;
	public static final int TYPE_INVERTED = 7;
	public static final int TYPE_DIAGONAL = 8;
	public static final int TYPE_UPPER_DIAGONAL = 9;
	public static final int TYPE_LOWER_DIAGONAL = 10;
	public static final int TYPE_TRIMMED = 11;
	public static final int TYPE_LPF = 12;
	public static final int TYPE_HPF = 13;
	public static final int TYPE_ZSCORE = 14;
	public static final int TYPE_TEXT = 15;
	public static final int TYPE_SORT_ROW_ASC = 16;
	public static final int TYPE_SORT_COL_ASC = 17;
	public static final int TYPE_SORT_COLUMN_FIRST = 18;
	public static final int TYPE_SORT_ROW_FIRST = 19;
	public static final int TYPE_SORT_ROW_VALUES = 20;
	public static final int TYPE_SORT_COLUMN_VALUES = 21;
	public static final int TYPE_SORT_ROW_GROUP = 22;
	public static final int TYPE_SORT_COLUMN_GROUP = 23;
	public static final String TYPE_BASIC_CODE = "B";
	public static final String TYPE_DERIVED_CODE = "D";
	public static final String TYPE_SUPPORT_CODE = "S";
	public static final String TYPE_CONFIDENCE_CODE = "C";
	public static final String TYPE_ZSCORE_CODE = "Z";
	public static final String TYPE_LIFT_CODE = "L";
	public static final String TYPE_BINARIZED_CODE = "01";
	public static final String TYPE_INVERTED_CODE = "I";
	public static final String TYPE_TRANSITIVE_CLOSURE_CODE = "T";
	public static final String TYPE_DIAGONAL_CODE = "DG";
	public static final String TYPE_UPPER_DIAGONAL_CODE = "UDG";
	public static final String TYPE_LOWER_DIAGONAL_CODE = "LDG";
	public static final String TYPE_TRIMMED_CODE = "TRIM";
	public static final String TYPE_HPF_CODE = "HI";
	public static final String TYPE_LPF_CODE = "LO";
	public static final String TYPE_TEXT_CODE = "TXT";
	public static final String TYPE_ROW_ASC_CODE = "RASC";
	public static final String TYPE_COL_ASC_CODE = "CASC";
	public static final String TYPE_ROW_COUNT_CODE = "#R";
	public static final String TYPE_COL_COUNT_CODE = "#C";
	public static final String TYPE_SORT_COLUMN_FIRST_CODE = "C1ST";
	public static final String TYPE_SORT_ROW_FIRST_CODE = "R1ST";
	public static final String TYPE_SORT_ROW_VALUES_CODE = "RVAL";
	public static final String TYPE_SORT_COLUMN_VALUES_CODE = "CVAL";

	public static final String AGGREG_TEXT = "/SUM ";

	private Relation relation;
	private String id;
	private String idRow;
	private String idCol;
	private Historic historic;
	private Text textType;
	private int type;
	private MatrixOperations mat = null;
	private int sourceIndex;
	private boolean hasTransitiveValues;
	private String currentProcessingMode = Configuration.CPU_PROCESSING;
	private CRSMatrix crsMatrix;
	private MatrixDescriptor descriptor;
	private HashMap<String, String> cellParams;
	private String[][] underlyingElements;

	public Dominoes(String processingMode) {
		currentProcessingMode = processingMode;
	}

	public Dominoes(String idRow, String idCol, String _device) throws IllegalArgumentException {
		this.setIdRow(idRow);
		this.setIdCol(idCol);

		this.setHistoric(new Historic(idRow, idCol));

		this.type = Dominoes.TYPE_BASIC;
		this.currentProcessingMode = _device;
	}

	public Dominoes(ProvMatrix provMatrix, MatrixDescriptor descriptor, String processingUnit) {
		this(provMatrix.getRowDimentionAbbreviate(), provMatrix.getColumnDimentionAbbreviate(), processingUnit);
		this.relation = provMatrix.getRelation();
		this.descriptor = descriptor;
		this.crsMatrix = provMatrix.getMatrix();
	}

	public Dominoes(String idRow, String idCol, Relation relation, MatrixDescriptor descriptor, MatrixOperations mat,
			String _device) throws IllegalArgumentException {
		this.relation = relation;
		this.descriptor = descriptor;
		this.setIdRow(idRow);
		this.setIdCol(idCol);
		this.setMat(mat);
		this.setHistoric(new Historic(idRow, idCol));
		this.type = Dominoes.TYPE_BASIC;
		this.currentProcessingMode = _device;
	}

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
		arrowText = new Text("\u21D2");
		arrowText.setFill(Dominoes.COLOR_NORMAL_FONT);
		arrowText.setFont(new Font("Times", 10));
		arrowText.setX(GRAPH_WIDTH / 2 - 12);
		arrowText.toFront();
		arrowText.setY(border.getHeight() - back.getHeight() + 10);

		if (this.relation != null) {
			relationText = new Text(this.relation.getAbbreviate());
			relationText.setFont(new Font("Times", 10));
			relationText.setY(border.getHeight() - back.getHeight() + 33);
			relationText.setX(GRAPH_WIDTH / 2 - 20);
		} else {
			relationText = new Text(this.id);
			if (relationText.getText().length() >= 4) {
				relationText.setText(relationText.getText().substring(0, 4));
			}
			relationText.setFont(new Font("Courier New", 12));
			relationText.setY(border.getHeight() - back.getHeight() + 35);
			relationText.setX(GRAPH_WIDTH / 2 - 22);
		}

		relationText.setFill(Dominoes.COLOR_NORMAL_FONT);
		relationText.toFront();

		relationText.setRotate(90);

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

		textType = new Text();
		int fontSize = 12;
		textType.setFill(Dominoes.COLOR_NORMAL_FONT);

		int z = 0;
		switch (this.getType()) {
		case Dominoes.TYPE_BASIC:
			textType.setText(Dominoes.TYPE_BASIC_CODE);
			textType.setFill(Dominoes.COLOR_INIVISIBLE);

			circle.setFill(Dominoes.COLOR_INIVISIBLE);

			historic.setFill(Dominoes.COLOR_INIVISIBLE);

			break;
		case Dominoes.TYPE_DERIVED:
			textType.setText(Dominoes.TYPE_DERIVED_CODE);
			textType.setFill(Dominoes.COLOR_INIVISIBLE);

			circle.setFill(Dominoes.COLOR_INIVISIBLE);

			border.setFill(Dominoes.COLOR_BORDER_DERIVED);
			back.setWidth(back.getWidth() - 2);
			back.setHeight(back.getHeight() - 2);
			back.setX(back.getX() + 1);
			back.setY(back.getY() + 1);

			line.setFill(Dominoes.COLOR_LINE);

			break;
		case Dominoes.TYPE_SUPPORT:
			textType.setText(Dominoes.TYPE_SUPPORT_CODE);
			break;
		case Dominoes.TYPE_CONFIDENCE:
			textType.setText(Dominoes.TYPE_CONFIDENCE_CODE);
			break;
		case Dominoes.TYPE_ZSCORE:
			textType.setText(Dominoes.TYPE_ZSCORE_CODE);
			break;
		case Dominoes.TYPE_LIFT:
			textType.setText(Dominoes.TYPE_LIFT_CODE);
			break;
		case Dominoes.TYPE_TRANSITIVE_CLOSURE:
			textType.setText(Dominoes.TYPE_TRANSITIVE_CLOSURE_CODE);
			break;
		case Dominoes.TYPE_BINARIZED:
			z = 5;
			textType.setText(Dominoes.TYPE_BINARIZED_CODE);
			break;
		case Dominoes.TYPE_INVERTED:
			textType.setText(Dominoes.TYPE_INVERTED_CODE);
			break;
		case Dominoes.TYPE_DIAGONAL: {
			textType.setText(Dominoes.TYPE_DIAGONAL_CODE);
			z = 10;
			break;
		}
		case Dominoes.TYPE_HPF: {
			textType.setText(Dominoes.TYPE_HPF_CODE);
			z = 20;
			break;
		}
		case Dominoes.TYPE_LPF: {
			textType.setText(Dominoes.TYPE_LPF_CODE);
			z = 20;
			break;
		}
		case Dominoes.TYPE_TEXT: {
			textType.setText(Dominoes.TYPE_TEXT_CODE);
			z = 17;
			break;
		}
		case Dominoes.TYPE_SORT_ROW_ASC: {
			textType.setText(Dominoes.TYPE_ROW_ASC_CODE);
			z = 23;
			break;
		}
		case Dominoes.TYPE_SORT_COL_ASC: {
			textType.setText(Dominoes.TYPE_COL_ASC_CODE);
			z = 23;
			break;
		}
		case Dominoes.TYPE_SORT_ROW_GROUP: {
			textType.setText(Dominoes.TYPE_ROW_COUNT_CODE);
			z = 18;
			break;
		}
		case Dominoes.TYPE_SORT_COLUMN_GROUP: {
			textType.setText(Dominoes.TYPE_COL_COUNT_CODE);
			z = 18;
			break;
		}
		case Dominoes.TYPE_SORT_ROW_VALUES: {
			textType.setText(Dominoes.TYPE_SORT_ROW_VALUES_CODE);
			z = 23;
			break;
		}
		case Dominoes.TYPE_SORT_COLUMN_VALUES: {
			textType.setText(Dominoes.TYPE_SORT_COLUMN_VALUES_CODE);
			z = 23;
			break;
		}
		case Dominoes.TYPE_SORT_COLUMN_FIRST: {
			textType.setText(Dominoes.TYPE_SORT_COLUMN_FIRST_CODE);
			z = 21;
			break;
		}
		case Dominoes.TYPE_SORT_ROW_FIRST: {
			textType.setText(Dominoes.TYPE_SORT_ROW_FIRST_CODE);
			z = 21;
			break;
		}
		case Dominoes.TYPE_UPPER_DIAGONAL: {
			textType.setText(Dominoes.TYPE_UPPER_DIAGONAL_CODE);
			z = 17;
			break;
		}
		case Dominoes.TYPE_LOWER_DIAGONAL: {
			textType.setText(Dominoes.TYPE_LOWER_DIAGONAL_CODE);
			z = 17;
			break;
		}
		case Dominoes.TYPE_TRIMMED: {
			textType.setText(Dominoes.TYPE_TRIMMED_CODE);
			z = 23;
			break;
		}
		}
		textType.setX((circle.getCenterX() - circle.getRadius() / 2 - padding));
		textType.setY(circle.getCenterY() + circle.getRadius() / 2 + padding);
		Font f = new Font("Times", fontSize);
		textType.setFont(f);

		circle.toFront();
		textType.toFront();

		Group groupType = new Group(circle, textType);
		groupType.setTranslateX(border.getX() + border.getWidth() - (radius + circlePadding) - z);
		groupType.setTranslateY((radius + circlePadding));
		groupType.setAutoSizeChildren(true);

		Group domino = null;
		if (this.relation == null) {
			domino = new Group(border, back, line, historic, groupType, idRow, idCol, relationText, arrowText);
			Tooltip.install(domino, new Tooltip(this.idRow + "x" + this.getIdCol()));
		} else {
			domino = new Group(border, back, line, historic, groupType, idRow, idCol, relationText, arrowText);
			String tooltipDesc = "";
			if (relation != null) {
				tooltipDesc = this.relation.getDescription();
			} else {
				tooltipDesc = this.id;
			}
			Tooltip.install(domino, new Tooltip(this.idRow + " (" + tooltipDesc + ") " + this.getIdCol()));
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
		this.mat = mat;
		if (mat != null) {
			this.descriptor = mat.getMatrixDescriptor();
			if (mat instanceof MatrixOperationsCPU) {
				this.crsMatrix = Prov2DominoesUtil.cells2Matrix(mat.getData(), mat.getMatrixDescriptor().getNumRows(),
						mat.getMatrixDescriptor().getNumCols());

				this.mat = (MatrixOperations) mat;
				String[][] ue = ((MatrixOperationsCPU) mat).getUnderlyingElements();
				if (ue != null) {
					this.setUnderlyingElements(Prov2DominoesUtil.cloneStringMatrix(ue));
				}
			}
		}
	}

	public void standardScore() throws Exception {
		MatrixOperations _newMat = mat.standardScoreSparse();
		this.mat = _newMat;
		this.descriptor = _newMat.getMatrixDescriptor();
		if (!Configuration.isGPUProcessing()) {
			this.crsMatrix = _newMat.getMatrix();
		}
		this.type = Dominoes.TYPE_ZSCORE;
		this.cellParams = null;
	}

	public void confidence() throws Exception {
		MatrixOperations _newMat = mat.confidence();
		this.mat = _newMat;
		this.descriptor = _newMat.getMatrixDescriptor();
		if (!Configuration.isGPUProcessing()) {
			this.crsMatrix = _newMat.getMatrix();
		}
		this.type = Dominoes.TYPE_CONFIDENCE;
		this.cellParams = null;
	}

	public void transitiveClosure() throws Exception {
		MatrixOperations _newMat = mat.transitiveClosure();
		this.mat = _newMat;
		this.descriptor = _newMat.getMatrixDescriptor();
		if (!Configuration.isGPUProcessing()) {
			this.crsMatrix = _newMat.getMatrix();
		}
		this.cellParams = null;
		this.type = Dominoes.TYPE_TRANSITIVE_CLOSURE;
	}

	public void binarize() throws Exception {
		MatrixOperations _newMat = mat.binarize();
		this.mat = _newMat;
		this.descriptor = _newMat.getMatrixDescriptor();
		if (!Configuration.isGPUProcessing()) {
			this.crsMatrix = _newMat.getMatrix();
		}
		this.type = Dominoes.TYPE_BINARIZED;
	}

	public void invert() throws Exception {
		MatrixOperations _newMat = mat.invert();
		this.mat = _newMat;
		this.descriptor = _newMat.getMatrixDescriptor();
		if (!Configuration.isGPUProcessing()) {
			this.crsMatrix = _newMat.getMatrix();
		}
		this.type = Dominoes.TYPE_INVERTED;
		this.cellParams = null;
	}

	public void sortDefaultDimensionValues() throws Exception {
		MatrixOperations _newMat = mat.sortDefaultDimensionValues();
		this.mat = _newMat;
		this.descriptor = _newMat.getMatrixDescriptor();
		if (!Configuration.isGPUProcessing()) {
			this.crsMatrix = _newMat.getMatrix();
		}
		this.type = Dominoes.TYPE_SORT_ROW_VALUES;
		this.cellParams = null;
	}

	public void sortRows() throws Exception {
		MatrixOperations _newMat = mat.sortRows();
		this.mat = _newMat;
		this.descriptor = _newMat.getMatrixDescriptor();
		if (!Configuration.isGPUProcessing()) {
			this.crsMatrix = _newMat.getMatrix();
		}
		this.type = Dominoes.TYPE_SORT_ROW_ASC;
		this.cellParams = null;
	}

	public void sortCols() throws Exception {
		MatrixOperations _newMat = mat.sortColumns();
		this.mat = _newMat;
		this.descriptor = _newMat.getMatrixDescriptor();
		if (!Configuration.isGPUProcessing()) {
			this.crsMatrix = _newMat.getMatrix();
		}
		this.type = Dominoes.TYPE_SORT_COL_ASC;
		this.cellParams = null;
	}

	public void sortByRowGroup() throws Exception {
		MatrixOperations _newMat = mat.sortByRowGroup();
		this.mat = _newMat;
		this.descriptor = _newMat.getMatrixDescriptor();
		if (!Configuration.isGPUProcessing()) {
			this.crsMatrix = _newMat.getMatrix();
		}
		this.type = Dominoes.TYPE_SORT_ROW_GROUP;
		this.cellParams = null;
	}

	public void sortByColumnGroup() throws Exception {
		MatrixOperations _newMat = mat.sortByColumnGroup();
		this.mat = _newMat;
		this.descriptor = _newMat.getMatrixDescriptor();
		if (!Configuration.isGPUProcessing()) {
			this.crsMatrix = _newMat.getMatrix();
		}
		this.type = Dominoes.TYPE_SORT_COLUMN_GROUP;
		this.cellParams = null;
	}

	public void sortColumnFirst() throws Exception {
		MatrixOperations _newMat = mat.sortColumnFirst();
		this.mat = _newMat;
		this.descriptor = _newMat.getMatrixDescriptor();
		if (!Configuration.isGPUProcessing()) {
			this.crsMatrix = _newMat.getMatrix();
		}
		this.type = Dominoes.TYPE_SORT_COLUMN_FIRST;
		this.cellParams = null;
	}

	public void sortRowFirst() throws Exception {
		MatrixOperations _newMat = mat.sortRowFirst();
		this.mat = _newMat;
		this.descriptor = _newMat.getMatrixDescriptor();
		if (!Configuration.isGPUProcessing()) {
			this.crsMatrix = _newMat.getMatrix();
		}
		this.type = Dominoes.TYPE_SORT_ROW_FIRST;
		this.cellParams = null;
	}

	public void highPassFilter(double d) throws Exception {
		MatrixOperations _newMat = mat.highPassFilter(d);
		this.mat = _newMat;
		this.descriptor = _newMat.getMatrixDescriptor();
		if (!Configuration.isGPUProcessing()) {
			this.crsMatrix = _newMat.getMatrix();
		}
		this.type = Dominoes.TYPE_HPF;
	}

	public void lowPassFilter(double d) throws Exception {
		MatrixOperations _newMat = mat.lowPassFilter(d);
		this.mat = _newMat;
		this.descriptor = _newMat.getMatrixDescriptor();
		if (!Configuration.isGPUProcessing()) {
			this.crsMatrix = _newMat.getMatrix();
		}
		this.type = Dominoes.TYPE_LPF;
	}

	public void filterColumnText(TextFilterData t) throws Exception {
		MatrixOperations _newMat = mat.filterColumnText(t);
		this.mat = _newMat;
		this.descriptor = _newMat.getMatrixDescriptor();
		if (!Configuration.isGPUProcessing()) {
			this.crsMatrix = _newMat.getMatrix();
		}
		this.type = Dominoes.TYPE_TEXT;
	}

	public void filterRowText(TextFilterData t) throws Exception {
		MatrixOperations _newMat = mat.filterRowText(t);
		this.mat = _newMat;
		this.descriptor = _newMat.getMatrixDescriptor();
		if (!Configuration.isGPUProcessing()) {
			this.crsMatrix = _newMat.getMatrix();
		}
		this.type = Dominoes.TYPE_TEXT;
	}

	public void diagonalize() throws Exception {
		MatrixOperations _newMat = mat.diagonalize();
		this.mat = _newMat;
		this.descriptor = _newMat.getMatrixDescriptor();
		if (!Configuration.isGPUProcessing()) {
			this.crsMatrix = _newMat.getMatrix();
		}
		this.type = Dominoes.TYPE_DIAGONAL;
	}

	public void upperDiagonal() throws Exception {
		MatrixOperations _newMat = mat.upperDiagonal();
		this.mat = _newMat;
		this.descriptor = _newMat.getMatrixDescriptor();
		if (!Configuration.isGPUProcessing()) {
			this.crsMatrix = _newMat.getMatrix();
		}
		this.type = Dominoes.TYPE_UPPER_DIAGONAL;
	}

	public void lowerDiagonal() throws Exception {
		MatrixOperations _newMat = mat.lowerDiagonal();
		this.mat = _newMat;
		this.descriptor = _newMat.getMatrixDescriptor();
		if (!Configuration.isGPUProcessing()) {
			this.crsMatrix = _newMat.getMatrix();
		}
		this.type = Dominoes.TYPE_LOWER_DIAGONAL;
	}

	public void trim() throws Exception {
		MatrixOperations _newMat = mat.trim();
		this.mat = _newMat;
		this.descriptor = _newMat.getMatrixDescriptor();
		if (!Configuration.isGPUProcessing()) {
			this.crsMatrix = _newMat.getMatrix();
		}
		this.type = Dominoes.TYPE_TRIMMED;
		this.cellParams = null;
	}

	/**
	 * This function reduce the lines of a matrix
	 *
	 * @return the historic invert
	 * @throws Exception
	 */
	public boolean aggregateDimension() throws Exception {
		if (!(this.type == Dominoes.TYPE_BASIC)) {
			this.type = Dominoes.TYPE_DERIVED;
			if (this.getIdRow().equals(this.getIdCol())) {
				this.type = Dominoes.TYPE_SUPPORT;
			}
		}
		this.setIdRow(Dominoes.AGGREG_TEXT + idRow);
		this.historic.reduceRow();

		MatrixOperations _newMat = mat.aggregateDimension();
		this.mat = _newMat;
		this.descriptor = this.mat.getMatrixDescriptor();
		if (!Configuration.isGPUProcessing()) {
			this.crsMatrix = _newMat.getMatrix();
		}
		this.cellParams = null;
		return true;
	}

	public void transpose() throws Exception {
		this.getHistoric().reverse();
		this.setIdRow(this.getHistoric().getFirstItem());
		this.setIdCol(this.getHistoric().getLastItem());

		MatrixOperations _newMat = mat.transpose();
		this.cellParams = null;
		this.mat = _newMat;
		this.descriptor = _newMat.getMatrixDescriptor();
		if (!Configuration.isGPUProcessing()) {
			this.crsMatrix = _newMat.getMatrix();
		}
	}

	public Dominoes multiply(Dominoes dom) throws Exception {
		Dominoes domResult = new Dominoes(dom.getDevice());
		domResult.type = Dominoes.TYPE_DERIVED;

		if (idRow.equals(dom.getIdCol())) {
			domResult.type = Dominoes.TYPE_SUPPORT;
		}
		MatrixOperations resultMat = getMat().multiply(dom.getMat());
		String[][] resultUnderlying = null;
		if (!Configuration.isGPUProcessing()) {
			resultUnderlying = Prov2DominoesUtil
					.cloneStringMatrix(((MatrixOperationsCPU) resultMat).getUnderlyingElements());
		}
		domResult.setMat(resultMat);
		domResult.setUnderlyingElements(resultUnderlying);
		domResult.historic = new Historic(this.getHistoric(), dom.getHistoric());

		domResult.setIdRow(getIdRow());
		domResult.setIdCol(dom.getIdCol());

		if (!Configuration.isGPUProcessing()) {
			domResult.setCrsMatrix(resultMat.getMatrix());
		}
		return domResult;
	}

	public Dominoes sum(Dominoes dom) throws Exception {
		Dominoes domResult = new Dominoes(dom.getDevice());

		domResult.type = Dominoes.TYPE_DERIVED;

		MatrixOperations resultMat = getMat().sum(dom.getMat());
		String[][] resultUnderlying = null;
		if (currentProcessingMode.equalsIgnoreCase(Configuration.CPU_PROCESSING)) {
			resultUnderlying = Prov2DominoesUtil
					.cloneStringMatrix(((MatrixOperationsCPU) resultMat).getUnderlyingElements());
		}
		domResult.setMat(resultMat);
		domResult.setUnderlyingElements(resultUnderlying);
		domResult.historic = new Historic(this.getHistoric(), dom.getHistoric());

		domResult.setIdRow(getIdRow());
		domResult.setIdCol(getIdCol());
		if (!Configuration.isGPUProcessing()) {
			domResult.setCrsMatrix(resultMat.getMatrix());
		}
		return domResult;
	}

	public Dominoes subtract(Dominoes dom) throws Exception {
		Dominoes domResult = new Dominoes(dom.getDevice());

		domResult.type = Dominoes.TYPE_DERIVED;

		MatrixOperations resultMat = getMat().subtract(dom.getMat());
		String[][] resultUnderlying = null;
		if (currentProcessingMode.equalsIgnoreCase(Configuration.CPU_PROCESSING)) {
			resultUnderlying = Prov2DominoesUtil
					.cloneStringMatrix(((MatrixOperationsCPU) resultMat).getUnderlyingElements());
		}
		domResult.setMat(resultMat);
		domResult.setUnderlyingElements(resultUnderlying);
		domResult.historic = new Historic(this.getHistoric(), dom.getHistoric());

		domResult.setIdRow(getIdRow());
		domResult.setIdCol(getIdCol());
		if (!Configuration.isGPUProcessing()) {
			domResult.setCrsMatrix(resultMat.getMatrix());
		}
		return domResult;
	}

	public void setUnderlyingElements(String[][] underlyingElements) {
		this.underlyingElements = underlyingElements;
	}

	public String[][] getUnderlyingElements() {
		return this.underlyingElements;
	}

	public boolean isSquare() {
		return getDescriptor().getNumRows() == getDescriptor().getNumCols();
	}

	@SuppressWarnings("unchecked")
	public Dominoes cloneNoMatrix() {
		Dominoes cloned = new Dominoes(getIdRow(), getIdCol(), getRelation(), getDescriptor(), getMat(), getDevice());
		cloned.setType(this.type);
		cloned.setCurrentProcessingMode(getCurrentProcessingMode());
		if (getHistoric() != null) {
			cloned.setHistoric(getHistoric().clone());
		}
		cloned.setId(this.id);
		if (this.crsMatrix != null) {
			cloned.setCrsMatrix(new CRSMatrix(this.crsMatrix));
		}
		cloned.setUnderlyingElements(Prov2DominoesUtil.cloneStringMatrix(this.underlyingElements));
		if (cellParams != null) {
			cloned.setCellParams((HashMap<String, String>) cellParams.clone());
		}
		return cloned;
	}

	public String getDevice() {
		return currentProcessingMode;
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

	public void setType(int type) {
		this.type = type;
	}

	public String getCurrentProcessingMode() {
		return currentProcessingMode;
	}

	public void setCurrentProcessingMode(String currentDevice) {
		this.currentProcessingMode = currentDevice;
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

	public CRSMatrix getCrsMatrix() {
		return crsMatrix;
	}

	public void setCrsMatrix(CRSMatrix crsMatrix) {
		this.crsMatrix = crsMatrix;
	}

	public MatrixDescriptor getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(MatrixDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public Text getTextType() {
		return textType;
	}

	public void setTextType(Text textType) {
		this.textType = textType;
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((idCol == null) ? 0 : idCol.hashCode());
		result = prime * result + ((idRow == null) ? 0 : idRow.hashCode());
		result = prime * result + ((relation == null) ? 0 : relation.hashCode());
		return result;
	}

	public HashMap<String, String> getCellParams() {
		return cellParams;
	}

	public void setCellParams(HashMap<String, String> cellParams) {
		this.cellParams = cellParams;
	}

	public boolean hasTransitiveValues() {
		return hasTransitiveValues;
	}

	public void setHasTransitiveValues(boolean hasTransitiveValues) {
		this.hasTransitiveValues = hasTransitiveValues;
	}

}
