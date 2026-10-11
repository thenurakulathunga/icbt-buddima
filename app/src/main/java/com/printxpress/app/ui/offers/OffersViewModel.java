package com.printxpress.app.ui.offers;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.core.base.BaseViewModel;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.data.model.Promotion;
import com.printxpress.app.data.repository.PromotionRepository;

import java.util.List;

/**
 * Offers and packages state (S22–S23, FR-4.5): the active promotions and one selected offer.
 *
 * @since 1.0
 */
public class OffersViewModel extends BaseViewModel {

    /** Promotions. */
    private final PromotionRepository promotions;
    /** Active promotions. */
    private final MutableLiveData<Resource<List<Promotion>>> list = new MutableLiveData<>();
    /** One promotion. */
    private final MutableLiveData<Resource<Promotion>> offer = new MutableLiveData<>();

    /**
     * Creates the ViewModel.
     *
     * @param promotions promotion repository
     */
    public OffersViewModel(PromotionRepository promotions) {
        this.promotions = promotions;
    }

    /** Loads the active promotions. */
    public void refresh() {
        load(promotions.getActivePromotions(), list);
    }

    /**
     * Loads one promotion.
     *
     * @param promoId the promotion
     */
    public void loadOffer(long promoId) {
        load(promotions.getPromotion(promoId), offer);
    }

    /** @return the active promotions */
    public LiveData<Resource<List<Promotion>>> getOffers() {
        return list;
    }

    /** @return the selected promotion */
    public LiveData<Resource<Promotion>> getOffer() {
        return offer;
    }
}
