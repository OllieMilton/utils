package ollie.utils.concurrent;

@FunctionalInterface
public interface WaitCondition<T> {

	boolean checkCondition(T value);
}
