
package com.ifatter.andorm;

import android.app.Activity;
import android.os.Bundle;
import com.ifatter.andorm.dao.BookDao;
import com.ifatter.andorm.dao.impl.BookDaoImpl;
import com.ifatter.andorm.model.Book;
import com.ifatter.andorm.orm.DaoFactory;

public class AndormExampleActivity extends Activity {

    BookDao bookDao = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        bookDao = DaoFactory.createDao(BookDaoImpl.class);

        Book book = new Book();
        book.setTitle("999");
        book.setUrl("http:///andorm/");
        book.setIconUrl("http:///andorm/icon.png");
        bookDao.insert(book);

        book = new Book();
        book.setTitle("999");
        book.setUrl("http://www");
        book.setIconUrl("http://www");
        bookDao.insert(book);

    }

}
