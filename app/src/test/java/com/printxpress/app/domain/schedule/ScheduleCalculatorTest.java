package com.printxpress.app.domain.schedule;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.printxpress.app.core.common.Money;
import com.printxpress.app.domain.fulfilment.HomeDeliveryStrategy;
import com.printxpress.app.domain.fulfilment.PickupStrategy;

import org.junit.Before;
import org.junit.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * Unit tests for {@link ScheduleCalculator} (BR-6: turnaround, no Sundays, fixed holidays) and
 * the fulfilment strategies' earliest dates (TC-36, TC-37).
 *
 * @since 1.0
 */
public class ScheduleCalculatorTest {

    /** Fixed "today": Tuesday 22 September 2026. */
    private static final LocalDate TODAY = LocalDate.of(2026, 9, 22);

    /** Calculator under test. */
    private ScheduleCalculator schedule;

    /** Pins today and uses the fixed holidays. */
    @Before
    public void setUp() {
        schedule = new ScheduleCalculator(() -> TODAY, ScheduleCalculator.FIXED_HOLIDAYS);
    }

    /** Two-day turnaround from Tuesday gives Thursday. */
    @Test
    public void earliestDate_addsTurnaround() {
        assertEquals(LocalDate.of(2026, 9, 24), schedule.earliestDate(2));
    }

    /** A turnaround landing on Sunday moves to Monday (TC-37). */
    @Test
    public void earliestDate_skipsSunday() {
        assertEquals(DayOfWeek.SUNDAY, TODAY.plusDays(5).getDayOfWeek());
        assertEquals(LocalDate.of(2026, 9, 28), schedule.earliestDate(5));
    }

    /** Fixed holidays are closed. */
    @Test
    public void holidaysAreClosed() {
        assertTrue(schedule.isClosed(LocalDate.of(2026, 12, 25)));
        assertTrue(schedule.isClosed(LocalDate.of(2027, 2, 4)));
        assertFalse(schedule.isClosed(LocalDate.of(2026, 12, 24)));
    }

    /** Dates before the lead time, Sundays and past dates are not available. */
    @Test
    public void isAvailable_rules() {
        assertFalse(schedule.isAvailable(LocalDate.of(2026, 9, 23), 2));
        assertTrue(schedule.isAvailable(LocalDate.of(2026, 9, 24), 2));
        assertFalse(schedule.isAvailable(LocalDate.of(2026, 9, 27), 2));
        assertFalse(schedule.isAvailable(null, 2));
    }

    /** The picker window starts at the earliest date and has the requested length. */
    @Test
    public void window_startsAtEarliest() {
        List<LocalDate> days = schedule.window(2, 14);
        assertEquals(14, days.size());
        assertEquals(schedule.earliestDate(2), days.get(0));
    }

    /** Delivery needs one extra day compared with pickup and carries its fee. */
    @Test
    public void fulfilment_deliveryAddsOneDay() {
        PickupStrategy pickup = new PickupStrategy(schedule);
        HomeDeliveryStrategy delivery = new HomeDeliveryStrategy(schedule, Money.of("350.00"));
        assertEquals(LocalDate.of(2026, 9, 24), pickup.earliestDate(2));
        assertEquals(LocalDate.of(2026, 9, 25), delivery.earliestDate(2));
        assertTrue(pickup.deliveryFee().isZero());
        assertEquals(Money.of("350.00"), delivery.deliveryFee());
        assertTrue(delivery.requiresAddress());
        assertFalse(pickup.requiresAddress());
    }
}
