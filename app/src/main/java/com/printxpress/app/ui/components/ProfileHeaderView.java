package com.printxpress.app.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.StringRes;

import com.bumptech.glide.Glide;
import com.printxpress.app.R;
import com.printxpress.app.data.model.User;
import com.printxpress.app.databinding.ViewProfileHeaderBinding;

import java.io.File;

/**
 * Profile header on the brand gradient: circular avatar, name, email or phone and an edit action.
 * For guests it shows a sign-in prompt instead.
 *
 * @since 1.0
 */
public class ProfileHeaderView extends LinearLayout {

    /** The inflated children. */
    private final ViewProfileHeaderBinding binding;

    /**
     * Creates the header.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public ProfileHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        setBackgroundResource(R.drawable.bg_gradient_brand);
        int pad = getResources().getDimensionPixelSize(R.dimen.space_lg);
        setPadding(pad, pad, pad, pad);
        binding = ViewProfileHeaderBinding.inflate(LayoutInflater.from(context), this);
    }

    /**
     * Shows a user.
     *
     * @param user the user
     */
    public void bind(User user) {
        binding.txtName.setText(user.getFullName());
        String contact = user.getEmail() != null ? user.getEmail() : user.getPhone();
        if (user.getEmail() != null && user.getPhone() != null) {
            contact = user.getEmail() + "\n" + user.getPhone();
        }
        binding.txtContact.setText(contact);
        String path = user.getProfileImagePath();
        if (path != null && new File(path).exists()) {
            Glide.with(this).load(new File(path)).circleCrop().placeholder(R.drawable.img_avatar_placeholder)
                    .into(binding.imgAvatar);
        } else {
            binding.imgAvatar.setImageResource(R.drawable.img_avatar_placeholder);
        }
        binding.btnEdit.setVisibility(VISIBLE);
    }

    /**
     * Shows the guest state.
     *
     * @param title    heading text
     * @param subtitle prompt text
     */
    public void bindGuest(@StringRes int title, @StringRes int subtitle) {
        binding.txtName.setText(title);
        binding.txtContact.setText(subtitle);
        binding.imgAvatar.setImageResource(R.drawable.img_avatar_placeholder);
        binding.btnEdit.setVisibility(GONE);
    }

    /**
     * Registers the edit action.
     *
     * @param listener run on tap
     */
    public void setOnEdit(Runnable listener) {
        binding.btnEdit.setOnClickListener(v -> listener.run());
    }

    /**
     * Registers the avatar tap action.
     *
     * @param listener run on tap
     */
    public void setOnAvatarClick(Runnable listener) {
        binding.imgAvatar.setOnClickListener(v -> listener.run());
    }
}
