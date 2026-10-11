package com.printxpress.app.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import com.printxpress.app.core.common.Money;
import com.printxpress.app.core.security.PasswordHasher;
import com.printxpress.app.core.util.CurrencyFormatter;
import com.printxpress.app.core.util.OrderNumberGenerator;

import org.junit.Test;

import java.time.LocalDate;

/**
 * Unit tests for money arithmetic, LKR formatting, PBKDF2 hashing (NFR-5) and order numbers.
 *
 * @since 1.0
 */
public class CoreUtilitiesTest {

    /** Money keeps two decimals and rounds half up. */
    @Test
    public void money_arithmeticAndRounding() {
        assertEquals(Money.of("0.30"), Money.of("0.10").plus(Money.of("0.20")));
        assertEquals(Money.of("1.25"), Money.of("12.45").percent(10));
        assertEquals(Money.of("33.33"), Money.of("100").dividedBy(3));
        assertTrue(Money.of("-1").isNegative());
        assertTrue(Money.of("-1").nonNegative().isZero());
        assertEquals(Money.of("5.00"), Money.of("5").min(Money.of("7")));
    }

    /** Currency uses "Rs. 1,250.00" and shows discounts as negatives. */
    @Test
    public void currencyFormatter_lkr() {
        assertEquals("Rs. 1,250.00", CurrencyFormatter.format(Money.of("1250")));
        assertEquals("Rs. 0.00", CurrencyFormatter.format(null));
        assertEquals("- Rs. 945.00", CurrencyFormatter.formatDiscount(Money.of("945")));
    }

    /** Same password and salt give the same hash; a different salt or password does not. */
    @Test
    public void passwordHasher_verify() {
        PasswordHasher hasher = new PasswordHasher();
        String salt = hasher.generateSalt();
        String hash = hasher.hash("Print@2026".toCharArray(), salt);
        assertTrue(hasher.verify("Print@2026".toCharArray(), salt, hash));
        assertFalse(hasher.verify("Print@2025".toCharArray(), salt, hash));
        assertNotEquals(hash, hasher.hash("Print@2026".toCharArray(), hasher.generateSalt()));
        assertFalse(hash.contains("Print"));
    }

    /** Order numbers follow PX-yyMMdd-NNNN. */
    @Test
    public void orderNumber_format() {
        String number = OrderNumberGenerator.generate(LocalDate.of(2026, 9, 22), 1);
        assertEquals("PX-260922-0001", number);
        assertTrue(OrderNumberGenerator.isValid(number));
        assertFalse(OrderNumberGenerator.isValid("PX-26092-0001"));
    }

    /** Sequence numbers outside 1–9999 are rejected. */
    @Test(expected = IllegalArgumentException.class)
    public void orderNumber_rejectsZeroSequence() {
        OrderNumberGenerator.generate(LocalDate.of(2026, 9, 22), 0);
    }
}
