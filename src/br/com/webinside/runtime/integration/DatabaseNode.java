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

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class DatabaseNode {
    private DatabaseHandler database;
    private String user;
    private String pass;
    private int loginTimeout;
    private int queryTimeout;
    private int connectionTimeout;
    private int maxConnections;

    /**
     * Creates a new DatabaseNode object.
     */
    public DatabaseNode() {
        database = null;
        user = "";
        pass = "";
        queryTimeout = 0;
        maxConnections = 0;
        connectionTimeout = 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @param db DOCUMENT ME!
     */
    public void setDatabase(DatabaseHandler db) {
        this.database = db;
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
     * @param max DOCUMENT ME!
     */
    public void setMax(String max) {
        int nmax = 0;
        try {
            nmax = Integer.parseInt(max.trim());
            if (nmax < 0) {
                nmax = 0;
            }
        } catch (NumberFormatException err) {
        }
        this.maxConnections = nmax;
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
        this.maxConnections = max;
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
    public DatabaseHandler getDatabase() {
        return database;
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
     * @return DOCUMENT ME!
     */
    public String getPass() {
        return pass;
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
     * @return DOCUMENT ME!
     */
    public int getConnectionTimeout() {
        return connectionTimeout;
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
     * @return DOCUMENT ME!
     */
    public int getLoginTimeout() {
        return loginTimeout;
    }
}
