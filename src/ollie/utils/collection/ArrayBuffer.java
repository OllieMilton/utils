package ollie.utils.collection;

import java.lang.reflect.Array;
import java.nio.BufferOverflowException;

public class ArrayBuffer<T> {

	private T[] buffer;
	private int readPos;
	private int writePos;
	
	@SuppressWarnings("unchecked")
	public ArrayBuffer(Class<T> type, int size) {
		buffer = (T[]) Array.newInstance(type, size);
		readPos = 0;
		writePos = 0;
	}
	
	public void put(byte[] in) {
		if (writePos + in.length <= buffer.length) {
			System.arraycopy(in, 0, buffer, writePos, in.length);
			writePos += in.length;
		} else {
			throw new BufferOverflowException();
		}
	}
	
	public T get() {
		return buffer[readPos++];
	}
	
	public int get(T[] b) {
		int remaining = writePos-readPos;
		int read = -1;
		if (remaining > b.length) {
			// more bytes available than requested so set read to the number requested
			read = b.length;
		} else if (remaining > 0) {
			// less bytes available than requested so set read to the number remaining
			read = remaining;
		}
		if (read > -1) {
			System.arraycopy(buffer, readPos, b, 0, read);
			readPos += read;
		}
		return read;
	}
	
	public int get(T[] b, int off, int len) {
		int remaining = writePos-readPos;
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
			System.arraycopy(buffer, readPos, b, off, read);
			readPos += read;
		}
		return read;
	}
	
	public void seek(int pos) {
		if (pos < 0 || pos > buffer.length) {
			throw new IndexOutOfBoundsException("Position ["+pos+"] is outside the bounds of the buffer.");
		}
		readPos = pos;
	}
	
	public int pos() {
		return readPos;
	}
	
	public int size() {
		return writePos;
	}
	
	public int length() {
		return buffer.length;
	}
	
	public int available() {
		return writePos - readPos;
	}

}
