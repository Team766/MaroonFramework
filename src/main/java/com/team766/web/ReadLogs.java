package com.team766.web;

import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Stream;
import com.team766.logging.Category;
import com.team766.logging.LogEntry;
import com.team766.logging.LogEntryRenderer;
import com.team766.logging.LogReader;
import com.team766.logging.LogWriter;
import com.team766.logging.Severity;

public class ReadLogs implements WebServer.Handler {
	private static final String ENDPOINT = "/readlogs";
	private static final String ALL_ERRORS_NAME = "All Errors";
	private static final int ENTRIES_PER_PAGE = 100;

	private HashMap<String, LogReader> logReaders = new HashMap<String, LogReader>();
	private HashMap<String, String> readerDescriptions = new HashMap<String, String>();
	private HashMap<String, Iterator<LogEntry>> readerStreams = new HashMap<String, Iterator<LogEntry>>();

	private static String makeLogEntriesTable(final LogReader reader,
			final Iterator<LogEntry> entries) {
		String r = "<table id=\"log-entries\" border=\"1\">\n";
		for (int i = 0; i < ENTRIES_PER_PAGE; ++i) {
			if (!entries.hasNext()) {
				break;
			}
			LogEntry entry = entries.next();
			r += String.format(
					"<tr><td style=\"white-space: pre\">%s</td><td style=\"white-space: pre\">%s</td><td style=\"white-space: pre\">%s</td><td style=\"white-space: pre\">%s</td></tr>\n",
					entry.getCategory(), entry.getTime(), entry.getSeverity(),
					LogEntryRenderer.renderLogEntry(entry, reader));
		}
		r += "</table>";
		return r;
	}

	private String makePage(final String id) {
		String r = String.join("\n",
				new String[] {
						LogWriter.logFilePathBase == null
								? ""
								: "<p>Free disk space: " + NumberFormat.getNumberInstance(Locale.US)
										.format(new File(LogWriter.logFilePathBase)
												.getUsableSpace())
										+ "</p>",
						"<form action=\"" + ENDPOINT + "\"><p>",
						HtmlElements.buildDropDown("logFile", "",
								LogWriter.logFilePathBase == null ? Stream.<String>of()::iterator
										: Arrays.stream(new File(LogWriter.logFilePathBase)
												.list())::iterator),
						HtmlElements.buildDropDown("category", "",
								Stream.concat(Stream.of("", ALL_ERRORS_NAME),
										Arrays.stream(Category.values())
												.map(Category::name))::iterator),
						"<input type=\"submit\" value=\"Open Log\">", "</p></form>",});
		if (id != null) {
			r += String.join("\n", new String[] {"<h1>Log: " + readerDescriptions.get(id) + "</h1>",
					makeLogEntriesTable(logReaders.get(id), readerStreams.get(id)),
					"<input type=\"button\" onclick=\"window.location = '" + ENDPOINT + "?id=" + id
							+ "'; this.disabled=true; this.value='Loading...';\" value=\"Next page\" />", });
		}
		return r;
	}

	private String makeReader(final String logFile, final String description,
			final Function<Stream<LogEntry>, Stream<LogEntry>> filter) {
		LogReader reader;
		try {
			reader = new LogReader(new File(LogWriter.logFilePathBase, logFile).getAbsolutePath());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		String id = Integer.toString(logReaders.size());
		logReaders.put(id, reader);
		readerDescriptions.put(id, logFile + " " + description);
		readerStreams.put(id, filter.apply(Stream.generate(() -> {
			try {
				return reader.readNext();
			} catch (EOFException e) {
				return null;
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}).takeWhile(e -> e != null)).iterator());
		return id;
	}

	private String makeUnfilteredReader(final String logFile) {
		return makeReader(logFile, "", s -> s);
	}

	private String makeCategoryReader(final String logFile, final Category category) {
		return makeReader(logFile, category.name(),
				s -> s.filter(e -> e.getCategory() == category));
	}

	private String makeAllErrorsReader(final String logFile) {
		return makeReader(logFile, ALL_ERRORS_NAME,
				s -> s.filter(entry -> entry.getSeverity() == Severity.ERROR));
	}

	@Override
	public String endpoint() {
		return ENDPOINT;
	}

	@Override
	public String handle(final Map<String, Object> params) {
		String id = (String) params.get("id");
		String logFile = (String) params.get("logFile");
		String categoryName = (String) params.get("category");
		if (!logReaders.containsKey(id)) {
			id = null;
		}
		if (logFile != null) {
			if (categoryName == null || categoryName.equals("")) {
				id = makeUnfilteredReader(logFile);
			} else if (categoryName.equals(ALL_ERRORS_NAME)) {
				id = makeAllErrorsReader(logFile);
			} else {
				Category category = Enum.valueOf(Category.class, categoryName);
				id = makeCategoryReader(logFile, category);
			}
		}
		return makePage(id);
	}

	@Override
	public String title() {
		return "Log Reader";
	}
}
