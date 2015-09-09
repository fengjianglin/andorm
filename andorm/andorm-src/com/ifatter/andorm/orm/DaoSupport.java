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
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class DaoSupport {

	private static final Map<Class<? extends Model>, Template> templates = Collections
			.synchronizedMap(new WeakHashMap<Class<? extends Model>, Template>());

	private Template mTemplate;

	Object transaction(Method method, Object... args) throws Throwable {
		return getTemplate().transaction(this, method, args);
	}

	protected final synchronized Template getTemplate() {
		if (mTemplate == null) {
			DatabaseCache mDBCache = getDatabaseCache();
			Class<? extends Model> clazz = null; //= getClass();
			mTemplate = templates.get(clazz);
			if (mTemplate == null) {
				mTemplate = new Template(mDBCache.openDatabase(), clazz);
				templates.put(clazz, mTemplate);
			}

			if (mTemplate == null) {
				throw new RuntimeException(getClass() + " need extends Model");
			}
		}
		return mTemplate;
	}

	protected final synchronized static Template getTemplate(
			Class<? extends Model> clazz) {
		Template template = templates.get(clazz);
		if (template == null) {
			DatabaseCache mDBCache = getDatabaseCache();
			template = new Template(mDBCache.openDatabase(), clazz);
			templates.put(clazz, template);
		}
		return template;
	}

	private static DatabaseCache getDatabaseCache() {
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
		return new DatabaseCache(path);
	}
}
