package ollie.utils.state;

public interface StateTransitionListener<T extends Enum<T>> {

	public void onStateTransition(T newState, T previousState);
}
