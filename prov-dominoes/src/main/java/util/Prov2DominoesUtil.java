package util;

import java.util.ArrayList;
import java.util.List;

import org.la4j.matrix.sparse.CRSMatrix;

import arch.MatrixDescriptor;
import arch.MatrixOperations;
import arch.MatrixOperationsFactory;
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
				MatrixOperations mat = MatrixOperationsFactory.getMatrix2D(Configuration.CPU_DEVICE, descriptor, true);
				mat.setData(matrix2Cells(provMatrix.getMatrix()));
				// System.out.println(mat);
				Dominoes dom = new Dominoes(provMatrix.getRowDimentionAbbreviate(),
						provMatrix.getColumnDimentionAbbreviate(), provMatrix.getRelation(), mat,
						Configuration.CPU_DEVICE);

				dominoesList.add(dom);
			}

		}
		return dominoesList;
	}

	private static ArrayList<Cell> matrix2Cells(CRSMatrix matrix) {
		ArrayList<Cell> cells = new ArrayList<>();
		for (int i = 0; i < matrix.rows(); i++) {
			for (int j = 0; j < matrix.columns(); j++) {
				cells.add(new Cell(i, j, (float) matrix.get(i, j)));
			}
		}
		return cells;
	}

}
