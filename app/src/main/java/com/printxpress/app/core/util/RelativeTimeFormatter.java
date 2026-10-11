package com.printxpress.app.core.util;

import android.content.Context;

import com.printxpress.app.R;

import java.time.LocalDateTime;

/**
 * Formats timestamps relative to now: "Just now", "5 min ago", "3 h ago", "2 d ago", then the date.
 *
 * @since 1.0
 */
public final class RelativeTimeFormatter {

    /** Minutes in an hour. */
    private static final long HOUR = 60;
    /** Minutes in a day. */
    private static final long DAY = 24 * HOUR;
    /** Days after which the full date is shown. */
    private static final long WEEK_DAYS = 7;

    /** Hidden constructor: utility class. */
    private RelativeTimeFormatter() {
    }

    /**
     * Formats a timestamp.
     *
     * @param context for strings
     * @param time    the timestamp
     * @return the relative text
     */
    public static String format(Context context, LocalDateTime time) {
        long minutes = DateTimeUtils.minutesBetween(time, LocalDateTime.now());
        if (minutes < 1) {
            return context.getString(R.string.time_just_now);
        }
        if (minutes < HOUR) {
            return context.getString(R.string.time_minutes, (int) minutes);
        }
        if (minutes < DAY) {
            return context.getString(R.string.time_hours, (int) (minutes / HOUR));
        }
        if (minutes < WEEK_DAYS * DAY) {
            return context.getString(R.string.time_days, (int) (minutes / DAY));
        }
        return DateTimeUtils.formatDisplay(time == null ? null : time.toLocalDate());
    }
}
