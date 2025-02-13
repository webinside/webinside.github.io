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

package br.com.webinside.runtime.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.webinside.runtime.component.DriversDef;
import br.com.webinside.runtime.component.JdbcAlias;
import br.com.webinside.runtime.component.JdbcAliases;
import br.com.webinside.runtime.database.impl.ConnectionJava;
import br.com.webinside.runtime.database.impl.ConnectionMjava;
import br.com.webinside.runtime.database.impl.ConnectionSql;
import br.com.webinside.runtime.util.ErrorLog;
import br.com.webinside.runtime.util.StringA;

/**
 * Database Connection Manager
 *
 * @author Geraldo Moraes
 * @date 2009-10-05
 */
public abstract class DatabaseManager {

    private static final Map<String, Integer> aliasRoundRobin = 
    	Collections.synchronizedMap(new HashMap());
    
	private String realAlias;
    private DatabaseConnection dbgeneric;
    private String poolPrefix;
	private boolean inTransaction;
	private boolean noConnectRetry;
	
    /**
     * Creates a new DatabaseManager object.
     *
     * @param type DOCUMENT ME!
     * @param alias DOCUMENT ME!
     * @param user DOCUMENT ME!
     * @param pass DOCUMENT ME!
     */
    public DatabaseManager(String type, String alias, String user, String pass) {
    	realAlias = (alias == null ? "" : alias);
    	if (type == null) type = "";
        type = type.trim().toUpperCase();
        if (type.equals("JAVA")) {
            dbgeneric = new ConnectionJava();
        } else if (type.equals("MJAVA")) {
            dbgeneric = new ConnectionMjava(type, alias, user, pass);
        } else {
            dbgeneric = new ConnectionSql(type, alias, user, pass);
        }
        if (dbgeneric != null) {
            dbgeneric.setParent(this);
        }
    }

    /**
     * Creates a new DatabaseManager object.
     *
     * @param database DOCUMENT ME!
     */
    public DatabaseManager(DatabaseManager database) {
        if ((database != null) && (database.dbgeneric != null)) {
        	realAlias = database.realAlias;
            dbgeneric = database.dbgeneric.cloneMe();
            dbgeneric.setParent(this);
            poolPrefix = database.poolPrefix;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param prefix DOCUMENT ME!
     */
    public void setPoolPrefix(String prefix) {
        poolPrefix = prefix;
    }

    public void setInTransaction(boolean inTransaction) {
		this.inTransaction = inTransaction;
	}

    public void setNoConnectRetry(boolean noConnectRetry) {
    	this.noConnectRetry = noConnectRetry;
    }
    
	/**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ErrorLog getErrorLog() {
        return dbgeneric.getErrorLog();
    }

    /**
     * DOCUMENT ME!
     *
     * @param errorLog DOCUMENT ME!
     */
    public void setErrorLog(ErrorLog errorLog) {
        dbgeneric.setErrorLog(errorLog);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int connect() {
        return connect(dbgeneric.getMaxConnections());
    }

    /**
     * DOCUMENT ME!
     *
     * @param maxConnections DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int connect(int maxConnections) {
        if (maxConnections < 0) {
            maxConnections = 0;
        }
        if (dbgeneric.getType().equals("")) {
            return ErrorCode.INVALIDTYPE;
        }
        if (isConnected()) {
            return ErrorCode.NOERROR;
        }
        if (dbgeneric.isPooled() && (dbgeneric.getConnectionTimeout() > 0)) {
            // usando o pool do WI
            int count = 0;
            while (count < 3) {
                int status = getConnection(maxConnections);
                if (status == ErrorCode.MAXCONNECTIONS) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                    	// ignorado
                    }
                } else {
                    return status;
                }
                count++;
            }
            return ErrorCode.MAXCONNECTIONS;
        } else {
            // usando conexão direta
        	return dbGenericConnect();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static boolean isNativeInstalled(String type) {
        if (type == null) {
            type = "";
        }
        if (type.equals("DATASOURCE")) {
            return true;
        }
        if (type.equals("ODBC")) {
            return true;
        }
        if (type.equals("MJAVA")) {
            return true;
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isConnected() {
        return dbgeneric.isConnected();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getErrorMessage() {
        return dbgeneric.getErrorMessage();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getVersion() {
        return dbgeneric.getVersion();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] listMetas() {
        return dbgeneric.listMetas();
    }

    /**
     * DOCUMENT ME!
     *
     * @param meta DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ResultSet listMeta(String meta) {
        return dbgeneric.listMeta(meta);
    }

    /**
     * DOCUMENT ME!
     *
     * @param metalist DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ResultSet listMeta(String[] metalist) {
        return dbgeneric.listMeta(metalist);
    }

    /**
     * DOCUMENT ME!
     *
     * @param table DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ResultSet listMetaStruct(String table) {
        return dbgeneric.listMetaStruct(table);
    }

    /**
     * DOCUMENT ME!
     *
     * @param status DOCUMENT ME!
     */
    public void autocommit(boolean status) {
        dbgeneric.autocommit(status);
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param status DOCUMENT ME!
     */
    public boolean isAutocommit() {
       return dbgeneric.isAutocommit();
    }

    /**
     * DOCUMENT ME!
     */
    public void commit() {
        dbgeneric.commit();
    }

    /**
     * DOCUMENT ME!
     */
    public void rollback() {
        dbgeneric.rollback();
    }

    /**
     * DOCUMENT ME!
     */
    public void clear() {
        dbgeneric.clear();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public DatabaseConnection getDatabaseConnection() {
        return dbgeneric;
    }

    /**
     * DOCUMENT ME!
     */
    public void close() {
        clear();
        dbgeneric.setParent(null);
        boolean close = true;
        if (dbgeneric.getConnectionTimeout() > 0) {
            synchronized (DatabaseThread.databasePool) {
            	DatabaseThread.threadName = 
            		"close pool - " + Thread.currentThread().getName();
                List connections =
                    (List) DatabaseThread.databasePool.get(getKeyName());
                if (connections != null) {
            		Iterator it = new ArrayList(connections).iterator();            
                    while (it.hasNext()) {
                    	DatabaseThreadNode node = (DatabaseThreadNode) it.next();
                        if (dbgeneric.equals(node.getDatabaseConnection())) {
                        	if (dbgeneric.isValid()) {
	                            int minutes = dbgeneric.getConnectionTimeout();
	                            node.setMaxTime(maxExpireTime(minutes));
	                            node.setInUse(false);
                            	close = false;
                        	} else {
                                ErrorLog log = dbgeneric.getErrorLog();
                                if (log != null) {
                                	String err = "DBPool Close was invalid";
                                    log.write("DatabaseManager", getKeyName(), err);
                                }
                            	connections.remove(node);
                            	node.getDatabaseConnection().close();
                        	}	
                            break;
                        }
                    }
                }
            }
        }
        if (close && dbgeneric.isConnected()) {
           	dbgeneric.close();
        }
    }

    private int getConnection(int max) {
    	DatabaseThreadNode node = null;
    	synchronized (DatabaseThread.databasePool) {
        	DatabaseThread.threadName = 
        		"get pool - " + Thread.currentThread().getName();
            List connections = 
            	(List) DatabaseThread.databasePool.get(getKeyName());
            if (connections == null) {
                connections = new ArrayList();
                DatabaseThread.databasePool.put(getKeyName(), connections);
            }
    		Iterator it = new ArrayList(connections).iterator();            
            while (it.hasNext()) {
            	DatabaseThreadNode aux = (DatabaseThreadNode) it.next();
                if (!aux.isInUse()) {
                	node = aux;
	                dbgeneric = node.getDatabaseConnection();
	                dbgeneric.setMaxRows(DatabaseConnection.DEFAULT_MAX_ROWS);
	                dbgeneric.setParent(this);	                
	                node.setMaxTime(maxExpireTime(10));	
	                node.setInUse(true);
	                break;
                }
            }
            if ((node == null) && (max > 0) && (connections.size() >= max)) {
                String err = "Too many connections";
                ErrorLog log = dbgeneric.getErrorLog();
                if (log != null) {
                    log.write("DatabaseManager", getKeyName(), err);
                }
                return ErrorCode.MAXCONNECTIONS;
            }
        }         
    	DatabaseThread.threadName = 
    		"get pool validate - " + Thread.currentThread().getName();
        if (node != null) {
            if (isValidPoolConnection()) {
                return ErrorCode.NOERROR;
            } else {
                synchronized (DatabaseThread.databasePool) {
                    List connections = 
                    	(List) DatabaseThread.databasePool.get(getKeyName());            
                    ErrorLog log = dbgeneric.getErrorLog();
                    if (log != null) {
                    	String err = "DBPool GetConnection was invalid";
                        log.write("DatabaseManager", getKeyName(), err);
                    }
                	connections.remove(node);
                	node.getDatabaseConnection().close();
                }    
            }        	
        }
    	DatabaseThread.threadName = 
    		"get new - " + Thread.currentThread().getName();
        int status = dbGenericConnect();
        if (status == ErrorCode.NOERROR) {
            node = new DatabaseThreadNode(dbgeneric);
            node.setMaxTime(maxExpireTime(10));	
            node.setInUse(true);
            synchronized (DatabaseThread.databasePool) {
                List connections = 
                	(List) DatabaseThread.databasePool.get(getKeyName()); 
                if ((max == 0) || (connections.size() < max)) {
                	connections.add(node);
                } else {
                	dbgeneric.setConnectionTimeout(0);
                }
            }    
        }
        return status;
    }

    private boolean isValidPoolConnection() {
        if (dbgeneric.isConnected()) {
	        try {
	            if (dbgeneric instanceof ConnectionSql) {
	                Connection con = 
	                	((ConnectionSql) dbgeneric).getConnection();
	                con.setAutoCommit(!inTransaction);
	                JdbcAliases aliases = DriversDef.getJDBCAliases();
	                JdbcAlias alias = aliases.get(dbgeneric.getType());
	                if (alias != null) {
		                String validation = alias.getValidationQuery().trim();
		                if (!validation.equals("")) {
			                Statement stmt = con.createStatement();
		                    stmt.executeQuery(validation);
			                stmt.close();
		                }
	                }     
	            }
	            return true;
	        } catch (Exception err) {
	        	dbgeneric.setParent(null);
	        	dbgeneric.close();
	        }
        } 
        return false;
    }

    private String getKeyName() {
        String name = dbgeneric.getType() + "-" + realAlias;
        if ((poolPrefix != null) && !poolPrefix.equals("")) {
            name = poolPrefix + "-" + name;
        }
        if (!dbgeneric.getUser().trim().equals("")) {
            name = name + "-" + dbgeneric.getUser().trim();
        }
        return name;
    }
    
    private long maxExpireTime(int minutes) {
        long now = new java.util.Date().getTime();
        return now + (1000 * 60 * minutes);
    }

    private void checkAliasRoundRobin() {
    	if (realAlias.indexOf("[") > -1) {
    		int ini = realAlias.indexOf("[");
    		int end = realAlias.indexOf("]", ini);
    		if (end == -1) {
    			end = realAlias.length();
    		}
    		String p1 = StringA.mid(realAlias, 0, ini - 1);
    		String p2 = StringA.mid(realAlias, end + 1, realAlias.length());
    		String rrAlias = StringA.mid(realAlias, ini + 1, end - 1);
    		String[] split = rrAlias.split(",");
    		int position = 0;
    		if (aliasRoundRobin.containsKey(realAlias)) {
    			position = aliasRoundRobin.get(realAlias);
    			if (position >= split.length) {
    				position = 0;
    			}
    		}
    		dbgeneric.setAlias(p1 + split[position].trim() + p2);
    		aliasRoundRobin.put(realAlias, position + 1);
    	}
    }
    
    private int dbGenericConnect() {
    	int resp = -99, count = 0;
    	int maxCount = (noConnectRetry ? 1 : 5);
        while (resp != ErrorCode.NOERROR && count < maxCount) {
        	checkAliasRoundRobin();
        	resp = dbgeneric.connect();
            count++;
        }
    	autocommit(!inTransaction);
    	return resp;
    }
        
    public static String[] showThreadPoolKeys() throws IOException {
    	List resp = new ArrayList();
		Map pool = DatabaseThread.databasePool;
    	synchronized (pool) {
    		for (Iterator it = pool.keySet().iterator(); it.hasNext();) {
				resp.add(it.next());
			}
        }         
    	return (String[])resp.toArray(new String[resp.size()]);
    }

    public static DatabaseThreadNode[] showConnections(String key) 
    throws IOException {
    	List resp = new ArrayList();
		Map pool = DatabaseThread.databasePool;
    	synchronized (DatabaseThread.databasePool) {
    		List connectionList = (List)pool.get(key);
    		if (connectionList == null) {
    			connectionList = new ArrayList();
    		}
    		for (Iterator it = connectionList.iterator(); it.hasNext();) {
				resp.add(it.next());
			}
        }    
    	return (DatabaseThreadNode[])resp.toArray(new DatabaseThreadNode[resp.size()]);
    }

    public static void closePoolConnetions() {
		Map pool = DatabaseThread.databasePool;
    	synchronized (pool) {
    		for (Iterator it = pool.keySet().iterator(); it.hasNext();) {
    			String poolKey = (String)it.next();
        		List connectionList = (List)pool.get(poolKey);
        		if (connectionList == null) {
        			connectionList = new ArrayList();
        		}
        		for (Iterator it2 = connectionList.iterator(); it2.hasNext();) {
        			DatabaseThreadNode node = (DatabaseThreadNode) it2.next();
        			node.getDatabaseConnection().close();
    			}
			}
    		pool.clear();
        }         
    }
    
    public boolean isIntersys() {
    	if (dbgeneric != null) return dbgeneric.isIntersys();
    	return false;
    }
    
}
