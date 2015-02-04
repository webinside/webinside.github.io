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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.InflaterInputStream;

import br.com.webinside.runtime.database.DatabaseConnection;
import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.util.ErrorLog;
import br.com.webinside.runtime.util.StringA;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.5 $
 */
public class ResultSetSql implements ResultSet {
    private java.sql.ResultSet rset;
    private DatabaseConnection parent;
    private String longTextColumns = "";
    private int line = 0;
    private Map row = new HashMap();
	private List columnData;

    /**
     * Creates a new ResultSetSql object.
     *
     * @param dbio DOCUMENT ME!
     */
    public ResultSetSql(DatabaseConnection dbio) {
        parent = dbio;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int rowCount() {
        if (rset == null) {
            return -1;
        }
        try {
        	rset.last();
            int pos = rset.getRow();
            if (line > 0) {
            	rset.absolute(line);
            } else {
            	rset.beforeFirst();
            }
            return pos;
        } catch (Throwable err) {
            return -1;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param row DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int go(int pos) {
        if ((rset == null) || (pos < 0)) {
            return -1;
        }
        if ((pos == line) && (pos != 0)) {
            return pos;
        }
        if (rset.getClass().getSimpleName().equals("LdapResultSet")) {
        	return -1;
        }
        try {
            if ((pos == 0) || !rset.absolute(pos)) {
            	rset.beforeFirst();
                line = 0;
            } else {
                line = pos;
            }
            row = new HashMap();
            return line;
        } catch (Throwable err) {
            return -1;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int next() {
        try {
            if (rset == null) {
                return 0;
            }
            row = new HashMap();
            if (rset.next()) {
                line = line + 1;
            } else {
                line = 0;
            }
            if (rset.getClass().getSimpleName().equals("LdapResultSet")) {
            	int refPos = columnIndexLdap("ref");
            	if (refPos > 0 && !column(refPos).equals("")) {
            		line = 0;
            	}
            }
            return line;
        } catch (SQLException err) {
        	if (parent != null) {
                ErrorLog log = parent.getErrorLog();
                if (log != null) {
                    log.write("ResultSetSQL", "Next(SQL)", err);
                }
        		if (parent.getType().equals("CACHE")) {
            		parent.setValid(false);
        		}	
        	}
            clear();
            return 0;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param field DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String column(String field) {
        return column(field, "");
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param field DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String columnNext(String field) {
    	String resp = "";
    	if (next() > 0) {
    		resp = column(field, "");
    	}
        return resp;
    }    

    /**
     * DOCUMENT ME!
     *
     * @param field DOCUMENT ME!
     * @param binFilter DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String column(String field, boolean binFilter) {
        if (binFilter) {
            return column(field, binaryFilter);
        } else {
            return column(field);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param field DOCUMENT ME!
     * @param binFilter DOCUMENT ME!
     * @param showNull DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String column(String field, boolean binFilter, boolean showNull) {
        if (binFilter) {
            return column(field, binaryFilter, showNull);
        } else {
            return column(field, "", showNull);
        }
    }

    // Restric exemplo: binary,blob,text
    public String column(String field, String restrict) {
        return column(field, restrict, false);
    }

    // Restric exemplo: binary,blob,text
    public String column(String field, String restrict, boolean showNull) {
        int indice = columnIndex(field);
        if (indice < 1) {
            if (showNull) {
                return "<null>";
            } else {
                return "";
            }
        }
        return column(indice, restrict, showNull);
    }

    /**
     * DOCUMENT ME!
     *
     * @param index DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String column(int index) {
        return column(index, "");
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param index DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String columnNext(int index) {
    	String resp = "";
    	if (next() > 0) {
    		resp = column(index, "");
    	}
        return resp;
    }

    /**
     * DOCUMENT ME!
     *
     * @param index DOCUMENT ME!
     * @param binFilter DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String column(int index, boolean binFilter) {
        if (binFilter) {
            return column(index, binaryFilter);
        } else {
            return column(index);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param index DOCUMENT ME!
     * @param binFilter DOCUMENT ME!
     * @param showNull DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String column(int index, boolean binFilter, boolean showNull) {
        if (binFilter) {
            return column(index, binaryFilter, showNull);
        } else {
            return column(index, "", showNull);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param index DOCUMENT ME!
     * @param restrict DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String column(int index, String restrict) {
        return column(index, restrict, false);
    }

    // Restric exemplo: binary,blob,text
    public String column(int index, String restrict, boolean showNull) {
        if (restrict == null) {
            restrict = "";
        }
        if (line == 0) {
            return "";
        }
        String chave = index + "";
        if (rset == null) {
            return "";
        }
        try {
            String resp = "";
            if (row.containsKey(chave)) {
                resp = (String) row.get(chave);
            } else {
                if (!isRestricted(restrict, columnType(index))) {
                	resp = columnValue(index);
                    if (resp == null) {
                        if (showNull) {
                            resp = "<null>";
                        } else {
                            resp = "";
                        }
                    }
                } else {
                    if (showNull && (rset.getString(index) == null)) {
                        resp = "<null>";
                    } else {
                        resp = "<binary>";
                    }
                }
                row.put(chave, resp);
            }
            return resp;
        } catch (SQLException err) {
        	if (parent != null) {
                ErrorLog log = parent.getErrorLog();
                if (log != null) {
                    log.write("ResultSetSQL", "Column(SQL)", err);
                }
        		if (parent.getType().equals("CACHE")) {
            		parent.setValid(false);
        		}	
        	}
        }
        return "";
    }
    
    private String columnValue(int index) throws SQLException {
    	if (longTextColumns.indexOf(index + ",") > -1) {
    		StringBuffer resp = new StringBuffer();
    		try { 
	    		InputStream in = rset.getAsciiStream(index);
	    		int c;
	    		byte[] buffer = new byte[10240];
	    		while ((c = in.read(buffer)) > -1) {
	    			resp.append(new String(buffer, 0, c));
	    		}
	    		in.close();
    		} catch (Exception err) { 
    			return null;
    		}	
    		return resp.toString();
    	} else {
    		String resp = rset.getString(index);
    		if (resp == null) {
    		    return null;
    		}
            // corrige o unicode
            byte[] lista = new byte[resp.length()];
            for (int i = 0; i < resp.length(); i++) {
                lista[i] = (byte) resp.charAt(i);
            }
            return new String(lista);
    	}	
    }	
    
    private boolean isRestricted(String restrict, String type) {
        if (restrict == null) {
            restrict = "";
        }
        if (type == null) {
            type = "";
        }
        if (!restrict.equals("")) {
            int cont = StringA.count(restrict.toLowerCase(), ',');
            for (int i = 1; i <= (cont + 1); i++) {
                if (type.toLowerCase().indexOf(StringA.piece(
                                    restrict.toLowerCase(), ",", i)) > -1) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param out DOCUMENT ME!
     * @param field DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int columnBin(OutputStream out, String field) {
        int indice = columnIndex(field);
        if (indice <= 0) {
            return -1;
        }
        return columnBin(out, indice);
    }

    /**
     * DOCUMENT ME!
     *
     * @param out DOCUMENT ME!
     * @param index DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int columnBin(OutputStream out, int index) {
    	return columnBin(out, index, false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param out DOCUMENT ME!
     * @param index DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int columnBinZip(OutputStream out, int index) {
    	return columnBin(out, index, true);
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param out DOCUMENT ME!
     * @param index DOCUMENT ME!
     * @param zip DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private int columnBin(OutputStream out, int index, boolean zip) {
        if ((rset == null) || (out == null)) {
            return -1;
        }
        if (line == 0) {
            return -1;
        }
        try {
            byte[] bt = new byte[10240];
            InputStream in = rset.getBinaryStream(index);
            if (in == null) {
                return 0;
            }
            if (zip) {
            	in = new InflaterInputStream(in);
            }
            boolean vazio = true;
            try {
                int tam = 0;
                while ((tam = in.read(bt)) > 0) {
                    vazio = false;
                    out.write(bt, 0, tam);
                    out.flush();
                }
                in.close();
            } catch (IOException ioerr) {
                ErrorLog log = parent.getErrorLog();
                if (log != null) {
                    log.write("ResultSet", "ColumnBin", ioerr);
                }
                return -1;
            }
            if (vazio) {
                return 0;
            }
            return 1;
        } catch (SQLException err) {
        	if (parent != null && parent.getType().equals("CACHE")) {
        		parent.setValid(false);
        	}
            return -1;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param index DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String columnName(int index) {
    	return columnProp(index, "name");
    }

	/**
     * DOCUMENT ME!
     *
     * @param index DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String columnType(int index) {
    	return columnProp(index, "type");
    }

    /**
     * DOCUMENT ME!
     *
     * @param index DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String columnTableName(int index) {
    	return columnProp(index, "tablename");
    }

    private String columnProp(int index, String prop) {
		String resp = "";
        if (index > 0) {
			columnNameAndType();
			if (index <= columnData.size()) {
				Map data = (Map)columnData.get(index - 1);
				resp = (String)data.get(prop);
			}
        }
        return resp;
	}

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] columnNames() {
		return columnProps("name");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] columnTypes() {
		return columnProps("type");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] columnTableNames() {
		return columnProps("tablename");
    }

    private String[] columnProps(String prop) {
		columnNameAndType();
		String[] resp = new String[columnData.size()];
		if (rset.getClass().getSimpleName().equals("LdapResultSet")) {
			resp = new String[resp.length-1];
		}
		for (int i = 0; i < resp.length; i++) {
			Map data = (Map) columnData.get(i);
			String value = (String) data.get(prop);
			resp[i] = value;
		}
		return resp;
	}
    
    private void columnNameAndType() {
        if (columnData == null) {
			columnData = new ArrayList();
	        if (rset != null) {        	
	            try {
	                ResultSetMetaData md = rset.getMetaData();
	                int cont = md.getColumnCount();
	                for (int i = 1; i <= cont; i++) {
	                    String name = md.getColumnLabel(i);
	                    if (name == null || name.trim().equals("")) {
	                      name = md.getColumnName(i);
	                    }
						Map data = new HashMap();	                    
	                    data.put("name", name.trim());
	                    String mdClass = md.getClass().getSimpleName();
	                    if (!mdClass.equals("JdbcLdapMetaData")) {
		                    String type = md.getColumnTypeName(i);
		                    data.put("type", type.trim());
		                    String tableName = "";
		                    try {
		                    	tableName = md.getTableName(i);
			                    if (tableName == null) {
			                    	tableName = "";
			                    }
		                    } catch (Throwable err) {
		                    	// ignorado
		                    }
		                    data.put("tablename", tableName.trim());
	                    }
						columnData.add(data);
	                }
	            } catch (SQLException err) {
	            	// não deve ocorrer
	            }
	        }
        }    
    }

    /**
     * DOCUMENT ME!
     *
     * @param field DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int columnIndex(String field) {
        if ((field == null) || (field.equals(""))) {
            return 0;
        }
        field = field.trim();
        if (rset != null) {
            try {
                return rset.findColumn(field);
            } catch (SQLException err) {
            	// ignorado
            }
        }
        return 0;
    }

    private int columnIndexLdap(String field) {
    	columnNameAndType();
    	if (columnData != null) {
    		for (int i = 0; i < columnData.size(); i++) {
				Map map = (Map) columnData.get(i);
				if (map.get("name").equals(field)) {
					return i + 1;
				}
			}
    	}
    	return 0;
    }	
    
    
    
    /**
     * DOCUMENT ME!
     *
     * @param restrict DOCUMENT ME!
     * @param useLegacy DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Map columns(String restrict, boolean useLegacy) {
        Map map = new HashMap();
        columnNameAndType();
        String valor = new String();
        for (int i = 0; i < columnData.size(); i++) {
        	Map data = (Map) columnData.get(i);
            String nome = ((String)data.get("name")).toLowerCase();
            valor = column(i + 1, restrict);
            if (valor == null) valor = "";
            map.put(nome, valor);
            if (useLegacy) {
                map.put((i + 1) + "", valor);
            }
        }
        return map;
    }

    public Map columns(String restrict) {
    	return columns(restrict, false);
    }
    
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ResultSetMetaData getMetaData() {
        try {
            if (rset != null) {
                return rset.getMetaData();
            }
        } catch (SQLException err) {
           	// não deve ocorrer
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     */
    public void clear() {
        line = 0;
        row = new HashMap();
        try {
            if (rset != null) {
                rset.close();
            }
        } catch (SQLException err) {
           	// não deve ocorrer
        }
        rset = null;
        columnData = null;
    }

    public void setLongTextColumns(String longTextColumns) {
    	String aux = StringA.changeChars(longTextColumns, " ", "");
		this.longTextColumns = aux + ",";
	}

	public void setResultSet(java.sql.ResultSet rset) {
		this.rset = rset;
	}
}
