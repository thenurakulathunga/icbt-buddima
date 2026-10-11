package com.printxpress.app.ui.gallery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.viewpager2.widget.ViewPager2;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.databinding.FragmentGalleryViewerBinding;
import com.printxpress.app.ui.adapters.GalleryPageAdapter;

/**
 * Full-screen gallery viewer (S33, FR-5.4): swipe between samples of the chosen category and
 * pinch or double-tap to zoom.
 *
 * @since 1.0
 */
public class GalleryViewerFragment extends BaseFragment<FragmentGalleryViewerBinding> {

    /** Screen state. */
    private GalleryViewModel viewModel;
    /** The pages. */
    private final GalleryPageAdapter adapter = new GalleryPageAdapter();
    /** Whether the start position was applied. */
    private boolean positioned;

    /** Creates the screen; the framework instantiates it with no arguments. */
    public GalleryViewerFragment() {
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
    protected FragmentGalleryViewerBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentGalleryViewerBinding.inflate(inflater, container, false);
    }

    /**
     * Sets up the pager.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(GalleryViewModel.class);
        positioned = saved != null;
        binding.topBar.setOnBackClick(() -> nav().navigateUp());
        binding.pager.setAdapter(adapter);
        viewModel.selectCategory(requireArguments().getLong(Constants.ARG_CATEGORY_ID));
    }

    /** Fills the pager and jumps to the tapped sample. */
    @Override
    protected void initObservers() {
        observe(viewModel.getItems(), r -> {
            if (r.isLoading()) {
                binding.stateLayout.showLoading();
            } else if (r.isError()) {
                binding.stateLayout.showError(r.getMessageRes(), viewModel::refresh);
            } else {
                adapter.submitList(r.getData(), () -> {
                    if (!positioned) {
                        positioned = true;
                        binding.pager.setCurrentItem(requireArguments().getInt(Constants.ARG_POSITION), false);
                    }
                    updateCounter(binding.pager.getCurrentItem());
                });
                binding.stateLayout.showContent();
            }
        });
    }

    /** Keeps the counter in the title in step with the page. */
    @Override
    protected void initListeners() {
        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateCounter(position);
            }
        });
    }

    /**
     * Shows "3 / 12" in the top bar.
     *
     * @param position the current page
     */
    private void updateCounter(int position) {
        if (binding != null && adapter.getItemCount() > 0) {
            binding.topBar.setTitle(getString(R.string.gallery_counter, position + 1, adapter.getItemCount()));
        }
    }
}
