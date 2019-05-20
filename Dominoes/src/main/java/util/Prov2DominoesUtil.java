package util;

import java.util.ArrayList;
import java.util.List;

import org.la4j.matrix.sparse.CRSMatrix;

import com.josericardojunior.arch.Cell;
import com.josericardojunior.arch.MatrixOperations;
import com.josericardojunior.arch.MatrixOperationsFactory;
import com.josericardojunior.arch.MatrixDescriptor;
import com.josericardojunior.domain.Dominoes;

import domain.Configuration;
import model.ProvMatrix;

public class Prov2DominoesUtil {

	public static List<Dominoes> convert(List<ProvMatrix> matrices) throws Exception {
		List<Dominoes> dominoesList = new ArrayList<>();
		for (ProvMatrix provMatrix : matrices) {
			MatrixDescriptor descriptor = new MatrixDescriptor(provMatrix.getRowDimentionAbbreviate(),
					provMatrix.getColumnDimentionAbbreviate());
			System.out.println("Convertendo: " + provMatrix.getRowDimentionAbbreviate() + " | "
					+ provMatrix.getColumnDimentionAbbreviate() + " : " + provMatrix.getRelation().getDescription()
					+ " ...");
			System.out.println(provMatrix.getRowDescriptors());
			System.out.println(provMatrix.getColumnDescriptors());
			descriptor.setRowsDesc(provMatrix.getRowDescriptors());
			descriptor.setColumnsDesc(provMatrix.getColumnDescriptors());
			MatrixOperations mat = MatrixOperationsFactory.getMatrix2D(Configuration.CPU_DEVICE, descriptor);
			mat.setData(matrix2Cells(provMatrix.getMatrix()));
			System.out.println(mat);
			Dominoes dom = new Dominoes(provMatrix.getRowDimentionAbbreviate(),
					provMatrix.getColumnDimentionAbbreviate(), provMatrix.getRelation(), mat,
					Configuration.CPU_DEVICE);

			dominoesList.add(dom);

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
		System.out.println(cells);
		return cells;
	}

}
