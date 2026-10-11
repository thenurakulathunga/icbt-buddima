package com.printxpress.app.data.repository;

import androidx.lifecycle.LiveData;

import com.printxpress.app.core.common.Resource;
import com.printxpress.app.data.model.Address;
import com.printxpress.app.data.model.Branch;
import com.printxpress.app.data.model.District;

import java.util.List;

/**
 * Delivery addresses, districts and pickup branches (FR-1.6, FR-2.9).
 * <p>Pattern role: Repository.</p>
 *
 * @since 1.0
 */
public interface AddressRepository {

    /**
     * Lists a user's addresses, default first.
     *
     * @param userId the owner
     * @return the addresses
     */
    LiveData<Resource<List<Address>>> getAddresses(long userId);

    /**
     * Loads one address.
     *
     * @param addressId the address
     * @return the address
     */
    LiveData<Resource<Address>> getAddress(long addressId);

    /**
     * Inserts or updates an address after re-validating it.
     *
     * @param address the address (id 0 for a new one)
     * @return the address id
     */
    LiveData<Resource<Long>> save(Address address);

    /**
     * Deletes an address.
     *
     * @param userId    the owner
     * @param addressId the address
     * @return true on success
     */
    LiveData<Resource<Boolean>> delete(long userId, long addressId);

    /**
     * Makes an address the default.
     *
     * @param userId    the owner
     * @param addressId the address
     * @return true on success
     */
    LiveData<Resource<Boolean>> setDefault(long userId, long addressId);

    /** @return all 25 districts sorted by name */
    LiveData<Resource<List<District>>> getDistricts();

    /** @return the pickup branches */
    LiveData<Resource<List<Branch>>> getBranches();
}
