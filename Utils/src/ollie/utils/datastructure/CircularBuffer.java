package ollie.utils.datastructure;

import java.lang.reflect.Array;
import java.nio.BufferOverflowException;

public class CircularBuffer<T> implements Buffer<T> {
	
	private T[] buffer;
	private int readPos;
	private int writePos;
	
	@SuppressWarnings("unchecked")
	public CircularBuffer(int size, Class<T> type) {
		buffer = (T[]) Array.newInstance(type, size);
	}
		
	@Override
	public void put(T[] in) {
		if (in.length <= freeSpace()) {
			if (writePos + in.length <= buffer.length) {
				System.arraycopy(in, 0, buffer, writePos, in.length);
				writePos += in.length;
			} else {
				int firstWrite = buffer.length - writePos;
				int secondWrite = in.length - firstWrite;
				System.arraycopy(in, 0, buffer, writePos, firstWrite);
				writePos = 0;
				System.arraycopy(in, 0, buffer, writePos, secondWrite);
				writePos += secondWrite;
			}
		} else {
			throw new BufferOverflowException();
		}
	}

	@Override
	public T get() {
		readPos = readPos % buffer.length;
		T result = buffer[readPos];
		readPos ++;
		return result;
	}

	@Override
	public int get(T[] t) {
		return get(t, 0, t.length);
	}

	@Override
	public int get(T[] t, int off, int len) {
		int remaining = size();
		int read = -1;
		if (remaining > len) {
			// more bytes available than requested so set read to the number requested
			read = len;
		} else if (remaining > 0) {
			// less bytes available than requested so set read to the number remaining
			read = remaining;
		}
		if (read > -1) {
			// copy read number of bytes into b
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
		}
		return read;
	}

	@Override
	public int pos() {
		return readPos;
	}

	@Override
	public int size() {
		return buffer.length - freeSpace();
	}

	@Override
	public int length() {
		return buffer.length;
	}

	@Override
	public int freeSpace() {
		if (readPos > writePos) {
			return readPos - writePos;
		} else if (writePos > readPos) {
			return (buffer.length - writePos) + readPos;
		} else {
			return buffer.length;
		}
	}
	
	@Override
	public boolean isFull() {
		return freeSpace() == 0; 
	}

}
