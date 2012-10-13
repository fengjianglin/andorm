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

import android.text.TextUtils;

import com.ifatter.util.LocalResourceBundle;
import com.ifatter.util.ManifestParser;

public class DBConfig {

	public static final String PACKAGE_NAME = new ManifestParser().parser()
			.getPackageName();

	public static final String DEFAULT_PATH = "/data/data/" + PACKAGE_NAME
			+ "/andorm/";

	public static final String DEFAULT_NAME = "andorm_default.db";

	private String path;
	private String name;

	private DBConfig(String path, String name) {
		this.path = path;
		this.name = name;
	}

	public String getPath() {
		return path;
	}

	public String getName() {
		return name;
	}

	/**
	 * cfgPath = "com/ifatter/andorm/database/config";
	 */
	public static DBConfig get(String cfgPath) {
		String path, name;
		LocalResourceBundle bundle;
		if (TextUtils.isEmpty(cfgPath)
				|| (bundle = LocalResourceBundle.getBundle(cfgPath)) == null) {
			path = DEFAULT_PATH;
			name = DEFAULT_NAME;
		} else {
			if (TextUtils.isEmpty((path = bundle.getString("db.path")))) {
				path = DEFAULT_PATH;
			}
			if (TextUtils.isEmpty((name = bundle.getString("db.name")))) {
				name = DEFAULT_NAME;
			}
		}
		return new DBConfig(path, name);
	}

}
