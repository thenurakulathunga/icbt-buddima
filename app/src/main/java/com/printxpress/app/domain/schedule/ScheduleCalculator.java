package com.printxpress.app.domain.schedule;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.MonthDay;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Works out which pickup or delivery dates are allowed (BR-6, FR-2.10).
 * <p>
 * The earliest date is today plus the lead time (turnaround, plus a day for delivery). Sundays
 * and the fixed-date public holidays below are never offered. Lunar Poya holidays move every
 * year, so they are not hard-coded; the list can be extended through the constructor.
 * </p>
 *
 * @since 1.0
 */
public class ScheduleCalculator {

    /** Fixed-date Sri Lankan public holidays: Independence Day, New Year (2 days), May Day, Christmas. */
    public static final Set<MonthDay> FIXED_HOLIDAYS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            MonthDay.of(2, 4), MonthDay.of(4, 13), MonthDay.of(4, 14), MonthDay.of(5, 1), MonthDay.of(12, 25))));

    /** Supplies the current date (injectable for tests). */
    private final Supplier<LocalDate> today;
    /** Closed days. */
    private final Set<MonthDay> holidays;

    /** Creates a calculator using the system date and the fixed holidays. */
    public ScheduleCalculator() {
        this(LocalDate::now, FIXED_HOLIDAYS);
    }

    /**
     * Creates a calculator with an injected clock and holiday list.
     *
     * @param today    supplies the current date
     * @param holidays closed month-days
     */
    public ScheduleCalculator(Supplier<LocalDate> today, Set<MonthDay> holidays) {
        this.today = today;
        this.holidays = holidays;
    }

    /** @return the current date */
    public LocalDate today() {
        return today.get();
    }

    /**
     * Returns the first allowed date for a lead time.
     *
     * @param leadDays days needed before pickup or delivery
     * @return the earliest open date on or after today plus the lead time
     */
    public LocalDate earliestDate(int leadDays) {
        LocalDate date = today().plusDays(Math.max(0, leadDays));
        while (isClosed(date)) {
            date = date.plusDays(1);
        }
        return date;
    }

    /**
     * Checks whether a date may be chosen.
     *
     * @param date     the candidate
     * @param leadDays the lead time
     * @return true when on or after the earliest date and not closed
     */
    public boolean isAvailable(LocalDate date, int leadDays) {
        return date != null && !date.isBefore(today().plusDays(Math.max(0, leadDays))) && !isClosed(date);
    }

    /**
     * Checks for Sundays and holidays.
     *
     * @param date the date
     * @return true when branches are closed
     */
    public boolean isClosed(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SUNDAY || holidays.contains(MonthDay.from(date));
    }

    /**
     * Lists consecutive calendar days for the date strip, starting at the earliest date so that
     * closed days appear disabled rather than disappearing.
     *
     * @param leadDays the lead time
     * @param count    the number of days to list
     * @return the days in order
     */
    public List<LocalDate> window(int leadDays, int count) {
        List<LocalDate> days = new ArrayList<>(count);
        LocalDate start = earliestDate(leadDays);
        for (int i = 0; i < count; i++) {
            days.add(start.plusDays(i));
        }
        return days;
    }
}
