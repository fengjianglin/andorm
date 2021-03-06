
package com.ifatter.andorm.dao.impl;

import java.util.List;

import com.ifatter.andorm.dao.BookDao;
import com.ifatter.andorm.model.Book;
import com.ifatter.andorm.orm.DaoSupport;

public class BookDaoImpl extends DaoSupport implements BookDao {

    public List<Book> findAll() {
        return getTemplate().find();
    }

    public long insert(Book book) {
        return getTemplate().insert(book);
    }

    public long insert(String title, String url, String icon_url) {
        long ret = -1;
        Book book = new Book();
        book.setTitle(title);
        book.setUrl(url);
        book.setIconUrl(icon_url);
        ret = insert(book);
        return ret;
    }

    public void testTransaction() {
        System.out.println("------testTransaction");
    }

}
