package ollie.utils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public class QueuedDeBounce<T, R> {

	private final int timeout;
	private final AtomicLong lastInvoked = new AtomicLong(0L);
	private ExecutorService exe = Executors.newSingleThreadExecutor();
	
	public QueuedDeBounce(int timeout) {
		this.timeout = timeout;
	}
	
	public R invoke(T arg, Function<T, R> function) {
		Future<R> f = exe.submit(new Callable<R>() {

			@Override
			public R call() throws Exception {
				while (!((System.currentTimeMillis() + timeout) / timeout > lastInvoked.get())) {
					Thread.sleep(50);
				}
				R r = function.apply(arg);
				lastInvoked.set((System.currentTimeMillis() + timeout) / timeout);
				return r;
			}
		});
		
		try {
			return f.get();
		} catch (InterruptedException | ExecutionException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void invoke(Runnable function) {
		exe.execute(new Runnable() {
			
			@Override
			public void run() {
				while (!((System.currentTimeMillis() + timeout) / timeout > lastInvoked.get())) {
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				}
				function.run();
				lastInvoked.set((System.currentTimeMillis() + timeout) / timeout);
			}
		});
	}
}
