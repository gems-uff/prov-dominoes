package command;

import java.io.IOException;
import java.util.List;

import domain.Dominoes;

import boundary.App;
import control.Controller;
import convertion.ProvMatrixFactory;
import domain.Configuration;
import convertion.ProvMatrixDefaultFactory;
import convertion.ProvMatrixExtendedFactory;
import model.ProvMatrix;
import util.Prov2DominoesUtil;

public class LoadCommand extends AbstractCommand {

	private String[] filePaths;
	private String id;
	private String dir;

	public LoadCommand(String[] filePaths) {
		super();
		this.filePaths = filePaths;
	}

	public LoadCommand(String[] filePaths, String dir) {
		this(filePaths);
		this.dir = dir;
	}

	public LoadCommand() {
		super();
		this.filePaths = new String[1];
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
			ProvMatrixFactory provFactory =null;
			if (Configuration.defaultFactory) {
				provFactory = new ProvMatrixDefaultFactory(filePaths, dir);
			} else {
				provFactory = new ProvMatrixExtendedFactory(filePaths, dir);
			}
			List<ProvMatrix> provMatrixList = provFactory.buildMatrices();
			List<Dominoes> dominoesList = Prov2DominoesUtil.convert(provMatrixList);
			App.getPieceSelectorList().clear();
			App.getMovementCanvas().clear();
			App.getTabbedMatrixGraphPane().clear();
			Controller.resultLoadMatrices = dominoesList;
			App.getPieceSelectorList().Configure(Controller.resultLoadMatrices);
		} catch (IOException e) {
			result = false;
			App.alertException(e, e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			result = false;
			App.alertException(e, e.getMessage());
		}
		return result;
	}

	@Override
	protected boolean undoIt() {
		boolean result = true;
		App.getPieceSelectorList().clear();
		App.getMovementCanvas().clear();
		App.getTabbedMatrixGraphPane().clear();
		App.getArea().clear();
		return result;
	}

	@Override
	public String getName() {
		String files = "";
		for (int i = 0; i < filePaths.length; i++) {
			files += filePaths[i] + ",";
		}
		if (filePaths.length > 0) {
			files = files.substring(0, files.length() - 1);
		}
		return "LOAD(\"" + files + "\")";
	}

}