package provdominoes.domain;

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
	public static final int TYPE_SORT_ROW_COUNT = 22;
	public static final int TYPE_SORT_COLUMN_COUNT = 23;
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
	private String currentProcessingMode = Configuration.CPU_PROCESSING;
	private CRSMatrix crsMatrix;
	private MatrixDescriptor descriptor;
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
		case Dominoes.TYPE_SORT_ROW_COUNT: {
			textType.setText(Dominoes.TYPE_ROW_COUNT_CODE);
			z = 18;
			break;
		}
		case Dominoes.TYPE_SORT_COLUMN_COUNT: {
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
			this.crsMatrix = Prov2DominoesUtil.cells2Matrix(mat.getData(), mat.getMatrixDescriptor().getNumRows(),
					mat.getMatrixDescriptor().getNumCols());
			this.descriptor = mat.getMatrixDescriptor();
			if (mat instanceof MatrixOperationsCPU) {
				this.mat = (MatrixOperations) mat;
				String[][] ue = ((MatrixOperationsCPU) mat).getUnderlyingElements();
				if (ue != null) {
					this.setUnderlyingElements(Prov2DominoesUtil.cloneStringMatrix(ue));
				}
			}
		}
	}

	public void setupOperation(boolean benefitFromSparseOperation) throws Exception {
		this.mat = MatrixOperations.configureOperation(this.crsMatrix, descriptor, benefitFromSparseOperation);
		if (this.underlyingElements != null && mat instanceof MatrixOperationsCPU) {
			MatrixOperationsCPU _newMatCPU = (MatrixOperationsCPU) mat;
			_newMatCPU.setUnderlyingElements(this.underlyingElements);
			setMat(_newMatCPU);
		}
	}

	public void transpose() throws Exception {
		this.setupOperation(true);
		this.getHistoric().reverse();
		this.setIdRow(this.getHistoric().getFirstItem());
		this.setIdCol(this.getHistoric().getLastItem());

		MatrixOperations _newMat = mat.transpose();
		setMat(_newMat);
	}

	public void standardScore() throws Exception {
		this.setupOperation(false);
		MatrixOperations _newMat = mat.standardScoreSparse();
		setMat(_newMat);
		this.type = Dominoes.TYPE_ZSCORE;
	}

	public void confidence() throws Exception {
		this.setupOperation(true);
		MatrixOperations _newMat = mat.confidence(currentProcessingMode.equalsIgnoreCase("GPU"));
		setMat(_newMat);
		this.type = Dominoes.TYPE_CONFIDENCE;
	}

	public void transitiveClosure() throws Exception {
		this.setupOperation(false);
		MatrixOperations _newMat = mat.transitiveClosure();
		setMat(_newMat);
		this.type = Dominoes.TYPE_TRANSITIVE_CLOSURE;
	}

	public void binarize() throws Exception {
		this.setupOperation(true);
		MatrixOperations _newMat = mat.binarize();
		setMat(_newMat);
		this.type = Dominoes.TYPE_BINARIZED;
	}

	public void invert() throws Exception {
		this.setupOperation(false);
		MatrixOperations _newMat = mat.invert();
		setMat(_newMat);
		this.type = Dominoes.TYPE_INVERTED;
	}
	
	public void sortDefaultDimensionValues() throws Exception {
		this.setupOperation(false);
		MatrixOperations _newMat = mat.sortDefaultDimensionValues();
		setMat(_newMat);
		this.type = Dominoes.TYPE_SORT_ROW_VALUES;
	}

	public void sortRows() throws Exception {
		this.setupOperation(false);
		MatrixOperations _newMat = mat.sortRows();
		setMat(_newMat);
		this.type = Dominoes.TYPE_SORT_ROW_ASC;
	}

	public void sortCols() throws Exception {
		this.setupOperation(false);
		MatrixOperations _newMat = mat.sortColumns();
		setMat(_newMat);
		this.type = Dominoes.TYPE_SORT_COL_ASC;
	}
	
	public void sortRowCount() throws Exception {
		this.setupOperation(false);
		MatrixOperations _newMat = mat.sortByRowGroup();
		setMat(_newMat);
		this.type = Dominoes.TYPE_SORT_ROW_COUNT;
	}
	
	public void sortColumnCount() throws Exception {
		this.setupOperation(false);
		MatrixOperations _newMat = mat.sortByColumnGroup();
		setMat(_newMat);
		this.type = Dominoes.TYPE_SORT_COLUMN_COUNT;
	}

	public void sortColumnFirst() throws Exception {
		this.setupOperation(false);
		MatrixOperations _newMat = mat.sortColumnFirst();
		setMat(_newMat);
		this.type = Dominoes.TYPE_SORT_COLUMN_FIRST;
	}

	public void sortRowFirst() throws Exception {
		this.setupOperation(false);
		MatrixOperations _newMat = null;
		_newMat = mat.sortRowFirst();
		setMat(_newMat);
		this.type = Dominoes.TYPE_SORT_ROW_FIRST;
	}

	public void highPassFilter(double d) throws Exception {
		this.setupOperation(false);
		MatrixOperations _newMat = mat.highPassFilter(d);
		setMat(_newMat);
		this.type = Dominoes.TYPE_HPF;
	}

	public void lowPassFilter(double d) throws Exception {
		this.setupOperation(false);
		MatrixOperations _newMat = mat.lowPassFilter(d);
		setMat(_newMat);
		this.type = Dominoes.TYPE_LPF;
	}

	public void filterColumnText(TextFilterData t) throws Exception {
		this.setupOperation(false);
		MatrixOperations _newMat = mat.filterColumnText(t);
		setMat(_newMat);
		this.type = Dominoes.TYPE_TEXT;
	}

	public void filterRowText(TextFilterData t) throws Exception {
		this.setupOperation(false);
		MatrixOperations _newMat = mat.filterRowText(t);
		setMat(_newMat);
		this.type = Dominoes.TYPE_TEXT;
	}

	public void diagonalize() throws Exception {
		this.setupOperation(false);
		MatrixOperations _newMat = mat.diagonalize();
		setMat(_newMat);
		this.type = Dominoes.TYPE_DIAGONAL;
	}

	public void upperDiagonal() throws Exception {
		this.setupOperation(false);
		MatrixOperations _newMat = mat.upperDiagonal();
		setMat(_newMat);
		this.type = Dominoes.TYPE_UPPER_DIAGONAL;
	}

	public void lowerDiagonal() throws Exception {
		this.setupOperation(false);
		MatrixOperations _newMat = mat.lowerDiagonal();
		setMat(_newMat);
		this.type = Dominoes.TYPE_LOWER_DIAGONAL;
	}

	public void trim() throws Exception {
		this.setupOperation(false);
		MatrixOperations _newMat = mat.trim();
		setMat(_newMat);
		this.type = Dominoes.TYPE_TRIMMED;
	}

	/**
	 * This function reduce the lines of a matrix
	 *
	 * @return the historic invert
	 * @throws Exception
	 */
	public boolean aggregateDimension() throws Exception {
		this.setupOperation(true);

		if (!(this.type == Dominoes.TYPE_BASIC)) {
			this.type = Dominoes.TYPE_DERIVED;
			if (this.getIdRow().equals(this.getIdCol())) {
				this.type = Dominoes.TYPE_SUPPORT;
			}
		}
		this.setIdRow(Dominoes.AGGREG_TEXT + idRow);
		this.historic.reduceRow();

		MatrixOperations _newMat = mat
				.aggregateDimension(currentProcessingMode.equalsIgnoreCase(Configuration.GPU_PROCESSING));
		setMat(_newMat);

		return true;
	}

	public Dominoes multiply(Dominoes dom) throws Exception {
		this.setupOperation(true);
		dom.setupOperation(true);
		Dominoes domResult = new Dominoes(dom.getDevice());

		domResult.type = Dominoes.TYPE_DERIVED;

		if (idRow.equals(dom.getIdCol())) {
			domResult.type = Dominoes.TYPE_SUPPORT;
		}
		MatrixOperations resultMat = getMat().multiply(dom.getMat(), currentProcessingMode.equalsIgnoreCase(Configuration.GPU_PROCESSING));
		String[][] resultUnderlying = null;
		if (!currentProcessingMode.equalsIgnoreCase(Configuration.GPU_PROCESSING)) {
			resultUnderlying = Prov2DominoesUtil.cloneStringMatrix(((MatrixOperationsCPU) resultMat).getUnderlyingElements());
		}
		domResult.setMat(resultMat);
		domResult.setUnderlyingElements(resultUnderlying);
		domResult.historic = new Historic(this.getHistoric(), dom.getHistoric());

		domResult.setIdRow(getIdRow());
		domResult.setIdCol(dom.getIdCol());

		return domResult;
	}

	public Dominoes sum(Dominoes dom) throws Exception {
		this.setupOperation(false);
		dom.setupOperation(false);
		Dominoes domResult = new Dominoes(dom.getDevice());

		domResult.type = Dominoes.TYPE_DERIVED;

		if (idRow.equals(dom.getIdCol())) {
			domResult.type = Dominoes.TYPE_SUPPORT;
		}
		MatrixOperations resultMat = getMat().sum(dom.getMat());
		String[][] resultUnderlying = null;
		if (currentProcessingMode.equalsIgnoreCase(Configuration.CPU_PROCESSING)) {
			resultUnderlying = Prov2DominoesUtil.cloneStringMatrix(((MatrixOperationsCPU) resultMat).getUnderlyingElements());
		}
		domResult.setMat(resultMat);
		domResult.setUnderlyingElements(resultUnderlying);
		domResult.historic = new Historic(this.getHistoric(), dom.getHistoric());

		domResult.setIdRow(getIdRow());
		domResult.setIdCol(getIdCol());

		return domResult;
	}

	public Dominoes subtract(Dominoes dom) throws Exception {
		this.setupOperation(false);
		dom.setupOperation(false);
		Dominoes domResult = new Dominoes(dom.getDevice());

		domResult.type = Dominoes.TYPE_DERIVED;

		if (idRow.equals(dom.getIdCol())) {
			domResult.type = Dominoes.TYPE_SUPPORT;
		}
		MatrixOperations resultMat = getMat().subtract(dom.getMat());
		String[][] resultUnderlying = null;
		if (currentProcessingMode.equalsIgnoreCase(Configuration.CPU_PROCESSING)) {
			resultUnderlying = Prov2DominoesUtil.cloneStringMatrix(((MatrixOperationsCPU) resultMat).getUnderlyingElements());
		}
		domResult.setMat(resultMat);
		domResult.setUnderlyingElements(resultUnderlying);
		domResult.historic = new Historic(this.getHistoric(), dom.getHistoric());

		domResult.setIdRow(getIdRow());
		domResult.setIdCol(getIdCol());

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

	public Dominoes cloneNoMatrix() {
		Dominoes cloned = new Dominoes(getIdRow(), getIdCol(), getRelation(), getDescriptor(), getMat(), getDevice());
		cloned.setType(this.type);
		cloned.setCurrentProcessingMode(getCurrentProcessingMode());
		if (getHistoric() != null) {
			cloned.setHistoric(getHistoric().clone());
		}
		cloned.setId(this.id);
		cloned.setCrsMatrix(new CRSMatrix(this.crsMatrix));
		cloned.setUnderlyingElements(Prov2DominoesUtil.cloneStringMatrix(this.underlyingElements));
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

}
