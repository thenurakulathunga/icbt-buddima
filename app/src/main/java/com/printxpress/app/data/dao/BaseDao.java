package com.printxpress.app.data.dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.printxpress.app.data.db.CursorMapper;
import com.printxpress.app.data.db.CursorReader;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Generic base for table-level data access.
 * <p>
 * Pattern role: DAO + Template Method. Subclasses provide the table name, id column, row mapper
 * and value conversion; this class supplies CRUD, parameterised query helpers and transactions.
 * Every query binds arguments through {@code ?} placeholders and closes its cursor with
 * try-with-resources. Methods block and must be called from {@code AppExecutors.diskIO()}.
 * </p>
 *
 * @param <T> the entity type
 * @since 1.0
 */
public abstract class BaseDao<T> {

    /** Shared database helper. */
    protected final SQLiteOpenHelper helper;

    /**
     * Creates the DAO.
     *
     * @param helper the database helper
     */
    protected BaseDao(SQLiteOpenHelper helper) {
        this.helper = helper;
    }

    /** @return the table this DAO owns */
    protected abstract String table();

    /** @return the primary key column */
    protected abstract String idColumn();

    /** @return the row mapper for this table */
    protected abstract CursorMapper<T> mapper();

    /**
     * Converts an entity into column values, without the primary key.
     *
     * @param entity the entity
     * @return the values to write
     */
    protected abstract ContentValues toValues(T entity);

    /**
     * Returns the entity's primary key.
     *
     * @param entity the entity
     * @return the id
     */
    protected abstract long getId(T entity);

    /**
     * Stores a generated primary key on the entity.
     *
     * @param entity the entity
     * @param id     the new id
     */
    protected abstract void setId(T entity, long id);

    /** @return a writable database */
    protected SQLiteDatabase writable() {
        return helper.getWritableDatabase();
    }

    /** @return a readable database */
    protected SQLiteDatabase readable() {
        return helper.getReadableDatabase();
    }

    /**
     * Inserts an entity and stores the generated id on it.
     *
     * @param entity the entity
     * @return the new row id
     * @throws android.database.SQLException when a constraint fails
     */
    public long insert(T entity) {
        long id = writable().insertOrThrow(table(), null, toValues(entity));
        setId(entity, id);
        return id;
    }

    /**
     * Updates an entity by primary key.
     *
     * @param entity the entity
     * @return true when a row changed
     */
    public boolean update(T entity) {
        return writable().update(table(), toValues(entity), idColumn() + " = ?",
                new String[]{String.valueOf(getId(entity))}) > 0;
    }

    /**
     * Deletes a row by primary key.
     *
     * @param id the id
     * @return true when a row was deleted
     */
    public boolean deleteById(long id) {
        return writable().delete(table(), idColumn() + " = ?", new String[]{String.valueOf(id)}) > 0;
    }

    /**
     * Finds a row by primary key.
     *
     * @param id the id
     * @return the entity, or null
     */
    public T findById(long id) {
        return querySingle("SELECT * FROM " + table() + " WHERE " + idColumn() + " = ?", String.valueOf(id));
    }

    /**
     * Lists every row.
     *
     * @param orderBy an ORDER BY expression built from contract constants, or null
     * @return the entities
     */
    public List<T> findAll(String orderBy) {
        String sql = "SELECT * FROM " + table() + (orderBy == null ? "" : " ORDER BY " + orderBy);
        return queryList(sql);
    }

    /**
     * Runs a query and maps every row with this DAO's mapper.
     *
     * @param sql  the SQL with {@code ?} placeholders
     * @param args the bound arguments
     * @return the entities
     */
    protected List<T> queryList(String sql, String... args) {
        return queryList(mapper(), sql, args);
    }

    /**
     * Runs a query and maps every row with a given mapper.
     *
     * @param rowMapper the mapper
     * @param sql       the SQL with {@code ?} placeholders
     * @param args      the bound arguments
     * @param <R>       the row type
     * @return the mapped rows
     */
    protected <R> List<R> queryList(CursorMapper<R> rowMapper, String sql, String... args) {
        List<R> result = new ArrayList<>();
        try (Cursor cursor = readable().rawQuery(sql, args)) {
            CursorReader reader = new CursorReader(cursor);
            while (cursor.moveToNext()) {
                result.add(rowMapper.map(reader));
            }
        }
        return result;
    }

    /**
     * Runs a query and maps the first row.
     *
     * @param sql  the SQL with {@code ?} placeholders
     * @param args the bound arguments
     * @return the entity, or null when there is no row
     */
    protected T querySingle(String sql, String... args) {
        try (Cursor cursor = readable().rawQuery(sql, args)) {
            return cursor.moveToFirst() ? mapper().map(new CursorReader(cursor)) : null;
        }
    }

    /**
     * Runs a query returning one number, e.g. COUNT(*).
     *
     * @param sql  the SQL with {@code ?} placeholders
     * @param args the bound arguments
     * @return the number, or 0 when there is no row
     */
    protected long queryLong(String sql, String... args) {
        return DatabaseUtils.longForQuery(readable(), sql, args);
    }

    /**
     * Runs work in a transaction that rolls back if it throws.
     *
     * @param work the work
     * @param <R>  the result type
     * @return the work's result
     * @throws RuntimeException wrapping any checked exception from the work
     */
    public <R> R inTransaction(Callable<R> work) {
        SQLiteDatabase db = writable();
        db.beginTransaction();
        try {
            R result = work.call();
            db.setTransactionSuccessful();
            return result;
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalStateException(e);
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Runs work without a result in a transaction.
     *
     * @param work the work
     */
    public void runInTransaction(Runnable work) {
        inTransaction(() -> {
            work.run();
            return null;
        });
    }

    /**
     * Builds a {@code ?,?,?} placeholder list.
     *
     * @param count the number of placeholders
     * @return the placeholder text
     */
    protected static String placeholders(int count) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < count; i++) {
            sb.append(i == 0 ? "?" : ",?");
        }
        return sb.toString();
    }

    /**
     * Puts a nullable long into content values.
     *
     * @param values the values
     * @param key    the column
     * @param value  the value, may be null
     */
    protected static void putNullable(ContentValues values, String key, Long value) {
        if (value == null) {
            values.putNull(key);
        } else {
            values.put(key, value);
        }
    }
}
