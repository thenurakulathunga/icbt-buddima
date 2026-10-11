package com.printxpress.app.ui.main;

import android.os.Bundle;

import androidx.navigation.NavController;
import androidx.navigation.NavOptions;

import com.printxpress.app.R;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.ui.components.AppTopBar;

/**
 * Central list of cross-screen navigation routes inside {@code nav_main}, so screens do not
 * build argument bundles themselves.
 *
 * @since 1.0
 */
public final class ShellRoutes {

    /** Hidden constructor: static routes. */
    private ShellRoutes() {
    }

    /**
     * Connects the cart and bell shortcuts of a top bar.
     *
     * @param bar the top bar
     * @param nav the navigation controller
     */
    static void bindTopBarActions(AppTopBar bar, NavController nav) {
        bar.showActions(true);
        bar.setOnCartClick(() -> nav.navigate(R.id.cartFragment));
        bar.setOnNotificationsClick(() -> nav.navigate(R.id.notificationsFragment));
    }

    /**
     * Opens the product list.
     *
     * @param nav        the navigation controller
     * @param categoryId the category, or 0 for all
     * @param title      the screen title, may be null
     * @param query      the initial search, may be null
     */
    public static void productList(NavController nav, long categoryId, String title, String query) {
        Bundle args = new Bundle();
        args.putLong(Constants.ARG_CATEGORY_ID, categoryId);
        args.putString(Constants.ARG_TITLE, title);
        args.putString(Constants.ARG_QUERY, query);
        nav.navigate(R.id.productListFragment, args);
    }

    /**
     * Opens a product.
     *
     * @param nav       the navigation controller
     * @param productId the product
     */
    public static void productDetail(NavController nav, long productId) {
        Bundle args = new Bundle();
        args.putLong(Constants.ARG_PRODUCT_ID, productId);
        nav.navigate(R.id.productDetailFragment, args);
    }

    /**
     * Starts the configure flow for a product or an existing cart line.
     *
     * @param nav        the navigation controller
     * @param productId  the product
     * @param cartItemId the cart line to edit, or 0 for a new one
     */
    public static void configure(NavController nav, long productId, long cartItemId) {
        Bundle args = new Bundle();
        args.putLong(Constants.ARG_PRODUCT_ID, productId);
        args.putLong(Constants.ARG_CART_ITEM_ID, cartItemId);
        nav.navigate(R.id.nav_configure, args);
    }

    /**
     * Leaves the finished checkout for the order's tracking screen.
     *
     * @param nav     the navigation controller
     * @param orderId the order
     */
    public static void orderDetailFromSuccess(NavController nav, long orderId) {
        NavOptions clear = new NavOptions.Builder().setPopUpTo(R.id.homeFragment, false).build();
        nav.navigate(R.id.orderDetailFragment, idArgs(Constants.ARG_ORDER_ID, orderId), clear);
    }

    /**
     * Opens an order's tracking screen.
     *
     * @param nav     the navigation controller
     * @param orderId the order
     */
    public static void orderDetail(NavController nav, long orderId) {
        nav.navigate(R.id.orderDetailFragment, idArgs(Constants.ARG_ORDER_ID, orderId));
    }

    /**
     * Opens an offer.
     *
     * @param nav     the navigation controller
     * @param promoId the promotion
     */
    public static void offerDetail(NavController nav, long promoId) {
        nav.navigate(R.id.offerDetailFragment, idArgs(Constants.ARG_PROMO_ID, promoId));
    }

    /**
     * Opens one print guideline.
     *
     * @param nav         the navigation controller
     * @param guidelineId the guideline
     */
    public static void guidelineDetail(NavController nav, long guidelineId) {
        nav.navigate(R.id.guidelineDetailFragment, idArgs(Constants.ARG_GUIDELINE_ID, guidelineId));
    }

    /**
     * Opens the contact screen, optionally about an order.
     *
     * @param nav     the navigation controller
     * @param orderId the order, or 0
     */
    public static void contact(NavController nav, long orderId) {
        nav.navigate(R.id.contactFragment, idArgs(Constants.ARG_ORDER_ID, orderId));
    }

    /**
     * Opens the full-screen gallery viewer.
     *
     * @param nav        the navigation controller
     * @param categoryId the category filter, or 0 for all
     * @param position   the first sample to show
     */
    public static void galleryViewer(NavController nav, long categoryId, int position) {
        Bundle args = idArgs(Constants.ARG_CATEGORY_ID, categoryId);
        args.putInt(Constants.ARG_POSITION, position);
        nav.navigate(R.id.galleryViewerFragment, args);
    }

    /**
     * Builds a bundle holding one id.
     *
     * @param key the argument name
     * @param id  the id
     * @return the bundle
     */
    private static Bundle idArgs(String key, long id) {
        Bundle args = new Bundle();
        args.putLong(key, id);
        return args;
    }
}
