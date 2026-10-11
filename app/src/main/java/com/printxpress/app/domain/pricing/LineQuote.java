package com.printxpress.app.domain.pricing;

import com.printxpress.app.core.common.Money;

/**
 * Immutable price of one configured line: unit price with options, gross amount, bulk discount
 * and line total.
 *
 * @since 1.0
 */
public final class LineQuote {

    /** Unit price including options. */
    private final Money unitPrice;
    /** Unit price times quantity, before bulk discount. */
    private final Money gross;
    /** Bulk discount amount. */
    private final Money bulkDiscount;
    /** Line total after bulk discount. */
    private final Money lineTotal;
    /** Bulk discount percentage applied. */
    private final double bulkPercent;

    /**
     * Creates a quote.
     *
     * @param unitPrice    unit price including options
     * @param gross        amount before bulk discount
     * @param bulkDiscount bulk discount amount
     * @param lineTotal    amount after bulk discount
     * @param bulkPercent  bulk percentage applied
     */
    public LineQuote(Money unitPrice, Money gross, Money bulkDiscount, Money lineTotal, double bulkPercent) {
        this.unitPrice = unitPrice;
        this.gross = gross;
        this.bulkDiscount = bulkDiscount;
        this.lineTotal = lineTotal;
        this.bulkPercent = bulkPercent;
    }

    /** @return the unit price including options */
    public Money getUnitPrice() {
        return unitPrice;
    }

    /** @return the amount before bulk discount */
    public Money getGross() {
        return gross;
    }

    /** @return the bulk discount amount */
    public Money getBulkDiscount() {
        return bulkDiscount;
    }

    /** @return the amount after bulk discount */
    public Money getLineTotal() {
        return lineTotal;
    }

    /** @return the bulk percentage applied */
    public double getBulkPercent() {
        return bulkPercent;
    }
}
