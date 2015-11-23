package ollie.utils.state;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import ollie.utils.concurrent.ConditionalWait;
import ollie.utils.concurrent.WaitCondition;

/**
 * A thread safe container for some 'state' enum. Methods are provided for transitioning 
 * state in a thread safe manor using a reentrant read write lock.
 * Additionally a listeners can be supplied so that some action can hang off 
 * a state transition - note that the listener is invoked from inside the read lock.
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
	private ReentrantReadWriteLock lock;
	private Map<Thread, ConditionalWait<T, T>> waitMap;
	
	/**
	 * Constructs a new state holder with the given initial state.
	 * @param initialState - the initial state.
	 */
	public StateHolder(T initialState) {
		this.initialState = initialState;
		lock = new ReentrantReadWriteLock(true);
		terminalStates = new HashSet<>();
		waitMap = new ConcurrentHashMap<>();
		setState(initialState);
		callListeners(initialState);
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
	 * @return True if the current state is any of the given states.
	 */
	@SafeVarargs
	public final boolean is(T...args) {
		lock.readLock().lock();
		try {
			Set<T> set = new HashSet<>(Arrays.asList(args));
			return set.contains(currentState);
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
			releaseWriteLock();
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
				tryTransition(newState);
			}
		} finally {
			releaseWriteLock();
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
			setState(initialState);
		} finally {
			releaseWriteLock();
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
			// new state must be different and 
			if (newState != currentState) {
				// no terminal states configured...
				if (terminalStates.isEmpty() || 
						// or new state is terminal we don't care what current is...
						(terminalStates.contains(newState)) ||
						// or new state is non terminal and current state is non terminal.
						(!terminalStates.contains(currentState))) {
					setState(newState);
					// down grade the lock then call the listeners
					lock.readLock().lock();
					try {
						releaseWriteLock();
						callListeners(newState);
					} finally {
						lock.readLock().unlock();
					}
					result = true;
				}
			}
		} finally {
			releaseWriteLock();
		}
		return result;
	}
	
	private void releaseWriteLock() {
		if (lock.isWriteLockedByCurrentThread()) {
			lock.writeLock().unlock();
		}
	}
	
	private void setState(T newState) {
		previousState = currentState;
		currentState = newState;
	}
	
	private void callListeners(T newState) {
		for (ConditionalWait<T, T> condWait : waitMap.values()) {
			condWait.test(newState, newState);
		}
		if (listener != null) {
			listener.onStateTransition(newState, previousState);
		}
	}
	
	/**
	 * @return True if current state is a terminal state, false if no terminals states are configured or not in a terminal state.
	 */
	public boolean isInTerminalState() {
		if (!terminalStates.isEmpty()) {
			lock.readLock().lock();
			try {
				return terminalStates.contains(currentState);
			} finally {
				lock.readLock().unlock();
			}
		}
		return false;
	}
	
	/**
	 * Makes the calling thread wait until current state becomes equal to the given state {@code waitTest}.
	 * @param waitTest - the state to wait for.
	 * @param timeout - the amount of time to timeout after.
	 * @param unit - the unit of the timeout amount.
	 * @throws TimeoutException if the timeout expires.
	 */
	public void waitForState(WaitCondition<T> waitTest, long timeout, TimeUnit unit) throws TimeoutException {
		ConditionalWait<T, T> condWait = null;
		lock.readLock().lock();
		try {
			if (waitTest.checkCondition(currentState)) {
				return;
			} else {
				condWait = new ConditionalWait<>();
				waitMap.put(Thread.currentThread(), condWait);
			}
		} finally {
			lock.readLock().unlock();
		}
		condWait.get(waitTest, timeout, unit);
		waitMap.remove(Thread.currentThread());
	}
	
	/**
	 * Makes the calling thread wait until current state becomes equal to the given state {@code waitTest}.
	 * @param waitTest - the state to wait for.
	 */ 
	public void waitForState(WaitCondition<T> waitTest) {
		try {
			waitForState(waitTest, -1L, null);
		} catch (TimeoutException e) {
			// this will never happen.
		}
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "StateHolder current state: "+currentState+", previous state: "+previousState;
	}
}
