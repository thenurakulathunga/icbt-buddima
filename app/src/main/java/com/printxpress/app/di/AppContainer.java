package com.printxpress.app.di;

import android.content.Context;

import com.printxpress.app.core.concurrency.AppExecutors;
import com.printxpress.app.core.security.PasswordHasher;
import com.printxpress.app.core.session.SessionManager;
import com.printxpress.app.core.storage.FileStorageManager;
import com.printxpress.app.data.dao.AddressDao;
import com.printxpress.app.data.dao.BranchDao;
import com.printxpress.app.data.dao.CartDao;
import com.printxpress.app.data.dao.CategoryDao;
import com.printxpress.app.data.dao.DesignDao;
import com.printxpress.app.data.dao.DistrictDao;
import com.printxpress.app.data.dao.FaqDao;
import com.printxpress.app.data.dao.GalleryDao;
import com.printxpress.app.data.dao.GuidelineDao;
import com.printxpress.app.data.dao.NotificationDao;
import com.printxpress.app.data.dao.OrderDao;
import com.printxpress.app.data.dao.ProductDao;
import com.printxpress.app.data.dao.ProductOptionDao;
import com.printxpress.app.data.dao.PromotionDao;
import com.printxpress.app.data.dao.SupportTicketDao;
import com.printxpress.app.data.dao.UserDao;
import com.printxpress.app.data.db.DatabaseHelper;
import com.printxpress.app.data.repository.AddressRepository;
import com.printxpress.app.data.repository.AddressRepositoryImpl;
import com.printxpress.app.data.repository.AuthRepository;
import com.printxpress.app.data.repository.AuthRepositoryImpl;
import com.printxpress.app.data.repository.CartRepository;
import com.printxpress.app.data.repository.CartRepositoryImpl;
import com.printxpress.app.data.repository.CatalogRepository;
import com.printxpress.app.data.repository.CatalogRepositoryImpl;
import com.printxpress.app.data.repository.DesignRepository;
import com.printxpress.app.data.repository.DesignRepositoryImpl;
import com.printxpress.app.data.repository.GalleryRepository;
import com.printxpress.app.data.repository.GalleryRepositoryImpl;
import com.printxpress.app.data.repository.NotificationRepository;
import com.printxpress.app.data.repository.NotificationRepositoryImpl;
import com.printxpress.app.data.repository.OrderRepository;
import com.printxpress.app.data.repository.OrderRepositoryImpl;
import com.printxpress.app.data.repository.PromotionRepository;
import com.printxpress.app.data.repository.PromotionRepositoryImpl;
import com.printxpress.app.data.repository.SupportRepository;
import com.printxpress.app.data.repository.SupportRepositoryImpl;
import com.printxpress.app.data.repository.UserRepository;
import com.printxpress.app.data.repository.UserRepositoryImpl;
import com.printxpress.app.domain.pricing.PriceCalculator;
import com.printxpress.app.domain.schedule.ScheduleCalculator;
import com.printxpress.app.worker.OrderProgressScheduler;

import java.time.LocalDate;

/**
 * Builds and holds every long-lived dependency: helpers, DAOs, repositories and domain services.
 * <p>
 * Pattern role: Service Locator (manual dependency injection). Created once in
 * {@code PrintXpressApp}; ViewModels receive their dependencies from here through
 * {@link ViewModelFactory}, so no class creates its own collaborators.
 * </p>
 *
 * @since 1.0
 */
public class AppContainer {

    /** Application context. */
    private final Context context;
    /** Shared executors. */
    private final AppExecutors executors;
    /** Session storage. */
    private final SessionManager session;
    /** Private file storage. */
    private final FileStorageManager files;
    /** Pricing service. */
    private final PriceCalculator priceCalculator = new PriceCalculator();
    /** Date rules. */
    private final ScheduleCalculator scheduleCalculator = new ScheduleCalculator();
    /** Order progress simulation jobs. */
    private final OrderProgressScheduler orderProgressScheduler;

    /** Authentication. */
    private final AuthRepository authRepository;
    /** Profile. */
    private final UserRepository userRepository;
    /** Addresses, districts and branches. */
    private final AddressRepository addressRepository;
    /** Catalogue. */
    private final CatalogRepository catalogRepository;
    /** Saved designs. */
    private final DesignRepository designRepository;
    /** Cart. */
    private final CartRepository cartRepository;
    /** Promotions. */
    private final PromotionRepository promotionRepository;
    /** Orders. */
    private final OrderRepository orderRepository;
    /** Notification centre. */
    private final NotificationRepository notificationRepository;
    /** Guidelines, FAQs and tickets. */
    private final SupportRepository supportRepository;
    /** Gallery. */
    private final GalleryRepository galleryRepository;

    /**
     * Wires the object graph.
     *
     * @param context any context; the application context is retained
     */
    public AppContainer(Context context) {
        this.context = context.getApplicationContext();
        executors = AppExecutors.getInstance();
        session = SessionManager.getInstance(this.context);
        files = new FileStorageManager(this.context);
        orderProgressScheduler = new OrderProgressScheduler(this.context);
        DatabaseHelper db = DatabaseHelper.getInstance(this.context);

        UserDao userDao = new UserDao(db);
        ProductDao productDao = new ProductDao(db);
        DesignDao designDao = new DesignDao(db);
        CartDao cartDao = new CartDao(db, productDao, designDao);
        OrderDao orderDao = new OrderDao(db);
        AddressDao addressDao = new AddressDao(db);
        BranchDao branchDao = new BranchDao(db);
        ProductOptionDao optionDao = new ProductOptionDao(db);
        PasswordHasher hasher = new PasswordHasher();

        authRepository = new AuthRepositoryImpl(executors, userDao, hasher, session, files);
        userRepository = new UserRepositoryImpl(executors, userDao, hasher, files);
        addressRepository = new AddressRepositoryImpl(executors, addressDao, new DistrictDao(db), branchDao);
        catalogRepository = new CatalogRepositoryImpl(executors, new CategoryDao(db), productDao, optionDao);
        designRepository = new DesignRepositoryImpl(executors, designDao, cartDao, files);
        cartRepository = new CartRepositoryImpl(executors, cartDao, productDao, designDao);
        PromotionRepositoryImpl promotions = new PromotionRepositoryImpl(executors, new PromotionDao(db), orderDao,
                priceCalculator, LocalDate::now);
        promotionRepository = promotions;
        orderRepository = new OrderRepositoryImpl(executors, orderDao, cartDao, addressDao, branchDao, optionDao,
                promotions, priceCalculator, scheduleCalculator);
        notificationRepository = new NotificationRepositoryImpl(executors, new NotificationDao(db));
        supportRepository = new SupportRepositoryImpl(executors, new GuidelineDao(db), new FaqDao(db),
                new SupportTicketDao(db));
        galleryRepository = new GalleryRepositoryImpl(executors, new GalleryDao(db));
    }

    /** @return the order progress scheduler */
    public OrderProgressScheduler getOrderProgressScheduler() {
        return orderProgressScheduler;
    }

    /** @return the application context */
    public Context getContext() {
        return context;
    }

    /** @return the shared executors */
    public AppExecutors getExecutors() {
        return executors;
    }

    /** @return the session manager */
    public SessionManager getSession() {
        return session;
    }

    /** @return the file storage manager */
    public FileStorageManager getFiles() {
        return files;
    }

    /** @return the price calculator */
    public PriceCalculator getPriceCalculator() {
        return priceCalculator;
    }

    /** @return the schedule calculator */
    public ScheduleCalculator getScheduleCalculator() {
        return scheduleCalculator;
    }

    /** @return the auth repository */
    public AuthRepository getAuthRepository() {
        return authRepository;
    }

    /** @return the user repository */
    public UserRepository getUserRepository() {
        return userRepository;
    }

    /** @return the address repository */
    public AddressRepository getAddressRepository() {
        return addressRepository;
    }

    /** @return the catalogue repository */
    public CatalogRepository getCatalogRepository() {
        return catalogRepository;
    }

    /** @return the design repository */
    public DesignRepository getDesignRepository() {
        return designRepository;
    }

    /** @return the cart repository */
    public CartRepository getCartRepository() {
        return cartRepository;
    }

    /** @return the promotion repository */
    public PromotionRepository getPromotionRepository() {
        return promotionRepository;
    }

    /** @return the order repository */
    public OrderRepository getOrderRepository() {
        return orderRepository;
    }

    /** @return the notification repository */
    public NotificationRepository getNotificationRepository() {
        return notificationRepository;
    }

    /** @return the support repository */
    public SupportRepository getSupportRepository() {
        return supportRepository;
    }

    /** @return the gallery repository */
    public GalleryRepository getGalleryRepository() {
        return galleryRepository;
    }
}
