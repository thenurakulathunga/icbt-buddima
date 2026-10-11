package com.printxpress.app.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.LinearLayout;

import com.printxpress.app.R;
import com.printxpress.app.core.util.DateTimeUtils;
import com.printxpress.app.data.model.OrderStatusHistory;
import com.printxpress.app.data.model.enums.FulfilmentType;
import com.printxpress.app.data.model.enums.OrderStatus;
import com.printxpress.app.databinding.ViewTimelineStepBinding;
import com.printxpress.app.domain.order.OrderStateFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Vertical order tracker (FR-3.2): done steps are filled blue circles with a check, the current
 * step is a blue ring, upcoming steps are grey rings. Each done step shows its timestamp from the
 * status history (FR-3.6). A cancelled order ends with a red Cancelled step.
 *
 * @since 1.0
 */
public class OrderTimelineView extends LinearLayout {

    /** Duration of the current-step fade (docs/05 §7). */
    private static final long FADE_MS = 300;

    /**
     * Creates the view.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public OrderTimelineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    /**
     * Renders the timeline.
     *
     * @param history    the status history, oldest first
     * @param current    the current status
     * @param fulfilment pickup or delivery (decides step 5)
     */
    public void bind(List<OrderStatusHistory> history, OrderStatus current, FulfilmentType fulfilment) {
        removeAllViews();
        List<OrderStatus> path = new ArrayList<>(OrderStateFactory.pathFor(fulfilment));
        if (current == OrderStatus.CANCELLED) {
            path = reachedSteps(history, path);
            path.add(OrderStatus.CANCELLED);
        }
        int currentIndex = path.indexOf(current);
        for (int i = 0; i < path.size(); i++) {
            OrderStatus step = path.get(i);
            boolean isLast = i == path.size() - 1;
            addStep(step, history, i < currentIndex, i == currentIndex, isLast, current == OrderStatus.COMPLETED);
        }
    }

    /**
     * Keeps only the steps the order actually reached before it was cancelled.
     *
     * @param history the history
     * @param path    the full path
     * @return the reached steps in order
     */
    private static List<OrderStatus> reachedSteps(List<OrderStatusHistory> history, List<OrderStatus> path) {
        List<OrderStatus> reached = new ArrayList<>();
        for (OrderStatus step : path) {
            for (OrderStatusHistory entry : history) {
                if (entry.getStatus() == step) {
                    reached.add(step);
                    break;
                }
            }
        }
        return reached;
    }

    /**
     * Adds one step row.
     *
     * @param step      the status of the row
     * @param history   the history
     * @param done      whether the step is complete
     * @param isCurrent whether it is the current step
     * @param isLast    whether it is the final row (no connector)
     * @param allDone   whether the whole order is completed
     */
    private void addStep(OrderStatus step, List<OrderStatusHistory> history, boolean done, boolean isCurrent,
                         boolean isLast, boolean allDone) {
        ViewTimelineStepBinding row = ViewTimelineStepBinding.inflate(LayoutInflater.from(getContext()), this, true);
        StatusStyle style = StatusStyle.of(step);
        row.txtTitle.setText(style.getLabel());
        boolean cancelled = step == OrderStatus.CANCELLED;
        boolean filled = done || (isCurrent && (allDone || cancelled));
        if (cancelled) {
            row.imgMarker.setBackgroundResource(R.drawable.bg_timeline_cancelled);
            row.imgMarker.setImageResource(R.drawable.ic_close);
        } else if (filled) {
            row.imgMarker.setBackgroundResource(R.drawable.bg_timeline_done);
            row.imgMarker.setImageResource(R.drawable.ic_check);
        } else if (isCurrent) {
            row.imgMarker.setBackgroundResource(R.drawable.bg_timeline_current);
            row.imgMarker.setImageDrawable(null);
            AlphaAnimation fade = new AlphaAnimation(0.3f, 1f);
            fade.setDuration(FADE_MS);
            row.imgMarker.startAnimation(fade);
        } else {
            row.imgMarker.setBackgroundResource(R.drawable.bg_timeline_upcoming);
            row.imgMarker.setImageDrawable(null);
        }
        row.txtTitle.setTextColor(resolve(done || isCurrent
                ? com.google.android.material.R.attr.colorOnSurface : com.google.android.material.R.attr.colorOnSurfaceVariant));
        row.txtSubtitle.setText(subtitleFor(step, history, done || isCurrent));
        row.line.setVisibility(isLast ? View.INVISIBLE : View.VISIBLE);
        row.line.setBackgroundColor(resolve(done ? androidx.appcompat.R.attr.colorPrimary
                : com.google.android.material.R.attr.colorOutline));
        row.getRoot().setContentDescription(row.txtTitle.getText() + ", " + row.txtSubtitle.getText());
    }

    /**
     * Builds the subtitle: timestamps of reaching the step and any reschedule remarks.
     *
     * @param step    the step
     * @param history the history
     * @param reached whether the step has been reached
     * @return the subtitle text
     */
    private String subtitleFor(OrderStatus step, List<OrderStatusHistory> history, boolean reached) {
        if (!reached) {
            return getContext().getString(R.string.timeline_upcoming);
        }
        StringBuilder text = new StringBuilder();
        boolean first = true;
        for (OrderStatusHistory entry : history) {
            if (entry.getStatus() != step) {
                continue;
            }
            if (first) {
                text.append(DateTimeUtils.formatDisplay(entry.getChangedAt()));
                if (step == OrderStatus.CANCELLED && entry.getRemark() != null) {
                    text.append('\n').append(entry.getRemark());
                }
                first = false;
            } else if (entry.getRemark() != null) {
                text.append('\n').append(getContext().getString(R.string.timeline_rescheduled, describeRemark(entry.getRemark())));
            }
        }
        return text.toString();
    }

    /**
     * Turns a stored reschedule remark ({@code yyyy-MM-dd slot}) into display text.
     *
     * @param remark the stored remark
     * @return e.g. "Tue, 06 Oct 2026 · 09:00 - 12:00"
     */
    private static String describeRemark(String remark) {
        LocalDate date = DateTimeUtils.parseDate(remark);
        String slot = remark.length() > 11 ? remark.substring(11) : "";
        return date == null ? remark : DateTimeUtils.formatDisplay(date) + " · " + slot;
    }

    /**
     * Reads a colour attribute.
     *
     * @param attr the attribute
     * @return the colour
     */
    private int resolve(int attr) {
        TypedValue value = new TypedValue();
        getContext().getTheme().resolveAttribute(attr, value, true);
        return value.data;
    }
}
