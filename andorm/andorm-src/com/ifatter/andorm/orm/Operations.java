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
 * 定义原子操作
 * 
 * @author ifatter
 */
interface Operations {

	public boolean isExist(String sql, String[] selectionArgs);

	public Model find(int id);

	public long insert(Model entity);

	public int update(Model entity);

	public int save(Model entity);

	public int delete(Model id);
	
	public int delete(int id);

	public void delete(Integer... ids);

	public int delete(String whereClause, String[] whereArgs);

	public int deleteAll();

	public List<Model> rawQuery(String sql, String[] selectionArgs);

	public List<Model> findAll();

	public List<Model> find(String selection, String[] selectionArgs);

	public List<Model> find(String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit);

	public List<Map<String, String>> query(String sql, String[] selectionArgs);

	public void execSql(String sql, Object[] selectionArgs);

}
