package ollie.common.datastructure;

import java.nio.BufferOverflowException;

/**
 * A primitive byte implementation of {@code CircularBuffer} that uses native calls to {@code System.array.copy()} for efficiency 
 * and does not allow overwriting of occupied unread elements.
 * If an attempt is made to add an element to the buffer and there is no free space then a {@code BufferOverflowException} is thrown.
 * 
 * @author Ollie
 */
public class CircularByteBuffer implements ByteBuffer {
	
	private byte[] buffer;
	private int readPos;
	private int writePos;
	private boolean full = false;
	private byte emptyValue = -1;
	
	public CircularByteBuffer(int size) {
		buffer = new byte[size];
	}
					
	/* (non-Javadoc)
	 * @see ollie.utils.datastructure.ByteBuffer#put(java.lang.Object)
	 */
	@Override
	public void put(byte in) {
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
	 * @see ollie.utils.datastructure.ByteBuffer#put(java.lang.Object[])
	 */
	@Override
	public void put(byte[] in) {
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
	 * @see ollie.utils.datastructure.ByteBuffer#get()
	 */
	@Override
	public byte get() {
		byte result = emptyValue;
		if (size() > 0) {
			readPos = readPos % buffer.length;
			result = buffer[readPos];
			readPos ++;
			full = false;
		}
		return result;
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
				System.arraycopy(buffer, readPos, b, off, read);
				readPos += read;
			} else {
				// need to wrap back round to the beginning of the array
				System.arraycopy(buffer, readPos, b, off, firstRead);
				readPos = 0;
				int secondRead = writePos;
				System.arraycopy(buffer, readPos, b, off+firstRead, secondRead);
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
		return buffer.length - freeSpace();
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
		if (readPos > writePos) {
			return readPos - writePos;
		} else if (writePos > readPos) {
			return (buffer.length - writePos) + readPos;
		} else {
			return full ? 0 : buffer.length;
		}
	}
	
	/* (non-Javadoc)
	 * @see ollie.utils.datastructure.ByteBuffer#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return size() == 0; 
	}
	
	/* (non-Javadoc)
	 * @see ollie.utils.datastructure.ByteBuffer#setEmptyValue(java.lang.Object)
	 */
	@Override
	public void setEmptyValue(byte emptyValue) {
		this.emptyValue = emptyValue;
	}

	@Override
	public void commit() {
		// TODO Auto-generated method stub
		
	}

}
