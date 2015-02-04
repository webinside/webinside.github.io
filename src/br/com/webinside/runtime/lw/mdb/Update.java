package br.com.webinside.runtime.lw.mdb;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import br.com.webinside.runtime.core.EngFunction;
import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.integration.AbstractConnector;
import br.com.webinside.runtime.integration.DatabaseAliases;
import br.com.webinside.runtime.integration.InterfaceHeaders;
import br.com.webinside.runtime.integration.InterfaceParameters;
import br.com.webinside.runtime.integration.JavaParameter;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.WIMap;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

public class Update extends AbstractConnector implements InterfaceParameters {
	
	public void execute(WIMap wiMap, DatabaseAliases databases, 
			InterfaceHeaders headers) throws UserException {
		try {
			String db = wiMap.get("tmp.mongoupd.database");
			DB mdb = BaseUtil.getDB(getParams(), db);
			String table = wiMap.get("tmp.mongoupd.table");
			DBCollection dbc = mdb.getCollection(table);
			BaseUtil.ensureIndex(dbc, "_regid");
			String session = wiMap.get("wi.session.id");
			String regvar = wiMap.get("tmp.mongoupd.regvar");
			String regkey = wiMap.get("tmp.mongoupd.regkey");
			long regId = Function.parseLong(wiMap.get(regvar + "." + regkey));
			if (regId == 0) return;
			String pMulti = wiMap.get("tmp.mongoupd.prefix_multi");
			if (pMulti.trim().equals("")) {
				BasicDBObject objVer = 
						BaseUtil.getVersionObj(dbc, regId, 0, false, session);
				objVer.putAll(getMap(wiMap, regvar, regkey));
				dbc.insert(objVer);
			} else {
				regvar += "." + pMulti; 
				long verId = Function.parseLong(wiMap.get(regvar + "." + regkey));
				BasicDBObject objVer = 
						BaseUtil.getVersionObj(dbc, regId, verId, true, session);
				objVer.putAll(getMap(wiMap, regvar, regkey));
				dbc.insert(objVer);
			}
		} catch (Exception err) {
        	EngFunction.invalidateTransaction(wiMap, err.toString());
			String msg = "Page: " + wiMap.get("wi.page.id");
			getParams().getErrorLog().write(getClass().getName(), msg, err);
		}
	}
	
	private Map getMap(WIMap wiMap, String regvar, String regkey) 
	throws Exception {
		Map map = new HashMap();
		WIMap subWIMap = (WIMap) wiMap.getObj(regvar + ".");
		if (subWIMap != null) {
			Set<Map.Entry> entrySet = subWIMap.getAsMap().entrySet();
			for (Map.Entry<String, String> entry : entrySet) {
				if (entry.getKey().equals(regkey) || 
					entry.getKey().indexOf(".") > -1 ||	
					entry.getValue().trim().equals("")) continue;
				map.put(entry.getKey(), entry.getValue());
			}
		}
		return map;
	}
 		
	public JavaParameter[] getInputParameters() {
		JavaParameter[] in = new JavaParameter[5];
		in[0] = new JavaParameter("tmp.mongoupd.database", "Mongo Database");
		in[1] = new JavaParameter("tmp.mongoupd.table", "Mongo Table (Collection)");
		in[2] = new JavaParameter("tmp.mongoupd.regvar", "Variável do Objeto");
		in[3] = new JavaParameter("tmp.mongoupd.regkey", "Campo Id do Objeto");
		in[4] = new JavaParameter("tmp.mongoupd.prefix_multi", "Prefixo (multivalorado)");
		return in;
	}

	public JavaParameter[] getOutputParameters() {
		return new JavaParameter[0];
	}
	
}
