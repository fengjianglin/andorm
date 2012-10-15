
package com.ifatter.andorm.dao;

import java.util.List;
import com.ifatter.andorm.model.Book;
import com.ifatter.andorm.orm.annotation.Database;
import com.ifatter.andorm.orm.annotation.Model;
import com.ifatter.andorm.orm.annotation.Transaction;

/* @Database isn't required */
@Database(dbCfgPath = "com/ifatter/andorm/dao/andorm_db")
@Model(model = Book.class)
public interface BookDao {

    public List<Book> findAll();

    public long insert(Book book);

    public long insert(String title, String url, String icon_url);

    @Transaction
    public void testTransaction();
}
