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

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.sql.Timestamp;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import br.com.webinside.runtime.database.DatabaseConnection;
import br.com.webinside.runtime.database.DatabaseDrivers;
import br.com.webinside.runtime.database.ErrorCode;
import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.integration.Producer;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.util.ErrorLog;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class ConnectionSql extends DatabaseConnection {
    private Connection connection;
    private Statement stmt;
    private java.sql.ResultSet resultset;
    private boolean returnGeneratedKeys;

    /**
     * Creates a new ConnectionSql object.
     *
     * @param type DOCUMENT ME!
     * @param alias DOCUMENT ME!
     * @param user DOCUMENT ME!
     * @param pass DOCUMENT ME!
     */
    public ConnectionSql(String type, String alias, String user, String pass) {
        super(type, alias, user, pass);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public DatabaseConnection cloneMe() {
        ConnectionSql database =
            new ConnectionSql(getType(), getAlias(), getUser(), getPass());
        database.load(this);
        return database;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isPooled() {
        if (getType().equals("DATASOURCE")) {
            return false;
        }
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isSql() {
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getVersion() {
        return "";
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
        if (isConnected()) {
            return ErrorCode.NOERROR;
        }
        DatabaseDrivers drivers =
            new DatabaseDrivers(this, getAlias(), getUser(), getPass());
        if (getType().equals("DATASOURCE")) {
            connection = drivers.datasource();
        } else if (getType().equals("ODBC")) {
            connection = drivers.odbc();
        } else {
            connection = drivers.jdbc(getType(), getLoginTimeout());
        }
        if (connection == null) {
            return ErrorCode.UNKNOWN;
        }
        onConnect();
        return ErrorCode.NOERROR;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isConnected() {
        if (connection == null) {
            return false;
        }
        try {
            if (!connection.isClosed()) {
                return true;
            }
        } catch (SQLException err) {
            System.err.println(getClass().getName() + ": " + err);
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Connection getConnection() {
        return connection;
    }

    /**
     * DOCUMENT ME!
     *
     * @param query DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    public ResultSet execute(String query) throws SQLException {
        return execute(query, null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param query DOCUMENT ME!
     * @param wiMap DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    public ResultSet execute(String query, WIMap wiMap)
        throws SQLException {
        clear();
        if (query == null) {
            query = "";
        }
        if (wiMap == null) {
            wiMap = new WIMap();
        }
        try {
            if (connection == null) {
                setErrorMessage("(-1)Null Connection");
                return null;
            }
            boolean isUpdate = 
            	wiMap.get("wi.download").equalsIgnoreCase("true");
            Map out = createStatement(query, wiMap, null, isUpdate);
            String pvtDisableMaxRows = wiMap.get("pvt.disableMaxRows").trim();
            String tmpDisableMaxRows = wiMap.get("tmp.disableMaxRows").trim();
            if (getMaxRows() > 0 && 
            		!pvtDisableMaxRows.equalsIgnoreCase("true") && 
            		!tmpDisableMaxRows.equalsIgnoreCase("true")) {
                stmt.setMaxRows(getMaxRows());
            }
            if (stmt instanceof CallableStatement) {
                CallableStatement callStmt = (CallableStatement) stmt;
                for (Iterator it = out.keySet().iterator(); it.hasNext();) {
                	int pIndex = ((Integer) it.next()).intValue();
                	callStmt.registerOutParameter(pIndex, Types.VARCHAR);
				}
                if (getType().equals("ORACLE")) {
                    callStmt.registerOutParameter(1, -10); // -10 = OracleTypes.CURSOR
                    callStmt.execute();
                    resultset = (java.sql.ResultSet) callStmt.getObject(1);
                } else {
                	resultset = callStmt.executeQuery();
                }
                outProcedure(callStmt, out, wiMap);
            } else if (stmt instanceof PreparedStatement) {
            	resultset = ((PreparedStatement) stmt).executeQuery();
            } else {
            	resultset = stmt.executeQuery(query);
            }
            if (resultset != null) {
                ResultSetSql resp = new ResultSetSql(this);
                resp.setResultSet(resultset);
                resp.setLongTextColumns(wiMap.get("wi.sql.longtextcolumns"));
                return resp;
            }
        } catch (SQLException err) {
        	if (getType().equals("CACHE")) {
        		try {
        			setValid(connection.isValid(1000));
        		} catch (Throwable ex) {
                	setValid(false);
        		}
        	}
            sqlError(err, stmt.getWarnings());
            throw err;
        } catch (IOException err) {
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param query DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    public int executeUpdate(String query) throws SQLException {
        return executeUpdate(query, null);
    }

    /**
     * DOCUMENT ME!
     *
     * @param query DOCUMENT ME!
     * @param wiMap DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    public int executeUpdate(String query, WIMap wiMap)
        throws SQLException {
        clear();
        if (query == null) {
            query = "";
        }
        if (wiMap == null) {
            wiMap = new WIMap();
        }
        try {
            if (connection == null) {
                setErrorMessage("(-1)Null Connection");
                return -1;
            }
            Map out = createStatement(query, wiMap, null, true);
            if (stmt instanceof CallableStatement) {
                // desabilita generatedKeys para o proximo update
                returnGeneratedKeys = false;
                CallableStatement callStmt = (CallableStatement) stmt;
                for (Iterator it = out.keySet().iterator(); it.hasNext();) {
                	int pIndex = ((Integer) it.next()).intValue();
                	callStmt.registerOutParameter(pIndex, Types.VARCHAR);
				}
                int resp = 0;
                if (getType().equals("ORACLE")) {
                    callStmt.registerOutParameter(1, Types.INTEGER);
                    callStmt.execute();
                    resp = callStmt.getInt(1);
                } else {
                    resp = callStmt.executeUpdate();
                }
                outProcedure(callStmt, out, wiMap);
                return resp;
            } else if (stmt instanceof PreparedStatement) {
                // desabilita generatedKeys para o proximo update
                returnGeneratedKeys = false;
                return ((PreparedStatement) stmt).executeUpdate();
            } else {
            	if (returnGeneratedKeys) {
                    // desabilita generatedKeys para o proximo update
                    returnGeneratedKeys = false;
            		int rgkCode = Statement.RETURN_GENERATED_KEYS;
                    return stmt.executeUpdate(query, rgkCode);
            	} else {
                    return stmt.executeUpdate(query);
            	}
            }
        } catch (SQLException err) {
        	if (getType().equals("CACHE")) {
            	setValid(false);
        	}
            sqlError(err, stmt.getWarnings());
            throw err;
        } catch (IOException err) {
            return -1;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param in DOCUMENT ME!
     * @param query DOCUMENT ME!
     * @param wiMap DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws SQLException DOCUMENT ME!
     */
    public int executeUpdate(InputStream in, String query, WIMap wiMap)
        throws SQLException {
        clear();
        if (query == null) {
            query = "";
        }
        if (wiMap == null) {
            wiMap = new WIMap();
        }
        try {
            if (connection == null) {
                setErrorMessage("(-1)Null Connection");
                return -1;
            }
            createStatement(query, wiMap, in, true);
            // desabilita generatedKeys para o proximo update
            returnGeneratedKeys = false;
            int resp = -1;
            try {
                resp = ((PreparedStatement) stmt).executeUpdate();
            } catch (ArrayIndexOutOfBoundsException err) {
                setErrorMessage("(-12)SQL must have ? and not have ' or \"");
            }
            return resp;
        } catch (SQLException err) {
        	if (getType().equals("CACHE")) {
            	setValid(false);
        	}
            sqlError(err, stmt.getWarnings());
            throw err;
        } catch (IOException err) {
            return -1;
        }
    }

    private Map createStatement(String query, WIMap wiMap, InputStream in, 
    		boolean isUpdate) throws SQLException, IOException {
        // Montando a nova query
    	Map out = new HashMap();
        List params = new ArrayList();
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        StringBuffer resp = new StringBuffer();
        int posStream = -1;
        int from = 0;
        int pos = -1;
        while ((pos = query.indexOf("?", from)) > -1) {
        	String let = StringA.mid(query, pos + 1, pos + 1);
            int end = pos;
            if (let.equals("|") || let.equals("[")) {
            	int inc = (let.equals("[") ? 6 : 1);
            	end = Function.lastPipePos(query, pos + inc);
            }
            String param = StringA.mid(query, pos + 1, end);
            if (param.equals("") && in != null && posStream == -1) {
                posStream = params.size();
                Map aux = new HashMap();
                aux.put("type", "bin");
                aux.put("value", "");
                params.add(aux);
            } else if (!param.equals("")) {
            	param = decodeParam(prod, out, params, param);
            }
            resp.append(StringA.mid(query, from, pos - 1));
            resp.append("?");
            from = end + 1;
        }
        resp.append(StringA.mid(query, from, query.length()));
        String newQuery = resp.toString();

        // Criando o Statement
		String cleanQuery = StringA.changeChars(query.toLowerCase()," ","");
        try {        	
        	if (isUpdate) throw new AbstractMethodError();
        	// Usado para SELECT
            int sens = java.sql.ResultSet.TYPE_SCROLL_INSENSITIVE;
            int conc = java.sql.ResultSet.CONCUR_READ_ONLY;
            if (cleanQuery.startsWith("{call")) {
                stmt = connection.prepareCall(newQuery.trim(), sens, conc);
            } else if (query.equals(newQuery) && (in == null)) {
                stmt = connection.createStatement(sens, conc);
            } else {
                stmt = connection.prepareStatement(newQuery, sens, conc);
            }
        } catch (AbstractMethodError err) {
        	// Usado para UPDATE
            if (cleanQuery.startsWith("{call")) {
                stmt = connection.prepareCall(newQuery);
            } else if (query.equals(newQuery) && (in == null)) {
                stmt = connection.createStatement();
            } else {
            	if (returnGeneratedKeys) {
            		int rgkCode = Statement.RETURN_GENERATED_KEYS;
                    stmt = connection.prepareStatement(newQuery, rgkCode);
            	} else {
                    stmt = connection.prepareStatement(newQuery);
            	}
            }
        }
        if (getQueryTimeout() > 0) {
            try {
                stmt.setQueryTimeout(getQueryTimeout());
            } catch (Throwable err) {            	
            	System.out.println("QueryTimeout: " + err);
            }  
        }
		if (stmt instanceof PreparedStatement) {
			int inc = 1;
			if (cleanQuery.startsWith("{call?")) {
				inc = 2;
			}
			populatePS(params, inc, posStream, in);
		}	
        try {
        	if (!isUpdate && !getType().equals("CACHE")) {
        		stmt.setFetchSize(100);
        	}	
        } catch (Throwable err) {
        	// ignorado
        }
        return out;
    }

    private String decodeParam(ProducerParam prod, Map out, 
    		List params, String param) {
    	String type = "";
    	if (param.startsWith("[")) {
    		type = param.substring(1,4);
    		param = StringA.mid(param, 5, param.length());
    	}
    	if (param.endsWith(".out|")) {
    		int pos = param.indexOf(".out|");
    		param = StringA.mid(param, 0, pos-1) + "|";
    		String var = StringA.changeChars(param, "|", "").trim();
    		out.put(new Integer(params.size()+1), var);
    	}
        prod.setInput(param);
        new Producer(prod).execute();
        String value = prod.getOutput().trim();
        Map aux = new HashMap();
        aux.put("type", type);
        aux.put("value", value);
        params.add(aux);
    	return param;
    }
    
    private void populatePS(List params, int inc, 
    		int posStream, InputStream in) throws SQLException, IOException {
    	for (int i = 0; i < params.size(); i++) {
    		Map aux = (Map) params.get(i);
    		String type = (String) aux.get("type");
    		String value = (String) aux.get("value");
			PreparedStatement ps = (PreparedStatement) stmt;
    		if (i == posStream) {
    			ps.setBinaryStream((i + 1), in,	in.available());
    		} else {
    			if (type.equals("")) {
    				try {
    					ParameterMetaData pmd = ps.getParameterMetaData();
	    				String clName = pmd.getParameterClassName(i + inc);
	    				if (Date.class.getName().equals(clName) ||
	    						Timestamp.class.getName().equals(clName)) {
	    					type = "dat";
	    				} else if (Integer.class.getName().equals(clName)) {
	    					type = "int";
	    				} else if (Long.class.getName().equals(clName)) {
	    					type = "lon";
	    				} else if (Float.class.getName().equals(clName)) {
	    					type = "flt";
	    				} else if (Double.class.getName().equals(clName)) {
	    					type = "dbl";
	    				}
    				} catch (Throwable ex) {
    					// ignorado
    				}
    			}	
    			if (type.equalsIgnoreCase("asc")) {
    				if (!value.equalsIgnoreCase("null")) {
        				StringReader reader = new StringReader(value); 
        				ps.setCharacterStream((i + inc), reader, value.length());
    				} else {
    					ps.setNull((i + inc), Types.NULL);
    				}
    			} else if (type.equalsIgnoreCase("bin")) {
    				ByteArrayInputStream bais = null;
    				if (value.startsWith("file:")) {
    					String iaux = StringA.piece(value, "file:", 2);
    					if (!(new File(iaux).isFile())) {
    						ps.setNull((i + inc), Types.NULL);
    						continue;
    					}
    					InputStream is = new FileInputStream(iaux);
    					byte[] bt = new byte[is.available()];
    					is.read(bt, 0, bt.length);
    					is.close();
    					bais = new ByteArrayInputStream(bt);
    				} else {
    					bais = new ByteArrayInputStream(value.getBytes());
    				}	
    				ps.setBinaryStream((i + inc), bais, bais.available());
    			} else if (type.equalsIgnoreCase("dat")) {
    				try {
    					String pat1 = "yyyy-MM-dd hh:mm:ss";
    					String pat2 = "yyyy-MM-dd";
    					SimpleDateFormat sds = new SimpleDateFormat(pat1);
    					if (value.trim().length() == 10) {
    						sds = new SimpleDateFormat(pat2);
    					}
    					long time = sds.parse(value.trim()).getTime();
    					Timestamp ts = new Timestamp(time);
    					ps.setTimestamp((i + inc), ts);
    				} catch (Exception err) { 
    					ps.setNull((i + inc), Types.NULL);
    				}
    			} else if (type.equalsIgnoreCase("int")) {
    				try {
    					int a = Integer.parseInt(value.trim());
    	   				ps.setInt((i + inc), a);
    				} catch (Exception err) {
    					ps.setNull((i + inc), Types.NULL);
    				}
    			} else if (type.equalsIgnoreCase("lon")) {
    				try {
    					long a = Long.parseLong(value.trim());
    	   				ps.setLong((i + inc), a);
    				} catch (Exception err) {
    					ps.setNull((i + inc), Types.NULL);
    				}
    			} else if (type.equalsIgnoreCase("flt")) {
    				try {
    					float a = Float.parseFloat(value.trim());
    	   				ps.setFloat((i + inc), a);
    				} catch (Exception err) {
    					ps.setNull((i + inc), Types.NULL);
    				}
    			} else if (type.equalsIgnoreCase("dbl")) {
    				try {
    					double a = Double.parseDouble(value.trim());
    	   				ps.setDouble((i + inc), a);
    				} catch (Exception err) {
    					ps.setNull((i + inc), Types.NULL);
    				}
    			} else if (type.equalsIgnoreCase("bit")) {
    				value = value.trim().equals("1") ? "1" : "0";
    				ps.setBoolean((i + inc), Boolean.parseBoolean(value));
    			} else if (type.equalsIgnoreCase("str")) {
    				if (!value.equalsIgnoreCase("null")) {
        				ps.setString((i + inc), value);
    				} else {
    					ps.setNull((i + inc), Types.NULL);
    				}
    			} else if (type.equalsIgnoreCase("nul")) {
    				if (!value.equals("")) {
        				ps.setString((i + inc), value);
    				} else {
    					ps.setNull((i + inc), Types.NULL);
    				}
    			} else {
    				ps.setString((i + inc), value);
    			}
    		}
    	}
    }

    private void outProcedure(CallableStatement callStmt, Map out, 
    		WIMap wiMap) throws SQLException {
        Iterator it = out.entrySet().iterator();
        while (it.hasNext()) {
        	Map.Entry entry = (Entry) it.next();
        	int pIndex = ((Integer) entry.getKey()).intValue();
        	String value = (String) entry.getValue(); 
            wiMap.put(value, callStmt.getString(pIndex));
        }
    }
    
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] listMetas() {
        clear();
        List resp = new ArrayList();
        try {
            if (connection == null) {
                setErrorMessage("(-1)Null Connection");
                return new String[0];
            }
            DatabaseMetaData meta = connection.getMetaData();
            java.sql.ResultSet result = meta.getTableTypes();
            if (result != null) {
	            int numcol = result.getMetaData().getColumnCount();
	            if (numcol >= 1) {
	                while (result.next()) {
	                    String item = result.getString(1).trim();
	                    resp.add(item);
	                }
	            }
            }
        } catch (SQLException err) {
            setErrorMessage(err.getMessage());
        }
        return (String[])resp.toArray(new String[0]);
    }

    /**
     * DOCUMENT ME!
     *
     * @param meta DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ResultSet listMeta(String meta) {
        if (meta == null) {
            meta = "";
        }
        String[] metalist = new String[1];
        metalist[0] = meta;
        return listMeta(metalist);
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
        if (metalist == null) {
            metalist = new String[0];
        }
        ResultSetSql resp = new ResultSetSql(this);
        try {
            if (connection == null) {
                setErrorMessage("(-1)Null Connection");
                return null;
            }
            DatabaseMetaData meta = connection.getMetaData();
            java.sql.ResultSet result =
                meta.getTables(null, null, "%", metalist);
            if (result != null) {
                resp.setResultSet(result);
                return resp;
            }
        } catch (SQLException err) {
            setErrorMessage(err.getMessage());
        }
        return null;
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
        if (table == null) {
            table = "";
        }
        ResultSetSql resp = new ResultSetSql(this);
        try {
            if (connection == null) {
                setErrorMessage("(-1)Null Connection");
                return null;
            }
            DatabaseMetaData meta = connection.getMetaData();
            String cat = null;
            String sche = null;
            List info = StringA.pieceAsList(table, ".", 0, 0, false);
            if (info.size() == 2) {
                sche = (String) info.get(0);
                table = (String) info.get(1);
            } else if (info.size() == 3) {
                cat = (String) info.get(0);
                sche = (String) info.get(1);
                table = (String) info.get(2);
            }
            java.sql.ResultSet result = meta.getColumns(cat, sche, table, "%");
            if (result != null) {
                resp.setResultSet(result);
                return resp;
            }
        } catch (SQLException err) {
            setErrorMessage(err.getMessage());
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Map getDataTypes() {
        clear();
        Map response = new HashMap();
        try {
            if (connection == null) {
                setErrorMessage("(-1)Null Connection");
                return response;
            }
            DatabaseMetaData meta = connection.getMetaData();
            java.sql.ResultSet rs = meta.getTypeInfo();
            while (rs != null && rs.next()) {
                if ((rs.getInt(2) != Types.OTHER)
                            && (rs.getInt(2) != Types.ARRAY)) {
                    response.put(rs.getString(1), rs.getString(2));
                }
            }
        } catch (SQLException err) {
            setErrorMessage(err.getMessage());
        }
        return response;
    }

    /**
     * DOCUMENT ME!
     */
    public void commit() {
        if (connection == null) {
            return;
        }
        try {
            connection.commit();
        } catch (SQLException err) {
            ErrorLog log = getErrorLog();
            if (log != null) {
                log.write(getClass().getName(), "Commit", err);
            }
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void rollback() {
        if (connection == null) {
            return;
        }
        try {
            connection.rollback();
        } catch (SQLException err) {
            ErrorLog log = getErrorLog();
            if (log != null) {
                log.write(getClass().getName(), "Rollback", err);
            }
        }
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param status DOCUMENT ME!
     */
    public void autocommit(boolean status) {
        if (connection != null) {
            try {
                connection.setAutoCommit(status);
            } catch (SQLException err) {
                ErrorLog log = getErrorLog();
                if (log != null) {
                    log.write(getClass().getName(), "AutoCommit", err);
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param status DOCUMENT ME!
     */
    public boolean isAutocommit() {
        if (connection != null) {
            try {
               return connection.getAutoCommit();
            } catch (SQLException err) {
                ErrorLog log = getErrorLog();
                if (log != null) {
                    log.write(getClass().getName(), "isAutocommit", err);
                }
            }
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     */
    public void clear() {
        setErrorMessage("");
        try {
            if (resultset != null) {
                resultset.close();
            }
        } catch (SQLException err) {
        	// ignorado
        }
        resultset = null;
        try {
            if (stmt != null) {
                stmt.close();
                if (getType().equals("ODBC")) {
                    try {
                    	stmt.wait(5000);
                    } catch (Exception err) {
                    	// ignorado
                    }                	
                }
            }
        } catch (SQLException err) {
        	// ignorado
        }
        stmt = null;
    }

    /**
     * DOCUMENT ME!
     */
    public void close() {
        if (getParent() != null) {
            getParent().close();
        } else {
            clear();
            try {
                if (connection != null) {
                    connection.close();
                	onClose();
                }
            } catch (SQLException err) {
            	// ignorado
            }
            connection = null;        
        }
    }

	public void returnGeneratedKeys() {
		this.returnGeneratedKeys = true;
	}

    public ResultSet getGeneratedKeys() throws SQLException {
    	if (stmt != null && stmt.getGeneratedKeys() != null) {
            ResultSetSql resp = new ResultSetSql(this);
            resp.setResultSet(stmt.getGeneratedKeys());
            return resp;
    	}
    	return null;
    }

	private void sqlError(SQLException err, SQLWarning warnings) {
        int error = err.getErrorCode();
        if (error > 0) {
            error = -error;
        }
        if (error == 0) {
            error = -1;
        }
        Set messages = new LinkedHashSet();
        messages.add(err.getMessage());
       	while (warnings != null) {
       		messages.add(warnings.getMessage());
        	warnings = warnings.getNextWarning();
        }
       	StringBuffer message = new StringBuffer();
       	for (Iterator it = messages.iterator(); it.hasNext();) {
       		if (message.length() > 0) message.append("\r\n");
       		message.append((String)it.next());
		}
        setErrorMessage("(" + error + ")" + message.toString());
    }
}
