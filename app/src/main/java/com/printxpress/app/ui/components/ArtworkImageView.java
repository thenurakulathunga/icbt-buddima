package com.printxpress.app.ui.components;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import androidx.annotation.DrawableRes;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;

import com.bumptech.glide.Glide;
import com.google.android.material.imageview.ShapeableImageView;
import com.printxpress.app.R;
import com.printxpress.app.core.util.DrawableResolver;

import java.io.File;

/**
 * Image slot for products, categories, gallery tiles and promos.
 * <p>
 * Shows the photo named in the database when it has been added to the app (docs/07). Until then
 * it shows the category's Hugeicon centred on the primary container colour, filling the whole
 * slot, so every screen looks finished while assets are still being collected. Local files
 * (uploads, avatars) load through Glide.
 * </p>
 *
 * @since 1.0
 */
public class ArtworkImageView extends ShapeableImageView {

    /** Icon size as a share of the slot's shorter side. */
    private static final float ICON_SHARE = 0.36f;

    /** Icon shown in the fallback panel, or 0 when a photo is shown. */
    @DrawableRes
    private int fallbackIcon;

    /**
     * Creates the view.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public ArtworkImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Shows a named photo, or the fallback icon panel.
     *
     * @param photoName drawable name of the photo, may be null
     * @param iconName  drawable name of the fallback icon, may be null
     */
    public void setArtwork(String photoName, String iconName) {
        Context ctx = getContext();
        if (photoName != null && DrawableResolver.exists(ctx, photoName)) {
            fallbackIcon = 0;
            setScaleType(ScaleType.CENTER_CROP);
            Glide.with(this).load(DrawableResolver.resolve(ctx, photoName)).centerCrop().into(this);
            return;
        }
        showIcon(iconName != null && DrawableResolver.exists(ctx, iconName)
                ? DrawableResolver.resolve(ctx, iconName) : R.drawable.ic_image);
    }

    /**
     * Shows a local file (image) or an icon for other types.
     *
     * @param path    absolute path, may be null
     * @param isImage whether the file is an image
     * @param iconRes icon used for non-images or missing files
     */
    public void setFile(String path, boolean isImage, @DrawableRes int iconRes) {
        if (path != null && isImage && new File(path).exists()) {
            fallbackIcon = 0;
            setScaleType(ScaleType.CENTER_CROP);
            Glide.with(this).load(new File(path)).centerCrop().placeholder(R.drawable.img_placeholder_product).into(this);
        } else {
            showIcon(iconRes);
        }
    }

    /**
     * Shows an icon centred on the primary container colour.
     *
     * @param iconRes the icon
     */
    public void showIcon(@DrawableRes int iconRes) {
        Glide.with(this).clear(this);
        fallbackIcon = iconRes;
        setScaleType(ScaleType.FIT_XY);
        renderFallback(getWidth(), getHeight());
    }

    /**
     * Re-renders the fallback panel so the icon keeps its proportion.
     *
     * @param w    new width
     * @param h    new height
     * @param oldw old width
     * @param oldh old height
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (fallbackIcon != 0) {
            renderFallback(w, h);
        }
    }

    /**
     * Builds a full-bleed panel: tinted background with the icon centred on top.
     *
     * @param w the slot width, 0 before layout
     * @param h the slot height, 0 before layout
     */
    private void renderFallback(int w, int h) {
        Drawable icon = AppCompatResources.getDrawable(getContext(), fallbackIcon);
        if (icon == null) {
            return;
        }
        icon = DrawableCompat.wrap(icon.mutate());
        DrawableCompat.setTint(icon, resolveColor(com.google.android.material.R.attr.colorOnPrimaryContainer));
        LayerDrawable layers = new LayerDrawable(new Drawable[]{
                new ColorDrawable(resolveColor(com.google.android.material.R.attr.colorPrimaryContainer)), icon});
        int minSide = Math.min(w, h);
        int size = minSide > 0 ? (int) (minSide * ICON_SHARE) : getResources().getDimensionPixelSize(R.dimen.icon_lg);
        layers.setLayerGravity(1, Gravity.CENTER);
        layers.setLayerSize(1, size, size);
        setImageDrawable(layers);
    }

    /**
     * Reads a colour attribute from the theme.
     *
     * @param attr the attribute
     * @return the colour
     */
    private int resolveColor(int attr) {
        TypedValue value = new TypedValue();
        getContext().getTheme().resolveAttribute(attr, value, true);
        return value.data;
    }

    /**
     * Converts a category image name such as img_cat_mugs to its icon name ic_cat_mugs.
     *
     * @param categoryImageRes the category image name
     * @return the icon name, or null
     */
    public static String iconForCategory(String categoryImageRes) {
        return categoryImageRes == null ? null : categoryImageRes.replace("img_cat_", "ic_cat_");
    }
}
