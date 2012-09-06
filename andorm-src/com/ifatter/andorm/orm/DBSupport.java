
package com.ifatter.andorm.orm;

import android.text.TextUtils;

public abstract class DBSupport {

    final String getName() {
        String name = configName();
        if (TextUtils.isEmpty(name)) {
            return "default_database.db";
        } else {
            return name;
        }
    }

    final int getVersion() {
        int version = configVersion();
        if (version <= 0) {
            return 1;
        } else {
            return version;
        }
    }

    final Class<?>[] getClasses() {
        Class<?>[] classes = configBeanClasses();
        if (classes == null) {
            return new Class<?>[] {};
        } else {
            return classes;
        }
    }

    public abstract String configName();

    public abstract int configVersion();

    public abstract Class<?>[] configBeanClasses();
}
