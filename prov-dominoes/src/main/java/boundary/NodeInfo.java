package boundary;

import java.awt.Color;

public class NodeInfo {

	private String id;
	private String userData;
	private String tooltip;
	private Color color = Color.BLACK;
	private float threshold = 0;
	boolean isHighlighted;

	public NodeInfo(String id) {
		this.id = id;
	}

	public void setHighlighted(boolean isHighlighted) {
		this.isHighlighted = isHighlighted;
	}

	static final Color Highlight = Color.YELLOW;

	public String getUserData() {
		return userData;
	}

	public void setUserData(String userData) {
		this.userData = userData;
	}

	public Color getColor() {
		if (isHighlighted)
			return Highlight;

		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public String getId() {
		return id;
	}

	public String toString() {
		return id;
	}

	public boolean isHighlighted() {
		return isHighlighted;
	}

	public float getThreshold() {
		return threshold;
	}

	public void setThreshold(float threshold) {
		this.threshold = threshold;
	}

	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;

	}

	public String getTooltip() {
		return tooltip;
	}

}
