package com.printxpress.app.ui.catalog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.databinding.FragmentProductListBinding;
import com.printxpress.app.ui.adapters.ProductAdapter;
import com.printxpress.app.ui.components.SpacingItemDecoration;
import com.printxpress.app.ui.main.MainViewModel;
import com.printxpress.app.ui.main.ShellRoutes;
import com.printxpress.app.ui.main.TopBarBinder;

/**
 * Product list (S08): search, filter and sort products of one category or of the whole catalogue.
 *
 * @since 1.0
 */
public class ProductListFragment extends BaseFragment<FragmentProductListBinding> {

    /** Screen state, shared with {@link FilterSheet}. */
    private ProductListViewModel viewModel;
    /** Product grid. */
    private final ProductAdapter adapter = new ProductAdapter(false);

    /** Creates the screen; the framework instantiates it with no arguments. */
    public ProductListFragment() {
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
    protected FragmentProductListBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentProductListBinding.inflate(inflater, container, false);
    }

    /**
     * Reads arguments and sets up the grid.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(ProductListViewModel.class);
        Bundle args = requireArguments();
        String title = args.getString(Constants.ARG_TITLE);
        String query = args.getString(Constants.ARG_QUERY);
        binding.topBar.setTitle(title == null ? getString(R.string.products_search_title) : title);
        binding.topBar.setOnBackClick(() -> nav().navigateUp());
        TopBarBinder.bind(binding.topBar, activityViewModel(MainViewModel.class), getViewLifecycleOwner(), nav());
        binding.listProducts.setLayoutManager(new GridLayoutManager(requireContext(),
                getResources().getInteger(R.integer.product_grid_span)));
        binding.listProducts.addItemDecoration(new SpacingItemDecoration(getResources().getDimensionPixelSize(R.dimen.space_md)));
        binding.listProducts.setAdapter(adapter);
        viewModel.start(args.getLong(Constants.ARG_CATEGORY_ID), query);
        binding.searchBar.setQuery(viewModel.current().getQuery());
        if (query != null && query.isEmpty() && saved == null) {
            binding.searchBar.focusInput();
        }
    }

    /** Renders results, count and filter badge. */
    @Override
    protected void initObservers() {
        observe(viewModel.getFilter(), f -> binding.searchBar.setFilterCount(f.getActiveOptionCount()
                + (f.getSort() == com.printxpress.app.data.model.ProductFilter.Sort.POPULAR ? 0 : 1)));
        observe(viewModel.getProducts(), r -> {
            if (r.isLoading()) {
                binding.stateLayout.showLoading();
            } else if (r.isError()) {
                binding.stateLayout.showError(r.getMessageRes(), viewModel::retry);
            } else if (r.getData().isEmpty()) {
                binding.txtCount.setText(getResources().getQuantityString(R.plurals.result_count, 0, 0));
                binding.stateLayout.showEmpty(R.drawable.ic_search, R.string.empty_search_title,
                        R.string.empty_search_message, R.string.action_clear_filters, () -> {
                            binding.searchBar.setQuery("");
                            viewModel.setQuery("");
                            viewModel.clearFilters();
                        });
            } else {
                int n = r.getData().size();
                binding.txtCount.setText(getResources().getQuantityString(R.plurals.result_count, n, n));
                adapter.submitList(r.getData());
                binding.stateLayout.showContent();
            }
        });
    }

    /** Wires search, filter and item taps. */
    @Override
    protected void initListeners() {
        binding.searchBar.setOnQueryChanged(viewModel::setQuery);
        binding.searchBar.setOnSubmit(viewModel::setQuery);
        binding.searchBar.setOnFilterClick(() -> new FilterSheet().show(getChildFragmentManager()));
        adapter.setOnItemClickListener((product, p) -> ShellRoutes.productDetail(nav(), product.getProductId()));
    }
}
