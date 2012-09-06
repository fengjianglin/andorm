
package com.ifatter.andorm.orm;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Field;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Template<T> implements Operations<T> {

    private final ORMSQLiteHelper mDBHelper;

    private final Class<T> mClazz;

    private Field[] mFields;

    private String mTableName;

    private String mIdColumn;

    protected Template(ORMSQLiteHelper dbHelper, Class<T> clazz) {

        this.mDBHelper = dbHelper;
        this.mClazz = clazz;

        if (this.mClazz.isAnnotationPresent(Table.class)) {
            Table table = (Table)this.mClazz.getAnnotation(Table.class);
            this.mTableName = table.name();
        }

        this.mFields = this.mClazz.getDeclaredFields();

        for (Field field : this.mFields) {
            if (field.isAnnotationPresent(Id.class)) {
                Column column = (Column)field.getAnnotation(Column.class);
                this.mIdColumn = column.name();
                break;
            }
        }
    }

    ORMSQLiteHelper getOrmsqLiteHelper() {
        return mDBHelper;
    }

    public boolean isExist(String sql, String[] selectionArgs) {

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.mDBHelper.getSqLiteDatabase();
            cursor = db.rawQuery(sql, selectionArgs);
            if (cursor.getCount() > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // if (db != null) {
            // db.close();
            // }
        }
        return false;
    }

    public T get(int id) {
        String selection = this.mIdColumn + " = ?";
        String[] selectionArgs = {
            Integer.toString(id)
        };
        List<T> list = find(null, selection, selectionArgs, null, null, null, null);
        if ((list != null) && (list.size() > 0)) {
            return (T)list.get(0);
        }
        return null;
    }

    public long insert(T entity) {
        SQLiteDatabase db = null;
        try {
            db = this.mDBHelper.getSqLiteDatabase();
            ContentValues cv = new ContentValues();
            setContentValues(entity, cv, "insert");
            long row = db.insert(this.mTableName, null, cv);
            return row;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // if (db != null) {
            // db.close();
            // }
        }
        return 0L;
    }

    public int delete(int id) {
        int ret = 0;
        SQLiteDatabase db = this.mDBHelper.getSqLiteDatabase();
        String where = this.mIdColumn + " = ?";
        String[] whereValue = {
            Integer.toString(id)
        };
        ret = db.delete(this.mTableName, where, whereValue);
        // db.close();
        return ret;
    }

    public void delete(Integer... ids) {
        if (ids.length > 0) {
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < ids.length; i++) {
                sb.append('?').append(',');
            }
            sb.deleteCharAt(sb.length() - 1);
            SQLiteDatabase db = this.mDBHelper.getSqLiteDatabase();
            String sql = "delete from " + this.mTableName + " where " + this.mIdColumn + " in ("
                    + sb + ")";

            db.execSQL(sql, (Object[])ids);
            // db.close();
        }
    }

    public int update(T entity) {
        int ret = 0;
        SQLiteDatabase db = null;
        try {
            db = this.mDBHelper.getSqLiteDatabase();
            ContentValues cv = new ContentValues();

            setContentValues(entity, cv, "update");

            String where = this.mIdColumn + " = ?";
            int id = Integer.parseInt(cv.get(this.mIdColumn).toString());
            cv.remove(this.mIdColumn);

            String[] whereValue = {
                Integer.toString(id)
            };
            ret = db.update(this.mTableName, cv, where, whereValue);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // if (db != null) {
            // db.close();
            // }
        }
        return ret;
    }

    public List<T> rawQuery(String sql, String[] selectionArgs) {

        List<T> list = new ArrayList<T>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.mDBHelper.getSqLiteDatabase();
            cursor = db.rawQuery(sql, selectionArgs);
            getListFromCursor(list, cursor);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // if (db != null) {
            // db.close();
            // }
        }

        return list;
    }

    public List<T> find() {
        return find(null, null, null, null, null, null, null);
    }

    public List<T> find(String[] columns, String selection, String[] selectionArgs, String groupBy,
            String having, String orderBy, String limit) {

        List<T> list = new ArrayList<T>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = this.mDBHelper.getSqLiteDatabase();
            cursor = db.query(this.mTableName, columns, selection, selectionArgs, groupBy, having,
                    orderBy, limit);

            getListFromCursor(list, cursor);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            // if (db != null) {
            // db.close();
            // }
        }

        return list;
    }

    public List<Map<String, String>> query(String sql, String[] selectionArgs) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        List<Map<String, String>> retList = new ArrayList<Map<String, String>>();
        try {
            db = this.mDBHelper.getSqLiteDatabase();
            cursor = db.rawQuery(sql, selectionArgs);
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
            // if (db != null) {
            // db.close();
            // }
        }
        return retList;
    }

    public void execSql(String sql, Object[] selectionArgs) {
        SQLiteDatabase db = null;
        try {
            db = this.mDBHelper.getSqLiteDatabase();
            if (selectionArgs == null) {
                db.execSQL(sql);
            } else {
                db.execSQL(sql, selectionArgs);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // if (db != null) {
            // db.close();
            // }
        }
    }

    private void getListFromCursor(List<T> list, Cursor cursor) throws IllegalAccessException,
            InstantiationException {
        while (cursor.moveToNext()) {
            T entity = (T)this.mClazz.newInstance();

            for (Field field : this.mFields) {
                Column column = null;
                if (field.isAnnotationPresent(Column.class)) {
                    column = (Column)field.getAnnotation(Column.class);

                    field.setAccessible(true);
                    Class<?> fieldType = field.getType();

                    int c = cursor.getColumnIndex(column.name());
                    if (c < 0) {
                        continue;
                    } else if ((Integer.TYPE == fieldType) || (Integer.class == fieldType)) {
                        field.set(entity, cursor.getInt(c));
                    } else if (String.class == fieldType) {
                        field.set(entity, cursor.getString(c));
                    } else if ((Long.TYPE == fieldType) || (Long.class == fieldType)) {
                        field.set(entity, Long.valueOf(cursor.getLong(c)));
                    } else if ((Float.TYPE == fieldType) || (Float.class == fieldType)) {
                        field.set(entity, Float.valueOf(cursor.getFloat(c)));
                    } else if ((Short.TYPE == fieldType) || (Short.class == fieldType)) {
                        field.set(entity, Short.valueOf(cursor.getShort(c)));
                    } else if ((Double.TYPE == fieldType) || (Double.class == fieldType)) {
                        field.set(entity, Double.valueOf(cursor.getDouble(c)));
                    } else if (Blob.class == fieldType) {
                        field.set(entity, cursor.getBlob(c));
                    } else if (Character.TYPE == fieldType) {
                        String fieldValue = cursor.getString(c);

                        if ((fieldValue != null) && (fieldValue.length() > 0)) {
                            field.set(entity, Character.valueOf(fieldValue.charAt(0)));
                        }
                    }
                }
            }

            list.add((T)entity);
        }
    }

    private void setContentValues(T entity, ContentValues cv, String type)
            throws IllegalAccessException {

        for (Field field : this.mFields) {
            if (!field.isAnnotationPresent(Column.class)) {
                continue;
            }
            Column column = (Column)field.getAnnotation(Column.class);

            field.setAccessible(true);
            Object fieldValue = field.get(entity);
            if (fieldValue == null) {
                continue;
            }
            if (("insert".equals(type)) && (field.isAnnotationPresent(Id.class))) {
                continue;
            }
            cv.put(column.name(), fieldValue.toString());
        }
    }

    public void close() {
        try {
            mDBHelper.close();
        } catch (Exception e) {
        }
    }

}
