package ollie.utils.logging.l4j;

import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ollie.utils.logging.SomeOtherLoggingClass;
import ollie.utils.logging.l4j.LogEntry;
import ollie.utils.logging.l4j.LogEntryAppender;

@RunWith(JUnit4.class)
public class LogEntryAppenderTest {

	private Log logger = LogFactory.getLog(getClass());
	
	@Test
	public void testAppender() {
		SomeShitClass shit = new SomeShitClass();
		LinkedList<LogEntry> logEntries = new LinkedList<>();
		new LogEntryAppender("LogEntryAppenderTest", (entry) -> logEntries.add(entry), getClass(), shit.getClass());
		logger.debug("starting shit logger...");
		shit.log();
		
		Assert.assertEquals(2, logEntries.size());
		Assert.assertEquals("DEBUG", logEntries.get(0).getLevel());
		Assert.assertEquals("INFO", logEntries.get(1).getLevel());
		
		Assert.assertEquals(getClass().getName(), logEntries.get(0).getLoggingClass());
		Assert.assertEquals(shit.getClass().getName(), logEntries.get(1).getLoggingClass());
		
		Assert.assertEquals("starting shit logger...", logEntries.get(0).getMessage());
		Assert.assertEquals("shit", logEntries.get(1).getMessage());
		
		Assert.assertEquals("LogEntryAppenderTest", logEntries.get(0).getIdentifier());
		Assert.assertEquals("LogEntryAppenderTest", logEntries.get(1).getIdentifier());
		
	}
	
	@Test
	public void testAppenderAllCurrentLoggers() {
		SomeShitClass shit = new SomeShitClass();
		@SuppressWarnings("unused")
		SomeOtherLoggingClass other = new SomeOtherLoggingClass();
		LinkedList<LogEntry> logEntries = new LinkedList<>();
		new LogEntryAppender("LogEntryAppenderTest", (entry) -> logEntries.add(entry));
		logger.debug("starting shit logger...");
		shit.log();
		
		Assert.assertEquals(2, logEntries.size());
		Assert.assertEquals("DEBUG", logEntries.get(0).getLevel());
		Assert.assertEquals("INFO", logEntries.get(1).getLevel());
		
		Assert.assertEquals(getClass().getName(), logEntries.get(0).getLoggingClass());
		Assert.assertEquals(shit.getClass().getName(), logEntries.get(1).getLoggingClass());
		
		Assert.assertEquals("starting shit logger...", logEntries.get(0).getMessage());
		Assert.assertEquals("shit", logEntries.get(1).getMessage());
		
		Assert.assertEquals("LogEntryAppenderTest", logEntries.get(0).getIdentifier());
		Assert.assertEquals("LogEntryAppenderTest", logEntries.get(1).getIdentifier());
		
	}
	
	private class SomeShitClass {
		private Log logger = LogFactory.getLog(getClass());
		
		public void log() {
			logger.info("shit");
		}
	}
}
