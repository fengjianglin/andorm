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

import java.io.File;
import java.lang.reflect.Method;

public class DaoSupport {

	private Template mTemplate;

	Object transaction(Method method, Object... args) throws Throwable {
		return getTemplate().transaction(this, method, args);
	}

	protected final synchronized Template getTemplate() {
		if (mTemplate == null) {

			DBConfig support = DBConfig.get();
			String dirPath = support.getPath();
			File dir = new File(dirPath);
			if (!dir.exists()) {
				boolean b = dir.mkdirs();
				if (!b) {
					throw new AndormException(dirPath + " can't be created");
				}
			}
			String fileName = support.getName();
			String path = dir.getAbsolutePath() + '/' + fileName;
			DatabaseCache mDBCache = new DatabaseCache(path);

			@SuppressWarnings("unchecked")
			Class<Model> clazz = (Class<Model>) getClass();
			mTemplate = new Template(mDBCache.openDatabase(), clazz);
			if (mTemplate == null) {
				throw new RuntimeException(getClass() + " need extends Model");
			}

		}
		return mTemplate;
	}
}
