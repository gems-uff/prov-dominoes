package provdominoes.command;

import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import provdominoes.boundary.App;
import provdominoes.domain.Configuration;
import provdominoes.domain.Dominoes;

public class RowTextCommand extends AbstractCommand {

	private Group piece;
	private double x;
	private double y;
	private Dominoes oldDominoes;
	private int index;
	private TextFilterData text;

	public RowTextCommand() {
		this.index = -1;
	}

	public RowTextCommand(int index) {
		this();
		this.index = index;
	}

	@Override
	protected boolean doIt() {
		boolean success = true;
		this.oldDominoes = App.getArea().getData().getDominoes().get(index).cloneNoMatrix();
		this.piece = App.getArea().getData().getPieces().get(index);
		x = this.piece.getTranslateX();
		y = this.piece.getTranslateY();
		try {
			Dominoes toText = App.getArea().getData().getDominoes().get(index);
			if (!super.isReproducing()) {
				text = getText();
			}
			if (text != null) {
				Dominoes domino = provdominoes.control.Controller.filterRowText(toText, text);

				App.getArea().remove(index);
				this.piece = App.getArea().add(domino, piece.getTranslateX(), piece.getTranslateY(), index);

				if (Configuration.autoSave) {
					App.getArea().saveAndSendToList(piece);
				}
			} else {
				success = false;
			}
		} catch (Exception e) {
			App.alertException(e,
					"Erro desconhecido ao efetuar filtro de linhas e colunas contendo palavra: " + text + ".");
			e.printStackTrace();
			success = false;
		}
		super.setReproducing(false);
		return success;
	}

	private TextFilterData getText() {
		TextFilterData d = null;
		// Create the custom dialog.
		Dialog<TextFilterData> dialog = new Dialog<>();
		dialog.setTitle("Row Word Filter");
		dialog.setHeaderText("Please enter the expression you want to filter on rows!");

		// Set the button types.
		ButtonType loginButtonType = new ButtonType("Filter", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, ButtonType.CANCEL);

		// Create the username and password labels and fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField expression = new TextField();
		expression.setPromptText("String or expression");
		CheckBox cb = new CheckBox("Regular Expression");
		CheckBox cs = new CheckBox("Case Sensitive");
		
		cb.setOnAction((event) -> {
			if (cb.isSelected()) {
				cs.setSelected(true);
				cs.setDisable(true);
			} else {
				cs.setDisable(false);
			}
		});

		grid.add(new Label("Expression:"), 0, 0);
		grid.add(expression, 1, 0);
		grid.add(cb, 2, 0);
		grid.add(cs, 3, 0);
		

		// Enable/Disable login button depending on whether a username was entered.
		Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
		loginButton.setDisable(true);

		// Do some validation (using the Java 8 lambda syntax).
		expression.textProperty().addListener((observable, oldValue, newValue) -> {
			loginButton.setDisable(newValue.trim().isEmpty());
		});

		dialog.getDialogPane().setContent(grid);

		// Request focus on the username field by default.
		Platform.runLater(() -> expression.requestFocus());

		// Convert the result to a username-password-pair when the login button is
		// clicked.
		dialog.setResultConverter(dialogButton -> {
			if (dialogButton == loginButtonType) {
				return new TextFilterData(expression.getText(), cb.isSelected(), cs.isSelected());
			}
			return null;
		});

		Optional<TextFilterData> result = dialog.showAndWait();

		if (result.isPresent()) {
			d = result.get();
		}
		return d;
	}

	@Override
	protected boolean undoIt() {
		boolean result = false;
		Group p = App.getArea().getData().getPieces().get(this.index);
		App.getArea().closePiece(p);
		App.getArea().add(this.oldDominoes, x, y, this.index);
		result = true;
		return result;
	}

	@Override
	public String getName() {
		return ROW_TEXT_COMMAND + "(" + this.oldDominoes.getId() + ", " + text.isRegularExpression() + ", "
				+ text.isRegularExpression() + ", \"" + text.getExpression() + "\")";
	}

	private String id;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public void setText(TextFilterData text) {
		this.text = text;
	}

}
