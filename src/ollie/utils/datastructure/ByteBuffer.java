package ollie.utils.datastructure;

public interface ByteBuffer {
	
	/**
	 * Add the given byte to the buffer.
	 * @param in - the bytes to add.
	 */
	void put(byte in);
	
	/**
	 * Add the bytes from the given array to the tail of the buffer.
	 * @param in - the array to add to the buffer.
	 */
	void put(byte[] in);

	/**
	 * Get the next byte from the front of the buffer.
	 * @return The next byte from the buffer or null or the empty value if configured.
	 */
	byte get();
	
	/**
	 * Get enough from the front of the buffer to fill the given array. Partially fills the array if the buffer does not contain enough.
	 * @param t - the array to fill.
	 * @return The actual number of bytes added to the array or -1 if the end of the buffer has been reached.
	 */
	int get(byte[] t);

	/**
	 * Gets the specified number of bytes from the buffer and adds them to the given array starting from the given offset.
	 * @param b - the array to add the bytes to.
	 * @param off - the offset to start adding the bytes in the array at.
	 * @param len - the number of bytes to add.
	 * @return The actual number of bytes added to the array or -1 if the end of the buffer has been reached.
	 */
	int get(byte[] b, int off, int len);
	
	/**
	 * Resets the read position to the given position. Note this can go wrong in implementations that discard the buffer over time.
	 * @param pos -  the new read position.
	 */
	void seek(int pos);

	/**
	 * @return The current index at the front of the buffer
	 */
	int pos();

	/**
	 * @return The current number of bytes in the buffer.
	 */
	int size();

	/**
	 * @return The total buffer size (empty and occupied bytes).
	 */
	int length();

	/**
	 * @return The number of bytes available to be written to in the buffer.
	 */
	int freeSpace();

	/**
	 * Determines whether the buffer is empty.
	 * @return true if the buffer is empty.
	 */
	boolean isEmpty();

	/**
	 * Sets an object to be returned on a call to {@code get()} if the buffer is empty. Defaults to null.
	 * @param emptyValue - the value to return if the buffer is empty.
	 */
	void setEmptyValue(byte emptyValue);
}
