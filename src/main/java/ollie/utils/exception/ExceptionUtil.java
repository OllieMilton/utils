package ollie.utils.exception;

public class ExceptionUtil {

	/**
	 * Gets the message from the root cause of the given throwable.
	 * @param t - the throwable to find the cause message of.
	 * @return the root cause message.
	 */
	public static String causeMessage(Throwable t) {
		return cause(t).getMessage();
	}
	
	/**
	 * Gets the root cause of the given throwable.
	 * @param t - the throwable to find the cause of.
	 * @return the root cause.
	 */
	public static Throwable cause(Throwable t) {
		Throwable result = t;
		while (result.getCause() != null) {
			result = result.getCause();
		}
		return result;
	}
}
