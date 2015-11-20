package ollie.utils.concurrent;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * {@code ConditionalWait} makes a given thread wait until some condition becomes true.
 * 
 * @author Ollie
 *
 * @param <T>
 */
public class ConditionalWait<T, R> {

	private CountDownLatch latch = new CountDownLatch(1);
	private T waitTest;
	private R result;
	private boolean cancelled = false;
	private boolean done = false;

	public R get(T waitTest, long timeout, TimeUnit unit) throws TimeoutException {
		try {
			this.waitTest = waitTest;
			if (unit == null) {
				latch.await();
			} else {
				if (!latch.await(timeout, unit)) {
					throw new TimeoutException();
				}
			}
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		if (cancelled) {
			throw new CancellationException("Error - wait cancelled.");
		}
		R r = result;
		result = null;
		return r;
	}

	public R get(T waitTest) {
		try {
			return get(waitTest, -1L, null);
		} catch (TimeoutException e) {
			// this will never happen.
			return null;
		}
	}

	public void test(T test, R result) {
		if ((waitTest == null && test == null) || (waitTest != null && waitTest.equals(test))) {
			latch.countDown();
			done = true;
			this.result = result;
		}
	}

	public boolean cancel(boolean mayInterruptIfRunning) {
		latch.countDown();
		cancelled = true;
		return true;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public boolean isDone() {
		return done || cancelled;
	}
}
