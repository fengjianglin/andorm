
package com.ifatter.andorm.orm;

import android.database.sqlite.SQLiteDatabase;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DaoTransactionFactory {

    /**
     * @param <T>
     * @param daoImplClass dao实现类
     * @param constructorParams dao实现类构造函数所需要的参数
     * @return *动态代理实现，需要向上转型成接口
     */
    public static <T extends DaoSupport<?>> Object createDaoTransaction(Class<T> daoImplClass,
            Object... constructorParams) {
        T instance = null;
        try {
            @SuppressWarnings("unchecked")
            Constructor<T>[] constructors = daoImplClass.getConstructors();
            for (Constructor<T> c : constructors) {
                try {
                    instance = c.newInstance(constructorParams);
                    break;
                } catch (Exception e) {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (instance != null) {
            DaoTransInvoHandler<T> handler = new DaoTransInvoHandler<T>(instance);
            ClassLoader classLoader = instance.getClass().getClassLoader();
            Class<?>[] interfaces = instance.getClass().getInterfaces();
            Object proxy = Proxy.newProxyInstance(classLoader, interfaces, handler);
            return proxy;
        }
        return null;
    }

    private static class DaoTransInvoHandler<T extends DaoSupport<?>> implements InvocationHandler {
        private T inner;

        public DaoTransInvoHandler(T inner) {
            this.inner = inner;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.isAnnotationPresent(Transaction.class)) {
                SQLiteDatabase db = inner.getTemplate().getOrmsqLiteHelper().getSqLiteDatabase();
                db.beginTransaction();
                try {
                    System.out.println("--------DaoTransactionFactory");
                    Object ret = method.invoke(inner, args);
                    db.setTransactionSuccessful();
                    return ret;
                } finally {
                    db.endTransaction();
                }
            } else {
                return method.invoke(inner, args);
            }
        }
    };
}
