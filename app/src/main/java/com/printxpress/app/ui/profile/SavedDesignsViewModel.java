package com.printxpress.app.ui.profile;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseViewModel;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.data.model.Design;
import com.printxpress.app.data.repository.AuthRepository;
import com.printxpress.app.data.repository.DesignRepository;

import java.util.List;

/**
 * Saved designs library (S33, FR-1.7): list and delete.
 *
 * @since 1.0
 */
public class SavedDesignsViewModel extends BaseViewModel {

    /** Session. */
    private final AuthRepository auth;
    /** Designs. */
    private final DesignRepository designs;
    /** The library. */
    private final MutableLiveData<Resource<List<Design>>> list = new MutableLiveData<>();

    /**
     * Creates the ViewModel.
     *
     * @param auth    auth repository
     * @param designs design repository
     */
    public SavedDesignsViewModel(AuthRepository auth, DesignRepository designs) {
        this.auth = auth;
        this.designs = designs;
    }

    /** Reloads the library. */
    public void refresh() {
        load(designs.getDesigns(auth.currentUserId()), list);
    }

    /** @return the designs */
    public LiveData<Resource<List<Design>>> getDesigns() {
        return list;
    }

    /**
     * Deletes a design (blocked while a cart line uses it).
     *
     * @param design the design
     */
    public void delete(Design design) {
        subscribe(designs.delete(auth.currentUserId(), design), r -> {
            if (r.isSuccess()) {
                postMessage(R.string.design_deleted);
                refresh();
            } else if (r.isError()) {
                postError(r.getMessageRes());
            }
        });
    }
}
