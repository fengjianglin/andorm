
package com.ifatter.andorm.orm;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;

public abstract class DaoSupport<T> {

    private Template<T> mTemplate;

    private ORMSQLiteHelper mDBHelper;

    private ORMSQLiteHelper.DatabaseListener mListener = new ORMSQLiteHelper.DatabaseListener() {
        public void onCreate() {
            DaoSupport.this.onCreate();
        }

        public void onUpgrade(int oldVersion, int newVersion) {
            DaoSupport.this.onUpgrade(oldVersion, newVersion);
        }
    };

    public DaoSupport(Context context) {

        Class<?> claz = getClass();
        if (claz.isAnnotationPresent(Database.class)) {
            Database db = claz.getAnnotation(Database.class);
            Class<? extends DBSupport> c = db.database();
            try {
                Constructor<? extends DBSupport> con = c.getConstructor();
                DBSupport support = con.newInstance();
                mDBHelper = new ORMSQLiteHelper(context, support.getName(),
                        support.getVersion(), support.getClasses());
                mDBHelper.setDabaseListener(mListener);
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
                        System.out.println("--------DaoSupport");
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

    public void onCreate() {

    }

    public void onUpgrade(int oldVersion, int newVersion) {

    }

}
