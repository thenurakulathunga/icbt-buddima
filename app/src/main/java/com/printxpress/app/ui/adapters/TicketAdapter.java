package com.printxpress.app.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.StringRes;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseListAdapter;
import com.printxpress.app.core.util.DateTimeUtils;
import com.printxpress.app.data.model.SupportTicket;
import com.printxpress.app.data.model.enums.TicketStatus;
import com.printxpress.app.databinding.ItemTicketBinding;

/**
 * Rows of My tickets.
 * <p>Pattern role: Adapter.</p>
 *
 * @since 1.0
 */
public class TicketAdapter extends BaseListAdapter<SupportTicket, ItemTicketBinding> {

    /** Creates the adapter. */
    public TicketAdapter() {
        super(SupportTicket::getTicketId, t -> t.getStatus() + t.getSubject());
    }

    /**
     * Inflates a row.
     *
     * @param inflater layout inflater
     * @param parent   the list
     * @return the binding
     */
    @Override
    protected ItemTicketBinding inflate(LayoutInflater inflater, ViewGroup parent) {
        return ItemTicketBinding.inflate(inflater, parent, false);
    }

    /**
     * Binds a ticket.
     *
     * @param binding  the row binding
     * @param item     the ticket
     * @param position the position
     */
    @Override
    protected void bind(ItemTicketBinding binding, SupportTicket item, int position) {
        Context context = binding.getRoot().getContext();
        binding.txtSubject.setText(item.getSubject());
        binding.txtStatus.setText(labelOf(item.getStatus()));
        binding.txtMessage.setText(item.getMessage());
        String date = DateTimeUtils.formatDisplay(item.getCreatedAt());
        binding.txtMeta.setText(item.getOrderNumber() == null ? date
                : context.getString(R.string.ticket_meta, date, item.getOrderNumber()));
    }

    /**
     * Maps a status to its label.
     *
     * @param status the status
     * @return the label
     */
    @StringRes
    private static int labelOf(TicketStatus status) {
        if (status == TicketStatus.RESOLVED) {
            return R.string.ticket_status_resolved;
        }
        return status == TicketStatus.IN_PROGRESS ? R.string.ticket_status_progress : R.string.ticket_status_open;
    }
}
