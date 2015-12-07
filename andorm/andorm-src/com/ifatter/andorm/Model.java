package com.ifatter.andorm;

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
