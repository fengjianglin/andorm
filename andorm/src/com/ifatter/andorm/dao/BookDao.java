package com.ifatter.andorm.dao;

import com.ifatter.andorm.database.ExampleDatabase;
import com.ifatter.andorm.model.Book;
import com.ifatter.andorm.orm.annotation.Database;
import com.ifatter.andorm.orm.annotation.Model;
import com.ifatter.andorm.orm.annotation.Transaction;

import java.util.List;

@Database(database = ExampleDatabase.class)
// @Database(cfgPath = "com/ifatter/andorm/database/config")
@Model(model = Book.class)
public interface BookDao {

	public List<Book> findAll();

	public long insert(Book book);

	public long insert(String title, String url, String icon_url);

	@Transaction
	public void testTransaction();
}
