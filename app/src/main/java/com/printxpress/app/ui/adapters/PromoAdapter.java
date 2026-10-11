package com.printxpress.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.printxpress.app.core.base.BaseListAdapter;
import com.printxpress.app.data.model.Promotion;
import com.printxpress.app.databinding.ItemPromoBinding;

import java.util.function.Consumer;

/**
 * Promotion cards for the Offers list.
 * <p>Pattern role: Adapter.</p>
 *
 * @since 1.0
 */
public class PromoAdapter extends BaseListAdapter<Promotion, ItemPromoBinding> {

    /** Code chip callback, may be null. */
    private Consumer<String> onCodeClick;

    /** Creates the adapter. */
    public PromoAdapter() {
        super(Promotion::getPromoId, p -> p.getTitle() + p.getPromoCode() + p.getEndDate());
    }

    /**
     * Registers the code chip callback.
     *
     * @param listener receives the promo code
     */
    public void setOnCodeClick(Consumer<String> listener) {
        this.onCodeClick = listener;
    }

    /**
     * Inflates a card.
     *
     * @param inflater layout inflater
     * @param parent   the list
     * @return the binding
     */
    @Override
    protected ItemPromoBinding inflate(LayoutInflater inflater, ViewGroup parent) {
        return ItemPromoBinding.inflate(inflater, parent, false);
    }

    /**
     * Binds a promotion.
     *
     * @param binding  the card binding
     * @param item     the promotion
     * @param position the position
     */
    @Override
    protected void bind(ItemPromoBinding binding, Promotion item, int position) {
        binding.card.bind(item);
        binding.card.setOnCodeClick(code -> {
            if (onCodeClick != null) {
                onCodeClick.accept(code);
            }
        });
    }
}
