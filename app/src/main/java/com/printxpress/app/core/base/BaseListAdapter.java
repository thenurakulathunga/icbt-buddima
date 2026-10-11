package com.printxpress.app.core.base;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.viewbinding.ViewBinding;

import java.util.Objects;
import java.util.function.Function;

/**
 * Generic RecyclerView adapter with DiffUtil, a typed binding and an item click listener.
 * <p>
 * Pattern role: Adapter + Template Method. Subclasses only inflate a row and bind an item; list
 * diffing, holder creation and click handling are shared.
 * </p>
 *
 * @param <T>  the item type
 * @param <VB> the row binding type
 * @since 1.0
 */
public abstract class BaseListAdapter<T, VB extends ViewBinding> extends ListAdapter<T, BaseViewHolder<VB>> {

    /**
     * Receives item clicks.
     *
     * @param <T> the item type
     */
    public interface OnItemClickListener<T> {

        /**
         * Called when a row is tapped.
         *
         * @param item     the item
         * @param position the adapter position
         */
        void onItemClick(T item, int position);
    }

    /** The click listener, may be null. */
    private OnItemClickListener<T> clickListener;

    /**
     * Creates an adapter that identifies items by a stable key and compares contents with equals
     * on a content signature.
     *
     * @param idOf        extracts a stable id
     * @param signatureOf extracts a value that changes whenever the row should re-bind
     */
    protected BaseListAdapter(Function<T, Object> idOf, Function<T, Object> signatureOf) {
        super(new DiffUtil.ItemCallback<T>() {
            @Override
            public boolean areItemsTheSame(@NonNull T oldItem, @NonNull T newItem) {
                return Objects.equals(idOf.apply(oldItem), idOf.apply(newItem));
            }

            @Override
            public boolean areContentsTheSame(@NonNull T oldItem, @NonNull T newItem) {
                return Objects.equals(signatureOf.apply(oldItem), signatureOf.apply(newItem));
            }
        });
    }

    /**
     * Sets the click listener.
     *
     * @param listener the listener, may be null
     */
    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.clickListener = listener;
    }

    /**
     * Inflates a row.
     *
     * @param inflater layout inflater
     * @param parent   the RecyclerView
     * @return the row binding
     */
    protected abstract VB inflate(LayoutInflater inflater, ViewGroup parent);

    /**
     * Binds an item to a row.
     *
     * @param binding  the row binding
     * @param item     the item
     * @param position the adapter position
     */
    protected abstract void bind(VB binding, T item, int position);

    /**
     * Creates a holder and wires the row click.
     *
     * @param parent   the RecyclerView
     * @param viewType ignored
     * @return the holder
     */
    @NonNull
    @Override
    public BaseViewHolder<VB> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        BaseViewHolder<VB> holder = new BaseViewHolder<>(inflate(LayoutInflater.from(parent.getContext()), parent));
        holder.itemView.setOnClickListener(v -> {
            int position = holder.getBindingAdapterPosition();
            if (clickListener != null && position != androidx.recyclerview.widget.RecyclerView.NO_POSITION) {
                clickListener.onItemClick(getItem(position), position);
            }
        });
        return holder;
    }

    /**
     * Binds the item at a position.
     *
     * @param holder   the holder
     * @param position the position
     */
    @Override
    public void onBindViewHolder(@NonNull BaseViewHolder<VB> holder, int position) {
        bind(holder.getBinding(), getItem(position), position);
    }
}
