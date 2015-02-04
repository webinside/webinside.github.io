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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import br.com.webinside.modules.Util;
import br.com.webinside.runtime.exception.UserException;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class Indexer {
    /** DOCUMENT ME! */
    public static final int DS_DIR = 1;

    //	public static final int DS_DB = 2;
    /** DOCUMENT ME! */
    public static final int CREATE = 10;
    /** DOCUMENT ME! */
    public static final int DELETE = 20;
    /** DOCUMENT ME! */
    public static final int UPDATE = 30;
    /** DOCUMENT ME! */
    public static final int READ_CONF = 40;
    /** DOCUMENT ME! */
    public static final String[] STOP_WORDS;

    static {
        String[] stopWords = new String[0];
        BufferedReader br = null;
        try {
        	InputStream is = Indexer.class.getResourceAsStream("stopwords.txt");
        	InputStreamReader isr = new InputStreamReader(is);
        	br = new BufferedReader(isr);
            HashSet hs = new HashSet();
            String line = null;
            while ((line = br.readLine()) != null) {
                hs.add(line);
            }
            stopWords = new String[hs.size()];
            int j = 0;
            for (Iterator i = hs.iterator(); i.hasNext();) {
                stopWords[j++] = (String) i.next();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            STOP_WORDS = stopWords;
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean reindex = false;
    private Hashtable hasht = null;
    private boolean recursive;
    private boolean parseHTML;
    private int indexType;
    private String indexName;
    private String dataSource;
    private String indexedColumns;
    private String extraFields;
    private String mask;
    private IndexWriter writer;
    private WIDocument doc;
    private Properties props;

    /**
     * Creates a new Indexer object.
     *
     * @param indexName DOCUMENT ME!
     * @param action DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public Indexer(String indexName, int action) throws IOException {
        this.indexName = indexName;

        doc = new WIDocument();
        props = new Properties();

        if (action == Indexer.CREATE) {
            File file = new File(this.indexName);
            boolean dirExists = file.exists();
            if (!dirExists) {
                dirExists = file.mkdirs();
            }
            if (dirExists) {
                writer =
                    new IndexWriter(this.indexName,
                        new StandardAnalyzer(Indexer.STOP_WORDS), true);
            }
        } else if (action == Indexer.UPDATE) {
            writer =
                new IndexWriter(this.indexName,
                    new StandardAnalyzer(Indexer.STOP_WORDS), false);
            loadProperties();
        } else if (action == Indexer.READ_CONF) {
            loadProperties();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param f DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public File getFile(File f) {
        return getFile(f.getAbsolutePath());
    }

    /**
     * Este método faz uma busca no repositório de arquivos do índice por um
     * arquivo baseado pelo nome indicado no parâmetro <code>file</code>. Se
     * existirem vários arquivos com o mesmo nome a primeira ocorrência será
     * retornada, se não existir nenhum arquivo com este nome o método irá
     * retornar <code>null</code>.
     *
     * @param file Arquivo a ser procurado.
     *
     * @return um objeto File do arquivo passado como parâmetro, caso exista
     *         vários arquivos com o mesmo nome no repositório de arquivos do
     *         índice será retornado primeiro arquivo encontrado. Se nenhum
     *         arquivo for encontrado, retorna null.
     */
    public File getFile(String file) {
        return getFileFromDir(new File(getDataSource()), file);
    }

    private File getFileFromDir(File file, String arq) {
        File result = null;

        if (file.isDirectory()) {
            File[] files = file.listFiles();

            for (int i = 0; (i < files.length) && (result == null); i++) {
                result = getFileFromDir(files[i], arq);
            }
        } else if (file.getName().endsWith(arq)) {
            result = file;
        }
        return result;
    }

    /**
     * Método responsavel pela reindexacao de um indice. Primeiramente fecha o
     * IndexReader desse índice caso ele se encontre aberto em virtude de
     * alguma realização de pesquisas. Logo após, o arquivo index.dat que
     * contém os valores das propriedades adicionais de cada um dos documentos
     * é lido. Esse arquivo é composto por um objeto Hashtable onde a chave é
     * o caminho absoluto do arquivo que representa o documento e o valor é
     * outro objeto Hashtable que representa as propriedades adicionais desse
     * arquivo. Em seguida, o índice existente é apagado e um novo índice
     * começará a ser criado.
     *
     * @throws ClassNotFoundException DOCUMENT ME!
     * @throws IOException
     * @throws UserException
     */
    public synchronized void reindex()
        throws ClassNotFoundException, IOException, UserException {
        reindex = true;

        if (writer != null) {
            writer.close();
        }
        WISearchUtil.closeIndexReader(indexName);
        loadProperties();

        // recupera o caminho absoluto onde foi gravado o arquivo com as
        // informacoes sobre o indice (index.properties)
        String indexPath = new File(indexName).getAbsolutePath();

        // recupera o arquivo counter.dat responsavel pela armazenamento
        // do proximo iddoc a ser atribuido a um documento
        int count = 0;
        File counterFile = new File(indexPath, "counter.dat");

        if (counterFile.exists()) {
            // se o arquivo ja existir, recupere o valor do contador
            ObjectInputStream ois =
                new ObjectInputStream(new FileInputStream(counterFile));
            count = ois.readInt();
            ois.close();
        }

        // nome do arquivo que contem os valores das propriedades adicionais
        // de cada um dos documentos do indice
        File file = new File(indexPath, "index.dat");

        if (file.exists()) {
            ObjectInputStream ois =
                new ObjectInputStream(new FileInputStream(file));
            hasht = (Hashtable) ois.readObject();
            ois.close();
        } else {
            hasht = new Hashtable();
        }

        // remova o indice existente...
        File f = new File(indexName);
        if (f.exists()) {
            deleteIndex(f);
        }

        writer = new IndexWriter(this.indexName, new StandardAnalyzer(), true);
        doIt();

        // atualize o contador incrementando-o de 1
        if (count > 0) {
            ObjectOutputStream oos =
                new ObjectOutputStream(new FileOutputStream(counterFile));
            oos.writeInt(count);
            oos.close();
        }

        reindex = false;
    }

    /**
     * Método que carrega as propriedades do índice.
     *
     * @throws IOException DOCUMENT ME!
     */
    private void loadProperties() throws IOException {
        // carrega o arquivo de propriedades do índice a ser re-indexado
        FileInputStream fis =
            new FileInputStream(indexName + "/index.properties");
        props.load(fis);
        fis.close();

        // preenche as propriedades do objeto de acordo
        // com as informacoes constantes no arquivo
        dataSource = props.getProperty("dataSource");
        indexType = Integer.parseInt(props.getProperty("indexType"));
        parseHTML = props.getProperty("parseHTML", "").equalsIgnoreCase("true");
        doc.setParseHTML(parseHTML);
        extraFields = props.getProperty("extraFields", "");
        /*if (indexType == Indexer.DS_DB) {
           query = props.getProperty("query");
           indexedColumns = props.getProperty("indexedColumns");
           } else*/
        if (indexType == Indexer.DS_DIR) {
            recursive =
                props.getProperty("recursive", "").equalsIgnoreCase("true");
            mask = props.getProperty("mask", "");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param ds DOCUMENT ME!
     */
    public void setDataSource(String ds) {
        dataSource = ds;
        props.setProperty("dataSource", dataSource);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDataSource() {
        return dataSource;
    }

    /**
     * DOCUMENT ME!
     *
     * @param extraFields DOCUMENT ME!
     */
    public void setExtraFields(String extraFields) {
        this.extraFields = extraFields;
        props.setProperty("extraFields", this.extraFields);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getExtraFields() {
        return extraFields;
        //    StringTokenizer st = new StringTokenizer(extraFields, ", ");
        //    String[] result = new String[st.countTokens()];
        //    int i = 0;
        //
        //    while (st.hasMoreTokens()) {
        //      result[i++] = st.nextToken();
        //    }
        //
        //    return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param mask DOCUMENT ME!
     */
    public void setMask(String mask) {
        this.mask = mask;
        props.setProperty("mask", mask);
        //this.mask = parseMask(mask);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getMask() {
        return mask;
    }

    /**
     * DOCUMENT ME!
     *
     * @param indexType DOCUMENT ME!
     */
    public void setIndexType(int indexType) {
        this.indexType = indexType;
        props.setProperty("indexType", String.valueOf(indexType));
    }

    /**
     * DOCUMENT ME!
     *
     * @param recursive DOCUMENT ME!
     */
    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
        props.setProperty("recursive", String.valueOf(recursive));
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean getRecursive() {
        return recursive;
    }

    /**
     * DOCUMENT ME!
     *
     * @param parse DOCUMENT ME!
     */
    public void setParseHTML(boolean parse) {
        doc.setParseHTML(parse);
        props.setProperty("parseHTML", String.valueOf(parse));
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean getParseHTML() {
        return props.getProperty("parseHTML").equalsIgnoreCase("true");
    }

    /**
     * DOCUMENT ME!
     *
     * @param store DOCUMENT ME!
     */
    public void setStoreContent(boolean store) {
        doc.setStoreContent(store);
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
        this.indexedColumns = indexedColumns;
        props.setProperty("indexedColumns", indexedColumns);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getIndexedColumnsCount() {
        StringTokenizer st = new StringTokenizer(indexedColumns, ",");
        return st.countTokens();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public IndexWriter getWriter() {
        return writer;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public WIDocument getWIDocument() {
        doc.setDataSource(dataSource);
        doc.setParseHTML(parseHTML);
        return doc;
    }

    /**
     * DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws UserException
     */
    public synchronized void doIt() throws IOException, UserException {
        boolean indexed = false;
        writer.mergeFactor = 20;

        try {
            if (indexType == Indexer.DS_DIR) {
                indexed = indexDocs(new File(dataSource));
            }
        } finally {
            FileOutputStream fos =
                new FileOutputStream(indexName + "/index.properties");
            props.store(fos, "WI 3.x - Informacoes sobre indice");
            fos.close();

            if (indexed) {
                writer.optimize();
            }
            writer.close();
        }
    }

    /**
     * Metodo que verifica se o nome de um arquivo (file) termina com uma das
     * extensoes que faz parte do array de strings (mask). Caso nao haja
     * nenhuma mascara definida o metodo retorna true.
     *
     * @param file DOCUMENT ME!
     * @param masks DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private boolean containMask(String file, String[] masks) {
        boolean contain = false;
        if ((masks != null) && (masks.length > 0)) {
            int i = masks.length;

            while ((i > 0) && !contain) {
                //by LRBS 2.1.5b21
                contain = file.toLowerCase().endsWith(masks[--i].toLowerCase());
                //-by LRBS
            }
        } else {
            contain = true;
        }

        return contain;
    }

    public boolean indexDocs(String content) throws UserException {
        try {
            writer.addDocument(doc.getDocument(content));
        } catch (IOException e) {
            throw new UserException(e);
        }
        return true;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param file
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     * @throws UserException
     *  
     */
    public boolean indexDocs(File file) throws UserException {
        boolean result = false;
        if (file.isDirectory()) {
            File[] files = file.listFiles();

            for (int i = 0; i < files.length; i++) {
                if (files[i].isDirectory() && !recursive) {
                    continue;
                } else /*if (files[i].isFile() && containMask(files[i].getName(), mask)
                   || files[i].isDirectory())*/
                 {
                    indexDocs(files[i]);
                }
            }
        } else if (containMask(file.getName(), Util.split(mask, ", "))) {
            // reindaxação...
            try {
            if (reindex) {
                Hashtable ef = (Hashtable) hasht.get(file.getAbsolutePath());
                WISearchUtil.appendExtraFieldsInfo(indexName,
                    file.getAbsolutePath(), ef);
                doc.setProperties(ef);
            }

            writer.addDocument(doc.getDocument(file));
            result = true;
            } catch (Exception e) {
                throw new UserException(e);
            }
        }

        return result;
    }

    private void deleteIndex(File file) throws IOException {
        //Directory directory = new FSDirectory(file, false);
        Directory directory = FSDirectory.getDirectory(file, true);
        /*IndexReader reader = IndexReader.open(directory);
           for (int i = 0; i < reader.numDocs(); i++) {
             reader.delete(i);
           }
           reader.close();*/
        directory.close();
    }
}
