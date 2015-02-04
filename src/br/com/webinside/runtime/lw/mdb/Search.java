package br.com.webinside.runtime.lw.mdb;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class Search {

	public static void main(String[] args) throws Exception {
		new Search().execute();
	}

	public void execute() throws Exception {
		Mongo mongo = new Mongo("localhost");
		DB mdb = mongo.getDB("lineweb");
		mdb.getSisterDB("admin").authenticate("admin", "adm153lw".toCharArray());
		DBCollection dbc = mdb.getCollection("tb_ficha");
		
		BasicDBObject search = new BasicDBObject();
		search.put("nota", new BasicDBObject("$gt", 3).append("$lte", 10));		
		DBCursor cursor = dbc.find(search).sort(new BasicDBObject().append("aluno", 1)).limit(2);
		while (cursor.hasNext()) {
			DBObject doc = cursor.next();
			System.out.println("->" + doc.get("aluno") + ": " + doc.get("nota"));
		}
		cursor.close();
	}

}
