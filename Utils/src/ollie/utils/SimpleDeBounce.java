package ollie.utils;

import java.util.concurrent.atomic.AtomicLong;

public class SimpleDeBounce {

	private final int timeout;
	private AtomicLong lastInvoked = new AtomicLong(0L);
	
	public SimpleDeBounce(int timeout) {
		this.timeout = timeout;
	}
	
	public void invoke(Runnable function) {
		if ((System.currentTimeMillis() + timeout) / timeout > lastInvoked.get()) {
			lastInvoked.set((System.currentTimeMillis() + timeout) / timeout);
			function.run();
		}
	}

}
