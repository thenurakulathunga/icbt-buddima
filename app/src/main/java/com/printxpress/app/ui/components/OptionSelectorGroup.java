package com.printxpress.app.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.printxpress.app.R;
import com.printxpress.app.data.model.ProductOption;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * A titled row of choice chips for one option type (Size, Paper Type…), used on the configure
 * screen (single choice) and the filter sheet (multiple choice).
 *
 * @since 1.0
 */
public class OptionSelectorGroup extends LinearLayout {

    /** The type title. */
    private final TextView title;
    /** The chips. */
    private final ChipGroup chips;
    /** Selection callback, may be null. */
    private Runnable onSelectionChanged;
    /** The option type name. */
    private String typeName;

    /**
     * Creates the group.
     *
     * @param context the context
     * @param attrs   XML attributes, may be null when created in code
     */
    public OptionSelectorGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        title = new TextView(context);
        title.setTextAppearance(R.style.TextAppearance_PrintXpress_TitleSmall);
        addView(title);
        chips = new ChipGroup(context);
        chips.setSingleSelection(true);
        chips.setSelectionRequired(true);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        params.topMargin = getResources().getDimensionPixelSize(R.dimen.space_xs);
        addView(chips, params);
        chips.setOnCheckedStateChangeListener((group, ids) -> {
            if (onSelectionChanged != null) {
                onSelectionChanged.run();
            }
        });
    }

    /**
     * Creates the group in code.
     *
     * @param context the context
     */
    public OptionSelectorGroup(Context context) {
        this(context, null);
    }

    /**
     * Shows the options of one type; the product's default (or first) option is pre-selected
     * in single-choice mode.
     *
     * @param optionTypeName the type name used as title
     * @param options        the options of that type
     * @param showModifiers  whether to append the price effect, e.g. "+40%"
     */
    public void bind(String optionTypeName, List<ProductOption> options, boolean showModifiers) {
        typeName = optionTypeName;
        title.setText(optionTypeName);
        chips.removeAllViews();
        Chip defaultChip = null;
        for (ProductOption option : options) {
            Chip chip = new Chip(getContext(), null, com.google.android.material.R.attr.chipStyle);
            chip.setId(View.generateViewId());
            chip.setText(showModifiers ? labelFor(option) : option.getValue());
            chip.setCheckable(true);
            chip.setTag(option);
            chips.addView(chip);
            if (defaultChip == null || option.isDefaultOption()) {
                defaultChip = chip;
            }
        }
        if (chips.isSingleSelection() && defaultChip != null) {
            chips.check(defaultChip.getId());
        }
    }

    /**
     * Builds a chip label with its price effect.
     *
     * @param option the option
     * @return e.g. "Double sided +40%"
     */
    private String labelFor(ProductOption option) {
        int percent = (int) Math.round(option.getPriceModifier());
        if (percent == 0) {
            return option.getValue();
        }
        String effect = getContext().getString(percent > 0 ? R.string.option_modifier_up : R.string.option_modifier_down,
                Math.abs(percent));
        return option.getValue() + " " + effect;
    }

    /**
     * Switches between single choice (configure) and multiple choice (filter).
     *
     * @param single true for single choice
     */
    public void setSingleSelection(boolean single) {
        chips.setSingleSelection(single);
        chips.setSelectionRequired(single);
    }

    /** @return the selected option in single-choice mode, or null */
    public ProductOption getSelectedOption() {
        int id = chips.getCheckedChipId();
        View chip = id == View.NO_ID ? null : chips.findViewById(id);
        return chip == null ? null : (ProductOption) chip.getTag();
    }

    /** @return the selected option values (multi-choice mode) */
    public Set<String> getSelectedValues() {
        Set<String> values = new LinkedHashSet<>();
        for (Integer id : chips.getCheckedChipIds()) {
            View chip = chips.findViewById(id);
            if (chip != null) {
                values.add(((ProductOption) chip.getTag()).getValue());
            }
        }
        return values;
    }

    /**
     * Checks chips by option id (restoring a configuration).
     *
     * @param optionIds the ids to check
     */
    public void selectOptionIds(Set<Long> optionIds) {
        for (Chip chip : allChips()) {
            if (optionIds.contains(((ProductOption) chip.getTag()).getOptionId())) {
                chips.check(chip.getId());
            }
        }
    }

    /**
     * Checks chips by value (restoring a filter).
     *
     * @param values the values to check
     */
    public void selectValues(Set<String> values) {
        for (Chip chip : allChips()) {
            chip.setChecked(values.contains(((ProductOption) chip.getTag()).getValue()));
        }
    }

    /** Clears every selection (filter reset). */
    public void clearSelection() {
        chips.clearCheck();
    }

    /** @return the option type name */
    public String getTypeName() {
        return typeName;
    }

    /**
     * Registers a selection callback.
     *
     * @param listener run on every change
     */
    public void setOnSelectionChanged(Runnable listener) {
        this.onSelectionChanged = listener;
    }

    /** @return every chip in the group */
    private List<Chip> allChips() {
        List<Chip> list = new ArrayList<>();
        for (int i = 0; i < chips.getChildCount(); i++) {
            list.add((Chip) chips.getChildAt(i));
        }
        return list;
    }
}
