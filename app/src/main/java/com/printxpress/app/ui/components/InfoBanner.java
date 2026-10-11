package com.printxpress.app.ui.components;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;

import androidx.annotation.StringRes;

import com.google.android.material.card.MaterialCardView;
import com.printxpress.app.R;
import com.printxpress.app.databinding.ViewInfoBannerBinding;

/**
 * Coloured message banner (kit Toast): info, warning, success or error, e.g. "Printing has
 * started" on order detail or the turnaround note at checkout.
 *
 * @since 1.0
 */
public class InfoBanner extends MaterialCardView {

    /** Banner kinds. */
    public enum Type {
        /** Blue information. */
        INFO(R.color.px_primary_container, R.color.px_on_primary_container, R.drawable.ic_info),
        /** Orange warning. */
        WARNING(R.color.px_warning_container, R.color.px_on_warning_container, R.drawable.ic_warning),
        /** Green success. */
        SUCCESS(R.color.px_success_container, R.color.px_on_success_container, R.drawable.ic_check),
        /** Red error. */
        ERROR(R.color.px_error_container, R.color.px_on_error_container, R.drawable.ic_error);

        /** Container colour. */
        private final int container;
        /** Text and icon colour. */
        private final int content;
        /** Icon. */
        private final int icon;

        /**
         * Creates a type.
         *
         * @param container container colour
         * @param content   text colour
         * @param icon      icon
         */
        Type(int container, int content, int icon) {
            this.container = container;
            this.content = content;
            this.icon = icon;
        }
    }

    /** The inflated children. */
    private final ViewInfoBannerBinding binding;

    /**
     * Creates the banner.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public InfoBanner(Context context, AttributeSet attrs) {
        super(context, attrs);
        binding = ViewInfoBannerBinding.inflate(LayoutInflater.from(context), this);
        setStrokeWidth(0);
        setCardElevation(0);
        setRadius(getResources().getDimension(R.dimen.radius_lg));
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.InfoBanner);
        try {
            setType(Type.values()[a.getInt(R.styleable.InfoBanner_px_type, 0)]);
            setTitle(a.getString(R.styleable.InfoBanner_px_title));
            binding.txtMessage.setText(a.getString(R.styleable.InfoBanner_px_message));
        } finally {
            a.recycle();
        }
    }

    /**
     * Applies a banner type.
     *
     * @param type the type
     */
    public void setType(Type type) {
        int content = getContext().getColor(type.content);
        setCardBackgroundColor(getContext().getColor(type.container));
        binding.imgIcon.setImageResource(type.icon);
        binding.imgIcon.setImageTintList(ColorStateList.valueOf(content));
        binding.txtTitle.setTextColor(content);
        binding.txtMessage.setTextColor(content);
    }

    /**
     * Sets the title; null hides it.
     *
     * @param title the title
     */
    public void setTitle(CharSequence title) {
        binding.txtTitle.setText(title);
        binding.txtTitle.setVisibility(title == null ? GONE : VISIBLE);
    }

    /**
     * Sets the message.
     *
     * @param messageRes the message
     */
    public void setMessage(@StringRes int messageRes) {
        binding.txtMessage.setText(messageRes);
    }

    /**
     * Sets a prepared message.
     *
     * @param message the message
     */
    public void setMessage(CharSequence message) {
        binding.txtMessage.setText(message);
    }
}
