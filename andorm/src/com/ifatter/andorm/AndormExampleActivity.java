package com.ifatter.andorm;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.ifatter.Andorm;
import com.ifatter.andorm.model.Book;
import com.ifatter.andorm.query.Select;

public class AndormExampleActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Andorm.initialize(getApplicationContext());

		Book book = new Book();
		book.title = "111";
		book.url = "http:///andorm/";
		book.iconUrl = "http:///andorm/icon.png";
		book.insert();

		List<Book> books = Select.from(Book.class).findAll();

		Toast.makeText(this, books.get(0).title, Toast.LENGTH_LONG).show();
	}

}
