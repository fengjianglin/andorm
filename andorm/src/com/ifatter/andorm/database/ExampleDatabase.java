
package com.ifatter.andorm.database;

import com.ifatter.andorm.model.Book;
import com.ifatter.andorm.orm.Config;

public class ExampleDatabase extends Config {

    @Override
    public String configName() {
        return "example_database.db";
    }

    @Override
    public int configVersion() {
        return 1;
    }

    @Override
    public Class<?>[] configBeanClasses() {
        return new Class<?>[] {
            Book.class
        };
    }

}
