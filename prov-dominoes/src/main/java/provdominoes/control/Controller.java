package provdominoes.control;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.swing.JFrame;

import javafx.embed.swing.JFXPanel;
import javafx.scene.Scene;
import provdominoes.command.TextFilterData;
import provdominoes.domain.Configuration;
import provdominoes.domain.Dominoes;
import provdominoes.util.ConfigurationFile;

public class Controller {

	public static String[] args = null;

	public static final String MESSAGE_STARTED = "started";
	public static final String MESSAGE_FINISHED = "finished";

	public static final int amout_tiles = 6;

	public static String message = "";

	public static List<Dominoes> resultLoadMatrices;

	public static int indexTileSelected = -1;

	/**
	 * This function is used to initialize the Configuration class
	 * 
	 * @throws IOException
	 * @throws Exception
	 */
	public static void loadConfiguration() throws IOException, Exception {
		new Configuration();
		new ConfigurationFile().loadConfigurationFile();
	}

	/**
	 * This function has begin when the user want using a matrix in Dominoes
	 * database.
	 *
	 * @param dominoes the row and the column this dominoes will be used in the
	 *                 search to load
	 * @return The domino, in database, which contains the row and the column equal
	 *         to row and column of the domino passed how parameters to this
	 *         function
	 * @throws IOException
	 */
	public static Dominoes loadMatrix(Dominoes dominoes) throws IOException {
		/*
		 * DominoesDao result = DaoFactory.getDominoesDao(Configuration.accessMode); if
		 * (result == null) { throw new
		 * IllegalArgumentException("Invalid argument.\nAccess mode not defined"); }
		 * return result.loadMatrix(dominoes);
		 */

		return null;
	}

	/**
	 * This function has begin when the user want to multiply two matrices.
	 *
	 * @param dom1 First operator in multiplication
	 * @param dom2 Second operator in multiplication
	 * @return The result of multiplication
	 * @throws Exception
	 */
	public static Dominoes multiply(Dominoes dom1, Dominoes dom2) throws Exception {
		// call dominoes
		Dominoes result = dom1.multiply(dom2);
		return result;
	}

	/**
	 * This functions is called when user want remove a matrix of database
	 * 
	 * @param dominoes The dominoes corresponding to matrix
	 * @return True, in affirmative case
	 * @throws IOException
	 */
	public static boolean removeMatrix(Dominoes dominoes) throws IOException {
		/*
		 * DominoesDao result = DaoFactory.getDominoesDao(Configuration.accessMode);
		 * 
		 * if (result == null) { throw new
		 * IllegalArgumentException("Invalid argument.\nAccess mode not defined"); }
		 * 
		 * return result.removeMatrix(dominoes);
		 */
		return true;
	}

	/**
	 * This function has begin when the user want to save a matrix
	 *
	 * @param dominoes information to be saved
	 * @return true, in case afirmative.
	 * @throws IOException
	 */
	public static boolean saveMatrix(Dominoes dominoes) throws IOException {
		/*
		 * DominoesDao result = DaoFactory.getDominoesDao(Configuration.accessMode); if
		 * (result == null) { throw new
		 * IllegalArgumentException("Invalid argument.\nAccess mode not defined"); }
		 * return result.saveMatrix(dominoes);
		 */
		return true;
	}

	/**
	 * This function has begin when the user want to transpose a matrix.
	 * 
	 * @param domino Matrix to be transposed
	 * @return Return the transpose of the matrix in the parameter
	 * @throws Exception
	 */
	public static Dominoes tranposeDominoes(Dominoes domino) throws Exception {

		domino.transpose();

		return domino;

	}

	/**
	 * This function has begin when the user want calculate the confidence
	 * 
	 * @param support domino Matrix to be calculated
	 * @return Return the confidence of the support matrix
	 * @throws Exception
	 */
	public static Dominoes confidence(Dominoes domino) throws Exception {

		domino.confidence();

		return domino;

	}

	/**
	 * This function has begin when the user want calculate the transitive closure
	 * 
	 * @param transitive closure domino Matrix to be calculated
	 * @return Return the transitive closure
	 * @throws Exception
	 */
	public static Dominoes transitiveClosure(Dominoes domino) throws Exception {
		domino.transitiveClosure();
		return domino;
	}

	public static Dominoes binarize(Dominoes domino) throws Exception {
		domino.binarize();
		return domino;
	}

	public static Dominoes invert(Dominoes domino) throws Exception {
		domino.invert();
		return domino;
	}
	
	public static Dominoes sortRows(Dominoes domino) throws Exception {
		domino.sortRows();
		return domino;
	}
	
	public static Dominoes sortCols(Dominoes domino) throws Exception {
		domino.sortCols();
		return domino;
	}

	public static Dominoes percent(Dominoes domino, double d) throws Exception {
		domino.percent(d);
		return domino;
	}
	
	public static Dominoes filterColumnText(Dominoes domino, TextFilterData t) throws Exception {
		domino.filterColumnText(t);
		return domino;
	}
	
	public static Dominoes filterRowText(Dominoes domino, TextFilterData t) throws Exception {
		domino.filterRowText(t);
		return domino;
	}

	public static Dominoes diagonalize(Dominoes domino) throws Exception {
		domino.diagonalize();
		return domino;
	}

	public static Dominoes upperDiagonal(Dominoes domino) throws Exception {
		domino.upperDiagonal();
		return domino;
	}

	public static Dominoes lowerDiagonal(Dominoes domino) throws Exception {
		domino.lowerDiagonal();
		return domino;
	}

	public static Dominoes trim(Dominoes domino) throws Exception {
		domino.trim();
		return domino;
	}

	/**
	 * This function has begin when the user want calculate the Statandard Score
	 * 
	 * @param support domino Matrix to be calculated
	 * @return Return the confidence of the support matrix
	 * @throws Exception
	 */
	public static Dominoes standardScore(Dominoes domino) throws Exception {

		domino.standardScore();

		return domino;

	}

	/**
	 * This function has begin when the user want to reduce a matrix.
	 * 
	 * @param domino Matrix to be reduced
	 * @return Return the reduced matrix in the parameter
	 * @throws Exception
	 */
	public static Dominoes reduceDominoes(Dominoes domino) throws Exception {

		if (domino.reduceRows()) {
			return domino;
		}
		return null;

	}

	public static double opposite(double size, double index) {
		if (size < 0 || index < 0 || index > size) {
			throw new IllegalArgumentException(
					"Invalid parameter." + "\nController.opposite(...) parameter is invalid");
		}
		double result = Math.abs(index - size);
		return result;
	}

	public static String changeFormat(SimpleDateFormat source, SimpleDateFormat target, String format)
			throws ParseException {
		Date date = source.parse(format);
		String result = target.format(date);
		return result;
	}

	public static JFrame FXToJFrame(Scene scene) {
		JFrame jFrame = new JFrame();
		JFXPanel panel = new JFXPanel();
		panel.setScene(scene);
		jFrame.add(panel);

		return jFrame;
	}

	public static void printPrompt(String string) {
		Controller.message = string;
		System.out.println(string);

	}

}
