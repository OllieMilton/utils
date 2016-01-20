package ollie.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class MathsTest {

	@Test
	public void convertUp() {
		Assert.assertEquals(1, Maths.convertRange(0.5, 1, 0, 2, 0), 0);
	}
	
	@Test
	public void convertUpNeg() {
		Assert.assertEquals(1, Maths.convertRange(-0.5, -1, 0, 2, 0), 0);
	}
	
	@Test
	public void convertDown() {
		Assert.assertEquals(0.5, Maths.convertRange(1, 2, 0, 1, 0), 0);
	}
	
	@Test
	public void convertDownNeg() {
		Assert.assertEquals(-0.5, Maths.convertRange(1, 2, 0, -1, 0), 0);
	}
}
