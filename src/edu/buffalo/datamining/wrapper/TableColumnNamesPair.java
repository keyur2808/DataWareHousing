/**
 * 
 */
package edu.buffalo.datamining.wrapper;

import java.io.Serializable;

/**
 * @author Keyur
 *
 */
public class TableColumnNamesPair implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private String tableName=null;
	private String columnName=null;
	
	
	public TableColumnNamesPair(String tableName,String columnName) {
	   this.tableName=tableName;
	   this.columnName=columnName;
	}


	public String getTableName() {
		return tableName;
	}


	public void setTableName(String tableName) {
		this.tableName = tableName;
	}


	public String getColumnName() {
		return columnName;
	}


	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}

}
