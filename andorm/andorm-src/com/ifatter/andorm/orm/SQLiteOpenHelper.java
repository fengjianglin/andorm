
package com.ifatter.andorm.orm;

import com.ifatter.andorm.reflect.Invoker;
import com.ifatter.andorm.reflect.Reflactor;

import android.database.sqlite.SQLiteDatabase;

/**
 * @see android.database.sqlite.SQLiteOpenHelper
 */
public abstract class SQLiteOpenHelper {

    private final String mSqlitePath;

    private SQLiteDatabase mDatabase = null;

    private boolean mIsInitializing = false;

    private Reflactor mDBReflactor = Reflactor.in(SQLiteDatabase.class);

    public SQLiteOpenHelper(String sqlitePath) {
        if (sqlitePath == null) {
            throw new IllegalArgumentException("SqlitePath can't be null");
        }
        mSqlitePath = sqlitePath;
    }

    public final synchronized SQLiteDatabase getDatabase() {
        if (mDatabase != null && mDatabase.isOpen() && !mDatabase.isReadOnly()) {
            return mDatabase; // The database is already open for business
        }

        if (mIsInitializing) {
            throw new IllegalStateException("getSqliteDatabase called recursively");
        }

        boolean success = false;
        SQLiteDatabase db = null;
        if (mDatabase != null) {
            new Invoker(mDatabase, mDBReflactor.returnGetMethodOf("lock")).invoke();
            // mDatabase.lock();
        }
        try {
            mIsInitializing = true;
            db = SQLiteDatabase.openOrCreateDatabase(mSqlitePath, null);
            success = true;
            return db;
        } finally {
            mIsInitializing = false;
            if (success) {
                if (mDatabase != null) {
                    try {
                        mDatabase.close();
                    } catch (Exception e) {
                    }
                    new Invoker(mDatabase, mDBReflactor.returnGetMethodOf("unlock")).invoke();
                    // mDatabase.unlock();
                }
                mDatabase = db;
            } else {
                if (mDatabase != null) {
                    new Invoker(mDatabase, mDBReflactor.returnGetMethodOf("unlock")).invoke();
                    // mDatabase.unlock();
                }
                if (db != null) {
                    db.close();
                }
            }
        }
    }

    public synchronized void close() {
        if (mIsInitializing) {
            throw new IllegalStateException("Closed during initialization");
        }

        if (mDatabase != null && mDatabase.isOpen()) {
            mDatabase.close();
            mDatabase = null;
        }
    }

}
