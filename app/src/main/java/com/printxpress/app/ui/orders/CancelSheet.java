package com.printxpress.app.ui.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.annotation.Nullable;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseBottomSheet;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.data.model.Order;
import com.printxpress.app.databinding.SheetCancelBinding;
import com.printxpress.app.ui.components.AppDialog;

/**
 * Cancel sheet (S20, FR-3.4): a reason is required (TC-47), an optional note is limited to 200
 * characters, and a destructive confirmation dialog protects the action (docs/05 §9 rule 4).
 *
 * @since 1.0
 */
public class CancelSheet extends BaseBottomSheet<SheetCancelBinding> {

    /** The parent order detail state. */
    private OrderDetailViewModel viewModel;

    /** Creates the screen; the framework instantiates it with no arguments. */
    public CancelSheet() {
        super();
    }

    /**
     * Inflates the sheet.
     *
     * @param inflater  layout inflater
     * @param container parent
     * @return the binding
     */
    @Override
    protected SheetCancelBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return SheetCancelBinding.inflate(inflater, container, false);
    }

    /**
     * Connects to the parent ViewModel.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = parentViewModel(OrderDetailViewModel.class);
    }

    /** Closes after success and mirrors loading. */
    @Override
    protected void initObservers() {
        viewModel.getLoading().observe(getViewLifecycleOwner(), binding.btnConfirm::setLoading);
        viewModel.getDone().observe(getViewLifecycleOwner(), e -> {
            if (Integer.valueOf(R.string.order_cancelled).equals(e.peekContent()) && e.getContentIfNotHandled() != null) {
                dismiss();
            }
        });
    }

    /** Validates the reason and confirms. */
    @Override
    protected void initListeners() {
        binding.groupReasons.setOnCheckedChangeListener((g, id) -> binding.txtReasonError.setVisibility(View.GONE));
        binding.btnConfirm.setOnClickListener(v -> {
            int checked = binding.groupReasons.getCheckedRadioButtonId();
            String note = binding.inputNote.getText().trim();
            boolean reasonOk = checked != View.NO_ID;
            boolean noteOk = note.length() <= Constants.MAX_CANCEL_NOTE;
            binding.txtReasonError.setVisibility(reasonOk ? View.GONE : View.VISIBLE);
            binding.inputNote.setError(noteOk ? 0 : R.string.err_note_too_long);
            if (!reasonOk || !noteOk) {
                return;
            }
            String reason = ((RadioButton) binding.getRoot().findViewById(checked)).getText().toString();
            String full = note.isEmpty() ? reason : getString(R.string.cancel_reason_with_note, reason, note);
            Order order = viewModel.current();
            new AppDialog.Builder(requireContext()).icon(R.drawable.ic_warning)
                    .title(getString(R.string.cancel_confirm_title, order == null ? "" : order.getOrderNumber()))
                    .message(R.string.cancel_confirm_message)
                    .positive(R.string.action_confirm_cancel, () -> viewModel.cancel(full))
                    .negative(R.string.action_keep_order).destructive(true).show();
        });
    }
}
