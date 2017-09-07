package ollie.utils.concurrent;

import java.io.Closeable;

public class VolatileReference<T> {

	private ThreadLocal<Reference<T>> tl = new ThreadLocal<>();
	private volatile T ref;
	
	public VolatileReference(T ref) {
		this.ref = ref;
	}
	
	public VolatileReference() {}
	
	public void set(T ref) {
		this.ref = ref;
	}
	
	public Reference<T> get() {
		if (tl.get() == null) {
			tl.set(new Reference<>(ref, this));
		}
		return tl.get();
	}

	public static class Reference<T> implements Closeable {
		private final T ref;
		private VolatileReference<T> vr;
		
		private Reference(T ref, VolatileReference<T> vr) {
			this.ref = ref;
			this.vr = vr;
		}
		
		public T get() {
			return ref;
		}
		
		@Override
		public void close() {
			vr.tl.remove();
		}
	}
	
}
