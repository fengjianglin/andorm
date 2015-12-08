package com.ifatter.andorm;

import com.ifatter.andorm.query.Delete;
import com.ifatter.andorm.query.Insert;
import com.ifatter.andorm.query.Save;
import com.ifatter.andorm.query.Update;

public class Model {

	public long insert() {
		return Insert.into(getClass()).values(this);
	}

	public int update() {
		return Update.from(getClass()).set(this);
	}

	public int save() {
		return Save.from(getClass()).save(this);
	}

	public int delete() {
		return Delete.from(getClass()).delete(this);
	}
}
