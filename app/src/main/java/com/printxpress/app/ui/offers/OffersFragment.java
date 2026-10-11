package com.printxpress.app.ui.offers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.core.util.ClipboardUtils;
import com.printxpress.app.databinding.FragmentOffersBinding;
import com.printxpress.app.ui.adapters.PromoAdapter;
import com.printxpress.app.ui.components.SnackbarHelper;
import com.printxpress.app.ui.components.SpacingItemDecoration;
import com.printxpress.app.ui.main.ShellRoutes;

/**
 * Offers and packages list (S22, FR-4.5): tap a card for details, tap the code to copy it (TC-56).
 *
 * @since 1.0
 */
public class OffersFragment extends BaseFragment<FragmentOffersBinding> {

    /** Screen state. */
    private OffersViewModel viewModel;
    /** The cards. */
    private final PromoAdapter adapter = new PromoAdapter();

    /** Creates the screen; the framework instantiates it with no arguments. */
    public OffersFragment() {
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
    protected FragmentOffersBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentOffersBinding.inflate(inflater, container, false);
    }

    /**
     * Sets up the list.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(OffersViewModel.class);
        binding.topBar.setOnBackClick(() -> nav().navigateUp());
        binding.listOffers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.listOffers.addItemDecoration(new SpacingItemDecoration(getResources().getDimensionPixelSize(R.dimen.space_md)));
        binding.listOffers.setAdapter(adapter);
        viewModel.refresh();
    }

    /** Renders the states. */
    @Override
    protected void initObservers() {
        observe(viewModel.getOffers(), r -> {
            if (r.isLoading()) {
                binding.stateLayout.showLoading();
            } else if (r.isError()) {
                binding.stateLayout.showError(r.getMessageRes(), viewModel::refresh);
            } else if (r.getData().isEmpty()) {
                binding.stateLayout.showEmpty(R.drawable.ic_offer, R.string.empty_offers_title,
                        R.string.empty_offers_message, 0, null);
            } else {
                adapter.submitList(r.getData());
                binding.stateLayout.showContent();
            }
        });
    }

    /** Wires taps. */
    @Override
    protected void initListeners() {
        adapter.setOnItemClickListener((promo, p) -> ShellRoutes.offerDetail(nav(), promo.getPromoId()));
        adapter.setOnCodeClick(code -> {
            ClipboardUtils.copy(requireContext(), getString(R.string.app_name), code);
            SnackbarHelper.info(binding.getRoot(), getString(R.string.copied_code, code));
        });
    }
}
