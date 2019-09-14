package util;

import java.util.ArrayList;
import java.util.List;

import org.la4j.matrix.sparse.CRSMatrix;

import arch.MatrixDescriptor;
import domain.Configuration;
import domain.Dominoes;
import model.ProvMatrix;
import processor.Cell;

public class Prov2DominoesUtil {

	public static List<Dominoes> convert(List<ProvMatrix> matrices) throws Exception {
		List<Dominoes> dominoesList = new ArrayList<>();
		for (ProvMatrix provMatrix : matrices) {
			if (provMatrix != null && provMatrix.getRelation() != null && !provMatrix.getRowDescriptors().isEmpty()
					&& !provMatrix.getColumnDescriptors().isEmpty()) {
				MatrixDescriptor descriptor = new MatrixDescriptor(provMatrix.getRowDimentionAbbreviate(),
						provMatrix.getColumnDimentionAbbreviate());
				System.out.println("Convertendo: " + provMatrix.getRowDimentionAbbreviate() + " | "
						+ provMatrix.getColumnDimentionAbbreviate() + " : " + provMatrix.getRelation().getDescription()
						+ " ...");
				System.out.println(provMatrix.getRowDescriptors());
				System.out.println(provMatrix.getColumnDescriptors());
				descriptor.setRowsDesc(provMatrix.getRowDescriptors());
				descriptor.setColumnsDesc(provMatrix.getColumnDescriptors());
				Dominoes dom = new Dominoes(provMatrix, descriptor,
						(Configuration.isGPUProcessing() ? Configuration.GPU_DEVICE : Configuration.CPU_DEVICE));
				dominoesList.add(dom);
			}

		}
		return dominoesList;
	}

	public static ArrayList<Cell> matrix2Cells(CRSMatrix matrix) {
		ArrayList<Cell> cells = new ArrayList<>();
		for (int i = 0; i < matrix.rows(); i++) {
			for (int j = 0; j < matrix.columns(); j++) {
				cells.add(new Cell(i, j, (float) matrix.get(i, j)));
			}
		}
		return cells;
	}
	
	public static CRSMatrix cells2Matrix(ArrayList<Cell> cells, int rows, int cols) {
		CRSMatrix matrix = new CRSMatrix(rows, cols);
		for (Cell cell : cells) {
			matrix.set(cell.row, cell.col, cell.value);
		}
		return matrix;
	}

}
