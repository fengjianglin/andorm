
package com.ifatter.andorm.dao;

import com.ifatter.andorm.database.ExampleDatabase;
import com.ifatter.andorm.model.Book;
import com.ifatter.andorm.orm.Database;
import com.ifatter.andorm.orm.Transaction;

import java.util.List;

@Database(database = ExampleDatabase.class)
// @Database(cfgPath = "com/ifatter/andorm/database/config")
public interface BookDao {

    public List<Book> findAll();

    public long insert(Book book);

    public long insert(String title, String url, String icon_url);

    public List<Book> findAll2();

    public long insert2(Book book);

    public long insert2(String title, String url, String icon_url);

    @Transaction
    public void testTransaction();
}
