package ollie.utils.datastructure;

/**
 * @author Ollie
 *
 * @param <T> The generic type of the elements in the buffer.
 */
public interface Buffer<T> {

	/**
	 * Add the given element to the buffer.
	 * @param in - the elements to add.
	 */
	void put(T in);
	
	/**
	 * Add the elements from the given array to the tail of the buffer.
	 * @param in - the array to add to the buffer.
	 */
	void put(T[] in);

	/**
	 * Get the next element from the front of the buffer.
	 * @return The next element from the buffer or null or the empty value if configured.
	 */
	T get();
	
	/**
	 * Get enough from the front of the buffer to fill the given array. Partially fills the array if the buffer does not contain enough.
	 * @param t - the array to fill.
	 * @return The actual number of elements added to the array or -1 if the end of the buffer has been reached.
	 */
	int get(T[] t);

	/**
	 * Gets the specified number of elements from the buffer and adds them to the given array starting from the given offset.
	 * @param t - the array to add the elements to.
	 * @param off - the offset to start adding the elements in the array at.
	 * @param len - the number of elements to add.
	 * @return The actual number of elements added to the array or -1 if the end of the buffer has been reached.
	 */
	int get(T[] t, int off, int len);
	
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
	 * @return The current number of elements in the buffer.
	 */
	int size();

	/**
	 * @return The total buffer size (empty and occupied elements).
	 */
	int length();

	/**
	 * @return The number of elements available to be written to in the buffer.
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
	void setEmptyValue(T emptyValue);
}