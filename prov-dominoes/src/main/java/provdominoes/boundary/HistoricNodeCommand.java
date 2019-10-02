package provdominoes.boundary;

import java.util.LinkedList;

import provdominoes.command.AbstractCommand;

public class HistoricNodeCommand {

	private String id;
	private AbstractCommand command;
	private HistoricNodeCommand parent;
	private LinkedList<HistoricNodeCommand> children;

	public HistoricNodeCommand() {
		super();
		this.children = new LinkedList<>();
	}

	public HistoricNodeCommand(String id, AbstractCommand command) {
		this();
		this.id = id;
		this.command = command;
	}

	public HistoricNodeCommand addCommand(String id, AbstractCommand command) {
		HistoricNodeCommand newCommand = new HistoricNodeCommand(id, command);
		newCommand.setParent(this);
		if (command != null) {
			children.add(newCommand);
		}
		return newCommand;
	}

	public static void getCommandList(HistoricNodeCommand node, LinkedList<AbstractCommand> commands) {
		if (node != null && node.getParent() != null) {
			commands.addFirst(node.getCommand());
			getCommandList(node.getParent(), commands);
		}
	}

	public static HistoricNodeCommand findNode(String id, HistoricNodeCommand node) {
		if (id != null && node != null && node.getId().equals(id)) {
			return node;
		} else {
			if (node != null) {
				for (HistoricNodeCommand n : node.getChildren()) {
					HistoricNodeCommand found = findNode(id, n);
					if (found != null) {
						return found;
					}
				}
			}
		}
		return null;
	}

	public AbstractCommand getCommand() {
		return command;
	}

	public void setCommand(AbstractCommand command) {
		this.command = command;
	}

	public HistoricNodeCommand getParent() {
		return parent;
	}

	public void setParent(HistoricNodeCommand parent) {
		this.parent = parent;
	}

	public LinkedList<HistoricNodeCommand> getChildren() {
		return children;
	}

	public void setChildren(LinkedList<HistoricNodeCommand> children) {
		this.children = children;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "HistoricNodeCommand [id=" + id + "]";
	}

}
