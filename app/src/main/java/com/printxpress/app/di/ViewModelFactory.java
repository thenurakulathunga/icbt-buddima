package com.printxpress.app.di;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Creates every ViewModel with its dependencies taken from {@link AppContainer}.
 * <p>
 * Pattern role: Factory. Each ViewModel class is registered with a supplier in
 * {@link #registerAll()}; screens obtain ViewModels only through this factory (CLAUDE.md), so
 * ViewModels never look up their own dependencies.
 * </p>
 *
 * @since 1.0
 */
public class ViewModelFactory implements ViewModelProvider.Factory {

    /** Registered creators keyed by ViewModel class. */
    private final Map<Class<? extends ViewModel>, Supplier<? extends ViewModel>> creators = new HashMap<>();
    /** The dependency container. */
    private final AppContainer container;

    /**
     * Creates the factory and registers every ViewModel.
     *
     * @param container the dependency container
     */
    public ViewModelFactory(AppContainer container) {
        this.container = container;
        registerAll();
    }

    /**
     * Registers a creator.
     *
     * @param type    the ViewModel class
     * @param creator supplies a new instance
     * @param <T>     the ViewModel type
     */
    protected <T extends ViewModel> void register(Class<T> type, Supplier<T> creator) {
        creators.put(type, creator);
    }

    /** Registers the creators of every screen's ViewModel. */
    private void registerAll() {
        ViewModelRegistry.registerAll(this, container);
    }

    /**
     * Creates a ViewModel.
     *
     * @param modelClass the requested class
     * @param <T>        the ViewModel type
     * @return a new instance
     * @throws IllegalArgumentException when the class is not registered
     */
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        Supplier<? extends ViewModel> creator = creators.get(modelClass);
        if (creator == null) {
            throw new IllegalArgumentException("Unknown ViewModel " + modelClass.getName());
        }
        return (T) creator.get();
    }
}
