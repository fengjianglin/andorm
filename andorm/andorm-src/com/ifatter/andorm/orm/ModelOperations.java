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

import java.util.List;
import java.util.Map;

/**
 * 定义Model原子操作
 * 
 * @author ifatter
 */
public class ModelOperations {

	public static Model find(int id) {
		return null;

	}

	public static void delete(Integer... ids) {

	}

	public static int delete(String whereClause, String[] whereArgs) {
		return 0;

	}

	public static int deleteAll() {
		return 0;

	}

	public static List<Model> rawQuery(String sql, String[] selectionArgs) {
		return null;

	}

	public static List<Model> findAll() {
		return null;

	}

	public static List<Model> find(String selection, String[] selectionArgs) {
		return null;

	}

	public static List<Model> find(String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit) {
		return null;

	}

	public static List<Map<String, String>> query(String sql,
			String[] selectionArgs) {
		return null;

	}

	public static void execSql(String sql, Object[] selectionArgs) {

	}

}
