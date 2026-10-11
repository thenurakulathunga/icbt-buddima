package com.printxpress.app.ui.components;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.printxpress.app.R;
import com.printxpress.app.data.model.Promotion;
import com.printxpress.app.databinding.ItemPromoPageBinding;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Swipeable row of {@link PromoCard}s with page dots that advances every 5 seconds (Home hero).
 *
 * @since 1.0
 */
public class PromoCarousel extends LinearLayout {

    /** Auto-scroll interval. */
    private static final long AUTO_SCROLL_MS = 5000;

    /** The pager. */
    private final ViewPager2 pager;
    /** The dots. */
    private final PageIndicator indicator;
    /** The adapter. */
    private final Adapter adapter = new Adapter();
    /** Main-thread handler for auto-scroll. */
    private final Handler handler = new Handler(Looper.getMainLooper());
    /** Advances one page and reschedules itself. */
    private final Runnable advance = new Runnable() {
        @Override
        public void run() {
            if (adapter.getItemCount() > 1) {
                pager.setCurrentItem((pager.getCurrentItem() + 1) % adapter.getItemCount(), true);
            }
            handler.postDelayed(this, AUTO_SCROLL_MS);
        }
    };

    /**
     * Creates the carousel.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public PromoCarousel(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        pager = new ViewPager2(context);
        pager.setAdapter(adapter);
        addView(pager, new LayoutParams(LayoutParams.MATCH_PARENT,
                getResources().getDimensionPixelSize(R.dimen.promo_card_height)));
        indicator = new PageIndicator(context, null);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.topMargin = getResources().getDimensionPixelSize(R.dimen.space_sm);
        addView(indicator, params);
        indicator.attachTo(pager);
    }

    /**
     * Replaces the promotions.
     *
     * @param promotions the promotions
     */
    public void setPromotions(List<Promotion> promotions) {
        adapter.items = new ArrayList<>(promotions);
        adapter.notifyDataSetChanged();
        setVisibility(promotions.isEmpty() ? GONE : VISIBLE);
    }

    /**
     * Registers the card tap callback.
     *
     * @param listener receives the tapped promotion
     */
    public void setOnPromoClick(Consumer<Promotion> listener) {
        adapter.onClick = listener;
    }

    /**
     * Registers the code chip callback.
     *
     * @param listener receives the code
     */
    public void setOnCodeClick(Consumer<String> listener) {
        adapter.onCode = listener;
    }

    /** Starts auto-scroll when shown. */
    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        handler.postDelayed(advance, AUTO_SCROLL_MS);
    }

    /** Stops auto-scroll when hidden. */
    @Override
    protected void onDetachedFromWindow() {
        handler.removeCallbacks(advance);
        super.onDetachedFromWindow();
    }

    /**
     * Pager adapter of promo cards.
     */
    private static final class Adapter extends RecyclerView.Adapter<Holder> {

        /** The promotions. */
        private List<Promotion> items = new ArrayList<>();
        /** Card tap callback. */
        private Consumer<Promotion> onClick;
        /** Code chip callback. */
        private Consumer<String> onCode;

        /**
         * Creates a page.
         *
         * @param parent   the pager
         * @param viewType ignored
         * @return the holder
         */
        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Holder(ItemPromoPageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        /**
         * Binds a page.
         *
         * @param holder   the holder
         * @param position the position
         */
        @Override
        public void onBindViewHolder(@NonNull Holder holder, int position) {
            Promotion promotion = items.get(position);
            holder.binding.promoCard.bind(promotion);
            holder.binding.promoCard.setOnClickListener(v -> {
                if (onClick != null) {
                    onClick.accept(promotion);
                }
            });
            holder.binding.promoCard.setOnCodeClick(code -> {
                if (onCode != null) {
                    onCode.accept(code);
                }
            });
        }

        /** @return the number of promotions */
        @Override
        public int getItemCount() {
            return items.size();
        }
    }

    /**
     * Page holder.
     */
    private static final class Holder extends RecyclerView.ViewHolder {

        /** The page binding. */
        private final ItemPromoPageBinding binding;

        /**
         * Creates a holder.
         *
         * @param binding the page binding
         */
        Holder(ItemPromoPageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
