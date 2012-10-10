
package com.ifatter.andorm.dao;

import com.ifatter.andorm.database.ExampleDatabase;
import com.ifatter.andorm.model.Book;
import com.ifatter.andorm.orm.Database;
import com.ifatter.andorm.orm.Transaction;

@Database(database = ExampleDatabase.class, cfgPath = "com/ifatter/andorm/database/config")
public interface BookDao {

    public long insert(Book book);

    public long insert(String title, String url, String icon_url);

    @Transaction
    public void testTransaction();
}
