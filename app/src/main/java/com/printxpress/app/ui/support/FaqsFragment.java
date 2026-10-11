package com.printxpress.app.ui.support;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.databinding.FragmentFaqsBinding;
import com.printxpress.app.ui.adapters.FaqAdapter;
import com.printxpress.app.ui.components.SpacingItemDecoration;

/**
 * FAQs (S27, FR-5.2, TC-58): live search over questions and answers, tap to expand.
 *
 * @since 1.0
 */
public class FaqsFragment extends BaseFragment<FragmentFaqsBinding> {

    /** Screen state. */
    private FaqsViewModel viewModel;
    /** The rows. */
    private final FaqAdapter adapter = new FaqAdapter();

    /** Creates the screen; the framework instantiates it with no arguments. */
    public FaqsFragment() {
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
    protected FragmentFaqsBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentFaqsBinding.inflate(inflater, container, false);
    }

    /**
     * Sets up the list and loads everything.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(FaqsViewModel.class);
        binding.topBar.setOnBackClick(() -> nav().navigateUp());
        binding.listFaqs.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.listFaqs.addItemDecoration(new SpacingItemDecoration(getResources().getDimensionPixelSize(R.dimen.space_sm)));
        binding.listFaqs.setAdapter(adapter);
        if (viewModel.getFaqs().getValue() == null) {
            viewModel.search("");
        }
    }

    /** Renders the states. */
    @Override
    protected void initObservers() {
        observe(viewModel.getFaqs(), r -> {
            if (r.isLoading() && adapter.getItemCount() == 0) {
                binding.stateLayout.showLoading();
            } else if (r.isError()) {
                binding.stateLayout.showError(r.getMessageRes(), viewModel::refresh);
            } else if (r.isSuccess() && r.getData().isEmpty()) {
                adapter.submitList(r.getData());
                binding.stateLayout.showEmpty(R.drawable.ic_faq, R.string.empty_faqs_title,
                        R.string.empty_faqs_message, 0, null);
            } else if (r.isSuccess()) {
                adapter.submitList(r.getData());
                binding.stateLayout.showContent();
            }
        });
    }

    /** Searches as the user types. */
    @Override
    protected void initListeners() {
        binding.searchBar.setOnQueryChanged(viewModel::search);
        binding.searchBar.setOnSubmit(viewModel::search);
    }
}
