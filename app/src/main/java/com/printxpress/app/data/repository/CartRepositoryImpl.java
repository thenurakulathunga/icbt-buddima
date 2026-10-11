package com.printxpress.app.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.R;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.core.concurrency.AppExecutors;
import com.printxpress.app.core.validation.rules.RangeRule;
import com.printxpress.app.core.validation.rules.StepRule;
import com.printxpress.app.data.dao.CartDao;
import com.printxpress.app.data.dao.DesignDao;
import com.printxpress.app.data.dao.ProductDao;
import com.printxpress.app.data.model.CartItem;
import com.printxpress.app.data.model.Design;
import com.printxpress.app.data.model.Product;
import com.printxpress.app.data.model.ProductOption;

import java.util.List;

/**
 * SQLite-backed {@link CartRepository}.
 *
 * @since 1.0
 */
public class CartRepositoryImpl extends BaseRepository implements CartRepository {

    /** Cart access. */
    private final CartDao cartDao;
    /** Product access. */
    private final ProductDao productDao;
    /** Design access. */
    private final DesignDao designDao;
    /** Badge count. */
    private final MutableLiveData<Integer> count = new MutableLiveData<>(0);

    /**
     * Creates the repository.
     *
     * @param executors  background executors
     * @param cartDao    cart DAO
     * @param productDao product DAO
     * @param designDao  design DAO
     */
    public CartRepositoryImpl(AppExecutors executors, CartDao cartDao, ProductDao productDao, DesignDao designDao) {
        super(executors);
        this.cartDao = cartDao;
        this.productDao = productDao;
        this.designDao = designDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<List<CartItem>>> getCart(long userId) {
        return execute(() -> cartDao.findByUser(userId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<CartItem>> getItem(long cartItemId) {
        return execute(() -> cartDao.findById(cartItemId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<Long>> save(CartItem item) {
        return execute(() -> {
            Product product = productDao.findById(item.getProductId());
            require(product != null && product.isActive(), R.string.err_product_unavailable);
            requireQuantity(product, item.getQuantity());
            require(item.hasArtwork(), R.string.err_design_required);
            require(item.getCustomText() == null || item.getCustomText().length() <= Constants.MAX_CUSTOM_TEXT,
                    R.string.err_text_too_long);
            if (item.getDesignId() != null) {
                Design design = designDao.findById(item.getDesignId());
                require(design != null && design.getUserId() == item.getUserId(), R.string.err_design_required);
            }
            for (ProductOption option : item.getOptions()) {
                require(option.getProductId() == product.getProductId(), R.string.err_product_unavailable);
            }
            long id = cartDao.save(item);
            count.postValue(cartDao.count(item.getUserId()));
            return id;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<Boolean>> updateQuantity(long userId, long cartItemId, int quantity) {
        return execute(() -> {
            CartItem item = cartDao.findById(cartItemId);
            require(item != null && item.getUserId() == userId && item.getProduct() != null, R.string.err_generic);
            requireQuantity(item.getProduct(), quantity);
            return cartDao.updateQuantity(userId, cartItemId, quantity);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<Boolean>> remove(long userId, long cartItemId) {
        return execute(() -> {
            boolean removed = cartDao.delete(userId, cartItemId);
            count.postValue(cartDao.count(userId));
            return removed;
        });
    }

    /** @return the observable cart line count */
    @Override
    public LiveData<Integer> getCartCount() {
        return count;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshCount(long userId) {
        if (userId < 0) {
            count.postValue(0);
            return;
        }
        executors.diskIO().execute(() -> count.postValue(cartDao.count(userId)));
    }

    /**
     * Applies BR-3 to a quantity.
     *
     * @param product  the product with its limits
     * @param quantity the quantity
     */
    static void requireQuantity(Product product, int quantity) {
        String value = String.valueOf(quantity);
        boolean inRange = new RangeRule(product.getMinQty(), product.getMaxQty(), 0).validate(value).isValid();
        boolean onStep = new StepRule(product::getMinQty, product::getQtyStep, 0).validate(value).isValid();
        require(inRange && onStep, R.string.err_qty_invalid);
    }
}
