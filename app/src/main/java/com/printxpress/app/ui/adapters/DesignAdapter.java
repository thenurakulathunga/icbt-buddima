package com.printxpress.app.ui.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.printxpress.app.core.base.BaseListAdapter;
import com.printxpress.app.data.model.Design;
import com.printxpress.app.databinding.ItemDesignBinding;

import java.util.function.Consumer;

/**
 * Saved design cards: selectable in the design picker, deletable in Saved designs.
 * <p>Pattern role: Adapter.</p>
 *
 * @since 1.0
 */
public class DesignAdapter extends BaseListAdapter<Design, ItemDesignBinding> {

    /** Selected design id, or -1. */
    private long selectedId = -1;
    /** Delete callback, null hides the delete button. */
    private Consumer<Design> onDelete;

    /** Creates the adapter. */
    public DesignAdapter() {
        super(Design::getDesignId, d -> d.getTitle() + d.getFilePath());
    }

    /**
     * Inflates a card.
     *
     * @param inflater layout inflater
     * @param parent   the list
     * @return the binding
     */
    @Override
    protected ItemDesignBinding inflate(LayoutInflater inflater, ViewGroup parent) {
        return ItemDesignBinding.inflate(inflater, parent, false);
    }

    /**
     * Binds a design.
     *
     * @param binding  the row binding
     * @param item     the design
     * @param position the position
     */
    @Override
    protected void bind(ItemDesignBinding binding, Design item, int position) {
        binding.card.bind(item);
        binding.card.setSelectedMark(item.getDesignId() == selectedId);
        if (onDelete != null) {
            binding.card.setOnDelete(() -> onDelete.accept(item));
        }
    }

    /**
     * Marks the selected design.
     *
     * @param designId the design id, or -1
     */
    public void setSelectedId(long designId) {
        selectedId = designId;
        notifyDataSetChanged();
    }

    /**
     * Enables deleting and registers the callback.
     *
     * @param listener receives the design
     */
    public void setOnDelete(Consumer<Design> listener) {
        this.onDelete = listener;
    }
}
