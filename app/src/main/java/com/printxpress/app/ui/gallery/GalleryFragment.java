package com.printxpress.app.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.android.material.chip.Chip;
import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.data.model.Category;
import com.printxpress.app.databinding.FragmentGalleryBinding;
import com.printxpress.app.ui.adapters.GalleryAdapter;
import com.printxpress.app.ui.components.SpacingItemDecoration;
import com.printxpress.app.ui.main.ShellRoutes;

import java.util.List;

/**
 * Design gallery (S32, FR-5.4): sample designs filtered by category; a tap opens the full-screen
 * viewer at that sample.
 *
 * @since 1.0
 */
public class GalleryFragment extends BaseFragment<FragmentGalleryBinding> {

    /** Screen state. */
    private GalleryViewModel viewModel;
    /** The grid. */
    private final GalleryAdapter adapter = new GalleryAdapter(false);

    /** Creates the screen; the framework instantiates it with no arguments. */
    public GalleryFragment() {
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
    protected FragmentGalleryBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentGalleryBinding.inflate(inflater, container, false);
    }

    /**
     * Sets up the grid and loads data.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(GalleryViewModel.class);
        binding.topBar.setOnBackClick(() -> nav().navigateUp());
        binding.listGallery.setLayoutManager(new GridLayoutManager(requireContext(),
                getResources().getInteger(R.integer.gallery_grid_span)));
        binding.listGallery.addItemDecoration(new SpacingItemDecoration(getResources().getDimensionPixelSize(R.dimen.space_md)));
        binding.listGallery.setAdapter(adapter);
        viewModel.loadCategories();
        viewModel.selectCategory(viewModel.getCategoryId());
    }

    /** Renders the chips and the grid. */
    @Override
    protected void initObservers() {
        observe(viewModel.getCategories(), r -> {
            if (r.isSuccess()) {
                buildChips(r.getData());
            }
        });
        observe(viewModel.getItems(), r -> {
            if (r.isLoading()) {
                binding.stateLayout.showLoading();
            } else if (r.isError()) {
                binding.stateLayout.showError(r.getMessageRes(), viewModel::refresh);
            } else if (r.getData().isEmpty()) {
                adapter.submitList(r.getData());
                binding.stateLayout.showEmpty(R.drawable.ic_image, R.string.empty_gallery_title,
                        R.string.empty_gallery_message, 0, null);
            } else {
                adapter.submitList(r.getData());
                binding.stateLayout.showContent();
            }
        });
    }

    /**
     * Builds the All chip plus one chip per category.
     *
     * @param categories the categories
     */
    private void buildChips(List<Category> categories) {
        binding.chipsCategory.removeAllViews();
        addChip(getString(R.string.filter_all), 0);
        for (Category c : categories) {
            addChip(c.getName(), c.getCategoryId());
        }
    }

    /**
     * Adds one filter chip.
     *
     * @param label      the chip text
     * @param categoryId the category it selects
     */
    private void addChip(String label, long categoryId) {
        Chip chip = new Chip(requireContext(), null, com.google.android.material.R.attr.chipStyle);
        chip.setId(View.generateViewId());
        chip.setText(label);
        chip.setCheckable(true);
        chip.setChecked(categoryId == viewModel.getCategoryId());
        chip.setOnCheckedChangeListener((button, checked) -> {
            if (checked) {
                viewModel.selectCategory(categoryId);
            }
        });
        binding.chipsCategory.addView(chip);
    }

    /** Opens the viewer. */
    @Override
    protected void initListeners() {
        adapter.setOnItemClickListener((item, position) ->
                ShellRoutes.galleryViewer(nav(), viewModel.getCategoryId(), position));
    }
}
