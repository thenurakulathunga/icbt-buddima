package com.printxpress.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.printxpress.app.core.base.BaseListAdapter;
import com.printxpress.app.data.model.Faq;
import com.printxpress.app.databinding.ItemFaqBinding;

import java.util.HashSet;
import java.util.Set;

/**
 * Expandable FAQ rows that remember which answers are open while the list is filtered.
 * <p>Pattern role: Adapter.</p>
 *
 * @since 1.0
 */
public class FaqAdapter extends BaseListAdapter<Faq, ItemFaqBinding> {

    /** Ids of the expanded questions. */
    private final Set<Long> expanded = new HashSet<>();

    /** Creates the adapter. */
    public FaqAdapter() {
        super(Faq::getFaqId, Faq::getQuestion);
    }

    /**
     * Inflates a row.
     *
     * @param inflater layout inflater
     * @param parent   the list
     * @return the binding
     */
    @Override
    protected ItemFaqBinding inflate(LayoutInflater inflater, ViewGroup parent) {
        return ItemFaqBinding.inflate(inflater, parent, false);
    }

    /**
     * Binds a question and restores its expanded state.
     *
     * @param binding  the row binding
     * @param item     the question
     * @param position the position
     */
    @Override
    protected void bind(ItemFaqBinding binding, Faq item, int position) {
        binding.card.bind(item);
        binding.card.setExpanded(expanded.contains(item.getFaqId()), false);
        binding.card.setOnToggle(open -> {
            if (open) {
                expanded.add(item.getFaqId());
            } else {
                expanded.remove(item.getFaqId());
            }
        });
    }
}
