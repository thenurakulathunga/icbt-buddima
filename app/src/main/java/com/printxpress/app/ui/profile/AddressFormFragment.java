package com.printxpress.app.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.core.util.CurrencyFormatter;
import com.printxpress.app.core.util.KeyboardUtils;
import com.printxpress.app.data.model.District;
import com.printxpress.app.databinding.FragmentAddressFormBinding;
import com.printxpress.app.ui.components.FormBinder;
import com.printxpress.app.ui.components.SnackbarHelper;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Add or edit address form (S32, FR-1.6).
 *
 * @since 1.0
 */
public class AddressFormFragment extends BaseFragment<FragmentAddressFormBinding> {

    /** Form state. */
    private AddressFormViewModel viewModel;
    /** Text field binding. */
    private final FormBinder form = new FormBinder();

    /** Creates the screen; the framework instantiates it with no arguments. */
    public AddressFormFragment() {
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
    protected FragmentAddressFormBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentAddressFormBinding.inflate(inflater, container, false);
    }

    /**
     * Binds the fields and loads data.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(AddressFormViewModel.class);
        viewModel.start(requireArguments().getLong(Constants.ARG_ADDRESS_ID));
        binding.topBar.setTitle(viewModel.isEditing() ? R.string.address_form_edit : R.string.address_form_add);
        binding.topBar.setOnBackClick(() -> nav().navigateUp());
        List<String> labels = Arrays.asList(getString(R.string.address_label_home),
                getString(R.string.address_label_work), getString(R.string.address_label_other));
        binding.dropLabel.setItems(labels, s -> s);
        form.bind(AddressFormViewModel.FIELD_RECIPIENT, binding.inputRecipient, viewModel::setValue, viewModel::validateField)
                .bind(AddressFormViewModel.FIELD_PHONE, binding.inputPhone, viewModel::setValue, viewModel::validateField)
                .bind(AddressFormViewModel.FIELD_LINE1, binding.inputLine1, viewModel::setValue, viewModel::validateField)
                .bind(AddressFormViewModel.FIELD_LINE2, binding.inputLine2, viewModel::setValue, viewModel::validateField)
                .bind(AddressFormViewModel.FIELD_CITY, binding.inputCity, viewModel::setValue, viewModel::validateField)
                .bind(AddressFormViewModel.FIELD_POSTAL, binding.inputPostal, viewModel::setValue, viewModel::validateField);
        binding.switchDefault.setChecked(viewModel.isMakeDefault());
        restoreFields();
    }

    /** Observes districts, errors and results. */
    @Override
    protected void initObservers() {
        observe(viewModel.getDistricts(), r -> {
            if (r.isSuccess()) {
                binding.dropDistrict.setItems(r.getData(), d -> getString(R.string.district_fee_item, d.getName(),
                        CurrencyFormatter.format(d.getDeliveryFee())));
                selectDistrict();
            }
        });
        observe(viewModel.getPrefilled(), e -> {
            if (e.getContentIfNotHandled() != null) {
                restoreFields();
                binding.switchDefault.setChecked(viewModel.isMakeDefault());
            }
        });
        observe(viewModel.getFieldErrors(), e -> {
            form.showErrors(e.getErrors(), e.isFocusFirst());
            Map<String, Integer> errors = e.getErrors();
            Integer label = errors.get(AddressFormViewModel.FIELD_LABEL);
            Integer district = errors.get(AddressFormViewModel.FIELD_DISTRICT);
            binding.dropLabel.setError(label == null ? 0 : label);
            binding.dropDistrict.setError(district == null ? 0 : district);
        });
        observe(viewModel.getLoading(), binding.bottomBar::setLoading);
        observeMessages(viewModel);
        observe(viewModel.getSaved(), e -> {
            if (e.getContentIfNotHandled() != null) {
                SnackbarHelper.success(requireActivity().findViewById(android.R.id.content), R.string.address_saved);
                nav().navigateUp();
            }
        });
    }

    /** Wires dropdowns, switch and Save. */
    @Override
    protected void initListeners() {
        binding.dropLabel.<String>setOnItemSelected(s -> viewModel.setValue(AddressFormViewModel.FIELD_LABEL, s));
        binding.dropDistrict.<District>setOnItemSelected(d ->
                viewModel.setValue(AddressFormViewModel.FIELD_DISTRICT, String.valueOf(d.getDistrictId())));
        binding.switchDefault.setOnCheckedChangeListener((b, checked) -> viewModel.setMakeDefault(checked));
        binding.bottomBar.setOnButtonClick(() -> {
            KeyboardUtils.hide(binding.getRoot());
            viewModel.save();
        });
    }

    /** Copies stored values into the fields. */
    private void restoreFields() {
        binding.inputRecipient.setText(viewModel.value(AddressFormViewModel.FIELD_RECIPIENT));
        binding.inputPhone.setText(viewModel.value(AddressFormViewModel.FIELD_PHONE));
        binding.inputLine1.setText(viewModel.value(AddressFormViewModel.FIELD_LINE1));
        binding.inputLine2.setText(viewModel.value(AddressFormViewModel.FIELD_LINE2));
        binding.inputCity.setText(viewModel.value(AddressFormViewModel.FIELD_CITY));
        binding.inputPostal.setText(viewModel.value(AddressFormViewModel.FIELD_POSTAL));
        String label = viewModel.value(AddressFormViewModel.FIELD_LABEL);
        if (!label.isEmpty()) {
            binding.dropLabel.<String>selectWhere(s -> s.equalsIgnoreCase(label));
        }
        selectDistrict();
    }

    /** Selects the stored district in the dropdown. */
    private void selectDistrict() {
        String id = viewModel.value(AddressFormViewModel.FIELD_DISTRICT);
        if (!id.isEmpty()) {
            binding.dropDistrict.<District>selectWhere(d -> String.valueOf(d.getDistrictId()).equals(id));
        }
    }
}
