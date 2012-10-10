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

import com.ifatter.andorm.reflect.Reflactor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;

public abstract class DaoSupport2 {

    private Template2 mTemplate2;

    private ORMSQLiteHelper mDBHelper;

    public DaoSupport2(Context context) {

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

    public Object getDaoTransaction2() {

        InvocationHandler invocationHandler = new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.isAnnotationPresent(Transaction.class)) {
                    System.out.println("--------transation.begin");
                    SQLiteDatabase db = getTemplate2().getOrmsqLiteHelper().getSqLiteDatabase();
                    db.beginTransaction();
                    try {
                        Object ret = method.invoke(DaoSupport2.this, args);
                        db.setTransactionSuccessful();
                        return ret;
                    } finally {
                        db.endTransaction();
                        System.out.println("--------transation.end");
                    }
                } else {
                    return method.invoke(DaoSupport2.this, args);
                }
            }
        };
        Object proxy = Proxy.newProxyInstance(DaoSupport2.this.getClass().getClassLoader(),
                DaoSupport2.this.getClass().getInterfaces(), invocationHandler);
        return proxy;
    }

    @SuppressWarnings("unchecked")
    protected final synchronized <T> Template2 getTemplate2() {
        if (mTemplate2 == null) {
            Class<T> clazz = null;
            ParameterizedType type = (ParameterizedType)getClass().getGenericSuperclass();
            clazz = (Class<T>)(type.getActualTypeArguments()[0]);
            mTemplate2 = new Template2(mDBHelper, clazz);
        }
        return mTemplate2;
    }

    public void close() {
        try {
            mDBHelper.close();
        } catch (Exception e) {
        }
    }

}
