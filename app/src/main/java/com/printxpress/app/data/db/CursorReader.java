package com.printxpress.app.data.db;

import android.database.Cursor;

import com.printxpress.app.core.common.Money;
import com.printxpress.app.core.util.DateTimeUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Null-safe, column-name based reads from a {@link Cursor}.
 * <p>
 * Missing columns return defaults instead of throwing, so one mapper can read both plain and
 * joined queries.
 * </p>
 *
 * @since 1.0
 */
public final class CursorReader {

    /** The wrapped cursor. */
    private final Cursor cursor;

    /**
     * Wraps a cursor positioned on a row.
     *
     * @param cursor the cursor
     */
    public CursorReader(Cursor cursor) {
        this.cursor = cursor;
    }

    /**
     * Resolves a column index.
     *
     * @param column the column name
     * @return the index, or -1 when absent or null
     */
    private int index(String column) {
        int i = cursor.getColumnIndex(column);
        return i >= 0 && !cursor.isNull(i) ? i : -1;
    }

    /**
     * Reads a long.
     *
     * @param column the column name
     * @return the value, or 0
     */
    public long getLong(String column) {
        int i = index(column);
        return i < 0 ? 0 : cursor.getLong(i);
    }

    /**
     * Reads a nullable long, used for optional foreign keys.
     *
     * @param column the column name
     * @return the value, or null
     */
    public Long getNullableLong(String column) {
        int i = index(column);
        return i < 0 ? null : cursor.getLong(i);
    }

    /**
     * Reads an int.
     *
     * @param column the column name
     * @return the value, or 0
     */
    public int getInt(String column) {
        int i = index(column);
        return i < 0 ? 0 : cursor.getInt(i);
    }

    /**
     * Reads a double.
     *
     * @param column the column name
     * @return the value, or 0
     */
    public double getDouble(String column) {
        int i = index(column);
        return i < 0 ? 0 : cursor.getDouble(i);
    }

    /**
     * Reads a 0/1 flag.
     *
     * @param column the column name
     * @return true when the value is non-zero
     */
    public boolean getBoolean(String column) {
        return getInt(column) != 0;
    }

    /**
     * Reads text.
     *
     * @param column the column name
     * @return the value, or null
     */
    public String getString(String column) {
        int i = index(column);
        return i < 0 ? null : cursor.getString(i);
    }

    /**
     * Reads a REAL column as money.
     *
     * @param column the column name
     * @return the amount, or zero
     */
    public Money getMoney(String column) {
        return Money.of(getDouble(column));
    }

    /**
     * Reads an ISO date.
     *
     * @param column the column name
     * @return the date, or null
     */
    public LocalDate getDate(String column) {
        return DateTimeUtils.parseDate(getString(column));
    }

    /**
     * Reads a timestamp.
     *
     * @param column the column name
     * @return the timestamp, or null
     */
    public LocalDateTime getDateTime(String column) {
        return DateTimeUtils.parseDateTime(getString(column));
    }
}
