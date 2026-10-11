package com.printxpress.app.data.repository;

import androidx.lifecycle.LiveData;

import com.printxpress.app.core.common.Resource;
import com.printxpress.app.data.model.Faq;
import com.printxpress.app.data.model.Guideline;
import com.printxpress.app.data.model.SupportTicket;

import java.util.List;

/**
 * Print guidelines, FAQs and support tickets (FR-5.1, 5.3, 5.4).
 * <p>Pattern role: Repository.</p>
 *
 * @since 1.0
 */
public interface SupportRepository {

    /** @return all guidelines in display order */
    LiveData<Resource<List<Guideline>>> getGuidelines();

    /**
     * Loads one guideline.
     *
     * @param guidelineId the guideline
     * @return the guideline
     */
    LiveData<Resource<Guideline>> getGuideline(long guidelineId);

    /**
     * Searches FAQs.
     *
     * @param query the search text; blank lists all
     * @return the matching FAQs
     */
    LiveData<Resource<List<Faq>>> searchFaqs(String query);

    /**
     * Lists a user's tickets.
     *
     * @param userId the owner
     * @return the tickets, newest first
     */
    LiveData<Resource<List<SupportTicket>>> getTickets(long userId);

    /**
     * Creates a ticket after re-validating subject and message.
     *
     * @param ticket the ticket
     * @return the new ticket id
     */
    LiveData<Resource<Long>> createTicket(SupportTicket ticket);
}
