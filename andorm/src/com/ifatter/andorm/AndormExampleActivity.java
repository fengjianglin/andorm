
package com.ifatter.andorm;

import com.ifatter.andorm.dao.BookDao;
import com.ifatter.andorm.dao.impl.BookDaoImpl;
import com.ifatter.andorm.model.Book;

import android.app.Activity;
import android.os.Bundle;

public class AndormExampleActivity extends Activity {

    BookDao bookDao = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        bookDao = (BookDao)new BookDaoImpl(this).getDaoTransaction();

        Book book = new Book();
        book.setTitle("PPP");
        book.setUrl("http:///andorm/");
        book.setIconUrl("http:///andorm/icon.png");
        bookDao.insert2(book);

        book = new Book();
        book.setTitle("WWWW");
        book.setUrl("http://www");
        book.setIconUrl("http://www");
        bookDao.insert2(book);

        bookDao.testTransaction();

    }

}
