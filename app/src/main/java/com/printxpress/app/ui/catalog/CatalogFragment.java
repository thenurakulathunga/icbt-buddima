package com.printxpress.app.ui.catalog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.databinding.FragmentCatalogBinding;
import com.printxpress.app.ui.adapters.CategoryAdapter;
import com.printxpress.app.ui.components.SpacingItemDecoration;
import com.printxpress.app.ui.main.MainViewModel;
import com.printxpress.app.ui.main.ShellRoutes;
import com.printxpress.app.ui.main.TopBarBinder;

/**
 * Catalog tab (S07): every category as a grid; tapping opens its product list.
 *
 * @since 1.0
 */
public class CatalogFragment extends BaseFragment<FragmentCatalogBinding> {

    /** Screen state. */
    private CatalogViewModel viewModel;
    /** Category grid. */
    private final CategoryAdapter adapter = new CategoryAdapter(false);

    /** Creates the screen; the framework instantiates it with no arguments. */
    public CatalogFragment() {
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
    protected FragmentCatalogBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentCatalogBinding.inflate(inflater, container, false);
    }

    /**
     * Sets up the grid.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(CatalogViewModel.class);
        TopBarBinder.bind(binding.topBar, activityViewModel(MainViewModel.class), getViewLifecycleOwner(), nav());
        binding.listCategories.setLayoutManager(new GridLayoutManager(requireContext(),
                getResources().getInteger(R.integer.category_grid_span)));
        binding.listCategories.addItemDecoration(new SpacingItemDecoration(getResources().getDimensionPixelSize(R.dimen.space_md)));
        binding.listCategories.setAdapter(adapter);
    }

    /** Renders loading, error and content. */
    @Override
    protected void initObservers() {
        observe(viewModel.getCategories(), r -> {
            if (r.isLoading()) {
                binding.stateLayout.showLoading();
            } else if (r.isError()) {
                binding.stateLayout.showError(r.getMessageRes(), viewModel::refresh);
            } else {
                adapter.submitList(r.getData());
                binding.stateLayout.showContent();
            }
        });
    }

    /** Wires taps. */
    @Override
    protected void initListeners() {
        binding.searchBar.setClickOnly(() -> ShellRoutes.productList(nav(), 0, getString(R.string.products_all), ""));
        adapter.setOnItemClickListener((c, p) -> ShellRoutes.productList(nav(), c.getCategoryId(), c.getName(), null));
    }
}
