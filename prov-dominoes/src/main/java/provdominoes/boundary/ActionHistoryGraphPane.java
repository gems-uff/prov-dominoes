package provdominoes.boundary;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.functors.ConstantTransformer;

import edu.uci.ics.jung.algorithms.layout.TreeLayout;
import edu.uci.ics.jung.graph.DelegateForest;
import edu.uci.ics.jung.graph.Forest;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.GraphZoomScrollPane;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.DefaultModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse.Mode;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.picking.PickedState;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import provdominoes.command.AbstractCommand;
import provdominoes.command.CommandFactory;
import provdominoes.command.Redo;
import provdominoes.command.Undo;

/**
 *
 * @author Victor
 */
public class ActionHistoryGraphPane extends BorderPane {

	/**
	 * the graph
	 */
	private NodeInfo root;
	private HistoricNodeCommand rootCommand;
	private HistoricNodeCommand lastCommand;
	private int vertexCounter = 1;
	private int edgeCounter = 0;
	private Integer[] ids = new Integer[10];
	private Map<String, NodeInfo> nodes = new HashMap<>();
	private Map<String, NodeLink> edges = new HashMap<>();
	private Forest<String, String> graph;
	private VisualizationViewer<String, String> vv;
	private TreeLayout<String, String> treeLayout;

	public ActionHistoryGraphPane() {
		buildGraph();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void buildGraph() {
		graph = new DelegateForest<String, String>();
		createTree();
		treeLayout = new TreeLayout<String, String>(graph);
		vv = new VisualizationViewer<String, String>(treeLayout);
		vv.setBackground(Color.white);

		final DefaultModalGraphMouse<String, String> graphMouse = new DefaultModalGraphMouse<String, String>();
		graphMouse.setMode(Mode.PICKING);
		vv.setGraphMouse(graphMouse);
		final PickedState<String> pickedState = vv.getPickedVertexState();
		pickedState.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				Object obj = e.getItem();
				for (NodeInfo n : nodes.values()) {
					nodes.get(n.getId()).setHighlighted(false);
				}
				if (obj instanceof String) {
					String vertexId = (String) obj;
					nodes.get(vertexId).setHighlighted(pickedState.isPicked(vertexId));
				}
			}
		});

		vv.getRenderContext().setEdgeShapeTransformer(new EdgeShape.Line<String, String>());
		vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller<String>());
		vv.setVertexToolTipTransformer(new Transformer<String, String>() {

			@Override
			public String transform(String vertex) {
				return nodes.get(vertex).getTooltip();
			}
		});
		vv.getRenderContext().setArrowFillPaintTransformer(new ConstantTransformer(Color.lightGray));

		final GraphZoomScrollPane panel = new GraphZoomScrollPane(vv);
		panel.setPreferredSize(new Dimension(90, 700));
		SwingNode s = new SwingNode();
		s.setContent(panel);
		this.setCenter(s);

		this.setTop(addTransformingModeOptions());
		this.setMinWidth(120);
		this.setPrefWidth(120);
	}

	private HBox addTransformingModeOptions() {
		HBox hBox = new HBox();

		hBox.setPadding(new Insets(15, 12, 15, 12));
		hBox.setStyle("-fx-background-color: #9F945D;");

		final ToggleGroup optionGroup = new ToggleGroup();

		Label lblMouseMode = new Label("    Mouse Mode: ");
		lblMouseMode.setPrefSize(100, 20);

		RadioButton rbTransform = new RadioButton("Pan & Zoom");
		rbTransform.setPrefSize(100, 20);
		rbTransform.setToggleGroup(optionGroup);
		rbTransform.setUserData("T");

		RadioButton rbPick = new RadioButton("Picking");
		rbPick.setPrefSize(100, 20);
		rbPick.setUserData("P");
		rbPick.setToggleGroup(optionGroup);
		rbPick.setSelected(true);

		optionGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {

			@Override
			public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
				if (optionGroup.getSelectedToggle() != null) {
					@SuppressWarnings("unchecked")
					DefaultModalGraphMouse<String, String> dmg = (DefaultModalGraphMouse<String, String>) vv
							.getGraphMouse();

					if (optionGroup.getSelectedToggle().getUserData().equals("T")) {
						dmg.setMode(Mode.TRANSFORMING);
					} else if (optionGroup.getSelectedToggle().getUserData().equals("P")) {
						dmg.setMode(Mode.PICKING);
					}
				}

			}
		});

		Button btn = new Button();
		btn.setText("Reproduce...");
		btn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				executeReproduce();
			}
		});
		hBox.getChildren().addAll(btn, lblMouseMode, rbPick, rbTransform);
		return hBox;
	}
	
	public void executeReproduce() {
		HashMap<String, NodeInfo> tempNodes = new HashMap<>(nodes);
		for (NodeInfo node : tempNodes.values()) {
			if (node.isHighlighted) {
				LinkedList<AbstractCommand> commands = new LinkedList<>();
				HistoricNodeCommand cmd = HistoricNodeCommand.findNode(node.getId(), rootCommand);
				HistoricNodeCommand.getCommandList(cmd, commands);
				if (commands.size() > 0 || rootCommand.getId().equals(node.getId())) {
					commands.addFirst(rootCommand.getCommand());
					try {
						App.getCommandManager().reproduce(commands);
					} catch (IOException e) {
						App.alertException(e, "Failed trying to access script file!");
					}
					lastCommand = cmd;
				}
				break;
			}
		}
	}

	private void createTree() {
		if (rootCommand != null) {
			graph.addVertex(rootCommand.getId());
			if (edges.size() > 0) {
				for (int i = 0; i < ids.length; i++) {
					NodeLink edge = edges.get(ids[i] + "");
					if (edge != null && !graph.getEdges().contains(edge.getId())) {
						graph.addEdge(edge.getId(), edge.getOriginNode(), edge.getDestinationNode(), EdgeType.DIRECTED);
					}
				}
			}
		}
	}

	public String addCommand(AbstractCommand cmd) {
		String generatedId = "";
		updatePicks();
		if (cmd != null) {
			if (rootCommand == null) {
				rootCommand = new HistoricNodeCommand("" + vertexCounter++, cmd);
				this.root = new NodeInfo(rootCommand.getId());
				this.root.setTooltip(cmd.getName());
				this.nodes.put(root.getId(), root);
				this.lastCommand = this.rootCommand;
			} else {
				if (cmd instanceof Undo) {
					this.lastCommand = this.lastCommand.getParent();
				} else if (cmd instanceof Redo) {
					if (lastCommand == null) {
						this.lastCommand = rootCommand;
					} else {
						this.lastCommand = HistoricNodeCommand
								.findNode(App.getCommandManager().getRedoList().getFirst().getId(), rootCommand);
					}
				} else {
					generatedId = "" + vertexCounter++;
					NodeInfo newNode = new NodeInfo(generatedId);
					newNode.setTooltip(cmd.getName());
					this.nodes.put(newNode.getId(), newNode);
					this.lastCommand = this.lastCommand.addCommand(newNode.getId(), cmd);
					int id = edgeCounter++;
					NodeLink newEdge = new NodeLink("" + id, this.lastCommand.getParent().getId(),
							this.lastCommand.getId());
					this.edges.put(newEdge.getId(), newEdge);
					if (id == this.ids.length) {
						Collection<Integer> t = Arrays.asList(ids);
						this.ids = t.toArray(new Integer[ids.length + 10]);
					}
					this.ids[id] = id;
				}
			}
		}
		buildGraph();
		updatePicks();
		return generatedId;
	}

	public void updatePicks() {
		for (NodeInfo node : nodes.values()) {
			if (node != null) {
				if (lastCommand != null && node.getId().equals(lastCommand.getId())) {
					vv.getPickedVertexState().pick(node.getId(), true);
					node.setHighlighted(true);
				} else {
					vv.getPickedVertexState().pick(node.getId(), false);
					node.setHighlighted(false);
				}
			}
		}
	}

	public void reset() {
		this.vertexCounter = 1;
		this.edgeCounter = 0;
		this.ids = new Integer[10];
		this.root = null;
		this.rootCommand = null;
		this.lastCommand = null;
		this.nodes = new HashMap<>();
		this.edges = new HashMap<>();
		CommandFactory.getInstance().setPieceCounter(1);
		buildGraph();
	}

	public HistoricNodeCommand getRootCommand() {
		return rootCommand;
	}

	public void setRootCommand(HistoricNodeCommand rootCommand) {
		this.rootCommand = rootCommand;
	}

}
