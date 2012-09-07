/* 
 * Copyright (C) 2011-2012 ifatter.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ifatter.andorm.orm;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author ifatter
 */
public class DatabaseSession implements Session {

    private final String databasePath;

    private SQLiteDatabase database;

    public DatabaseSession(String databasePath) {
        this.databasePath = databasePath;
    }

    @Override
    public void open() {
        database = SQLiteDatabase.openOrCreateDatabase(databasePath, null);
    }

    @Override
    public boolean isOpen() {
        return database != null && database.isOpen();
    }

    @Override
    public void close() {
        if (database != null && database.isOpen()) {
            database.close();
        }
    }

}
