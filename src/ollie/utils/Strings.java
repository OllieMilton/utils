package ollie.utils;

public class Strings {

	private Strings() {}
	
	public static boolean isBlank(String s) {
		return s == null || s.equals("");
	}
	
	public static boolean isNotBlank(String s) {
		return !isBlank(s);
	}
}
