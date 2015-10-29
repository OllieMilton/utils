package ollie.utils.datastructure;

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

}
