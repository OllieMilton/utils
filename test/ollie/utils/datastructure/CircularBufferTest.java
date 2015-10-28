package ollie.utils.datastructure;

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
		
	}
}
