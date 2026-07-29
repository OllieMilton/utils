package ollie.utils.concurrent;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class DeBounceTest {

	@Test
	public void simpleDeBounceDropsCallsInsideTheWindow() {
		SimpleDeBounce<Void, Void> db = new SimpleDeBounce<>(60000);
		AtomicInteger invocations = new AtomicInteger(0);
		for (int i = 0; i < 100; i++) {
			db.invoke(() -> invocations.incrementAndGet());
		}
		Assert.assertEquals(1, invocations.get());
	}

	@Test
	public void simpleDeBounceReturnsNullForADroppedCall() {
		SimpleDeBounce<Integer, String> db = new SimpleDeBounce<>(60000);
		Assert.assertEquals("first", db.invoke(1, (i) -> "first"));
		Assert.assertNull(db.invoke(2, (i) -> "second"));
	}

	@Test
	public void simpleDeBounceInvokesAgainInTheNextWindow() throws Exception {
		SimpleDeBounce<Void, Void> db = new SimpleDeBounce<>(200);
		AtomicInteger invocations = new AtomicInteger(0);
		db.invoke(() -> invocations.incrementAndGet());
		Thread.sleep(450);
		db.invoke(() -> invocations.incrementAndGet());
		Assert.assertEquals(2, invocations.get());
	}

	/**
	 * The queued variant must run every call, waiting for the next wall
	 * clock window rather than dropping, so consecutive invocations land in
	 * distinct windows.
	 */
	@Test(timeout = 10000)
	public void queuedDeBounceQueuesRatherThanDropping() {
		int timeout = 300;
		QueuedDeBounce<Integer, Long> db = new QueuedDeBounce<>(timeout);
		AtomicInteger invocations = new AtomicInteger(0);
		long first = db.invoke(1, (i) -> { invocations.incrementAndGet(); return System.currentTimeMillis(); });
		long second = db.invoke(2, (i) -> { invocations.incrementAndGet(); return System.currentTimeMillis(); });
		Assert.assertEquals(2, invocations.get());
		Assert.assertTrue("Second invocation should have run in a later window",
				(second + timeout) / timeout > (first + timeout) / timeout);
	}
}
