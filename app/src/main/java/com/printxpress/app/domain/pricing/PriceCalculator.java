package com.printxpress.app.domain.pricing;

import com.printxpress.app.core.common.Money;
import com.printxpress.app.data.model.CartItem;
import com.printxpress.app.data.model.Product;
import com.printxpress.app.data.model.ProductOption;
import com.printxpress.app.data.model.Promotion;
import com.printxpress.app.domain.fulfilment.FulfilmentStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Single source of truth for every price in the app (configure screen, cart, checkout, order).
 * <p>
 * Delegates to the strategy chains built by {@link PricingStrategyFactory}: each line is priced
 * base, then options, then bulk; the cart then applies the promo to its eligible lines and adds
 * the fulfilment fee.
 * </p>
 *
 * @since 1.0
 */
public class PriceCalculator {

    /** Base plus options. */
    private final PricingStrategy grossLine = PricingStrategyFactory.grossLine();
    /** Base plus options minus bulk. */
    private final PricingStrategy netLine = PricingStrategyFactory.netLine();
    /** Promo strategy. */
    private final PricingStrategy promo = PricingStrategyFactory.promotion();

    /** Creates a calculator wired with the standard pricing strategies. */
    public PriceCalculator() {
        super();
    }

    /**
     * Prices one configured line (FR-2.5 live price).
     *
     * @param product  the product
     * @param options  the chosen options
     * @param quantity the quantity
     * @return the line quote
     */
    public LineQuote quoteLine(Product product, List<ProductOption> options, int quantity) {
        int qty = Math.max(0, quantity);
        PricingContext context = PricingContext.forLine(product.getBasePrice(), qty, modifiersOf(options));
        Money gross = grossLine.apply(context, Money.ZERO);
        Money net = netLine.apply(context, Money.ZERO);
        Money unit = grossLine.apply(PricingContext.forLine(product.getBasePrice(), 1, modifiersOf(options)), Money.ZERO);
        return new LineQuote(unit, gross, gross.minus(net), net, BulkDiscountPricing.percentFor(qty));
    }

    /**
     * Prices a cart with an optional promotion and fulfilment method.
     *
     * @param items      the cart lines with products and options attached
     * @param promotion  an eligible promotion, may be null
     * @param fulfilment pickup or delivery strategy, may be null before it is chosen
     * @return the breakdown
     */
    public PriceBreakdown calculate(List<CartItem> items, Promotion promotion, FulfilmentStrategy fulfilment) {
        Money gross = Money.ZERO;
        Money net = Money.ZERO;
        for (CartItem item : items) {
            if (item.getProduct() == null) {
                continue;
            }
            LineQuote quote = quoteLine(item.getProduct(), item.getOptions(), item.getQuantity());
            gross = gross.plus(quote.getGross());
            net = net.plus(quote.getLineTotal());
        }
        Money eligible = eligibleAmount(items, promotion);
        Money afterPromo = promo.apply(PricingContext.forPromotion(promotion, eligible), net);
        Money promoDiscount = net.minus(afterPromo);
        Money fee = fulfilment == null ? Money.ZERO : fulfilment.deliveryFee();
        boolean delivery = fulfilment != null && fulfilment.requiresAddress();
        return new PriceBreakdown(gross, gross.minus(net), promoDiscount,
                promoDiscount.isPositive() ? promotion.getPromoCode() : null, fee, delivery);
    }

    /**
     * Sums the net line totals in the promotion's target category.
     *
     * @param items     the cart lines
     * @param promotion the promotion, may be null
     * @return the eligible amount, or zero
     */
    public Money eligibleAmount(List<CartItem> items, Promotion promotion) {
        if (promotion == null) {
            return Money.ZERO;
        }
        Money eligible = Money.ZERO;
        for (CartItem item : items) {
            Product product = item.getProduct();
            if (product != null && promotion.appliesToCategory(product.getCategoryId())) {
                eligible = eligible.plus(quoteLine(product, item.getOptions(), item.getQuantity()).getLineTotal());
            }
        }
        return eligible;
    }

    /**
     * Extracts modifier percentages.
     *
     * @param options the options, may be null
     * @return the modifiers
     */
    private static List<Double> modifiersOf(List<ProductOption> options) {
        List<Double> modifiers = new ArrayList<>();
        if (options != null) {
            for (ProductOption option : options) {
                modifiers.add(option.getPriceModifier());
            }
        }
        return modifiers;
    }
}
