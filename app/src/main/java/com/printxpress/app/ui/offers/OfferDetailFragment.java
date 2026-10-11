package com.printxpress.app.ui.offers;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.core.common.Money;
import com.printxpress.app.core.util.ClipboardUtils;
import com.printxpress.app.core.util.CurrencyFormatter;
import com.printxpress.app.core.util.DateTimeUtils;
import com.printxpress.app.data.model.Promotion;
import com.printxpress.app.data.model.enums.DiscountType;
import com.printxpress.app.databinding.FragmentOfferDetailBinding;
import com.printxpress.app.ui.components.SnackbarHelper;
import com.printxpress.app.ui.main.ShellRoutes;

import java.text.DecimalFormat;

/**
 * Offer detail (S23, FR-4.5): discount, validity, minimum and scope, with copy code and a shortcut
 * to the matching products. Also the target of promotion notifications.
 *
 * @since 1.0
 */
public class OfferDetailFragment extends BaseFragment<FragmentOfferDetailBinding> {

    /** Screen state. */
    private OffersViewModel viewModel;
    /** The shown promotion, null until loaded. */
    private Promotion promotion;

    /** Creates the screen; the framework instantiates it with no arguments. */
    public OfferDetailFragment() {
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
    protected FragmentOfferDetailBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentOfferDetailBinding.inflate(inflater, container, false);
    }

    /**
     * Loads the promotion.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(OffersViewModel.class);
        binding.topBar.setOnBackClick(() -> nav().navigateUp());
        viewModel.loadOffer(requireArguments().getLong(Constants.ARG_PROMO_ID));
    }

    /** Renders the promotion. */
    @Override
    protected void initObservers() {
        observe(viewModel.getOffer(), r -> {
            if (r.isLoading()) {
                binding.stateLayout.showLoading();
            } else if (r.isError()) {
                binding.stateLayout.showError(r.getMessageRes(),
                        () -> viewModel.loadOffer(requireArguments().getLong(Constants.ARG_PROMO_ID)));
            } else {
                render(r.getData());
                binding.stateLayout.showContent();
            }
        });
    }

    /**
     * Fills the card and terms.
     *
     * @param p the promotion
     */
    private void render(Promotion p) {
        promotion = p;
        binding.topBar.setTitle(p.getTitle());
        binding.cardPromo.bind(p);
        binding.btnCopy.setText(getString(R.string.action_copy_code_value, p.getPromoCode()));
        String amount = p.getDiscountType() == DiscountType.PERCENT
                ? getString(R.string.offer_percent, new DecimalFormat("0.##").format(p.getDiscountValue()))
                : getString(R.string.offer_fixed, CurrencyFormatter.format(Money.of(p.getDiscountValue())));
        binding.tableTerms.clear();
        binding.tableTerms.addRow(p.getDescription());
        binding.tableTerms.addRow(getString(R.string.promo_code_label, p.getPromoCode()), amount);
        binding.tableTerms.addRow(getString(R.string.offer_valid, DateTimeUtils.formatDisplay(p.getStartDate()),
                DateTimeUtils.formatDisplay(p.getEndDate())));
        if (p.getMinOrderValue() != null && p.getMinOrderValue().isPositive()) {
            binding.tableTerms.addRow(getString(R.string.offer_min_order, CurrencyFormatter.format(p.getMinOrderValue())));
        }
        binding.tableTerms.addRow(p.getCategoryId() == null ? getString(R.string.offer_all_products)
                : getString(R.string.offer_category, p.getCategoryName()));
    }

    /** Wires the actions. */
    @Override
    protected void initListeners() {
        binding.cardPromo.setOnCodeClick(this::copy);
        binding.btnCopy.setOnClickListener(v -> {
            if (promotion != null) {
                copy(promotion.getPromoCode());
            }
        });
        binding.btnShop.setOnClickListener(v -> {
            if (promotion == null) {
                return;
            }
            long category = promotion.getCategoryId() == null ? 0 : promotion.getCategoryId();
            String title = promotion.getCategoryId() == null ? getString(R.string.products_all) : promotion.getCategoryName();
            ShellRoutes.productList(nav(), category, title, null);
        });
    }

    /**
     * Copies a code and confirms it.
     *
     * @param code the promo code
     */
    private void copy(String code) {
        ClipboardUtils.copy(requireContext(), getString(R.string.app_name), code);
        SnackbarHelper.info(binding.getRoot(), getString(R.string.copied_code, code));
    }
}
