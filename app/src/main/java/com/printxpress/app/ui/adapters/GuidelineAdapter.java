package com.printxpress.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.printxpress.app.core.base.BaseListAdapter;
import com.printxpress.app.data.model.Guideline;
import com.printxpress.app.databinding.ItemGuidelineBinding;

/**
 * Guideline cards for the Print guidelines list.
 * <p>Pattern role: Adapter.</p>
 *
 * @since 1.0
 */
public class GuidelineAdapter extends BaseListAdapter<Guideline, ItemGuidelineBinding> {

    /** Creates the adapter. */
    public GuidelineAdapter() {
        super(Guideline::getGuidelineId, Guideline::getTitle);
    }

    /**
     * Inflates a card.
     *
     * @param inflater layout inflater
     * @param parent   the list
     * @return the binding
     */
    @Override
    protected ItemGuidelineBinding inflate(LayoutInflater inflater, ViewGroup parent) {
        return ItemGuidelineBinding.inflate(inflater, parent, false);
    }

    /**
     * Binds a guideline.
     *
     * @param binding  the card binding
     * @param item     the guideline
     * @param position the position
     */
    @Override
    protected void bind(ItemGuidelineBinding binding, Guideline item, int position) {
        binding.card.bind(item);
    }
}
