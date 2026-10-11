package com.printxpress.app.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.core.util.ClipboardUtils;
import com.printxpress.app.data.model.Order;
import com.printxpress.app.databinding.FragmentHomeBinding;
import com.printxpress.app.ui.adapters.CategoryAdapter;
import com.printxpress.app.ui.adapters.GalleryAdapter;
import com.printxpress.app.ui.adapters.ProductAdapter;
import com.printxpress.app.ui.components.SnackbarHelper;
import com.printxpress.app.ui.components.SpacingItemDecoration;
import com.printxpress.app.ui.main.MainViewModel;
import com.printxpress.app.ui.main.ShellRoutes;
import com.printxpress.app.ui.main.TopBarBinder;

import java.util.List;

/**
 * Home screen (S06): the start of the "3 taps to start an order" path (NFR-3): category or product
 * tap, then Configure.
 *
 * @since 1.0
 */
public class HomeFragment extends BaseFragment<FragmentHomeBinding> {

    /** Screen state. */
    private HomeViewModel viewModel;
    /** Category row. */
    private final CategoryAdapter categoryAdapter = new CategoryAdapter(true);
    /** Popular grid. */
    private final ProductAdapter productAdapter = new ProductAdapter(false);
    /** Inspiration strip. */
    private final GalleryAdapter galleryAdapter = new GalleryAdapter(true);

    /** Creates the screen; the framework instantiates it with no arguments. */
    public HomeFragment() {
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
    protected FragmentHomeBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentHomeBinding.inflate(inflater, container, false);
    }

    /**
     * Sets up lists and the top bar.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(HomeViewModel.class);
        TopBarBinder.bind(binding.topBar, activityViewModel(MainViewModel.class), getViewLifecycleOwner(), nav());
        int gap = getResources().getDimensionPixelSize(R.dimen.space_md);
        setupList(binding.listCategories, new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false),
                categoryAdapter, gap);
        setupList(binding.listPopular, new GridLayoutManager(requireContext(),
                getResources().getInteger(R.integer.product_grid_span)), productAdapter, gap);
        setupList(binding.listInspiration, new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false),
                galleryAdapter, gap);
    }

    /**
     * Attaches a layout manager, adapter and spacing.
     *
     * @param list    the RecyclerView
     * @param manager the layout manager
     * @param adapter the adapter
     * @param gap     the spacing in pixels
     */
    private static void setupList(RecyclerView list, RecyclerView.LayoutManager manager,
                                  RecyclerView.Adapter<?> adapter, int gap) {
        list.setLayoutManager(manager);
        list.setAdapter(adapter);
        list.addItemDecoration(new SpacingItemDecoration(gap));
    }

    /** Renders every section. */
    @Override
    protected void initObservers() {
        observe(viewModel.getUser(), r -> {
            if (!r.isLoading()) {
                binding.topBar.setTitle(r.getData() == null ? getString(R.string.home_greeting_guest)
                        : getString(R.string.home_greeting, firstName(r.getData().getFullName())));
            }
        });
        observe(viewModel.getPromotions(), r -> {
            if (r.isSuccess()) {
                binding.promoCarousel.setPromotions(r.getData());
            }
            binding.swipeRefresh.setRefreshing(r.isLoading());
        });
        observe(viewModel.getCategories(), r -> submit(r.getData(), categoryAdapter));
        observe(viewModel.getPopular(), r -> submit(r.getData(), productAdapter));
        observe(viewModel.getInspiration(), r -> submit(r.getData(), galleryAdapter));
        observe(viewModel.getActiveOrders(), r -> {
            List<Order> orders = r.getData();
            boolean show = r.isSuccess() && orders != null && !orders.isEmpty();
            binding.sectionActiveOrder.setVisibility(show ? View.VISIBLE : View.GONE);
            if (show) {
                Order first = orders.get(0);
                binding.cardActiveOrder.bind(first);
                binding.cardActiveOrder.setOnClickListener(v -> ShellRoutes.orderDetail(nav(), first.getOrderId()));
            }
        });
    }

    /**
     * Submits data to an adapter when present.
     *
     * @param items   the items, may be null while loading
     * @param adapter the adapter
     * @param <T>     the item type
     */
    private static <T> void submit(List<T> items, androidx.recyclerview.widget.ListAdapter<T, ?> adapter) {
        if (items != null) {
            adapter.submitList(items);
        }
    }

    /** Wires taps. */
    @Override
    protected void initListeners() {
        binding.swipeRefresh.setOnRefreshListener(viewModel::refresh);
        binding.searchBar.setClickOnly(() -> ShellRoutes.productList(nav(), 0, getString(R.string.products_all), ""));
        binding.headerCategories.setOnActionClick(() -> nav().navigate(R.id.catalogFragment));
        binding.headerPopular.setOnActionClick(() -> ShellRoutes.productList(nav(), 0, getString(R.string.products_all), null));
        categoryAdapter.setOnItemClickListener((c, p) -> ShellRoutes.productList(nav(), c.getCategoryId(), c.getName(), null));
        productAdapter.setOnItemClickListener((product, p) -> ShellRoutes.productDetail(nav(), product.getProductId()));
        binding.headerInspiration.setOnActionClick(() -> nav().navigate(R.id.galleryFragment));
        galleryAdapter.setOnItemClickListener((item, p) -> nav().navigate(R.id.galleryFragment));
        binding.promoCarousel.setOnPromoClick(promo -> ShellRoutes.offerDetail(nav(), promo.getPromoId()));
        binding.promoCarousel.setOnCodeClick(code -> {
            ClipboardUtils.copy(requireContext(), getString(R.string.app_name), code);
            SnackbarHelper.info(binding.getRoot(), getString(R.string.copied_code, code));
        });
    }

    /** Refreshes when returning, e.g. after placing an order. */
    @Override
    public void onResume() {
        super.onResume();
        viewModel.refresh();
    }

    /**
     * Returns the first word of a name.
     *
     * @param fullName the full name
     * @return the first name
     */
    private static String firstName(String fullName) {
        String trimmed = fullName == null ? "" : fullName.trim();
        int space = trimmed.indexOf(' ');
        return space > 0 ? trimmed.substring(0, space) : trimmed;
    }
}
