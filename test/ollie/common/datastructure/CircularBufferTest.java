package ollie.common.datastructure;

import java.nio.BufferOverflowException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class CircularBufferTest {

	@Test(expected=BufferOverflowException.class)
	public void testOverflow() {
		Buffer<Integer> buff = new CircularBuffer<>(5, Integer.class);
		buff.put(new Integer[] {1,2,3,4,5,6});
	}
	
	@Test
	public void testEmptyGet() {
		Buffer<Byte> buff = new CircularBuffer<>(5, Byte.class, (byte)-1);
		Assert.assertEquals(-1, buff.get().byteValue());
	}
	
	@Test
	public void testGet() {
		Buffer<Integer> buff = new CircularBuffer<>(5, Integer.class);
		buff.put(new Integer[] {1,2,3,4,5});
		Assert.assertEquals(5, buff.size());
		Assert.assertEquals(5, buff.length());
		Assert.assertEquals(0, buff.freeSpace());
		
		Assert.assertEquals(1, buff.get().intValue());
		Assert.assertEquals(4, buff.size());
		Assert.assertEquals(5, buff.length());
		Assert.assertEquals(1, buff.freeSpace());
		
		Assert.assertEquals(2, buff.get().intValue());
		Assert.assertEquals(3, buff.size());
		Assert.assertEquals(5, buff.length());
		Assert.assertEquals(2, buff.freeSpace());
		
		Assert.assertEquals(3, buff.get().intValue());
		Assert.assertEquals(2, buff.size());
		Assert.assertEquals(5, buff.length());
		Assert.assertEquals(3, buff.freeSpace());
		
		Assert.assertEquals(4, buff.get().intValue());
		Assert.assertEquals(1, buff.size());
		Assert.assertEquals(5, buff.length());
		Assert.assertEquals(4, buff.freeSpace());
		
		Assert.assertEquals(5, buff.get().intValue());
		Assert.assertEquals(0, buff.size());
		Assert.assertEquals(5, buff.length());
		Assert.assertEquals(5, buff.freeSpace());
		
		Assert.assertNull(buff.get());
		Assert.assertEquals(0, buff.size());
		Assert.assertEquals(5, buff.length());
		Assert.assertEquals(5, buff.freeSpace());
	}
	
	@Test
	public void testMultiGet() {
		Buffer<Integer> buff = new CircularBuffer<>(5, Integer.class);
		buff.put(new Integer[] {1,2,3,4,5});
		Integer[] result = new Integer[5];
		Assert.assertEquals(5, buff.get(result));
		Assert.assertArrayEquals(new Integer[] {1,2,3,4,5}, result);
	}
	
	@Test
	public void testWrap() {
		Buffer<Integer> buff = new CircularBuffer<>(5, Integer.class);
		buff.put(new Integer[] {1,2,3,4,5});
		Integer[] result = new Integer[3];
		Assert.assertEquals(3, buff.get(result));
		Assert.assertArrayEquals(new Integer[] {1,2,3}, result);
		result = new Integer[5];
		buff.put(new Integer[] {7,8});
		Assert.assertEquals(4, buff.size());
		Assert.assertEquals(4, buff.get(result));
		Assert.assertArrayEquals(new Integer[] {4,5,7,8, null}, result);
		
		buff.put(new Integer[] {20,21,22});
		result = new Integer[5];
		buff.get(result);
		Assert.assertArrayEquals(new Integer[] {20,21,22,null, null}, result);
		
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
		Buffer<Integer> buff = new CircularBuffer<>(10, Integer.class);
		buff.put(new Integer[] {0,0,0,0,0,0,0,0});
		Assert.assertEquals(8, buff.get(new Integer[8]));

		buff.put(new Integer[] {1,2,3,4,5,6});
		Integer[] result = new Integer[6];
		Assert.assertEquals(6, buff.get(result));
		Assert.assertArrayEquals(new Integer[] {1,2,3,4,5,6}, result);
	}

	/**
	 * The single element put must wrap the write position back to the start
	 * of the array rather than writing past the end.
	 */
	@Test
	public void testSingleElementPutWraps() {
		Buffer<Integer> buff = new CircularBuffer<>(4, Integer.class);
		buff.put(new Integer[] {1,2,3});
		Assert.assertEquals(1, buff.get().intValue());
		Assert.assertEquals(2, buff.get().intValue());
		buff.put(4);
		buff.put(5);
		Assert.assertEquals(3, buff.get().intValue());
		Assert.assertEquals(4, buff.get().intValue());
		Assert.assertEquals(5, buff.get().intValue());
	}

	@Test
	public void testMultiPut() {
		Buffer<Integer> buff = new CircularBuffer<>(20, Integer.class);
		buff.put(new Integer[] {1,2,3,4,5});
		buff.put(new Integer[] {1,2,3,4,5});
		buff.put(new Integer[] {1,2,3,4,5});
		buff.put(new Integer[] {1,2,3,4,5});
		buff.get();
		Assert.assertEquals(1, buff.freeSpace());
	}
	
	@Test
	public void testReFill() {
		Buffer<Integer> buff = new CircularBuffer<>(10, Integer.class);
		buff.put(new Integer[] {1,2,3,4,5});
		buff.put(new Integer[] {6,7,8,9,10});
		int i=0;
		while (i++ < 100000) {
			Integer[] result = new Integer[2];
			Assert.assertEquals(2, buff.get(result));
			Assert.assertArrayEquals(new Integer[] {1,2}, result);
			
			result = new Integer[2];
			Assert.assertEquals(2, buff.get(result));
			Assert.assertArrayEquals(new Integer[] {3,4}, result);
			
			result = new Integer[2];
			Assert.assertEquals(2, buff.get(result));
			Assert.assertArrayEquals(new Integer[] {5,6}, result);
		
			buff.put(new Integer[] {1,2,3,4,5});
			
			result = new Integer[2];
			Assert.assertEquals(2, buff.get(result));
			Assert.assertArrayEquals(new Integer[] {7,8}, result);
			
			result = new Integer[2];
			Assert.assertEquals(2, buff.get(result));
			Assert.assertArrayEquals(new Integer[] {9,10}, result);
			
			buff.put(new Integer[] {6,7,8,9,10});
			
		}
	}

}
