package provdominoes.util;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.la4j.matrix.functor.MatrixProcedure;
import org.la4j.matrix.sparse.CRSMatrix;

import model.ProvMatrix;
import processor.Cell;
import provdominoes.arch.MatrixDescriptor;
import provdominoes.domain.Configuration;
import provdominoes.domain.Dominoes;

public class Prov2DominoesUtil {

	public static List<Dominoes> convert(List<ProvMatrix> matrices, Map<String, String> labels) throws Exception {
		List<Dominoes> dominoesList = new ArrayList<>();
		for (ProvMatrix provMatrix : matrices) {
			if (provMatrix != null && (provMatrix.getRelation() != null || provMatrix.getIdentifier() != null)
					&& !provMatrix.getRowDescriptors().isEmpty() && !provMatrix.getColumnDescriptors().isEmpty()
					&& !provMatrix.isEmpty()) {
				MatrixDescriptor descriptor = new MatrixDescriptor(provMatrix.getRowDimentionAbbreviate(),
						provMatrix.getColumnDimentionAbbreviate());
				if (provMatrix.getRelation() != null) {
					System.out.println("Convertendo: " + provMatrix.getRowDimentionAbbreviate() + " | "
							+ provMatrix.getColumnDimentionAbbreviate() + " : "
							+ provMatrix.getRelation().getDescription() + " ...");
				} else if (provMatrix.getIdentifier() != null) {
					System.out.println("Convertendo: " + provMatrix.getIdentifier() + " ...");
				}
				if (labels != null && !labels.keySet().isEmpty()) {
					List<String> rows = toLabels(provMatrix.getRowDescriptors(), labels);
					List<String> columns = toLabels(provMatrix.getColumnDescriptors(), labels);
					System.out.println(rows);
					System.out.println(columns);
					descriptor.setRowsDesc(rows);
					descriptor.setColumnsDesc(columns);
				} else {
					System.out.println(provMatrix.getRowDescriptors());
					System.out.println(provMatrix.getColumnDescriptors());
					descriptor.setRowsDesc(provMatrix.getRowDescriptors());
					descriptor.setColumnsDesc(provMatrix.getColumnDescriptors());
				}
				Dominoes dom = new Dominoes(provMatrix, descriptor,
						(Configuration.isGPUProcessing() ? Configuration.GPU_DEVICE : Configuration.CPU_DEVICE));
				if (provMatrix.getIdentifier() != null) {
					dom.setId(provMatrix.getIdentifier());
				}
				dominoesList.add(dom);
			}

		}
		return dominoesList;
	}

	private static List<String> toLabels(List<String> descriptors, Map<String, String> labels) {
		List<String> result = new ArrayList<>();
		for (String desc : descriptors) {
			if (labels.containsKey(desc)) {
				result.add(labels.get(desc));
			} else {
				result.add(desc);
			}
		}
		return result;
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

	public static double getNonZeroMin(CRSMatrix matrix) {
		double mininum = 0;
		if (matrix != null) {
			List<Double> nonzeros = new ArrayList<>();
			matrix.eachNonZero(new MatrixProcedure() {
				@Override
				public void apply(int row, int col, double value) {
					nonzeros.add(value);
				}
			});
			if (nonzeros.size() > 0) {
				mininum = nonzeros.stream().min(new Comparator<Double>() {
					@Override
					public int compare(Double t1, Double t2) {
						if (t1.doubleValue() > t2.doubleValue()) {
							return 1;
						}
						if (t1.doubleValue() < t2.doubleValue()) {
							return -1;
						} else {
							return 0;
						}
					}
				}).get();
			}
		}
		return mininum;
	}

	public static double getNonZeroAverage(CRSMatrix matrix) {
		double avg = 0;
		double sum = 0;
		if (matrix != null) {
			List<Double> nonzeros = new ArrayList<>();
			matrix.eachNonZero(new MatrixProcedure() {
				@Override
				public void apply(int row, int col, double value) {
					nonzeros.add(value);
				}
			});
			sum = nonzeros.stream().reduce(0.00, Double::sum);
			avg = sum / getNonZeroTotal(matrix);
		}
		return Math.floor(avg * 100000) / 100000;
	}

	public static int getNonZeroTotal(CRSMatrix matrix) {
		int total = 0;
		if (matrix != null) {
			List<Double> nonzeros = new ArrayList<>();
			matrix.eachNonZero(new MatrixProcedure() {
				@Override
				public void apply(int row, int col, double value) {
					nonzeros.add(value);
				}
			});
			total = nonzeros.size();
		}
		return total;
	}

	public static double getNonZeroStandardScore(CRSMatrix matrix, double mean) {
		double sdScore = 0.0;
		if (matrix != null) {
			List<Double> sdDiffs = new ArrayList<>();
			matrix.eachNonZero(new MatrixProcedure() {
				@Override
				public void apply(int row, int col, double value) {
					sdDiffs.add((value - mean) * (value - mean));
				}
			});
			double sum = sdDiffs.stream().reduce(0.00, Double::sum);
			sdScore = Math.sqrt(sum / new Double(sdDiffs.size()));
		}
		return Math.floor(sdScore * 100000) / 100000;
	}

}
