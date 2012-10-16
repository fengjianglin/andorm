
package com.ifatter.andorm.orm;

import android.database.sqlite.SQLiteDatabase;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class DatabaseCache {

    private static final Map<String, SQLiteDatabase> cache = Collections
            .synchronizedMap(new WeakHashMap<String, SQLiteDatabase>());

    private final String mSqlitePath;

    public DatabaseCache(String sqlitePath) {
        if (sqlitePath == null) {
            throw new IllegalArgumentException("SqlitePath can't be null");
        }
        mSqlitePath = sqlitePath;
    }

    public final synchronized SQLiteDatabase openDatabase() {

        SQLiteDatabase db = cache.get(mSqlitePath);
        if (db != null) {
            if (db.isOpen()) {
                return db;
            }
            cache.remove(mSqlitePath);
        }
        boolean success = false;
        try {
            db = SQLiteDatabase.openDatabase(mSqlitePath, null, SQLiteDatabase.CREATE_IF_NECESSARY
                    | SQLiteDatabase.OPEN_READWRITE);
            cache.put(mSqlitePath, db);
            success = true;
            return db;
        } finally {
            if (!success && db != null) {
                db.close();
            }
        }
    }

    public synchronized void closeDatabase() {
        SQLiteDatabase db = cache.get(mSqlitePath);
        if (db != null && db.isOpen()) {
            db.close();
        }
        cache.remove(mSqlitePath);
    }

    public static synchronized void clear() {
        for (SQLiteDatabase db : cache.values()) {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        cache.clear();
    }

}
