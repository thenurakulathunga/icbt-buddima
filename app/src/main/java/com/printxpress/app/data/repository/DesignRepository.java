package com.printxpress.app.data.repository;

import android.net.Uri;

import androidx.lifecycle.LiveData;

import com.printxpress.app.core.common.Resource;
import com.printxpress.app.core.storage.DesignFile;
import com.printxpress.app.data.model.Design;

import java.util.List;

/**
 * Artwork uploads and the Saved designs library (FR-1.7, FR-2.6, FR-2.7).
 * <p>Pattern role: Repository over SQLite and app-private files.</p>
 *
 * @since 1.0
 */
public interface DesignRepository {

    /**
     * Lists a user's saved designs.
     *
     * @param userId the owner
     * @return the designs, newest first
     */
    LiveData<Resource<List<Design>>> getDesigns(long userId);

    /**
     * Reads the name, type and size of a picked file.
     *
     * @param uri the picked content URI
     * @return the description
     */
    LiveData<Resource<DesignFile>> describe(Uri uri);

    /**
     * Validates (BR-10), copies the picked file into private storage and saves it as a design.
     *
     * @param userId the owner
     * @param picked the picked file description
     * @param title  the design title; blank uses the file name
     * @return the saved design
     */
    LiveData<Resource<Design>> saveUpload(long userId, DesignFile picked, String title);

    /**
     * Deletes a design and its file unless a cart item still uses it.
     *
     * @param userId the owner
     * @param design the design
     * @return true on success
     */
    LiveData<Resource<Boolean>> delete(long userId, Design design);
}
