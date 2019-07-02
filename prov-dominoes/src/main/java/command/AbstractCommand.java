package command;

import com.josericardojunior.domain.Dominoes;

import model.ProvMatrix.Relation;

public abstract class AbstractCommand {

	public static final String UNDO_COMMAND = "UNDO";
	public static final String REDO_COMMAND = "REDO";
	public static final String MOVE_COMMAND = "MOVE";
	public static final String SAVE_COMMAND = "SAVE";
	public static final String LOAD_COMMAND = "LOAD";
	public static final String TRANSPOSE_COMMAND = "TRANSPOSE";
	public static final String MULTIPLY_COMMAND = "MULTIPLY";
	public static final String ADD_COMMAND = "ADD";
	public static final String REMOVE_COMMAND = "REMOVE";
	public static final String AGGREGATE_LINES_COMMAND = "AGG_LINES";
	public static final String AGGREGATE_COLUMNS_COMMAND = "AGG_COLUMNS";
	public static final String CONFIDENCE_COMMAND = "CONFIDENCE";
	public static final String ZSCORE_COMMAND = "ZSCORE";
	public static final String TRANSITIVE_CLOSURE_COMMAND = "TRANSITIVE_CLOSURE";
	public static final String BINARIZE_COMMAND = "BINARIZE";
	public static final String INVERT_COMMAND = "INVERT";
	public static final String DIAGONALIZE_COMMAND = "DIAGONALIZE";
	public static final String UPPER_DIAGONAL_COMMAND = "UPPER_DIAGONAL";
	public static final String LOWER_DIAGONAL_COMMAND = "LOWER_DIAGONAL";

	public static final String CMD_PARAM_USAGE = "USD";
	public static final String CMD_PARAM_GENERATION = "WGB";
	public static final String CMD_PARAM_INVALIDATION = "WVB";
	public static final String CMD_PARAM_START = "STD";
	public static final String CMD_PARAM_END = "END ";
	public static final String CMD_PARAM_COMMUNICATION = "WFB";
	public static final String CMD_PARAM_DERIVATION = "WDF";
	public static final String CMD_PARAM_ASSOCIATION = "WAW";
	public static final String CMD_PARAM_ATTRIBUTION = "WAT";
	public static final String CMD_PARAM_DELEGATION = "AOB";
	public static final String CMD_PARAM_INFLUENCE = "WIB";
	public static final String CMD_PARAM_ALTERNATE = "AOF";
	public static final String CMD_PARAM_SPECIALIZATION = "SOF";
	public static final String CMD_PARAM_MENTION = "MOF";
	public static final String CMD_PARAM_MEMBERSHIP = "HMB";

	public abstract String getId();

	public abstract void setId(String id);

	public abstract String getName();

	protected abstract boolean doIt();

	protected abstract boolean undoIt();

	protected String cmd(Dominoes d) {
		String cmd = d.getRelation().getAbbreviate().replace(" ", "");
		if (d.getRelation() == Relation.RELATION_INFLUENCE) {
			cmd = cmd + "[" + d.getIdRow() + ", " + d.getIdCol() + "]";
		}
		return cmd;
	}

}
