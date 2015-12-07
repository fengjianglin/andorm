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

package com.ifatter.andorm;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class Delete extends Query {

	private static final Map<Class<?>, Operations<?>> operationsMap = Collections
			.synchronizedMap(new WeakHashMap<Class<?>, Operations<?>>());

	public final synchronized static <T extends Model> Operations<T> from(
			Class<T> clazz) {
		@SuppressWarnings("unchecked")
		Operations<T> op = (Operations<T>) operationsMap.get(clazz);
		if (op == null) {
			OperationsImpl<T> operations = operate(clazz);
			op = new Operations<T>(operations);
			operationsMap.put(clazz, op);
		}
		return op;
	}

	public static class Operations<K extends Model> {

		private final OperationsImpl<K> op;

		protected Operations(OperationsImpl<K> op) {
			if (op == null) {
				throw new AndormException("OperationsImpl can't be null");
			}
			this.op = op;
		}

		public int delete(Model entity) {
			return op.delete(entity);
		}
	}

}
