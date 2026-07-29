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
 * @param <T> the generic type to test against.
 * @param <R> the generic type of the returned value.
 */
public class ConditionalWait<T, R> {

	private CountDownLatch latch = new CountDownLatch(1);
	private R result;
	private WaitCondition<T> condition;
	private boolean cancelled = false;
	private boolean done = false;
	private Exception throwBack;
	
	public ConditionalWait() {}
	
	public ConditionalWait(WaitCondition<T> condition) {
		this.condition = condition;
	}

	public R get(T waitTest, long timeout, TimeUnit unit) throws TimeoutException {
		return get((value) -> doEqualityTest(waitTest, value), timeout, unit);
	}
	
	private boolean doEqualityTest(T waitTest, T value) {
		return ((waitTest == null && value == null) || (waitTest != null && waitTest.equals(value)));
	}
	
	public R get(long timeout, TimeUnit unit) throws TimeoutException {
		return get((WaitCondition<T>)null, timeout, unit);
	}
	
	public R get() {
		try {
			return get((WaitCondition<T>)null, -1, null);
		} catch (TimeoutException e) {
			// this will never happen.
			return null;
		}
	}
	
	public R get(WaitCondition<T> condition, long timeout, TimeUnit unit) throws TimeoutException {
		try {
			if (condition != null) {
				this.condition = condition;
			}
			if (this.condition == null) {
				throw new NullPointerException("Wait condition must not be null");
			}
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
		if (throwBack != null) {
			throw new RuntimeException(throwBack);
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
	
	public R get(WaitCondition<T> condition) {
		try {
			return get(condition, -1L, null);
		} catch (TimeoutException e) {
			// this will never happen.
			return null;
		}
	}

	public void test(T test, R result) {
		try {
			if (condition.checkCondition(test)) {
				this.result = result;
				latch.countDown();
				done = true;
			}
		} catch (Exception e) {
			throwBack = e;
			latch.countDown();
			done = true;
		}
	}
	
	public boolean cancel() {
		cancelled = true;
		latch.countDown();
		return true;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public boolean isDone() {
		return done || cancelled;
	}
}
