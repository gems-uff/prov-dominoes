package provdominoes.command;

import model.ProvRelation.Relation;
import provdominoes.domain.Dominoes;

public abstract class AbstractCommand {

	public static final String UNDO_COMMAND = "UNDO";
	public static final String REDO_COMMAND = "REDO";
	public static final String MOVE_COMMAND = "MOVE";
	public static final String SAVE_COMMAND = "SAVE";
	public static final String LOAD_MATRIX_COMMAND = "LOAD_MATRIX";
	public static final String LOAD_COMMAND = "LOAD";
	public static final String TRANSPOSE_COMMAND = "TRANSPOSE";
	public static final String MULTIPLY_COMMAND = "MULTIPLY";
	public static final String ADD_COMMAND = "ADD";
	public static final String REMOVE_COMMAND = "REMOVE";
	public static final String AGGREGATE_ROWS_COMMAND = "AGG_ROWS";
	public static final String AGGREGATE_COLUMNS_COMMAND = "AGG_COLUMNS";
	public static final String CONFIDENCE_COMMAND = "CONFIDENCE";
	public static final String ZSCORE_COMMAND = "ZSCORE";
	public static final String TRANSITIVE_CLOSURE_COMMAND = "TRANSITIVE_CLOSURE";
	public static final String BINARIZE_COMMAND = "BINARIZE";
	public static final String INVERT_COMMAND = "INVERT";
	public static final String SORT_ROW_COMMAND = "SORT_ROW";
	public static final String SORT_COLUMN_COMMAND = "SORT_COLUMN";
	public static final String SORT_COLUMN_FIRST_COMMAND = "SORT_COLUMN_1ST";
	public static final String SORT_ROW_FIRST_COMMAND = "SORT_ROW_1ST";
	public static final String DIAGONALIZE_COMMAND = "DIAGONALIZE";
	public static final String UPPER_DIAGONAL_COMMAND = "UPPER_DIAGONAL";
	public static final String LOWER_DIAGONAL_COMMAND = "LOWER_DIAGONAL";
	public static final String TRIM_COMMAND = "TRIM";
	public static final String HPF_COMMAND = "HPF";
	public static final String LPF_COMMAND = "LPF";
	public static final String COLUMN_TEXT_COMMAND = "COLUMN_TEXT";
	public static final String ROW_TEXT_COMMAND = "ROW_TEXT";

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

	private boolean reproducing;

	private boolean scripting;

	protected String cmd(Dominoes d) {

		String cmd = d.getId();
		if (d.getRelation() != null) {
			cmd = d.getRelation().getAbbreviate().replace(" ", "");
		}
		if (d.getRelation() == Relation.RELATION_INFLUENCE) {
			cmd = cmd + "[" + d.getIdRow() + "," + d.getIdCol() + "]";
		}
		return cmd;
	}

	public void updateCommandManager(CommandManager cmd, boolean reproducing, boolean scripting) {
		setReproducing(reproducing);
		setScripting(scripting);
		if (doIt()) {
			System.out.println(getName());
			cmd.addToHistory(this);
			cmd.generateCommandId(this, reproducing, scripting);
		} else {
			cmd.getHistory().clear();
		}
	}

	public boolean isReproducing() {
		return reproducing;
	}

	public void setReproducing(boolean reproducing) {
		this.reproducing = reproducing;
	}

	public boolean isScripting() {
		return scripting;
	}

	public void setScripting(boolean scripting) {
		this.scripting = scripting;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
		AbstractCommand other = (AbstractCommand) obj;
		if (getId() == null) {
			if (other.getId() != null)
				return false;
		} else if (!getId().equals(other.getId()))
			return false;
		return true;
	}


}
