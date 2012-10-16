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

import com.ifatter.andorm.orm.annotation.Database;
import com.ifatter.andorm.orm.annotation.Model;
import java.io.File;
import java.lang.reflect.Method;

public abstract class DaoSupport {

    private Template mTemplate;

    private DatabaseCache mDBCache;

    public DaoSupport() {
        boolean init = false;
        Class<?>[] classes = getClass().getInterfaces();
        if (classes != null) {
            for (Class<?> clazz : classes) {
                if (clazz.isAnnotationPresent(Database.class)) {
                    Database db = clazz.getAnnotation(Database.class);
                    initDB(db.dbCfgPath());
                    init = true;
                    break;
                }
            }
        }
        if (!init) {
            initDB(null);
        }
    }

    private void initDB(String cfgPath) {
        DBConfig support = DBConfig.get(cfgPath);
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
        mDBCache = new DatabaseCache(path);
    }

    Object transaction(Method method, Object... args) throws Throwable {
        return getTemplate().transaction(this, method, args);
    }

    protected final synchronized Template getTemplate() {
        if (mTemplate == null) {
            boolean b = false;
            Class<?>[] interfaces = getClass().getInterfaces();
            if (interfaces != null) {
                for (Class<?> clazz : interfaces) {
                    if (clazz.isAnnotationPresent(Model.class)) {
                        Model model = clazz.getAnnotation(Model.class);
                        Class<?> cla = (Class<?>)model.model();
                        mTemplate = new Template(mDBCache.openDatabase(), cla);
                        b = true;
                        break;
                    }
                }
            }
            if (!b) {
                throw new IllegalArgumentException(getClass() + " need @Model");
            }
        }
        return mTemplate;
    }
}
