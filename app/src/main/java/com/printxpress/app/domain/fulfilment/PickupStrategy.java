package com.printxpress.app.domain.fulfilment;

import com.printxpress.app.core.common.Money;
import com.printxpress.app.data.model.enums.FulfilmentType;
import com.printxpress.app.data.model.enums.PaymentMethod;
import com.printxpress.app.domain.schedule.ScheduleCalculator;

import java.time.LocalDate;

/**
 * Pickup at a branch: no fee, no extra lead time, pay at pickup.
 * <p>Pattern role: Concrete Strategy.</p>
 *
 * @since 1.0
 */
public final class PickupStrategy implements FulfilmentStrategy {

    /** Date rules. */
    private final ScheduleCalculator schedule;

    /**
     * Creates the strategy.
     *
     * @param schedule the date rules
     */
    public PickupStrategy(ScheduleCalculator schedule) {
        this.schedule = schedule;
    }

    /** @return {@link FulfilmentType#PICKUP} */
    @Override
    public FulfilmentType getType() {
        return FulfilmentType.PICKUP;
    }

    /** @return zero */
    @Override
    public Money deliveryFee() {
        return Money.ZERO;
    }

    /** @return zero extra days */
    @Override
    public int extraLeadDays() {
        return 0;
    }

    /** @return false: pickup needs a branch */
    @Override
    public boolean requiresAddress() {
        return false;
    }

    /** @return pay at pickup */
    @Override
    public PaymentMethod paymentMethod() {
        return PaymentMethod.PAY_AT_PICKUP;
    }

    /**
     * Returns the earliest pickup date.
     *
     * @param turnaroundDays the production turnaround
     * @return the earliest open date after the turnaround
     */
    @Override
    public LocalDate earliestDate(int turnaroundDays) {
        return schedule.earliestDate(turnaroundDays + extraLeadDays());
    }
}
