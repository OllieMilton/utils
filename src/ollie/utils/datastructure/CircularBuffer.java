package ollie.utils.datastructure;

import java.lang.reflect.Array;
import java.nio.BufferOverflowException;

/**
 * An implementation of a circular buffer that uses native calls to {@code System.arraycopy()} for efficiency and does not allow 
 * overwriting of occupied unread elements.
 * If an attempt is made to add an element to the buffer and there is no free space then a {@code BufferOverflowException} is thrown.
 * 
 * @author Ollie
 *
 * @param <T> The generic type contained in the buffer.
 */
public class CircularBuffer<T> implements Buffer<T> {
	
	private T[] buffer;
	private int readPos;
	private int writePos;
	private boolean full = false;
	private T emptyValue;
	
	public CircularBuffer(int size, Class<T> type) {
		this(size, type, null);
	}
	
	@SuppressWarnings("unchecked")
	public CircularBuffer(int size, Class<T> type, T emptyValue) {
		buffer = (T[]) Array.newInstance(type, size);
		this.emptyValue = emptyValue;
	}
				
	/* (non-Javadoc)
	 * @see ollie.utils.datastructure.Buffer#put(java.lang.Object)
	 */
	@Override
	public void put(T in) {
		if (freeSpace() > 0) {
			buffer[writePos++] = in;
			if (readPos == writePos) {
				full = true;
			}
		} else {
			throw new BufferOverflowException();
		}
	}

	/* (non-Javadoc)
	 * @see ollie.utils.datastructure.Buffer#put(java.lang.Object[])
	 */
	@Override
	public void put(T[] in) {
		if (in.length <= freeSpace()) {
			if (writePos + in.length <= buffer.length) {
				// we can populate the buffer in a single operation
				System.arraycopy(in, 0, buffer, writePos, in.length);
				writePos += in.length;
			} else {
				// going to overrun the array length so need to wrap
				int firstWrite = buffer.length - writePos;
				int secondWrite = in.length - firstWrite;
				System.arraycopy(in, 0, buffer, writePos, firstWrite);
				writePos = 0;
				System.arraycopy(in, 0, buffer, writePos, secondWrite);
				writePos += secondWrite;
			}
			if (readPos == writePos) {
				full = true;
			}
		} else {
			throw new BufferOverflowException();
		}
	}

	/* (non-Javadoc)
	 * @see ollie.utils.datastructure.Buffer#get()
	 */
	@Override
	public T get() {
		T result = emptyValue;
		if (size() > 0) {
			readPos = readPos % buffer.length;
			result = buffer[readPos];
			readPos ++;
			full = false;
		}
		return result;
	}

	/* (non-Javadoc)
	 * @see ollie.utils.datastructure.Buffer#get(java.lang.Object[])
	 */
	@Override
	public int get(T[] t) {
		return get(t, 0, t.length);
	}

	/* (non-Javadoc)
	 * @see ollie.utils.datastructure.Buffer#get(java.lang.Object[], int, int)
	 */
	@Override
	public int get(T[] t, int off, int len) {
		int remaining = size();
		int read = -1;
		if (remaining > len) {
			// more elements available than requested so set read to the number requested
			read = len;
		} else if (remaining > 0) {
			// less elements available than requested so set read to the number remaining
			read = remaining;
		}
		if (read > -1) {
			// copy read number of elements into t
			int firstRead = (buffer.length - readPos);
			if (read <= firstRead) {
				// can do it in a single operation
				System.arraycopy(buffer, readPos, t, off, read);
				readPos += read;
			} else {
				// need to wrap back round to the beginning of the array
				System.arraycopy(buffer, readPos, t, off, firstRead);
				readPos = 0;
				int secondRead = writePos;
				System.arraycopy(buffer, readPos, t, off+firstRead, secondRead);
				readPos += secondRead;
			}
			full = false;
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
	 * @see ollie.utils.datastructure.Buffer#pos()
	 */
	@Override
	public int pos() {
		return readPos;
	}

	/* (non-Javadoc)
	 * @see ollie.utils.datastructure.Buffer#size()
	 */
	@Override
	public int size() {
		return buffer.length - freeSpace();
	}

	/* (non-Javadoc)
	 * @see ollie.utils.datastructure.Buffer#length()
	 */
	@Override
	public int length() {
		return buffer.length;
	}

	/* (non-Javadoc)
	 * @see ollie.utils.datastructure.Buffer#freeSpace()
	 */
	@Override
	public int freeSpace() {
		if (readPos > writePos) {
			return readPos - writePos;
		} else if (writePos > readPos) {
			return (buffer.length - writePos) + readPos;
		} else {
			return full ? 0 : buffer.length;
		}
	}
	
	/* (non-Javadoc)
	 * @see ollie.utils.datastructure.Buffer#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return size() == 0; 
	}
	
	/* (non-Javadoc)
	 * @see ollie.utils.datastructure.Buffer#setEmptyValue(java.lang.Object)
	 */
	@Override
	public void setEmptyValue(T emptyValue) {
		this.emptyValue = emptyValue;
	}

}
