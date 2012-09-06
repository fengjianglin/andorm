
package com.ifatter.andorm;

import com.ifatter.andorm.dao.BookDao;
import com.ifatter.andorm.dao.impl.BookDaoImpl;
import com.ifatter.andorm.model.Book;

import android.app.Activity;
import android.os.Bundle;

public class AndormExampleActivity extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Book book = new Book();
        book.setTitle("七彩人生");
        book.setUrl("http://www.ifatter.com/andorm/");
        book.setIconUrl("http://www.ifatter.com/andorm/icon.png");

        BookDao bookDao = new BookDaoImpl(this);
        bookDao.insert(book);

    }
}
