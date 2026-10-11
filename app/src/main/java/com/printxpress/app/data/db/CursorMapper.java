package com.printxpress.app.data.db;

/**
 * Converts the current cursor row into a model object.
 *
 * @param <T> the model type
 * @since 1.0
 */
@FunctionalInterface
public interface CursorMapper<T> {

    /**
     * Maps one row.
     *
     * @param row reader positioned on the row
     * @return the model
     */
    T map(CursorReader row);
}
