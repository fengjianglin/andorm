package com.ifatter.andorm.orm;

public class Model extends DaoSupport {

	public long insert() {
		return getTemplate().insert(this);
	}

	public int save() {
		return 0;
	}
}
