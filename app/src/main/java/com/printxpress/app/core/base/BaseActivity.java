package com.printxpress.app.core.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.printxpress.app.PrintXpressApp;
import com.printxpress.app.di.AppContainer;
import com.printxpress.app.ui.components.SnackbarHelper;

/**
 * Template Method base for all activities: fixes the set-up order, subclasses supply the steps.
 * <p>
 * Also enables edge-to-edge drawing (enforced from Android 15 with targetSdk 36) and pads the
 * root view by the system-bar and keyboard insets so content is never hidden.
 * </p>
 *
 * @param <VB> the ViewBinding type of the activity layout
 * @since 1.0
 */
public abstract class BaseActivity<VB extends ViewBinding> extends AppCompatActivity {

    /** The inflated view binding, available after {@link #onCreate(Bundle)}. */
    protected VB binding;

    /** For subclasses only. */
    protected BaseActivity() {
        super();
    }

    /**
     * Inflates the binding and runs the template steps in order.
     *
     * @param savedInstanceState previously saved state, may be null
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        beforeCreate();
        EdgeToEdge.enable(this);
        super.onCreate(savedInstanceState);
        binding = inflateBinding(getLayoutInflater());
        setContentView(binding.getRoot());
        if (applySystemInsets()) {
            applyInsets(binding.getRoot());
        }
        initViews(savedInstanceState);
        initObservers();
        initListeners();
    }

    /**
     * Inflates the layout binding.
     *
     * @param inflater layout inflater
     * @return the inflated binding
     */
    protected abstract VB inflateBinding(LayoutInflater inflater);

    /**
     * Configures views; called once.
     *
     * @param savedInstanceState previously saved state, may be null
     */
    protected abstract void initViews(Bundle savedInstanceState);

    /** Subscribes to ViewModel LiveData. */
    protected void initObservers() {
    }

    /** Registers click and input listeners. */
    protected void initListeners() {
    }

    /** Hook that runs before {@code super.onCreate}, e.g. to install the splash screen. */
    protected void beforeCreate() {
    }

    /** @return true to pad the root by system bars and the keyboard (default) */
    protected boolean applySystemInsets() {
        return true;
    }

    /**
     * Pads a view by the system bar and IME insets.
     *
     * @param root the view to pad
     */
    protected void applyInsets(View root) {
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
            Insets ime = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(bars.left, bars.top, bars.right, Math.max(bars.bottom, ime.bottom));
            return WindowInsetsCompat.CONSUMED;
        });
    }

    /** @return the dependency container */
    protected AppContainer container() {
        return ((PrintXpressApp) getApplication()).getContainer();
    }

    /**
     * Obtains a ViewModel scoped to this activity through the shared factory.
     *
     * @param type the ViewModel class
     * @param <T>  the ViewModel type
     * @return the ViewModel
     */
    protected <T extends ViewModel> T viewModel(Class<T> type) {
        return new ViewModelProvider(this, ((PrintXpressApp) getApplication()).getViewModelFactory()).get(type);
    }

    /**
     * Shows an informational snackbar.
     *
     * @param messageRes the message
     */
    protected void showMessage(@StringRes int messageRes) {
        SnackbarHelper.info(binding.getRoot(), messageRes);
    }
}
