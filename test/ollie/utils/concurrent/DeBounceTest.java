package ollie.utils.concurrent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ollie.utils.concurrent.QueuedDeBounce;
import ollie.utils.concurrent.SimpleDeBounce;

@RunWith(JUnit4.class)
public class DeBounceTest {

	private void someFunction() {
		System.out.println("invoked "+System.currentTimeMillis() /1000);
	}
	
	private String someOtherFunction(Integer i) {
		System.out.println("invoked "+System.currentTimeMillis() /1000);
		return "";
	}
	
	@Test
	public void deBounce() {
		SimpleDeBounce<Void, Void> db = new SimpleDeBounce<>(500);
		for (;;)
			db.invoke(() -> someFunction());
	}
	
	@Test
	public void deQueuedBounce() {
		QueuedDeBounce<Integer, String> db = new QueuedDeBounce<>(1000);
		for (int i=0; i< 100; i++)
			db.invoke(99, (ine) -> someOtherFunction(ine));
	}
}
