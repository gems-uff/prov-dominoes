package provdominoes.arch;

public class StringCell {
	public int row;
	public int col;
	public String value;

	public StringCell() {

	}

	public StringCell(int row, int col, String value) {
		super();
		this.row = row;
		this.col = col;
		this.value = value;
	}

	@Override
	public String toString() {
		return "StringCell [row=" + row + ", col=" + col + ", value=" + value + "]";
	}
}
