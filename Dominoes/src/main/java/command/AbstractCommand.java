package command;

public abstract class AbstractCommand {

	public static final String UNDO_COMMAND = "UNDO";
	public static final String REDO_COMMAND = "REDO";
	public static final String MOVE_COMMAND = "MOVE";
	public static final String SAVE_COMMAND = "SAVE";
	public static final String TRANSPOSE_COMMAND = "TRANSPOSE";
	public static final String MULTIPLY_COMMAND = "MULTIPLY";
	public static final String ADD_COMMAND = "ADD";
	public static final String REMOVE_COMMAND = "REMOVE";
	public static final String AGGREGATE_LINES_COMMAND = "AGG_LINES";
	public static final String AGGREGATE_COLUMNS_COMMAND = "AGG_COLUMNS";
	public static final String CONFIDENCE_COMMAND = "CONFIDENCE";
	public static final String ZSCORE_COMMAND = "ZSCORE";
	

	protected abstract String getName();

	protected abstract boolean doIt();

	protected abstract boolean undoIt();

}
