package com.printxpress.app.domain.fulfilment;

import com.printxpress.app.core.common.Constants;
import com.printxpress.app.core.common.Money;
import com.printxpress.app.data.model.enums.FulfilmentType;
import com.printxpress.app.data.model.enums.PaymentMethod;
import com.printxpress.app.domain.schedule.ScheduleCalculator;

import java.time.LocalDate;

/**
 * Home delivery: the district fee, one extra day for the courier, cash on delivery.
 * <p>Pattern role: Concrete Strategy.</p>
 *
 * @since 1.0
 */
public final class HomeDeliveryStrategy implements FulfilmentStrategy {

    /** Date rules. */
    private final ScheduleCalculator schedule;
    /** Delivery fee of the address district. */
    private final Money fee;

    /**
     * Creates the strategy.
     *
     * @param schedule the date rules
     * @param fee      the district delivery fee
     */
    public HomeDeliveryStrategy(ScheduleCalculator schedule, Money fee) {
        this.schedule = schedule;
        this.fee = fee == null ? Money.ZERO : fee;
    }

    /** @return {@link FulfilmentType#DELIVERY} */
    @Override
    public FulfilmentType getType() {
        return FulfilmentType.DELIVERY;
    }

    /** @return the district fee */
    @Override
    public Money deliveryFee() {
        return fee;
    }

    /** @return one day for the courier */
    @Override
    public int extraLeadDays() {
        return Constants.DELIVERY_EXTRA_DAYS;
    }

    /** @return true: delivery needs an address */
    @Override
    public boolean requiresAddress() {
        return true;
    }

    /** @return cash on delivery */
    @Override
    public PaymentMethod paymentMethod() {
        return PaymentMethod.CASH_ON_DELIVERY;
    }

    /**
     * Returns the earliest delivery date.
     *
     * @param turnaroundDays the production turnaround
     * @return the earliest open date after turnaround plus courier time
     */
    @Override
    public LocalDate earliestDate(int turnaroundDays) {
        return schedule.earliestDate(turnaroundDays + extraLeadDays());
    }
}
