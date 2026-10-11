package com.printxpress.app.domain.pricing;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.printxpress.app.core.common.Money;
import com.printxpress.app.core.util.CurrencyFormatter;
import com.printxpress.app.data.model.CartItem;
import com.printxpress.app.data.model.Product;
import com.printxpress.app.data.model.ProductOption;
import com.printxpress.app.data.model.Promotion;
import com.printxpress.app.data.model.enums.DiscountType;
import com.printxpress.app.domain.fulfilment.PickupStrategy;
import com.printxpress.app.domain.schedule.ScheduleCalculator;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Unit tests for {@link PriceCalculator} and its strategies: option modifiers, BR-4 bulk tiers,
 * promotion scope, minimum and cap (TC-26, TC-38, TC-39).
 *
 * @since 1.0
 */
public class PriceCalculatorTest {

    /** Business cards category. */
    private static final long CARDS = 1L;
    /** Flyers category. */
    private static final long FLYERS = 2L;

    /** Calculator under test. */
    private PriceCalculator calculator;
    /** Standard business card, Rs. 15.00 per card. */
    private Product card;

    /** Builds the fixtures. */
    @Before
    public void setUp() {
        calculator = new PriceCalculator();
        card = product(CARDS, "15.00");
    }

    /**
     * Creates a product.
     *
     * @param categoryId its category
     * @param unitPrice  price per unit
     * @return the product
     */
    private static Product product(long categoryId, String unitPrice) {
        Product p = new Product();
        p.setCategoryId(categoryId);
        p.setBasePrice(Money.of(unitPrice));
        return p;
    }

    /**
     * Creates an option with a percentage modifier.
     *
     * @param percent the modifier
     * @return the option
     */
    private static ProductOption option(double percent) {
        ProductOption o = new ProductOption();
        o.setPriceModifier(percent);
        return o;
    }

    /**
     * Creates a cart line.
     *
     * @param product  the product
     * @param quantity the quantity
     * @param options  the options
     * @return the line
     */
    private static CartItem line(Product product, int quantity, List<ProductOption> options) {
        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setOptions(options);
        return item;
    }

    /**
     * Creates an active promotion.
     *
     * @param type       discount type
     * @param value      discount value
     * @param min        minimum order
     * @param categoryId category scope, or null
     * @return the promotion
     */
    private static Promotion promo(DiscountType type, double value, String min, Long categoryId) {
        Promotion p = new Promotion();
        p.setPromoCode("TEST10");
        p.setDiscountType(type);
        p.setDiscountValue(value);
        p.setMinOrderValue(Money.of(min));
        p.setCategoryId(categoryId);
        p.setStartDate(LocalDate.of(2026, 1, 1));
        p.setEndDate(LocalDate.of(2026, 12, 31));
        p.setActive(true);
        return p;
    }

    /** 100 cards with no options cost exactly base × quantity. */
    @Test
    public void quoteLine_noOptions() {
        LineQuote q = calculator.quoteLine(card, Collections.emptyList(), 100);
        assertEquals(Money.of("1500.00"), q.getLineTotal());
        assertEquals(Money.of("15.00"), q.getUnitPrice());
        assertTrue(q.getBulkDiscount().isZero());
    }

    /** TC-26: 500 double-sided cards = 7,500 × 1.40 − 10% = Rs. 9,450.00. */
    @Test
    public void quoteLine_modifiersAndBulkTier1() {
        LineQuote q = calculator.quoteLine(card, Arrays.asList(option(40), option(0)), 500);
        assertEquals(Money.of("10500.00"), q.getGross());
        assertEquals(Money.of("1050.00"), q.getBulkDiscount());
        assertEquals("Rs. 9,450.00", CurrencyFormatter.format(q.getLineTotal()));
        assertEquals(10.0, q.getBulkPercent(), 0.0);
    }

    /** BR-4 tier boundaries: 499 → 0%, 500 → 10%, 999 → 10%, 1000 → 15%. */
    @Test
    public void bulkTiers_boundaries() {
        assertEquals(0.0, BulkDiscountPricing.percentFor(499), 0.0);
        assertEquals(10.0, BulkDiscountPricing.percentFor(500), 0.0);
        assertEquals(10.0, BulkDiscountPricing.percentFor(999), 0.0);
        assertEquals(15.0, BulkDiscountPricing.percentFor(1000), 0.0);
    }

    /** TC-38: a 10% promo on Rs. 9,450.00 gives Rs. 8,505.00. */
    @Test
    public void calculate_percentPromo() {
        List<CartItem> cart = Collections.singletonList(line(card, 500, Collections.singletonList(option(40))));
        PriceBreakdown b = calculator.calculate(cart, promo(DiscountType.PERCENT, 10, "0", null),
                new PickupStrategy(new ScheduleCalculator()));
        assertEquals(Money.of("945.00"), b.getPromoDiscount());
        assertEquals(Money.of("8505.00"), b.getTotal());
        assertEquals("TEST10", b.getPromoCode());
        assertTrue(b.getDeliveryFee().isZero());
    }

    /** A category promo only discounts lines of that category. */
    @Test
    public void calculate_categoryScopedPromo() {
        Product flyer = product(FLYERS, "10.00");
        List<CartItem> cart = Arrays.asList(line(card, 100, Collections.emptyList()),
                line(flyer, 100, Collections.emptyList()));
        Promotion flyersOnly = promo(DiscountType.PERCENT, 20, "0", FLYERS);
        assertEquals(Money.of("1000.00"), calculator.eligibleAmount(cart, flyersOnly));
        PriceBreakdown b = calculator.calculate(cart, flyersOnly, null);
        assertEquals(Money.of("200.00"), b.getPromoDiscount());
        assertEquals(Money.of("2300.00"), b.getTotal());
    }

    /** Below the minimum order the promo gives nothing (TC-39). */
    @Test
    public void calculate_promoBelowMinimum() {
        List<CartItem> cart = Collections.singletonList(line(card, 100, Collections.emptyList()));
        PriceBreakdown b = calculator.calculate(cart, promo(DiscountType.PERCENT, 10, "20000", null), null);
        assertTrue(b.getPromoDiscount().isZero());
        assertNull(b.getPromoCode());
        assertEquals(Money.of("1500.00"), b.getTotal());
    }

    /** A fixed discount never exceeds the eligible amount. */
    @Test
    public void promoCodePricing_fixedIsCapped() {
        Promotion fixed = promo(DiscountType.FIXED, 5000, "0", null);
        assertEquals(Money.of("1500.00"), PromoCodePricing.discountFor(fixed, Money.of("1500.00")));
        assertEquals(Money.of("500.00"),
                PromoCodePricing.discountFor(promo(DiscountType.FIXED, 500, "0", null), Money.of("1500.00")));
    }
}
