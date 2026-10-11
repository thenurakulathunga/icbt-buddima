package com.printxpress.app.core.validation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.printxpress.app.core.validation.rules.EmailRule;
import com.printxpress.app.core.validation.rules.LengthRule;
import com.printxpress.app.core.validation.rules.MatchRule;
import com.printxpress.app.core.validation.rules.MinDateRule;
import com.printxpress.app.core.validation.rules.NotSundayRule;
import com.printxpress.app.core.validation.rules.PasswordStrengthRule;
import com.printxpress.app.core.validation.rules.PostalCodeRule;
import com.printxpress.app.core.validation.rules.RangeRule;
import com.printxpress.app.core.validation.rules.RequiredIfEmptyRule;
import com.printxpress.app.core.validation.rules.RequiredRule;
import com.printxpress.app.core.validation.rules.SriLankanPhoneRule;
import com.printxpress.app.core.validation.rules.StepRule;

import org.junit.Test;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * Unit tests for the {@link ValidationRule} strategies, {@link CompositeRule} and
 * {@link FormValidator} (docs/09 §5; input classes of TC-03 … TC-07, TC-16, TC-25).
 *
 * @since 1.0
 */
public class ValidationRulesTest {

    /** Error id used by every rule under test. */
    private static final int ERR = 42;

    /** Required fails on null, empty and whitespace, passes on text. */
    @Test
    public void requiredRule_rejectsBlank() {
        RequiredRule rule = new RequiredRule(ERR);
        assertFalse(rule.validate(null).isValid());
        assertFalse(rule.validate("").isValid());
        assertFalse(rule.validate("   ").isValid());
        assertTrue(rule.validate("a").isValid());
        assertEquals(ERR, rule.validate("").getErrorRes());
    }

    /** Email accepts normal addresses and rejects malformed ones. */
    @Test
    public void emailRule_boundaries() {
        EmailRule rule = new EmailRule(ERR);
        assertTrue(rule.validate("nimal@example.lk").isValid());
        assertTrue(rule.validate("first.last+tag@mail.co.uk").isValid());
        assertFalse(rule.validate("nimal@").isValid());
        assertFalse(rule.validate("nimal.example.lk").isValid());
        assertFalse(rule.validate("nimal@example").isValid());
    }

    /** Sri Lankan mobile numbers: 07XXXXXXXX or +947XXXXXXXX only (TC-05). */
    @Test
    public void phoneRule_sriLankanMobile() {
        SriLankanPhoneRule rule = new SriLankanPhoneRule(ERR);
        assertTrue(rule.validate("0771234567").isValid());
        assertTrue(rule.validate("+94771234567").isValid());
        assertFalse(rule.validate("0112345678").isValid());
        assertFalse(rule.validate("077123456").isValid());
        assertFalse(rule.validate("07712345678").isValid());
        assertFalse(rule.validate("94771234567").isValid());
    }

    /** Password needs 8+ characters with upper, lower, digit and symbol (BR-2). */
    @Test
    public void passwordStrength_allCriteria() {
        PasswordStrengthRule rule = new PasswordStrengthRule(ERR);
        assertTrue(rule.validate("Print@2026").isValid());
        assertFalse(rule.validate("Pr@1").isValid());
        assertFalse(rule.validate("print@2026").isValid());
        assertFalse(rule.validate("PRINT@2026").isValid());
        assertFalse(rule.validate("Print2026").isValid());
        assertFalse(rule.validate("Print@abcd").isValid());
        assertEquals(5, PasswordStrengthRule.score("Print@2026"));
        assertEquals(0, PasswordStrengthRule.score(""));
    }

    /** Length counts characters inclusively at both ends. */
    @Test
    public void lengthRule_inclusiveBounds() {
        LengthRule rule = new LengthRule(5, 10, ERR);
        assertFalse(rule.validate("abcd").isValid());
        assertTrue(rule.validate("abcde").isValid());
        assertTrue(rule.validate("abcdefghij").isValid());
        assertFalse(rule.validate("abcdefghijk").isValid());
    }

    /** Range accepts integers within the limits only. */
    @Test
    public void rangeRule_quantityLimits() {
        RangeRule rule = new RangeRule(100, 10000, ERR);
        assertFalse(rule.validate("99").isValid());
        assertTrue(rule.validate("100").isValid());
        assertTrue(rule.validate("10000").isValid());
        assertFalse(rule.validate("10001").isValid());
        assertFalse(rule.validate("abc").isValid());
    }

    /** Step accepts multiples of the step above the base. */
    @Test
    public void stepRule_multiples() {
        StepRule rule = new StepRule(() -> 100, () -> 50, ERR);
        assertTrue(rule.validate("150").isValid());
        assertFalse(rule.validate("120").isValid());
    }

    /** Postal codes are five digits. */
    @Test
    public void postalCodeRule_fiveDigits() {
        PostalCodeRule rule = new PostalCodeRule(ERR);
        assertTrue(rule.validate("20000").isValid());
        assertFalse(rule.validate("2000").isValid());
        assertFalse(rule.validate("2000A").isValid());
    }

    /** Match compares with the partner field. */
    @Test
    public void matchRule_comparesPartner() {
        String[] other = {"Print@2026"};
        MatchRule rule = new MatchRule(() -> other[0], ERR);
        assertTrue(rule.validate("Print@2026").isValid());
        assertFalse(rule.validate("Print@2025").isValid());
    }

    /** Either-or fields: blank is only an error when the partner is blank too. */
    @Test
    public void requiredIfEmpty_eitherField() {
        String[] partner = {""};
        RequiredIfEmptyRule rule = new RequiredIfEmptyRule(() -> partner[0], ERR);
        assertFalse(rule.validate("").isValid());
        partner[0] = "0771234567";
        assertTrue(rule.validate("").isValid());
    }

    /** Dates must not be Sunday or earlier than the minimum. */
    @Test
    public void dateRules_sundayAndMinimum() {
        NotSundayRule sunday = new NotSundayRule(ERR);
        assertFalse(sunday.validate("2026-09-27").isValid());
        assertTrue(sunday.validate("2026-09-28").isValid());
        MinDateRule min = new MinDateRule(() -> LocalDate.of(2026, 9, 24), ERR);
        assertFalse(min.validate("2026-09-23").isValid());
        assertTrue(min.validate("2026-09-24").isValid());
    }

    /** Composite stops at the first failing rule and reports its error. */
    @Test
    public void compositeRule_firstFailureWins() {
        CompositeRule rule = new CompositeRule(new RequiredRule(1), new LengthRule(5, 80, 2));
        assertEquals(1, rule.validate("").getErrorRes());
        assertEquals(2, rule.validate("Hi").getErrorRes());
        assertTrue(rule.validate("Flyer colour").isValid());
    }

    /** The form validator returns one error per invalid field. */
    @Test
    public void formValidator_collectsErrors() {
        FormValidator validator = new FormValidator()
                .field("name", new RequiredRule(1))
                .field("phone", new RequiredRule(2), new SriLankanPhoneRule(3));
        Map<String, String> values = new HashMap<>();
        values.put("name", "");
        values.put("phone", "123");
        Map<String, Integer> errors = validator.validateAll(values);
        assertEquals(Integer.valueOf(1), errors.get("name"));
        assertEquals(Integer.valueOf(3), errors.get("phone"));
        values.put("name", "Nimal Perera");
        values.put("phone", "0771234567");
        assertTrue(validator.isValid(values));
    }
}
