package com.printxpress.app.ui.configure;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.core.util.CurrencyFormatter;
import com.printxpress.app.core.util.KeyboardUtils;
import com.printxpress.app.data.model.Design;
import com.printxpress.app.databinding.FragmentDesignInputBinding;
import com.printxpress.app.ui.adapters.DesignAdapter;
import com.printxpress.app.ui.components.FormBinder;
import com.printxpress.app.ui.components.SnackbarHelper;
import com.printxpress.app.ui.components.SpacingItemDecoration;
import com.printxpress.app.ui.main.AuthGate;

import java.util.List;

/**
 * Design input (S12, FR-2.6, FR-2.7): upload artwork through the system file picker (no storage
 * permission), reuse a saved design, or type custom text; then add to cart. Guests are asked to
 * sign in first and the item is added straight after (TC-42).
 *
 * @since 1.0
 */
public class DesignInputFragment extends BaseFragment<FragmentDesignInputBinding> {

    /** Flow state shared with Configure. */
    private ConfigureViewModel viewModel;
    /** Field binding. */
    private final FormBinder form = new FormBinder();
    /** Saved design picker. */
    private final DesignAdapter savedAdapter = new DesignAdapter();
    /** Sign-in guard. */
    private final AuthGate authGate = new AuthGate(this);
    /** System document picker limited to the accepted types (BR-10). */
    private final ActivityResultLauncher<String[]> filePicker =
            registerForActivityResult(new ActivityResultContracts.OpenDocument(), uri -> {
                if (uri != null) {
                    viewModel.onFilePicked(uri);
                }
            });

    /** Creates the screen; the framework instantiates it with no arguments. */
    public DesignInputFragment() {
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
    protected FragmentDesignInputBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentDesignInputBinding.inflate(inflater, container, false);
    }

    /**
     * Sets up the toggle, picker list and fields.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = graphViewModel(R.id.nav_configure, ConfigureViewModel.class);
        binding.topBar.setOnBackClick(() -> nav().navigateUp());
        binding.toggleMode.setOptions(R.string.design_mode_upload, R.string.design_mode_text);
        binding.listSaved.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        binding.listSaved.addItemDecoration(new SpacingItemDecoration(getResources().getDimensionPixelSize(R.dimen.space_md)));
        binding.listSaved.setAdapter(savedAdapter);
        form.bind(ConfigureViewModel.FIELD_TEXT, binding.inputText, viewModel::setValue, viewModel::validateField)
                .bind(ConfigureViewModel.FIELD_INSTRUCTIONS, binding.inputInstructions, viewModel::setValue,
                        viewModel::validateField);
        binding.inputText.setText(viewModel.value(ConfigureViewModel.FIELD_TEXT));
        binding.inputInstructions.setText(viewModel.value(ConfigureViewModel.FIELD_INSTRUCTIONS));
        binding.bottomBar.setButtonText(viewModel.isEditing() ? R.string.action_update_cart : R.string.action_add_to_cart);
    }

    /** Renders mode, artwork, errors, price and the result. */
    @Override
    protected void initObservers() {
        observe(viewModel.getDesignMode(), mode -> {
            boolean upload = mode == ConfigureViewModel.MODE_UPLOAD;
            binding.toggleMode.setSelectedIndex(mode);
            binding.sectionUpload.setVisibility(upload ? View.VISIBLE : View.GONE);
            binding.sectionText.setVisibility(upload ? View.GONE : View.VISIBLE);
        });
        observe(viewModel.getPickedFile(), file -> {
            if (file != null) {
                binding.fileUpload.setFile(file.getDisplayName(), file.getMimeType(), file.getSizeBytes(), null);
            } else if (viewModel.getSavedDesign().getValue() == null) {
                binding.fileUpload.clear();
            }
        });
        observe(viewModel.getSavedDesign(), this::showSavedDesign);
        observe(viewModel.getLibrary(), r -> {
            List<Design> list = r.getData();
            boolean has = r.isSuccess() && list != null && !list.isEmpty();
            binding.headerSaved.setVisibility(has ? View.VISIBLE : View.GONE);
            binding.listSaved.setVisibility(has ? View.VISIBLE : View.GONE);
            if (has) {
                savedAdapter.submitList(list);
            }
        });
        observe(viewModel.getFileLoading(), busy -> binding.progressFile.setVisibility(busy ? View.VISIBLE : View.GONE));
        observe(viewModel.getDesignError(), res -> {
            binding.txtDesignError.setText(res == 0 ? null : getString(res));
            binding.txtDesignError.setVisibility(res == 0 ? View.GONE : View.VISIBLE);
        });
        observe(viewModel.getFieldErrors(), e -> form.showErrors(e.getErrors(), e.isFocusFirst()));
        observe(viewModel.getPrice(), p -> binding.bottomBar.setPrice(CurrencyFormatter.format(p.getTotal())));
        observe(viewModel.getLoading(), binding.bottomBar::setLoading);
        observeMessages(viewModel);
        observe(viewModel.getAdded(), e -> {
            if (e.getContentIfNotHandled() != null) {
                SnackbarHelper.success(requireActivity().findViewById(android.R.id.content), R.string.added_to_cart);
                navigate(R.id.action_design_to_cart, null);
            }
        });
    }

    /** Wires the toggle, picker and submit. */
    @Override
    protected void initListeners() {
        binding.toggleMode.setOnSelectionChanged(viewModel::setDesignMode);
        binding.fileUpload.setOnPickClickListener(() -> filePicker.launch(Constants.ACCEPTED_MIME_TYPES.toArray(new String[0])));
        binding.fileUpload.setOnRemoveClickListener(viewModel::clearArtwork);
        savedAdapter.setOnItemClickListener((design, p) -> viewModel.selectSavedDesign(design));
        binding.bottomBar.setOnButtonClick(() -> {
            KeyboardUtils.hide(binding.getRoot());
            authGate.run(viewModel::addToCart);
        });
    }

    /**
     * Shows a chosen saved design in the preview.
     *
     * @param design the design, or null
     */
    private void showSavedDesign(Design design) {
        savedAdapter.setSelectedId(design == null ? -1 : design.getDesignId());
        if (design != null) {
            binding.fileUpload.setFile(design.getTitle(), design.getMimeType(), design.getFileSizeBytes(),
                    design.getFilePath());
        } else if (viewModel.getPickedFile().getValue() == null) {
            binding.fileUpload.clear();
        }
    }
}
