package provdominoes.command;

import provdominoes.boundary.App;

public class MoveCommand extends AbstractCommand {

	private String identifier;
	private double oldX;
	private double oldY;
	private double y;
	private double x;

	public MoveCommand() {
		this.x = -1;
		this.y = -1;
	}

	public MoveCommand(String identifier, double x, double y) {
		this();
		this.identifier = identifier;
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



	@Override
	public String getName() {
		String cmd = MOVE_COMMAND + "(" + identifier + ", " + this.x
				+ ", " + this.y + ")";
		cmd = cmd.replace(".0", "");
		return cmd;

	}

	@Override
	protected boolean doIt() {
		App.getArea().getData().getPiece(identifier).setTranslateX(x);
		App.getArea().getData().getPiece(identifier).setTranslateY(y);
		return true;
	}

	@Override
	protected boolean undoIt() {
		App.getArea().getData().getPiece(identifier).setTranslateX(oldX);
		App.getArea().getData().getPiece(identifier).setTranslateY(oldY);
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

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}

}
