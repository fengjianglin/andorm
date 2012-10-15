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

import java.io.File;
import java.lang.reflect.Method;
import com.ifatter.andorm.orm.annotation.Database;
import com.ifatter.andorm.orm.annotation.Model;
import android.database.sqlite.SQLiteDatabase;

public abstract class DaoSupport {

    private Template mTemplate;

    private DatabaseManager mDBHelper;

    public DaoSupport() {

        boolean init = false;
        Class<?>[] classes = getClass().getInterfaces();
        if (classes != null) {
            for (Class<?> clazz : classes) {
                if (clazz.isAnnotationPresent(Database.class)) {
                    Database db = clazz.getAnnotation(Database.class);
                    initDBHelper(db);
                    init = true;
                    break;
                }
            }
        }
        if (!init) {
            throw new IllegalArgumentException(this.getClass() + " need @Database");
        }
    }

    private void initDBHelper(Database db) {
        String cfgPath = db.dbCfgPath();
        DBConfig support = DBConfig.get(cfgPath);
        String dirPath = support.getPath();
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String fileName = support.getName();
        String path = dir.getAbsolutePath() + '/' + fileName;
        mDBHelper = new DatabaseManager(path);
    }

    Object transaction(Method method, Object... args) throws Throwable {
        SQLiteDatabase db = mDBHelper.getSqLiteDatabase();
        try {
            db.beginTransaction();
            Object ret = method.invoke(this, args);
            db.setTransactionSuccessful();
            return ret;
        } finally {
            db.endTransaction();
        }
    }

    protected final synchronized Template getTemplate() {
        if (mTemplate == null) {
            boolean b = false;
            Class<?>[] interfaces = getClass().getInterfaces();
            if (interfaces != null) {
                for (Class<?> clazz : interfaces) {
                    if (clazz.isAnnotationPresent(Model.class)) {
                        Model model = clazz.getAnnotation(Model.class);
                        Class<?> cla = (Class<?>)model.model();
                        mTemplate = new Template(mDBHelper, cla);
                        b = true;
                        break;
                    }
                }
            }
            if (!b) {
                throw new IllegalArgumentException(getClass() + " need @Model");
            }
        }
        return mTemplate;
    }
}
