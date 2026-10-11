package com.printxpress.app.data.model;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

/**
 * Immutable search criteria for the product list (FR-2.3): category, name query, option values
 * and sort order. Option values are grouped by option type; a product matches when, for every
 * type with a selection, it offers at least one selected value.
 *
 * @since 1.0
 */
public final class ProductFilter {

    /** Sort orders offered in the sort sheet. */
    public enum Sort {
        /** Catalogue order. */
        POPULAR,
        /** Cheapest unit price first. */
        PRICE_LOW_HIGH,
        /** Most expensive unit price first. */
        PRICE_HIGH_LOW,
        /** Alphabetical by name. */
        NAME
    }

    /** Category id, or 0 for every category. */
    private final long categoryId;
    /** Case-insensitive name fragment, may be empty. */
    private final String query;
    /** Option type name to accepted values. */
    private final Map<String, Set<String>> optionValues;
    /** Sort order. */
    private final Sort sort;

    /**
     * Creates a filter.
     *
     * @param categoryId   category id or 0
     * @param query        name fragment, may be null
     * @param optionValues option type name to accepted values, may be null
     * @param sort         sort order, null means popular
     */
    public ProductFilter(long categoryId, String query, Map<String, Set<String>> optionValues, Sort sort) {
        this.categoryId = categoryId;
        this.query = query == null ? "" : query.trim();
        Map<String, Set<String>> copy = new LinkedHashMap<>();
        if (optionValues != null) {
            for (Map.Entry<String, Set<String>> e : optionValues.entrySet()) {
                if (e.getValue() != null && !e.getValue().isEmpty()) {
                    copy.put(e.getKey(), Collections.unmodifiableSet(new LinkedHashSet<>(e.getValue())));
                }
            }
        }
        this.optionValues = Collections.unmodifiableMap(copy);
        this.sort = sort == null ? Sort.POPULAR : sort;
    }

    /**
     * Creates an unfiltered filter for one category.
     *
     * @param categoryId category id or 0
     * @return the filter
     */
    public static ProductFilter forCategory(long categoryId) {
        return new ProductFilter(categoryId, null, null, Sort.POPULAR);
    }

    /**
     * Returns a copy with another query.
     *
     * @param newQuery the query
     * @return the new filter
     */
    public ProductFilter withQuery(String newQuery) {
        return new ProductFilter(categoryId, newQuery, optionValues, sort);
    }

    /**
     * Returns a copy with other option selections.
     *
     * @param newValues option type name to values
     * @return the new filter
     */
    public ProductFilter withOptions(Map<String, Set<String>> newValues) {
        return new ProductFilter(categoryId, query, newValues, sort);
    }

    /**
     * Returns a copy with another sort order.
     *
     * @param newSort the sort order
     * @return the new filter
     */
    public ProductFilter withSort(Sort newSort) {
        return new ProductFilter(categoryId, query, optionValues, newSort);
    }

    /** @return the category id, or 0 for all */
    public long getCategoryId() {
        return categoryId;
    }

    /** @return the trimmed name query */
    public String getQuery() {
        return query;
    }

    /** @return option type name to selected values (unmodifiable) */
    public Map<String, Set<String>> getOptionValues() {
        return optionValues;
    }

    /** @return the sort order */
    public Sort getSort() {
        return sort;
    }

    /** @return the number of selected option values, for the filter badge */
    public int getActiveOptionCount() {
        int count = 0;
        for (Set<String> values : optionValues.values()) {
            count += values.size();
        }
        return count;
    }
}
