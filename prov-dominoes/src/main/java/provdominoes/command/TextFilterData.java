package provdominoes.command;

public class TextFilterData {

	private String expression;
	private boolean regularExpression;
	private boolean caseSensitive;

	public TextFilterData() {
		super();
		this.expression = "";
	}

	public TextFilterData(String expression, boolean regularExpression, boolean caseSensitive) {
		super();
		this.expression = expression;
		this.regularExpression = regularExpression;
		this.caseSensitive = caseSensitive;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public boolean isRegularExpression() {
		return regularExpression;
	}

	public void setRegularExpression(boolean regularExpression) {
		this.regularExpression = regularExpression;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

}
