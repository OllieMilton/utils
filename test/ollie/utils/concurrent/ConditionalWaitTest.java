package ollie.utils.concurrent;

import java.util.concurrent.CancellationException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class ConditionalWaitTest {

	enum TestState {ONE, TWO, THREE}
	
	@Test
	public void doWait() {
		ConditionalWait<TestState, String> condWait = new ConditionalWait<>();
		new Thread(() -> {
			try {
				Thread.sleep(200);
				condWait.test(TestState.ONE, "ONE");
				Thread.sleep(200);
				condWait.test(TestState.TWO, "TWO");
				Thread.sleep(200);
				condWait.test(TestState.THREE, "THREE");
			} catch (Exception e) {
			
			}
		}).start();
		Assert.assertEquals("THREE", condWait.get(TestState.THREE));
	}
	
	@Test
	public void doWaitWithTimeout() throws TimeoutException {
		ConditionalWait<TestState, String> condWait = new ConditionalWait<>();
		new Thread(() -> {
			try {
				Thread.sleep(200);
				condWait.test(TestState.ONE, "ONE");
				Thread.sleep(200);
				condWait.test(TestState.TWO, "TWO");
				Thread.sleep(200);
				condWait.test(TestState.THREE, "THREE");
			} catch (Exception e) {
			
			}
		}).start();
		Assert.assertEquals("THREE", condWait.get(TestState.THREE, 2, TimeUnit.SECONDS));
	}
	
	@Test(expected=TimeoutException.class)
	public void timeout() throws TimeoutException {
		ConditionalWait<TestState, String> condWait = new ConditionalWait<>();
		new Thread(() -> {
			try {
				Thread.sleep(200);
				condWait.test(TestState.ONE, "ONE");
				Thread.sleep(200);
				condWait.test(TestState.TWO, "TWO");
				Thread.sleep(200);
				condWait.test(TestState.THREE, "THREE");
			} catch (Exception e) {
			
			}
		}).start();
		Assert.assertEquals("THREE", condWait.get(TestState.THREE, 10, TimeUnit.MILLISECONDS));
	}
	
	@Test(expected=CancellationException.class)
	public void cancelled() {
		ConditionalWait<TestState, String> condWait = new ConditionalWait<>();
		new Thread(() -> {
			try {
				Thread.sleep(300);
				condWait.cancel();
			} catch (Exception e) {
			
			}
		}).start();
		condWait.get(TestState.THREE);
	}
	
	public void lambdaTest() throws TimeoutException {
		ConditionalWait<TestState, String> condWait = new ConditionalWait<>();
		new Thread(() -> {
			try {
				Thread.sleep(200);
				condWait.test(TestState.ONE, "ONE");
				Thread.sleep(200);
				condWait.test(TestState.TWO, "TWO");
				Thread.sleep(200);
				condWait.test(TestState.THREE, "THREE");
			} catch (Exception e) {
			
			}
		}).start();
		Assert.assertEquals("THREE", condWait.get((value) -> value == TestState.THREE, 10, TimeUnit.MILLISECONDS));
	}
	
}
