package provdominoes.control;

import java.io.IOException;
import java.io.StringWriter;
import java.text.NumberFormat;
import java.text.ParseException;

import javafx.scene.Group;
import provdominoes.boundary.App;
import provdominoes.command.AbstractCommand;
import provdominoes.command.AddCommand;
import provdominoes.command.CommandFactory;
import provdominoes.command.MoveCommand;
import provdominoes.command.MultiplyCommand;
import provdominoes.command.Redo;
import provdominoes.command.TextFilterData;
import provdominoes.command.Undo;
import provdominoes.domain.Dominoes;
import provdominoes.util.TokenUtil;

public class ScriptController {

	private StringWriter script;
	private int undoCount;
	private String dir;

	public ScriptController(String dir) {
		this();
		this.dir = dir;
	}

	public ScriptController() {
		this.script = new StringWriter();
	}

	public void addToScript(AbstractCommand cmd) {
		if (cmd instanceof Undo) {
			undoCount++;
		} else if (cmd instanceof Redo) {
			undoCount--;
		} else {
			flushPendingUndos();
			this.script.append(cmd.getName() + "\n");
		}
	}

	private void flushPendingUndos() {
		if (undoCount > 0) {
			if (undoCount == 1) {
				this.script.append("UNDO()\n");
			} else {
				this.script.append("UNDO(" + undoCount + ")\n");
			}
			undoCount = 0;
		}
	}

	public String getScript() {
		flushPendingUndos();
		return this.script.toString();
	}

	public int getUndoCount() {
		return undoCount;
	}

	public void setUndoCount(int undoCount) {
		this.undoCount = undoCount;
	}

	public void clear() throws IOException {
		this.script.flush();
		this.script.close();
		this.script = new StringWriter();
		this.undoCount = 0;
	}

	public AbstractCommand parseCommand(String commandLine) {
		AbstractCommand cmd = null;
		if (commandLine != null) {

			commandLine = TokenUtil.getInstance().supressReserved(commandLine);
			if (commandLine.contains(AbstractCommand.SAVE_COMMAND)) {
				commandLine = commandLine.substring(0, commandLine.indexOf(")")+1);
			}
			String cmdl = commandLine.replace(" ", "").replace("\"", "").replace(")", "");

			if (cmdl.contains("=")) {
				String[] token = cmdl.split("=");
				String pieceAlias = token[0].toLowerCase();
				String cmdName = token[1].split("\\(")[0].toUpperCase();
				Group piece = null;
				if (cmdName.equals(AbstractCommand.ADD_COMMAND)) {
					String trigram = token[1].split("\\(")[1];
					for (Dominoes d : App.getList().getDominoes()) {
						if (d.getId().equals(trigram)) {
							int index = App.getList().getDominoes().indexOf(d);
							piece = App.getList().getPieces().get(index);
							break;
						}
					}
					if (piece != null) {
						AddCommand add = CommandFactory.getInstance().add(trigram);
						add.setKey(pieceAlias);
						cmd = add;
					}
				} else if (cmdName.equals(AbstractCommand.MULTIPLY_COMMAND)) {
					String[] operands = token[1].split("\\(")[1].split(",");
					int indexLeft = -1;
					int indexRight = -1;
					for (Dominoes d : App.getArea().getData().getDominoes()) {
						if (d.getId().equals(operands[0].toLowerCase())) {
							indexLeft = App.getArea().getData().getDominoes().indexOf(d);
							App.getArea().getData().setIndexFirstOperatorMultiplication(indexLeft);
						}
						if (d.getId().equals(operands[1].toLowerCase())) {
							indexRight = App.getArea().getData().getDominoes().indexOf(d);
							App.getArea().getData().setIndexSecondOperatorMultiplication(indexRight);
						}
						if (indexLeft != -1 && indexRight != -1) {
							break;
						}
					}
					if (indexLeft != -1 && indexRight != -1) {
						MultiplyCommand mult = new MultiplyCommand();
						mult.setKey(pieceAlias);
						cmd = mult;
					}
				}
			} else {
				String[] token = cmdl.split("\\(");
				token[0] = token[0].toUpperCase();
				if (token[0].equals(AbstractCommand.LOAD_COMMAND)) {
					String[] fileNames = token[1].split(",");
					cmd = CommandFactory.getInstance().load(fileNames, dir);
				} else if (token[0].equals(AbstractCommand.MOVE_COMMAND)) {
					Group piece = null;
					String[] operands = token[1].split(",");
					String pieceId = operands[0].toLowerCase();
					for (Dominoes d : App.getArea().getData().getDominoes()) {
						if (d.getId().equals(pieceId)) {
							int index = App.getArea().getData().getDominoes().indexOf(d);
							piece = App.getArea().getData().getPieces().get(index);
						}
					}
					if (piece != null) {
						MoveCommand move = CommandFactory.getInstance().move(piece, piece.getTranslateX(),
								piece.getTranslateY());
						move.setX(Double.valueOf(operands[1]));
						move.setY(Double.valueOf(operands[2]));
						cmd = move;
					}
				} else if (token[0].equals(AbstractCommand.TRANSPOSE_COMMAND)) {
					Group piece = null;
					for (Dominoes d : App.getArea().getData().getDominoes()) {
						if (d.getId().equals(token[1].toLowerCase())) {
							int index = App.getArea().getData().getDominoes().indexOf(d);
							piece = App.getArea().getData().getPieces().get(index);
						}
					}
					cmd = CommandFactory.getInstance().transpose(piece);
				} else if (token[0].equals(AbstractCommand.TRANSITIVE_CLOSURE_COMMAND)) {
					Group piece = null;
					for (Dominoes d : App.getArea().getData().getDominoes()) {
						if (d.getId().equals(token[1].toLowerCase())) {
							int index = App.getArea().getData().getDominoes().indexOf(d);
							piece = App.getArea().getData().getPieces().get(index);
						}
					}
					cmd = CommandFactory.getInstance().transitiveClosure(piece);
				} else if (token[0].equals(AbstractCommand.AGGREGATE_COLUMNS_COMMAND)) {
					Group piece = null;
					for (Dominoes d : App.getArea().getData().getDominoes()) {
						if (d.getId().equals(token[1].toLowerCase())) {
							int index = App.getArea().getData().getDominoes().indexOf(d);
							piece = App.getArea().getData().getPieces().get(index);
						}
					}
					cmd = CommandFactory.getInstance().aggColumns(piece);
				} else if (token[0].equals(AbstractCommand.AGGREGATE_LINES_COMMAND)) {
					Group piece = null;
					for (Dominoes d : App.getArea().getData().getDominoes()) {
						if (d.getId().equals(token[1].toLowerCase())) {
							int index = App.getArea().getData().getDominoes().indexOf(d);
							piece = App.getArea().getData().getPieces().get(index);
						}
					}
					cmd = CommandFactory.getInstance().aggLines(piece);
				} else if (token[0].equals(AbstractCommand.BINARIZE_COMMAND)) {
					Group piece = null;
					for (Dominoes d : App.getArea().getData().getDominoes()) {
						if (d.getId().equals(token[1].toLowerCase())) {
							int index = App.getArea().getData().getDominoes().indexOf(d);
							piece = App.getArea().getData().getPieces().get(index);
						}
					}
					cmd = CommandFactory.getInstance().binarize(piece);
				} else if (token[0].equals(AbstractCommand.CONFIDENCE_COMMAND)) {
					Group piece = null;
					for (Dominoes d : App.getArea().getData().getDominoes()) {
						if (d.getId().equals(token[1].toLowerCase())) {
							int index = App.getArea().getData().getDominoes().indexOf(d);
							piece = App.getArea().getData().getPieces().get(index);
						}
					}
					cmd = CommandFactory.getInstance().confidence(piece);
				} else if (token[0].equals(AbstractCommand.DIAGONALIZE_COMMAND)) {
					Group piece = null;
					for (Dominoes d : App.getArea().getData().getDominoes()) {
						if (d.getId().equals(token[1].toLowerCase())) {
							int index = App.getArea().getData().getDominoes().indexOf(d);
							piece = App.getArea().getData().getPieces().get(index);
						}
					}
					cmd = CommandFactory.getInstance().confidence(piece);
				} else if (token[0].equals(AbstractCommand.INVERT_COMMAND)) {
					Group piece = null;
					for (Dominoes d : App.getArea().getData().getDominoes()) {
						if (d.getId().equals(token[1].toLowerCase())) {
							int index = App.getArea().getData().getDominoes().indexOf(d);
							piece = App.getArea().getData().getPieces().get(index);
						}
					}
					cmd = CommandFactory.getInstance().invert(piece);
				} else if (token[0].equals(AbstractCommand.HPF_COMMAND)) {
					String[] operands = token[1].split(",");
					String percent = operands[1].toLowerCase();
					Group piece = null;
					for (Dominoes d : App.getArea().getData().getDominoes()) {
						if (d.getId().equals(operands[0].toLowerCase())) {
							int index = App.getArea().getData().getDominoes().indexOf(d);
							piece = App.getArea().getData().getPieces().get(index);
						}
					}
					try {
						cmd = CommandFactory.getInstance().filterHighPass(piece,
								NumberFormat.getInstance().parse(percent).doubleValue());
					} catch (ParseException e) {
						App.alertException(e, "Erro desconhecido ao processar comando: " + cmdl);
					}
				} else if (token[0].equals(AbstractCommand.LPF_COMMAND)) {
					String[] operands = token[1].split(",");
					String percent = operands[1].toLowerCase();
					Group piece = null;
					for (Dominoes d : App.getArea().getData().getDominoes()) {
						if (d.getId().equals(operands[0].toLowerCase())) {
							int index = App.getArea().getData().getDominoes().indexOf(d);
							piece = App.getArea().getData().getPieces().get(index);
						}
					}
					try {
						cmd = CommandFactory.getInstance().filterLowPass(piece,
								NumberFormat.getInstance().parse(percent).doubleValue());
					} catch (ParseException e) {
						App.alertException(e, "Erro desconhecido ao processar comando: " + cmdl);
					}
				} else if (token[0].equals(AbstractCommand.COLUMN_TEXT_COMMAND)) {
					String[] operands = token[1].split(",");
					String isRegexp = operands[1].toLowerCase().replace("\"", "");
					String isCaseSensitive = operands[2].toLowerCase().replace("\"", "");
					String exp = operands[3];
					exp = "\"" + exp + "\"";
					exp = TokenUtil.getInstance().impressReserved(exp).replace("\"", "");
					TextFilterData t = new TextFilterData(exp, Boolean.parseBoolean(isRegexp),
							Boolean.parseBoolean(isCaseSensitive));
					Group piece = null;
					for (Dominoes d : App.getArea().getData().getDominoes()) {
						if (d.getId().equals(operands[0].toLowerCase())) {
							int index = App.getArea().getData().getDominoes().indexOf(d);
							piece = App.getArea().getData().getPieces().get(index);
						}
					}
					cmd = CommandFactory.getInstance().filterColumnText(piece, t);
				} else if (token[0].equals(AbstractCommand.ROW_TEXT_COMMAND)) {
					String[] operands = token[1].split(",");
					String isRegexp = operands[1].toLowerCase().replace("\"", "");
					String isCaseSensitive = operands[2].toLowerCase().replace("\"", "");
					String exp = operands[3];
					exp = "\"" + exp + "\"";
					exp = TokenUtil.getInstance().impressReserved(exp).replace("\"", "");
					TextFilterData t = new TextFilterData(exp, Boolean.parseBoolean(isRegexp),
							Boolean.parseBoolean(isCaseSensitive));
					Group piece = null;
					for (Dominoes d : App.getArea().getData().getDominoes()) {
						if (d.getId().equals(operands[0].toLowerCase())) {
							int index = App.getArea().getData().getDominoes().indexOf(d);
							piece = App.getArea().getData().getPieces().get(index);
						}
					}
					cmd = CommandFactory.getInstance().filterRowText(piece, t);
				} else if (token[0].equals(AbstractCommand.SORT_ROW_COMMAND)) {
					Group piece = null;
					for (Dominoes d : App.getArea().getData().getDominoes()) {
						if (d.getId().equals(token[1].toLowerCase())) {
							int index = App.getArea().getData().getDominoes().indexOf(d);
							piece = App.getArea().getData().getPieces().get(index);
						}
					}
					cmd = CommandFactory.getInstance().sortRows(piece);
				} else if (token[0].equals(AbstractCommand.SORT_COLUMN_COMMAND)) {
					Group piece = null;
					for (Dominoes d : App.getArea().getData().getDominoes()) {
						if (d.getId().equals(token[1].toLowerCase())) {
							int index = App.getArea().getData().getDominoes().indexOf(d);
							piece = App.getArea().getData().getPieces().get(index);
						}
					}
					cmd = CommandFactory.getInstance().sortColumns(piece);
				} else if (token[0].equals(AbstractCommand.SORT_JOIN_ROWS_COMMAND)) {
					Group piece = null;
					for (Dominoes d : App.getArea().getData().getDominoes()) {
						if (d.getId().equals(token[1].toLowerCase())) {
							int index = App.getArea().getData().getDominoes().indexOf(d);
							piece = App.getArea().getData().getPieces().get(index);
						}
					}
					cmd = CommandFactory.getInstance().sortJoinRows(piece);
				} else if (token[0].equals(AbstractCommand.SORT_JOIN_COLS_COMMAND)) {
					Group piece = null;
					for (Dominoes d : App.getArea().getData().getDominoes()) {
						if (d.getId().equals(token[1].toLowerCase())) {
							int index = App.getArea().getData().getDominoes().indexOf(d);
							piece = App.getArea().getData().getPieces().get(index);
						}
					}
					cmd = CommandFactory.getInstance().sortJoinCols(piece);
				} else if (token[0].equals(AbstractCommand.LOWER_DIAGONAL_COMMAND)) {
					Group piece = null;
					for (Dominoes d : App.getArea().getData().getDominoes()) {
						if (d.getId().equals(token[1].toLowerCase())) {
							int index = App.getArea().getData().getDominoes().indexOf(d);
							piece = App.getArea().getData().getPieces().get(index);
						}
					}
					cmd = CommandFactory.getInstance().filterLowerDiagonal(piece);
				} else if (token[0].equals(AbstractCommand.UPPER_DIAGONAL_COMMAND)) {
					Group piece = null;
					for (Dominoes d : App.getArea().getData().getDominoes()) {
						if (d.getId().equals(token[1].toLowerCase())) {
							int index = App.getArea().getData().getDominoes().indexOf(d);
							piece = App.getArea().getData().getPieces().get(index);
						}
					}
					cmd = CommandFactory.getInstance().filterUpperDiagonal(piece);
				} else if (token[0].equals(AbstractCommand.TRIM_COMMAND)) {
					Group piece = null;
					for (Dominoes d : App.getArea().getData().getDominoes()) {
						if (d.getId().equals(token[1].toLowerCase())) {
							int index = App.getArea().getData().getDominoes().indexOf(d);
							piece = App.getArea().getData().getPieces().get(index);
						}
					}
					cmd = CommandFactory.getInstance().trim(piece);
				} else if (token[0].equals(AbstractCommand.REMOVE_COMMAND)) {
					Group piece = null;
					for (Dominoes d : App.getArea().getData().getDominoes()) {
						if (d.getId().equals(token[1].toLowerCase())) {
							int index = App.getArea().getData().getDominoes().indexOf(d);
							piece = App.getArea().getData().getPieces().get(index);
						}
					}
					cmd = CommandFactory.getInstance().remove(piece);
				} else if (token[0].equals(AbstractCommand.SAVE_COMMAND)) {
					Group piece = null;
					for (Dominoes d : App.getArea().getData().getDominoes()) {
						if (d.getId().equals(token[1].toLowerCase())) {
							int index = App.getArea().getData().getDominoes().indexOf(d);
							piece = App.getArea().getData().getPieces().get(index);
						}
					}
					cmd = CommandFactory.getInstance().save(piece);
				} else if (token[0].equals(AbstractCommand.ZSCORE_COMMAND)) {
					Group piece = null;
					for (Dominoes d : App.getArea().getData().getDominoes()) {
						if (d.getId().equals(token[1].toLowerCase())) {
							int index = App.getArea().getData().getDominoes().indexOf(d);
							piece = App.getArea().getData().getPieces().get(index);
						}
					}
					cmd = CommandFactory.getInstance().zscore(piece);
				} else if (token[0].equals(AbstractCommand.UNDO_COMMAND)) {
					int count = 1;
					if (token.length > 1) {
						String countToken = token[1];
						if (countToken.length() > 0) {
							count = Integer.valueOf(countToken);
						}
					}
					cmd = CommandFactory.getInstance().undo(count);
				}
			}
		}
		return cmd;
	}

	public void setDir(String dir) {
		this.dir = dir;
	}

}
