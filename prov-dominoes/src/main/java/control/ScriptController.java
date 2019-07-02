package control;

import java.io.IOException;
import java.io.StringWriter;

import com.josericardojunior.domain.Dominoes;

import boundary.App;
import command.AbstractCommand;
import command.AddCommand;
import command.CommandFactory;
import command.MoveCommand;
import command.MultiplyCommand;
import command.Redo;
import command.Undo;
import javafx.scene.Group;

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
			String cmdl = commandLine.replace(" ", "").replace("\"", "").replace(")", "");
			if (cmdl.contains("=")) {
				String[] token = cmdl.split("=");
				String pieceAlias = token[0].toLowerCase();
				String cmdName = token[1].split("\\(")[0].toUpperCase();
				Group piece = null;
				if (cmdName.equals(AbstractCommand.ADD_COMMAND)) {
					String trigram = token[1].split("\\(")[1];
					for (Dominoes d : App.getList().getDominoes()) {
						if (d.getRelation().getAbbreviate().replace(" ", "").equals(trigram)) {
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
					String[] fileNames = new String[1];
					fileNames[0] = token[1];
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
					cmd = CommandFactory.getInstance().filterBinarize(piece);
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
					cmd = CommandFactory.getInstance().filterInvert(piece);
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
