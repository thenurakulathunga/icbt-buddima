package com.printxpress.app.data.repository;

import androidx.lifecycle.LiveData;

import com.printxpress.app.core.common.Resource;
import com.printxpress.app.core.concurrency.AppExecutors;
import com.printxpress.app.data.dao.GalleryDao;
import com.printxpress.app.data.model.GalleryItem;

import java.util.List;

/**
 * SQLite-backed {@link GalleryRepository}.
 *
 * @since 1.0
 */
public class GalleryRepositoryImpl extends BaseRepository implements GalleryRepository {

    /** Gallery access. */
    private final GalleryDao galleryDao;

    /**
     * Creates the repository.
     *
     * @param executors  background executors
     * @param galleryDao gallery DAO
     */
    public GalleryRepositoryImpl(AppExecutors executors, GalleryDao galleryDao) {
        super(executors);
        this.galleryDao = galleryDao;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LiveData<Resource<List<GalleryItem>>> getGallery(long categoryId) {
        return execute(() -> galleryDao.findByCategory(categoryId));
    }
}
