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

import java.util.ResourceBundle;

public abstract class DBConfig {

    public static final String DEFAULT_NAME = "default_database.db";

    /**
     * cfgPath = "com/ifatter/andorm/database/config";
     */
    public static DBConfig get(String cfgPath) {
        final ResourceBundle bundle = ResourceBundle.getBundle(cfgPath);
        return new DBConfig() {
            public String configName() {
                String name = bundle.getString("name");
                if (TextUtils.isEmpty(name)) {
                    return DEFAULT_NAME;
                } else {
                    return name;
                }
            }

        };
    }

    final String getName() {
        String name = configName();
        if (TextUtils.isEmpty(name)) {
            return DEFAULT_NAME;
        } else {
            return name;
        }
    }

    public abstract String configName();

}
