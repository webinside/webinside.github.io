package br.com.webinside.runtime.lw.mdb;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.codec.digest.DigestUtils;

import br.com.webinside.runtime.component.Host;
import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.util.WIMap;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoOptions;

public class BaseUtil {

    private static Map<String, Mongo> mongoMap;
    
	public synchronized static DB getDB(ExecuteParams wiParams, String db) 
	throws Exception {
		if (mongoMap == null) {
			mongoMap = Collections.synchronizedMap(new HashMap());
		}
		Mongo mongo = mongoMap.get(db);
		if (mongo == null) {
			Host host = wiParams.getProject().getHosts().getHost("mongodb");
			MongoOptions options = new MongoOptions();
			options.connectionsPerHost = 20;
			mongo = new Mongo(host.getAddress(), options);
			char[] pass = host.getPass().toCharArray();
			mongo.getDB("admin").authenticate(host.getUser(), pass);
			mongoMap.put(db, mongo);
		}
		return mongo.getDB(db);
	}
	
	public static void fileMetadata(Map meta, File file) throws Exception {
		meta.put("length", file.length());
		FileInputStream fIn = new FileInputStream(file);
		meta.put("md5", DigestUtils.md5Hex(fIn));
		meta.put("sha1", DigestUtils.sha1Hex(fIn));
		fIn.close();
	}
	
	public static void ensureIndex(DBCollection dbc, String name) 
	throws Exception {
		BasicDBObject key = new BasicDBObject(name, 1);
		if (name.equals("_regid")) {
			dbc.ensureIndex(key, "_regid_", true);
		} else {
			dbc.ensureIndex(key);
		}
	}
	
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
