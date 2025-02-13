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

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.database.impl.ConnectionSql;
import br.com.webinside.runtime.util.ErrorLog;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public abstract class DatabaseConnection {
    private DatabaseManager parent;
    private String type;
    private String alias;
    private String user;
    private String pass;
    private int loginTimeout;
    private int queryTimeout;
    private int connectionTimeout;
    private int maxConnections;
    private int maxRows;
    private String errorMessage = "";
    private ErrorLog errorLog;
    private boolean valid = true;
    /** DOCUMENT ME! */
    public static final int DEFAULT_MAX_ROWS = 50000;

    /**
     * Creates a new DatabaseConnection object.
     *
     * @param type DOCUMENT ME!
     * @param alias DOCUMENT ME!
     * @param user DOCUMENT ME!
     * @param pass DOCUMENT ME!
     */
    public DatabaseConnection(String type, String alias, String user, String pass) {
        if (type == null) {
            type = "";
        }
        this.type = type.trim().toUpperCase();
        if (alias == null) {
            alias = "";
        }
        this.alias = alias;
        if (user == null) {
            user = "";
        }
        this.user = user;
        if (pass == null) {
            pass = "";
        }
        this.pass = pass;
        queryTimeout = 0;
        connectionTimeout = 0;
        maxConnections = 0;
        maxRows = DEFAULT_MAX_ROWS;
    }

    /**
     * DOCUMENT ME!
     *
     * @param db DOCUMENT ME!
     */
    protected void load(DatabaseConnection db) {
    	setLoginTimeout(db.getLoginTimeout());
    	setQueryTimeout(db.getQueryTimeout());
        setConnectionTimeout(db.getConnectionTimeout());
        setMaxConnections(db.getMaxConnections());
        setMaxRows(db.getMaxRows());
        setErrorLog(db.getErrorLog());
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     */
    public void setType(String type) {
        if (type == null) {
            type = "";
        }
        this.type = type.trim().toUpperCase();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getType() {
        return type;
    }

    /**
     * DOCUMENT ME!
     *
     * @param alias DOCUMENT ME!
     */
    public void setAlias(String alias) {
        if (alias == null) {
            alias = "";
        }
        this.alias = alias;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getAlias() {
        return alias;
    }

    /**
     * DOCUMENT ME!
     *
     * @param user DOCUMENT ME!
     */
    public void setUser(String user) {
        if (user == null) {
            user = "";
        }
        this.user = user;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getUser() {
        return user;
    }

    /**
     * DOCUMENT ME!
     *
     * @param pass DOCUMENT ME!
     */
    public void setPass(String pass) {
        if (pass == null) {
            pass = "";
        }
        this.pass = pass;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected String getPass() {
        return pass;
    }

    /**
     * DOCUMENT ME!
     *
     * @param minutes DOCUMENT ME!
     */
    public void setConnectionTimeout(int minutes) {
        if (minutes < 0) {
            minutes = 0;
        }
        connectionTimeout = minutes;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    /**
     * DOCUMENT ME!
     *
     * @param minutes DOCUMENT ME!
     */
    public void setLoginTimeout(int seconds) {
        if (seconds < 0) {
        	seconds = 0;
        }
        loginTimeout = seconds;
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param minutes DOCUMENT ME!
     */
    public void setQueryTimeout(int seconds) {
        if (seconds < 0) {
        	seconds = 0;
        }
        queryTimeout = seconds;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getLoginTimeout() {
        return loginTimeout;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getQueryTimeout() {
        return queryTimeout;
    }

    /**
     * DOCUMENT ME!
     *
     * @param max DOCUMENT ME!
     */
    public void setMaxConnections(int max) {
        if (max < 0) {
            max = 0;
        }
        maxConnections = max;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getMaxConnections() {
        return maxConnections;
    }

    /**
     * DOCUMENT ME!
     *
     * @param max DOCUMENT ME!
     */
    public void setMaxRows(int max) {
        if (max < 0) {
            max = DEFAULT_MAX_ROWS;
        }
        maxRows = max;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getMaxRows() {
        return maxRows;
    }

    /**
     * DOCUMENT ME!
     *
     * @param string DOCUMENT ME!
     */
    protected void setErrorMessage(String string) {
        errorMessage = string;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * DOCUMENT ME!
     *
     * @param errorLog DOCUMENT ME!
     */
    public void setErrorLog(ErrorLog errorLog) {
        this.errorLog = errorLog;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ErrorLog getErrorLog() {
        return errorLog;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract DatabaseConnection cloneMe();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract String getVersion();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract int connect();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract boolean isSql();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract boolean isConnected();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract String[] listMetas();

    /**
     * DOCUMENT ME!
     *
     * @param meta DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract ResultSet listMeta(String meta);

    /**
     * DOCUMENT ME!
     *
     * @param metalist DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract ResultSet listMeta(String[] metalist);

    /**
     * DOCUMENT ME!
     *
     * @param table DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract ResultSet listMetaStruct(String table);

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract Map getDataTypes();

    /**
     * DOCUMENT ME!
     */
    public abstract void commit();

    /**
     * DOCUMENT ME!
     */
    public abstract void rollback();

    /**
     * DOCUMENT ME!
     *
     * @param status DOCUMENT ME!
     */
    public abstract void autocommit(boolean status);
    
    /**
     * DOCUMENT ME!
     *
     * @param status DOCUMENT ME!
     */
    public abstract boolean isAutocommit();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract boolean isPooled();

    /**
     * DOCUMENT ME!
     */
    public abstract void clear();

    /**
     * DOCUMENT ME!
     */
    public abstract void close();
    /**
     * @return Returns the parent.
     */
    public DatabaseManager getParent() {
        return parent;
    }

    /**
     * @param parent The parent to set.
     */
    public void setParent(DatabaseManager parent) {
        this.parent = parent;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean usePreparedStatement() {
        return DatabaseDrivers.usePreparedStatement(getType());
    }
    
    public boolean isIntersys() {
    	if (this instanceof ConnectionSql) {
    		ConnectionSql csql = (ConnectionSql)this;
    		if (csql.getConnection() != null) {
    			try {
    				String dname = csql.getConnection().getMetaData().getDriverName();
    				if (dname != null && dname.toLowerCase().startsWith("intersystems")) return true;
				} catch (Exception e) { }
    		}
    	}
    	return false;
    }

	public boolean isValid() {
		return valid;
	}

	public void setValid(boolean valid) {
		this.valid = valid;
	}
    
	public void onConnect() {
		File connectionLog = connectionLog();
		if (connectionLog != null) {
			try {
				SimpleDateFormat sdf = 
					new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
				FileWriter fw = new FileWriter(connectionLog);
				PrintWriter pw = new PrintWriter(fw);
				pw.println("type: " + type);
				pw.println("alias: " + alias);
				pw.println("user: " + user);
				pw.println("date: " + sdf.format(new Date()));
				pw.println("thread id: " + Thread.currentThread().getId());
				pw.println("thread name: " + Thread.currentThread().getName());
				pw.close();
				fw.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void onClose() {
		File connectionLog = connectionLog();
		if (connectionLog != null) {
			connectionLog.delete();
		}
	}
	
	private File connectionLog() {
		ExecuteParams wiParams = ExecuteParams.get();
		if (wiParams != null && wiParams.getWIMap() != null) {
			WIMap wiMap = wiParams.getWIMap();
			if (wiMap.get("pvt.dbconnection.debug").equalsIgnoreCase("true")) {
				String path = wiMap.get("wi.proj.path");
				File dir = new File(path, "/WEB-INF/logs/connectionLog");
				if (!dir.isDirectory()) dir.mkdirs();
				return new File(dir, "id-" + hashCode() + ".log");
			}	
		}	
		return null;
	}
	
}
