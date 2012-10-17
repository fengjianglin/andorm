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

import com.ifatter.andorm.orm.annotation.Transaction;
import com.ifatter.andorm.reflect.Reflactor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class DaoFactory {

    private static Map<Class<? extends DaoSupport>, Object> map = Collections
            .synchronizedMap(new WeakHashMap<Class<? extends DaoSupport>, Object>());

    /**
     * @param <T>
     * @param daoImplClass dao实现类
     * @return *动态代理实现
     */
    @SuppressWarnings("unchecked")
    public static <T> T createDao(Class<? extends DaoSupport> daoImplClass) {

        Object proxy = map.get(daoImplClass);
        if (proxy != null) {
            return (T)proxy;
        }

        DaoSupport dao = Reflactor.newInstance(daoImplClass);
        if (dao != null) {
            DaoTransInvoHandler handler = new DaoTransInvoHandler((DaoSupport)dao);
            ClassLoader classLoader = dao.getClass().getClassLoader();
            Class<?>[] interfaces = dao.getClass().getInterfaces();
            proxy = Proxy.newProxyInstance(classLoader, interfaces, handler);
            map.put(daoImplClass, proxy);
            return (T)proxy;
        }
        return null;
    }

    private static class DaoTransInvoHandler implements InvocationHandler {

        private DaoSupport inner;

        public DaoTransInvoHandler(DaoSupport inner) {
            this.inner = inner;
        }

        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (method.isAnnotationPresent(Transaction.class)) {
                Object ret = inner.transaction(method, args);
                return ret;
            } else {
                return method.invoke(inner, args);
            }
        }
    };
}
