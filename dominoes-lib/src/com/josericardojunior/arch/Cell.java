package com.josericardojunior.arch;

public class Cell {
	public int row;
	public int col;
	public float value;

	public Cell() {

	}

	public Cell(int row, int col, float value) {
		this.row = row;
		this.col = col;
		this.value = value;
	}

	@Override
	public String toString() {
		return "Cell [row=" + row + ", col=" + col + ", value=" + value + "]";
	}
}
