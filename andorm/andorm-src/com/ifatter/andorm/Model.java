package com.ifatter.andorm;


public class Model {

	public long insert() {
		return Dao.getTemplate(getClass()).insert(this);
	}

	public int update() {
		return Dao.getTemplate(getClass()).update(this);
	}

	public int save() {
		return Dao.getTemplate(getClass()).save(this);
	}

	public int delete() {
		return Dao.getTemplate(getClass()).delete(this);
	}
}
