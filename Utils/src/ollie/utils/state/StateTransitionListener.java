package ollie.utils.state;

@FunctionalInterface
public interface StateTransitionListener<T extends Enum<T>> {

	public void onStateTransition(T newState, T previousState);
}
