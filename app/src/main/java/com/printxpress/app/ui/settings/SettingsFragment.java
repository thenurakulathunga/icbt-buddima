package com.printxpress.app.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.printxpress.app.BuildConfig;
import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.core.util.IntentUtils;
import com.printxpress.app.data.model.User;
import com.printxpress.app.databinding.FragmentSettingsBinding;
import com.printxpress.app.ui.auth.AuthActivity;
import com.printxpress.app.ui.components.AppDialog;
import com.printxpress.app.ui.components.LoadingOverlay;

/**
 * Settings (S34, FR-4.6, FR-1.10, TC-54): notification preferences, theme, demo speed, privacy
 * notice, open-source licences and delete account.
 *
 * @since 1.0
 */
public class SettingsFragment extends BaseFragment<FragmentSettingsBinding> {

    /** Screen state. */
    private SettingsViewModel viewModel;
    /** Blocking progress while the account is deleted. */
    private LoadingOverlay overlay;

    /** Creates the screen; the framework instantiates it with no arguments. */
    public SettingsFragment() {
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
    protected FragmentSettingsBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentSettingsBinding.inflate(inflater, container, false);
    }

    /**
     * Fills the controls from stored settings.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(SettingsViewModel.class);
        overlay = new LoadingOverlay(requireContext());
        binding.topBar.setOnBackClick(() -> nav().navigateUp());
        boolean signedIn = viewModel.isLoggedIn();
        binding.groupNotifications.setVisibility(signedIn ? View.VISIBLE : View.GONE);
        binding.cardDelete.setVisibility(signedIn ? View.VISIBLE : View.GONE);
        binding.toggleTheme.setOptions(R.string.theme_system, R.string.theme_light, R.string.theme_dark);
        binding.toggleTheme.setSelectedIndex(viewModel.getThemeIndex());
        binding.rowDemoSpeed.setChecked(viewModel.isDemoSpeed());
        binding.rowVersion.setSubtitle(getString(R.string.settings_version, BuildConfig.VERSION_NAME));
        viewModel.refresh();
    }

    /** Shows preferences and reacts to deletion. */
    @Override
    protected void initObservers() {
        observeMessages(viewModel);
        observe(viewModel.getLoading(), overlay::setVisible);
        observe(viewModel.getUser(), r -> {
            if (r.isSuccess() && r.getData() != null) {
                User u = r.getData();
                binding.rowOrderUpdates.setChecked(u.isNotifyOrders());
                binding.rowPromos.setChecked(u.isNotifyPromos());
            }
        });
        observe(viewModel.getDeleted(), e -> {
            if (e.getContentIfNotHandled() != null) {
                Intent intent = new Intent(requireContext(), AuthActivity.class)
                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            }
        });
    }

    /** Wires switches, the toggle and rows. */
    @Override
    protected void initListeners() {
        binding.rowOrderUpdates.setOnCheckedChange(on -> viewModel.savePreferences(on, binding.rowPromos.isChecked()));
        binding.rowPromos.setOnCheckedChange(on -> viewModel.savePreferences(binding.rowOrderUpdates.isChecked(), on));
        binding.rowSystemNotifications.setOnClickListener(v -> {
            if (!IntentUtils.openNotificationSettings(requireContext())) {
                showMessage(R.string.err_no_app);
            }
        });
        binding.toggleTheme.setOnSelectionChanged(viewModel::setThemeIndex);
        binding.rowDemoSpeed.setOnCheckedChange(viewModel::setDemoSpeed);
        binding.rowPrivacy.setOnClickListener(v -> new AppDialog.Builder(requireContext())
                .icon(R.drawable.ic_privacy).title(R.string.settings_privacy).message(R.string.privacy_text)
                .positive(R.string.action_ok, null).show());
        binding.rowLicences.setOnClickListener(v -> new AppDialog.Builder(requireContext())
                .icon(R.drawable.ic_licence).title(R.string.settings_licences).message(R.string.licences_text)
                .positive(R.string.action_ok, null).show());
        binding.rowDeleteAccount.setOnClickListener(v -> new AppDialog.Builder(requireContext())
                .icon(R.drawable.ic_delete_account).title(R.string.delete_account_title)
                .message(R.string.delete_account_message).destructive(true)
                .positive(R.string.settings_delete_account, viewModel::deleteAccount)
                .negative(R.string.action_cancel).show());
    }

    /** Hides the overlay if the screen goes away mid-request. */
    @Override
    public void onDestroyView() {
        overlay.hide();
        super.onDestroyView();
    }
}
