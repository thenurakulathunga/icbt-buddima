package com.printxpress.app.ui.components;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import androidx.annotation.StringRes;

import com.printxpress.app.R;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.databinding.ViewFileUploadBinding;

/**
 * Artwork picker zone (FR-2.6): a dashed empty state that opens the system file picker, then a
 * preview with thumbnail or PDF icon, file name, size, replace and remove.
 * The component never reads files itself; the screen passes in the chosen file's details.
 *
 * @since 1.0
 */
public class FileUploadView extends LinearLayout {

    /** Bytes per kilobyte. */
    private static final double KB = 1024d;

    /** The inflated children. */
    private final ViewFileUploadBinding binding;
    /** Pick callback, may be null. */
    private Runnable onPick;
    /** Remove callback, may be null. */
    private Runnable onRemove;

    /**
     * Creates the view.
     *
     * @param context the context
     * @param attrs   XML attributes
     */
    public FileUploadView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        binding = ViewFileUploadBinding.inflate(LayoutInflater.from(context), this);
        binding.zoneEmpty.setContentDescription(context.getString(R.string.upload_title));
        binding.zoneEmpty.setOnClickListener(v -> {
            if (onPick != null) {
                onPick.run();
            }
        });
        binding.btnReplace.setOnClickListener(v -> {
            if (onPick != null) {
                onPick.run();
            }
        });
        binding.btnRemove.setOnClickListener(v -> {
            clear();
            if (onRemove != null) {
                onRemove.run();
            }
        });
    }

    /**
     * Shows the preview for a file.
     *
     * @param name     display name
     * @param mimeType MIME type
     * @param size     size in bytes
     * @param path     private path for image thumbnails, may be null
     */
    public void setFile(String name, String mimeType, long size, String path) {
        binding.zoneEmpty.setVisibility(GONE);
        binding.cardPreview.setVisibility(VISIBLE);
        binding.txtName.setText(name);
        binding.txtSize.setText(formatSize(getContext(), size));
        boolean isImage = mimeType != null && !Constants.MIME_PDF.equals(mimeType);
        binding.imgThumb.setFile(path, isImage, Constants.MIME_PDF.equals(mimeType) ? R.drawable.ic_pdf : R.drawable.ic_image);
        setError(0);
    }

    /** Returns to the empty drop zone. */
    public void clear() {
        binding.cardPreview.setVisibility(GONE);
        binding.zoneEmpty.setVisibility(VISIBLE);
    }

    /** @return true when a file preview is showing */
    public boolean hasFile() {
        return binding.cardPreview.getVisibility() == VISIBLE;
    }

    /**
     * Shows an error under the zone.
     *
     * @param messageRes the message, or 0 to clear
     */
    public void setError(@StringRes int messageRes) {
        binding.txtError.setText(messageRes == 0 ? null : getContext().getString(messageRes));
        binding.txtError.setVisibility(messageRes == 0 ? GONE : VISIBLE);
    }

    /**
     * Registers the pick callback.
     *
     * @param listener run when the zone or Replace is tapped
     */
    public void setOnPickClickListener(Runnable listener) {
        this.onPick = listener;
    }

    /**
     * Registers the remove callback.
     *
     * @param listener run after the file is removed
     */
    public void setOnRemoveClickListener(Runnable listener) {
        this.onRemove = listener;
    }

    /**
     * Formats a byte count as KB or MB.
     *
     * @param context for string resources
     * @param bytes   the size
     * @return e.g. 2.4 MB
     */
    public static String formatSize(Context context, long bytes) {
        double kb = Math.max(0, bytes) / KB;
        return kb < KB ? context.getString(R.string.file_size_kb, kb) : context.getString(R.string.file_size_mb, kb / KB);
    }
}
