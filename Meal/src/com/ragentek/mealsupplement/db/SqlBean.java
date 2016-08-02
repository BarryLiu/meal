package com.ragentek.mealsupplement.db;

public interface SqlBean {
	String getTableName();
	String getPrimaryKeyColumnName();
	String[] getColumnNames();
}
