package processor;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.la4j.matrix.sparse.CRSMatrix;

class MatrixProcessorTest {

	@Test
	void gpuTest() {
		assertTrue(MatrixProcessor.isGPUEnabled());
	}

	@Test
	void binarizeTest() {
		long pointerMatrix = MatrixProcessor.createMatrixData(3, 4, true);
		CRSMatrix matrix = new CRSMatrix(3, 4);
		matrix.set(0, 0, 0);
		matrix.set(0, 1, -3);
		matrix.set(0, 2, -900);
		matrix.set(0, 3, 386.8);

		matrix.set(1, 0, -25.9);
		matrix.set(1, 1, -3);
		matrix.set(1, 2, 0);
		matrix.set(1, 3, 0);

		matrix.set(2, 0, 0);
		matrix.set(2, 1, 12.6);
		matrix.set(2, 2, -3);
		matrix.set(2, 3, 0);

		ArrayList<Cell> cells = matrix2cellList(matrix);

		int[] rows = new int[cells.size()];
		int[] cols = new int[cells.size()];
		float[] values = new float[cells.size()];

		cellList2Arrays(cells, rows, cols, values);

		MatrixProcessor.setData(pointerMatrix, rows, cols, values);

		long pointerResultMatrix = MatrixProcessor.createMatrixData(3, 4, true);

		MatrixProcessor.binarize(pointerMatrix, pointerResultMatrix);

		Cell[] response = MatrixProcessor.getSparseData(pointerResultMatrix);

		assertNotNull(response);
		assertTrue(response.length == 12);

		CRSMatrix resp = toCRSMatrix(response, 3, 4);
		assertTrue(resp.get(0, 0) == 0.0);
		assertTrue(resp.get(0, 1) == 0.0);
		assertTrue(resp.get(0, 2) == 0.0);
		assertTrue(resp.get(0, 3) == 1.0);

		assertTrue(resp.get(1, 0) == 0.0);
		assertTrue(resp.get(1, 1) == 0.0);
		assertTrue(resp.get(1, 2) == 0.0);
		assertTrue(resp.get(1, 3) == 0.0);

		assertTrue(resp.get(2, 0) == 0.0);
		assertTrue(resp.get(2, 1) == 1.0);
		assertTrue(resp.get(2, 2) == 0.0);
		assertTrue(resp.get(2, 3) == 0.0);
	}
	
	@Test
	void sumTest() {
		long pointerMatrix1 = MatrixProcessor.createMatrixData(3, 3, false);
		CRSMatrix matrix1 = new CRSMatrix(3, 3);
		matrix1.set(0, 0, 1.5);
		matrix1.set(0, 1, 1.5);
		matrix1.set(0, 2, 1.5);

		matrix1.set(1, 0, 1.5);
		matrix1.set(1, 1, 1.5);
		matrix1.set(1, 2, 1.5);

		matrix1.set(2, 0, 1.5);
		matrix1.set(2, 1, 1.5);
		matrix1.set(2, 2, 1.5);

		ArrayList<Cell> cells1 = matrix2cellList(matrix1);
		
		int[] rows1 = new int[cells1.size()];
		int[] cols1 = new int[cells1.size()];
		float[] values1 = new float[cells1.size()];

		cellList2Arrays(cells1, rows1, cols1, values1);

		MatrixProcessor.setData(pointerMatrix1, values1);
		
		long pointerMatrix2 = MatrixProcessor.createMatrixData(3, 3, false);
		CRSMatrix matrix2 = new CRSMatrix(3, 3);
		matrix2.set(0, 0, 1.5);
		matrix2.set(0, 1, 1.5);
		matrix2.set(0, 2, 1.5);

		matrix2.set(1, 0, 1.5);
		matrix2.set(1, 1, 1.5);
		matrix2.set(1, 2, 1.5);

		matrix2.set(2, 0, 1.5);
		matrix2.set(2, 1, 1.5);
		matrix2.set(2, 2, 1.5);

		ArrayList<Cell> cells2 = matrix2cellList(matrix2);
		
		int[] rows2 = new int[cells2.size()];
		int[] cols2 = new int[cells2.size()];
		float[] values2 = new float[cells2.size()];

		cellList2Arrays(cells2, rows2, cols2, values2);

		MatrixProcessor.setData(pointerMatrix2, values2);
		
		long pointerResultMatrix = MatrixProcessor.createMatrixData(3, 3, false);
		
		MatrixProcessor.sum(pointerMatrix1, pointerMatrix2, 9, pointerResultMatrix);

		Cell[] response = MatrixProcessor.getData(pointerResultMatrix, rows1, cols1);

		assertNotNull(response);
		assertTrue(response.length == 9);

		CRSMatrix resp = toCRSMatrix(response, 3, 3);
		assertTrue(resp.get(0, 0) == 3.0);
		assertTrue(resp.get(0, 1) == 3.0);
		assertTrue(resp.get(0, 2) == 3.0);

		assertTrue(resp.get(1, 0) == 3.0);
		assertTrue(resp.get(1, 1) == 3.0);
		assertTrue(resp.get(1, 1) == 3.0);

		assertTrue(resp.get(2, 0) == 3.0);
		assertTrue(resp.get(2, 1) == 3.0);
		assertTrue(resp.get(2, 2) == 3.0);
	}
	
	@Test
	void subtractTest() {
		long pointerMatrix1 = MatrixProcessor.createMatrixData(3, 3, false);
		CRSMatrix matrix1 = new CRSMatrix(3, 3);
		matrix1.set(0, 0, 3.0);
		matrix1.set(0, 1, 3.0);
		matrix1.set(0, 2, 3.0);

		matrix1.set(1, 0, 3.0);
		matrix1.set(1, 1, 3.0);
		matrix1.set(1, 2, 3.0);

		matrix1.set(2, 0, 3.0);
		matrix1.set(2, 1, 3.0);
		matrix1.set(2, 2, 3.0);

		ArrayList<Cell> cells1 = matrix2cellList(matrix1);
		
		int[] rows1 = new int[cells1.size()];
		int[] cols1 = new int[cells1.size()];
		float[] values1 = new float[cells1.size()];

		cellList2Arrays(cells1, rows1, cols1, values1);

		MatrixProcessor.setData(pointerMatrix1, values1);
		
		long pointerMatrix2 = MatrixProcessor.createMatrixData(3, 3, false);
		CRSMatrix matrix2 = new CRSMatrix(3, 3);
		matrix2.set(0, 0, 1.2);
		matrix2.set(0, 1, 1.2);
		matrix2.set(0, 2, 1.2);

		matrix2.set(1, 0, 1.2);
		matrix2.set(1, 1, 1.2);
		matrix2.set(1, 2, 1.2);

		matrix2.set(2, 0, 1.2);
		matrix2.set(2, 1, 1.2);
		matrix2.set(2, 2, 1.2);

		ArrayList<Cell> cells2 = matrix2cellList(matrix2);
		
		int[] rows2 = new int[cells2.size()];
		int[] cols2 = new int[cells2.size()];
		float[] values2 = new float[cells2.size()];

		cellList2Arrays(cells2, rows2, cols2, values2);

		MatrixProcessor.setData(pointerMatrix2, values2);
		
		long pointerResultMatrix = MatrixProcessor.createMatrixData(3, 3, false);
		
		MatrixProcessor.subtract(pointerMatrix1, pointerMatrix2, 9, pointerResultMatrix);

		Cell[] response = MatrixProcessor.getData(pointerResultMatrix, rows1, cols1);

		assertNotNull(response);
		assertTrue(response.length == 9);

		CRSMatrix resp = toCRSMatrix(response, 3, 3);
		assertEquals("1,8", new DecimalFormat("#.#####").format(resp.get(0, 0)));
		assertEquals("1,8", new DecimalFormat("#.#####").format(resp.get(0, 1)));
		assertEquals("1,8", new DecimalFormat("#.#####").format(resp.get(0, 2)));
		assertEquals("1,8", new DecimalFormat("#.#####").format(resp.get(1, 0)));
		assertEquals("1,8", new DecimalFormat("#.#####").format(resp.get(1, 1)));
		assertEquals("1,8", new DecimalFormat("#.#####").format(resp.get(1, 2)));
		assertEquals("1,8", new DecimalFormat("#.#####").format(resp.get(2, 0)));
		assertEquals("1,8", new DecimalFormat("#.#####").format(resp.get(2, 1)));
		assertEquals("1,8", new DecimalFormat("#.#####").format(resp.get(2, 2)));
		
	}

	@Test
	void invertTest() {
		long pointerMatrix = MatrixProcessor.createMatrixData(3, 4, false);
		CRSMatrix matrix = new CRSMatrix(3, 4);
		matrix.set(0, 0, 0);
		matrix.set(0, 1, -3);
		matrix.set(0, 2, -900);
		matrix.set(0, 3, 386.8);

		matrix.set(1, 0, -25.9);
		matrix.set(1, 1, -3);
		matrix.set(1, 2, 0);
		matrix.set(1, 3, 0);

		matrix.set(2, 0, 0);
		matrix.set(2, 1, 12.6);
		matrix.set(2, 2, -3);
		matrix.set(2, 3, 0);

		ArrayList<Cell> cells = matrix2cellList(matrix);

		int[] rows = new int[cells.size()];
		int[] cols = new int[cells.size()];
		float[] values = new float[cells.size()];

		cellList2Arrays(cells, rows, cols, values);

		MatrixProcessor.setData(pointerMatrix, values);

		long pointerResultMatrix = MatrixProcessor.createMatrixData(3, 4, false);

		MatrixProcessor.invert(3* 4, pointerMatrix, pointerResultMatrix);

		Cell[] response = MatrixProcessor.getData(pointerResultMatrix, rows, cols);

		assertNotNull(response);
		assertTrue(response.length == 12);

		CRSMatrix resp = toCRSMatrix(response, 3, 4);
		assertTrue(resp.get(0, 0) == 1.0);
		assertTrue(resp.get(0, 1) == 1.0);
		assertTrue(resp.get(0, 2) == 1.0);
		assertTrue(resp.get(0, 3) == 0.0);

		assertTrue(resp.get(1, 0) == 1.0);
		assertTrue(resp.get(1, 1) == 1.0);
		assertTrue(resp.get(1, 2) == 1.0);
		assertTrue(resp.get(1, 3) == 1.0);

		assertTrue(resp.get(2, 0) == 1.0);
		assertTrue(resp.get(2, 1) == 0.0);
		assertTrue(resp.get(2, 2) == 1.0);
		assertTrue(resp.get(2, 3) == 1.0);
	}
	
	@Test
	void diagonalizeTest() {
		long pointerMatrix = MatrixProcessor.createMatrixData(4, 4, false);
		CRSMatrix matrix = new CRSMatrix(4, 4);
		matrix.set(0, 0, 5);
		matrix.set(0, 1, -7);
		matrix.set(0, 2, -900);
		matrix.set(0, 3, 386.8);

		matrix.set(1, 0, -25.9);
		matrix.set(1, 1, -3);
		matrix.set(1, 2, 0);
		matrix.set(1, 3, 0);

		matrix.set(2, 0, 8);
		matrix.set(2, 1, 12.6);
		matrix.set(2, 2, 19);
		matrix.set(2, 3, 0);

		matrix.set(3, 0, 0);
		matrix.set(3, 1, 12.6);
		matrix.set(3, 2, -3);
		matrix.set(3, 3, 985);

		ArrayList<Cell> cells = matrix2cellList(matrix);

		int[] rows = new int[cells.size()];
		int[] cols = new int[cells.size()];
		float[] values = new float[cells.size()];

		cellList2Arrays(cells, rows, cols, values);

		MatrixProcessor.setData(pointerMatrix, values);

		long pointerResultMatrix = MatrixProcessor.createMatrixData(4, 4, false);

		MatrixProcessor.diagonalize(4, pointerMatrix, pointerResultMatrix);

		Cell[] response = MatrixProcessor.getData(pointerResultMatrix, rows, cols);

		assertNotNull(response);
		assertTrue(response.length == 16);

		CRSMatrix resp = toCRSMatrix(response, 4, 4);
		assertTrue(resp.get(0, 0) == 5.0);
		assertTrue(resp.get(1, 1) == -3.0);
		assertTrue(resp.get(2, 2) == 19.0);
		assertTrue(resp.get(3, 3) == 985.0);

		assertTrue(resp.get(0, 1) == 0.0);
		assertTrue(resp.get(0, 2) == 0.0);
		assertTrue(resp.get(0, 3) == 0.0);

		assertTrue(resp.get(1, 0) == 0.0);
		assertTrue(resp.get(1, 2) == 0.0);
		assertTrue(resp.get(1, 3) == 0.0);

		assertTrue(resp.get(2, 0) == 0.0);
		assertTrue(resp.get(2, 1) == 0.0);
		assertTrue(resp.get(2, 3) == 0.0);

		assertTrue(resp.get(3, 0) == 0.0);
		assertTrue(resp.get(3, 1) == 0.0);
		assertTrue(resp.get(3, 2) == 0.0);
	}

	@Test
	void upperDiagonalTest() {
		long pointerMatrix = MatrixProcessor.createMatrixData(4, 4, false);
		CRSMatrix matrix = new CRSMatrix(4, 4);
		matrix.set(0, 0, 5);
		matrix.set(0, 1, -7);
		matrix.set(0, 2, -900);
		matrix.set(0, 3, 386.8);

		matrix.set(1, 0, -25.9);
		matrix.set(1, 1, -3);
		matrix.set(1, 2, 3);
		matrix.set(1, 3, -200);

		matrix.set(2, 0, 8);
		matrix.set(2, 1, 12.6);
		matrix.set(2, 2, 19);
		matrix.set(2, 3, 0);

		matrix.set(3, 0, 0);
		matrix.set(3, 1, 12.6);
		matrix.set(3, 2, -3);
		matrix.set(3, 3, 985);

		ArrayList<Cell> cells = matrix2cellList(matrix);

		int[] rows = new int[cells.size()];
		int[] cols = new int[cells.size()];
		float[] values = new float[cells.size()];

		cellList2Arrays(cells, rows, cols, values);

		MatrixProcessor.setData(pointerMatrix, values);

		long pointerResultMatrix = MatrixProcessor.createMatrixData(4, 4, false);

		MatrixProcessor.upperDiagonal(4, pointerMatrix, pointerResultMatrix);

		Cell[] response = MatrixProcessor.getData(pointerResultMatrix, rows, cols);

		assertNotNull(response);
		assertTrue(response.length == 16);

		CRSMatrix resp = toCRSMatrix(response, 4, 4);
		assertTrue(resp.get(0, 0) == 5.0);
		assertTrue(resp.get(0, 1) == -7.0);
		assertTrue(resp.get(0, 2) == -900.0);
		assertTrue(new DecimalFormat("#.##").format(resp.get(0, 3)).equals("386,8"));

		assertTrue(resp.get(1, 0) == 0.0);
		assertTrue(resp.get(1, 1) == -3.0);
		assertTrue(resp.get(1, 2) == 3.0);
		assertTrue(resp.get(1, 3) == -200.0);

		assertTrue(resp.get(2, 0) == 0.0);
		assertTrue(resp.get(2, 1) == 0.0);
		assertTrue(resp.get(2, 2) == 19.0);
		assertTrue(resp.get(2, 3) == 0.0);

		assertTrue(resp.get(3, 0) == 0.0);
		assertTrue(resp.get(3, 1) == 0.0);
		assertTrue(resp.get(3, 2) == 0.0);
		assertTrue(resp.get(3, 3) == 985.0);
	}

	@Test
	void lowerDiagonalTest() {
		long pointerMatrix = MatrixProcessor.createMatrixData(4, 4, false);
		CRSMatrix matrix = new CRSMatrix(4, 4);
		matrix.set(0, 0, 5);
		matrix.set(0, 1, -7);
		matrix.set(0, 2, -900);
		matrix.set(0, 3, 386.8);

		matrix.set(1, 0, -25.9);
		matrix.set(1, 1, -3);
		matrix.set(1, 2, 3);
		matrix.set(1, 3, -200);

		matrix.set(2, 0, 8);
		matrix.set(2, 1, 12.6);
		matrix.set(2, 2, 19);
		matrix.set(2, 3, 0);

		matrix.set(3, 0, 0);
		matrix.set(3, 1, 27.4);
		matrix.set(3, 2, -3);
		matrix.set(3, 3, 985);

		ArrayList<Cell> cells = matrix2cellList(matrix);

		int[] rows = new int[cells.size()];
		int[] cols = new int[cells.size()];
		float[] values = new float[cells.size()];

		cellList2Arrays(cells, rows, cols, values);

		MatrixProcessor.setData(pointerMatrix, values);

		long pointerResultMatrix = MatrixProcessor.createMatrixData(4, 4, false);

		MatrixProcessor.lowerDiagonal(4, pointerMatrix, pointerResultMatrix);

		Cell[] response = MatrixProcessor.getData(pointerResultMatrix, rows, cols);

		assertNotNull(response);
		assertTrue(response.length == 16);

		CRSMatrix resp = toCRSMatrix(response, 4, 4);
		assertTrue(resp.get(0, 0) == 5.0);
		assertTrue(resp.get(0, 1) == 0.0);
		assertTrue(resp.get(0, 2) == 0.0);
		assertTrue(resp.get(0, 3) == 0.0);

		assertTrue(new DecimalFormat("#.##").format(resp.get(1, 0)).equals("-25,9"));
		assertTrue(resp.get(1, 1) == -3.0);
		assertTrue(resp.get(1, 2) == 0.0);
		assertTrue(resp.get(1, 3) == 0.0);

		assertTrue(resp.get(2, 0) == 8.0);
		assertTrue(new DecimalFormat("#.##").format(resp.get(2, 1)).equals("12,6"));
		assertTrue(resp.get(2, 2) == 19.0);
		assertTrue(resp.get(2, 3) == 0.0);

		assertTrue(resp.get(3, 0) == 0.0);
		assertTrue(new DecimalFormat("#.##").format(resp.get(3, 1)).equals("27,4"));
		assertTrue(resp.get(3, 2) == -3.0);
		assertTrue(resp.get(3, 3) == 985.0);
	}

	@Test
	void transitiveClosureTest1() {
		int v = 9;
		long pointerMatrix = MatrixProcessor.createMatrixData(v, v, false);
		CRSMatrix matrix = new CRSMatrix(v, v);
		matrix.set(0, 0, 1);
		matrix.set(1, 0, 0);
		matrix.set(2, 0, 0);
		matrix.set(3, 0, 0);
		matrix.set(4, 0, 0);
		matrix.set(5, 0, 0);
		matrix.set(6, 0, 1);
		matrix.set(7, 0, 0);
		matrix.set(8, 0, 0);

		matrix.set(0, 1, 0);
		matrix.set(1, 1, 1);
		matrix.set(2, 1, 0);
		matrix.set(3, 1, 1);
		matrix.set(4, 1, 0);
		matrix.set(5, 1, 0);
		matrix.set(6, 1, 0);
		matrix.set(7, 1, 0);
		matrix.set(8, 1, 0);

		matrix.set(0, 2, 1);
		matrix.set(1, 2, 0);
		matrix.set(2, 2, 1);
		matrix.set(3, 2, 0);
		matrix.set(4, 2, 0);
		matrix.set(5, 2, 0);
		matrix.set(6, 2, 0);
		matrix.set(7, 2, 0);
		matrix.set(8, 2, 0);

		matrix.set(0, 3, 0);
		matrix.set(1, 3, 0);
		matrix.set(2, 3, 0);
		matrix.set(3, 3, 1);
		matrix.set(4, 3, 0);
		matrix.set(5, 3, 0);
		matrix.set(6, 3, 0);
		matrix.set(7, 3, 0);
		matrix.set(8, 3, 0);

		matrix.set(0, 4, 0);
		matrix.set(1, 4, 0);
		matrix.set(2, 4, 1);
		matrix.set(3, 4, 0);
		matrix.set(4, 4, 1);
		matrix.set(5, 4, 0);
		matrix.set(6, 4, 0);
		matrix.set(7, 4, 0);
		matrix.set(8, 4, 0);

		matrix.set(0, 5, 0);
		matrix.set(1, 5, 1);
		matrix.set(2, 5, 0);
		matrix.set(3, 5, 0);
		matrix.set(4, 5, 0);
		matrix.set(5, 5, 1);
		matrix.set(6, 5, 0);
		matrix.set(7, 5, 0);
		matrix.set(8, 5, 0);

		matrix.set(0, 6, 0);
		matrix.set(1, 6, 0);
		matrix.set(2, 6, 0);
		matrix.set(3, 6, 0);
		matrix.set(4, 6, 0);
		matrix.set(5, 6, 0);
		matrix.set(6, 6, 1);
		matrix.set(7, 6, 0);
		matrix.set(8, 6, 1);

		matrix.set(0, 7, 0);
		matrix.set(1, 7, 0);
		matrix.set(2, 7, 1);
		matrix.set(3, 7, 0);
		matrix.set(4, 7, 0);
		matrix.set(5, 7, 1);
		matrix.set(6, 7, 0);
		matrix.set(7, 7, 1);
		matrix.set(8, 7, 0);

		matrix.set(0, 8, 1);
		matrix.set(1, 8, 0);
		matrix.set(2, 8, 0);
		matrix.set(3, 8, 0);
		matrix.set(4, 8, 0);
		matrix.set(5, 8, 0);
		matrix.set(6, 8, 0);
		matrix.set(7, 8, 1);
		matrix.set(8, 8, 1);

		ArrayList<Cell> cells = matrix2cellList(matrix);

		int[] rows = new int[cells.size()];
		int[] cols = new int[cells.size()];
		float[] values = new float[cells.size()];

		cellList2Arrays(cells, rows, cols, values);

		MatrixProcessor.setData(pointerMatrix, values);

		long pointerResultMatrix = MatrixProcessor.createMatrixData(v, v, false);

		MatrixProcessor.transitiveClosure(v, pointerMatrix, pointerResultMatrix);

		Cell[] response = MatrixProcessor.getData(pointerResultMatrix, rows, cols);

		assertNotNull(response);
		assertTrue(response.length == v * v);

		CRSMatrix resp = toCRSMatrix(response, v, v);
		assertTrue(resp.get(0, 0) == 1.0);
		assertTrue(resp.get(0, 1) == 0.0);
		assertTrue(resp.get(0, 2) == 1.0);
		assertTrue(resp.get(0, 3) == 0.0);
		assertTrue(resp.get(0, 4) == 0.5);
		assertTrue(resp.get(0, 5) == 0.0);
		assertTrue(resp.get(0, 6) == 0.5);
		assertTrue(resp.get(0, 7) == 0.5);
		assertTrue(resp.get(0, 8) == 1.0);

		assertTrue(new DecimalFormat("#.#").format(resp.get(1, 0)).equals("0,2"));
		assertTrue(resp.get(1, 1) == 1.0);
		assertTrue(new DecimalFormat("#.###").format(resp.get(1, 2)).equals("0,167"));
		assertTrue(resp.get(1, 3) == 0.0);
		assertTrue(new DecimalFormat("#.###").format(resp.get(1, 4)).equals("0,143"));
		assertTrue(resp.get(1, 5) == 1.0);
		assertTrue(resp.get(1, 6) == 0.25);
		assertTrue(resp.get(1, 7) == 0.5);
		assertTrue(new DecimalFormat("#.###").format(resp.get(1, 8)).equals("0,333"));

		assertTrue(new DecimalFormat("#.##").format(resp.get(2, 0)).equals("0,25"));
		assertTrue(resp.get(2, 1) == 0.0);
		assertTrue(resp.get(2, 2) == 1.0);
		assertTrue(resp.get(2, 3) == 0.0);
		assertTrue(resp.get(2, 4) == 1.0);
		assertTrue(resp.get(2, 5) == 0.0);
		assertTrue(new DecimalFormat("#.###").format(resp.get(2, 6)).equals("0,333"));
		assertTrue(resp.get(2, 7) == 1.00);
		assertTrue(resp.get(2, 8) == 0.5);

		assertTrue(new DecimalFormat("#.###").format(resp.get(3, 0)).equals("0,167"));
		assertTrue(resp.get(3, 1) == 1.0);
		assertTrue(new DecimalFormat("#.###").format(resp.get(3, 2)).equals("0,143"));
		assertTrue(resp.get(3, 3) == 1.0);
		assertTrue(new DecimalFormat("#.###").format(resp.get(3, 4)).equals("0,125"));
		assertTrue(resp.get(3, 5) == 0.5);
		assertTrue(new DecimalFormat("#.#").format(resp.get(3, 6)).equals("0,2"));
		assertTrue(new DecimalFormat("#.###").format(resp.get(3, 7)).equals("0,333"));
		assertTrue(new DecimalFormat("#.##").format(resp.get(3, 8)).equals("0,25"));

		assertTrue(resp.get(4, 0) == 0.0);
		assertTrue(resp.get(4, 1) == 0.0);
		assertTrue(resp.get(4, 2) == 0.0);
		assertTrue(resp.get(4, 3) == 0.0);
		assertTrue(resp.get(4, 4) == 1.0);
		assertTrue(resp.get(4, 5) == 0.0);
		assertTrue(resp.get(4, 6) == 0.0);
		assertTrue(resp.get(4, 7) == 0.0);
		assertTrue(resp.get(4, 8) == 0.0);

		assertTrue(new DecimalFormat("#.##").format(resp.get(5, 0)).equals("0,25"));
		assertTrue(resp.get(5, 1) == 0.0);
		assertTrue(new DecimalFormat("#.#").format(resp.get(5, 2)).equals("0,2"));
		assertTrue(resp.get(5, 3) == 0.0);
		assertTrue(new DecimalFormat("#.###").format(resp.get(5, 4)).equals("0,167"));
		assertTrue(resp.get(5, 5) == 1.0);
		assertTrue(new DecimalFormat("#.###").format(resp.get(5, 6)).equals("0,333"));
		assertTrue(resp.get(5, 7) == 1.0);
		assertTrue(resp.get(5, 8) == 0.5);

		assertTrue(resp.get(6, 0) == 1.0);
		assertTrue(resp.get(6, 1) == 0.0);
		assertTrue(resp.get(6, 2) == 0.5);
		assertTrue(resp.get(6, 3) == 0.0);
		assertTrue(new DecimalFormat("#.###").format(resp.get(6, 4)).equals("0,333"));
		assertTrue(resp.get(6, 5) == 0.0);
		assertTrue(resp.get(6, 6) == 1.0);
		assertTrue(new DecimalFormat("#.###").format(resp.get(6, 7)).equals("0,333"));
		assertTrue(resp.get(6, 8) == 0.5);

		assertTrue(new DecimalFormat("#.###").format(resp.get(7, 0)).equals("0,333"));
		assertTrue(resp.get(7, 1) == 0.0);
		assertTrue(new DecimalFormat("#.##").format(resp.get(7, 2)).equals("0,25"));
		assertTrue(resp.get(7, 3) == 0.0);
		assertTrue(new DecimalFormat("#.#").format(resp.get(7, 4)).equals("0,2"));
		assertTrue(resp.get(7, 5) == 0.0);
		assertTrue(resp.get(7, 6) == 0.5);
		assertTrue(resp.get(7, 7) == 1.0);
		assertTrue(resp.get(7, 8) == 1.0);

		assertTrue(resp.get(8, 0) == 0.5);
		assertTrue(resp.get(8, 1) == 0.0);
		assertTrue(new DecimalFormat("#.###").format(resp.get(8, 2)).equals("0,333"));
		assertTrue(resp.get(8, 3) == 0.0);
		assertTrue(new DecimalFormat("#.##").format(resp.get(8, 4)).equals("0,25"));
		assertTrue(resp.get(8, 5) == 0.0);
		assertTrue(resp.get(8, 6) == 1.0);
		assertTrue(new DecimalFormat("#.##").format(resp.get(8, 7)).equals("0,25"));
		assertTrue(resp.get(8, 8) == 1.0);

	}

	@Test
	void transitiveClosureTest2() {
		int v = 5;
		long pointerMatrix = MatrixProcessor.createMatrixData(v, v, false);
		CRSMatrix matrix = new CRSMatrix(v, v);
		matrix.set(0, 0, 1);
		matrix.set(1, 0, 0);
		matrix.set(2, 0, 1);
		matrix.set(3, 0, 0);
		matrix.set(4, 0, 0);

		matrix.set(0, 0, 0);
		matrix.set(1, 1, 1);
		matrix.set(2, 1, 0);
		matrix.set(3, 1, 1);
		matrix.set(4, 1, 0);

		matrix.set(0, 2, 1);
		matrix.set(1, 2, 1);
		matrix.set(2, 2, 1);
		matrix.set(3, 2, 0);
		matrix.set(4, 2, 0);

		matrix.set(0, 3, 1);
		matrix.set(1, 3, 0);
		matrix.set(2, 3, 0);
		matrix.set(3, 3, 1);
		matrix.set(4, 3, 0);

		matrix.set(0, 4, 0);
		matrix.set(1, 4, 0);
		matrix.set(2, 4, 1);
		matrix.set(3, 4, 1);
		matrix.set(4, 4, 1);

		ArrayList<Cell> cells = matrix2cellList(matrix);

		int[] rows = new int[cells.size()];
		int[] cols = new int[cells.size()];
		float[] values = new float[cells.size()];

		cellList2Arrays(cells, rows, cols, values);

		MatrixProcessor.setData(pointerMatrix, values);

		long pointerResultMatrix = MatrixProcessor.createMatrixData(v, v, false);

		MatrixProcessor.transitiveClosure(v, pointerMatrix, pointerResultMatrix);

		Cell[] response = MatrixProcessor.getData(pointerResultMatrix, rows, cols);

		assertNotNull(response);
		assertTrue(response.length == v * v);

		CRSMatrix resp = toCRSMatrix(response, v, v);
		assertTrue(resp.get(0, 0) == 1.0);
		assertTrue(resp.get(0, 1) == 0.5);
		assertTrue(resp.get(0, 2) == 1.0);
		assertTrue(resp.get(0, 3) == 1.0);
		assertTrue(resp.get(0, 4) == 0.5);

		assertTrue(resp.get(1, 0) == 0.5);
		assertTrue(resp.get(1, 1) == 1.0);
		assertTrue(resp.get(1, 2) == 1.0);
		assertTrue(new DecimalFormat("#.###").format(resp.get(1, 3)).equals("0,333"));
		assertTrue(resp.get(1, 4) == 0.5);

		assertTrue(resp.get(2, 0) == 1.0);
		assertTrue(new DecimalFormat("#.###").format(resp.get(2, 1)).equals("0,333"));
		assertTrue(resp.get(2, 2) == 1.0);
		assertTrue(resp.get(2, 3) == 0.5);
		assertTrue(resp.get(2, 4) == 1.0);

		assertTrue(new DecimalFormat("#.###").format(resp.get(3, 0)).equals("0,333"));
		assertTrue(resp.get(3, 1) == 1.0);
		assertTrue(resp.get(3, 2) == 0.5);
		assertTrue(resp.get(3, 3) == 1.0);
		assertTrue(resp.get(3, 4) == 1.0);

		assertTrue(resp.get(4, 0) == 0.0);
		assertTrue(resp.get(4, 1) == 0.0);
		assertTrue(resp.get(4, 2) == 0.0);
		assertTrue(resp.get(4, 3) == 0.0);
		assertTrue(resp.get(4, 4) == 1.0);

	}
	
	@Test
	void getDeviceCountTest() {
		assertTrue(MatrixProcessor.getDeviceCount() == 1);
	}

	private void cellList2Arrays(ArrayList<Cell> cells, int[] rows, int[] cols, float[] values) {
		for (int i = 0; i < cells.size(); i++) {
			Cell cell = cells.get(i);
			rows[i] = cell.row;
			cols[i] = cell.col;
			values[i] = cell.value;
		}
	}

	private ArrayList<Cell> matrix2cellList(CRSMatrix matrix) {
		ArrayList<Cell> cells = new ArrayList<>();
		for (int i = 0; i < matrix.rows(); i++) {
			for (int j = 0; j < matrix.columns(); j++) {
				cells.add(new Cell(i, j, (float) matrix.get(i, j)));
			}
		}
		return cells;
	}

	private CRSMatrix toCRSMatrix(Cell[] response, int r, int c) {
		CRSMatrix resp = new CRSMatrix(r, c);
		for (int i = 0; i < response.length; i++) {
			resp.set(response[i].row, response[i].col, response[i].value);
		}
		return resp;
	}

}
