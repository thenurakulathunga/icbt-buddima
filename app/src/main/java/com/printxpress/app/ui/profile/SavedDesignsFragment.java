package com.printxpress.app.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.databinding.FragmentSavedDesignsBinding;
import com.printxpress.app.ui.adapters.DesignAdapter;
import com.printxpress.app.ui.components.AppDialog;
import com.printxpress.app.ui.components.SpacingItemDecoration;

/**
 * Saved designs (S33, FR-1.7): uploaded artwork kept for reuse, with delete.
 *
 * @since 1.0
 */
public class SavedDesignsFragment extends BaseFragment<FragmentSavedDesignsBinding> {

    /** Screen state. */
    private SavedDesignsViewModel viewModel;
    /** Design grid. */
    private final DesignAdapter adapter = new DesignAdapter();

    /** Creates the screen; the framework instantiates it with no arguments. */
    public SavedDesignsFragment() {
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
    protected FragmentSavedDesignsBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentSavedDesignsBinding.inflate(inflater, container, false);
    }

    /**
     * Sets up the grid.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(SavedDesignsViewModel.class);
        binding.topBar.setOnBackClick(() -> nav().navigateUp());
        binding.listDesigns.setLayoutManager(new GridLayoutManager(requireContext(),
                getResources().getInteger(R.integer.product_grid_span)));
        binding.listDesigns.addItemDecoration(new SpacingItemDecoration(getResources().getDimensionPixelSize(R.dimen.space_md)));
        binding.listDesigns.setAdapter(adapter);
    }

    /** Renders the states. */
    @Override
    protected void initObservers() {
        observeMessages(viewModel);
        observe(viewModel.getDesigns(), r -> {
            if (r.isLoading()) {
                binding.stateLayout.showLoading();
            } else if (r.isError()) {
                binding.stateLayout.showError(r.getMessageRes(), viewModel::refresh);
            } else if (r.getData().isEmpty()) {
                binding.stateLayout.showEmpty(R.drawable.ic_saved_designs, R.string.empty_designs_title,
                        R.string.empty_designs_message, 0, null);
            } else {
                adapter.submitList(r.getData());
                binding.stateLayout.showContent();
            }
        });
    }

    /** Wires delete with confirmation. */
    @Override
    protected void initListeners() {
        adapter.setOnDelete(d -> new AppDialog.Builder(requireContext()).icon(R.drawable.ic_delete)
                .title(R.string.delete_design_title).message(R.string.delete_design_message)
                .positive(R.string.action_delete, () -> viewModel.delete(d)).negative(R.string.action_cancel)
                .destructive(true).show());
    }

    /** Reloads when shown. */
    @Override
    public void onResume() {
        super.onResume();
        viewModel.refresh();
    }
}
