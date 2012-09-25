
package com.ifatter.andorm.orm;

import com.ifatter.andorm.reflect.Invoker;
import com.ifatter.andorm.reflect.Reflactor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;

public abstract class SQLiteOpenHelper {

    private final Context mContext;

    private final String mSqlitePath;

    private final CursorFactory mFactory;

    private final int mNewVersion;

    private SQLiteDatabase mDatabase = null;

    private boolean mIsInitializing = false;

    private Reflactor mDBReflactor = Reflactor.in(SQLiteDatabase.class);

    public SQLiteOpenHelper(Context context, String name, CursorFactory factory, int version) {
        if (version < 1)
            throw new IllegalArgumentException("Version must be >= 1, was " + version);

        mContext = context;
        mSqlitePath = name;
        mFactory = factory;
        mNewVersion = version;
    }

    public synchronized SQLiteDatabase getWritableDatabase() {
        if (mDatabase != null && mDatabase.isOpen() && !mDatabase.isReadOnly()) {
            return mDatabase; // The database is already open for business
        }

        if (mIsInitializing) {
            throw new IllegalStateException("getWritableDatabase called recursively");
        }

        boolean success = false;
        SQLiteDatabase db = null;
        if (mDatabase != null) {
            new Invoker(mDatabase, mDBReflactor.returnGetMethodOf("lock")).invoke();
            // mDatabase.lock();
        }
        try {
            mIsInitializing = true;
            if (mSqlitePath == null) {
                db = SQLiteDatabase.create(null);
            } else {
                db = mContext.openOrCreateDatabase(mSqlitePath, 0, mFactory);
            }

            int version = db.getVersion();
            if (version != mNewVersion) {
                db.beginTransaction();
                try {
                    if (version == 0) {
                        onCreate(db);
                    } else {
                        onUpgrade(db, version, mNewVersion);
                    }
                    db.setVersion(mNewVersion);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
            }

            onOpen(db);
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
        if (mIsInitializing)
            throw new IllegalStateException("Closed during initialization");

        if (mDatabase != null && mDatabase.isOpen()) {
            mDatabase.close();
            mDatabase = null;
        }
    }

    public abstract void onCreate(SQLiteDatabase db);

    public abstract void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion);

    public void onOpen(SQLiteDatabase db) {
    }
}
