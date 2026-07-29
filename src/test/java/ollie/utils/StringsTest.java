package ollie.utils;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class StringsTest {

	@Test
	public void isBlank() {
		Assert.assertTrue(Strings.isBlank(null));
		Assert.assertTrue(Strings.isBlank(""));
		Assert.assertFalse(Strings.isBlank("twat"));
	}
	
	@Test
	public void isNotBlank() {
		Assert.assertFalse(Strings.isNotBlank(null));
		Assert.assertFalse(Strings.isNotBlank(""));
		Assert.assertTrue(Strings.isNotBlank("twat"));
	}
	
}
