package com.printxpress.app.ui.components;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.os.Parcelable;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.StringRes;
import androidx.core.content.res.ResourcesCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.printxpress.app.R;
import com.printxpress.app.databinding.ViewPxTextInputBinding;

import java.util.function.Consumer;

/**
 * The app's text field: label above an outlined input with error, helper and counter support.
 * <p>
 * Used on every form (login, register, profile, address, ticket, promo, custom text). The
 * ViewModel validates and the fragment calls {@link #setError(int)}; the component only renders.
 * It saves its own text so several instances on one screen never clash on child view ids.
 * </p>
 *
 * @since 1.0
 */
public class PxTextInput extends LinearLayout {

    /** Bundle key for the parent state. */
    private static final String KEY_SUPER = "super";
    /** Bundle key for the text. */
    private static final String KEY_TEXT = "text";

    /** The inflated children. */
    private final ViewPxTextInputBinding binding;
    /** Text change callback, may be null. */
    private Consumer<String> onTextChanged;
    /** Focus-lost callback, may be null. */
    private Runnable onFocusLost;
    /** Suppresses callbacks while text is set programmatically. */
    private boolean silent;

    /**
     * Creates the field.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public PxTextInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        binding = ViewPxTextInputBinding.inflate(LayoutInflater.from(context), this);
        binding.pxEdit.setSaveEnabled(false);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.PxTextInput);
        try {
            setLabel(a.getString(R.styleable.PxTextInput_px_label));
            binding.pxEdit.setHint(a.getString(R.styleable.PxTextInput_px_hint));
            String helper = a.getString(R.styleable.PxTextInput_px_helper);
            if (helper != null) {
                binding.pxLayout.setHelperText(helper);
            }
            int icon = a.getResourceId(R.styleable.PxTextInput_px_startIcon, 0);
            if (icon != 0) {
                binding.pxLayout.setStartIconDrawable(icon);
            }
            applyInputType(a.getInt(R.styleable.PxTextInput_px_inputType, 0));
            int max = a.getInt(R.styleable.PxTextInput_px_maxLength, 0);
            if (max > 0) {
                binding.pxEdit.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});
                binding.pxLayout.setCounterEnabled(true);
                binding.pxLayout.setCounterMaxLength(max);
            }
        } finally {
            a.recycle();
        }
        wireCallbacks();
    }

    /**
     * Configures the keyboard and presentation for an input type.
     *
     * @param type the px_inputType enum value
     */
    private void applyInputType(int type) {
        switch (type) {
            case 1:
                binding.pxEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                break;
            case 2:
                binding.pxEdit.setInputType(InputType.TYPE_CLASS_PHONE);
                break;
            case 3:
                binding.pxEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.pxLayout.setEndIconMode(TextInputLayout.END_ICON_PASSWORD_TOGGLE);
                break;
            case 4:
            case 7:
                binding.pxEdit.setInputType(InputType.TYPE_CLASS_NUMBER);
                break;
            case 5:
                binding.pxEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE
                        | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                binding.pxEdit.setMinLines(4);
                binding.pxEdit.setGravity(Gravity.TOP | Gravity.START);
                binding.pxEdit.setMinHeight(getResources().getDimensionPixelSize(R.dimen.text_area_min_height));
                break;
            case 6:
                binding.pxEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME
                        | InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                break;
            case 8:
                binding.pxEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                break;
            default:
                binding.pxEdit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
                break;
        }
        binding.pxEdit.setTypeface(ResourcesCompat.getFont(getContext(), R.font.roboto_flex_regular));
    }

    /** Connects the text watcher and focus listener. */
    private void wireCallbacks() {
        binding.pxEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!silent) {
                    clearError();
                    if (onTextChanged != null) {
                        onTextChanged.accept(s.toString());
                    }
                }
            }
        });
        binding.pxEdit.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && onFocusLost != null) {
                onFocusLost.run();
            }
        });
    }

    /** @return the current text, never null */
    public String getText() {
        return binding.pxEdit.getText() == null ? "" : binding.pxEdit.getText().toString();
    }

    /**
     * Sets the text without firing the change callback.
     *
     * @param text the text, may be null
     */
    public void setText(CharSequence text) {
        silent = true;
        binding.pxEdit.setText(text);
        if (text != null) {
            binding.pxEdit.setSelection(binding.pxEdit.length());
        }
        silent = false;
    }

    /**
     * Returns the password characters; callers should clear the array after use.
     *
     * @return the characters
     */
    public char[] getChars() {
        Editable text = binding.pxEdit.getText();
        char[] chars = new char[text == null ? 0 : text.length()];
        if (text != null) {
            text.getChars(0, text.length(), chars, 0);
        }
        return chars;
    }

    /**
     * Shows an error below the field.
     *
     * @param messageRes the message, or 0 to clear
     */
    public void setError(@StringRes int messageRes) {
        setError(messageRes == 0 ? null : getContext().getString(messageRes));
    }

    /**
     * Shows a prepared error below the field.
     *
     * @param message the message, or null to clear
     */
    public void setError(CharSequence message) {
        binding.pxLayout.setError(message);
        binding.pxLayout.setErrorEnabled(message != null);
    }

    /** Clears the error. */
    public void clearError() {
        setError((CharSequence) null);
    }

    /** @return true when an error is showing */
    public boolean hasError() {
        return binding.pxLayout.getError() != null;
    }

    /**
     * Sets the label above the field.
     *
     * @param label the label, null hides it
     */
    public void setLabel(CharSequence label) {
        binding.pxLabel.setText(label);
        binding.pxLabel.setVisibility(label == null ? GONE : VISIBLE);
        binding.pxEdit.setContentDescription(label);
    }

    /**
     * Sets helper text below the field.
     *
     * @param helper the helper text, null hides it
     */
    public void setHelper(CharSequence helper) {
        binding.pxLayout.setHelperText(helper);
    }

    /**
     * Registers a text change callback.
     *
     * @param listener receives the new text
     */
    public void setOnTextChanged(Consumer<String> listener) {
        this.onTextChanged = listener;
    }

    /**
     * Registers a focus-lost callback, used for inline validation.
     *
     * @param listener run when the field loses focus
     */
    public void setOnFocusLost(Runnable listener) {
        this.onFocusLost = listener;
    }

    /** Focuses the field and shows the keyboard. */
    public void focus() {
        binding.pxEdit.requestFocus();
    }

    /**
     * Enables or disables input.
     *
     * @param enabled true to allow typing
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        binding.pxLayout.setEnabled(enabled);
        binding.pxEdit.setEnabled(enabled);
    }

    /** @return the underlying edit text, for IME actions */
    public android.widget.EditText getEditText() {
        return binding.pxEdit;
    }

    /**
     * Saves the text in this view's own state.
     *
     * @return the saved state
     */
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle state = new Bundle();
        state.putParcelable(KEY_SUPER, super.onSaveInstanceState());
        state.putString(KEY_TEXT, getText());
        return state;
    }

    /**
     * Restores the text.
     *
     * @param state the saved state
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (state instanceof Bundle) {
            Bundle bundle = (Bundle) state;
            super.onRestoreInstanceState(bundle.getParcelable(KEY_SUPER));
            setText(bundle.getString(KEY_TEXT));
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    /**
     * Saves only this view, not its children.
     *
     * @param container the state container
     */
    @Override
    protected void dispatchSaveInstanceState(SparseArray<Parcelable> container) {
        dispatchFreezeSelfOnly(container);
    }

    /**
     * Restores only this view, not its children.
     *
     * @param container the state container
     */
    @Override
    protected void dispatchRestoreInstanceState(SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }
}
