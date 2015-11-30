package ollie.utils.logging.l4j;

import java.util.LinkedList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import ollie.utils.logging.SomeOtherLoggingClass;

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
	
	@Test
	public void testSetLogLevel() {
		SomeShitClass shit = new SomeShitClass();
		SomeOtherLoggingClass other = new SomeOtherLoggingClass();
		Logger.getRootLogger().setLevel(Level.TRACE);
		LinkedList<LogEntry> logEntries = new LinkedList<>();
		new LogEntryAppender("LogEntryAppenderTest", (entry) -> logEntries.add(entry));
		logger.debug("starting shit logger...");
		shit.log();
		other.logTrace();
		Assert.assertEquals(3, logEntries.size());
		Assert.assertEquals("DEBUG", logEntries.get(0).getLevel());
		Assert.assertEquals("INFO", logEntries.get(1).getLevel());
		Assert.assertEquals("TRACE", logEntries.get(2).getLevel());
		
		Assert.assertEquals(getClass().getName(), logEntries.get(0).getLoggingClass());
		Assert.assertEquals(shit.getClass().getName(), logEntries.get(1).getLoggingClass());
		Assert.assertEquals("ollie.utils.logging", logEntries.get(2).getLoggingClass());
		
		Assert.assertEquals("starting shit logger...", logEntries.get(0).getMessage());
		Assert.assertEquals("shit", logEntries.get(1).getMessage());
		Assert.assertEquals("A trace message from some other class.", logEntries.get(2).getMessage());
		
		Assert.assertEquals("LogEntryAppenderTest", logEntries.get(0).getIdentifier());
		Assert.assertEquals("LogEntryAppenderTest", logEntries.get(1).getIdentifier());
		Assert.assertEquals("LogEntryAppenderTest", logEntries.get(2).getIdentifier());
	}
	
	private class SomeShitClass {
		private Log logger = LogFactory.getLog(getClass());
		
		public void log() {
			logger.info("shit");
		}
	}
}
