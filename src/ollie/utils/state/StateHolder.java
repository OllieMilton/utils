package ollie.utils.state;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class StateHolder<T extends Enum<T>> {

	private T currentState;
	private T previousState;
	private StateTransitionListener<T> listener;
	private ReadWriteLock lock;
	
	public StateHolder(T initialState) {
		currentState = initialState;
		lock = new ReentrantReadWriteLock(true);
	}
	
	public StateHolder(StateTransitionListener<T> listener, T initialState) {
		this(initialState);
		this.listener = listener;
	}
	
	public T get() {
		lock.readLock().lock();
		try {
			return currentState;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public T getPrevious() {
		lock.readLock().lock();
		try {
			return previousState;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	public void transition(T newState) {
		lock.writeLock().lock();
		try {
			previousState = currentState;
			currentState = newState;
			if (listener != null) {
				listener.onStateTransition(newState, previousState);
			}
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	public void conditionalTransition(T condition, T newState) {
		lock.writeLock().lock();
		try {
			if (currentState == condition) {
				previousState = currentState;
				currentState = newState;
				if (listener != null) {
					listener.onStateTransition(newState, previousState);
				}
			}
		} finally {
			lock.writeLock().unlock();
		}
	}
}
