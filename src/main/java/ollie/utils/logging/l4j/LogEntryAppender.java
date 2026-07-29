package ollie.utils.logging.l4j;

import java.io.Serializable;
import java.time.Instant;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.layout.PatternLayout;

public class LogEntryAppender extends AbstractAppender {

	private LogEntryListener listener;
	private String identifier;
	
	/**
	 * Create a new {@code LogEntryAppender} and attaches it to the root logger.
	 * @param identifier - a string that identifies the source of the log messages.
	 * @param listener - the listener.
	 */
	public LogEntryAppender(String identifier, LogEntryListener listener, Filter filter, Layout<? extends Serializable> layout) {
		super(identifier, filter, layout);
		this.identifier = identifier;
		this.listener = listener;
	}
		
	/* (non-Javadoc)
	 * @see org.apache.logging.log4j.core.Appender#append(org.apache.logging.log4j.core.LogEvent)
	 */
	@Override
	public void append(LogEvent event) {
		LogEntry entry = new LogEntry();
		entry.setIdentifier(identifier);
		entry.setTimeStamp(Instant.ofEpochMilli(event.getTimeMillis()));
		entry.setLevel(event.getLevel().toString());
		entry.setLoggingClass(event.getLoggerName());
		entry.setMessage(event.getMessage().getFormattedMessage());
		listener.onLogEntry(entry);
	}
	
	public static void registerAppender(String identifier, LogEntryListener listener) {
		final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        Layout<? extends Serializable> layout = PatternLayout.createDefaultLayout(config);
        Appender app = new LogEntryAppender(identifier, listener, config.getFilter(), layout);
        app.start();
        config.getRootLogger().addAppender(app, Level.ALL, config.getFilter());
	}
		
}
