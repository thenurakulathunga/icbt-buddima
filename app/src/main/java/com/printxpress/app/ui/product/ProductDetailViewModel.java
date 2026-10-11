package com.printxpress.app.ui.product;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.core.base.BaseViewModel;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.data.model.GalleryItem;
import com.printxpress.app.data.model.Product;
import com.printxpress.app.data.model.ProductOption;
import com.printxpress.app.data.repository.CatalogRepository;
import com.printxpress.app.data.repository.GalleryRepository;
import com.printxpress.app.domain.pricing.LineQuote;
import com.printxpress.app.domain.pricing.PriceCalculator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

/**
 * Product detail state (S10, FR-2.4): product, options grouped by type, sample designs and a price
 * guide computed with the product's default options at the minimum and bulk-tier quantities.
 *
 * @since 1.0
 */
public class ProductDetailViewModel extends BaseViewModel {

    /**
     * One price guide row.
     */
    public static final class PriceRow {

        /** Quantity. */
        private final int quantity;
        /** Quote at that quantity. */
        private final LineQuote quote;

        /**
         * Creates a row.
         *
         * @param quantity the quantity
         * @param quote    the quote
         */
        PriceRow(int quantity, LineQuote quote) {
            this.quantity = quantity;
            this.quote = quote;
        }

        /** @return the quantity */
        public int getQuantity() {
            return quantity;
        }

        /** @return the quote */
        public LineQuote getQuote() {
            return quote;
        }
    }

    /** Catalogue. */
    private final CatalogRepository catalog;
    /** Gallery. */
    private final GalleryRepository gallery;
    /** Pricing. */
    private final PriceCalculator calculator;
    /** The product. */
    private final MutableLiveData<Resource<Product>> product = new MutableLiveData<>();
    /** Options grouped by type name. */
    private final MutableLiveData<Map<String, List<ProductOption>>> optionsByType = new MutableLiveData<>();
    /** Price guide. */
    private final MutableLiveData<List<PriceRow>> priceRows = new MutableLiveData<>();
    /** Samples in the product's category. */
    private final MutableLiveData<Resource<List<GalleryItem>>> samples = new MutableLiveData<>();
    /** Loaded options. */
    private List<ProductOption> options;
    /** Product id being shown. */
    private long productId = -1;

    /**
     * Creates the ViewModel.
     *
     * @param catalog    catalogue repository
     * @param gallery    gallery repository
     * @param calculator price calculator
     */
    public ProductDetailViewModel(CatalogRepository catalog, GalleryRepository gallery, PriceCalculator calculator) {
        this.catalog = catalog;
        this.gallery = gallery;
        this.calculator = calculator;
    }

    /**
     * Loads a product once.
     *
     * @param id the product id
     */
    public void start(long id) {
        if (productId == id) {
            return;
        }
        productId = id;
        subscribe(catalog.getProduct(id), r -> {
            product.setValue(r);
            if (r.isSuccess()) {
                load(gallery.getGallery(r.getData().getCategoryId()), samples);
                buildPriceRows();
            }
        });
        subscribe(catalog.getOptions(id), r -> {
            if (r.isSuccess()) {
                options = r.getData();
                Map<String, List<ProductOption>> grouped = new LinkedHashMap<>();
                for (ProductOption option : options) {
                    grouped.computeIfAbsent(option.getOptionTypeName(), k -> new ArrayList<>()).add(option);
                }
                optionsByType.setValue(grouped);
                buildPriceRows();
            }
        });
    }

    /** Retries after an error. */
    public void retry() {
        long id = productId;
        productId = -1;
        start(id);
    }

    /** Computes the price guide when both product and options are loaded. */
    private void buildPriceRows() {
        Resource<Product> r = product.getValue();
        if (r == null || !r.isSuccess() || options == null) {
            return;
        }
        Product p = r.getData();
        List<ProductOption> defaults = new ArrayList<>();
        for (ProductOption option : options) {
            if (option.isDefaultOption()) {
                defaults.add(option);
            }
        }
        TreeSet<Integer> quantities = new TreeSet<>();
        quantities.add(p.getMinQty());
        for (int tier : new int[]{Constants.BULK_TIER_1_QTY, Constants.BULK_TIER_2_QTY}) {
            int snapped = snap(p, tier);
            if (snapped <= p.getMaxQty()) {
                quantities.add(snapped);
            }
        }
        List<PriceRow> rows = new ArrayList<>();
        for (int qty : quantities) {
            rows.add(new PriceRow(qty, calculator.quoteLine(p, defaults, qty)));
        }
        priceRows.setValue(rows);
    }

    /**
     * Rounds a quantity up to the product's step grid.
     *
     * @param p   the product
     * @param qty the target quantity
     * @return the nearest valid quantity at or above it
     */
    static int snap(Product p, int qty) {
        if (qty <= p.getMinQty()) {
            return p.getMinQty();
        }
        int step = Math.max(1, p.getQtyStep());
        int steps = (qty - p.getMinQty() + step - 1) / step;
        return p.getMinQty() + steps * step;
    }

    /** @return the product */
    public LiveData<Resource<Product>> getProduct() {
        return product;
    }

    /** @return options grouped by type */
    public LiveData<Map<String, List<ProductOption>>> getOptionsByType() {
        return optionsByType;
    }

    /** @return the price guide */
    public LiveData<List<PriceRow>> getPriceRows() {
        return priceRows;
    }

    /** @return sample designs */
    public LiveData<Resource<List<GalleryItem>>> getSamples() {
        return samples;
    }
}
