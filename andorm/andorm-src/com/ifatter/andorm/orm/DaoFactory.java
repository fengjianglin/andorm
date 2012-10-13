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

import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class DaoFactory {

	/**
	 * @param <T>
	 * @param daoImplClass
	 *            dao实现类
	 * @param constructorParams
	 *            dao实现类构造函数所需要的参数
	 * @return *动态代理实现，需要向上转型成接口
	 */
	@SuppressWarnings("unchecked")
	public static <T> T createDao(Class<? extends DaoSupport> daoImplClass,
			Object... constructorParams) {
		DaoSupport dao = null;
		try {
			Constructor<DaoSupport>[] cons = daoImplClass.getConstructors();
			for (Constructor<DaoSupport> c : cons) {
				try {
					dao = c.newInstance(constructorParams);
					break;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (dao != null) {
			DaoTransInvoHandler handler = new DaoTransInvoHandler(
					(DaoSupport) dao);
			ClassLoader classLoader = dao.getClass().getClassLoader();
			Class<?>[] interfaces = dao.getClass().getInterfaces();
			Object proxy = Proxy.newProxyInstance(classLoader, interfaces,
					handler);
			return (T) proxy;
		}
		return null;
	}

	private static class DaoTransInvoHandler implements InvocationHandler {

		private DaoSupport inner;

		public DaoTransInvoHandler(DaoSupport inner) {
			this.inner = inner;
		}

		public Object invoke(Object proxy, Method method, Object[] args)
				throws Throwable {
			if (method.isAnnotationPresent(Transaction.class)) {
				System.out.println("--------transation.begin");
				SQLiteDatabase db = inner.getTemplate().getOrmsqLiteHelper()
						.getSqLiteDatabase();
				db.beginTransaction();
				try {
					Object ret = method.invoke(inner, args);
					db.setTransactionSuccessful();
					return ret;
				} finally {
					db.endTransaction();
					System.out.println("--------transation.end");
				}
			} else {
				return method.invoke(inner, args);
			}
		}
	};
}
