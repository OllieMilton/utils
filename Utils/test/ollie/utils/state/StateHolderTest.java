package ollie.utils.state;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class StateHolderTest {

	enum TestState {ONE, TWO, THREE}
	
	@Test
	public void testWaitForState() throws Exception {
		StateHolder<TestState> sh = new StateHolder<>(TestState.ONE);
		final AtomicBoolean threadOne = new AtomicBoolean(false);
		final AtomicBoolean threadTwo = new AtomicBoolean(false);
		final AtomicBoolean threadThree = new AtomicBoolean(false);
		new Thread(() -> {
			try {
				sh.waitForState((value) -> value == TestState.THREE, 2, TimeUnit.SECONDS);
				threadOne.set(true);
			} catch (Exception e) {
				
			}
		}).start();
		
		new Thread(() -> {
			try {
				sh.waitForState((value) -> value == TestState.THREE, 2, TimeUnit.SECONDS);
				threadTwo.set(true);
			} catch (Exception e) {
				
			}
		}).start();
		
		new Thread(() -> {
			try {
				sh.waitForState((value) -> value == TestState.THREE, 2, TimeUnit.SECONDS);
				threadThree.set(true);
			} catch (Exception e) {
				
			}
		}).start();
		
		Thread.sleep(200);
		sh.transition(TestState.TWO);
		Thread.sleep(200);
		sh.transition(TestState.THREE);
		
		Thread.sleep(200);
		Assert.assertTrue("ThreadOne false", threadOne.get());
		Assert.assertTrue("ThreadTwo false", threadTwo.get());
		Assert.assertTrue("ThreadThree false", threadThree.get());
		
	}
	
	@Test
	public void alreadyInState() throws TimeoutException {
		StateHolder<TestState> sh = new StateHolder<>(TestState.ONE);
		sh.waitForState((value) -> value == TestState.ONE, 1, TimeUnit.NANOSECONDS);
	}
}
