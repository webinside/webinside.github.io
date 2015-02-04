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

import java.sql.*;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class ResultSetMetaDataSimple implements ResultSetMetaData {
    /** DOCUMENT ME! */
    private String[] columnNames = new String[0];
    /** DOCUMENT ME! */
    private String[] columnLabels = new String[0];
    /** DOCUMENT ME! */
    private String[] columnTypes = new String[0];
    /** DOCUMENT ME! */
    private int[] columnSizes = new int[0];

    /**
     * Creates a new ResultSetMetaDataSimple object.
     */
    public ResultSetMetaDataSimple() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getColumnClassName(int column) {
        return "";
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isDefinitelyWritable(int column) {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isWritable(int column) {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isReadOnly(int column) {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getColumnTypeName(int column) {
        if (columnTypes == null) {
            return "VARCHAR"; // default
        }
        if ((column < 1) || (column > columnTypes.length)) {
            return "";
        }
        return columnTypes[column - 1];
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getColumnType(int column) {
        String type = getColumnTypeName(column);
        if (type.equalsIgnoreCase("VARCHAR")) {
            return Types.VARCHAR;
        }
        if (type.equalsIgnoreCase("TEXT")) {
            return Types.LONGVARCHAR;
        }
        if (type.equalsIgnoreCase("CHAR")) {
            return Types.CHAR;
        }
        if (type.equalsIgnoreCase("DECIMAL")) {
            return Types.DECIMAL;
        }
        if (type.equalsIgnoreCase("DOUBLE")) {
            return Types.DOUBLE;
        }
        if (type.equalsIgnoreCase("FLOAT")) {
            return Types.FLOAT;
        }
        if (type.equalsIgnoreCase("INTEGER")) {
            return Types.INTEGER;
        }
        if (type.equalsIgnoreCase("SMALLINT")) {
            return Types.SMALLINT;
        }
        if (type.equalsIgnoreCase("DATE")) {
            return Types.DATE;
        }
        if (type.equalsIgnoreCase("TIME")) {
            return Types.TIME;
        }
        return Types.OTHER;
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getCatalogName(int column) {
        return "";
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getTableName(int column) {
        return "";
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getScale(int column) {
        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getPrecision(int column) {
        return 0;
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSchemaName(int column) {
        return "";
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getColumnName(int column) {
        if ((column < 1) || (column > columnNames.length)) {
            return "";
        }
        return columnNames[column - 1];
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getColumnLabel(int column) {
        if ((column < 1) || (column > columnLabels.length)) {
            return "";
        }
        return columnLabels[column - 1];
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getColumnDisplaySize(int column) {
        if (columnSizes == null) {
            return 20; // default
        }
        if ((column < 1) || (column > columnSizes.length)) {
            return 0;
        }
        return columnSizes[column - 1];
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isSigned(int column) {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int isNullable(int column) {
        return columnNullableUnknown;
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isCurrency(int column) {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isSearchable(int column) {
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isCaseSensitive(int column) {
        return true;
    }

    /**
     * DOCUMENT ME!
     *
     * @param column DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isAutoIncrement(int column) {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getColumnCount() {
        return columnNames.length;
    }
        
	public void setColumnLabels(String[] columnLabels) {
		this.columnLabels = columnLabels;
	}
	
	public void setColumnNames(String[] columnNames) {
		this.columnNames = columnNames;
	}
	
	public void setColumnSizes(int[] columnSizes) {
		this.columnSizes = columnSizes;
	}
	
	public void setColumnTypes(String[] columnTypes) {
		this.columnTypes = columnTypes;
	}

	public boolean isWrapperFor(Class arg0) throws SQLException {
		return false;
	}

	public Object unwrap(Class arg0) throws SQLException {
		return null;
	}
}
