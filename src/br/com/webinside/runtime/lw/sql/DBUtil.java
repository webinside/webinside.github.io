package br.com.webinside.runtime.lw.sql;

import java.util.ArrayList;
import java.util.List;

import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.function.database.NodeTable;
import br.com.webinside.runtime.integration.DatabaseHandler;
import br.com.webinside.runtime.util.WIMap;

public class DBUtil {

    private DBUtil() { }

    public static void loadRndKey(WIMap wiMap, DatabaseHandler dh, NodeTable nodeTable,
    		String varKey, String varTarget) throws Exception {
		List<String> keys = new ArrayList(nodeTable.getPrimaryKeys().keySet());
		int keyVal = Integer.parseInt(wiMap.get(varKey));
		String query = "select ts_rnd_key from " + nodeTable.getName();
		query += " where " + keys.get(0) + " = " + keyVal;
		ResultSet rsKey = dh.execute(query, wiMap);
		if (rsKey.next() > 0) {
			wiMap.put(varTarget, rsKey.column(1));
		}			
    }
	
}
