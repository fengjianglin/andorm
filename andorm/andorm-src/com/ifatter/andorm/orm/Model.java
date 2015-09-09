package com.ifatter.andorm.orm;

public class Model {

	public long insert() {
		return DaoSupport.getTemplate(getClass()).insert(this);
	}

	public int update() {
		return DaoSupport.getTemplate(getClass()).update(this);
	}

	public int save() {
		return DaoSupport.getTemplate(getClass()).save(this);
	}

	public int delete() {
		return DaoSupport.getTemplate(getClass()).delete(this);
	}

}
