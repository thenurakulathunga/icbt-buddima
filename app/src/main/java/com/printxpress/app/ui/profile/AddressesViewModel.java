package com.printxpress.app.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseViewModel;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.data.model.Address;
import com.printxpress.app.data.repository.AddressRepository;
import com.printxpress.app.data.repository.AuthRepository;

import java.util.List;

/**
 * Addresses list state (S32, FR-1.6): load, delete (with confirmation in the UI) and default.
 *
 * @since 1.0
 */
public class AddressesViewModel extends BaseViewModel {

    /** Session. */
    private final AuthRepository auth;
    /** Addresses. */
    private final AddressRepository addresses;
    /** The list. */
    private final MutableLiveData<Resource<List<Address>>> list = new MutableLiveData<>();

    /**
     * Creates the ViewModel.
     *
     * @param auth      auth repository
     * @param addresses address repository
     */
    public AddressesViewModel(AuthRepository auth, AddressRepository addresses) {
        this.auth = auth;
        this.addresses = addresses;
    }

    /** Reloads the list. */
    public void refresh() {
        load(addresses.getAddresses(auth.currentUserId()), list);
    }

    /** @return the addresses */
    public LiveData<Resource<List<Address>>> getAddresses() {
        return list;
    }

    /**
     * Deletes an address and reloads.
     *
     * @param address the address
     */
    public void delete(Address address) {
        subscribe(addresses.delete(auth.currentUserId(), address.getAddressId()), r -> {
            if (r.isSuccess()) {
                postMessage(R.string.address_deleted);
                refresh();
            } else if (r.isError()) {
                postError(r.getMessageRes());
            }
        });
    }
}
