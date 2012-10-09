
package com.ifatter.andorm.dao.impl;

import com.ifatter.andorm.dao.BookDao;
import com.ifatter.andorm.database.ExampleDatabase;
import com.ifatter.andorm.model.Book;
import com.ifatter.andorm.orm.DaoSupport;
import com.ifatter.andorm.orm.Database;

import android.content.Context;

import java.util.List;

@Database(database = ExampleDatabase.class, cfgPath = "com/ifatter/andorm/database/config")
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

    public List<Book> getAllQuickAccessItem() {
        List<Book> ret = getTemplate().find();
        return ret;
    }

    public Book getQuickAccessItemById(int id) {
        Book ret = getTemplate().get(id);
        return ret;
    }

    public int modifyQuickAccess(Book item) {
        int rowCnt = getTemplate().update(item);
        return rowCnt > 0 ? 0 : -1;
    }

    public int delQuickAccess(int id) {
        long rows = getTemplate().delete(id);
        return rows > 0 ? 0 : -1;

    }

    @Override
    public void transaction() {
        System.out.println("------transaction");
    }
}
