package com.printxpress.app.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.StringRes;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.chip.Chip;
import com.printxpress.app.R;
import com.printxpress.app.core.util.DateTimeUtils;
import com.printxpress.app.databinding.ViewDateCellBinding;
import com.printxpress.app.databinding.ViewDateSlotPickerBinding;
import com.printxpress.app.domain.schedule.TimeSlot;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Date strip and time-slot chips for checkout and rescheduling (FR-2.10, FR-3.3).
 * <p>
 * The screen supplies the days and an availability rule from {@code ScheduleCalculator}; closed
 * days (Sundays, holidays) are shown but disabled so the customer understands why (TC-37).
 * </p>
 *
 * @since 1.0
 */
public class DateSlotPicker extends LinearLayout {

    /**
     * Receives selection changes.
     */
    public interface OnSelectionChangedListener {

        /**
         * Called when the date or slot changes.
         *
         * @param date the selected date, may be null
         * @param slot the selected slot label, may be null
         */
        void onSelectionChanged(LocalDate date, String slot);
    }

    /** The inflated children. */
    private final ViewDateSlotPickerBinding binding;
    /** The date cells. */
    private final List<MaterialCardView> cells = new ArrayList<>();
    /** The dates shown. */
    private final List<LocalDate> dates = new ArrayList<>();
    /** The selected date, may be null. */
    private LocalDate selectedDate;
    /** Selection callback, may be null. */
    private OnSelectionChangedListener listener;

    /**
     * Creates the picker.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public DateSlotPicker(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        binding = ViewDateSlotPickerBinding.inflate(LayoutInflater.from(context), this);
        for (TimeSlot slot : TimeSlot.values()) {
            Chip chip = new Chip(context, null, com.google.android.material.R.attr.chipStyle);
            chip.setId(View.generateViewId());
            chip.setText(slot.getLabel());
            chip.setCheckable(true);
            chip.setTag(slot.getLabel());
            binding.slotGroup.addView(chip);
        }
        binding.slotGroup.setOnCheckedStateChangeListener((group, ids) -> {
            binding.txtSlotError.setVisibility(GONE);
            notifyListener();
        });
    }

    /**
     * Shows a set of days.
     *
     * @param days      consecutive days to show
     * @param available decides which days can be picked
     */
    public void setDays(List<LocalDate> days, Predicate<LocalDate> available) {
        binding.dateRow.removeAllViews();
        cells.clear();
        dates.clear();
        for (LocalDate day : days) {
            ViewDateCellBinding cell = ViewDateCellBinding.inflate(LayoutInflater.from(getContext()),
                    binding.dateRow, false);
            boolean open = available.test(day);
            cell.txtDay.setText(day.format(DateTimeUtils.DAY_NAME));
            cell.txtNumber.setText(day.format(DateTimeUtils.DAY_NUMBER));
            cell.txtMonth.setText(open ? day.format(DateTimeUtils.MONTH_NAME) : getContext().getString(R.string.closed_day));
            cell.getRoot().setEnabled(open);
            cell.getRoot().setAlpha(open ? 1f : 0.4f);
            cell.getRoot().setContentDescription(DateTimeUtils.formatDisplay(day)
                    + (open ? "" : ", " + getContext().getString(R.string.closed_day)));
            cell.getRoot().setOnClickListener(v -> selectDate(day, true));
            binding.dateRow.addView(cell.getRoot());
            cells.add(cell.getRoot());
            dates.add(day);
        }
        if (selectedDate != null && dates.contains(selectedDate)) {
            selectDate(selectedDate, false);
        }
    }

    /**
     * Selects a date and slot, e.g. the earliest date or an order's current schedule.
     *
     * @param date the date, may be null
     * @param slot the slot label, may be null
     */
    public void setSelection(LocalDate date, String slot) {
        if (date != null) {
            selectDate(date, false);
        }
        for (int i = 0; i < binding.slotGroup.getChildCount(); i++) {
            Chip chip = (Chip) binding.slotGroup.getChildAt(i);
            chip.setChecked(chip.getTag().equals(slot));
        }
    }

    /**
     * Highlights a date.
     *
     * @param date   the date
     * @param notify whether to notify the listener
     */
    private void selectDate(LocalDate date, boolean notify) {
        selectedDate = date;
        for (int i = 0; i < cells.size(); i++) {
            cells.get(i).setChecked(dates.get(i).equals(date));
        }
        binding.txtDateError.setVisibility(GONE);
        if (notify) {
            notifyListener();
        }
    }

    /** @return the selected date, or null */
    public LocalDate getSelectedDate() {
        return selectedDate;
    }

    /** @return the selected slot label, or null */
    public String getSelectedSlot() {
        int id = binding.slotGroup.getCheckedChipId();
        View chip = id == View.NO_ID ? null : binding.slotGroup.findViewById(id);
        return chip == null ? null : (String) chip.getTag();
    }

    /**
     * Shows a date error.
     *
     * @param messageRes the message, or 0 to clear
     */
    public void setDateError(@StringRes int messageRes) {
        showError(binding.txtDateError, messageRes);
    }

    /**
     * Shows a slot error.
     *
     * @param messageRes the message, or 0 to clear
     */
    public void setSlotError(@StringRes int messageRes) {
        showError(binding.txtSlotError, messageRes);
    }

    /**
     * Registers a selection callback.
     *
     * @param onChanged the callback
     */
    public void setOnSelectionChangedListener(OnSelectionChangedListener onChanged) {
        this.listener = onChanged;
    }

    /**
     * Shows or hides an error line.
     *
     * @param view       the error text view
     * @param messageRes the message, or 0
     */
    private void showError(android.widget.TextView view, int messageRes) {
        view.setText(messageRes == 0 ? null : getContext().getString(messageRes));
        view.setVisibility(messageRes == 0 ? GONE : VISIBLE);
    }

    /** Sends the current selection to the listener. */
    private void notifyListener() {
        if (listener != null) {
            listener.onSelectionChanged(selectedDate, getSelectedSlot());
        }
    }
}
