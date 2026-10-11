package com.printxpress.app.core.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.viewbinding.ViewBinding;

import com.printxpress.app.PrintXpressApp;
import com.printxpress.app.di.AppContainer;
import com.printxpress.app.ui.components.SnackbarHelper;

/**
 * Template Method base for all fragments: inflates the binding, runs
 * {@code initViews → initObservers → initListeners}, and clears the binding in
 * {@link #onDestroyView()} to avoid leaking views.
 *
 * @param <VB> the ViewBinding type of the fragment layout
 * @since 1.0
 */
public abstract class BaseFragment<VB extends ViewBinding> extends Fragment {

    /** The binding, valid between onCreateView and onDestroyView. */
    protected VB binding;

    /** For subclasses only. */
    protected BaseFragment() {
        super();
    }

    /**
     * Inflates the binding.
     *
     * @param inflater  layout inflater
     * @param container parent view group
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
     * Runs the template steps.
     *
     * @param view  the root view
     * @param saved saved state
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle saved) {
        super.onViewCreated(view, saved);
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
     * Inflates the layout binding.
     *
     * @param inflater  layout inflater
     * @param container parent, may be null
     * @return the binding
     */
    protected abstract VB inflateBinding(LayoutInflater inflater, ViewGroup container);

    /**
     * Configures views; called once per view creation.
     *
     * @param saved saved state, may be null
     */
    protected abstract void initViews(@Nullable Bundle saved);

    /** Subscribes to ViewModel LiveData with the view lifecycle. */
    protected void initObservers() {
    }

    /** Registers click and input listeners. */
    protected void initListeners() {
    }

    /** @return the dependency container */
    protected AppContainer container() {
        return ((PrintXpressApp) requireActivity().getApplication()).getContainer();
    }

    /** @return the shared ViewModel factory */
    protected ViewModelProvider.Factory factory() {
        return ((PrintXpressApp) requireActivity().getApplication()).getViewModelFactory();
    }

    /**
     * Obtains a ViewModel scoped to this fragment.
     *
     * @param type the ViewModel class
     * @param <T>  the ViewModel type
     * @return the ViewModel
     */
    protected <T extends ViewModel> T viewModel(Class<T> type) {
        return new ViewModelProvider(this, factory()).get(type);
    }

    /**
     * Obtains a ViewModel scoped to the hosting activity (shared between fragments).
     *
     * @param type the ViewModel class
     * @param <T>  the ViewModel type
     * @return the ViewModel
     */
    protected <T extends ViewModel> T activityViewModel(Class<T> type) {
        return new ViewModelProvider(requireActivity(), factory()).get(type);
    }

    /**
     * Obtains a ViewModel scoped to a navigation graph, shared by the screens of one flow
     * (configure and design input; checkout and review) and cleared when the flow ends.
     *
     * @param graphId the nested graph id
     * @param type    the ViewModel class
     * @param <T>     the ViewModel type
     * @return the ViewModel
     */
    protected <T extends ViewModel> T graphViewModel(@IdRes int graphId, Class<T> type) {
        return new ViewModelProvider(nav().getViewModelStoreOwner(graphId), factory()).get(type);
    }

    /**
     * Observes LiveData with the view lifecycle.
     *
     * @param data     the stream
     * @param observer the observer
     * @param <T>      the value type
     */
    protected <T> void observe(LiveData<T> data, Observer<T> observer) {
        data.observe(getViewLifecycleOwner(), observer);
    }

    /**
     * Shows one-shot messages from a ViewModel as snackbars.
     *
     * @param viewModel the ViewModel
     */
    protected void observeMessages(BaseViewModel viewModel) {
        observe(viewModel.getMessage(), event -> {
            Integer res = event == null ? null : event.getContentIfNotHandled();
            if (res != null && binding != null) {
                SnackbarHelper.info(binding.getRoot(), res);
            }
        });
    }

    /** @return the navigation controller */
    protected NavController nav() {
        return NavHostFragment.findNavController(this);
    }

    /**
     * Navigates to a destination or action.
     *
     * @param id   destination or action id
     * @param args arguments, may be null
     */
    protected void navigate(@IdRes int id, @Nullable Bundle args) {
        nav().navigate(id, args);
    }

    /**
     * Shows an informational snackbar.
     *
     * @param messageRes the message
     */
    protected void showMessage(@StringRes int messageRes) {
        if (binding != null) {
            SnackbarHelper.info(binding.getRoot(), messageRes);
        }
    }
}
