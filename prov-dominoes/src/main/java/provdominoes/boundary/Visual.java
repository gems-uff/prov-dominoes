package provdominoes.boundary;

import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;
import provdominoes.domain.Dominoes;

public class Visual extends BorderPane {

	private double padding = 800;
	private TabPane tabPane;

	public Visual() {
		tabPane = new TabPane();

		this.setCenter(tabPane);

	}

	/**
	 * This Functions is used to define the moving area size
	 *
	 * @param width
	 * @param height
	 */
	public void setSize(double width, double height) {
		this.setMinWidth(width);
		this.setPrefWidth(width);
		this.setMaxWidth(width + padding);
		this.setPrefHeight(height);
	}

	public void addTabGraph(Dominoes domino) {
		Tab tab = new Tab(
				"Graph: " + domino.getIdRow() + "x" + domino.getIdCol() + " " + this.tabPane.getTabs().size());
		GraphPane graphPane = new GraphPane(domino);

		tab.setContent(graphPane);
		Tooltip.install(tab.getGraphic(), new Tooltip(domino.getHistoric().toString()));

		this.tabPane.getTabs().add(tab);
	}

	public void addTabCentralityGraph(Dominoes domino) {
		if (domino.getCrsMatrix().rows() == domino.getCrsMatrix().columns()) {
			Tab tab = new Tab("Centrality Graph: " + domino.getIdRow() + "x" + domino.getIdCol() + " "
					+ this.tabPane.getTabs().size());
			GraphCentralityPane graphCentralityPane = new GraphCentralityPane(domino);

			tab.setContent(graphCentralityPane);
			Tooltip.install(tab.getGraphic(), new Tooltip(domino.getHistoric().toString()));

			this.tabPane.getTabs().add(tab);
		} else {
			App.alert(AlertType.WARNING, "Piece Square Requirement", "Square Piece Required!",
					"This visualization is only possible for square pieces (same dimensions)!");
		}
	}

	void addTabMatrix(Dominoes domino) {
		try {
			Tab tab = new Tab(domino.getIdRow() + "(" + domino.getDescriptor().getNumRows() + ")" + "x"
					+ domino.getIdCol() + "(" + domino.getDescriptor().getNumCols() + ")" + " " + domino.getHistoric()
					+ appendType(domino));
			//domino.setupOperation(false);
			MatrixPane graphPane = new MatrixPane(domino);

			tab.setContent(graphPane);
			Tooltip.install(tab.getGraphic(), new Tooltip(domino.getHistoric().toString()));

			this.tabPane.getTabs().add(tab);
			this.tabPane.getSelectionModel().selectLast();
		} catch (Exception e) {
			App.alertException(e, "Error trying to load provenance piece into the Piece Canvas!");
			e.printStackTrace();
		}
	}

	private String appendType(Dominoes d) {
		String type = "";
		if (!d.getTextType().getText().equals(Dominoes.TYPE_BASIC_CODE)) {
			type = ", " + d.getTextType().getText();
		}
		return type;
	}

	void addTabChart(Dominoes domino) {
		Tab tab = new Tab(
				"Chart: " + domino.getIdRow() + "x" + domino.getIdCol() + " " + this.tabPane.getTabs().size());
		ChartPane graphPane = new ChartPane(domino);

		tab.setContent(graphPane);
		Tooltip.install(tab.getGraphic(), new Tooltip(domino.getHistoric().toString()));

		this.tabPane.getTabs().add(tab);
		this.tabPane.getSelectionModel().selectLast();

	}

	void addTabLineChart(Dominoes domino) {
		Tab tab = new Tab(
				"Line Chart: " + domino.getIdRow() + "x" + domino.getIdCol() + " " + this.tabPane.getTabs().size());
		LineChartPane graphPane = new LineChartPane(domino);

		tab.setContent(graphPane);
		Tooltip.install(tab.getGraphic(), new Tooltip(domino.getHistoric().toString()));

		this.tabPane.getTabs().add(tab);
		this.tabPane.getSelectionModel().selectLast();

	}

	public void clear() {
		this.tabPane.getTabs().clear();
	}

}
