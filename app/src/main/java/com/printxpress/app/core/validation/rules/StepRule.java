package com.printxpress.app.core.validation.rules;

import java.util.function.IntSupplier;

/**
 * Checks that a quantity sits on the product's step grid, e.g. business cards in 50s from 100
 * (BR-3: 100, 150 and 200 are valid, 125 is not). Non-numeric input fails.
 * <p>Pattern role: Concrete Strategy for {@code ValidationRule}.</p>
 *
 * @since 1.0
 */
public final class StepRule extends AbstractRule {

    /** Supplies the grid origin, i.e. the product minimum. */
    private final IntSupplier base;
    /** Supplies the step size. */
    private final IntSupplier step;

    /**
     * Creates the rule.
     *
     * @param base     supplies the first valid value
     * @param step     supplies the increment
     * @param errorRes message shown for an off-grid value
     */
    public StepRule(IntSupplier base, IntSupplier step, int errorRes) {
        super(errorRes);
        this.base = base;
        this.step = step;
    }

    /**
     * Checks the value equals {@code base + k * step} for some whole k.
     *
     * @param value the trimmed input
     * @return true when on the grid
     */
    @Override
    protected boolean isSatisfiedBy(String value) {
        try {
            long number = Long.parseLong(value);
            int s = Math.max(1, step.getAsInt());
            return (number - base.getAsInt()) % s == 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
