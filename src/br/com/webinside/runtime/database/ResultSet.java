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

import java.io.OutputStream;
import java.sql.ResultSetMetaData;
import java.util.Map;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.3 $
 */
public interface ResultSet {

    public static final String binaryFilter =
        "binary,blob,image,text,longchar,longvarchar,bytea";

    public int rowCount();

    public int go(int row);

    public int next();

    public String column(String field);

    public String columnNext(String field);
    
    public String column(String field, boolean binFilter);

    public String column(String field, boolean binFilter, boolean showNull);

    public String column(int index);

    public String columnNext(int index);

    public String column(int index, boolean binFilter);

    public String column(int index, boolean binFilter, boolean showNull);

    public String column(String field, String restrict);

    public String column(int index, String restrict);

    public int columnBin(OutputStream out, String field);

    public int columnBin(OutputStream out, int index);

    public String columnName(int index);

    public String columnType(int index);

    public String columnTableName(int index);

    public ResultSetMetaData getMetaData();

    public String[] columnNames();

    public String[] columnTypes();

    public String[] columnTableNames();

    public int columnIndex(String field);

    public Map columns(String restrict, boolean useLegacy);

    public Map columns(String restrict);

    public void clear();

}
