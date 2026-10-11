package com.printxpress.app.data.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Creates, seeds and upgrades the PrintXpress SQLite database.
 * <p>
 * Pattern role: Singleton, so the whole app shares one connection pool. Foreign keys are
 * switched on in {@link #onConfigure(SQLiteDatabase)} so cascades and constraints from docs/03
 * are enforced (NFR-7). Schema changes use step-wise migrations so user data is never dropped.
 * </p>
 *
 * @since 1.0
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    /** The lazily created shared instance. */
    private static volatile DatabaseHelper instance;

    /**
     * Creates a helper.
     *
     * @param context any context; the application context is used
     * @param name    the database file name, or null for an in-memory database
     */
    protected DatabaseHelper(Context context, String name) {
        super(context.getApplicationContext(), name, null, DbContract.DATABASE_VERSION);
    }

    /**
     * Returns the shared on-disk helper.
     *
     * @param context any context
     * @return the singleton helper
     */
    public static DatabaseHelper getInstance(Context context) {
        if (instance == null) {
            synchronized (DatabaseHelper.class) {
                if (instance == null) {
                    instance = new DatabaseHelper(context, DbContract.DATABASE_NAME);
                }
            }
        }
        return instance;
    }

    /**
     * Creates a separate in-memory database, used by instrumented tests.
     *
     * @param context any context
     * @return a new, seeded, in-memory helper
     */
    public static DatabaseHelper createInMemory(Context context) {
        return new DatabaseHelper(context, null);
    }

    /**
     * Enables foreign-key enforcement on every connection.
     *
     * @param db the database being configured
     */
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    /**
     * Creates every table and index, then seeds reference data, in one transaction.
     *
     * @param db the new database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            for (String sql : DbContract.CREATE_TABLES) {
                db.execSQL(sql);
            }
            for (String sql : DbContract.CREATE_INDEXES) {
                db.execSQL(sql);
            }
            new DatabaseSeeder(db).seed();
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Runs step-wise migrations, e.g. {@code if (oldVersion < 2) migrateTo2(db)}. Version 1 is
     * the first release, so there are no steps yet.
     *
     * @param db         the database
     * @param oldVersion the installed version
     * @param newVersion the target version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 1) {
            onCreate(db);
        }
    }
}
