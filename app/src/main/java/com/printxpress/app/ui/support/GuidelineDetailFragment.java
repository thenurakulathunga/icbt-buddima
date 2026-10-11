package com.printxpress.app.ui.support;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.core.util.IntentUtils;
import com.printxpress.app.data.model.Guideline;
import com.printxpress.app.data.model.enums.GuidelineTopic;
import com.printxpress.app.databinding.FragmentGuidelineDetailBinding;
import com.printxpress.app.ui.components.GuidelineCard;

/**
 * Guideline detail (S26, FR-5.1): the full guidance and, for the bleed topic, a to-scale template
 * that can be shared through the FileProvider (FR-5.5).
 *
 * @since 1.0
 */
public class GuidelineDetailFragment extends BaseFragment<FragmentGuidelineDetailBinding> {

    /** Width of the shared template image in pixels (95 mm at 300 DPI). */
    private static final int TEMPLATE_WIDTH_PX = 1122;

    /** Screen state. */
    private GuidelinesViewModel viewModel;

    /** Creates the screen; the framework instantiates it with no arguments. */
    public GuidelineDetailFragment() {
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
    protected FragmentGuidelineDetailBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentGuidelineDetailBinding.inflate(inflater, container, false);
    }

    /**
     * Loads the guideline.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(GuidelinesViewModel.class);
        binding.topBar.setOnBackClick(() -> nav().navigateUp());
        reload();
    }

    /** Loads the guideline from the arguments. */
    private void reload() {
        viewModel.loadOne(requireArguments().getLong(Constants.ARG_GUIDELINE_ID));
    }

    /** Renders the guideline and shares the template when ready. */
    @Override
    protected void initObservers() {
        observeMessages(viewModel);
        observe(viewModel.getGuideline(), r -> {
            if (r.isLoading()) {
                binding.stateLayout.showLoading();
            } else if (r.isError()) {
                binding.stateLayout.showError(r.getMessageRes(), this::reload);
            } else {
                render(r.getData());
                binding.stateLayout.showContent();
            }
        });
        observe(viewModel.getShareUri(), e -> {
            binding.btnShare.setLoading(false);
            if (e.peekContent() != null && e.getContentIfNotHandled() != null
                    && !IntentUtils.shareFile(requireContext(), getString(R.string.template_share_title),
                    e.peekContent(), Constants.MIME_PNG)) {
                showMessage(R.string.err_no_app);
            }
        });
    }

    /**
     * Fills the screen.
     *
     * @param g the guideline
     */
    private void render(Guideline g) {
        binding.topBar.setTitle(g.getTitle());
        binding.imgTopic.setImageResource(GuidelineCard.iconFor(g.getTopic()));
        binding.txtTitle.setText(g.getTitle());
        binding.txtContent.setText(g.getContent());
        binding.groupTemplate.setVisibility(g.getTopic() == GuidelineTopic.BLEED ? View.VISIBLE : View.GONE);
    }

    /** Wires the share action. */
    @Override
    protected void initListeners() {
        binding.btnShare.setOnClickListener(v -> {
            binding.btnShare.setLoading(true);
            viewModel.exportTemplate(binding.template.toBitmap(TEMPLATE_WIDTH_PX));
        });
    }
}
