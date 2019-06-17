package command;

import java.io.IOException;
import java.util.List;

import com.josericardojunior.domain.Dominoes;

import boundary.App;
import control.Controller;
import convertion.ProvMatrixFactory;
import model.ProvMatrix;
import util.Prov2DominoesUtil;

public class LoadCommand extends AbstractCommand {

	private String provFilePath;
	private String id;
	private String dir;

	public LoadCommand(String provFilePath) {
		super();
		this.provFilePath = provFilePath;
	}

	public LoadCommand(String provFilePath, String dir) {
		this(provFilePath);
		this.dir = dir;
	}

	public LoadCommand() {
		super();
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	@Override
	protected boolean doIt() {
		boolean result = true;
		try {
			String path = provFilePath;
			if (provFilePath != null && !provFilePath.contains(":\\")) {
				path = dir + provFilePath;
			}
			ProvMatrixFactory provFactory = new ProvMatrixFactory(path);
			List<ProvMatrix> provMatrixList = provFactory.buildMatrices();
			List<Dominoes> dominoesList = Prov2DominoesUtil.convert(provMatrixList);
			App.getPieceSelectorList().clear();
			App.getMovementCanvas().clear();
			App.getTabbedMatrixGraphPane().clear();
			Controller.resultLoadMatrices = dominoesList;
			App.getPieceSelectorList().Configure(Controller.resultLoadMatrices);
		} catch (IOException e) {
			result = false;
			e.printStackTrace();
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}
		return result;
	}

	@Override
	protected boolean undoIt() {
		boolean result = true;
		// App.getCommandManager().getRedoList().clear();
		// App.getCommandManager().getHistory().clear();
		// App.getCommandManager().uptadeMenu();
		// try {
		// App.getCommandManager().clear(true);
		App.getPieceSelectorList().clear();
		App.getMovementCanvas().clear();
		App.getTabbedMatrixGraphPane().clear();
		// } catch (IOException e) {
		// System.out.println("Erro ao acessar script de comandos");
		// e.printStackTrace();
		// }
		App.getArea().clear();
		return result;
	}

	@Override
	public String getName() {
		return "LOAD(\"" + provFilePath + "\")";
	}

}