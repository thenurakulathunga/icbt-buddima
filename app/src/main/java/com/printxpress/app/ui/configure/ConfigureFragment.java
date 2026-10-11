package com.printxpress.app.ui.configure;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.core.util.CurrencyFormatter;
import com.printxpress.app.data.model.Product;
import com.printxpress.app.data.model.ProductOption;
import com.printxpress.app.databinding.FragmentConfigureBinding;
import com.printxpress.app.ui.components.ArtworkImageView;
import com.printxpress.app.ui.components.OptionSelectorGroup;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Configure product (S11, FR-2.5): one chip group per option type, quantity on the product's step
 * grid (BR-3), and a live price with the bulk discount (BR-4).
 *
 * @since 1.0
 */
public class ConfigureFragment extends BaseFragment<FragmentConfigureBinding> {

    /** Flow state shared with the design step. */
    private ConfigureViewModel viewModel;

    /** Creates the screen; the framework instantiates it with no arguments. */
    public ConfigureFragment() {
        super();
    }

    /**
     * Inflates the layout.
     *
     * @param inflater  layout inflater
     * @param container parent
     * @return the binding
     */
    @Override
    protected FragmentConfigureBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentConfigureBinding.inflate(inflater, container, false);
    }

    /**
     * Starts the flow for the product (or cart line) in the arguments.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = graphViewModel(R.id.nav_configure, ConfigureViewModel.class);
        binding.topBar.setOnBackClick(() -> nav().navigateUp());
        Bundle args = requireArguments();
        viewModel.start(args.getLong(Constants.ARG_PRODUCT_ID), args.getLong(Constants.ARG_CART_ITEM_ID));
    }

    /** Renders product, options, quantity errors and price. */
    @Override
    protected void initObservers() {
        observe(viewModel.getProduct(), r -> {
            if (r.isLoading()) {
                binding.stateLayout.showLoading();
            } else if (r.isError()) {
                binding.stateLayout.showError(r.getMessageRes(), () -> nav().navigateUp());
            } else {
                showProduct(r.getData());
                binding.stateLayout.showContent();
            }
        });
        observe(viewModel.getOptionsByType(), this::buildGroups);
        observe(viewModel.getPrice(), p -> {
            binding.priceSummary.bind(p);
            binding.bottomBar.setPrice(CurrencyFormatter.format(p.getTotal()));
        });
        observe(viewModel.getFieldErrors(), e -> {
            Integer error = e.getErrors().get(ConfigureViewModel.FIELD_QTY);
            Product p = currentProduct();
            binding.stepper.setError(error == null || p == null ? null
                    : getString(R.string.err_qty_range, p.getMinQty(), p.getMaxQty(), p.getQtyStep()));
        });
    }

    /** Wires the stepper and Continue. */
    @Override
    protected void initListeners() {
        binding.stepper.setOnValueChangedListener(v -> {
            viewModel.setQuantity(binding.stepper.getRawText());
            viewModel.validateField(ConfigureViewModel.FIELD_QTY);
        });
        binding.bottomBar.setOnButtonClick(() -> {
            if (viewModel.validateConfiguration()) {
                navigate(R.id.action_configure_to_design, null);
            }
        });
    }

    /**
     * Renders the product header and stepper limits.
     *
     * @param p the product
     */
    private void showProduct(Product p) {
        binding.imgProduct.setArtwork(p.getImageRes(), ArtworkImageView.iconForCategory(p.getCategoryImageRes()));
        binding.txtName.setText(p.getName());
        binding.txtUnit.setText(getString(R.string.price_each, CurrencyFormatter.format(p.getBasePrice()), p.getPriceUnit()));
        binding.stepper.setRange(p.getMinQty(), p.getMaxQty(), p.getQtyStep());
        binding.stepper.setValue(parse(viewModel.value(ConfigureViewModel.FIELD_QTY), p.getMinQty()));
        binding.txtQtyHint.setText(getString(R.string.configure_qty_hint, p.getMinQty(), p.getMaxQty(), p.getQtyStep()));
        binding.bannerBulk.setVisibility(p.getMaxQty() >= Constants.BULK_TIER_1_QTY
                ? android.view.View.VISIBLE : android.view.View.GONE);
    }

    /**
     * Builds one single-choice group per option type.
     *
     * @param byType options grouped by type name
     */
    private void buildGroups(Map<String, List<ProductOption>> byType) {
        binding.groups.removeAllViews();
        for (Map.Entry<String, List<ProductOption>> entry : byType.entrySet()) {
            OptionSelectorGroup group = new OptionSelectorGroup(requireContext());
            group.bind(entry.getKey(), entry.getValue(), true);
            ProductOption chosen = viewModel.selectedFor(entry.getKey());
            if (chosen != null) {
                group.selectOptionIds(Collections.singleton(chosen.getOptionId()));
            }
            group.setOnSelectionChanged(() -> viewModel.selectOption(group.getSelectedOption()));
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.topMargin = getResources().getDimensionPixelSize(R.dimen.space_lg);
            binding.groups.addView(group, params);
        }
    }

    /** @return the loaded product, or null */
    private Product currentProduct() {
        return viewModel.getProduct().getValue() == null ? null : viewModel.getProduct().getValue().getData();
    }

    /**
     * Parses a number with a fallback.
     *
     * @param text     the text
     * @param fallback the fallback
     * @return the number
     */
    private static int parse(String text, int fallback) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            return fallback;
        }
    }
}
