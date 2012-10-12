
package com.ifatter.andorm;

import com.ifatter.Manifest;
import com.ifatter.andorm.dao.BookDao;
import com.ifatter.andorm.dao.impl.BookDaoImpl;
import com.ifatter.andorm.model.Book;
import com.ifatter.andorm.orm.DaoFactory;
import com.ifatter.andorm.util.ManifestParser;

import android.app.Activity;
import android.os.Bundle;

import java.io.IOException;

public class AndormExampleActivity extends Activity {

    BookDao bookDao = null;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        bookDao = DaoFactory.createDao(BookDaoImpl.class, this);

        Book book = new Book();
        book.setTitle("XXXX");
        book.setUrl("http:///andorm/");
        book.setIconUrl("http:///andorm/icon.png");
        bookDao.insert(book);

        book = new Book();
        book.setTitle("YYYY");
        book.setUrl("http://www");
        book.setIconUrl("http://www");
        bookDao.insert(book);

        bookDao.testTransaction();

        test();

    }

    public void test() {
        Manifest manifest = new ManifestParser().parser();
        System.out.println(manifest.getPackageName());
    }

}
