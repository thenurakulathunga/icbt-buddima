package com.printxpress.app.ui.catalog;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseBottomSheet;
import com.printxpress.app.data.model.ProductFilter;
import com.printxpress.app.data.model.ProductOption;
import com.printxpress.app.databinding.SheetFilterBinding;
import com.printxpress.app.ui.components.OptionSelectorGroup;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Filter and sort sheet (S09, FR-2.3): one multi-choice {@link OptionSelectorGroup} per option type
 * available in the category, plus sort order. Shares {@link ProductListViewModel} with the list.
 *
 * @since 1.0
 */
public class FilterSheet extends BaseBottomSheet<SheetFilterBinding> {

    /** The list's state. */
    private ProductListViewModel viewModel;
    /** The rendered option groups. */
    private final List<OptionSelectorGroup> groups = new ArrayList<>();

    /** Creates the screen; the framework instantiates it with no arguments. */
    public FilterSheet() {
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
    protected SheetFilterBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return SheetFilterBinding.inflate(inflater, container, false);
    }

    /**
     * Connects to the parent ViewModel and selects the current sort.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = parentViewModel(ProductListViewModel.class);
        binding.groupSort.check(chipFor(viewModel.current().getSort()));
    }

    /** Builds the option groups once the values are known. */
    @Override
    protected void initObservers() {
        viewModel.getFilterValues().observe(getViewLifecycleOwner(), r -> {
            if (r.isSuccess()) {
                buildGroups(r.getData());
            }
        });
    }

    /** Wires Reset and Show results. */
    @Override
    protected void initListeners() {
        binding.btnReset.setOnClickListener(v -> {
            for (OptionSelectorGroup group : groups) {
                group.clearSelection();
            }
            binding.groupSort.check(R.id.chipPopular);
        });
        binding.btnApply.setOnClickListener(v -> {
            Map<String, Set<String>> selected = new LinkedHashMap<>();
            for (OptionSelectorGroup group : groups) {
                selected.put(group.getTypeName(), group.getSelectedValues());
            }
            viewModel.setFilters(selected, sortFor(binding.groupSort.getCheckedChipId()));
            dismiss();
        });
    }

    /**
     * Creates one group per option type.
     *
     * @param values distinct values carrying their type name, in type order
     */
    private void buildGroups(List<ProductOption> values) {
        binding.groups.removeAllViews();
        groups.clear();
        Map<String, List<ProductOption>> byType = new LinkedHashMap<>();
        for (ProductOption value : values) {
            byType.computeIfAbsent(value.getOptionTypeName(), k -> new ArrayList<>()).add(value);
        }
        Map<String, Set<String>> current = viewModel.current().getOptionValues();
        for (Map.Entry<String, List<ProductOption>> entry : byType.entrySet()) {
            OptionSelectorGroup group = new OptionSelectorGroup(requireContext());
            group.setSingleSelection(false);
            group.bind(entry.getKey(), entry.getValue(), false);
            Set<String> chosen = current.get(entry.getKey());
            if (chosen != null) {
                group.selectValues(chosen);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.topMargin = getResources().getDimensionPixelSize(R.dimen.space_md);
            binding.groups.addView(group, params);
            groups.add(group);
        }
    }

    /**
     * Maps a sort order to its chip.
     *
     * @param sort the sort order
     * @return the chip id
     */
    private static int chipFor(ProductFilter.Sort sort) {
        switch (sort) {
            case PRICE_LOW_HIGH:
                return R.id.chipPriceLow;
            case PRICE_HIGH_LOW:
                return R.id.chipPriceHigh;
            case NAME:
                return R.id.chipName;
            default:
                return R.id.chipPopular;
        }
    }

    /**
     * Maps a chip to its sort order.
     *
     * @param chipId the checked chip
     * @return the sort order
     */
    private static ProductFilter.Sort sortFor(int chipId) {
        if (chipId == R.id.chipPriceLow) {
            return ProductFilter.Sort.PRICE_LOW_HIGH;
        }
        if (chipId == R.id.chipPriceHigh) {
            return ProductFilter.Sort.PRICE_HIGH_LOW;
        }
        if (chipId == R.id.chipName) {
            return ProductFilter.Sort.NAME;
        }
        return ProductFilter.Sort.POPULAR;
    }
}
