package com.printxpress.app.core.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.printxpress.app.PrintXpressApp;

/**
 * Base for rounded-top bottom sheets (filter, sort, reschedule, cancel) with the same
 * {@code initViews → initObservers → initListeners} template as {@link BaseFragment}.
 * The 32 dp top corners come from the theme's bottom sheet style (docs/05 §6.1).
 *
 * @param <VB> the ViewBinding type of the sheet layout
 * @since 1.0
 */
public abstract class BaseBottomSheet<VB extends ViewBinding> extends BottomSheetDialogFragment {

    /** The binding, valid between onCreateView and onDestroyView. */
    protected VB binding;

    /** For subclasses only. */
    protected BaseBottomSheet() {
        super();
    }

    /**
     * Inflates the binding.
     *
     * @param inflater  layout inflater
     * @param container parent
     * @param saved     saved state
     * @return the root view
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle saved) {
        binding = inflateBinding(inflater, container);
        return binding.getRoot();
    }

    /**
     * Expands the sheet fully and runs the template steps.
     *
     * @param view  the root view
     * @param saved saved state
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle saved) {
        super.onViewCreated(view, saved);
        if (getDialog() instanceof BottomSheetDialog) {
            BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
            dialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
            dialog.getBehavior().setSkipCollapsed(true);
        }
        initViews(saved);
        initObservers();
        initListeners();
    }

    /** Clears the binding. */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Inflates the sheet binding.
     *
     * @param inflater  layout inflater
     * @param container parent, may be null
     * @return the binding
     */
    protected abstract VB inflateBinding(LayoutInflater inflater, ViewGroup container);

    /**
     * Configures views.
     *
     * @param saved saved state, may be null
     */
    protected abstract void initViews(@Nullable Bundle saved);

    /** Subscribes to LiveData. */
    protected void initObservers() {
    }

    /** Registers listeners. */
    protected void initListeners() {
    }

    /**
     * Shows the sheet with its class name as tag.
     *
     * @param manager the fragment manager
     */
    public void show(FragmentManager manager) {
        show(manager, getClass().getSimpleName());
    }

    /**
     * Obtains a ViewModel scoped to the parent fragment, so the sheet can share its state.
     *
     * @param type the ViewModel class
     * @param <T>  the ViewModel type
     * @return the ViewModel
     */
    protected <T extends ViewModel> T parentViewModel(Class<T> type) {
        ViewModelProvider.Factory factory = ((PrintXpressApp) requireActivity().getApplication()).getViewModelFactory();
        return new ViewModelProvider(requireParentFragment(), factory).get(type);
    }
}
