
package com.ifatter.andorm.database;

import com.ifatter.andorm.model.Book;
import com.ifatter.andorm.orm.DBConfig;

public class ExampleDatabase extends DBConfig {

    @Override
    public String configName() {
        return "example_database.db";
    }

    @Override
    public Class<?>[] configBeanClasses() {
        return new Class<?>[] {
            Book.class
        };
    }

}
