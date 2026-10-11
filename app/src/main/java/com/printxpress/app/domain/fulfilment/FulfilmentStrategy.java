package com.printxpress.app.domain.fulfilment;

import com.printxpress.app.core.common.Money;
import com.printxpress.app.data.model.enums.FulfilmentType;
import com.printxpress.app.data.model.enums.PaymentMethod;

import java.time.LocalDate;

/**
 * Strategy for how an order reaches the customer: fee, lead time, payment and required details.
 * <p>Pattern role: Strategy (pickup vs home delivery, FR-2.9, BR-8).</p>
 *
 * @since 1.0
 */
public interface FulfilmentStrategy {

    /** @return the fulfilment type this strategy represents */
    FulfilmentType getType();

    /** @return the delivery fee (zero for pickup) */
    Money deliveryFee();

    /** @return extra days added to the production turnaround */
    int extraLeadDays();

    /** @return true when a delivery address is required, false when a branch is */
    boolean requiresAddress();

    /** @return the matching offline payment method */
    PaymentMethod paymentMethod();

    /**
     * Returns the earliest date for a given production turnaround.
     *
     * @param turnaroundDays the longest turnaround among the cart items
     * @return the earliest allowed date
     */
    LocalDate earliestDate(int turnaroundDays);
}
