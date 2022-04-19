package com.team766.logging;

import com.team766.library.CircularBuffer;
import edu.wpi.first.wpilibj.DataLogManager;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;

public final class Logger {

    private static boolean alsoLogToDataLog = false;

    private static class LogUncaughtException implements Thread.UncaughtExceptionHandler {
        public void uncaughtException(final Thread t, final Throwable e) {
            e.printStackTrace();

            LoggerExceptionUtils.logException(e);

			if (LogWriter.instance != null) {
                try {
					LogWriter.instance.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

            System.exit(1);
        }
    }

	private static final int MAX_NUM_RECENT_ENTRIES = 1000;

    private static EnumMap<Category, Logger> m_loggers =
            new EnumMap<Category, Logger>(Category.class);
    private CircularBuffer<LogEntry> m_recentEntries =
            new CircularBuffer<LogEntry>(MAX_NUM_RECENT_ENTRIES);
    private static Object s_lastWriteTimeSync = new Object();
    private static long s_lastWriteTime = 0L;
	private Severity s_minSeverity = Severity.INFO;

    static {
        for (Category category : Category.values()) {
            m_loggers.put(category, new Logger(category));
        }

        Thread.setDefaultUncaughtExceptionHandler(new LogUncaughtException());
    }

    public static void enableLoggingToDataLog(boolean enabled) {
        alsoLogToDataLog = enabled;
    }

    public static Logger get(final Category category) {
        return m_loggers.get(category);
    }

	public void setSeverityFilter(Severity threshold) {
		s_minSeverity = threshold;
	}

    static long getTime() {
        long nowNanosec = new Date().getTime() * 1000000;
        synchronized (s_lastWriteTimeSync) {
            // Ensure that log entries' timestamps are unique. This is important
            // because the log viewer uses an entry's timestamp as a unique ID,
            // and we don't want two different log entries to accidentally
            // compare as equal.
			nowNanosec = s_lastWriteTime = Math.max(nowNanosec, s_lastWriteTime + 1);
        }
        return nowNanosec;
    }

    private final Category m_category;

    private Logger(final Category category) {
        m_category = category;
    }

    public Collection<LogEntry> recentEntries() {
        return Collections.unmodifiableCollection(m_recentEntries);
    }

	public void logData(final Severity severity, final String format, final Object... args) {
		if (severity.compareTo(s_minSeverity) < 0) {
			return;
		}
		var entry =
				LogEntry.newBuilder()
                        .setTime(getTime())
                        .setSeverity(severity)
                        .setCategory(m_category)
						.setMessageStr(format);
        String message = String.format(format, args);

        for (Object arg : args) {
            var logValue = LogValue.newBuilder();
            SerializationUtils.valueToProto(arg, logValue);
            entry.addArg(logValue.build());
        }
		LogEntry logEntry = LogWriter.instance.logStoredFormat(entry);
		m_recentEntries.add(logEntry);
		if (alsoLogToDataLog) {
			DataLogManager.log(message);
		}
    }

	public void logRaw(final Severity severity, final String message) {
		if (severity.compareTo(s_minSeverity) < 0) {
			return;
		}
		var entry =
				LogEntry.newBuilder()
                        .setTime(getTime())
                        .setSeverity(severity)
                        .setCategory(m_category)
                        .setMessageStr(message)
                        .build();
		LogWriter.instance.log(entry);
        m_recentEntries.add(entry);
        if (alsoLogToDataLog) {
            DataLogManager.log(message);
        }
    }

	void logOnlyInMemory(final Severity severity, final String message) {
		if (severity.compareTo(s_minSeverity) < 0) {
			return;
		}
		var entry =
				LogEntry.newBuilder()
                        .setTime(getTime())
                        .setSeverity(severity)
                        .setCategory(m_category)
                        .setMessageStr(message)
                        .build();
        m_recentEntries.add(entry);
    }
}
