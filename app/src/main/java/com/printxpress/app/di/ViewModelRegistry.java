package com.printxpress.app.di;

import com.printxpress.app.ui.auth.LoginViewModel;
import com.printxpress.app.ui.auth.RegisterViewModel;
import com.printxpress.app.ui.auth.ResetPasswordViewModel;
import com.printxpress.app.ui.cart.CartViewModel;
import com.printxpress.app.ui.catalog.CatalogViewModel;
import com.printxpress.app.ui.checkout.CheckoutViewModel;
import com.printxpress.app.ui.checkout.OrderSuccessViewModel;
import com.printxpress.app.ui.configure.ConfigureViewModel;
import com.printxpress.app.ui.profile.SavedDesignsViewModel;
import com.printxpress.app.ui.catalog.ProductListViewModel;
import com.printxpress.app.ui.home.HomeViewModel;
import com.printxpress.app.ui.main.MainViewModel;
import com.printxpress.app.ui.product.ProductDetailViewModel;
import com.printxpress.app.ui.profile.AddressFormViewModel;
import com.printxpress.app.ui.profile.AddressesViewModel;
import com.printxpress.app.ui.profile.ChangePasswordViewModel;
import com.printxpress.app.ui.profile.EditProfileViewModel;
import com.printxpress.app.ui.profile.ProfileViewModel;
import com.printxpress.app.ui.gallery.GalleryViewModel;
import com.printxpress.app.ui.notifications.NotificationsViewModel;
import com.printxpress.app.ui.offers.OffersViewModel;
import com.printxpress.app.ui.orders.OrderDetailViewModel;
import com.printxpress.app.ui.orders.OrdersViewModel;
import com.printxpress.app.ui.settings.SettingsViewModel;
import com.printxpress.app.ui.support.ContactViewModel;
import com.printxpress.app.ui.support.FaqsViewModel;
import com.printxpress.app.ui.support.GuidelinesViewModel;
import com.printxpress.app.ui.support.TicketsViewModel;

/**
 * The list of ViewModel creators, one line per screen, kept apart from the factory mechanics.
 *
 * @since 1.0
 */
final class ViewModelRegistry {

    /** Hidden constructor: static registry. */
    private ViewModelRegistry() {
    }

    /**
     * Registers every ViewModel with the factory.
     *
     * @param f the factory to fill
     * @param c the dependency container
     */
    static void registerAll(ViewModelFactory f, AppContainer c) {
        f.register(MainViewModel.class, () -> new MainViewModel(c.getAuthRepository(), c.getCartRepository(),
                c.getNotificationRepository()));
        registerAccount(f, c);
        registerCatalog(f, c);
        registerOrdering(f, c);
        registerTracking(f, c);
        registerSupport(f, c);
    }

    /**
     * Registers order tracking, notification and settings ViewModels.
     *
     * @param f the factory
     * @param c the container
     */
    private static void registerTracking(ViewModelFactory f, AppContainer c) {
        f.register(OrdersViewModel.class, () -> new OrdersViewModel(c.getAuthRepository(), c.getOrderRepository()));
        f.register(OrderDetailViewModel.class, () -> new OrderDetailViewModel(c.getAuthRepository(),
                c.getOrderRepository(), c.getCartRepository(), c.getScheduleCalculator()));
        f.register(NotificationsViewModel.class, () -> new NotificationsViewModel(c.getAuthRepository(),
                c.getNotificationRepository()));
        f.register(SettingsViewModel.class, () -> new SettingsViewModel(c.getAuthRepository(), c.getUserRepository(),
                c.getSession(), c.getOrderProgressScheduler()));
    }

    /**
     * Registers offers, design support and contact ViewModels.
     *
     * @param f the factory
     * @param c the container
     */
    private static void registerSupport(ViewModelFactory f, AppContainer c) {
        f.register(OffersViewModel.class, () -> new OffersViewModel(c.getPromotionRepository()));
        f.register(GuidelinesViewModel.class, () -> new GuidelinesViewModel(c.getSupportRepository(), c.getFiles(),
                c.getExecutors()));
        f.register(FaqsViewModel.class, () -> new FaqsViewModel(c.getSupportRepository()));
        f.register(ContactViewModel.class, () -> new ContactViewModel(c.getAuthRepository(), c.getOrderRepository(),
                c.getSupportRepository()));
        f.register(TicketsViewModel.class, () -> new TicketsViewModel(c.getAuthRepository(), c.getSupportRepository()));
        f.register(GalleryViewModel.class, () -> new GalleryViewModel(c.getCatalogRepository(), c.getGalleryRepository()));
    }

    /**
     * Registers the ordering flow ViewModels.
     *
     * @param f the factory
     * @param c the container
     */
    private static void registerOrdering(ViewModelFactory f, AppContainer c) {
        f.register(ConfigureViewModel.class, () -> new ConfigureViewModel(c.getAuthRepository(),
                c.getCatalogRepository(), c.getCartRepository(), c.getDesignRepository(), c.getPriceCalculator()));
        f.register(CartViewModel.class, () -> new CartViewModel(c.getAuthRepository(), c.getCartRepository(),
                c.getPriceCalculator()));
        f.register(CheckoutViewModel.class, () -> new CheckoutViewModel(c.getAuthRepository(), c.getCartRepository(),
                c.getAddressRepository(), c.getPromotionRepository(), c.getOrderRepository(), c.getPriceCalculator(),
                c.getScheduleCalculator()));
        f.register(OrderSuccessViewModel.class, () -> new OrderSuccessViewModel(c.getOrderRepository()));
        f.register(SavedDesignsViewModel.class, () -> new SavedDesignsViewModel(c.getAuthRepository(),
                c.getDesignRepository()));
    }

    /**
     * Registers sign-in and profile ViewModels.
     *
     * @param f the factory
     * @param c the container
     */
    private static void registerAccount(ViewModelFactory f, AppContainer c) {
        f.register(LoginViewModel.class, () -> new LoginViewModel(c.getAuthRepository()));
        f.register(RegisterViewModel.class, () -> new RegisterViewModel(c.getAuthRepository()));
        f.register(ResetPasswordViewModel.class, () -> new ResetPasswordViewModel(c.getAuthRepository()));
        f.register(ProfileViewModel.class, () -> new ProfileViewModel(c.getAuthRepository(), c.getUserRepository()));
        f.register(EditProfileViewModel.class, () -> new EditProfileViewModel(c.getAuthRepository(), c.getUserRepository()));
        f.register(ChangePasswordViewModel.class, () -> new ChangePasswordViewModel(c.getAuthRepository(),
                c.getUserRepository()));
        f.register(AddressesViewModel.class, () -> new AddressesViewModel(c.getAuthRepository(), c.getAddressRepository()));
        f.register(AddressFormViewModel.class, () -> new AddressFormViewModel(c.getAuthRepository(),
                c.getUserRepository(), c.getAddressRepository()));
    }

    /**
     * Registers home and catalogue ViewModels.
     *
     * @param f the factory
     * @param c the container
     */
    private static void registerCatalog(ViewModelFactory f, AppContainer c) {
        f.register(HomeViewModel.class, () -> new HomeViewModel(c.getAuthRepository(), c.getUserRepository(),
                c.getCatalogRepository(), c.getPromotionRepository(), c.getGalleryRepository(), c.getOrderRepository()));
        f.register(CatalogViewModel.class, () -> new CatalogViewModel(c.getCatalogRepository()));
        f.register(ProductListViewModel.class, () -> new ProductListViewModel(c.getCatalogRepository()));
        f.register(ProductDetailViewModel.class, () -> new ProductDetailViewModel(c.getCatalogRepository(),
                c.getGalleryRepository(), c.getPriceCalculator()));
    }
}
