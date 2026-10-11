package com.printxpress.app.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseActivity;
import com.printxpress.app.databinding.ActivityOnboardingBinding;
import com.printxpress.app.databinding.ItemOnboardingPageBinding;
import com.printxpress.app.ui.auth.AuthActivity;

/**
 * First-run onboarding (S02): Browse · Upload · Track. Finishing or skipping marks onboarding as
 * done and opens sign-in.
 *
 * @since 1.0
 */
public class OnboardingActivity extends BaseActivity<ActivityOnboardingBinding> {

    /** Slide icons. */
    private static final int[] ICONS = {R.drawable.ic_nav_catalog, R.drawable.ic_upload, R.drawable.ic_delivery};
    /** Slide titles. */
    private static final int[] TITLES = {R.string.onboard_title_1, R.string.onboard_title_2, R.string.onboard_title_3};
    /** Slide texts. */
    private static final int[] TEXTS = {R.string.onboard_text_1, R.string.onboard_text_2, R.string.onboard_text_3};

    /** Creates the activity; the system instantiates it from the manifest. */
    public OnboardingActivity() {
        super();
    }

    /**
     * Inflates the layout.
     *
     * @param inflater layout inflater
     * @return the binding
     */
    @Override
    protected ActivityOnboardingBinding inflateBinding(LayoutInflater inflater) {
        return ActivityOnboardingBinding.inflate(inflater);
    }

    /**
     * Sets up the pager and dots.
     *
     * @param savedInstanceState saved state
     */
    @Override
    protected void initViews(Bundle savedInstanceState) {
        binding.pager.setAdapter(new SlideAdapter());
        binding.indicator.attachTo(binding.pager);
        binding.pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.btnNext.setText(position == ICONS.length - 1 ? R.string.onboard_get_started : R.string.action_next);
            }
        });
    }

    /** Wires Skip and Next. */
    @Override
    protected void initListeners() {
        binding.btnSkip.setOnClickListener(v -> finishOnboarding());
        binding.btnNext.setOnClickListener(v -> {
            int page = binding.pager.getCurrentItem();
            if (page < ICONS.length - 1) {
                binding.pager.setCurrentItem(page + 1);
            } else {
                finishOnboarding();
            }
        });
    }

    /** Records completion and opens sign-in. */
    private void finishOnboarding() {
        container().getSession().setOnboardingDone();
        startActivity(new Intent(this, AuthActivity.class));
        finish();
    }

    /**
     * Adapter for the three slides.
     */
    private static final class SlideAdapter extends RecyclerView.Adapter<SlideHolder> {

        /**
         * Creates a slide.
         *
         * @param parent   the pager
         * @param viewType ignored
         * @return the holder
         */
        @NonNull
        @Override
        public SlideHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SlideHolder(ItemOnboardingPageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
        }

        /**
         * Binds a slide.
         *
         * @param holder   the holder
         * @param position the slide
         */
        @Override
        public void onBindViewHolder(@NonNull SlideHolder holder, int position) {
            holder.binding.imgIllustration.setImageResource(ICONS[position]);
            holder.binding.txtTitle.setText(TITLES[position]);
            holder.binding.txtText.setText(TEXTS[position]);
        }

        /** @return three slides */
        @Override
        public int getItemCount() {
            return ICONS.length;
        }
    }

    /**
     * Slide holder.
     */
    private static final class SlideHolder extends RecyclerView.ViewHolder {

        /** The slide binding. */
        private final ItemOnboardingPageBinding binding;

        /**
         * Creates a holder.
         *
         * @param binding the slide binding
         */
        SlideHolder(ItemOnboardingPageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
