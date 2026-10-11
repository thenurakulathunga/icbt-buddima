package com.printxpress.app.data.dao;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;

import com.printxpress.app.data.db.CursorMapper;
import com.printxpress.app.data.db.DbContract.Orders;
import com.printxpress.app.data.db.DbContract.SupportTickets;
import com.printxpress.app.data.model.SupportTicket;
import com.printxpress.app.data.model.enums.TicketStatus;

import java.util.List;

/**
 * Data access for customer support tickets (FR-5.4).
 *
 * @since 1.0
 */
public class SupportTicketDao extends BaseDao<SupportTicket> {

    /** Row mapper for {@link SupportTicket}. */
    public static final CursorMapper<SupportTicket> MAPPER = row -> {
        SupportTicket t = new SupportTicket();
        t.setTicketId(row.getLong(SupportTickets.ID));
        t.setUserId(row.getLong(SupportTickets.USER_ID));
        t.setOrderId(row.getNullableLong(SupportTickets.ORDER_ID));
        t.setSubject(row.getString(SupportTickets.SUBJECT));
        t.setMessage(row.getString(SupportTickets.MESSAGE));
        t.setAttachmentPath(row.getString(SupportTickets.ATTACHMENT_PATH));
        t.setStatus(TicketStatus.from(row.getString(SupportTickets.STATUS)));
        t.setCreatedAt(row.getDateTime(SupportTickets.CREATED_AT));
        t.setOrderNumber(row.getString("order_number"));
        return t;
    };

    /**
     * Creates the DAO.
     *
     * @param helper the database helper
     */
    public SupportTicketDao(SQLiteOpenHelper helper) {
        super(helper);
    }

    /** @return the support tickets table */
    @Override
    protected String table() {
        return SupportTickets.TABLE;
    }

    /** @return the ticket id column */
    @Override
    protected String idColumn() {
        return SupportTickets.ID;
    }

    /** @return the ticket mapper */
    @Override
    protected CursorMapper<SupportTicket> mapper() {
        return MAPPER;
    }

    /**
     * Converts a ticket to values.
     *
     * @param t the ticket
     * @return the values
     */
    @Override
    protected ContentValues toValues(SupportTicket t) {
        ContentValues v = new ContentValues();
        v.put(SupportTickets.USER_ID, t.getUserId());
        putNullable(v, SupportTickets.ORDER_ID, t.getOrderId());
        v.put(SupportTickets.SUBJECT, t.getSubject());
        v.put(SupportTickets.MESSAGE, t.getMessage());
        v.put(SupportTickets.ATTACHMENT_PATH, t.getAttachmentPath());
        v.put(SupportTickets.STATUS, t.getStatus().name());
        return v;
    }

    /**
     * Returns the id.
     *
     * @param t the ticket
     * @return the id
     */
    @Override
    protected long getId(SupportTicket t) {
        return t.getTicketId();
    }

    /**
     * Stores the id.
     *
     * @param t  the ticket
     * @param id the id
     */
    @Override
    protected void setId(SupportTicket t, long id) {
        t.setTicketId(id);
    }

    /**
     * Lists a user's tickets, newest first, with the related order number.
     *
     * @param userId the owner
     * @return the tickets
     */
    public List<SupportTicket> findByUser(long userId) {
        return queryList("SELECT t.*, o." + Orders.ORDER_NUMBER + " AS order_number FROM " + SupportTickets.TABLE
                + " t LEFT JOIN " + Orders.TABLE + " o ON o." + Orders.ID + " = t." + SupportTickets.ORDER_ID
                + " WHERE t." + SupportTickets.USER_ID + " = ? ORDER BY t." + SupportTickets.ID + " DESC",
                String.valueOf(userId));
    }
}
