package com.printxpress.app.data.repository;

import androidx.lifecycle.LiveData;

import com.printxpress.app.R;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.core.concurrency.AppExecutors;
import com.printxpress.app.core.validation.CompositeRule;
import com.printxpress.app.core.validation.rules.LengthRule;
import com.printxpress.app.core.validation.rules.RequiredRule;
import com.printxpress.app.data.dao.FaqDao;
import com.printxpress.app.data.dao.GuidelineDao;
import com.printxpress.app.data.dao.SupportTicketDao;
import com.printxpress.app.data.model.Faq;
import com.printxpress.app.data.model.Guideline;
import com.printxpress.app.data.model.SupportTicket;
import com.printxpress.app.data.model.enums.TicketStatus;

import java.util.List;

/**
 * SQLite-backed {@link SupportRepository}.
 *
 * @since 1.0
 */
public class SupportRepositoryImpl extends BaseRepository implements SupportRepository {

    /** Guideline access. */
    private final GuidelineDao guidelineDao;
    /** FAQ access. */
    private final FaqDao faqDao;
    /** Ticket access. */
    private final SupportTicketDao ticketDao;

    /**
     * Creates the repository.
     *
     * @param executors    background executors
     * @param guidelineDao guideline DAO
     * @param faqDao       FAQ DAO
     * @param ticketDao    ticket DAO
     */
    public SupportRepositoryImpl(AppExecutors executors, GuidelineDao guidelineDao, FaqDao faqDao,
                                 SupportTicketDao ticketDao) {
        super(executors);
        this.guidelineDao = guidelineDao;
        this.faqDao = faqDao;
        this.ticketDao = ticketDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<List<Guideline>>> getGuidelines() {
        return execute(guidelineDao::findAllSorted);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<Guideline>> getGuideline(long guidelineId) {
        return execute(() -> guidelineDao.findById(guidelineId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<List<Faq>>> searchFaqs(String query) {
        return execute(() -> faqDao.search(query));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<List<SupportTicket>>> getTickets(long userId) {
        return execute(() -> ticketDao.findByUser(userId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<Long>> createTicket(SupportTicket ticket) {
        return execute(() -> {
            require(new CompositeRule(new RequiredRule(0), new LengthRule(5, 80, 0))
                    .validate(ticket.getSubject()).isValid(), R.string.err_ticket_subject);
            require(new CompositeRule(new RequiredRule(0), new LengthRule(10, 1000, 0))
                    .validate(ticket.getMessage()).isValid(), R.string.err_ticket_invalid);
            ticket.setSubject(ticket.getSubject().trim());
            ticket.setMessage(ticket.getMessage().trim());
            ticket.setStatus(TicketStatus.OPEN);
            return ticketDao.insert(ticket);
        });
    }
}
