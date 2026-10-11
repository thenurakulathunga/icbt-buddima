package com.printxpress.app.ui.orders;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseBottomSheet;
import com.printxpress.app.data.model.Order;
import com.printxpress.app.databinding.SheetRescheduleBinding;

/**
 * Reschedule sheet (S19, FR-3.3): pick a new allowed date and slot for an order that has not
 * started printing (BR-6, BR-7; TC-45).
 *
 * @since 1.0
 */
public class RescheduleSheet extends BaseBottomSheet<SheetRescheduleBinding> {

    /** The parent order detail state. */
    private OrderDetailViewModel viewModel;

    /** Creates the screen; the framework instantiates it with no arguments. */
    public RescheduleSheet() {
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
    protected SheetRescheduleBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return SheetRescheduleBinding.inflate(inflater, container, false);
    }

    /**
     * Fills the picker with allowed days and the current schedule.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = parentViewModel(OrderDetailViewModel.class);
        binding.dateSlotPicker.setDays(viewModel.rescheduleDays(), viewModel.availability());
        Order order = viewModel.current();
        if (order != null) {
            binding.dateSlotPicker.setSelection(order.getScheduledDate(), order.getScheduledSlot());
        }
    }

    /** Closes the sheet after success and mirrors loading. */
    @Override
    protected void initObservers() {
        viewModel.getLoading().observe(getViewLifecycleOwner(), binding.btnSave::setLoading);
        viewModel.getDone().observe(getViewLifecycleOwner(), e -> {
            if (Integer.valueOf(R.string.rescheduled).equals(e.peekContent()) && e.getContentIfNotHandled() != null) {
                dismiss();
            }
        });
    }

    /** Validates the choice and saves. */
    @Override
    protected void initListeners() {
        binding.btnSave.setOnClickListener(v -> {
            boolean dateOk = binding.dateSlotPicker.getSelectedDate() != null
                    && viewModel.availability().test(binding.dateSlotPicker.getSelectedDate());
            boolean slotOk = binding.dateSlotPicker.getSelectedSlot() != null;
            binding.dateSlotPicker.setDateError(dateOk ? 0 : R.string.err_date_invalid);
            binding.dateSlotPicker.setSlotError(slotOk ? 0 : R.string.err_slot_required);
            if (dateOk && slotOk) {
                viewModel.reschedule(binding.dateSlotPicker.getSelectedDate(), binding.dateSlotPicker.getSelectedSlot());
            }
        });
    }
}
