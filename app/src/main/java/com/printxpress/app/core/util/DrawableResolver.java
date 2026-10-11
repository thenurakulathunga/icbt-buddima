package com.printxpress.app.core.util;

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.DrawableRes;

import com.printxpress.app.R;

import java.util.HashMap;
import java.util.Map;

/**
 * Turns drawable names stored in the database ({@code image_res}, {@code icon_res}) into resource ids.
 * <p>
 * Product photos are collected separately (docs/07). Until a photo exists, the resolver falls
 * back to the next name in the chain and finally to the generic placeholder, so the app never
 * shows a broken image. Results are cached because {@code getIdentifier} is slow.
 * </p>
 *
 * @since 1.0
 */
public final class DrawableResolver {

    /** Cache of name to resource id (0 when missing). */
    private static final Map<String, Integer> CACHE = new HashMap<>();

    /** Hidden constructor: utility class. */
    private DrawableResolver() {
    }

    /**
     * Resolves the first existing drawable among the given names.
     *
     * @param context the context used to look up resources
     * @param names   candidate names in order of preference; nulls are skipped
     * @return the first match, or the product placeholder
     */
    @DrawableRes
    public static int resolve(Context context, String... names) {
        for (String name : names) {
            int id = lookup(context, name);
            if (id != 0) {
                return id;
            }
        }
        return R.drawable.img_placeholder_product;
    }

    /**
     * Checks whether a drawable with the given name exists.
     *
     * @param context the context used to look up resources
     * @param name    the drawable name
     * @return true when it exists
     */
    public static boolean exists(Context context, String name) {
        return lookup(context, name) != 0;
    }

    /**
     * Looks up one name with caching.
     *
     * @param context the context used to look up resources
     * @param name    the drawable name, may be null
     * @return the resource id, or 0 when missing
     */
    @SuppressLint("DiscouragedApi")
    private static synchronized int lookup(Context context, String name) {
        if (name == null || name.isEmpty()) {
            return 0;
        }
        Integer cached = CACHE.get(name);
        if (cached != null) {
            return cached;
        }
        int id = context.getResources().getIdentifier(name, "drawable", context.getPackageName());
        CACHE.put(name, id);
        return id;
    }
}
