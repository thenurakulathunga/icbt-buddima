package com.printxpress.app;

import android.app.Application;

import androidx.appcompat.app.AppCompatDelegate;

import com.printxpress.app.core.notification.NotificationChannels;
import com.printxpress.app.core.notification.NotificationHelper;
import com.printxpress.app.data.db.DatabaseHelper;
import com.printxpress.app.di.AppContainer;
import com.printxpress.app.di.ViewModelFactory;
import com.printxpress.app.worker.OrderEventsHandler;
import com.printxpress.app.worker.PromotionWorker;

/**
 * Application entry point: builds the {@link AppContainer} and the {@link ViewModelFactory} once,
 * applies the saved theme, creates notification channels, and connects the order simulation.
 *
 * @since 1.0
 */
public class PrintXpressApp extends Application {

    /** The dependency container. */
    private AppContainer container;
    /** The shared ViewModel factory. */
    private ViewModelFactory viewModelFactory;

    /** Creates the application; the system instantiates it from the manifest. */
    public PrintXpressApp() {
        super();
    }

    /** Builds dependencies and applies the theme. */
    @Override
    public void onCreate() {
        super.onCreate();
        container = new AppContainer(this);
        viewModelFactory = new ViewModelFactory(container);
        AppCompatDelegate.setDefaultNightMode(container.getSession().getThemeMode());
        onDependenciesReady();
    }

    /**
     * Start-up work that needs the container: channels, the order events observer, the promotion
     * job, and opening (on first run creating and seeding) the database on the disk thread.
     */
    protected void onDependenciesReady() {
        NotificationChannels.create(this);
        container.getOrderRepository().addListener(new OrderEventsHandler(this, container.getNotificationRepository(),
                container.getUserRepository(), NotificationHelper.getInstance(this),
                container.getOrderProgressScheduler(), container.getSession()));
        container.getExecutors().diskIO().execute(() -> DatabaseHelper.getInstance(this).getWritableDatabase());
        PromotionWorker.schedule(this);
    }

    /** @return the dependency container */
    public AppContainer getContainer() {
        return container;
    }

    /** @return the ViewModel factory */
    public ViewModelFactory getViewModelFactory() {
        return viewModelFactory;
    }
}
