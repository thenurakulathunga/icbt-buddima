package com.printxpress.app.ui.support;

import android.graphics.Bitmap;
import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.R;
import com.printxpress.app.core.base.BaseViewModel;
import com.printxpress.app.core.common.Event;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.core.concurrency.AppExecutors;
import com.printxpress.app.core.storage.FileStorageManager;
import com.printxpress.app.data.model.Guideline;
import com.printxpress.app.data.repository.SupportRepository;

import java.io.IOException;
import java.util.List;

/**
 * Print guidelines state (S25–S26, FR-5.1, FR-5.5): the guideline list, one guideline, and export
 * of the bleed template image for sharing.
 *
 * @since 1.0
 */
public class GuidelinesViewModel extends BaseViewModel {

    /** File name of the shared template image. */
    private static final String TEMPLATE_FILE = "printxpress_bleed_template.png";

    /** Support content. */
    private final SupportRepository support;
    /** Share-cache writer. */
    private final FileStorageManager files;
    /** Background threads. */
    private final AppExecutors executors;
    /** All guidelines. */
    private final MutableLiveData<Resource<List<Guideline>>> list = new MutableLiveData<>();
    /** One guideline. */
    private final MutableLiveData<Resource<Guideline>> guideline = new MutableLiveData<>();
    /** One-shot URI of the exported template. */
    private final MutableLiveData<Event<Uri>> shareUri = new MutableLiveData<>();

    /**
     * Creates the ViewModel.
     *
     * @param support   support repository
     * @param files     file storage
     * @param executors app executors
     */
    public GuidelinesViewModel(SupportRepository support, FileStorageManager files, AppExecutors executors) {
        this.support = support;
        this.files = files;
        this.executors = executors;
    }

    /** Loads every guideline. */
    public void loadAll() {
        load(support.getGuidelines(), list);
    }

    /**
     * Loads one guideline.
     *
     * @param guidelineId the guideline
     */
    public void loadOne(long guidelineId) {
        load(support.getGuideline(guidelineId), guideline);
    }

    /**
     * Writes the template image off the main thread and emits its URI.
     *
     * @param image the rendered template
     */
    public void exportTemplate(Bitmap image) {
        executors.diskIO().execute(() -> {
            try {
                shareUri.postValue(new Event<>(files.writeShareImage(image, TEMPLATE_FILE)));
            } catch (IOException | IllegalArgumentException e) {
                message.postValue(new Event<>(R.string.err_generic));
            }
        });
    }

    /** @return all guidelines */
    public LiveData<Resource<List<Guideline>>> getGuidelines() {
        return list;
    }

    /** @return the selected guideline */
    public LiveData<Resource<Guideline>> getGuideline() {
        return guideline;
    }

    /** @return fires once with the URI to share */
    public LiveData<Event<Uri>> getShareUri() {
        return shareUri;
    }
}
