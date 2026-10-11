package com.printxpress.app.ui.support;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.printxpress.app.core.base.BaseViewModel;
import com.printxpress.app.core.common.Resource;
import com.printxpress.app.data.model.Faq;
import com.printxpress.app.data.repository.SupportRepository;

import java.util.List;

/**
 * FAQ state (S27, FR-5.2): the questions matching the current search text.
 *
 * @since 1.0
 */
public class FaqsViewModel extends BaseViewModel {

    /** Support content. */
    private final SupportRepository support;
    /** Matching questions. */
    private final MutableLiveData<Resource<List<Faq>>> faqs = new MutableLiveData<>();
    /** The current search text. */
    private String query = "";

    /**
     * Creates the ViewModel.
     *
     * @param support support repository
     */
    public FaqsViewModel(SupportRepository support) {
        this.support = support;
    }

    /**
     * Searches questions and answers; an empty text lists everything.
     *
     * @param text the search text
     */
    public void search(String text) {
        query = text == null ? "" : text.trim();
        load(support.searchFaqs(query), faqs);
    }

    /** Re-runs the current search. */
    public void refresh() {
        search(query);
    }

    /** @return the matching questions */
    public LiveData<Resource<List<Faq>>> getFaqs() {
        return faqs;
    }
}
