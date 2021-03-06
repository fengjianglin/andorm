package com.ifatter.andorm.dao;

import java.util.List;
import com.ifatter.andorm.model.Book;
import com.ifatter.andorm.orm.annotation.Model;
import com.ifatter.andorm.orm.annotation.Transaction;

//@Database(dbCfgPath = "com/ifatter/andorm/dao/andorm_db")
@Model(model = Book.class)
public interface BookDao {

    @Transaction
    public List<Book> findAll();

    @Transaction
    public long insert(Book book);

    @Transaction
    public long insert(String title, String url, String icon_url);

}
