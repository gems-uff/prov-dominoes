package processor;

public class Cell {
	public int row;
	public int col;
	public float value;

	public Cell() {

	}

	public Cell(int row, int col, float value) {
		super();
		this.row = row;
		this.col = col;
		this.value = value;
	}

	@Override
	public String toString() {
		return "Cell [row=" + row + ", col=" + col + ", value=" + value + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + col;
		result = prime * result + row;
		result = prime * result + Float.floatToIntBits(value);
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
		Cell other = (Cell) obj;
		if (col != other.col)
			return false;
		if (row != other.row)
			return false;
		if (Float.floatToIntBits(value) != Float.floatToIntBits(other.value))
			return false;
		return true;
	}
}
