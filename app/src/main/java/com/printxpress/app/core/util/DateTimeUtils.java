package com.printxpress.app.core.util;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

/**
 * Date parsing and formatting helpers shared by the data and UI layers.
 * <p>
 * The database stores dates as ISO {@code yyyy-MM-dd} and timestamps as
 * {@code yyyy-MM-dd HH:mm:ss} (SQLite {@code datetime('now','localtime')}), which sort correctly
 * as text. Display format follows docs/05 §9, e.g. {@code Tue, 06 Oct 2026}.
 * </p>
 *
 * @since 1.0
 */
public final class DateTimeUtils {

    /** ISO date used in the database. */
    public static final DateTimeFormatter DB_DATE = DateTimeFormatter.ISO_LOCAL_DATE;
    /** Timestamp format used in the database. */
    public static final DateTimeFormatter DB_DATE_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.US);
    /** Display date, e.g. Tue, 06 Oct 2026. */
    public static final DateTimeFormatter DISPLAY_DATE =
            DateTimeFormatter.ofPattern("EEE, dd MMM yyyy", Locale.ENGLISH);
    /** Display date and time, e.g. 06 Oct 2026, 10:30. */
    public static final DateTimeFormatter DISPLAY_DATE_TIME =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm", Locale.ENGLISH);
    /** Short day name for date chips, e.g. Tue. */
    public static final DateTimeFormatter DAY_NAME = DateTimeFormatter.ofPattern("EEE", Locale.ENGLISH);
    /** Day of month for date chips, e.g. 06. */
    public static final DateTimeFormatter DAY_NUMBER = DateTimeFormatter.ofPattern("dd", Locale.ENGLISH);
    /** Month for date chips, e.g. Oct. */
    public static final DateTimeFormatter MONTH_NAME = DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH);
    /** Compact date used inside order numbers, e.g. 261006. */
    public static final DateTimeFormatter ORDER_NUMBER_DATE = DateTimeFormatter.ofPattern("yyMMdd", Locale.US);

    /** Hidden constructor: utility class. */
    private DateTimeUtils() {
    }

    /**
     * Formats a date for storage.
     *
     * @param date the date
     * @return the ISO text, or null for a null date
     */
    public static String toDbDate(LocalDate date) {
        return date == null ? null : date.format(DB_DATE);
    }

    /**
     * Formats a timestamp for storage.
     *
     * @param dateTime the timestamp
     * @return the database text, or null for a null timestamp
     */
    public static String toDbDateTime(LocalDateTime dateTime) {
        return dateTime == null ? null : dateTime.format(DB_DATE_TIME);
    }

    /**
     * Parses an ISO date from the database.
     *
     * @param text the stored text
     * @return the date, or null when empty or malformed
     */
    public static LocalDate parseDate(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(text.trim().substring(0, Math.min(10, text.trim().length())), DB_DATE);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /**
     * Parses a timestamp from the database.
     *
     * @param text the stored text
     * @return the timestamp, or null when empty or malformed
     */
    public static LocalDateTime parseDateTime(String text) {
        if (text == null || text.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDateTime.parse(text.trim(), DB_DATE_TIME);
        } catch (DateTimeParseException e) {
            LocalDate date = parseDate(text);
            return date == null ? null : date.atStartOfDay();
        }
    }

    /**
     * Formats a date for display.
     *
     * @param date the date
     * @return e.g. Tue, 06 Oct 2026, or an empty string
     */
    public static String formatDisplay(LocalDate date) {
        return date == null ? "" : date.format(DISPLAY_DATE);
    }

    /**
     * Formats a timestamp for display.
     *
     * @param dateTime the timestamp
     * @return e.g. 06 Oct 2026, 10:30, or an empty string
     */
    public static String formatDisplay(LocalDateTime dateTime) {
        return dateTime == null ? "" : dateTime.format(DISPLAY_DATE_TIME);
    }

    /**
     * Returns the whole minutes between two instants, never negative.
     *
     * @param from the earlier timestamp
     * @param to   the later timestamp
     * @return the elapsed minutes
     */
    public static long minutesBetween(LocalDateTime from, LocalDateTime to) {
        if (from == null || to == null) {
            return 0;
        }
        return Math.max(0, Duration.between(from, to).toMinutes());
    }
}
