package ollie.common.datastructure;

public class BufferOverflowException extends RuntimeException {

	private static final long serialVersionUID = -3458024449854858833L;

	public BufferOverflowException(String msg) {
		super(msg);
	}
}
