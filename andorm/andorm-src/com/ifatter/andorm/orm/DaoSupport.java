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

import com.ifatter.andorm.orm.annotation.Database;
import com.ifatter.andorm.orm.annotation.Model;
import com.ifatter.andorm.reflect.Reflactor;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.lang.reflect.Modifier;

public abstract class DaoSupport {

    private Template mTemplate;

    private ORMSQLiteHelper mDBHelper;

    public DaoSupport(Context context) {

        boolean init = false;

        Class<?> claz = getClass();
        if (claz.isAnnotationPresent(Database.class)) {
            Database db = claz.getAnnotation(Database.class);
            initDBHelper(context, db);
            init = true;
        } else {
            Class<?>[] classes = claz.getInterfaces();
            if (classes != null) {
                for (Class<?> clazz : classes) {
                    if (clazz.isAnnotationPresent(Database.class)) {
                        Database db = clazz.getAnnotation(Database.class);
                        initDBHelper(context, db);
                        init = true;
                        break;
                    }
                }
            }
        }

        if (!init) {
            throw new IllegalArgumentException(this.getClass() + " need Database");
        }
    }

    private void initDBHelper(Context context, Database db) {
        DBConfig support = null;

        Class<? extends DBConfig> c = db.database();
        if (!Modifier.isAbstract(c.getModifiers())) {
            support = Reflactor.newInstance(c);
        } else {
            String cfgPath = db.cfgPath();
            if (!TextUtils.isEmpty(cfgPath)) {
                support = DBConfig.get(cfgPath);
            } else {
                throw new IllegalArgumentException(db.getClass() + " need database or cfgPath");
            }
        }
        String dirPath = context.getFilesDir().getAbsolutePath() + "/database/";
        File dir = new File(dirPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String path = dirPath + support.getName();
        mDBHelper = new ORMSQLiteHelper(path);
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

    public void close() {
        try {
            mDBHelper.close();
        } catch (Exception e) {
        }
    }

}
