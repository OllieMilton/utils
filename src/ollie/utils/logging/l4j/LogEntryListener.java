package ollie.utils.logging.l4j;

import ollie.utils.logging.l4j.LogEntry;

public interface LogEntryListener {

	void onLogEntry(LogEntry entry);
}
