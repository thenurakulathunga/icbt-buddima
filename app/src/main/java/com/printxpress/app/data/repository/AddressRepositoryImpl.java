package com.printxpress.app.data.repository;

import androidx.lifecycle.LiveData;

import com.printxpress.app.R;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.core.concurrency.AppExecutors;
import com.printxpress.app.core.validation.rules.LengthRule;
import com.printxpress.app.core.validation.rules.PostalCodeRule;
import com.printxpress.app.core.validation.rules.RegexRule;
import com.printxpress.app.core.validation.rules.RequiredRule;
import com.printxpress.app.core.validation.rules.SriLankanPhoneRule;
import com.printxpress.app.data.dao.AddressDao;
import com.printxpress.app.data.dao.BranchDao;
import com.printxpress.app.data.dao.DistrictDao;
import com.printxpress.app.data.db.DbContract.Branches;
import com.printxpress.app.data.model.Address;
import com.printxpress.app.data.model.Branch;
import com.printxpress.app.data.model.District;

import java.util.List;

/**
 * SQLite-backed {@link AddressRepository}.
 *
 * @since 1.0
 */
public class AddressRepositoryImpl extends BaseRepository implements AddressRepository {

    /** Address access. */
    private final AddressDao addressDao;
    /** District lookup. */
    private final DistrictDao districtDao;
    /** Branch lookup. */
    private final BranchDao branchDao;

    /**
     * Creates the repository.
     *
     * @param executors   background executors
     * @param addressDao  address DAO
     * @param districtDao district DAO
     * @param branchDao   branch DAO
     */
    public AddressRepositoryImpl(AppExecutors executors, AddressDao addressDao, DistrictDao districtDao,
                                 BranchDao branchDao) {
        super(executors);
        this.addressDao = addressDao;
        this.districtDao = districtDao;
        this.branchDao = branchDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<List<Address>>> getAddresses(long userId) {
        return execute(() -> addressDao.findByUser(userId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<Address>> getAddress(long addressId) {
        return execute(() -> addressDao.findById(addressId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<Long>> save(Address address) {
        return execute(() -> {
            require(new RequiredRule(0).validate(address.getLabel()).isValid(), R.string.err_label_invalid);
            require(new LengthRule(3, 60, 0).validate(address.getRecipientName()).isValid()
                    && new RequiredRule(0).validate(address.getRecipientName()).isValid(), R.string.err_name_invalid);
            require(new RequiredRule(0).validate(address.getRecipientPhone()).isValid()
                    && new SriLankanPhoneRule(0).validate(address.getRecipientPhone()).isValid(),
                    R.string.err_phone_invalid);
            require(new RequiredRule(0).validate(address.getLine1()).isValid()
                    && new LengthRule(5, 100, 0).validate(address.getLine1()).isValid(), R.string.err_address_invalid);
            require(new RequiredRule(0).validate(address.getCity()).isValid()
                    && new RegexRule(Constants.REGEX_CITY, 0).validate(address.getCity()).isValid(),
                    R.string.err_city_invalid);
            require(new RequiredRule(0).validate(address.getPostalCode()).isValid()
                    && new PostalCodeRule(0).validate(address.getPostalCode()).isValid(), R.string.err_postal_invalid);
            require(districtDao.findById(address.getDistrictId()) != null, R.string.err_district_required);
            address.setRecipientPhone(SriLankanPhoneRule.normalize(address.getRecipientPhone()));
            return addressDao.save(address);
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<Boolean>> delete(long userId, long addressId) {
        return execute(() -> addressDao.delete(userId, addressId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<Boolean>> setDefault(long userId, long addressId) {
        return execute(() -> {
            addressDao.setDefault(userId, addressId);
            return true;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<List<District>>> getDistricts() {
        return execute(districtDao::findAllSorted);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<List<Branch>>> getBranches() {
        return execute(() -> branchDao.findAll(Branches.ID));
    }
}
