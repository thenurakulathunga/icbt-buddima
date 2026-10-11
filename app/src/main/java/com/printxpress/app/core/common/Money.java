package com.printxpress.app.core.common;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Immutable value object for Sri Lankan rupee amounts.
 * <p>
 * Uses {@link BigDecimal} with two decimal places and {@link RoundingMode#HALF_UP} so price
 * calculations never suffer floating-point drift. Every operation returns a new instance.
 * </p>
 *
 * @since 1.0
 */
public final class Money implements Comparable<Money> {

    /** Number of decimal places kept for rupee amounts. */
    public static final int SCALE = 2;
    /** Rounding applied after every operation. */
    public static final RoundingMode ROUNDING = RoundingMode.HALF_UP;
    /** Zero rupees. */
    public static final Money ZERO = new Money(BigDecimal.ZERO);

    /** Hundred, used by percentage calculations. */
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    /** The normalised amount. */
    private final BigDecimal amount;

    /**
     * Creates an amount, normalising it to {@link #SCALE} decimal places.
     *
     * @param amount the raw amount, must not be null
     */
    private Money(BigDecimal amount) {
        this.amount = Objects.requireNonNull(amount, "amount").setScale(SCALE, ROUNDING);
    }

    /**
     * Creates money from a decimal value.
     *
     * @param amount the amount in rupees
     * @return the money value
     */
    public static Money of(BigDecimal amount) {
        return new Money(amount);
    }

    /**
     * Creates money from a double, as read from a SQLite REAL column.
     *
     * @param amount the amount in rupees
     * @return the money value
     */
    public static Money of(double amount) {
        return new Money(BigDecimal.valueOf(amount));
    }

    /**
     * Creates money from a decimal string such as {@code "1250.50"}.
     *
     * @param amount the amount as text
     * @return the money value
     * @throws NumberFormatException when the text is not a number
     */
    public static Money of(String amount) {
        return new Money(new BigDecimal(amount));
    }

    /**
     * Adds another amount.
     *
     * @param other the amount to add
     * @return the sum
     */
    public Money plus(Money other) {
        return new Money(amount.add(other.amount));
    }

    /**
     * Subtracts another amount.
     *
     * @param other the amount to subtract
     * @return the difference, which may be negative
     */
    public Money minus(Money other) {
        return new Money(amount.subtract(other.amount));
    }

    /**
     * Multiplies by a whole quantity.
     *
     * @param quantity the multiplier
     * @return the product
     */
    public Money times(int quantity) {
        return new Money(amount.multiply(BigDecimal.valueOf(quantity)));
    }

    /**
     * Multiplies by a decimal factor.
     *
     * @param factor the multiplier
     * @return the product
     */
    public Money times(BigDecimal factor) {
        return new Money(amount.multiply(factor));
    }

    /**
     * Divides by a whole quantity, for example to derive a unit price.
     *
     * @param divisor the divisor, must be positive
     * @return the quotient rounded to two places
     * @throws IllegalArgumentException when the divisor is not positive
     */
    public Money dividedBy(int divisor) {
        if (divisor <= 0) {
            throw new IllegalArgumentException("divisor must be positive");
        }
        return new Money(amount.divide(BigDecimal.valueOf(divisor), SCALE, ROUNDING));
    }

    /**
     * Returns the given percentage of this amount, e.g. {@code percent(10)} of Rs. 200 is Rs. 20.
     *
     * @param percentage the percentage
     * @return the portion of this amount
     */
    public Money percent(double percentage) {
        return new Money(amount.multiply(BigDecimal.valueOf(percentage)).divide(HUNDRED, SCALE, ROUNDING));
    }

    /**
     * Returns the smaller of this amount and another.
     *
     * @param other the amount to compare with
     * @return the minimum
     */
    public Money min(Money other) {
        return compareTo(other) <= 0 ? this : other;
    }

    /**
     * Clamps negative amounts to zero.
     *
     * @return this amount, or zero when negative
     */
    public Money nonNegative() {
        return isNegative() ? ZERO : this;
    }

    /** @return true when the amount is below zero */
    public boolean isNegative() {
        return amount.signum() < 0;
    }

    /** @return true when the amount is exactly zero */
    public boolean isZero() {
        return amount.signum() == 0;
    }

    /** @return true when the amount is above zero */
    public boolean isPositive() {
        return amount.signum() > 0;
    }

    /**
     * Checks whether this amount is at least another amount.
     *
     * @param other the threshold
     * @return true when this amount is greater than or equal to {@code other}
     */
    public boolean isAtLeast(Money other) {
        return compareTo(other) >= 0;
    }

    /** @return the amount as a {@link BigDecimal} */
    public BigDecimal getAmount() {
        return amount;
    }

    /** @return the amount as a double for SQLite REAL storage */
    public double toDouble() {
        return amount.doubleValue();
    }

    /**
     * Compares two amounts numerically.
     *
     * @param other the amount to compare with
     * @return a negative, zero or positive number
     */
    @Override
    public int compareTo(Money other) {
        return amount.compareTo(other.amount);
    }

    /**
     * Two money values are equal when their normalised amounts are equal.
     *
     * @param o the other object
     * @return true when equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Money)) {
            return false;
        }
        return amount.compareTo(((Money) o).amount) == 0;
    }

    /** @return a hash consistent with {@link #equals(Object)} */
    @Override
    public int hashCode() {
        return amount.stripTrailingZeros().hashCode();
    }

    /** @return the plain amount, e.g. {@code 1250.00}; use CurrencyFormatter for display */
    @Override
    public String toString() {
        return amount.toPlainString();
    }
}
