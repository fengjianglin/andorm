
package com.ifatter.andorm.dao.impl;

import com.ifatter.andorm.dao.BookDao;
import com.ifatter.andorm.model.Book;
import com.ifatter.andorm.orm.DaoSupport2;

import android.content.Context;

import java.util.List;

public class BookDaoImpl2 extends DaoSupport2 implements BookDao {

    public BookDaoImpl2(Context context) {
        super(context);
    }

    public List<Book> findAll() {
        return getTemplate2().find();
    }

    public long insert(Book book) {
        return getTemplate2().insert(book);
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

    public List<Book> findAll2() {
        return getTemplate2().find();
    }

    public long insert2(Book book) {
        return getTemplate2().insert(book);
    }

    public long insert2(String title, String url, String icon_url) {
        long ret = -1;
        Book book = new Book();
        book.setTitle(title);
        book.setUrl(url);
        book.setIconUrl(icon_url);
        ret = insert2(book);
        return ret;
    }

    public void testTransaction() {
        System.out.println("------testTransaction");
    }

}
