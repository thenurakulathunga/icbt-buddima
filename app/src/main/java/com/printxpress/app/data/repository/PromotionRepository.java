package com.printxpress.app.data.repository;

import androidx.lifecycle.LiveData;

import com.printxpress.app.core.common.Resource;
import com.printxpress.app.data.model.CartItem;
import com.printxpress.app.data.model.Promotion;

import java.time.LocalDate;
import java.util.List;

/**
 * Offers and promo codes (FR-2.11, FR-4.3, FR-4.5).
 * <p>Pattern role: Repository.</p>
 *
 * @since 1.0
 */
public interface PromotionRepository {

    /** @return promotions running today */
    LiveData<Resource<List<Promotion>>> getActivePromotions();

    /**
     * Loads one promotion.
     *
     * @param promoId the promotion
     * @return the promotion
     */
    LiveData<Resource<Promotion>> getPromotion(long promoId);

    /**
     * Checks a typed code against the cart and returns the promotion, or a specific error (TC-39).
     *
     * @param userId the customer
     * @param code   the typed code
     * @param cart   the cart lines with products
     * @return the promotion to apply
     */
    LiveData<Resource<Promotion>> applyCode(long userId, String code, List<CartItem> cart);

    /**
     * Lists running promotions on the calling thread, for the promotion worker.
     *
     * @param today the date
     * @return the running promotions
     */
    List<Promotion> findActiveBlocking(LocalDate today);
}
