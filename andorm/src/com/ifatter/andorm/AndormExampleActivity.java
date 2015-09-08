package com.ifatter.andorm;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.ifatter.Andorm;
import com.ifatter.andorm.model.Book;

public class AndormExampleActivity extends Activity {

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		Andorm.initialize(this.getApplicationContext());

		Book book = new Book();
		book.setTitle("111");
		book.setUrl("http:///andorm/");
		book.setIconUrl("http:///andorm/icon.png");
		long i = book.insert();
		Toast.makeText(this, "" + i, Toast.LENGTH_LONG).show();
	}

}
