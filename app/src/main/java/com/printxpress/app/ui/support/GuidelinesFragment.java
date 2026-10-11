package com.printxpress.app.ui.support;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.databinding.FragmentGuidelinesBinding;
import com.printxpress.app.ui.adapters.GuidelineAdapter;
import com.printxpress.app.ui.components.SpacingItemDecoration;
import com.printxpress.app.ui.main.ShellRoutes;

/**
 * Print guidelines list (S25, FR-5.1): formats, resolution, colour, bleed and fonts.
 *
 * @since 1.0
 */
public class GuidelinesFragment extends BaseFragment<FragmentGuidelinesBinding> {

    /** Screen state. */
    private GuidelinesViewModel viewModel;
    /** The cards. */
    private final GuidelineAdapter adapter = new GuidelineAdapter();

    /** Creates the screen; the framework instantiates it with no arguments. */
    public GuidelinesFragment() {
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
    protected FragmentGuidelinesBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentGuidelinesBinding.inflate(inflater, container, false);
    }

    /**
     * Sets up the list.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(GuidelinesViewModel.class);
        binding.topBar.setOnBackClick(() -> nav().navigateUp());
        binding.listGuidelines.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.listGuidelines.addItemDecoration(new SpacingItemDecoration(getResources().getDimensionPixelSize(R.dimen.space_md)));
        binding.listGuidelines.setAdapter(adapter);
        viewModel.loadAll();
    }

    /** Renders the states. */
    @Override
    protected void initObservers() {
        observe(viewModel.getGuidelines(), r -> {
            if (r.isLoading()) {
                binding.stateLayout.showLoading();
            } else if (r.isError()) {
                binding.stateLayout.showError(r.getMessageRes(), viewModel::loadAll);
            } else {
                adapter.submitList(r.getData());
                binding.stateLayout.showContent();
            }
        });
    }

    /** Opens a guideline. */
    @Override
    protected void initListeners() {
        adapter.setOnItemClickListener((g, p) -> ShellRoutes.guidelineDetail(nav(), g.getGuidelineId()));
    }
}
