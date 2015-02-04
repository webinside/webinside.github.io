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

import java.io.OutputStream;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.webinside.runtime.database.ResultSet;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.4 $
 */
public class ResultSetNoSql implements ResultSet {
    private Map headersNoSQL;
    private Map[] rowsNoSQL;
    private int line = 0;
	private Map row = new HashMap();
	private List columnData;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int rowCount() {
        return -1;
    }

    /**
     * DOCUMENT ME!
     *
     * @param row DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int go(int pos) {
        return -1;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int next() {
        if (rowsNoSQL != null) {
            if (line == rowsNoSQL.length) {
                return 0;
            }
            line = line + 1;
            row = (Map)rowsNoSQL[line - 1];
            return line;
        }
        return 0;
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
        return column(field, "");
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
        return column(field, "");
    }

    // Restric exemplo: binary,blob,text
    public String column(String field, String restrict) {
        int indice = columnIndex(field);
        if (indice < 1) {
            return "";
        }
        return column(indice, restrict);
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
        return column(index, "");
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
        return column(index, "");
    }

    // Restric exemplo: binary,blob,text
    public String column(int index, String restrict) {
        if (restrict == null) {
            restrict = "";
        }
        if (line == 0) {
            return "";
        }
        String chave = index + "";
        if (rowsNoSQL != null) {
            if (row.containsKey(chave)) {
                String mresp = (String) row.get(chave);
                if (mresp == null) {
                    mresp = "";
                }
                return mresp;
            } else {
                return "";
            }
        }
        return "";
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
        return -1;
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
        return -1;
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
     * @param index DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String columnTableName(int index) {
        return "";
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
    
    private String[] columnProps(String prop) {
		columnNameAndType();
		String[] resp = new String[columnData.size()];
		for (int i = 0; i < resp.length; i++) {
			Map data = (Map) columnData.get(i);
			resp[i] = (String) data.get(prop);
		}
		return resp;
	}
    
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] columnTableNames() {
        return new String[0];
    }

    private void columnNameAndType() {
        if (columnData == null) {
			columnData = new ArrayList();
	        if (headersNoSQL != null) {
	            int count = 1;
	            while (headersNoSQL.containsKey(count + "")) {
	                String value = (String) headersNoSQL.get(count + "");
	                if (value == null) {
	                    value = "";
	                }
	                Map data = new HashMap();
	                data.put("name", value);
	                data.put("type", "VARCHAR");
	                columnData.add(data);					
	                count = count + 1;
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
        if (headersNoSQL != null) {
            String resp = (String) headersNoSQL.get(field.toUpperCase());
            if (resp == null) {
                resp = "0";
            }
            int nresp = 0;
            try {
                nresp = Integer.parseInt(resp.trim());
                if (nresp < 0) {
                    nresp = 0;
                }
            } catch (NumberFormatException err) {
            }
            return nresp;
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
        if (rowsNoSQL != null) {
            if (line == 0) return map;
            return (Map)rowsNoSQL[line - 1];
        }
        return map;
    }

    /**
     * DOCUMENT ME!
     *
     * @param restrict DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Map columns(String restrict) {
        return columns(restrict, false);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ResultSetMetaData getMetaData() {
        ResultSetMetaDataSimple rs = new ResultSetMetaDataSimple();
        rs.setColumnNames(columnNames());
        rs.setColumnTypes(columnTypes());
        return rs;
    }

    /**
     * DOCUMENT ME!
     */
    public void clear() {
        line = 0;
        row = new HashMap();
        headersNoSQL = null;
        rowsNoSQL = null;
        columnData = null;
    }
    
	public void setHeadersNoSQL(Map headersNoSQL) {
		this.headersNoSQL = headersNoSQL;
	}
	
	public void setRowsNoSQL(Map[] rowsNoSQL) {
		this.rowsNoSQL = rowsNoSQL;
	}
}
