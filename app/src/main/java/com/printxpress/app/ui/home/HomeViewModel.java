package com.printxpress.app.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.core.base.BaseViewModel;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.data.model.Category;
import com.printxpress.app.data.model.GalleryItem;
import com.printxpress.app.data.model.Order;
import com.printxpress.app.data.model.Product;
import com.printxpress.app.data.model.Promotion;
import com.printxpress.app.data.model.User;
import com.printxpress.app.data.repository.AuthRepository;
import com.printxpress.app.data.repository.CatalogRepository;
import com.printxpress.app.data.repository.GalleryRepository;
import com.printxpress.app.data.repository.OrderRepository;
import com.printxpress.app.data.repository.PromotionRepository;
import com.printxpress.app.data.repository.UserRepository;

import java.util.List;

/**
 * Home screen state (S06): greeting, promotions, categories, popular products, gallery strip and
 * the customer's most recent active order.
 *
 * @since 1.0
 */
public class HomeViewModel extends BaseViewModel {

    /** Number of popular products shown. */
    private static final int POPULAR_LIMIT = 8;

    /** Session. */
    private final AuthRepository auth;
    /** Profile, for the greeting. */
    private final UserRepository users;
    /** Catalogue. */
    private final CatalogRepository catalog;
    /** Promotions. */
    private final PromotionRepository promotions;
    /** Gallery. */
    private final GalleryRepository gallery;
    /** Orders. */
    private final OrderRepository orders;

    /** Signed-in user. */
    private final MutableLiveData<Resource<User>> user = new MutableLiveData<>();
    /** Running promotions. */
    private final MutableLiveData<Resource<List<Promotion>>> promos = new MutableLiveData<>();
    /** Categories. */
    private final MutableLiveData<Resource<List<Category>>> categories = new MutableLiveData<>();
    /** Popular products. */
    private final MutableLiveData<Resource<List<Product>>> popular = new MutableLiveData<>();
    /** Gallery strip. */
    private final MutableLiveData<Resource<List<GalleryItem>>> inspiration = new MutableLiveData<>();
    /** Active orders. */
    private final MutableLiveData<Resource<List<Order>>> activeOrders = new MutableLiveData<>();

    /**
     * Creates the ViewModel.
     *
     * @param auth       auth repository
     * @param users      user repository
     * @param catalog    catalogue repository
     * @param promotions promotion repository
     * @param gallery    gallery repository
     * @param orders     order repository
     */
    public HomeViewModel(AuthRepository auth, UserRepository users, CatalogRepository catalog,
                         PromotionRepository promotions, GalleryRepository gallery, OrderRepository orders) {
        this.auth = auth;
        this.users = users;
        this.catalog = catalog;
        this.promotions = promotions;
        this.gallery = gallery;
        this.orders = orders;
    }

    /** Reloads every section. */
    public void refresh() {
        long userId = auth.currentUserId();
        if (userId > 0) {
            load(users.getUser(userId), user);
            load(orders.getActiveOrders(userId), activeOrders);
        } else {
            user.setValue(Resource.success(null));
            activeOrders.setValue(Resource.success(java.util.Collections.emptyList()));
        }
        load(promotions.getActivePromotions(), promos);
        load(catalog.getCategories(), categories);
        load(catalog.getPopularProducts(POPULAR_LIMIT), popular);
        load(gallery.getGallery(0), inspiration);
    }

    /** @return the signed-in user (null data for guests) */
    public LiveData<Resource<User>> getUser() {
        return user;
    }

    /** @return running promotions */
    public LiveData<Resource<List<Promotion>>> getPromotions() {
        return promos;
    }

    /** @return categories */
    public LiveData<Resource<List<Category>>> getCategories() {
        return categories;
    }

    /** @return popular products */
    public LiveData<Resource<List<Product>>> getPopular() {
        return popular;
    }

    /** @return gallery items */
    public LiveData<Resource<List<GalleryItem>>> getInspiration() {
        return inspiration;
    }

    /** @return active orders */
    public LiveData<Resource<List<Order>>> getActiveOrders() {
        return activeOrders;
    }
}
