package com.printxpress.app.core.base;

import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

/**
 * RecyclerView holder that keeps a typed ViewBinding.
 *
 * @param <VB> the row binding type
 * @since 1.0
 */
public class BaseViewHolder<VB extends ViewBinding> extends RecyclerView.ViewHolder {

    /** The row binding. */
    private final VB binding;

    /**
     * Creates a holder.
     *
     * @param binding the row binding
     */
    public BaseViewHolder(VB binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    /** @return the row binding */
    public VB getBinding() {
        return binding;
    }
}
