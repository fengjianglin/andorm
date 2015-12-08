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

package com.ifatter.andorm.query;

import java.io.File;

import com.ifatter.andorm.AndormException;
import com.ifatter.andorm.DBConfig;
import com.ifatter.andorm.DatabaseCache;
import com.ifatter.andorm.Model;

public abstract class Query {

	protected final synchronized static <T extends Model> OperationsImpl<T> operate(
			Class<T> clazz) {
		DBConfig config = DBConfig.get();
		String dirPath = config.getPath();
		File dir = new File(dirPath);
		if (!dir.exists()) {
			boolean b = dir.mkdirs();
			if (!b) {
				throw new AndormException(dirPath + " can't be created");
			}
		}
		String fileName = config.getName();
		String path = dir.getAbsolutePath() + '/' + fileName;
		DatabaseCache mDBCache = new DatabaseCache(path);
		OperationsImpl<T> operations = new OperationsImpl<T>(
				mDBCache.openDatabase(), clazz);
		return operations;
	}

}
