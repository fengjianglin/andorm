/* 
 * Copyright (C) 2011-2012 ifatter.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ifatter.andorm.orm;

import com.ifatter.andorm.orm.annotation.Column;
import com.ifatter.andorm.orm.annotation.Id;
import com.ifatter.andorm.orm.annotation.Table;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Template implements Operations {

	private final SQLiteDatabase mDatabase;

	private final Class<Model> mClazz;

	private Field[] mFields;

	private String mTableName;

	private String mIdColumn;

	protected Template(SQLiteDatabase db, Class<Model> clazz) {

		this.mDatabase = db;
		this.mClazz = clazz;

		if (this.mClazz.isAnnotationPresent(Table.class)) {
			Table table = (Table) this.mClazz.getAnnotation(Table.class);
			this.mTableName = table.name();
		}

		this.mFields = this.mClazz.getDeclaredFields();

		for (Field field : this.mFields) {
			if (field.isAnnotationPresent(Id.class)) {
				Column column = (Column) field.getAnnotation(Column.class);
				this.mIdColumn = column.name();
				break;
			}
		}

		ensureTableExist();
	}

	private void createTable() {

		String tableName = "";
		if (this.mClazz.isAnnotationPresent(Table.class)) {
			Table table = (Table) this.mClazz.getAnnotation(Table.class);
			tableName = table.name();
		}

		StringBuilder sb = new StringBuilder();
		sb.append("CREATE TABLE ").append(tableName).append(" (");

		Field[] fields = this.mClazz.getDeclaredFields();
		int size = fields.length;

		for (int i = size - 1; i >= 0; i--) {

			Field field = fields[i];
			if (!field.isAnnotationPresent(Column.class)) {
				continue;
			}

			Column column = (Column) field.getAnnotation(Column.class);

			String columnType = "";
			if (column.type().equals("")) {
				columnType = getColumnType(field.getType());
			} else {
				columnType = column.type();
			}

			sb.append(column.name() + " " + columnType);

			if (column.length() != 0) {
				sb.append("(" + column.length() + ")");
			}

			if (field.isAnnotationPresent(Id.class)) {
				Class<?> clz = field.getType();
				if (clz == Integer.TYPE || clz == Integer.class) {
					sb.append(" primary key autoincrement");
				} else {
					sb.append(" primary key");
				}
			}

			sb.append(", ");
		}

		int length = sb.length();
		sb.delete(length - 2, length);
		sb.append(")");

		String sql = sb.toString();
		mDatabase.execSQL(sql);
	}

	public void dropTable() {
		String sql = "DROP TABLE IF EXISTS " + mTableName;
		mDatabase.execSQL(sql);
	}

	private String getColumnType(Class<?> fieldType) {
		if (String.class == fieldType) {
			return "TEXT";
		}
		if ((Integer.TYPE == fieldType) || (Integer.class == fieldType)) {
			return "INTEGER";
		}
		if ((Long.TYPE == fieldType) || (Long.class == fieldType)) {
			return "BIGINT";
		}
		if ((Float.TYPE == fieldType) || (Float.class == fieldType)) {
			return "FLOAT";
		}
		if ((Short.TYPE == fieldType) || (Short.class == fieldType)) {
			return "INT";
		}
		if ((Double.TYPE == fieldType) || (Double.class == fieldType)) {
			return "DOUBLE";
		}
		if (Blob.class == fieldType) {
			return "BLOB";
		}

		return "TEXT";
	}

	private void ensureTableExist() {
		if (!isTableExist()) {
			createTable();
		}
	}

	private boolean isTableExist() {
		boolean exist = false;
		Cursor cursor = null;
		try {
			String sql = "SELECT COUNT(*) FROM sqlite_master where type = 'table' and name = '"
					+ mTableName + "'";
			cursor = mDatabase.rawQuery(sql, null);
			if (cursor.moveToFirst() && cursor.getInt(0) > 0) {
				exist = true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return exist;
	}

	public boolean isExist(String sql, String[] selectionArgs) {

		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, selectionArgs);
			if (cursor.getCount() > 0) {
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return false;
	}

	public Model find(int id) {
		String selection = this.mIdColumn + " = ?";
		String[] selectionArgs = { Integer.toString(id) };
		List<Model> list = find(null, selection, selectionArgs, null, null,
				null, null);
		if ((list != null) && (list.size() > 0)) {
			return list.get(0);
		}
		return null;
	}

	public long insert(Model entity) {
		try {
			ContentValues cv = new ContentValues();
			setContentValues(entity, cv, "insert");
			long row = mDatabase.insert(this.mTableName, null, cv);
			return row;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return 0L;
	}

	public int update(Model entity) {
		int ret = 0;
		try {
			ContentValues cv = new ContentValues();
			setContentValues(entity, cv, "update");
			String where = this.mIdColumn + " = ?";
			int id = Integer.parseInt(cv.get(this.mIdColumn).toString());
			cv.remove(this.mIdColumn);

			String[] whereValue = { Integer.toString(id) };
			ret = mDatabase.update(this.mTableName, cv, where, whereValue);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return ret;
	}

	public int save(Model entity) {
		try {
			Field field = mClazz.getField(mIdColumn);
			String fieldValue = field.get(entity).toString();
			int idValue = Integer.parseInt(fieldValue);
			if (find(idValue) == null) {
				long i = insert(entity);
				if (i == -1) {
					return 0;
				} else {
					return 1;
				}
			} else {
				return update(entity);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		return 0;
	}

	public int delete(int id) {
		int ret = 0;
		String where = this.mIdColumn + " = ?";
		String[] whereValue = { Integer.toString(id) };
		ret = mDatabase.delete(this.mTableName, where, whereValue);
		return ret;
	}

	public void delete(Integer... ids) {
		if (ids.length > 0) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < ids.length; i++) {
				sb.append('?').append(',');
			}
			sb.deleteCharAt(sb.length() - 1);
			String sql = "delete from " + this.mTableName + " where "
					+ this.mIdColumn + " in (" + sb + ")";

			mDatabase.execSQL(sql, (Object[]) ids);
		}
	}

	@Override
	public int delete(String whereClause, String[] whereArgs) {
		return this.mDatabase.delete(this.mTableName, whereClause, whereArgs);
	}

	public int deleteAll() {
		return this.mDatabase.delete(mTableName, "1", null);
	}

	public List<Model> rawQuery(String sql, String[] selectionArgs) {

		List<Model> list = new ArrayList<Model>();
		Cursor cursor = null;
		try {
			cursor = mDatabase.rawQuery(sql, selectionArgs);
			getListFromCursor(list, cursor);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return list;
	}

	public List<Model> findAll() {
		return find(null, null, null, null, null, null, null);
	}

	@Override
	public List<Model> find(String selection, String[] selectionArgs) {
		return find(null, selection, selectionArgs, null, null, null, null);
	}

	public List<Model> find(String[] columns, String selection,
			String[] selectionArgs, String groupBy, String having,
			String orderBy, String limit) {

		List<Model> list = new ArrayList<Model>();
		Cursor cursor = null;
		try {
			cursor = mDatabase.query(this.mTableName, columns, selection,
					selectionArgs, groupBy, having, orderBy, limit);
			getListFromCursor(list, cursor);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return list;
	}

	public List<Map<String, String>> query(String sql, String[] selectionArgs) {
		Cursor cursor = null;
		List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
		try {
			cursor = mDatabase.rawQuery(sql, selectionArgs);
			while (cursor.moveToNext()) {
				Map<String, String> map = new HashMap<String, String>();
				for (String columnName : cursor.getColumnNames()) {
					map.put(columnName.toLowerCase(),
							cursor.getString(cursor.getColumnIndex(columnName)));
				}
				retList.add(map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}
		return retList;
	}

	public void execSql(String sql, Object[] selectionArgs) {
		try {
			if (selectionArgs == null) {
				mDatabase.execSQL(sql);
			} else {
				mDatabase.execSQL(sql, selectionArgs);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
	}

	private <T> void getListFromCursor(List<T> list, Cursor cursor)
			throws IllegalAccessException, InstantiationException {
		while (cursor.moveToNext()) {
			@SuppressWarnings("unchecked")
			T entity = (T) this.mClazz.newInstance();

			for (Field field : this.mFields) {
				Column column = null;
				if (field.isAnnotationPresent(Column.class)) {
					column = (Column) field.getAnnotation(Column.class);

					field.setAccessible(true);
					Class<?> fieldType = field.getType();

					int c = cursor.getColumnIndex(column.name());
					if (c < 0) {
						continue;
					} else if ((Integer.TYPE == fieldType)
							|| (Integer.class == fieldType)) {
						field.set(entity, cursor.getInt(c));
					} else if (String.class == fieldType) {
						field.set(entity, cursor.getString(c));
					} else if ((Long.TYPE == fieldType)
							|| (Long.class == fieldType)) {
						field.set(entity, Long.valueOf(cursor.getLong(c)));
					} else if ((Float.TYPE == fieldType)
							|| (Float.class == fieldType)) {
						field.set(entity, Float.valueOf(cursor.getFloat(c)));
					} else if ((Short.TYPE == fieldType)
							|| (Short.class == fieldType)) {
						field.set(entity, Short.valueOf(cursor.getShort(c)));
					} else if ((Double.TYPE == fieldType)
							|| (Double.class == fieldType)) {
						field.set(entity, Double.valueOf(cursor.getDouble(c)));
					} else if (Blob.class == fieldType) {
						field.set(entity, cursor.getBlob(c));
					} else if (Character.TYPE == fieldType) {
						String fieldValue = cursor.getString(c);

						if ((fieldValue != null) && (fieldValue.length() > 0)) {
							field.set(entity,
									Character.valueOf(fieldValue.charAt(0)));
						}
					}
				}
			}

			list.add((T) entity);
		}
	}

	private <T> void setContentValues(T entity, ContentValues cv, String type)
			throws IllegalAccessException {

		for (Field field : this.mFields) {
			if (!field.isAnnotationPresent(Column.class)) {
				continue;
			}
			Column column = (Column) field.getAnnotation(Column.class);

			field.setAccessible(true);
			Object fieldValue = field.get(entity);
			if (fieldValue == null) {
				continue;
			}
			if (("insert".equals(type))
					&& (field.isAnnotationPresent(Id.class))) {
				continue;
			}
			cv.put(column.name(), fieldValue.toString());
		}
	}

	Object transaction(Object object, Method method, Object... args)
			throws Throwable {
		try {
			mDatabase.beginTransaction();
			Object ret = method.invoke(object, args);
			mDatabase.setTransactionSuccessful();
			return ret;
		} finally {
			mDatabase.endTransaction();
		}
	}

}
