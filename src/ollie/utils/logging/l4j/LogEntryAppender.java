package ollie.utils.logging.l4j;

import java.time.Instant;

import org.apache.log4j.AppenderSkeleton;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;

public class LogEntryAppender extends AppenderSkeleton {

	private LogEntryListener listener;
	private String identifier;
	
	public LogEntryAppender(String identifier, LogEntryListener listener, Class<?>...classes) {
		this.identifier = identifier;
		this.listener = listener;
		for (Class<?> c : classes) {
			Logger.getLogger(c).addAppender(this);
		}
	}
	
	public LogEntryAppender(String identifier, LogEntryListener listener) {
		this.identifier = identifier;
		this.listener = listener;
		Logger.getRootLogger().addAppender(this);
	}
		
	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#close()
	 */
	@Override
	public void close() {
	
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.Appender#requiresLayout()
	 */
	@Override
	public boolean requiresLayout() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.apache.log4j.AppenderSkeleton#append(org.apache.log4j.spi.LoggingEvent)
	 */
	@Override
	protected void append(LoggingEvent event) {
		LogEntry entry = new LogEntry();
		entry.setIdentifier(identifier);
		entry.setTimeStamp(Instant.ofEpochMilli(event.getTimeStamp()));
		entry.setLevel(event.getLevel().toString());
		entry.setLoggingClass(event.getLoggerName());
		entry.setMessage(event.getMessage().toString());
		listener.onLogEntry(entry);
	}
	
}
