package com.printxpress.app.core.validation.rules;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Rejects Sundays, when branches are closed (BR-6).
 * <p>Pattern role: Concrete Strategy for {@code ValidationRule}.</p>
 *
 * @since 1.0
 */
public final class NotSundayRule extends AbstractRule {

    /**
     * Creates the rule.
     *
     * @param errorRes message shown for a Sunday or malformed date
     */
    public NotSundayRule(int errorRes) {
        super(errorRes);
    }

    /**
     * Parses the date and checks its weekday.
     *
     * @param value the trimmed ISO date
     * @return true for Monday to Saturday
     */
    @Override
    protected boolean isSatisfiedBy(String value) {
        try {
            return LocalDate.parse(value).getDayOfWeek() != DayOfWeek.SUNDAY;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
