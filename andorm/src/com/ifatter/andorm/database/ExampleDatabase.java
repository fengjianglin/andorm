
package com.ifatter.andorm.database;

import com.ifatter.andorm.orm.DBConfig;

public class ExampleDatabase extends DBConfig {

    @Override
    public String configName() {
        return "database_from_code.db";
    }
}
