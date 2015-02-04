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

package br.com.webinside.modules.search;

import java.util.*;
import java.io.*;

//import org.apache.lucene.index.*;
public class IndexProperties {
    /** DOCUMENT ME! */
    public static final char SEPARATOR = '^';
    /** DOCUMENT ME! */
    public static final String FILENAME = "index.properties";
    /** DOCUMENT ME! */
    public static final String FILEDAT = "index.dat";
    private Properties props;
    private String extraFields;

    /**
     * Creates a new IndexProperties object.
     */
    public IndexProperties() {
        props = new Properties();
    }

    /**
     * Creates a new IndexProperties object.
     *
     * @param indexPath DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws FileNotFoundException DOCUMENT ME!
     */
    public IndexProperties(String indexPath)
        throws IOException, FileNotFoundException {
        this();
        File file = new File(indexPath, IndexProperties.FILENAME);
        if (file.exists()) {
            loadProperties(file);
        }
    }

    private void loadProperties(File file) throws IOException {
        // carrega o arquivo de propriedades do indice a ser re-indexado
        FileInputStream fis = new FileInputStream(file);
        props.load(fis);
        fis.close();

        // preenche as propriedades do objeto de acordo
        // com as informacoes constantes no arquivo
        /*dataSource = props.getProperty("dataSource");
           indexType = Integer.parseInt(props.getProperty("indexType"));
           doc.setParseHTML(props.getProperty("parseHTML", "").equalsIgnoreCase("true"));*/
        extraFields = props.getProperty("extraFields", "");
        /*if (indexType == Indexer.DS_DB) {
           query = props.getProperty("query");
           indexedColumns = props.getProperty("indexedColumns");
           } else if (indexType == Indexer.DS_DIR) {
             recursive = props.getProperty("recursive", "").equalsIgnoreCase("true");
             mask = parseMask(props.getProperty("mask", ""));
           }*/
    }

    /**
     * DOCUMENT ME!
     *
     * @param ds DOCUMENT ME!
     */
    public void setDataSource(String ds) {
        props.setProperty("dataSource", ds);
    }

    /**
     * DOCUMENT ME!
     *
     * @param extraFields DOCUMENT ME!
     */
    public void setExtraFields(String extraFields) {
        props.setProperty("extraFields", extraFields);
        this.extraFields = extraFields;
    }

    /**
     * DOCUMENT ME!
     *
     * @param mask DOCUMENT ME!
     */
    public void setMask(String mask) {
        props.setProperty("mask", mask);
    }

    /**
     * DOCUMENT ME!
     *
     * @param indexType DOCUMENT ME!
     */
    public void setIndexType(int indexType) {
        props.setProperty("indexType", String.valueOf(indexType));
    }

    /**
     * DOCUMENT ME!
     *
     * @param recursive DOCUMENT ME!
     */
    public void setRecursive(boolean recursive) {
        props.setProperty("recursive", String.valueOf(recursive));
    }

    /**
     * DOCUMENT ME!
     *
     * @param query DOCUMENT ME!
     */
    public void setQuery(String query) {
        props.setProperty("query", query);
    }

    /**
     * DOCUMENT ME!
     *
     * @param parse DOCUMENT ME!
     */
    public void setParseHTML(boolean parse) {
        props.setProperty("parseHTML", String.valueOf(parse));
    }

    /**
     * DOCUMENT ME!
     *
     * @param store DOCUMENT ME!
     */
    public void setStoreContent(boolean store) {
        props.setProperty("storeContent", String.valueOf(store));
    }

    /**
     * DOCUMENT ME!
     *
     * @param primaryKeys DOCUMENT ME!
     */
    public void setPrimaryKeys(String primaryKeys) {
        props.setProperty("primaryKeys", primaryKeys);
    }

    /**
     * DOCUMENT ME!
     *
     * @param indexedColumns DOCUMENT ME!
     */
    public void setIndexedColumns(String indexedColumns) {
        props.setProperty("indexedColumns", indexedColumns);
    }

    /**
     * DOCUMENT ME!
     *
     * @param key DOCUMENT ME!
     * @param value DOCUMENT ME!
     */
    public void setProperty(String key, String value) {
        props.setProperty(key, value);
    }

    /**
     * DOCUMENT ME!
     *
     * @param key DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getProperty(String key) {
        return props.getProperty(key, "");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String[] getExtraFields() {
        StringTokenizer st = new StringTokenizer(extraFields, ", ");
        String[] result = new String[st.countTokens()];
        int i = 0;

        while (st.hasMoreTokens()) {
            result[i++] = st.nextToken();
        }

        return result;
    }

    // testando...
    public void appendExtraFieldsInfo(Hashtable ht)
        throws IOException {
        File file = new File("c:/temp", FILEDAT);
        if (!file.exists()) {
            file.createNewFile();
        }

        String[] ef = getExtraFields();
        FileWriter fw = new FileWriter(file.getAbsolutePath(), true);
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ef.length; i++) {
            sb.append((String) ht.get(ef[0])).append(SEPARATOR);
        }
        sb.deleteCharAt(sb.length());
        fw.write(sb + "\r\n");
        fw.close();
    }
}
