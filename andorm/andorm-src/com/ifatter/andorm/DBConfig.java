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

import com.ifatter.Andorm;

public class DBConfig {

	public static final String DEFAULT_PATH = Andorm.getAppPath() + "/andorm/";

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

	public static DBConfig get() {
		return new DBConfig(DEFAULT_PATH, DEFAULT_NAME);
	}

}
