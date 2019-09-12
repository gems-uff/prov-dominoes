package boundary;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import domain.Dominoes;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import processor.Cell;


public class LineChartPane extends Pane {

	private static final String TYPE_ORDER_BY_NORMAL = "Normal";
	private static final String TYPE_ORDER_BY_INCREASING = "Increasing";
	private static final String TYPE_ORDER_BY_DECREASING = "Decreasing";
	
	private static final String SELECT_ALL_ROWS = "All";
	
    private ComboBox<String> cbSelectedRow;
    private ComboBox<String> cbOrderBy;

    private final LineChart<String, Number> lc;

    private double maxZoom = 20;
    private double minZoom = 0.05;

    private double srcSceneX;
    private double srcSceneY;
    private double srcTranslateX;
    private double srcTranslateY;

    
    public LineChartPane(Dominoes domino) {

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();

        xAxis.setLabel(domino.getIdRow());
        yAxis.setLabel(domino.getIdCol());

        lc = new LineChart<String, Number>(xAxis, yAxis);
        lc.setTitle(domino.getHistoric().toString());
        lc.setAnimated(false);
//        bc.set
        
        // change color BarChart

        cbSelectedRow = new ComboBox<>();
        ObservableList<String> itemsCBMatrixRows = FXCollections.observableArrayList();
        itemsCBMatrixRows.add(SELECT_ALL_ROWS);
        for (int i = 0; i < domino.getDescriptor().getNumRows(); i++) {
            itemsCBMatrixRows.add(domino.getDescriptor().getRowAt(i));

        }
        cbSelectedRow.setItems(itemsCBMatrixRows);
        cbSelectedRow.getSelectionModel().select(0);
        cbSelectedRow.toFront();
        
        cbSelectedRow.valueProperty().addListener(new ChangeListener<String>() {
            @SuppressWarnings("rawtypes")
			@Override
            public void changed(ObservableValue ov, String t, String t1) {
                // draw chart
                drawChart(domino);
            }
        });
        
        cbOrderBy = new ComboBox<>();
        ObservableList<String> itemsBCOrderBy = FXCollections.observableArrayList();
        
        itemsBCOrderBy.add(TYPE_ORDER_BY_NORMAL);
        itemsBCOrderBy.add(TYPE_ORDER_BY_INCREASING);
        itemsBCOrderBy.add(TYPE_ORDER_BY_DECREASING);
        
        cbOrderBy.setItems(itemsBCOrderBy);
        cbOrderBy.getSelectionModel().select(0);
        cbOrderBy.toFront();
        
        cbOrderBy.valueProperty().addListener(new ChangeListener<String>() {
            @SuppressWarnings("rawtypes")
			@Override
            public void changed(ObservableValue ov, String t, String t1) {
                // draw chart
                drawChart(domino);
            }
        });

        drawChart(domino);
        
        this.setOnScroll(new EventHandler<ScrollEvent>() {

            @Override
            public void handle(ScrollEvent event) {
                double srcX = event.getX() - lc.getTranslateX() - lc.prefWidth(-1) / 2;
                double srcY = event.getY() - lc.getTranslateY() - lc.prefHeight(-1) / 2;
                double trgX = srcX;
                double trgY = srcY;

                double factor = 0.05;

                if (event.getDeltaY() < 0 && lc.getScaleX() > minZoom) {
                    lc.setScaleX(lc.getScaleX() * (1 - factor));
                    lc.setScaleY(lc.getScaleY() * (1 - factor));
                    trgX = srcX * (1 - factor);
                    trgY = srcY * (1 - factor);
                } else if (event.getDeltaY() > 0 && lc.getScaleX() < maxZoom) {
                    lc.setScaleX(lc.getScaleX() * (1 + factor));
                    lc.setScaleY(lc.getScaleY() * (1 + factor));
                    trgX = srcX * (1 + factor);
                    trgY = srcY * (1 + factor);
                }
                lc.setTranslateX(lc.getTranslateX() - (trgX - srcX));
                lc.setTranslateY(lc.getTranslateY() - (trgY - srcY));

            }
        });
        this.setOnMouseDragged(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                double offsetX = event.getSceneX() - srcSceneX;
                double offsetY = event.getSceneY() - srcSceneY;
                double newTranslateX = srcTranslateX + offsetX;
                double newTranslateY = srcTranslateY + offsetY;

                lc.setTranslateX(newTranslateX);
                lc.setTranslateY(newTranslateY);

            }
        });
        this.setOnMousePressed(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                srcSceneX = event.getSceneX();
                srcSceneY = event.getSceneY();
                srcTranslateX = lc.getTranslateX();
                srcTranslateY = lc.getTranslateY();

                cursorProperty().set(Cursor.CLOSED_HAND);
            }
        });
        this.setOnMouseReleased(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {

                cursorProperty().set(Cursor.OPEN_HAND);
            }
        });
        
        
        // building interface
        
        HBox hBox = new HBox();
    	
    	hBox.setPadding(new Insets(15, 12, 15, 12));
    	hBox.setSpacing(10);
    	hBox.setStyle("-fx-background-color: #66FFFF;");
    	
    	
    	Label lSelectedRow = new Label("Selected Row: ");
    	lSelectedRow.setPrefSize(100,  20);
    	
    	Label lOrderBy = new Label("Order By: ");
    	lOrderBy.setPrefSize(100,  20);
    	
        hBox.setPrefWidth(lc.getPrefWidth());
        hBox.getChildren().addAll(lSelectedRow, cbSelectedRow, lOrderBy, cbOrderBy);
        
        BorderPane border = new BorderPane();
        border.setTop(hBox);
        border.toFront();

        this.getChildren().addAll(lc, border);

    }

	private synchronized void drawChart(Dominoes domino) {
    	
    	int _nCols = domino.getDescriptor().getNumCols();
    	
    	lc.getXAxis().setTickLabelFont(new Font("Lucida Console", (int)lc.getXAxis().getTickLabelFont().getSize()));
    	
        // Fill Chart
        lc.getData().removeAll(lc.getData());
        Map<String, XYChart.Series<String, Number>> series = new HashMap<String, XYChart.Series<String, Number>>();
        
        String itemSelected = cbSelectedRow.getSelectionModel().getSelectedItem();
        
        int rowSelected = -1;
        
        if (!itemSelected.equals(SELECT_ALL_ROWS))
        	rowSelected = cbSelectedRow.getSelectionModel().getSelectedIndex();
        
        String typeOrderSelected = cbOrderBy.getSelectionModel().getSelectedItem();
        
        String nameCol = "";
        String nameRow = "";
        
        ArrayList<Cell> cells = domino.getMat().getData();
        ArrayList<Cell> temp = new ArrayList<Cell>();
        
        if (rowSelected != -1){
	        // 1 - selecting the cells where its row is equals selected row
	        for(Cell cell: cells){
	        	if(cell.row == rowSelected)
	        		temp.add(cell);
	        }
	        cells = temp;
        }
        // 1 - end
        
        // 2 - Adding the columns where value is 0 (zero)
        if (rowSelected != -1){
	        int j = 0;
	        temp = new ArrayList<Cell>(_nCols);
	        for(int column = 0; column < _nCols; column++){
	        	if(j < cells.size() && cells.get(j).col == column){
	        		temp.add(cells.get(j++));
	        	}else{
	        		temp.add(new Cell(rowSelected, column, 0));
	        	}
	        }
	        cells = temp;
	        temp = null;
        }
        // 2 - end
        
        // [Optional] 3 - order columns by value
        if(typeOrderSelected.equals(TYPE_ORDER_BY_INCREASING)){
        	cells.sort(new Comparator<Cell>() {
        		public int compare(Cell c1, Cell c2) {
        			if(c1.value < c2.value) return -1;
        			else if(c1.value > c2.value) return 1;
        			return 0;
        		};
        	});
        } else if (typeOrderSelected.equals(TYPE_ORDER_BY_DECREASING)){
        	cells.sort(new Comparator<Cell>() {
        		public int compare(Cell c1, Cell c2) {
        			if(c1.value > c2.value) return -1;
        			else if(c1.value < c2.value) return 1;
        			return 0;
        		};
        	});	
        }
        // 3 - end
        
        // 4 - Adding in Chart
        for(int i = 0; i < cells.size(); i++){
        	nameRow = domino.getDescriptor().getRowAt(cells.get(i).row);
        	nameCol = domino.getDescriptor().getColumnAt(cells.get(i).col);
        	
        	XYChart.Series<String, Number> currentSerie = series.get(nameCol);
        	if (currentSerie == null){
        		currentSerie = new XYChart.Series<String, Number>();
        		currentSerie.setName(nameCol); //row name
        		series.put(nameCol, currentSerie);	
        	}
        	currentSerie.getData().add(new XYChart.Data<String,Number>(nameRow, cells.get(i).value));

        }
        
        for (Map.Entry<String, XYChart.Series<String,Number>> _serie : series.entrySet()){
        	lc.getData().add(_serie.getValue());
        }
        
        
        double fontSize = lc.getXAxis().getTickLabelFont().getSize();
    	double textLarger = 0;
        	
    	for(int i = 0; i < _nCols; i++){
    		if(domino.getDescriptor().getColumnAt(i).length() > textLarger){
    			textLarger = domino.getDescriptor().getColumnAt(i).length();
    		}
    	}
    	lc.setPrefWidth(_nCols * fontSize * 2);
    	lc.setPrefHeight(1.5 * textLarger * fontSize);	
        
    }

}
