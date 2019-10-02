package provdominoes.command;

import provdominoes.boundary.App;

public class MoveCommand extends AbstractCommand {

	private int index;
	private double oldX;
	private double oldY;
	private double y;
	private double x;

	public MoveCommand() {
		this.x = -1;
		this.y = -1;
	}

	public MoveCommand(int index, double x, double y) {
		this();
		this.index = index;
		this.x = x;
		this.y = y;
	}

	public double getOldX() {
		return oldX;
	}

	public void setOldX(double oldX) {
		this.oldX = oldX;
	}

	public double getOldY() {
		return oldY;
	}

	public void setOldY(double oldY) {
		this.oldY = oldY;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public String getName() {
		String cmd = MOVE_COMMAND + "(" + App.getArea().getData().getDominoes().get(index).getId() + ", " + this.x
				+ ", " + this.y + ")";
		cmd = cmd.replace(".0", "");
		return cmd;

	}

	@Override
	protected boolean doIt() {
		App.getArea().getData().getPieces().get(index).setTranslateX(x);
		App.getArea().getData().getPieces().get(index).setTranslateY(y);
		return true;
	}

	@Override
	protected boolean undoIt() {
		App.getArea().getData().getPieces().get(index).setTranslateX(oldX);
		App.getArea().getData().getPieces().get(index).setTranslateY(oldY);
		return true;
	}

	public void setX(double translateX) {
		this.x = translateX;

	}

	public void setY(double translateY) {
		this.y = translateY;
	}

	private String id;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public double getY() {
		return y;
	}

	public double getX() {
		return x;
	}

}
