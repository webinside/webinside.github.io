package br.com.webinside.runtime.function.database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;

import br.com.webinside.runtime.database.impl.ConnectionSql;
import br.com.webinside.runtime.integration.AbstractConnector;
import br.com.webinside.runtime.integration.DatabaseHandler;
import br.com.webinside.runtime.util.WIMap;

public abstract class AbstractConnectorDB extends AbstractConnector {

	private static Map tables = new HashMap();
	
	protected NodeTable getNodeTable(DatabaseHandler dh, String table) 
	throws SQLException {
		synchronized (tables) {
			if (!tables.containsKey(table)) {
				ConnectionSql conSql = (ConnectionSql) dh.getDatabaseConnection();
				loadTable(conSql.getConnection(), table);
			}
		}	
		return (NodeTable)tables.get(table);
	}
	
	protected void doReset(WIMap wiMap) {
		tables = new HashMap();
		String className = getClass().getName();
		String detail = "proj: " + wiMap.get("wi.proj.id") + 
			", page: " + wiMap.get("wi.page.id");
		System.out.println(className + ": Reset " + detail);
		getParams().getErrorLog().write(className, "Reset", detail);
	}
	
	private void loadTable(Connection connection, String table) 
	throws SQLException {
		NodeTable tableObj = new NodeTable(table);
		tableObj.setPrimaryKeys(getKeys(connection, table, 'P'));
		tableObj.setImportedKeys(getKeys(connection, table, 'I'));
		tableObj.setExportedKeys(getKeys(connection, table, 'E'));
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery("select * from " + table);
		ResultSetMetaData rsmd = rs.getMetaData();
	    int numCols = rsmd.getColumnCount();
	    for (int i = 1; i <= numCols; i++) {
	    	NodeColumn column = new NodeColumn(rsmd.getColumnName(i));
			column.setType(rsmd.getColumnTypeName(i));
			column.setLength(rsmd.getColumnDisplaySize(i));
			column.setAutoIncrement(rsmd.isAutoIncrement(i));
			column.setNullable(rsmd.isNullable(i) == 1);
			if (connection.getMetaData().getURL().startsWith("jdbc:Cache")) {
				String[] pkcols = 
					(String[]) tableObj.getPrimaryKeys().get(column.getName()); 
				if (pkcols != null && "RowIDField_As_PKey".equals(pkcols[5])) {
					column.setAutoIncrement(true);
				}
			}
			tableObj.addColumn(column);
	    }
	    tables.put(table, tableObj);
		rs.close();
		stmt.close();
	}

	private Map getKeys(Connection connection, String table, 
			char type) throws SQLException {
		Map map = new LinkedHashMap();
		DatabaseMetaData dbMetaData = connection.getMetaData();
        ResultSet rs = null;
    	if (type == 'P') {
            rs = dbMetaData.getPrimaryKeys(null, null, table);
    	} else if (type == 'I') {
            rs = dbMetaData.getImportedKeys(null, null, table);
    	} else if (type == 'E') {
            rs = dbMetaData.getExportedKeys(null, null, table);
    	}
		Map<Integer, String> reorder = new TreeMap();
        while (rs.next()) {
    	    int numCols = rs.getMetaData().getColumnCount();
    	    String[] cols = new String[numCols];
    	    for (int i = 1; i <= numCols; i++) {
    	    	cols[i - 1] = rs.getString(i);
    	    }
        	if (type == 'P') {
    	    	map.put(rs.getString(4), cols);
    	    	if (rs.getInt(5) > 0) {
        	    	reorder.put(rs.getInt(5), rs.getString(4));
    	    	}
        	} else if (type == 'I') {
    	    	map.put(rs.getString(8), cols);
    	    } else if (type == 'E') {
    	    	map.put(rs.getString(7) + "." + rs.getString(8), cols);
    	    }	
        }
		rs.close();
        if (!reorder.isEmpty()) {
    		Map map2 = new LinkedHashMap();
        	for (Map.Entry<Integer, String> entry : reorder.entrySet()) {
        		String key = entry.getValue(); 
				map2.put(key, map.get(key));
			}
        	map = map2;
        }
		return map;
	}
	
}
