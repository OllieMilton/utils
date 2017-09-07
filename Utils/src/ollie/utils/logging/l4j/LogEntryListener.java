package ollie.utils.logging.l4j;

@FunctionalInterface
public interface LogEntryListener {

	void onLogEntry(LogEntry entry);
}
