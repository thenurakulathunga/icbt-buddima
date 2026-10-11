package com.printxpress.app.ui.product;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.core.util.CurrencyFormatter;
import com.printxpress.app.data.model.Product;
import com.printxpress.app.data.model.ProductOption;
import com.printxpress.app.databinding.FragmentProductDetailBinding;
import com.printxpress.app.ui.adapters.GalleryAdapter;
import com.printxpress.app.ui.components.ArtworkImageView;
import com.printxpress.app.ui.components.SpacingItemDecoration;
import com.printxpress.app.ui.main.MainViewModel;
import com.printxpress.app.ui.main.ShellRoutes;
import com.printxpress.app.ui.main.TopBarBinder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Product detail (S10, FR-2.4): images, description, specifications, price guide and samples,
 * with a sticky "Configure and order" action.
 *
 * @since 1.0
 */
public class ProductDetailFragment extends BaseFragment<FragmentProductDetailBinding> {

    /** Screen state. */
    private ProductDetailViewModel viewModel;
    /** Sample strip. */
    private final GalleryAdapter samplesAdapter = new GalleryAdapter(true);

    /** Creates the screen; the framework instantiates it with no arguments. */
    public ProductDetailFragment() {
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
    protected FragmentProductDetailBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentProductDetailBinding.inflate(inflater, container, false);
    }

    /**
     * Loads the product and sets up the samples strip.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(ProductDetailViewModel.class);
        binding.topBar.setOnBackClick(() -> nav().navigateUp());
        TopBarBinder.bind(binding.topBar, activityViewModel(MainViewModel.class), getViewLifecycleOwner(), nav());
        binding.listSamples.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        binding.listSamples.addItemDecoration(new SpacingItemDecoration(getResources().getDimensionPixelSize(R.dimen.space_md)));
        binding.listSamples.setAdapter(samplesAdapter);
        viewModel.start(requireArguments().getLong(Constants.ARG_PRODUCT_ID));
    }

    /** Renders the product sections. */
    @Override
    protected void initObservers() {
        observe(viewModel.getProduct(), r -> {
            if (r.isLoading()) {
                binding.stateLayout.showLoading();
                binding.bottomBar.setButtonEnabled(false);
            } else if (r.isError()) {
                binding.stateLayout.showError(r.getMessageRes(), viewModel::retry);
            } else {
                showProduct(r.getData());
                binding.stateLayout.showContent();
                binding.bottomBar.setButtonEnabled(true);
            }
        });
        observe(viewModel.getOptionsByType(), this::showSpecs);
        observe(viewModel.getPriceRows(), this::showPrices);
        observe(viewModel.getSamples(), r -> {
            boolean has = r.isSuccess() && !r.getData().isEmpty();
            binding.headerSamples.setVisibility(has ? View.VISIBLE : View.GONE);
            binding.listSamples.setVisibility(has ? View.VISIBLE : View.GONE);
            if (has) {
                samplesAdapter.submitList(r.getData());
            }
        });
    }

    /** Wires the configure action. */
    @Override
    protected void initListeners() {
        binding.bottomBar.setOnButtonClick(() -> ShellRoutes.configure(nav(),
                requireArguments().getLong(Constants.ARG_PRODUCT_ID), 0));
    }

    /**
     * Renders the header fields.
     *
     * @param product the product
     */
    private void showProduct(Product product) {
        binding.topBar.setTitle(product.getName());
        binding.imgHero.setArtwork(product.getImageRes(), ArtworkImageView.iconForCategory(product.getCategoryImageRes()));
        binding.txtCategory.setText(product.getCategoryName());
        binding.txtName.setText(product.getName());
        binding.chipTurnaround.setText(getResources().getQuantityString(R.plurals.detail_turnaround,
                product.getTurnaroundDays(), product.getTurnaroundDays()));
        binding.chipMinOrder.setText(getString(R.string.detail_min_order, product.getMinQty()));
        binding.txtDescription.setText(product.getDescription());
        binding.bottomBar.setPrice(CurrencyFormatter.format(product.getBasePrice().times(product.getMinQty())));
    }

    /**
     * Renders the specification table: one row per option type listing its values.
     *
     * @param byType options grouped by type
     */
    private void showSpecs(Map<String, List<ProductOption>> byType) {
        binding.tableSpecs.clear();
        for (Map.Entry<String, List<ProductOption>> entry : byType.entrySet()) {
            List<String> values = new ArrayList<>();
            for (ProductOption option : entry.getValue()) {
                values.add(option.getValue());
            }
            binding.tableSpecs.addRow(entry.getKey(), String.join(getString(R.string.list_separator), values));
        }
    }

    /**
     * Renders the price guide.
     *
     * @param rows the rows
     */
    private void showPrices(List<ProductDetailViewModel.PriceRow> rows) {
        binding.tablePrices.clear();
        binding.tablePrices.addHeader(getString(R.string.detail_qty_col), getString(R.string.detail_unit_col),
                getString(R.string.detail_total_col));
        for (ProductDetailViewModel.PriceRow row : rows) {
            binding.tablePrices.addRow(String.valueOf(row.getQuantity()),
                    CurrencyFormatter.format(row.getQuote().getLineTotal().dividedBy(row.getQuantity())),
                    CurrencyFormatter.format(row.getQuote().getLineTotal()));
        }
    }
}
