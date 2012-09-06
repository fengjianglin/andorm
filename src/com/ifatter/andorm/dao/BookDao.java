
package com.ifatter.andorm.dao;

import com.ifatter.andorm.model.Book;
import com.ifatter.andorm.orm.Transaction;

public interface BookDao {

    public long insert(Book book);

    @Transaction
    public void transaction();
}
