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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;

public abstract class DaoSupport<T> {

    private Template<T> mTemplate;

    private ORMSQLiteHelper mDBHelper;

    public DaoSupport(Context context) {

        Class<?> claz = getClass();
        if (claz.isAnnotationPresent(Database.class)) {
            Database db = claz.getAnnotation(Database.class);
            Class<? extends Config> c = db.database();
            try {
                Constructor<? extends Config> con = c.getConstructor();
                Config support = con.newInstance();
                String dirPath = context.getFilesDir().getAbsolutePath() + "/database/";
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String path = dirPath + support.getName();
                mDBHelper = new ORMSQLiteHelper(path, support.getClasses());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * @return 动态代理实现，需要向上转型成接口
     */
    public Object getDaoTransaction() {
        InvocationHandler invocationHandler = new InvocationHandler() {
            public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                if (method.isAnnotationPresent(Transaction.class)) {
                    SQLiteDatabase db = DaoSupport.this.getTemplate().getOrmsqLiteHelper()
                            .getSqLiteDatabase();
                    db.beginTransaction();
                    try {
                        Object ret = method.invoke(DaoSupport.this, args);
                        db.setTransactionSuccessful();
                        return ret;
                    } finally {
                        db.endTransaction();
                    }
                } else {
                    return method.invoke(DaoSupport.this, args);
                }
            }
        };
        Object proxy = Proxy.newProxyInstance(DaoSupport.this.getClass().getClassLoader(),
                DaoSupport.this.getClass().getInterfaces(), invocationHandler);
        return proxy;
    }

    @SuppressWarnings("unchecked")
    protected final synchronized Template<T> getTemplate() {
        if (mTemplate == null) {
            Class<T> clazz = null;
            ParameterizedType type = (ParameterizedType)getClass().getGenericSuperclass();
            clazz = (Class<T>)(type.getActualTypeArguments()[0]);
            mTemplate = new Template<T>(mDBHelper, clazz);
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
