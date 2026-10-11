package com.printxpress.app.data.repository;

import android.net.Uri;

import androidx.lifecycle.LiveData;

import com.printxpress.app.R;
import com.printxpress.app.core.common.Constants;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.core.concurrency.AppExecutors;
import com.printxpress.app.core.storage.DesignFile;
import com.printxpress.app.core.storage.FileStorageManager;
import com.printxpress.app.core.validation.ValidationRule;
import com.printxpress.app.core.validation.rules.FileSizeRule;
import com.printxpress.app.core.validation.rules.FileTypeRule;
import com.printxpress.app.data.dao.CartDao;
import com.printxpress.app.data.dao.DesignDao;
import com.printxpress.app.data.model.CartItem;
import com.printxpress.app.data.model.Design;

import java.io.IOException;
import java.util.List;

/**
 * {@link DesignRepository} backed by SQLite and {@link FileStorageManager}.
 *
 * @since 1.0
 */
public class DesignRepositoryImpl extends BaseRepository implements DesignRepository {

    /** Longest stored title. */
    private static final int MAX_TITLE = 60;

    /** Design access. */
    private final DesignDao designDao;
    /** Cart access, to protect designs still in use. */
    private final CartDao cartDao;
    /** File storage. */
    private final FileStorageManager files;

    /**
     * Creates the repository.
     *
     * @param executors background executors
     * @param designDao design DAO
     * @param cartDao   cart DAO
     * @param files     file storage
     */
    public DesignRepositoryImpl(AppExecutors executors, DesignDao designDao, CartDao cartDao, FileStorageManager files) {
        super(executors);
        this.designDao = designDao;
        this.cartDao = cartDao;
        this.files = files;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<List<Design>>> getDesigns(long userId) {
        return execute(() -> designDao.findByUser(userId));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<DesignFile>> describe(Uri uri) {
        return execute(() -> {
            DesignFile file = files.describe(uri);
            validate(file);
            return file;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<Design>> saveUpload(long userId, DesignFile picked, String title) {
        return execute(() -> {
            validate(picked);
            DesignFile copy;
            try {
                copy = files.copyToDesigns(picked);
            } catch (IOException e) {
                throw new RepositoryException(R.string.err_file_read);
            }
            Design design = new Design();
            design.setUserId(userId);
            String name = ValidationRule.isBlank(title) ? picked.getDisplayName() : title.trim();
            design.setTitle(name.length() > MAX_TITLE ? name.substring(0, MAX_TITLE) : name);
            design.setFilePath(copy.getLocation());
            design.setMimeType(copy.getMimeType());
            design.setFileSizeBytes(copy.getSizeBytes());
            designDao.insert(design);
            return design;
        });
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<Boolean>> delete(long userId, Design design) {
        return execute(() -> {
            for (CartItem item : cartDao.findByUser(userId)) {
                require(item.getDesignId() == null || item.getDesignId() != design.getDesignId(),
                        R.string.err_design_in_use);
            }
            boolean deleted = designDao.delete(userId, design.getDesignId());
            if (deleted) {
                files.delete(design.getFilePath());
            }
            return deleted;
        });
    }

    /**
     * Applies BR-10 to a picked file.
     *
     * @param file the file description
     */
    private static void validate(DesignFile file) {
        require(new FileTypeRule(Constants.ACCEPTED_MIME_TYPES, 0).validate(file.getMimeType()).isValid()
                && !ValidationRule.isBlank(file.getMimeType()), R.string.err_file_type);
        require(new FileSizeRule(Constants.MAX_UPLOAD_BYTES, 0).validate(String.valueOf(file.getSizeBytes())).isValid(),
                R.string.err_file_size);
    }
}
