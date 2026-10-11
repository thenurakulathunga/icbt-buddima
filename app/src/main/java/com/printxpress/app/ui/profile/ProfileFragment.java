package com.printxpress.app.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.data.model.User;
import com.printxpress.app.databinding.FragmentProfileBinding;
import com.printxpress.app.ui.auth.AuthActivity;
import com.printxpress.app.ui.components.AppDialog;
import com.printxpress.app.ui.main.MainViewModel;
import com.printxpress.app.ui.main.TopBarBinder;

/**
 * Profile tab (S30, FR-1.4): header, account shortcuts and log out; guests see a sign-in button.
 *
 * @since 1.0
 */
public class ProfileFragment extends BaseFragment<FragmentProfileBinding> {

    /** Screen state. */
    private ProfileViewModel viewModel;

    /** Creates the screen; the framework instantiates it with no arguments. */
    public ProfileFragment() {
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
    protected FragmentProfileBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentProfileBinding.inflate(inflater, container, false);
    }

    /**
     * Connects the top bar.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(ProfileViewModel.class);
        TopBarBinder.bind(binding.topBar, activityViewModel(MainViewModel.class), getViewLifecycleOwner(), nav());
    }

    /** Renders the signed-in or guest state. */
    @Override
    protected void initObservers() {
        observe(viewModel.getUser(), r -> {
            if (r.isLoading()) {
                return;
            }
            User user = r.getData();
            boolean signedIn = user != null;
            if (signedIn) {
                binding.header.bind(user);
            } else {
                binding.header.bindGuest(R.string.guest_profile_title, R.string.guest_profile_subtitle);
            }
            binding.btnSignIn.setVisibility(signedIn ? View.GONE : View.VISIBLE);
            binding.cardMenu.setVisibility(signedIn ? View.VISIBLE : View.GONE);
            binding.rowLogout.setVisibility(signedIn ? View.VISIBLE : View.GONE);
        });
    }

    /** Wires the menu rows. */
    @Override
    protected void initListeners() {
        binding.btnSignIn.setOnClickListener(v -> startActivity(AuthActivity.returning(requireContext())));
        binding.header.setOnEdit(() -> navigate(R.id.editProfileFragment, null));
        binding.rowEditProfile.setOnClickListener(v -> navigate(R.id.editProfileFragment, null));
        binding.rowAddresses.setOnClickListener(v -> navigate(R.id.addressesFragment, null));
        binding.rowPassword.setOnClickListener(v -> navigate(R.id.changePasswordFragment, null));
        binding.rowDesigns.setOnClickListener(v -> navigate(R.id.savedDesignsFragment, null));
        binding.rowHistory.setOnClickListener(v -> navigate(R.id.ordersFragment, null));
        binding.rowSettings.setOnClickListener(v -> navigate(R.id.settingsFragment, null));
        binding.rowLogout.setOnClickListener(v -> new AppDialog.Builder(requireContext())
                .icon(R.drawable.ic_logout).title(R.string.logout_title).message(R.string.logout_message)
                .positive(R.string.menu_logout, viewModel::logout).negative(R.string.action_cancel)
                .destructive(true).show());
    }

    /** Reloads when returning from edit screens or sign-in. */
    @Override
    public void onResume() {
        super.onResume();
        viewModel.refresh();
    }
}
