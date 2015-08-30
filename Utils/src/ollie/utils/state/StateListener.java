package ollie.utils.state;

public interface StateListener<T extends Enum<T>> {

	public void onStateTransition(T newState, T previousState);
}
