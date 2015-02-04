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

import java.io.*;
import java.util.Hashtable;
import org.apache.lucene.index.IndexReader;

import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class WISearchUtil {
    /** DOCUMENT ME! */
    private static final boolean DEBUG = false;
    /** DOCUMENT ME! */
    private static final String DELIMITER = "|";
    
    private static Hashtable extraField;

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     * @param content DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String processContent(WIMap context, String content) {
        StringBuffer sb = new StringBuffer();

        int iniDelim = -1;
        int endDelim = -1;
        int offset = 0;
        while ((iniDelim = content.indexOf(WISearchUtil.DELIMITER, offset)) != -1) {
            sb.append(content.substring(offset, iniDelim));
            if ((
                            endDelim =
                                content.indexOf(WISearchUtil.DELIMITER,
                                    iniDelim + 1)
                        ) != -1) {
                String var = content.substring(iniDelim + 1, endDelim);
                sb.append(context.get(var));
            }
            offset = endDelim + 1;
        }
        sb.append(content.substring(offset, content.length()));

        return sb.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param txt DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String limparConteudo(String txt) {
        txt = txt.replace('á', 'a');
        txt = txt.replace('Á', 'A');
        txt = txt.replace('é', 'e');
        txt = txt.replace('É', 'E');
        txt = txt.replace('í', 'i');
        txt = txt.replace('Í', 'I');
        txt = txt.replace('ó', 'o');
        txt = txt.replace('Ó', 'O');
        txt = txt.replace('ú', 'u');
        txt = txt.replace('Ú', 'U');
        txt = txt.replace('à', 'a');
        txt = txt.replace('À', 'A');
        txt = txt.replace('è', 'e');
        txt = txt.replace('È', 'E');
        txt = txt.replace('ì', 'i');
        txt = txt.replace('Ì', 'I');
        txt = txt.replace('ò', 'o');
        txt = txt.replace('Ò', 'O');
        txt = txt.replace('ù', 'u');
        txt = txt.replace('Ù', 'U');
        txt = txt.replace('â', 'a');
        txt = txt.replace('Â', 'A');
        txt = txt.replace('ê', 'e');
        txt = txt.replace('Ê', 'E');
        txt = txt.replace('î', 'i');
        txt = txt.replace('Î', 'I');
        txt = txt.replace('ô', 'o');
        txt = txt.replace('Ô', 'O');
        txt = txt.replace('û', 'u');
        txt = txt.replace('Û', 'U');
        txt = txt.replace('ä', 'a');
        txt = txt.replace('Ä', 'A');
        txt = txt.replace('ë', 'e');
        txt = txt.replace('Ë', 'E');
        txt = txt.replace('ï', 'i');
        txt = txt.replace('I', 'I');
        txt = txt.replace('ö', 'o');
        txt = txt.replace('Ö', 'O');
        txt = txt.replace('ü', 'u');
        txt = txt.replace('Ü', 'U');
        txt = txt.replace('ã', 'a');
        txt = txt.replace('Ã', 'A');
        txt = txt.replace('õ', 'o');
        txt = txt.replace('Õ', 'O');
        txt = txt.replace('ç', 'c');
        txt = txt.replace('Ç', 'C');
        return txt;
    }

    /**
     * DOCUMENT ME!
     *
     * @param reader DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public static Reader limparConteudo(Reader reader)
        throws IOException {
        char[] c = new char[1];
        StringWriter sw = new StringWriter();

        while (reader.read(c) != -1) {
            sw.write(c);
        }
        reader.close();
        sw.close();

        return new StringReader(WISearchUtil.limparConteudo(sw.toString()));
    }

    /**
     * DOCUMENT ME!
     *
     * @param msg DOCUMENT ME!
     */
    public static void debug(Object msg) {
        System.out.println(msg);
    }

    /**
     * Método que retorna um identificador único para um documento a ser
     * inserido no índice. A informação sobre o contador está armazenado no
     * arquivo "counter.dat" que contém o próximo identificador a ser
     * associado a um documento. Esse identificador sera útil quando se quiser
     * que um documento específico seja removido do índice pois a numeração
     * gerada pelo próprio índice não garante que o mesmo documento sempre
     * terá o mesmo número (chaves efêmeras).
     *
     * @param path diretório onde se encontra o arquivo "counter.dat"
     *
     * @return um identificador único para um documento a ser inserido no
     *         índice.
     *
     * @throws IOException DOCUMENT ME!
     * @throws FileNotFoundException DOCUMENT ME!
     */
    public static synchronized String getIDDoc(String path)
        throws IOException, FileNotFoundException {
        int count = 0;
        File file = new File(path, "counter.dat");

        if (!file.exists()) {
            //se o arquivo que armazena o contador não existir, crie-o
            file.createNewFile();
        } else {
            //se o arquivo já existir, recupere o valor do contador
            ObjectInputStream ois =
                new ObjectInputStream(new FileInputStream(file));
            count = ois.readInt();
            ois.close();
        }

        //atualize o contador incrementando-o de 1
        ObjectOutputStream oos =
            new ObjectOutputStream(new FileOutputStream(file));
        oos.writeInt(count + 1);
        oos.close();

        return String.valueOf(count);
    }

    /**
     * Metodo que verifica se o índice já foi aberto para pesquisa e armazenado
     * em cache. Se sim, fecha-o para que possa ser feita a atualização.
     *
     * @param indexName DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public static synchronized void closeIndexReader(String indexName)
        throws IOException {
        Hashtable indexCache = CachedIndex.indexCache;
        CachedIndex ci = null;
        if (indexCache != null) {
            ci = (CachedIndex) indexCache.get(indexName);
            if (ci != null) {
                ci.close();
                //testando...
                //um codigo abaixo foi comentado em virtude disso
                // remove do cache o IndexReader que está instanciado
                indexCache.remove(indexName);
                if (DEBUG) {
                    WISearchUtil.debug(
                        "Indice aberto para leitura, removendo-o!");
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public static IndexReader getIndexReader(String name)
        throws IOException {
        CachedIndex index = (CachedIndex) CachedIndex.indexCache.get(name);

        // look in cache
        if ((index != null)
                    && (index.modified == IndexReader.getCurrentVersion(name))) {
            return index.reader; // cache hit
        } else {
            index = new CachedIndex(name); // cache miss
        }
        CachedIndex.indexCache.put(name, index); // add to cache
        return index.reader;
    }

    /**
     * Método que armazena as informações adicionais de um documento em um
     * arquivo. O conteúdo do arquivo é um objeto Hashtable serializado onde a
     * chave dos elementos é o nome de um arquivo e o valor e um outro objeto
     * Hashtable onde estão armazenadas as propriedades adicinais do documento
     * representado por este arquivo.
     *
     * @param indexName DOCUMENT ME!
     * @param fileName DOCUMENT ME!
     * @param ht DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws ClassNotFoundException DOCUMENT ME!
     * @throws OptionalDataException DOCUMENT ME!
     * @throws FileNotFoundException DOCUMENT ME!
     */
    public static synchronized void appendExtraFieldsInfo(String indexName,
        String fileName, Hashtable ht)
        throws IOException, ClassNotFoundException, OptionalDataException, 
            FileNotFoundException {
        String indexPath = new File(indexName).getAbsolutePath();
        File file = new File(indexPath, "index.dat");

        if (!file.exists()) {
            extraField = new Hashtable();
            file.createNewFile();
        } else if (extraField == null) {
            ObjectInputStream ois =
                new ObjectInputStream(new FileInputStream(file));
            extraField = (Hashtable) ois.readObject();
            ois.close();
        }

        ObjectOutputStream oos =
            new ObjectOutputStream(new FileOutputStream(file));
        extraField.put(fileName, ht);
        oos.writeObject(extraField);
        oos.close();
    }
}
