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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Hashtable;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import br.com.webinside.modules.Util;
import br.com.webinside.modules.parser.HTMLParser;
import br.com.webinside.runtime.exception.UserException;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class WIDocument {
    private String dataSource;
    private boolean store;
    private boolean parseHTML;
    private Hashtable property;

    /**
     * Creates a new WIDocument object.
     */
    WIDocument() {
        store = true;
        parseHTML = false;
        property = new Hashtable();
    }

    /**
     * Creates a new WIDocument object.
     *
     * @param dataSource DOCUMENT ME!
     */
    WIDocument(String dataSource) {
        this();
        this.dataSource = dataSource;
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
     * @param string DOCUMENT ME!
     */
    public void setDataSource(String string) {
        dataSource = string;
    }

    /**
     * DOCUMENT ME!
     *
     * @param parse DOCUMENT ME!
     */
    public void setParseHTML(boolean parse) {
        parseHTML = parse;
    }

    /**
     * DOCUMENT ME!
     *
     * @param store DOCUMENT ME!
     */
    public void setStoreContent(boolean store) {
        this.store = store;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean getParseHTML() {
        return parseHTML;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean getStoreContent() {
        return store;
    }

    /**
     * DOCUMENT ME!
     *
     * @param props DOCUMENT ME!
     */
    public void setProperties(Hashtable props) {
        property = props;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected Hashtable getProperty() {
        return property;
    }

    /**
     * DOCUMENT ME!
     *
     * @param f DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     * @throws UserException
     * @throws IOException
     *
     * @throws java.io.IOException DOCUMENT ME!
     * @throws InterruptedException DOCUMENT ME!
     */
    public Document getDocument(File f) throws IOException, UserException {
        //a propriedade "path" retorna o caminho completo do arquivo incluindo o nome do arquivo.
        //a propriedade "name" retorna apenas o nome do arquivo, depreciada em favor de "fileName".
        //a propriedade "fileName" retorna apenas o nome do arquivo.
        //a propriedade "relativePath" retorna o caminho do diretório do arquivo relativo ao repositório.
        //a propriedade "absolutePath" retorna o caminho completo do diretório onde está o arquivo.
        String[] names =
        {"path", "name", "fileName", "relativePath", "absolutePath"};
        String[] fields =
        {
            f.getPath(), f.getName(), f.getName(),
            Util.getRelativePath(f, new File(dataSource)),
            Util.getAbsolutePath(f)
        };

        return getDocument(names, fields, f);
    }

    /**
     * DOCUMENT ME!
     *
     * @param names DOCUMENT ME!
     * @param fields DOCUMENT ME!
     * @param is DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws java.io.IOException DOCUMENT ME!
     * @throws UserException
     * @throws InterruptedException DOCUMENT ME!
     */
    public Document getDocument(String[] names, String[] fields, 
            File f) throws IOException, UserException {
 
        InputStream is = new FileInputStream(f);
        String fileName = f.getName();
        
        Reader reader = new BufferedReader(new InputStreamReader(is));

        if (fileName.endsWith(".doc") || fileName.endsWith(".pdf")
                || fileName.endsWith(".xls")) {
            String s = null;
            s = DocumentParser.parse(f);
            if (s != null) {
                reader = new StringReader(s);
            }
        }
        StringBuffer sb = new StringBuffer();
        char[] c = new char[1];
        while (reader.read(c) != -1) {
            sb.append(c);
        }
        reader.close();
        return getDocument(names, fields, sb.toString());
    }

    public Document getDocument(String[] names, String[] fields, String content
            ) throws IOException {
        Document doc = new Document();
 
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals("fileName")) {
                //o nome do arquivo é armazenado como uma palavra-chave para poder ser pesquisado
                doc.add(Field.Keyword(names[i], fields[i]));
            } else {
                doc.add(Field.UnIndexed(names[i], fields[i]));
            }
        }
        addContent(doc, content);
        return doc;
    }

    public Document getDocument(String content) throws IOException {
        Document doc = new Document();
        addContent(doc, content);
        return doc;
    }
    
    private void addContent(Document doc, String content) throws IOException {
        Reader reader = new StringReader(content);
        String title = null;
            // realiza o processamento do conteudo HTML
            if (parseHTML) {
                HTMLParser parser = new HTMLParser(reader);
                try {
                    title = parser.getTitle();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                reader = parser.getReader();
            }

            // recupera o título desse documento
            if ((title == null) || title.equals("")) {
                title = "Untitled";
            }
            doc.add(Field.UnIndexed("title", title));
                        
            // conversão dos caracteres acentuados
            reader = WISearchUtil.limparConteudo(reader);

            // alem de indexar o conteúdo, também o armazene a fim de tornar
            // a sua recuperação mais rápida. Isso é aconselhável quando
            // a fonte de dados for um banco de dados porém tem o incoveniente
            // da necessidade de um maior espaço em disco
            if (store) {
                StringBuffer sb = new StringBuffer();
                char[] c = new char[1];

                while (reader.read(c) != -1) {
                    sb.append(c);
                }
                reader.close();

                doc.add(Field.Text("contents", sb.toString()));
            } else {
                //apenas toquenize e indexe o conteúdo sem armazená-lo...
                doc.add(Field.Text("contents", reader));
            }

            // adiciona como palavras-chaves do documento
            if (!property.isEmpty()) {
                Enumeration e = property.keys();
                while (e.hasMoreElements()) {
                    String elem = (String) e.nextElement();

                    //doc.add(Field.Keyword(elem, (String) property.get(elem)));
                    //toqueniza e indexa o conteúdo tratado (?!) mas não o armazena
                    doc.add(Field.UnStored(elem,
                            WISearchUtil.limparConteudo(
                                (String) property.get(elem))));

                    //apenas armazena o conteúdo originalmente como veio da aplicação
                    //para acessar esse conteúdo basta acrescentar o sufixo .content ao nome da propriedade
                    doc.add(Field.UnIndexed(elem + ".content",
                            (String) property.get(elem)));
                }
            }
    }

}
