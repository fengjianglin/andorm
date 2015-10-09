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
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

public class Dao<T> {

	private static final Map<Class<? extends Model>, Template> templates = Collections
			.synchronizedMap(new WeakHashMap<Class<? extends Model>, Template>());

	public Dao() {
	}
	
	protected final synchronized static Template getTemplate(
			Class<? extends Model> clazz) {
		Template template = templates.get(clazz);
		if (template == null) {

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
			template = new Template(mDBCache.openDatabase(), clazz);
			templates.put(clazz, template);

		}
		return template;
	}

}
