package com.printxpress.app.ui.components;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.printxpress.app.databinding.ViewQuantityStepperBinding;

import java.util.function.IntConsumer;

/**
 * Minus / typed quantity / plus control that follows a product's min, max and step (BR-3).
 * <p>
 * The buttons move along the step grid and never leave the range. Typed input is passed on as
 * typed; values that cannot be parsed are reported as {@link #INVALID} so the ViewModel can show
 * the error through {@link #setError(CharSequence)}.
 * </p>
 *
 * @since 1.0
 */
public class QuantityStepper extends LinearLayout {

    /** Value reported for unparseable input. */
    public static final int INVALID = -1;

    /** The inflated children. */
    private final ViewQuantityStepperBinding binding;
    /** Minimum quantity. */
    private int min = 1;
    /** Maximum quantity. */
    private int max = Integer.MAX_VALUE;
    /** Step size. */
    private int step = 1;
    /** Change callback, may be null. */
    private IntConsumer listener;
    /** Suppresses callbacks while the value is set programmatically. */
    private boolean silent;

    /**
     * Creates the stepper.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public QuantityStepper(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        binding = ViewQuantityStepperBinding.inflate(LayoutInflater.from(context), this);
        binding.editQty.setSaveEnabled(false);
        binding.btnMinus.setOnClickListener(v -> changeBy(-step));
        binding.btnPlus.setOnClickListener(v -> changeBy(step));
        binding.editQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!silent) {
                    updateButtons();
                    notifyListener();
                }
            }
        });
    }

    /**
     * Sets the allowed grid.
     *
     * @param newMin  minimum
     * @param newMax  maximum
     * @param newStep step size
     */
    public void setRange(int newMin, int newMax, int newStep) {
        min = newMin;
        max = newMax;
        step = Math.max(1, newStep);
        updateButtons();
    }

    /** @return the typed quantity, or {@link #INVALID} */
    public int getValue() {
        try {
            return Integer.parseInt(binding.editQty.getText().toString().trim());
        } catch (NumberFormatException e) {
            return INVALID;
        }
    }

    /** @return the raw typed text, for validation */
    public String getRawText() {
        return binding.editQty.getText().toString();
    }

    /**
     * Sets the quantity without firing the callback.
     *
     * @param value the quantity
     */
    public void setValue(int value) {
        silent = true;
        binding.editQty.setText(String.valueOf(value));
        silent = false;
        updateButtons();
    }

    /**
     * Registers a change callback.
     *
     * @param onChanged receives the new value or {@link #INVALID}
     */
    public void setOnValueChangedListener(IntConsumer onChanged) {
        this.listener = onChanged;
    }

    /**
     * Shows or hides an error below the control.
     *
     * @param message the message, or null to clear
     */
    public void setError(CharSequence message) {
        binding.txtError.setText(message);
        binding.txtError.setVisibility(message == null ? GONE : VISIBLE);
    }

    /**
     * Moves the value along the grid, snapping to it first when off-grid.
     *
     * @param delta plus or minus one step
     */
    private void changeBy(int delta) {
        int current = getValue();
        int next;
        if (current == INVALID || current < min) {
            next = min;
        } else {
            int offset = (current - min) % step;
            int snapped = current - offset;
            next = offset != 0 && delta > 0 ? snapped + step : (offset != 0 ? snapped : current + delta);
        }
        next = Math.max(min, Math.min(max, next));
        silent = true;
        binding.editQty.setText(String.valueOf(next));
        binding.editQty.setSelection(binding.editQty.length());
        silent = false;
        updateButtons();
        notifyListener();
    }

    /** Enables the buttons only when a step in that direction stays in range. */
    private void updateButtons() {
        int value = getValue();
        binding.btnMinus.setEnabled(value == INVALID || value > min);
        binding.btnPlus.setEnabled(value == INVALID || value < max);
    }

    /** Sends the current value to the listener. */
    private void notifyListener() {
        if (listener != null) {
            listener.accept(getValue());
        }
    }
}
