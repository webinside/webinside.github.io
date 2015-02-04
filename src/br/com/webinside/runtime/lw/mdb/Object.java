package br.com.webinside.runtime.lw.mdb;

import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.integration.AbstractConnector;
import br.com.webinside.runtime.integration.DatabaseAliases;
import br.com.webinside.runtime.integration.InterfaceHeaders;
import br.com.webinside.runtime.integration.InterfaceParameters;
import br.com.webinside.runtime.integration.JavaParameter;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.WIMap;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class Object extends AbstractConnector implements InterfaceParameters {
	
	public void execute(WIMap wiMap, DatabaseAliases databases, 
			InterfaceHeaders headers) throws UserException {
		try {
			String db = wiMap.get("tmp.mongoobj.database");
			DB mdb = BaseUtil.getDB(getParams(), db);
			String table = wiMap.get("tmp.mongoobj.table");
			DBCollection dbc = mdb.getCollection(table);
			String regvar = wiMap.get("tmp.mongoobj.regvar");
			String regkey = wiMap.get("tmp.mongoobj.regkey");
			long regId = Function.parseLong(wiMap.get(regvar + "." + regkey));
			String pMulti = wiMap.get("tmp.mongoobj.prefix_multi").trim();
			if (!pMulti.trim().equals("")) regvar += "." + pMulti; 
			BaseUtil.clearMap(wiMap, regvar, regkey);
			boolean found = false;
			BasicDBObject docKey = new BasicDBObject("_regid", regId + "");
			DBObject docRoot = dbc.findOne(docKey);
			if (docRoot != null && (Boolean)docRoot.get("_valid") == true) {
				if (pMulti.equals("") && docRoot.containsField("version")) {
						long version = (Long)docRoot.get("version");
						String verId = regId + "v" + version;
						BaseUtil.loadObj(dbc, verId, wiMap, regvar);
				} 
				if (!pMulti.equals("") && docRoot.containsField("multi")) {
					BasicDBList multiList = (BasicDBList)docRoot.get("multi");
					long version = Function.parseLong(wiMap.get(regvar + "." + regkey));
					if (multiList.contains(version)) {
						String verId = regId + "v" + version;
						found = BaseUtil.loadObj(dbc, verId, wiMap, regvar);
					}
				}
			}
			if (!pMulti.equals("") && !found) wiMap.remove(regvar + "." + regkey);
		} catch (Exception err) {
			String msg = "Page: " + wiMap.get("wi.page.id");
			getParams().getErrorLog().write(getClass().getName(), msg, err);
		}
	}
	 		
	public JavaParameter[] getInputParameters() {
		JavaParameter[] in = new JavaParameter[5];
		in[0] = new JavaParameter("tmp.mongoobj.database", "Mongo Database");
		in[1] = new JavaParameter("tmp.mongoobj.table", "Mongo Table (Collection)");
		in[2] = new JavaParameter("tmp.mongoobj.regvar", "Variável do Objeto");
		in[3] = new JavaParameter("tmp.mongoobj.regkey", "Campo Id do Objeto");
		in[4] = new JavaParameter("tmp.mongoobj.prefix_multi", "Prefixo (multivalorado)");
		return in;
	}

	public JavaParameter[] getOutputParameters() {
		return new JavaParameter[0];
	}
	
}
