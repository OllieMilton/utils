package ollie.utils.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Promise<T> implements Future<T> {

	private CountDownLatch latch = new CountDownLatch(1);
	private T result;
	private boolean cancelled = false;
	private boolean done = false;

	public T get(long timeout, TimeUnit unit) throws TimeoutException {
		try {
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
		T r = result;
		result = null;
		return r;
	}

	public T get() {
		try {
			return get(-1L, null);
		} catch (TimeoutException e) {
			// this will never happen.
			return null;
		}
	}

	public void set(T result) {
		this.result = result;
		latch.countDown();
		done = true;
	}

	@Override
	public boolean cancel(boolean mayInterruptIfRunning) {
		latch.countDown();
		cancelled = true;
		return true;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public boolean isDone() {
		return done || cancelled;
	}
}
