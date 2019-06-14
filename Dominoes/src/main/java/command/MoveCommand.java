package command;

import boundary.App;

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

	public MoveCommand(int index) {
		this();
		this.index = index;
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
		return MOVE_COMMAND + "(" + App.getArea().getData().getDominoes().get(index).getId() + ")";
		
	}

	@Override
	protected boolean doIt() {
		if (this.x == -1 && this.y == -1) {
			this.oldX = App.getArea().getData().getPieces().get(index).getTranslateX();
			this.oldY = App.getArea().getData().getPieces().get(index).getTranslateY();
		} else {
			App.getArea().getData().getPieces().get(index).setTranslateX(x);
			App.getArea().getData().getPieces().get(index).setTranslateY(y);
		}
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

}
