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

public class Remove extends AbstractConnector implements InterfaceParameters {
	
	public void execute(WIMap wiMap, DatabaseAliases databases, 
			InterfaceHeaders headers) throws UserException {
		try {
			String db = wiMap.get("tmp.mongodel.database");
			DB mdb = BaseUtil.getDB(getParams(), db);
			String table = wiMap.get("tmp.mongodel.table");
			DBCollection dbc = mdb.getCollection(table);
			String regvar = wiMap.get("tmp.mongodel.regvar");
			String regkey = wiMap.get("tmp.mongodel.regkey");
			long regId = Function.parseLong(wiMap.get(regvar + "." + regkey));
			if (regId == 0) return;
			String pMulti = wiMap.get("tmp.mongodel.prefix_multi");
			if (!pMulti.trim().equals("")) regvar += "." + pMulti;
			long multiVer = Function.parseLong(wiMap.get(regvar + "." + regkey));
			if (multiVer == 0) return;
			BasicDBObject docKey = new BasicDBObject("_regid", regId + "");
			DBObject docRoot = dbc.findOne(docKey);
			if (pMulti.trim().equals("")) {				
				if (docRoot != null && docRoot.containsField("version")) {
					docRoot.put("_valid", false);
					dbc.update(docKey, docRoot);
					long version = (Long)docRoot.get("version");
					BaseUtil.delete(dbc, regId + "v" + version);
				}
			} else if (docRoot != null && docRoot.containsField("multi")) {
				BasicDBList multiList = (BasicDBList)docRoot.get("multi");
				if (multiList.contains(multiVer)) {
					multiList.remove((Long)multiVer);
					if (multiList.size() == 0) docRoot.put("_valid", false);
					docRoot.put("multi", multiList);
					dbc.update(docKey, docRoot);
					BaseUtil.delete(dbc, regId + "v" + multiVer);
				}
			}
		} catch (Exception err) {
			String msg = "Page: " + wiMap.get("wi.page.id");
			getParams().getErrorLog().write(getClass().getName(), msg, err);
		}
	}
	 		
	public JavaParameter[] getInputParameters() {
		JavaParameter[] in = new JavaParameter[5];
		in[0] = new JavaParameter("tmp.mongodel.database", "Mongo Database");
		in[1] = new JavaParameter("tmp.mongodel.table", "Mongo Table (Collection)");
		in[2] = new JavaParameter("tmp.mongodel.regvar", "Variável do Objeto");
		in[3] = new JavaParameter("tmp.mongodel.regkey", "Campo Id do Objeto");
		in[4] = new JavaParameter("tmp.mongodel.prefix_multi", "Prefixo (multivalorado)");
		return in;
	}

	public JavaParameter[] getOutputParameters() {
		return new JavaParameter[0];
	}
	
}
