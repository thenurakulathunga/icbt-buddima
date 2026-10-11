package com.printxpress.app.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseFragment;
import com.printxpress.app.core.notification.NotificationHelper;
import com.printxpress.app.data.model.AppNotification;
import com.printxpress.app.databinding.FragmentNotificationsBinding;
import com.printxpress.app.ui.adapters.NotificationAdapter;
import com.printxpress.app.ui.main.ShellRoutes;

/**
 * Notification centre (S21, FR-4.4): tap opens the related order or offer and marks it read,
 * swipe marks read, and "Mark all read" clears the badge (TC-52, TC-53).
 *
 * @since 1.0
 */
public class NotificationsFragment extends BaseFragment<FragmentNotificationsBinding> {

    /** Screen state. */
    private NotificationsViewModel viewModel;
    /** The list. */
    private final NotificationAdapter adapter = new NotificationAdapter();

    /** Creates the screen; the framework instantiates it with no arguments. */
    public NotificationsFragment() {
        super();
    }

    /**
     * Inflates the layout.
     *
     * @param inflater  layout inflater
     * @param container parent
     * @return the binding
     */
    @Override
    protected FragmentNotificationsBinding inflateBinding(LayoutInflater inflater, ViewGroup container) {
        return FragmentNotificationsBinding.inflate(inflater, container, false);
    }

    /**
     * Sets up the menu, list and swipe gesture.
     *
     * @param saved saved state
     */
    @Override
    protected void initViews(@Nullable Bundle saved) {
        viewModel = viewModel(NotificationsViewModel.class);
        binding.topBar.setOnBackClick(() -> nav().navigateUp());
        binding.topBar.getToolbar().inflateMenu(R.menu.menu_notifications);
        binding.listNotifications.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.listNotifications.addItemDecoration(new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL));
        binding.listNotifications.setAdapter(adapter);
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh,
                                  @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder vh, int direction) {
                int position = vh.getBindingAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    viewModel.markRead(adapter.getCurrentList().get(position));
                    adapter.notifyItemChanged(position);
                }
            }
        }).attachToRecyclerView(binding.listNotifications);
    }

    /** Renders the states. */
    @Override
    protected void initObservers() {
        observeMessages(viewModel);
        observe(viewModel.getNotifications(), r -> {
            if (r.isLoading() && adapter.getItemCount() == 0) {
                binding.stateLayout.showLoading();
            } else if (r.isError()) {
                binding.stateLayout.showError(r.getMessageRes(), viewModel::refresh);
            } else if (r.isSuccess() && r.getData().isEmpty()) {
                binding.stateLayout.showEmpty(R.drawable.ic_notifications, R.string.empty_notifications_title,
                        R.string.empty_notifications_message, 0, null);
            } else if (r.isSuccess()) {
                adapter.submitList(r.getData());
                binding.stateLayout.showContent();
            }
        });
    }

    /** Wires taps and the menu. */
    @Override
    protected void initListeners() {
        binding.topBar.getToolbar().setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.actionMarkAllRead) {
                viewModel.markAllRead();
                return true;
            }
            return false;
        });
        adapter.setOnItemClickListener((n, p) -> open(n));
    }

    /**
     * Marks read and follows the deep link.
     *
     * @param n the notification
     */
    private void open(AppNotification n) {
        viewModel.markRead(n);
        if (n.getOrderId() != null) {
            ShellRoutes.orderDetail(nav(), n.getOrderId());
        } else if (n.getPromoId() != null) {
            ShellRoutes.offerDetail(nav(), n.getPromoId());
        }
    }

    /** Reloads and checks the permission banner. */
    @Override
    public void onResume() {
        super.onResume();
        viewModel.refresh();
        binding.bannerPermission.setVisibility(NotificationHelper.getInstance(requireContext()).canPost()
                ? View.GONE : View.VISIBLE);
    }
}
