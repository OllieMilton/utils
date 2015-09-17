package ollie.utils.state;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * A thread safe container for some 'state' enum. Methods are provided for transitioning 
 * state in a thread safe manor using a reentrant read write lock.
 * Additionally a listeners can be supplied so that some action can hang off 
 * a state transition - not that the listener is invoked from inside the write lock.
 * A set of terminal states can also be supplied, once any of the terminal states have been reached 
 * any call to transition the state where the incoming state is not a terminal will result in an 
 * {@code IllegalStateException} until state has been reset via the method {@code reset()}.
 *
 *  
 * @author Ollie
 *
 * @param <T> the generic type - must extend enum.
 */
public class StateHolder<T extends Enum<T>> {

	private Set<T> terminalStates;
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
		terminalStates = new HashSet<>();
	}
	
	/**
	 * Constructs a new state holder with the given initial state and terminal state.
	 * @param initialState - the initial state.
	 * @param terminalState - the terminal state.
	 */
	@SafeVarargs
	public StateHolder(T initialState, T...terminalStates) {
		this(initialState);
		this.terminalStates.addAll(Arrays.asList(terminalStates));
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
	@SafeVarargs
	public StateHolder(StateTransitionListener<T> listener, T initialState, T...terminalStates) {
		this(initialState, terminalStates);
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
			checkTerminal(newState);
			tryTransition(newState);
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
			checkTerminal(newState);
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
	
	private void checkTerminal(T newState) {
		if (!terminalStates.isEmpty() && !terminalStates.contains(newState) && terminalStates.contains(currentState)) {
			throw new IllegalStateException("Cannot transition to state ["+newState+"] - terminal state ["+currentState+"] has been reached.");
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
	
	/**
	 * Attempts to transition to the given state if not in the terminal state.
	 * @param newState - the state to transition to.
	 * @return true if successfully transitioned.
	 */
	public boolean tryTransition(T newState) {
		boolean result = false;
		lock.writeLock().lock();
		try {
			// no terminal states configured...
			if (terminalStates.isEmpty() || 
					// new state is terminal we don't care what current is...
					(terminalStates.contains(newState)) ||
					// or new state is non terminal and current state is non terminal.
					(!terminalStates.contains(currentState))) {
				previousState = currentState;
				currentState = newState;
				result = true;
				if (listener != null) {
					listener.onStateTransition(newState, previousState);
				}
			}
		} finally {
			lock.writeLock().unlock();
		}
		return result;
	}
}
