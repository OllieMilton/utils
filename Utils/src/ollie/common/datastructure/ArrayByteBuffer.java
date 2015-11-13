package ollie.common.datastructure;

import java.nio.BufferOverflowException;

public class ArrayByteBuffer implements ByteBuffer {

	private byte[] buffer;
	private int readPos;
	private int writePos;
	private byte emptyValue;
	
	public ArrayByteBuffer(int size) {
		buffer = new byte[size];
	}
	
	/* (non-Javadoc)
	 * @see ollie.utils.datastructure.ByteBuffer#put(java.lang.Object)
	 */
	@Override
	public void put(byte in) {
		if (freeSpace() > 0) {
			buffer[writePos++] = in;
		} else {
			throw new BufferOverflowException();
		}
	}
	
	/* (non-Javadoc)
	 * @see ollie.utils.datastructure.ByteBuffer#put(java.lang.Object[])
	 */
	@Override
	public void put(byte[] in) {
		if (freeSpace() >= in.length) {
			System.arraycopy(in, 0, buffer, writePos, in.length);
			writePos += in.length;
		} else {
			throw new BufferOverflowException();
		}
	}
	
	/* (non-Javadoc)
	 * @see ollie.utils.datastructure.ByteBuffer#get()
	 */
	@Override
	public byte get() {
		if (readPos >= writePos) {
			return emptyValue;
		}
		return buffer[readPos++];
	}
	
	/* (non-Javadoc)
	 * @see ollie.utils.datastructure.ByteBuffer#get(java.lang.Object[])
	 */
	@Override
	public int get(byte[] b) {
		return get(b, 0, b.length);
	}
	
	/* (non-Javadoc)
	 * @see ollie.utils.datastructure.ByteBuffer#get(java.lang.Object[], int, int)
	 */
	@Override
	public int get(byte[] b, int off, int len) {
		int remaining = writePos-readPos;
		int read = -1;
		if (remaining > len) {
			// more elements available than requested so set read to the number requested
			read = len;
		} else if (remaining > 0) {
			// less elements available than requested so set read to the number remaining
			read = remaining;
		}
		if (read > -1) {
			// copy read number of elements into b
			System.arraycopy(buffer, readPos, b, off, read);
			readPos += read;
		}
		return read;
	}
	
	/* (non-Javadoc)
     * @see ollie.utils.datastructure.ByteBuffer#seek(int)
     */
    @Override
    public void seek(int pos) {
        if (pos < 0 || pos > buffer.length) {
            throw new IndexOutOfBoundsException("Position ["+pos+"] is outside the bounds of the buffer.");
        }
        readPos = pos;
    }
	
	/* (non-Javadoc)
	 * @see ollie.utils.datastructure.ByteBuffer#pos()
	 */
	@Override
	public int pos() {
		return readPos;
	}
	
	/* (non-Javadoc)
	 * @see ollie.utils.datastructure.ByteBuffer#size()
	 */
	@Override
	public int size() {
		return writePos;
	}
	
	/* (non-Javadoc)
	 * @see ollie.utils.datastructure.ByteBuffer#length()
	 */
	@Override
	public int length() {
		return buffer.length;
	}
	
	/* (non-Javadoc)
	 * @see ollie.utils.datastructure.ByteBuffer#freeSpace()
	 */
	@Override
	public int freeSpace() {
		return buffer.length - writePos;
	}

	/* (non-Javadoc)
	 * @see ollie.utils.datastructure.ByteBuffer#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return freeSpace() == buffer.length;
	}
	
	/* (non-Javadoc)
	 * @see ollie.utils.datastructure.ByteBuffer#setEmptyValue(java.lang.Object)
	 */
	@Override
	public void setEmptyValue(byte emptyValue) {
		this.emptyValue = emptyValue;
	}
}
