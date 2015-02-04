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

package br.com.webinside.runtime.database.impl;

import java.util.HashMap;
import java.util.Map;

import br.com.webinside.runtime.database.DatabaseConnection;
import br.com.webinside.runtime.database.ErrorCode;
import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class ConnectionMjava extends DatabaseConnection {
    private static String mjavaVersion = "";
    private ConnectionMjavaClient mJava;
    private String filter = "";

    /**
     * Creates a new ConnectionMjava object.
     *
     * @param type DOCUMENT ME!
     * @param alias DOCUMENT ME!
     * @param user DOCUMENT ME!
     * @param pass DOCUMENT ME!
     */
    public ConnectionMjava(String type, String alias, String user, String pass) {
        super(type, alias, user, pass);
        setMaxRows(0);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public DatabaseConnection cloneMe() {
        ConnectionMjava database =
            new ConnectionMjava(getType(), getAlias(), getUser(), getPass());
        database.load(this);
        return database;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isPooled() {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isSql() {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getVersion() {
        if ((mJava != null) && (mjavaVersion.equals(""))) {
            mjavaVersion = mJava.getVersionCode();
        }
        String resp = mjavaVersion;
        if (resp.equals("0")) {
            setErrorMessage("(-20)Mjava Server Connection Failure");
            mjavaVersion = "";
        } else {
            setErrorMessage("(-21)Mjava Server Version - " + resp);
        }
        return resp;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int connect() {
        if ((getAlias() == null) || (getUser() == null) || (getPass() == null)) {
            return ErrorCode.NULLPARAM;
        }
        if (getType().equals("")) {
            return ErrorCode.INVALIDTYPE;
        }
        if (!isConnected()) {
            mJava = new ConnectionMjavaClient(this);
        	onConnect();
        }
        return ErrorCode.NOERROR;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isConnected() {
        if (mJava != null) {
            if (!getVersion().equals("0")) {
                setErrorMessage("");
                return true;
            }
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param query DOCUMENT ME!
     * @param hash DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ResultSet execute(String query, WIMap hash) {
        return execute(query, hash, 0, 0);
    }

    /**
     * DOCUMENT ME!
     *
     * @param query DOCUMENT ME!
     * @param hash DOCUMENT ME!
     * @param from DOCUMENT ME!
     * @param size DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ResultSet execute(String query, WIMap hash, int from, int size) {
        clear();
        if (query == null) {
            query = "";
        }
        if (hash == null) {
            hash = new WIMap();
        }
        ResultSetNoSql resp = new ResultSetNoSql();
        if (mJava == null) {
            setErrorMessage("(-1)Null Connection");
            return null;
        }
        mJava.setFilter(filter);
        resp.setRowsNoSQL(mJava.executeSQL(query, hash, from, size));
        setErrorMessage(mJava.getErrorMsg());
        resp.setHeadersNoSQL(mJava.getHeaders());
        if (!getErrorMessage().trim().equals("")) {
            return null;
        }
        return resp;
    }

    /**
     * DOCUMENT ME!
     *
     * @param query DOCUMENT ME!
     * @param hash DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int executeUpdate(String query, WIMap hash) {
        if (mJava == null) {
            setErrorMessage("(-1)Null Connection");
            return -1;
        }
        clear();
        mJava.setFilter(filter);
        int resp = mJava.executeUpdate(query, hash);
        if (resp < 0) {
            setErrorMessage(mJava.getErrorMsg());
        }
        return resp;
    }

    /**
     * DOCUMENT ME!
     *
     * @param filter DOCUMENT ME!
     */
    public void setFilter(String filter) {
        if (filter == null) {
            filter = "";
        }
        this.filter = filter;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getFilter() {
        return filter;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] listMetas() {
        clear();
        return new String[0];
    }

    /**
     * DOCUMENT ME!
     *
     * @param meta DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ResultSet listMeta(String meta) {
        return listMeta(new String[0]);
    }

    /**
     * DOCUMENT ME!
     *
     * @param metalist DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ResultSet listMeta(String[] metalist) {
        clear();
        return new ResultSetNoSql();
    }

    /**
     * DOCUMENT ME!
     *
     * @param table DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ResultSet listMetaStruct(String table) {
        clear();
        return new ResultSetNoSql();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Map getDataTypes() {
        Map resp = new HashMap();
        resp.put("VARCHAR", java.sql.Types.VARCHAR + "");
        return resp;
    }

    /**
     * DOCUMENT ME!
     */
    public void commit() {
    }

    /**
     * DOCUMENT ME!
     */
    public void rollback() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param status DOCUMENT ME!
     */
    public void autocommit(boolean status) {
    }

    /**
     * DOCUMENT ME!
     *
     * @param status DOCUMENT ME!
     */
    public boolean isAutocommit() {
    	return false;
    }

    /**
     * DOCUMENT ME!
     */
    public void clear() {
        setErrorMessage("");
    }

    /**
     * DOCUMENT ME!
     */
    public void close() {
        clear();
        if (mJava != null) {
            onClose();
        }
        mJava = null;
    }
    
	protected String getPass() {
		return super.getPass();
	}
}
