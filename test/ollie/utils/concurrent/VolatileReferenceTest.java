package ollie.utils.concurrent;

import java.io.FileNotFoundException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ollie.utils.concurrent.VolatileReference.Reference;

@RunWith(JUnit4.class)
public class VolatileReferenceTest {

	private VolatileReference<String> vref = new VolatileReference<>("test1");
	
	@Test
	public void testRef() {
		try (Reference<String> ref = vref.get()) {
			Assert.assertEquals("test1", ref.get());
			vref.set("test2");
			Assert.assertEquals("test1", ref.get());
		}
		Assert.assertEquals("test2", vref.get().get());
	}

}
