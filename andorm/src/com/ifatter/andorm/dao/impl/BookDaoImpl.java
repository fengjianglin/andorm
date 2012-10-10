
package com.ifatter.andorm.dao.impl;

import com.ifatter.andorm.dao.BookDao;
import com.ifatter.andorm.model.Book;
import com.ifatter.andorm.orm.DaoSupport;

import android.content.Context;

public class BookDaoImpl extends DaoSupport<Book> implements BookDao {

    public BookDaoImpl(Context context) {
        super(context);
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

    @Override
    public void testTransaction() {
        System.out.println("------testTransaction");
    }
}
