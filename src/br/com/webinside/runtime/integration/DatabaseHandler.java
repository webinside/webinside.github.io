/*
 * WEBINSIDE - Ferramenta de produtividade Java
 * Copyright (c) 2011-2012 LINEWEB Soluções Tecnológicas Ltda.
 * Copyright (c) 2009-2010 Incógnita Inteligência Digital Ltda.
 *
 * Este programa é software livre; você pode redistribuí-lo e/ou modificá-lo 
 * sob os termos da GNU LESSER GENERAL PUBLIC LICENSE (LGPL) conforme publicada 
 * pela Free Software Foundation; versão 2.1 da Licença.
 * Este programa é distribuído na expectativa de que seja útil, porém, SEM 
 * NENHUMA GARANTIA; nem mesmo a garantia implícita de COMERCIABILIDADE OU 
 * ADEQUAÇÃO A UMA FINALIDADE ESPECÍFICA.
 * 
 * Consulte a GNU LGPL para mais detalhes.
 * Você deve ter recebido uma cópia da GNU LGPL junto com este programa; se não, 
 * veja em http://www.gnu.org/licenses/ 
 */

package br.com.webinside.runtime.integration;

import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletContext;

import br.com.webinside.runtime.component.AbstractProject;
import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.database.DatabaseConnection;
import br.com.webinside.runtime.database.DatabaseManager;
import br.com.webinside.runtime.database.ErrorCode;
import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.database.ResultSetJava;
import br.com.webinside.runtime.database.impl.ConnectionMjava;
import br.com.webinside.runtime.database.impl.ConnectionSql;
import br.com.webinside.runtime.util.FileIO;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;
import br.com.webinside.runtime.util.WIVersion;

/**
 * DOCUMENT ME!
 * 
 * @author $author$
 * @version $Revision: 1.4 $
 */
public class DatabaseHandler extends DatabaseManager {

	private AbstractProject project;
	private String id = "";
	private String sqlFilterIn = "";
	private String sqlFilterOut = "";
	private String executedSql = "";
	private Producer producer = new Producer();
	private String logType = "";

	/**
	 * Creates a new DatabaseGeneric object.
	 * 
	 * @param type
	 *            DOCUMENT ME!
	 * @param alias
	 *            DOCUMENT ME!
	 * @param user
	 *            DOCUMENT ME!
	 * @param pass
	 *            DOCUMENT ME!
	 */
	public DatabaseHandler(String id, String type, String alias, String user,
			String pass) {
		super(type, alias, user, pass);
		if (id != null) {
			this.id = id;
		}
	}

	/**
	 * Creates a new DatabaseGeneric object.
	 * 
	 * @param database
	 *            DOCUMENT ME!
	 */
	public DatabaseHandler(String id, DatabaseManager database) {
		super(database);
		if (id != null) {
			this.id = id;
		}
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public DatabaseHandler cloneMe() {
		DatabaseHandler clone = new DatabaseHandler(id, this);
		clone.setLog(logType);
		return clone;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param type DOCUMENT ME!
	 */
	public void setLog(String type) {
		if (type == null) {
			return;
		}
		if (!type.equals("SIMPLE") && !type.equals("FULL")) {
			type = "";
		}
		logType = type;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param project DOCUMENT ME!
	 */
	public void setProject(AbstractProject project) {
		this.project = project;
	}	
	
	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public String getExecutedSQL() {
		return executedSql;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param in
	 *            DOCUMENT ME!
	 * @param out
	 *            DOCUMENT ME!
	 */
	public void setCharFilter(String in, String out) {
		if (in == null) {
			in = "";
		}
		if (out == null) {
			out = "";
		}
		sqlFilterIn = in;
		sqlFilterOut = out;
	}

	// Retorna todos os registros
	public ResultSet execute(String query, WIMap wiMap) throws Exception {
		return execute(query, wiMap, 1, 0);
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param query
	 *            DOCUMENT ME!
	 * @param wiMap
	 *            DOCUMENT ME!
	 * @param from
	 *            DOCUMENT ME!
	 * @param size
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	public ResultSet execute(String query, WIMap wiMap, int from, int size)
			throws Exception {
		if (from < 0) from = 1;
		if (size < 0) size = 0;
		ResultSet resp = null;
		if (wiMap == null) {
			wiMap = new WIMap();
		}
		wiMap.put("wi.db.id", getId());
		String type = getDatabaseConnection().getType();
		query = filterQuery(wiMap, query);
	    String tname = Thread.currentThread().getName();
        String antname = StringA.piece(StringA.piece(tname, "^", 2), "-", 3);
        String queryThread = query.trim() + " - " + antname.trim();
	    Function.setThreadName(threadTitle() + ", Query:" + queryThread);
		if (type.equals("JAVA")) {
			String clazz = StringA.piece(query, ":", 1).trim();
			String params = StringA.piece(query, ":", 2, 0).trim();
			Class c = getUserClass(wiMap, clazz);
			resp = (ResultSetJava) c.newInstance();
			((ResultSetJava)resp).execute(project, wiMap, params);
		} else if (type.equals("MJAVA")) {
			if (getVersion().startsWith(WIVersion.MJAVAVERSION)) {
				executedSql = mjavaProduce(query, wiMap);
				ConnectionMjava mjava = 
					(ConnectionMjava) getDatabaseConnection();
				mjava.setFilter(sqlFilterIn);
				resp = mjava.execute(executedSql, wiMap, from, size);
			}
		} else {
			// Usado para JDBC
			ProducerParam prod = new ProducerParam();
			prod.setWIMap(wiMap);
			prod.setCharFilter(sqlFilterIn, sqlFilterOut);
			prod.setInput(query);
			producer.setParam(prod);
			prod.addProtectedPipe("?||");
			producer.execute();
			executedSql = prod.getOutput().trim();
			ConnectionSql sql = 
				(ConnectionSql) getDatabaseConnection();
			if (query.trim().toLowerCase()
					.startsWith("|$wi.listmetastruct")) {
				String metatable = StringA.piece(query, "(", 2).trim();
				metatable = StringA.piece(metatable, ")", 1).trim();
				prod.setInput(metatable);
				producer.execute();
				resp = sql.listMetaStruct(prod.getOutput().trim());
			} else if (query.trim().toLowerCase()
					.startsWith("|$wi.listmeta")) {
				String metatable = StringA.piece(query, "(", 2).trim();
				metatable = StringA.piece(metatable, ")", 1).trim();
				prod.setInput(metatable);
				producer.execute();
				resp = sql.listMeta(prod.getOutput().trim());
			} else {
				String tquery = query.trim().toLowerCase(); 
				if (tquery.startsWith("|$wi.longtextcolumns")) {
					int pos = tquery.indexOf("$|");
					if (pos == -1) {
						pos = tquery.length();
					}
					String func = StringA.mid(tquery, 0, pos + 1);
					int p1 = func.indexOf("(");
					int p2 = func.indexOf(")");
					String value = StringA.mid(func, p1 + 1, p2 - 1); 
					wiMap.put("wi.sql.longtextcolumns", value);
				}
				resp = sql.execute(executedSql, wiMap);
			}	
		}
		sqlValid();
		sqlLog(wiMap, true, logType);
	    Function.setThreadName(tname);    
		wiMap.remove("wi.db.id");
		return resp;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param query
	 *            DOCUMENT ME!
	 * @param wiMap
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	public int executeUpdate(String query, WIMap wiMap) throws Exception {
		int resp = -1;
		if (wiMap == null) {
			wiMap = new WIMap();
		}
		wiMap.put("wi.db.id", getId());
		String type = getDatabaseConnection().getType();
		query = filterQuery(wiMap, query);
	    String tname = Thread.currentThread().getName();
        String antname = StringA.piece(StringA.piece(tname, "^", 2), "-", 3);
        String queryThread = query.trim() + " - " + antname.trim();
	    Function.setThreadName(threadTitle() + ", Update:" + queryThread);    
		if (type.equals("MJAVA")) {
			if (getVersion().startsWith(WIVersion.MJAVAVERSION)) {
				executedSql = mjavaProduce(query, wiMap);
				ConnectionMjava mjava = 
					(ConnectionMjava) getDatabaseConnection();
				mjava.setFilter(sqlFilterIn);
				resp = mjava.executeUpdate(executedSql, wiMap);
			}
		} else {
			ProducerParam prod = new ProducerParam();
			prod.setWIMap(wiMap);
			prod.setCharFilter(sqlFilterIn, sqlFilterOut);
			prod.setInput(query);
			producer.setParam(prod);
			prod.addProtectedPipe("?||");
			producer.execute();
			executedSql = prod.getOutput().trim();
			ConnectionSql sql = (ConnectionSql) getDatabaseConnection();
			resp = sql.executeUpdate(executedSql, wiMap);
		}
		sqlValid();
		sqlLog(wiMap, false, logType);
		updateLog(wiMap, true);
	    Function.setThreadName(tname);    
		wiMap.remove("wi.db.id");
		return resp;
	}

	/**
	 * DOCUMENT ME!
	 * 
	 * @param in
	 *            DOCUMENT ME!
	 * @param query
	 *            DOCUMENT ME!
	 * @param wiMap
	 *            DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 * 
	 * @throws Exception
	 *             DOCUMENT ME!
	 */
	public int executeUpdate(InputStream in, String query, WIMap wiMap)
			throws Exception {
		int tipo = 0;
		if (getDatabaseConnection() instanceof ConnectionSql) tipo = 1;
		int resp = -1;
		if (wiMap == null) {
			wiMap = new WIMap();
		}
		wiMap.put("wi.db.id", getId());
		query = filterQuery(wiMap, query);
	    String tname = Thread.currentThread().getName();
        String antname = StringA.piece(StringA.piece(tname, "^", 2), "-", 3);
	    Function.setThreadName(threadTitle() + ", Binary - " + antname.trim());    
		ProducerParam prod = new ProducerParam();
		prod.setWIMap(wiMap);
		prod.setCharFilter(sqlFilterIn, sqlFilterOut);
		prod.setInput(query);
		producer.setParam(prod);
		if (tipo == 1) {
			prod.addProtectedPipe("?||");
			producer.execute();
			executedSql = prod.getOutput().trim();
			ConnectionSql sql = 
				(ConnectionSql) getDatabaseConnection();
			resp = sql.executeUpdate(in, executedSql, wiMap);
		}
		sqlValid();
	    sqlLog(wiMap, false, logType);
		updateLog(wiMap, true);
	    Function.setThreadName(tname);    
		wiMap.remove("wi.db.id");
		return resp;
	}

    private String mjavaProduce(String txt, WIMap wiMap) {
	  	StringA resp = new StringA(mjavaCleanSpace(txt));
	    if (resp.toString().charAt(0) == '|') {
	    	int last = resp.indexOf("|",1);
	    	if (last == -1) {
	    		last = resp.length();
	    	}
	    	String p1 = resp.mid(0, last).trim();
	    	String p2 = resp.mid(last + 1, resp.length()).trim();
	        resp.set(mjavaCleanSpace(Producer.execute(wiMap, p1).trim()));
	        if (!p2.equals("")) {
	        	resp.append(p2);
	        }        
	    }
	    return resp.toString();
    }	
		
	private String mjavaCleanSpace(String txt) {
		StringA resp = new StringA();
		boolean remspc = true;
		if (txt == null) {
			txt = new String();
		}
		for (int i = 0; i < txt.length(); i++) {
			char let = txt.charAt(i);
			if (let == '=') {
				remspc = false;
			}
			if (let == ',') {
				remspc = true;
			}
			if ((let != ' ') && (let != '\r') && (let != '\n')) {
				resp.append(let);
			}
			if ((let == ' ') && (!remspc)) {
				resp.append(let);
			}
		}
		return resp.toString();
	}
	
	public void updateLog(WIMap wiMap, boolean ok) {
		if (project == null || wiMap.get("wi.updatelog").equals("true") || 
				wiMap.get("wi.builder").equals("true")) {
			return;
		}
		String database = project.getDBLogDatabase();
		String table = project.getDBLogTable(); 
		if (!database.equals("") && !table.equals("")) {
			StringBuffer keys = new StringBuffer();
			StringBuffer values = new StringBuffer();
			if (ok) wiMap.put("wi.sql.status", "T");
			else wiMap.put("wi.sql.status", "F");
			wiMap.put("wi.db.id", getId());
			wiMap.put("wi.sql.query", executedSql);
			wiMap.put("wi.sql.params", updateLogSqlParams(wiMap, executedSql));
			WIMap auxMap = new WIMap();
			int pos = 0;
			Iterator it = project.getDBLogColumnsMap().entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry entry = (Entry) it.next();
				pos ++;
				String col = (String) entry.getKey();
				if (col.equalsIgnoreCase("id")) continue;
				if (keys.length() > 0) keys.append(",");
				keys.append(col);
				int len = auxMap.keySet().size();
				String value = (String) entry.getValue();
				auxMap.put("par" + (len + 1), Producer.execute(wiMap, value));
				if (values.length() > 0) values.append(",");
				String type = "";
				if (pos == 2) type = "[dat]";
				if (pos > 5) type = "[asc]";
				values.append("?" + type + "|par" + (len + 1) + "|");
			}
			String query = "insert into " + table + " (" + keys.toString() + 
				") values (" + values.toString() + ")";
			ExecuteParams wiParams = ExecuteParams.get();
			DatabaseAliases aliases = wiParams.getDatabaseAliases();
			// Se a conexão não for clonada e der erro o rollback remove o log
			DatabaseHandler handler = aliases.get(database).cloneMe();
			connectionOpenOrClose(wiParams, true);
			String debug = wiMap.get("pvt.updatelog.debugdir").trim();
			try {
				int h = handler.connect();
				if (h == ErrorCode.NOERROR) {
					auxMap.put("wi.updatelog", "true");
					handler.executeUpdate(query, auxMap);
				}	
			} catch (Exception err) {
	            SimpleDateFormat sdf = 
	            	new SimpleDateFormat("yyyyMMddHHmmssSSSS");
	            String debugId = "updateLog-" + sdf.format(new Date());
				handler.getErrorLog().write(getClass().getName(), debugId, err);
				if (!debug.equals("")) {
		        	try {
		        		auxMap.remove("wi.db.id");
		        		auxMap.remove("wi.updatelog");
		        		File f = new File(debug, debugId + ".log");
		        		FileIO io = new FileIO(f.getAbsolutePath(), 'W');
		        		io.writeln(query);
		        		io.writeln(auxMap.toString());
		            	io.close();
		            } catch (Exception ioex) {
		            	// ignorado
		            }
				}
			} finally {
				connectionOpenOrClose(wiParams, false);
				handler.close();
			}
			wiMap.remove("wi.db.id");
			wiMap.remove("wi.sql.status");
			if (ok) wiMap.remove("wi.sql.query");
			wiMap.remove("wi.sql.params");
		}
	}

	private String updateLogSqlParams(WIMap wiMap, String query) {
		Map params = new LinkedHashMap();
		int from = 0;
		int start = 0;
		while ((start = query.indexOf("?", from)) > -1) {
			String prox = StringA.mid(query, start + 1, start + 1);
			if (!prox.equals("|") && !prox.equals("[")) {
				from = start + 1;
				continue;
			}
			int pipe = start + 1;
			if (prox.equals("[")) pipe = start + 6; 
			int end = query.indexOf("|", pipe + 1);
			if (StringA.mid(query, pipe + 1, pipe + 1).equals("$")) {
				end = query.indexOf("$|", pipe + 2);
				if (end > -1) end ++;
			}
			if (end == -1) end = query.indexOf(" ", pipe + 1);
			if (end == -1) end = query.length() - 1;
			String param = StringA.mid(query, pipe, end);
			params.put(param, Producer.execute(wiMap, param));
			from = end + 1;
		}
		return params.toString();
	}
	
	private void sqlLog(WIMap wiMap, boolean select, String type) {
		if (type.equals("") || (wiMap == null) || getErrorLog() == null
				|| wiMap.get("wi.updatelog").equals("true")) {
			return;
		}
		String logDir = getErrorLog().getParentDir();
		LogsGenerator log = LogsGenerator.getInstance(logDir, "sql.log");
		String ip = wiMap.get("wi.session.ip");
		String page = wiMap.get("wi.jsp.filename");
		String tmsg = "select";
		if (!select) {
			tmsg = "update";
		}
		DatabaseConnection gen = getDatabaseConnection();
		String alias = gen.getAlias();
		String text = "DB: " + alias + " - Type: " + tmsg;
		String detail = null;
		if (type.equals("FULL")) {
	        detail = getExecutedSQL();
	        WIMap psMap = getExecutedSQLParams(wiMap); 
	        if (psMap.keySet().size() > 0) {
	        	detail += "\r\n--- PARAMS ---\r\n";
	        	detail += psMap.toString();
	        }	
		}
		log.write(page, null, ip, text, detail);
	}

	private void sqlValid() {
		ExecuteParams params = ExecuteParams.get();
		if (params != null && params.getWIMap() != null) {
			params.getWIMap().put("wi.sql.valid", getExecutedSQL());
		}
	}
	
	private String threadTitle() {
		String type = getDatabaseConnection().getType() + 
			"(" + getDatabaseConnection().getAlias() + ")"; 
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");	            
		return df.format(new Date()) + " - DB:" + type;
	}
	
	/**
	 * DOCUMENT ME!
	 * 
	 * @return DOCUMENT ME!
	 */
	public Producer getProducer() {
		return producer;
	}

	/**
	 * @return
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	private Class getUserClass(WIMap wiMap, String className)
			throws ClassNotFoundException {
		ServletContext sc = (ServletContext) wiMap.getObj("wi.servletcontext");
		String path = sc.getRealPath("/");
		path = StringA.mid(path, 0, path.length() - 2);
		if (!path.endsWith(wiMap.get("wi.proj.id"))) {
			sc = sc.getContext("/" + wiMap.get("wi.proj.id"));
		}
		if (sc != null) {
			ClassLoader cl = (ClassLoader) sc.getAttribute("classloader");
			if (cl != null) {
				return cl.loadClass(className);
			}
		}	
		return getClass().getClassLoader().loadClass(className);
	}
	
    public void connectionOpenOrClose(ExecuteParams wiParams, boolean open) {
    	String var = "open";
    	if (!open) var = "close";
    	if (wiParams != null && wiParams.getWIMap() != null) {
    		if (!open && !isConnected()) return;
    		String prefix = "pvt.dbconnection";
    		String key = prefix + "." + id + "." + var;
			String operation = wiParams.getWIMap().get(key).trim(); 
			if (!operation.equals("")) {
				try {
					WIMap auxMap = wiParams.getWIMap().cloneMe();
					auxMap.put("wi.updatelog", "true");
					executeUpdate(operation, auxMap);
				} catch (Exception e) {
					wiParams.getErrorLog().write(getClass().getName(), 
							var + "Connection", e);
				}
			}
    	}	
    }
	
    public String filterQuery(WIMap wiMap, String query) {
		String key = "pvt.dbconnection." + id + ".singleline";
		String singleline = wiMap.get(key).trim(); 
		if (singleline.equalsIgnoreCase("false")) {
			return query;
		}
		return StringA.changeChars(query, "\r\n", "  ");
    }

    public WIMap getExecutedSQLParams(WIMap map) {
    	return getExecutedSQLParams(map, "");
    }

    public WIMap getExecutedSQLParams(WIMap map, String funcText) {
    	WIMap auxMap = new WIMap();
    	if (map == null) {
    		return auxMap;
    	}
    	String sql = getExecutedSQL();
    	if (!funcText.equals("")) {
    		sql = funcText;
    	}
    	int from = 0;
    	while (from < sql.length()) {
    		String mark = (funcText.equals("") ? "?|" : "|");
    		int pos = sql.indexOf(mark, from);
    		if (pos > -1) {
    			int end = Function.lastPipePos(sql, pos + 1);
    			if (end > -1) {
    				String var = StringA.mid(sql, pos + mark.length(), end - 1);
    				String value = map.get(var);
    				if (var.startsWith("$")) {
    					value = Producer.execute(map, "|" + var + "|");
    					auxMap.putAll(getExecutedSQLParams(map, var).getAsMap());
    				}
					var = "|" + var + "|";
    				auxMap.put(var, value);
        			from = end + 1;
    			} else {
        			from = sql.length();
    			}
    		} else {
    			from = sql.length();
    		}
    	}
    	return auxMap;
    }
    
}
