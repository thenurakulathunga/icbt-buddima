package com.printxpress.app.data.repository;

import androidx.lifecycle.LiveData;

import com.printxpress.app.R;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.core.concurrency.AppExecutors;
import com.printxpress.app.core.validation.rules.RegexRule;
import com.printxpress.app.data.dao.OrderDao;
import com.printxpress.app.data.dao.PromotionDao;
import com.printxpress.app.data.model.CartItem;
import com.printxpress.app.data.model.Promotion;
import com.printxpress.app.domain.pricing.PriceCalculator;
import com.printxpress.app.domain.pricing.PromoEligibility;

import java.time.LocalDate;
import java.util.List;
import java.util.function.Supplier;

/**
 * SQLite-backed {@link PromotionRepository}.
 *
 * @since 1.0
 */
public class PromotionRepositoryImpl extends BaseRepository implements PromotionRepository {

    /** Promotion access. */
    private final PromotionDao promotionDao;
    /** Order access, for the first-order rule. */
    private final OrderDao orderDao;
    /** Pricing, for the eligible amount. */
    private final PriceCalculator calculator;
    /** Supplies today's date. */
    private final Supplier<LocalDate> today;

    /**
     * Creates the repository.
     *
     * @param executors    background executors
     * @param promotionDao promotion DAO
     * @param orderDao     order DAO
     * @param calculator   price calculator
     * @param today        supplies today's date
     */
    public PromotionRepositoryImpl(AppExecutors executors, PromotionDao promotionDao, OrderDao orderDao,
                                   PriceCalculator calculator, Supplier<LocalDate> today) {
        super(executors);
        this.promotionDao = promotionDao;
        this.orderDao = orderDao;
        this.calculator = calculator;
        this.today = today;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<List<Promotion>>> getActivePromotions() {
        return execute(() -> promotionDao.findActiveOn(today.get()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<Promotion>> getPromotion(long promoId) {
        return execute(() -> promotionDao.findById(promoId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<Promotion>> applyCode(long userId, String code, List<CartItem> cart) {
        return execute(() -> {
            Promotion promotion = resolve(userId, code, cart);
            return promotion;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Promotion> findActiveBlocking(LocalDate date) {
        return promotionDao.findActiveOn(date);
    }

    /**
     * Validates a code on the calling thread; shared with order placement.
     *
     * @param userId the customer
     * @param code   the typed code
     * @param cart   the cart lines
     * @return the eligible promotion
     * @throws RepositoryException with the specific reason when not eligible
     */
    Promotion resolve(long userId, String code, List<CartItem> cart) {
        String normalised = PromoEligibility.normalise(code);
        require(new RegexRule(Constants.REGEX_PROMO_CODE, 0).validate(normalised).isValid()
                && !normalised.isEmpty(), R.string.err_promo_invalid);
        Promotion promotion = promotionDao.findByCode(normalised);
        PromoEligibility.Result result = PromoEligibility.check(promotion, today.get(),
                calculator.eligibleAmount(cart, promotion), orderDao.countOrders(userId));
        require(result != PromoEligibility.Result.NOT_FOUND, R.string.err_promo_not_found);
        require(result != PromoEligibility.Result.EXPIRED, R.string.err_promo_expired);
        require(result != PromoEligibility.Result.FIRST_ORDER_ONLY, R.string.err_promo_first_order);
        require(result != PromoEligibility.Result.NOT_APPLICABLE, R.string.err_promo_not_applicable);
        require(result != PromoEligibility.Result.BELOW_MINIMUM, R.string.err_promo_minimum_plain);
        return promotion;
    }
}
