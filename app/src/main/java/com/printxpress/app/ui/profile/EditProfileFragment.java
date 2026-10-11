package com.printxpress.app.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.core.util.KeyboardUtils;
import com.printxpress.app.data.model.User;
import com.printxpress.app.databinding.FragmentEditProfileBinding;
import com.printxpress.app.ui.components.FormBinder;
import com.printxpress.app.ui.components.SnackbarHelper;

import java.io.File;

/**
 * Edit profile (S31, FR-1.4). The photo comes from the system photo picker, which needs no
 * storage permission (NFR-6).
 *
 * @since 1.0
 */
public class EditProfileFragment extends BaseFragment<FragmentEditProfileBinding> {

    /** Form state. */
    private EditProfileViewModel viewModel;
    /** Field binding. */
    private final FormBinder form = new FormBinder();
    /** Photo picker. */
    private final ActivityResultLauncher<PickVisualMediaRequest> photoPicker =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) {
                    viewModel.updatePhoto(uri);
                }
            });

    /** Creates the screen; the framework instantiates it with no arguments. */
    public EditProfileFragment() {
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
    protected FragmentEditProfileBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentEditProfileBinding.inflate(inflater, container, false);
    }

    /**
     * Binds fields and loads the user.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(EditProfileViewModel.class);
        binding.topBar.setOnBackClick(() -> nav().navigateUp());
        form.bind(EditProfileViewModel.FIELD_NAME, binding.inputName, viewModel::setValue, viewModel::validateField)
                .bind(EditProfileViewModel.FIELD_EMAIL, binding.inputEmail, viewModel::setValue, viewModel::validateField)
                .bind(EditProfileViewModel.FIELD_PHONE, binding.inputPhone, viewModel::setValue, viewModel::validateField);
        viewModel.start();
    }

    /** Renders the user, errors and results. */
    @Override
    protected void initObservers() {
        observe(viewModel.getUser(), r -> {
            if (r.isSuccess()) {
                showUser(r.getData());
            }
        });
        observe(viewModel.getFieldErrors(), e -> form.showErrors(e.getErrors(), e.isFocusFirst()));
        observe(viewModel.getLoading(), binding.bottomBar::setLoading);
        observeMessages(viewModel);
        observe(viewModel.getSaved(), event -> {
            if (event.getContentIfNotHandled() != null) {
                SnackbarHelper.success(requireActivity().findViewById(android.R.id.content), R.string.profile_saved);
                nav().navigateUp();
            }
        });
    }

    /** Wires photo and save. */
    @Override
    protected void initListeners() {
        binding.btnPhoto.setOnClickListener(v -> photoPicker.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE).build()));
        binding.bottomBar.setOnButtonClick(() -> {
            KeyboardUtils.hide(binding.getRoot());
            viewModel.save();
        });
    }

    /**
     * Fills the fields from the stored values and shows the avatar.
     *
     * @param user the user
     */
    private void showUser(User user) {
        binding.inputName.setText(viewModel.value(EditProfileViewModel.FIELD_NAME));
        binding.inputEmail.setText(viewModel.value(EditProfileViewModel.FIELD_EMAIL));
        binding.inputPhone.setText(viewModel.value(EditProfileViewModel.FIELD_PHONE));
        String path = user.getProfileImagePath();
        if (path != null && new File(path).exists()) {
            Glide.with(this).load(new File(path)).circleCrop().into(binding.imgAvatar);
        }
    }
}
