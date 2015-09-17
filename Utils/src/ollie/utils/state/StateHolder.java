package ollie.utils.state;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A thread safe container for some 'state' enum. Methods are provided for transitioning 
 * state in a thread safe manor using a reentrant read write lock.
 * Additionally a listeners can be supplied so that some action can hang off 
 * a state transition - not that the listener is invoked from inside the write lock.
 * A terminal state can also be supplied, once reached any call to transition the state will 
 * result in an {@code IllegalStateException} until state has been reset via the method {@code reset()}.
 *
 *  
 * @author Ollie
 *
 * @param <T> the generic type - must extend enum.
 */
public class StateHolder<T extends Enum<T>> {

	private T terminalState;
	private T initialState;
	private T currentState;
	private T previousState;
	private StateTransitionListener<T> listener;
	private ReadWriteLock lock;
	
	/**
	 * Constructs a new state holder with the given initial state.
	 * @param initialState - the initial state.
	 */
	public StateHolder(T initialState) {
		this.initialState = initialState;
		currentState = initialState;
		lock = new ReentrantReadWriteLock(true);
	}
	
	/**
	 * Constructs a new state holder with the given initial state and terminal state.
	 * @param initialState - the initial state.
	 * @param terminalState - the terminal state.
	 */
	public StateHolder(T initialState, T terminalState) {
		this(initialState);
		this.terminalState = terminalState;
	}
	
	/**
	 * Constructs a new state holder with a listener and the given initial state.
	 * @param listener - the state listener.
	 * @param terminalState - the terminal state.
	 */
	public StateHolder(StateTransitionListener<T> listener, T initialState) {
		this(initialState);
		this.listener = listener;
	}
	
	/**
	 * Constructs a new state holder with a listener and the given initial and terminal states.
	 * @param listener - the state listener.
	 * @param initialState - the initial state.
	 * @param terminalState - the terminal state.
	 */
	public StateHolder(StateTransitionListener<T> listener, T initialState, T terminalState) {
		this(initialState, terminalState);
		this.listener = listener;
	}
	
	/**
	 * Gets the current state.
	 * @return The current state.
	 */
	public T get() {
		lock.readLock().lock();
		try {
			return currentState;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * Gets the state that was previously current.
	 * @return The previous state.
	 */
	public T getPrevious() {
		lock.readLock().lock();
		try {
			return previousState;
		} finally {
			lock.readLock().unlock();
		}
	}
	
	/**
	 * Transitions to the given state.
	 * @param newState - the state to transition to.
	 */
	public void transition(T newState) {
		lock.writeLock().lock();
		try {
			checkTerminal();
			previousState = currentState;
			currentState = newState;
			if (listener != null) {
				listener.onStateTransition(newState, previousState);
			}
		} finally {
			lock.writeLock().unlock();
		}
	}
	
	/**
	 * Transitions to given newState only if current state is equal to given condition.
	 * @param condition - the conditional state.
	 * @param newState - the state to transition to.
	 */
	public void conditionalTransition(T condition, T newState) {
		lock.writeLock().lock();
		try {
			checkTerminal();
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
	
	private void checkTerminal() {
		if (terminalState != null && currentState == terminalState) {
			throw new IllegalStateException("Cannot transition - terminal state ["+terminalState+"] has been reached.");
		}
	}
	
	/**
	 * Resets current state back to the initial state.
	 */
	public void reset() {
		lock.writeLock().lock();
		try {
			currentState = initialState;
		} finally {
			lock.writeLock().unlock();
		}
	}
}
