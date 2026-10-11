package com.printxpress.app.ui.support;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.printxpress.app.R;
import com.printxpress.app.databinding.FragmentSupportBinding;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.ui.main.MainViewModel;
import com.printxpress.app.ui.main.ShellRoutes;
import com.printxpress.app.ui.main.TopBarBinder;

/**
 * Support tab (S24, FR-5): the hub for design support resources and contacting the shop.
 *
 * @since 1.0
 */
public class SupportFragment extends BaseFragment<FragmentSupportBinding> {

    /** Creates the screen; the framework instantiates it with no arguments. */
    public SupportFragment() {
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
    protected FragmentSupportBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentSupportBinding.inflate(inflater, container, false);
    }

    /**
     * Binds the shell top bar.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        TopBarBinder.bind(binding.topBar, activityViewModel(MainViewModel.class), getViewLifecycleOwner(), nav());
    }

    /** Wires the menu rows. */
    @Override
    protected void initListeners() {
        binding.rowGuidelines.setOnClickListener(v -> navigate(R.id.guidelinesFragment, null));
        binding.rowFaqs.setOnClickListener(v -> navigate(R.id.faqsFragment, null));
        binding.rowGallery.setOnClickListener(v -> navigate(R.id.galleryFragment, null));
        binding.rowOffers.setOnClickListener(v -> navigate(R.id.offersFragment, null));
        binding.rowContact.setOnClickListener(v -> ShellRoutes.contact(nav(), 0));
        binding.rowTickets.setOnClickListener(v -> navigate(R.id.ticketsFragment, null));
    }
}
