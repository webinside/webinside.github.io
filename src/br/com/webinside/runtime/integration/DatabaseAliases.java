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

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.webinside.runtime.component.AbstractProject;
import br.com.webinside.runtime.component.Database;
import br.com.webinside.runtime.component.Databases;
import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.database.ErrorCode;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.4 $
 */
public class DatabaseAliases {
	private static final Map passwords = 
    	Collections.synchronizedMap(new HashMap());
    private ExecuteParams wiParams;
    private String logType = "";
    private Map databases = new HashMap();

    /**
     * DOCUMENT ME!
     *
     * @param params DOCUMENT ME!
     */
    public void setWIParams(ExecuteParams params) {
        wiParams = params;
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
     * @return DOCUMENT ME!
     */
    public Map getAliasesMap() {
    	Map resp = new HashMap();
    	Iterator it = new HashSet(databases.keySet()).iterator();
    	while (it.hasNext()) {
    		String id = (String)it.next();
    		DatabaseNode node = (DatabaseNode)databases.get(id);
			Map aux = new HashMap();
			String type = node.getDatabase().getDatabaseConnection().getType();
    		String alias = node.getDatabase().getDatabaseConnection().getAlias();
    		aux.put("type", type);
			aux.put("alias", alias);
			aux.put("user", node.getUser());
			aux.put("pass", node.getPass());
    		resp.put(id, aux);
    	}
        return resp;
    }

    private void loadDatabase(Database db, AbstractProject project) {
        if (db == null) {
            return;
        }
        if (databases == null) {
            databases = new HashMap();
        }
        String dbid = db.getId().trim();
        if (dbid.equals("")) {
            return;
        }
        String type = db.getType();
        String alias = db.getAlias();
        DatabaseHandler dbio =
            new DatabaseHandler(db.getId(), type, alias, "", "");
        dbio.setPoolPrefix(project.getId() + "-" + db.getId());
        dbio.setProject(project);
        if (wiParams != null) {
            dbio.setErrorLog(wiParams.getErrorLog());
        }
        dbio.setLog(logType);
        DatabaseNode node = new DatabaseNode();
        node.setDatabase(dbio);
        node.setUser(db.getUser());
        node.setPass(recoverPass(db, project.getId()));
        node.setMax(db.getMaxConnections());
        int ctime = 0;
        try { 
            ctime = Integer.parseInt(db.getConnectionTimeout().trim());
        } catch (NumberFormatException err) {
        	// ignorado
        }
        node.setConnectionTimeout(ctime);
        int ltime = 0;
        try {
            ltime = Integer.parseInt(db.getLoginTimeout());
        } catch (NumberFormatException err) {
        	// ignorado
        }
        node.setLoginTimeout(ltime);
        int qtime = 0;
        try {
            qtime = Integer.parseInt(db.getQueryTimeout());
        } catch (NumberFormatException err) {
        	// ignorado
        }
        node.setQueryTimeout(qtime);
        databases.put(dbid, node);
    }

    /**
     * DOCUMENT ME!
     *
     * @param proj DOCUMENT ME!
     */
    public void loadDatabases(AbstractProject project) {
        databases = new HashMap();
        if (project == null) {
            return;
        }
        Databases dbs = project.getDatabases();
        List set = dbs.get();
        for (Iterator i = set.iterator(); i.hasNext();) {
            Database db = (Database) i.next();
            loadDatabase(db, project);
        }
        Database dbJava = new Database("WI-JAVA");
        dbJava.setType("JAVA");
        loadDatabase(dbJava, project);
    }

    /**
     * DOCUMENT ME!
     *
     * @param alias DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public DatabaseHandler get(String alias) {
    	return get(alias, true);
    }	
    
    /**
     * DOCUMENT ME!
     *
     * @param alias DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public DatabaseHandler get(String alias, boolean forceConnect) {
        if (alias == null) {
            alias = "";
        }
        alias = alias.trim();
        if (!databases.containsKey(alias)) {
            return null;
        }
        DatabaseNode node = null;
        try {
            node = (DatabaseNode) databases.get(alias);
        } catch (ClassCastException err) {
        	// ignorado
        }
        if (node != null) {
            DatabaseHandler handler = node.getDatabase();
            if (handler != null) {
                if (!handler.isConnected() && forceConnect) {
                    connect(handler, node);
                }
                handler.runSqlOnOpenOrCloseConnection(wiParams, true);
                if (wiParams != null && wiParams.getWIMap() != null) {
                    WIMap wiMap = wiParams.getWIMap();
                    String maxrows = wiMap.get("tmp.dbmaxrows." + alias).trim();
                    if (!maxrows.equals("")) {
                    	int max = Function.parseInt(maxrows);
                    	handler.getDatabaseConnection().setMaxRows(max);
                    }
                }
                return handler;
            }
        }
        return null;
    }
    
    /**
     * DOCUMENT ME!
     */
    public void closeAll() {
    	doAll("close", true);
    }

    /**
     * DOCUMENT ME!
     */
    public void autocommitAll(boolean autocommit) {
    	doAll("autocommit", autocommit);
    }
    
    /**
     * DOCUMENT ME!
     */
    public void commitAll() {
    	doAll("commit", true);
    }
    
    /**
     * DOCUMENT ME!
     */
    public void rollbackAll() {
    	doAll("rollback", true);
    }
    
    private void doAll(String type, boolean autocommit) {
        Iterator it = databases.keySet().iterator();
        while (it.hasNext()) {
            String wialias = (String) it.next();
            DatabaseNode node = (DatabaseNode) databases.get(wialias);
            DatabaseHandler handler = node.getDatabase();
            if (handler != null) {
            	if (type.equals("close")) {
                    handler.runSqlOnOpenOrCloseConnection(wiParams, false);
            		handler.close();
            		for (DatabaseHandler dbhClone : handler.getCloneList()) {
            			dbhClone.runSqlOnOpenOrCloseConnection(wiParams, false);
            			dbhClone.close();
					}
            	}
            	if (type.equals("autocommit")) {
            		handler.setInTransaction(!autocommit);
            		handler.autocommit(autocommit);
            	}
            	if (handler.isConnected()) {
	            	if (type.equals("commit")) {
	            		handler.commit();
	            	}
	            	if (type.equals("rollback")) {
	            		handler.rollback();
	            	}
            	}	
            }
        }
    }

    private void connect(DatabaseHandler db, DatabaseNode node) {
        WIMap wiMap = null;
        if (wiParams != null) {
            wiMap = wiParams.getWIMap();
            if (wiMap != null) {
                wiMap.put("wi.sql.valid", "");
                wiMap.put("wi.sql.query", "");
                wiMap.put("wi.sql.error", "");
            }
        }
        if (wiMap == null) {
            wiMap = new WIMap();
        }
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        String dbalias = db.getDatabaseConnection().getAlias().trim();
        String user = node.getUser().trim();
        String pass = node.getPass().trim();
        if (wiParams != null) {
            wiParams.getProducer().setParam(prod);
            prod.setInput(dbalias);
            wiParams.getProducer().execute();
            dbalias = prod.getOutput().trim();
            prod.setInput(user);
            wiParams.getProducer().execute();
            user = prod.getOutput().trim();
            prod.setInput(pass);
            wiParams.getProducer().execute();
            pass = prod.getOutput().trim();
        }
        db.getDatabaseConnection().setAlias(dbalias);
        db.getDatabaseConnection().setUser(user);
        db.getDatabaseConnection().setPass(pass);
        db.getDatabaseConnection().setLoginTimeout(node.getLoginTimeout());
        db.getDatabaseConnection().setQueryTimeout(node.getQueryTimeout());
        db.getDatabaseConnection().setConnectionTimeout(
        		node.getConnectionTimeout());
        db.getDatabaseConnection().setMaxConnections(node.getMaxConnections());
        int sts = db.connect(node.getMaxConnections());
        if (sts == ErrorCode.MAXCONNECTIONS) {
            if (wiMap != null) {
                wiMap.put("wi.sql.error", "DB(-2): Too many connections");
            }
            return;
        }
        if (!db.isConnected()) {
            if (wiMap != null) {
                wiMap.put("wi.sql.error", "DB(-1): " + db.getErrorMessage());
            }
        }
    }

    private String recoverPass(Database db, String projID) {
        boolean doit = false;
        if (projID.equals("")) {
            projID = "proj";
        }
        String dbid = db.getId().trim();
        String passkey = projID + "-" + dbid;
        String pass = "";
        String passenc = db.getPassEnc();
        if (passwords.containsKey(passkey)) {
            String fullpass = (String) passwords.get(passkey);
            String p1 = StringA.piece(fullpass, "<>", 1);
            pass = StringA.piece(fullpass, "<>", 2);
            if (!p1.equals(passenc)) {
                doit = true;
            }
        } else {
            doit = true;
        }
        if (doit) {
            pass = db.getPass();
            passwords.put(passkey, passenc + "<>" + pass);
        }
        return pass;
    }
    
}
