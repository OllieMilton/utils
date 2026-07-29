package ollie.common.datastructure;

import java.nio.BufferOverflowException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CircularByteBufferTest {

	@Test(expected=BufferOverflowException.class)
	public void testOverflow() {
		ByteBuffer buff = new CircularByteBuffer(5);
		buff.put(new byte[] {1,2,3,4,5,6});
	}
	
	@Test
	public void testEmptyGet() {
		ByteBuffer buff = new CircularByteBuffer(5);
		Assert.assertEquals(-1, buff.get());
	}
	
	@Test
	public void testGet() {
		ByteBuffer buff = new CircularByteBuffer(5);
		buff.put(new byte[] {1,2,3,4,5});
		Assert.assertEquals(5, buff.size());
		Assert.assertEquals(5, buff.length());
		Assert.assertEquals(0, buff.freeSpace());
		
		Assert.assertEquals(1, buff.get());
		Assert.assertEquals(4, buff.size());
		Assert.assertEquals(5, buff.length());
		Assert.assertEquals(1, buff.freeSpace());
		
		Assert.assertEquals(2, buff.get());
		Assert.assertEquals(3, buff.size());
		Assert.assertEquals(5, buff.length());
		Assert.assertEquals(2, buff.freeSpace());
		
		Assert.assertEquals(3, buff.get());
		Assert.assertEquals(2, buff.size());
		Assert.assertEquals(5, buff.length());
		Assert.assertEquals(3, buff.freeSpace());
		
		Assert.assertEquals(4, buff.get());
		Assert.assertEquals(1, buff.size());
		Assert.assertEquals(5, buff.length());
		Assert.assertEquals(4, buff.freeSpace());
		
		Assert.assertEquals(5, buff.get());
		Assert.assertEquals(0, buff.size());
		Assert.assertEquals(5, buff.length());
		Assert.assertEquals(5, buff.freeSpace());
		
		Assert.assertEquals(-1, buff.get());
		Assert.assertEquals(0, buff.size());
		Assert.assertEquals(5, buff.length());
		Assert.assertEquals(5, buff.freeSpace());
	}
	
	@Test
	public void testMultiGet() {
		ByteBuffer buff = new CircularByteBuffer(5);
		buff.put(new byte[] {1,2,3,4,5});
		byte[] result = new byte[5];
		Assert.assertEquals(5, buff.get(result));
		Assert.assertArrayEquals(new byte[] {1,2,3,4,5}, result);
	}
	
	@Test
	public void testWrap() {
		ByteBuffer buff = new CircularByteBuffer(5);
		buff.put(new byte[] {1,2,3,4,5});
		byte[] result = new byte[3];
		Assert.assertEquals(3, buff.get(result));
		Assert.assertArrayEquals(new byte[] {1,2,3}, result);
		result = new byte[5];
		buff.put(new byte[] {7,8});
		Assert.assertEquals(4, buff.size());
		Assert.assertEquals(4, buff.get(result));
		Assert.assertArrayEquals(new byte[] {4,5,7,8,0}, result);
		
		buff.put(new byte[] {20,21,22});
		result = new byte[5];
		buff.get(result);
		Assert.assertArrayEquals(new byte[] {20,21,22,0,0}, result);
		
		Assert.assertTrue(buff.isEmpty());
	}
	
	/**
	 * Wraps mid array so that the put is split into two copies with a non
	 * zero first segment (firstWrite = 2, secondWrite = 4). Regression test
	 * for the wrapping put copying the second segment from offset 0 of the
	 * source instead of offset firstWrite.
	 */
	@Test
	public void testMidArrayWrap() {
		ByteBuffer buff = new CircularByteBuffer(10);
		buff.put(new byte[] {0,0,0,0,0,0,0,0});
		Assert.assertEquals(8, buff.get(new byte[8]));

		buff.put(new byte[] {1,2,3,4,5,6});
		byte[] result = new byte[6];
		Assert.assertEquals(6, buff.get(result));
		Assert.assertArrayEquals(new byte[] {1,2,3,4,5,6}, result);
	}

	/**
	 * Repeatedly wraps with a chunk size that does not divide the buffer
	 * length so the wrap point moves through the array, checking every chunk
	 * round trips intact.
	 */
	@Test
	public void testMisalignedRepeatedWraps() {
		ByteBuffer buff = new CircularByteBuffer(2048);
		byte[] in = new byte[500];
		byte[] out = new byte[500];
		for (int chunk = 0; chunk < 50; chunk++) {
			for (int i = 0; i < in.length; i++) {
				in[i] = (byte) (chunk*500 + i);
			}
			buff.put(in);
			Assert.assertEquals(500, buff.get(out));
			Assert.assertArrayEquals("corrupted at chunk "+chunk, in, out);
		}
	}

	/**
	 * The single byte put must wrap the write position back to the start of
	 * the array rather than writing past the end.
	 */
	@Test
	public void testSingleBytePutWraps() {
		ByteBuffer buff = new CircularByteBuffer(4);
		buff.put(new byte[] {1,2,3});
		Assert.assertEquals(1, buff.get());
		Assert.assertEquals(2, buff.get());
		buff.put((byte)4);
		buff.put((byte)5);
		Assert.assertEquals(3, buff.get());
		Assert.assertEquals(4, buff.get());
		Assert.assertEquals(5, buff.get());
	}

	@Test
	public void testMultiPut() {
		ByteBuffer buff = new CircularByteBuffer(20);
		buff.put(new byte[] {1,2,3,4,5});
		buff.put(new byte[] {1,2,3,4,5});
		buff.put(new byte[] {1,2,3,4,5});
		buff.put(new byte[] {1,2,3,4,5});
		buff.get();
		Assert.assertEquals(1, buff.freeSpace());
	}
	
	@Test
	public void testReFill() {
		ByteBuffer buff = new CircularByteBuffer(10);
		buff.put(new byte[] {1,2,3,4,5});
		buff.put(new byte[] {6,7,8,9,10});
		int i=0;
		while (i++ < 100000) {
			byte[] result = new byte[2];
			Assert.assertEquals(2, buff.get(result));
			Assert.assertArrayEquals(new byte[] {1,2}, result);
			
			result = new byte[2];
			Assert.assertEquals(2, buff.get(result));
			Assert.assertArrayEquals(new byte[] {3,4}, result);
			
			result = new byte[2];
			Assert.assertEquals(2, buff.get(result));
			Assert.assertArrayEquals(new byte[] {5,6}, result);
		
			buff.put(new byte[] {1,2,3,4,5});
			
			result = new byte[2];
			Assert.assertEquals(2, buff.get(result));
			Assert.assertArrayEquals(new byte[] {7,8}, result);
			
			result = new byte[2];
			Assert.assertEquals(2, buff.get(result));
			Assert.assertArrayEquals(new byte[] {9,10}, result);
			
			buff.put(new byte[] {6,7,8,9,10});
			
		}
		
	}

}
