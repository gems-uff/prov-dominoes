package boundary;

import domain.Dominoes;

import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.BorderPane;

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
    
    public void addTabGraph(Dominoes domino){
        Tab tab = new Tab("Graph: " + domino.getIdRow() + "x" + domino.getIdCol() + " " + this.tabPane.getTabs().size());
        GraphPane graphPane = new GraphPane(domino);
        
        tab.setContent(graphPane);
        Tooltip.install(tab.getGraphic(), new Tooltip(domino.getHistoric().toString()));
        
        this.tabPane.getTabs().add(tab);       
    }
    
    public void addTabCentralityGraph(Dominoes domino){
        Tab tab = new Tab("Centrality Graph: " + domino.getIdRow() + "x" + domino.getIdCol() + " " + this.tabPane.getTabs().size());
        GraphCentralityPane graphCentralityPane = new GraphCentralityPane(domino);
        
        tab.setContent(graphCentralityPane);
        Tooltip.install(tab.getGraphic(), new Tooltip(domino.getHistoric().toString()));
        
        this.tabPane.getTabs().add(tab);       
    }

    void addTabMatrix(Dominoes domino) {
        try {
			Tab tab = new Tab("Matrix: " + domino.getIdRow() + "x" + domino.getIdCol() + " " + this.tabPane.getTabs().size());
			domino.setupOperation(true);
			MatrixPane graphPane = new MatrixPane(domino);
			
			tab.setContent(graphPane);
			Tooltip.install(tab.getGraphic(), new Tooltip(domino.getHistoric().toString()));

			this.tabPane.getTabs().add(tab);
			this.tabPane.getSelectionModel().selectLast();
		} catch (Exception e) {
			App.alertException(e, "Erro desconhecido ao tentar carregar peça no canvas!");
			e.printStackTrace();
		}
    }
    
    void addTabChart(Dominoes domino) {
        Tab tab = new Tab("Chart: " + domino.getIdRow() + "x" + domino.getIdCol() + " " + this.tabPane.getTabs().size());
        ChartPane graphPane = new ChartPane(domino);
        
        tab.setContent(graphPane);
        Tooltip.install(tab.getGraphic(), new Tooltip(domino.getHistoric().toString()));

        this.tabPane.getTabs().add(tab);
        this.tabPane.getSelectionModel().selectLast();
        
    }
    
    void addTabLineChart(Dominoes domino) {
        Tab tab = new Tab("Line Chart: " + domino.getIdRow() + "x" + domino.getIdCol() + " " + this.tabPane.getTabs().size());
        LineChartPane graphPane = new LineChartPane(domino);
        
        tab.setContent(graphPane);
        Tooltip.install(tab.getGraphic(), new Tooltip(domino.getHistoric().toString()));

        this.tabPane.getTabs().add(tab);
        this.tabPane.getSelectionModel().selectLast();
        
    }
    
    public void clear(){
    	this.tabPane.getTabs().clear();
    }
    
}
