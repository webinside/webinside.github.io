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
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import br.com.webinside.runtime.component.DriversDef;
import br.com.webinside.runtime.component.JdbcAlias;
import br.com.webinside.runtime.component.JdbcAliases;
import br.com.webinside.runtime.util.ErrorLog;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.5 $
 */
public class DatabaseDrivers {
    private static Context jndiContext;

    static {
        try {
            jndiContext = new InitialContext();
        } catch (Exception err) {
            System.err.println(DatabaseDrivers.class.getName() + ": " + err);
        }
    }

    private String db_alias = "";
    private String db_user = "";
    private String db_pass = "";
    private Connection conexao;
    private DatabaseConnection parent;

    /**
     * Creates a new DatabaseDrivers object.
     *
     * @param dbio DOCUMENT ME!
     * @param alias DOCUMENT ME!
     * @param user DOCUMENT ME!
     * @param pass DOCUMENT ME!
     */
    public DatabaseDrivers(DatabaseConnection dbio, String alias, String user,
        String pass) {
        parent = dbio;
        if (alias != null) {
            db_alias = alias;
        }
        if (user != null) {
            db_user = user;
        }
        if (pass != null) {
            db_pass = pass;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Connection datasource() {
        conexao = null;
        String tname = Thread.currentThread().getName();
        String antname = StringA.piece(StringA.piece(tname, "^", 2), "-", 3);
        try {
			SimpleDateFormat df = 
				new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");	            
            Function.setThreadName(df.format(new Date()) + 
            		" - Connecting to DATASOURCE " + db_alias + 
            		" - " + antname.trim());
            DataSource ds = (DataSource) jndiContext.lookup(db_alias);
            if (!db_user.trim().equals("")) {
                conexao = ds.getConnection(db_user, db_pass);
            } else {
                conexao = ds.getConnection();
            }
            conexao.setAutoCommit(true);
        } catch (NamingException err) {
            ErrorLog log = parent.getErrorLog();
            if (log != null) {
                log.write("DatabaseDrivers", "Datasource JNDI Name Not Found",
                    err);
            }
        } catch (Exception err) {
            ErrorLog log = parent.getErrorLog();
            if (log != null) {
                log.write("DatabaseDrivers", "Datasource", err);
            }
        } finally {
        	Function.setThreadName(tname);
        }
        return conexao;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Connection odbc() {
        conexao = null;
        String tname = Thread.currentThread().getName();
        String antname = StringA.piece(StringA.piece(tname, "^", 2), "-", 3);
        try {
            Class.forName("sun.jdbc.odbc.JdbcOdbcDriver");
            String host = "jdbc:odbc:" + db_alias;
            DriverManager.setLoginTimeout(0);
			SimpleDateFormat df = 
				new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");	            
            Function.setThreadName(df.format(new Date()) + 
            		" - Connecting to ODBC " + db_alias + 
            		" - " + antname.trim());         
            conexao = DriverManager.getConnection(host, db_user, db_pass);
            conexao.setAutoCommit(true);
        } catch (SQLException err) {
            ErrorLog log = parent.getErrorLog();
            if (log != null) {
                log.write("DatabaseDrivers", "ODBC", err);
            }
        } catch (ClassNotFoundException err) {
            ErrorLog log = parent.getErrorLog();
            if (log != null) {
                log.write("DatabaseDrivers", "ODBC Driver Not Found", err);
            }
        } finally {
        	Function.setThreadName(tname);
        }
        return conexao;
    }

    /**
     * DOCUMENT ME!
     *
     * @param dbtipo DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Connection jdbc(String dbtipo, int loginTimeout) {
        if (dbtipo == null) {
            dbtipo = "";
        }
        if (dbtipo.equals("")) {
            return null;
        }
        conexao = null;
        JdbcAliases aliases = null;
        try {
            aliases = DriversDef.getJDBCAliases();
        } catch (IOException e) {
            ErrorLog log = parent.getErrorLog();
            if (log == null) {
                e.printStackTrace();
            } else {
                log.write("DatabaseDrivers", "Reading drivers", e);
            }
        }
        if (aliases != null) {
            JdbcAlias alias = aliases.get(dbtipo.toUpperCase().trim());
            if (alias != null) {
                connection(alias, true, loginTimeout);
            }
        }
        return conexao;
    }

    /**
     * DOCUMENT ME!
     *
     * @param alias DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String getVersion(JdbcAlias alias) {
        if (alias == null) {
            return "";
        }
        try {
			if (!alias.getClassType().equals("")) {
				Driver drv = (Driver) Class.forName(alias.getClassName()).getConstructor().newInstance();
				if (alias.getClassType().equals("REGISTERDRIVER")) {
					DriverManager.registerDriver(drv);
				}
			} else {
				Class.forName(alias.getClassName());
			}
        } catch (ClassNotFoundException err) {
        	// ignorado
        } catch (Exception err) {
        	// ignorado
        }
        Enumeration e = DriverManager.getDrivers();
        while (e.hasMoreElements()) {
            Driver dr = (Driver) e.nextElement();
            String name = dr.getClass().getName();
            String version = dr.getMajorVersion() + "." + dr.getMinorVersion();
            String clname = alias.getClassName();
            if (clname.equals("org.apache.derby.jdbc.EmbeddedDriver")) {
            	clname = "org.apache.derby.jdbc.AutoloadedDriver";
            }
            if (name.equalsIgnoreCase(clname)) {
                return version;
            }
        }
        return "";
    }

    private void connection(JdbcAlias alias, boolean connect, int loginTimeout) {
        conexao = null;
        String msg = alias.getDescription();
        if (!alias.getID().equals(alias.getDescription())) {
            msg = alias.getDescription() + "(" + alias.getID() + ")";
        }
        String tname = Thread.currentThread().getName();
        String antname = StringA.piece(StringA.piece(tname, "^", 2), "-", 3);
        Driver drv = null;
        try {
			if (!alias.getClassType().equals("")) {
				drv = (Driver) Class.forName(alias.getClassName()).getConstructor().newInstance();
				if (alias.getClassType().equals("REGISTERDRIVER")) {
					DriverManager.registerDriver(drv);
				}
			} else {
				Class.forName(alias.getClassName());
			}
            if (connect) {
                String url = produce(alias.getUrl());
                msg += (" in " + url);
                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
                Function.setThreadName(df.format(new Date())
                        + " - Connecting to " + msg + " - " + antname.trim());
                if (alias.getClassType().equals("USEINSTANCE")) {
                    Properties props = new Properties();
                    props.put("user", db_user);
                    props.put("password", db_pass);
                    props.put("loginTimeout", loginTimeout);
                    conexao = drv.connect(url, props);
                } else {
                    DriverManager.setLoginTimeout(loginTimeout);
                    conexao = DriverManager.getConnection(url, db_user, db_pass);
                }
                conexao.setAutoCommit(true);
            }
        } catch (ClassNotFoundException err) {
            ErrorLog log = parent.getErrorLog();
            if (connect && (log != null)) {
                log.write("DatabaseDrivers", msg + " Driver Not Found", err);
            }
        } catch (Exception err) {
            ErrorLog log = parent.getErrorLog();
            if (connect && (log != null)) {
                log.write("DatabaseDrivers", msg, err);
            }
        } finally {
            Function.setThreadName(tname);
        }
    }
    
    private String produce(String url) {
        String lowurl = url.toLowerCase();
        WIMap wiMap = new WIMap();
        wiMap.put("alias", db_alias);
        wiMap.put("root_alias", StringA.piece(db_alias, "//", 2, 0));
        if (lowurl.indexOf("|oracle_alias|") > -1) {
            String allhost = StringA.piece(db_alias, "/", 3);
            String base = StringA.piece(db_alias, "/", 4, 0);
            if (base.equals("")) {
                base = "orcl";
            }
            String host = StringA.piece(allhost, ":", 1);
            if (host.equals("")) {
                host = "localhost";
            }
            String port = StringA.piece(allhost, ":", 2);
            if (port.equals("")) {
                port = "1521";
            }
            String comp = "@" + host + ":" + port + ":" + base;
            if (host.startsWith("(")) {
                comp = "@" + host;
            }
            wiMap.put("oracle_alias", comp);
        }
        if (lowurl.indexOf("|sqlserver_alias|") > -1) {
            String allhost = StringA.piece(db_alias, ";", 1);
            String resto = StringA.piece(db_alias, ";", 2, 0);
            if (!resto.equals("")) {
                resto = ";" + resto;
            }
            String host = StringA.piece(allhost, ":", 1);
            String port = StringA.piece(allhost, ":", 2);
            if (port.equals("")) {
                port = "1433";
            }
            wiMap.put("sqlserver_alias", host + ":" + port + resto);
        }
        int from = 0;
        int pos;
        StringBuffer resp = new StringBuffer();
        while ((pos = lowurl.indexOf("|", from)) > -1) {
            int next = lowurl.indexOf("|", pos + 1);
            if (next == -1) {
                next = lowurl.length();
            }
            resp.append(StringA.mid(url, from, pos - 1));
            String key = StringA.mid(lowurl, pos + 1, next - 1);
            resp.append(wiMap.get(key));
            from = next + 1;
        }
        resp.append(StringA.mid(url, from, url.length()));
        return resp.toString();
    }
    
    public static boolean usePreparedStatement(String dbtipo) {
        if (dbtipo == null) {
            dbtipo = "";
        }
        JdbcAliases aliases = null;
        try {
            aliases = DriversDef.getJDBCAliases();
            JdbcAlias alias = aliases.get(dbtipo.toUpperCase().trim());
            return alias.usePreparedStatement();
        } catch (Exception e) {
        	// ignorado.
        }
    	return true;
    }

}
