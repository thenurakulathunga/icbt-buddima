package com.printxpress.app.ui.configure;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFormViewModel;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.core.common.Event;
import com.printxpress.app.core.common.Money;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.core.storage.DesignFile;
import com.printxpress.app.core.validation.ValidationRule;
import com.printxpress.app.core.validation.rules.LengthRule;
import com.printxpress.app.core.validation.rules.RangeRule;
import com.printxpress.app.core.validation.rules.RequiredRule;
import com.printxpress.app.core.validation.rules.StepRule;
import com.printxpress.app.data.model.CartItem;
import com.printxpress.app.data.model.Design;
import com.printxpress.app.data.model.Product;
import com.printxpress.app.data.model.ProductOption;
import com.printxpress.app.data.repository.AuthRepository;
import com.printxpress.app.data.repository.CartRepository;
import com.printxpress.app.data.repository.CatalogRepository;
import com.printxpress.app.data.repository.DesignRepository;
import com.printxpress.app.domain.pricing.LineQuote;
import com.printxpress.app.domain.pricing.PriceBreakdown;
import com.printxpress.app.domain.pricing.PriceCalculator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Shared state of the configure (S11) and design input (S12) screens: options, quantity and a
 * live price (FR-2.5), then artwork – an uploaded file, a saved design or custom text (FR-2.6,
 * FR-2.7, BR-9, BR-10) – and adding or updating the cart line (FR-2.8).
 * <p>Scoped to the {@code nav_configure} graph so both screens share it.</p>
 *
 * @since 1.0
 */
public class ConfigureViewModel extends BaseFormViewModel {

    /** Field key: quantity. */
    public static final String FIELD_QTY = "qty";
    /** Field key: custom text. */
    public static final String FIELD_TEXT = "text";
    /** Field key: instructions. */
    public static final String FIELD_INSTRUCTIONS = "instructions";
    /** Design mode: upload a file or pick a saved design. */
    public static final int MODE_UPLOAD = 0;
    /** Design mode: type custom text. */
    public static final int MODE_TEXT = 1;

    /** Session. */
    private final AuthRepository auth;
    /** Catalogue. */
    private final CatalogRepository catalog;
    /** Cart. */
    private final CartRepository cart;
    /** Designs. */
    private final DesignRepository designs;
    /** Pricing. */
    private final PriceCalculator calculator;

    /** Product. */
    private final MutableLiveData<Resource<Product>> product = new MutableLiveData<>();
    /** Options grouped by type name. */
    private final MutableLiveData<Map<String, List<ProductOption>>> optionsByType = new MutableLiveData<>();
    /** Live price. */
    private final MutableLiveData<PriceBreakdown> price = new MutableLiveData<>(PriceBreakdown.EMPTY);
    /** Picked but not yet saved file. */
    private final MutableLiveData<DesignFile> pickedFile = new MutableLiveData<>();
    /** Chosen saved design. */
    private final MutableLiveData<Design> savedDesign = new MutableLiveData<>();
    /** Saved designs to pick from. */
    private final MutableLiveData<Resource<List<Design>>> library = new MutableLiveData<>();
    /** Artwork error (BR-9, BR-10), 0 when none. */
    private final MutableLiveData<Integer> designError = new MutableLiveData<>(0);
    /** Design mode. */
    private final MutableLiveData<Integer> designMode = new MutableLiveData<>(MODE_UPLOAD);
    /** Whether the file is being read. */
    private final MutableLiveData<Boolean> fileLoading = new MutableLiveData<>(false);
    /** One-shot signal that the cart was updated. */
    private final MutableLiveData<Event<Boolean>> added = new MutableLiveData<>();
    /** Selected option per type name. */
    private final Map<String, ProductOption> selected = new LinkedHashMap<>();
    /** Cart line being edited, 0 for a new line. */
    private long cartItemId;
    /** Product being configured. */
    private long productId = -1;
    /** Options restored from a cart line, applied once options load. */
    private List<ProductOption> restoreOptions;

    /**
     * Creates the ViewModel.
     *
     * @param auth       auth repository
     * @param catalog    catalogue repository
     * @param cart       cart repository
     * @param designs    design repository
     * @param calculator price calculator
     */
    public ConfigureViewModel(AuthRepository auth, CatalogRepository catalog, CartRepository cart,
                              DesignRepository designs, PriceCalculator calculator) {
        this.auth = auth;
        this.catalog = catalog;
        this.cart = cart;
        this.designs = designs;
        this.calculator = calculator;
        validator.field(FIELD_QTY, new RequiredRule(R.string.err_qty_range),
                new RangeRule(() -> current().getMinQty(), () -> current().getMaxQty(), R.string.err_qty_range),
                new StepRule(() -> current().getMinQty(), () -> current().getQtyStep(), R.string.err_qty_range));
        validator.field(FIELD_TEXT, new LengthRule(0, Constants.MAX_CUSTOM_TEXT, R.string.err_text_too_long));
        validator.field(FIELD_INSTRUCTIONS, new LengthRule(0, Constants.MAX_INSTRUCTIONS, R.string.err_note_too_long));
    }

    /**
     * Loads the product (and the cart line when editing) once.
     *
     * @param id       the product
     * @param lineId   the cart line to edit, or 0
     */
    public void start(long id, long lineId) {
        if (productId == id && cartItemId == lineId) {
            return;
        }
        productId = id;
        cartItemId = lineId;
        subscribe(catalog.getProduct(id), r -> {
            product.setValue(r);
            if (r.isSuccess() && lineId == 0) {
                setValue(FIELD_QTY, String.valueOf(r.getData().getMinQty()));
                recalculate();
            }
        });
        subscribe(catalog.getOptions(id), r -> {
            if (r.isSuccess()) {
                groupOptions(r.getData());
            }
        });
        if (lineId > 0) {
            subscribe(cart.getItem(lineId), r -> {
                if (r.isSuccess() && r.getData() != null) {
                    restore(r.getData());
                }
            });
        }
        if (auth.isLoggedIn()) {
            load(designs.getDesigns(auth.currentUserId()), library);
        }
    }

    /**
     * Groups options by type and pre-selects defaults (or restored choices).
     *
     * @param options the product's options
     */
    private void groupOptions(List<ProductOption> options) {
        Map<String, List<ProductOption>> grouped = new LinkedHashMap<>();
        for (ProductOption option : options) {
            grouped.computeIfAbsent(option.getOptionTypeName(), k -> new ArrayList<>()).add(option);
            if (option.isDefaultOption() || !selected.containsKey(option.getOptionTypeName())) {
                selected.put(option.getOptionTypeName(), option);
            }
        }
        applyRestoredOptions();
        optionsByType.setValue(grouped);
        recalculate();
    }

    /**
     * Copies a cart line into the form.
     *
     * @param item the cart line
     */
    private void restore(CartItem item) {
        setValue(FIELD_QTY, String.valueOf(item.getQuantity()));
        setValue(FIELD_TEXT, item.getCustomText() == null ? "" : item.getCustomText());
        setValue(FIELD_INSTRUCTIONS, item.getInstructions() == null ? "" : item.getInstructions());
        restoreOptions = item.getOptions();
        applyRestoredOptions();
        if (item.getDesign() != null && item.getDesign().getFilePath() != null) {
            savedDesign.setValue(item.getDesign());
            designMode.setValue(MODE_UPLOAD);
        } else {
            designMode.setValue(MODE_TEXT);
        }
        recalculate();
    }

    /** Applies options restored from a cart line when both have loaded. */
    private void applyRestoredOptions() {
        if (restoreOptions == null) {
            return;
        }
        for (ProductOption option : restoreOptions) {
            selected.put(option.getOptionTypeName(), option);
        }
        if (optionsByType.getValue() != null) {
            optionsByType.setValue(optionsByType.getValue());
        }
    }

    /**
     * Records a chosen option and reprices.
     *
     * @param option the option
     */
    public void selectOption(ProductOption option) {
        if (option != null) {
            selected.put(option.getOptionTypeName(), option);
            recalculate();
        }
    }

    /**
     * Records the typed quantity and reprices.
     *
     * @param text the quantity text
     */
    public void setQuantity(String text) {
        setValue(FIELD_QTY, text);
        recalculate();
    }

    /** Recomputes the live price; invalid quantities price as zero. */
    private void recalculate() {
        Resource<Product> r = product.getValue();
        if (r == null || !r.isSuccess()) {
            return;
        }
        int qty = parseQuantity();
        if (qty <= 0 || !validator.validate(FIELD_QTY, value(FIELD_QTY)).isValid()) {
            price.setValue(PriceBreakdown.EMPTY);
            return;
        }
        LineQuote quote = calculator.quoteLine(r.getData(), selectedOptions(), qty);
        price.setValue(new PriceBreakdown(quote.getGross(), quote.getBulkDiscount(), Money.ZERO, null, Money.ZERO, false));
    }

    /**
     * Validates the configure step before moving to the design step.
     *
     * @return true when the quantity is valid
     */
    public boolean validateConfiguration() {
        validateField(FIELD_QTY);
        return validator.validate(FIELD_QTY, value(FIELD_QTY)).isValid();
    }

    /**
     * Switches between upload and text.
     *
     * @param mode {@link #MODE_UPLOAD} or {@link #MODE_TEXT}
     */
    public void setDesignMode(int mode) {
        designMode.setValue(mode);
        designError.setValue(0);
    }

    /**
     * Reads and validates a picked file (BR-10); it is copied only when added to the cart.
     *
     * @param uri the picked content URI
     */
    public void onFilePicked(Uri uri) {
        fileLoading.setValue(true);
        subscribe(designs.describe(uri), r -> {
            if (r.isLoading()) {
                return;
            }
            fileLoading.setValue(false);
            if (r.isSuccess()) {
                pickedFile.setValue(r.getData());
                savedDesign.setValue(null);
                designError.setValue(0);
            } else {
                pickedFile.setValue(null);
                designError.setValue(r.getMessageRes());
            }
        });
    }

    /**
     * Chooses a saved design instead of a new upload.
     *
     * @param design the design
     */
    public void selectSavedDesign(Design design) {
        savedDesign.setValue(design);
        pickedFile.setValue(null);
        designError.setValue(0);
    }

    /** Removes the picked file or saved design. */
    public void clearArtwork() {
        pickedFile.setValue(null);
        savedDesign.setValue(null);
    }

    /** Validates the design step and adds (or updates) the cart line. */
    public void addToCart() {
        boolean formValid = validateAll();
        boolean hasArtwork = hasArtwork();
        designError.setValue(hasArtwork ? 0 : R.string.err_design_required);
        if (!formValid || !hasArtwork) {
            return;
        }
        DesignFile file = pickedFile.getValue();
        if (isUploadMode() && file != null) {
            subscribe(designs.saveUpload(auth.currentUserId(), file, null), r -> {
                if (r.isSuccess()) {
                    saveLine(r.getData().getDesignId());
                } else if (r.isError()) {
                    designError.setValue(r.getMessageRes());
                }
            }, true);
        } else {
            Design design = savedDesign.getValue();
            saveLine(isUploadMode() && design != null ? design.getDesignId() : null);
        }
    }

    /** @return true when BR-9 is met for the current mode */
    private boolean hasArtwork() {
        if (isUploadMode()) {
            return pickedFile.getValue() != null || savedDesign.getValue() != null;
        }
        return !ValidationRule.isBlank(value(FIELD_TEXT));
    }

    /**
     * Writes the cart line.
     *
     * @param designId the design, or null for text only
     */
    private void saveLine(Long designId) {
        CartItem item = new CartItem.Builder(auth.currentUserId(), productId).id(cartItemId).quantity(parseQuantity())
                .design(designId).customText(isUploadMode() ? null : value(FIELD_TEXT))
                .instructions(value(FIELD_INSTRUCTIONS)).options(selectedOptions()).build();
        subscribe(cart.save(item), r -> {
            if (r.isSuccess()) {
                added.setValue(new Event<>(true));
            } else if (r.isError()) {
                postError(r.getMessageRes());
            }
        }, true);
    }

    /** @return true in upload mode */
    private boolean isUploadMode() {
        Integer mode = designMode.getValue();
        return mode == null || mode == MODE_UPLOAD;
    }

    /** @return the chosen options in type order */
    public List<ProductOption> selectedOptions() {
        return new ArrayList<>(selected.values());
    }

    /**
     * Returns the chosen option of one type.
     *
     * @param typeName the option type
     * @return the option, or null
     */
    public ProductOption selectedFor(String typeName) {
        return selected.get(typeName);
    }

    /** @return the typed quantity, or 0 when not a number */
    private int parseQuantity() {
        try {
            return Integer.parseInt(value(FIELD_QTY).trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    /** @return the loaded product, or an empty placeholder */
    private Product current() {
        Resource<Product> r = product.getValue();
        return r != null && r.isSuccess() ? r.getData() : new Product();
    }

    /** @return true when editing an existing cart line */
    public boolean isEditing() {
        return cartItemId > 0;
    }

    /** @return the product */
    public LiveData<Resource<Product>> getProduct() {
        return product;
    }

    /** @return options grouped by type */
    public LiveData<Map<String, List<ProductOption>>> getOptionsByType() {
        return optionsByType;
    }

    /** @return the live price */
    public LiveData<PriceBreakdown> getPrice() {
        return price;
    }

    /** @return the picked file */
    public LiveData<DesignFile> getPickedFile() {
        return pickedFile;
    }

    /** @return the chosen saved design */
    public LiveData<Design> getSavedDesign() {
        return savedDesign;
    }

    /** @return saved designs */
    public LiveData<Resource<List<Design>>> getLibrary() {
        return library;
    }

    /** @return the artwork error, 0 when none */
    public LiveData<Integer> getDesignError() {
        return designError;
    }

    /** @return the design mode */
    public LiveData<Integer> getDesignMode() {
        return designMode;
    }

    /** @return whether the picked file is being read */
    public LiveData<Boolean> getFileLoading() {
        return fileLoading;
    }

    /** @return fires once after the cart was updated */
    public LiveData<Event<Boolean>> getAdded() {
        return added;
    }
}
