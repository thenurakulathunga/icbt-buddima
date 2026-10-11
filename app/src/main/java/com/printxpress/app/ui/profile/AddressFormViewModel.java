package com.printxpress.app.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFormViewModel;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.core.common.Event;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.core.validation.rules.LengthRule;
import com.printxpress.app.core.validation.rules.PostalCodeRule;
import com.printxpress.app.core.validation.rules.RegexRule;
import com.printxpress.app.core.validation.rules.RequiredRule;
import com.printxpress.app.core.validation.rules.SriLankanPhoneRule;
import com.printxpress.app.data.model.Address;
import com.printxpress.app.data.model.District;
import com.printxpress.app.data.repository.AddressRepository;
import com.printxpress.app.data.repository.AuthRepository;
import com.printxpress.app.data.repository.UserRepository;

import java.util.List;

/**
 * Add or edit an address (S32, FR-1.6, TC-15, TC-16) with the docs/01 §8 rules; the district
 * decides the delivery fee.
 *
 * @since 1.0
 */
public class AddressFormViewModel extends BaseFormViewModel {

    /** Field key: label. */
    public static final String FIELD_LABEL = "label";
    /** Field key: recipient. */
    public static final String FIELD_RECIPIENT = "recipient";
    /** Field key: recipient phone. */
    public static final String FIELD_PHONE = "phone";
    /** Field key: line 1. */
    public static final String FIELD_LINE1 = "line1";
    /** Field key: line 2. */
    public static final String FIELD_LINE2 = "line2";
    /** Field key: city. */
    public static final String FIELD_CITY = "city";
    /** Field key: district id. */
    public static final String FIELD_DISTRICT = "district";
    /** Field key: postal code. */
    public static final String FIELD_POSTAL = "postal";

    /** Session. */
    private final AuthRepository auth;
    /** Profile, for pre-filling the recipient. */
    private final UserRepository users;
    /** Addresses. */
    private final AddressRepository addresses;
    /** Districts. */
    private final MutableLiveData<Resource<List<District>>> districts = new MutableLiveData<>();
    /** One-shot signal that the fields were pre-filled. */
    private final MutableLiveData<Event<Boolean>> prefilled = new MutableLiveData<>();
    /** One-shot saved signal. */
    private final MutableLiveData<Event<Boolean>> saved = new MutableLiveData<>();
    /** Address being edited, 0 for new. */
    private long addressId = -1;
    /** Default flag. */
    private boolean makeDefault;

    /**
     * Creates the ViewModel.
     *
     * @param auth      auth repository
     * @param users     user repository
     * @param addresses address repository
     */
    public AddressFormViewModel(AuthRepository auth, UserRepository users, AddressRepository addresses) {
        this.auth = auth;
        this.users = users;
        this.addresses = addresses;
        validator.field(FIELD_LABEL, new RequiredRule(R.string.err_label_invalid));
        validator.field(FIELD_RECIPIENT, new RequiredRule(R.string.err_name_invalid),
                new LengthRule(3, 60, R.string.err_name_invalid), new RegexRule(Constants.REGEX_NAME, R.string.err_name_invalid));
        validator.field(FIELD_PHONE, new RequiredRule(R.string.err_phone_invalid),
                new SriLankanPhoneRule(R.string.err_phone_invalid));
        validator.field(FIELD_LINE1, new RequiredRule(R.string.err_address_invalid),
                new LengthRule(5, 100, R.string.err_address_invalid));
        validator.field(FIELD_LINE2, new LengthRule(0, 100, R.string.err_address_invalid));
        validator.field(FIELD_CITY, new RequiredRule(R.string.err_city_invalid),
                new RegexRule(Constants.REGEX_CITY, R.string.err_city_invalid));
        validator.field(FIELD_DISTRICT, new RequiredRule(R.string.err_district_required));
        validator.field(FIELD_POSTAL, new RequiredRule(R.string.err_postal_invalid),
                new PostalCodeRule(R.string.err_postal_invalid));
    }

    /**
     * Loads districts and, when editing, the address; runs once.
     *
     * @param id the address id, or 0 for new
     */
    public void start(long id) {
        if (addressId == id) {
            return;
        }
        addressId = id;
        load(addresses.getDistricts(), districts);
        if (id > 0) {
            subscribe(addresses.getAddress(id), r -> {
                if (r.isSuccess() && r.getData() != null) {
                    fill(r.getData());
                }
            });
        } else {
            subscribe(users.getUser(auth.currentUserId()), r -> {
                if (r.isSuccess()) {
                    setValue(FIELD_RECIPIENT, r.getData().getFullName());
                    setValue(FIELD_PHONE, r.getData().getPhone() == null ? "" : r.getData().getPhone());
                    prefilled.setValue(new Event<>(true));
                }
            });
        }
    }

    /**
     * Copies an address into the form.
     *
     * @param a the address
     */
    private void fill(Address a) {
        setValue(FIELD_LABEL, a.getLabel());
        setValue(FIELD_RECIPIENT, a.getRecipientName());
        setValue(FIELD_PHONE, a.getRecipientPhone());
        setValue(FIELD_LINE1, a.getLine1());
        setValue(FIELD_LINE2, a.getLine2() == null ? "" : a.getLine2());
        setValue(FIELD_CITY, a.getCity());
        setValue(FIELD_DISTRICT, String.valueOf(a.getDistrictId()));
        setValue(FIELD_POSTAL, a.getPostalCode());
        makeDefault = a.isDefaultAddress();
        prefilled.setValue(new Event<>(true));
    }

    /** @return true when editing an existing address */
    public boolean isEditing() {
        return addressId > 0;
    }

    /** @return the districts */
    public LiveData<Resource<List<District>>> getDistricts() {
        return districts;
    }

    /** @return fires when stored values should be shown in the fields */
    public LiveData<Event<Boolean>> getPrefilled() {
        return prefilled;
    }

    /** @return fires once after saving */
    public LiveData<Event<Boolean>> getSaved() {
        return saved;
    }

    /** @return the default flag */
    public boolean isMakeDefault() {
        return makeDefault;
    }

    /**
     * Sets the default flag.
     *
     * @param value true to make this the default address
     */
    public void setMakeDefault(boolean value) {
        makeDefault = value;
    }

    /** Validates and saves. */
    public void save() {
        if (!validateAll()) {
            return;
        }
        Address a = new Address();
        a.setAddressId(Math.max(0, addressId));
        a.setUserId(auth.currentUserId());
        a.setLabel(value(FIELD_LABEL).trim());
        a.setRecipientName(value(FIELD_RECIPIENT).trim());
        a.setRecipientPhone(value(FIELD_PHONE).trim());
        a.setLine1(value(FIELD_LINE1).trim());
        a.setLine2(value(FIELD_LINE2).trim().isEmpty() ? null : value(FIELD_LINE2).trim());
        a.setCity(value(FIELD_CITY).trim());
        a.setDistrictId(Long.parseLong(value(FIELD_DISTRICT)));
        a.setPostalCode(value(FIELD_POSTAL).trim());
        a.setDefaultAddress(makeDefault);
        subscribe(addresses.save(a), r -> {
            if (r.isSuccess()) {
                saved.setValue(new Event<>(true));
            } else if (r.isError()) {
                postError(r.getMessageRes());
            }
        }, true);
    }
}
