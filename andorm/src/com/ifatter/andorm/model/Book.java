package com.ifatter.andorm.model;

import com.ifatter.andorm.orm.Model;
import com.ifatter.andorm.orm.annotation.Column;
import com.ifatter.andorm.orm.annotation.Id;
import com.ifatter.andorm.orm.annotation.Table;

@Table(name = "table_book")
public class Book extends Model {

	@Id
	@Column(name = "_id")
	public int id;

	@Column(name = "title", length = 64)
	public String title;

	@Column(name = "url", length = 64)
	public String url;

	@Column(name = "icon_url", length = 64)
	public String iconUrl;
	
	
	public void test(){
		System.out.println("----------Book");
	}


}
