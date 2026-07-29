package ollie.utils;

public class Strings {

	private Strings() {}
	
	public static boolean isBlank(String s) {
		return !isNotBlank(s);
	}
	
	public static boolean isNotBlank(String s) {
		return s == null ? false : !s.equals("");
	}
}
