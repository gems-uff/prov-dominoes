package provdominoes.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
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

	public static List<Dominoes> convert(List<ProvMatrix> matrices, Map<String, String> dimensionLabels,
			HashMap<String, String> cellParams) throws Exception {
		List<Dominoes> dominoesList = new ArrayList<>();
		for (ProvMatrix provMatrix : matrices) {
			if (provMatrix != null && (provMatrix.getRelation() != null || provMatrix.getIdentifier() != null)
					&& !provMatrix.getRowDescriptors().isEmpty() && !provMatrix.getColumnDescriptors().isEmpty()
					&& !provMatrix.isEmpty()) {
				MatrixDescriptor descriptor = new MatrixDescriptor(provMatrix.getRowDimentionAbbreviate(),
						provMatrix.getColumnDimentionAbbreviate());
				descriptor.setRowsDesc(toCaptions(provMatrix.getRowDescriptors(), dimensionLabels, true));
				descriptor.setColumnsDesc(toCaptions(provMatrix.getColumnDescriptors(), dimensionLabels, true));
				if (dimensionLabels != null && !dimensionLabels.keySet().isEmpty()) {
					descriptor.setRowsTooltips(toCaptions(provMatrix.getRowDescriptors(), dimensionLabels, false));
					descriptor.setColumnsTooltips(toCaptions(provMatrix.getColumnDescriptors(), dimensionLabels, false));
				}
				Dominoes dom = new Dominoes(provMatrix, descriptor,
						(Configuration.isGPUProcessing() ? Configuration.GPU_PROCESSING
								: Configuration.CPU_PROCESSING));
				dom.setCellParams(cellParams);
				if (provMatrix.getIdentifier() != null) {
					dom.setId(provMatrix.getIdentifier());
				}
				dominoesList.add(dom);
			}

		}
		return dominoesList;
	}

	private static List<String> toCaptions(List<String> descriptors, Map<String, String> labels, boolean isLabel) {
		List<String> result = new ArrayList<>();
		for (String desc : descriptors) {
			if (labels!=null && labels.containsKey(desc)) {
				String token = labels.get(desc);
				if (isLabel) {
					if (!token.split(",")[0].isEmpty()) {
						result.add(token.split(",")[0]);
					} else {
						result.add(desc);
					}
				} else {
					if (token.contains(",") && !token.endsWith(",")) {
						result.add(token.split(",")[1]);
					} else {
						result.add(desc);
					}
				}
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

	public static CRSMatrix cells2Matrix(List<Cell> cells, int rows, int cols) {
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

	public static String[][] cloneStringMatrix(String[][] stringMatrix) {
		String[][] clonedStringMatrix = null;
		if (stringMatrix != null) {
			clonedStringMatrix = new String[stringMatrix.length][stringMatrix[0].length];
			for (int i = 0; i < clonedStringMatrix.length; i++) {
				clonedStringMatrix[i] = Arrays.copyOf(stringMatrix[i], stringMatrix[i].length);
			}
		}
		return clonedStringMatrix;
	}

	public static List<String> sortWithLabels(double[] numbers, List<String> labels) {
		quickSort(numbers, 0, numbers.length - 1, labels);
		return labels;
	}

	public static void quickSort(double arr[], int begin, int end, List<String> newLabels) {
		if (begin < end) {
			int partitionIndex = partition(arr, begin, end, newLabels);

			quickSort(arr, begin, partitionIndex - 1, newLabels);
			quickSort(arr, partitionIndex + 1, end, newLabels);
		}
	}

	private static int partition(double arr[], int begin, int end, List<String> newLabels) {
		double pivot = arr[end];
		int i = (begin - 1);

		for (int j = begin; j < end; j++) {
			if (arr[j] > pivot) {
				i++;

				double swapTemp = arr[i];
				String swapLabel = newLabels.get(i);
				arr[i] = arr[j];
				newLabels.set(i, newLabels.get(j));
				arr[j] = swapTemp;
				newLabels.set(j, swapLabel);
			}
		}

		double swapTemp = arr[i + 1];
		String swapLabel = newLabels.get(i + 1);
		arr[i + 1] = arr[end];
		newLabels.set(i + 1, newLabels.get(end));
		arr[end] = swapTemp;
		newLabels.set(end, swapLabel);

		return i + 1;
	}
}
