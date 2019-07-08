package boundary;

import java.awt.Color;

public class NodeLink {

	private float capacity;
	private float weight;
	private String originNode;
	private String destinationNode;

	public NodeLink(String id, float weight) {
		this.id = id;
		this.weight = weight;
	}

	public NodeLink(String id, String originNode, String destinationNode) {
		super();
		this.originNode = originNode;
		this.destinationNode = destinationNode;
		this.id = id;
	}

	String id;

	Color color = Color.BLACK;

	public Color getColor() {
		return color;
	}

	public float getWeight() {
		return weight;
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

	public String getOriginNode() {
		return originNode;
	}

	public void setOriginNode(String originNode) {
		this.originNode = originNode;
	}

	public String getDestinationNode() {
		return destinationNode;
	}

	public void setDestinationNode(String destinationNode) {
		this.destinationNode = destinationNode;
	}

	public void setId(String id) {
		this.id = id;
	}

	public float getCapacity() {
		return capacity;
	}

	public void setCapacity(float capacity) {
		this.capacity = capacity;
	}


	public void setWeight(float weight) {
		this.weight = weight;
	}
}
