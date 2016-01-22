package ollie.utils;

import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;

public class SimpleDeBounce<T, R> {

	private final int timeout;
	private AtomicLong lastInvoked = new AtomicLong(0L);
	
	public SimpleDeBounce(int timeout) {
		this.timeout = timeout;
	}
	
	public R invoke(T arg, Function<T, R> function) {
		if ((System.currentTimeMillis() + timeout) / timeout > lastInvoked.get()) {
			R r = function.apply(arg);
			lastInvoked.set((System.currentTimeMillis() + timeout) / timeout);
			return r;
		}
		return null;
	}
	
	public void invoke(Runnable function) {
		if ((System.currentTimeMillis() + timeout) / timeout > lastInvoked.get()) {
			function.run();
			lastInvoked.set((System.currentTimeMillis() + timeout) / timeout);
		}
	}

}
