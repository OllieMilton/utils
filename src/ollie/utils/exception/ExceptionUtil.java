package ollie.utils.exception;

public class ExceptionUtil {

	public static String causeMessage(Exception e) {
		String message = e.getMessage();
		if (e.getCause() != null) {
			message = e.getCause().getMessage();
		}
		return message;
	}
}
