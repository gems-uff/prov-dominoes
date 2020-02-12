package provdominoes.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TokenUtil {

	private static final String REGEXP_STRING = "\"(?:[^\"\\\\]|\\\\.)*\"";
	private static final String CODED_OPEN_PARENTESIS = ">>>>>>>OP<<<<<<<";
	private static final String CODED_CLOSE_PARENTESIS = ">>>>>>>CP<<<<<<<";
	private static final String CODED_VAR_POINTER = ">>>>>>>V<<<<<<<";
	private static final String CODED_COMMA_MARK = ">>>>>>>O<<<<<<<";

	private static TokenUtil instance;

	public static TokenUtil getInstance() {
		if (instance == null) {
			instance = new TokenUtil();
		}
		return instance;
	}

	public boolean isEmpty(String token) {
		return token != null && token.length() == 0;
	}

	public String supressReserved(String token) {
		String result = null;
		Map<String, String> replacements = new HashMap<>();
		if (token != null) {
			Pattern p = Pattern.compile(REGEXP_STRING);
			Matcher m = p.matcher(token);
			while (m.find()) {
				String replacement = token.substring(m.start(), m.end());
				replacement = replacement.replace("(", CODED_OPEN_PARENTESIS);
				replacement = replacement.replace(")", CODED_CLOSE_PARENTESIS);
				replacement = replacement.replace("$", CODED_VAR_POINTER);
				replacement = replacement.replace(",", CODED_COMMA_MARK);
				replacements.put(token.substring(m.start(), m.end()), replacement);
			}
			result = token;
			for (String key : replacements.keySet()) {
				result = result.replace(key, replacements.get(key));
			}
		}
		return result;
	}

	public String impressReserved(String token) {
		String result = null;
		if (token != null) {
			result = token.replace(CODED_OPEN_PARENTESIS, "(");
			result = result.replace(CODED_CLOSE_PARENTESIS, ")");
			result = result.replace(CODED_VAR_POINTER, "$");
			result = result.replace(CODED_COMMA_MARK, ",");
		}
		return result;
	}

	public String impressEscaped(String token) {
		String result = null;
		if (token != null) {
			result = token.replace(" ", "_");

			result = result.replace("=", "\\=");
			result = result.replace("'", "\\'");
			result = result.replace("(", "\\(");
			result = result.replace(")", "\\)");
			result = result.replace(",", "\\,");
			result = result.replace(";", "\\;");
			result = result.replace("[", "\\[");
			result = result.replace("]", "\\]");

			result = result.replace(".", "\\.");
			result = result.replace(":", "\\:");
			result = result.replace("-", "\\-");

		}
		return result;
	}

}
