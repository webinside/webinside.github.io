package br.com.webinside.runtime.lw.mdb;

import java.util.HashSet;
import java.util.Set;

import br.com.webinside.runtime.util.WIMap;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;


public class BaseUtilR1 {

	public static BasicDBObject getVersionObj(DBCollection dbc, long regId, 
			long verId, boolean multi, String session) throws Exception {
		boolean create = true;
		long lastVersion = 0, atualVersion = 1;
		BasicDBObject docKey = new BasicDBObject("_regid", regId + "");
		DBObject docRoot = dbc.findOne(docKey);
		if (docRoot == null) {
			docRoot = new BasicDBObject("_regid", regId + "");
		}
		if (docRoot.containsField("version")) {
			boolean valid = true;
			if (docRoot.containsField("last_session")) {
				valid = (Boolean)docRoot.get("_valid");
				String last = (String) docRoot.get("last_session");
				if (session.equals(last) && valid) create = false;
			}
			lastVersion = atualVersion = (Long)docRoot.get("version");
			if (!multi && create && valid) delete(dbc, regId + "v" + lastVersion);
			if (create || multi) atualVersion++;
			if (!create && verId > 0) atualVersion = verId; 
		}
		String regVerId = regId + "v" + atualVersion;
		BasicDBObject docVer = new BasicDBObject("_regid", regVerId);
		if (create || multi) {
			docRoot.put("last_session", session);
			docRoot.put("_valid", true);
			if (atualVersion > lastVersion) {
				docRoot.put("version", atualVersion);
			}	
			if (multi) {
				BasicDBList multiList = (BasicDBList)docRoot.get("multi");
				if (multiList == null) multiList = new BasicDBList();
				if (verId != atualVersion) multiList.remove((Long)verId);
				if (!multiList.contains(atualVersion)) {
					multiList.add(atualVersion);
				}
				docRoot.put("multi", multiList);
			}
			dbc.update(docKey, docRoot, true, false);
		}
		if (lastVersion >= atualVersion) dbc.remove(docVer);
		return docVer;
	}

	public static void clearMap(WIMap wiMap, String regvar, String regkey) {
		WIMap map = (WIMap) wiMap.getObj(regvar + ".");
		if (map != null) {
			Set<String> keySet = new HashSet(map.getAsMap().keySet());
			for (String key : keySet) {
				if (key.equals(regkey) || key.indexOf(".") > -1) continue;
				map.remove(key);
			}
		}	
	}

	public static boolean loadObj(DBCollection dbc, String regId, 
			WIMap wiMap, String prefix) throws Exception {
		BasicDBObject docKey = new BasicDBObject("_regid", regId);
		DBObject doc = dbc.findOne(docKey);
		if (doc != null) {
			for (String key : doc.keySet()) {
				if (!key.startsWith("_")) {
					wiMap.put(prefix + "." + key, (String) doc.get(key));
				}
			}
			return true;
		}
		return false;
	}
	
	public static void delete(DBCollection dbc, String regId) throws Exception {
		BasicDBObject docKey = new BasicDBObject("_regid", regId);
		DBObject doc = dbc.findOne(docKey);
		if (doc != null) {
			doc.put("_valid", false);
			dbc.update(docKey, doc);
		}
	}

}
