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

import android.database.sqlite.SQLiteDatabase;

import java.lang.reflect.Field;
import java.sql.Blob;

import com.ifatter.andorm.orm.annotation.Column;
import com.ifatter.andorm.orm.annotation.Id;
import com.ifatter.andorm.orm.annotation.Table;

final public class ORMSQLiteHelper extends SQLiteOpenHelper {

    private SQLiteDatabase sqliteDatebase;

    public ORMSQLiteHelper(String dbPath) {
        super(dbPath);
    }

    synchronized SQLiteDatabase getSqLiteDatabase() {
        if (isAvailableDB()) {
            return sqliteDatebase;
        }
        sqliteDatebase = getDatabase();
        return sqliteDatebase;
    }

    synchronized boolean isAvailableDB() {
        if (sqliteDatebase != null && sqliteDatebase.isOpen()) {
            if (sqliteDatebase.isReadOnly()) {
                sqliteDatebase.close();
            } else {
                return true;
            }
        }
        return false;
    }

    public void createTable(SQLiteDatabase db, Class<?> clazz) {

        String tableName = "";
        if (clazz.isAnnotationPresent(Table.class)) {
            Table table = (Table)clazz.getAnnotation(Table.class);
            tableName = table.name();
        }

        StringBuilder sb = new StringBuilder();
        sb.append("CREATE TABLE ").append(tableName).append(" (");

        Field[] fields = clazz.getDeclaredFields();
        int size = fields.length;

        for (int i = size - 1; i >= 0; i--) {

            Field field = fields[i];
            if (!field.isAnnotationPresent(Column.class)) {
                continue;
            }

            Column column = (Column)field.getAnnotation(Column.class);

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

            if (((field.isAnnotationPresent(Id.class)) && (field.getType() == Integer.TYPE))
                    || (field.getType() == Integer.class)) {
                sb.append(" primary key autoincrement");
            } else if (field.isAnnotationPresent(Id.class)) {
                sb.append(" primary key");
            }

            sb.append(", ");
        }

        int length = sb.length();
        sb.delete(length - 2, length);
        sb.append(")");

        String sql = sb.toString();
        db.execSQL(sql);
    }

    public void dropTable(SQLiteDatabase db, Class<?> clazz) {
        String tableName = "";
        if (clazz.isAnnotationPresent(Table.class)) {
            Table table = (Table)clazz.getAnnotation(Table.class);
            tableName = table.name();
        }
        String sql = "DROP TABLE IF EXISTS " + tableName;
        db.execSQL(sql);
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

}
