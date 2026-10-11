package com.printxpress.app.ui.support;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.core.base.BaseViewModel;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.data.model.SupportTicket;
import com.printxpress.app.data.repository.AuthRepository;
import com.printxpress.app.data.repository.SupportRepository;

import java.util.Collections;
import java.util.List;

/**
 * My tickets state (S29, FR-5.3): the signed-in user's support requests.
 *
 * @since 1.0
 */
public class TicketsViewModel extends BaseViewModel {

    /** Session. */
    private final AuthRepository auth;
    /** Tickets. */
    private final SupportRepository support;
    /** The list. */
    private final MutableLiveData<Resource<List<SupportTicket>>> tickets = new MutableLiveData<>();

    /**
     * Creates the ViewModel.
     *
     * @param auth    auth repository
     * @param support support repository
     */
    public TicketsViewModel(AuthRepository auth, SupportRepository support) {
        this.auth = auth;
        this.support = support;
    }

    /** Reloads the list; guests see an empty list. */
    public void refresh() {
        if (!auth.isLoggedIn()) {
            tickets.setValue(Resource.success(Collections.emptyList()));
            return;
        }
        load(support.getTickets(auth.currentUserId()), tickets);
    }

    /** @return the tickets */
    public LiveData<Resource<List<SupportTicket>>> getTickets() {
        return tickets;
    }
}
