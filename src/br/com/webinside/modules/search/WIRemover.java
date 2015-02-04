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

import java.util.Hashtable;
import java.util.StringTokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;

import br.com.webinside.modules.Util;
import br.com.webinside.runtime.integration.*;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class WIRemover extends AbstractConnector implements InterfaceParameters {
    //parâmetro de entrada
    /** DOCUMENT ME! */
    private static final String IDLIST_VAR = "tmp.idList";
    /** DOCUMENT ME! */
    private static final String INDEX_NAME_VAR = "tmp.indexName";

    //parâmetro de saída
    /** DOCUMENT ME! */
    private static final String ERROR_VAR = "tmp.remover.error";

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     * @param databases DOCUMENT ME!
     * @param headers DOCUMENT ME!
     */
    public void execute(WIMap context, DatabaseAliases databases,
        InterfaceHeaders headers) {
        try {
            synchronized (this) {
                // o nome do indice a ser alterado
                String indexName =
                    context.get("wi.proj.path") + "/WEB-INF/index/"
                    + context.get(WIRemover.INDEX_NAME_VAR);

                // verifica se o indice a ser alterado ja foi aberto para pesquisa
                // e armazenado em cache.
                // Se sim, fecha-o para que possa ser feita a atualizacao.
                IndexReader ir = null;
                CachedIndex ci = null;
                Hashtable indexCache = CachedIndex.indexCache;
                if (indexCache != null) {
                    ci = (CachedIndex) indexCache.get(indexName);
                    ir = (ci != null) ? ci.getReader()
                                      : IndexReader.open(indexName);
                }

                // lista de IDDocs dos documentos a serem removidos do indice
                String idList = context.get(WIRemover.IDLIST_VAR);
                StringTokenizer st = new StringTokenizer(idList, ", ");
                while (st.hasMoreTokens()) {
                    String token = st.nextToken();
                    Term term = new Term("iddoc", token);
                    context.put("tmp.freq", String.valueOf(ir.delete(term)));
                }

                // se o indice nao foi recuperado do cache, feche-o
                if (ci == null) {
                    ir.close();
                } else {
                    ci.close();
                    indexCache.remove(indexName);
                }

                IndexWriter writer =
                    new IndexWriter(indexName, new StandardAnalyzer(), false);
                writer.optimize();
                writer.close();

                //        if(context.get("tmp.erase").equalsIgnoreCase("on")) {
                //          StringTokenizer stNames = new StringTokenizer(context.get("tmp.nameList"), ", ");
                //          st = new StringTokenizer(idList, ", ");
                //          while (stNames.hasMoreTokens()) {
                //            String docName = stNames.nextToken();
                //            String token = st.nextToken();
                //            File fl = new File(context.get("pvt.pubDir")+"/"+docName);
                //            fl.delete();
                //            fl = new File(context.get("pvt.pubDir")+"/imagens/"+token);
                //            File[] fls = fl.listFiles();
                //            for (int i=0;fls!=null && i<fls.length; i++) fls[i].delete();
                //            fl.delete();
                //          }
                //        }
            }
        } catch (Exception e) {
            context.put(WIRemover.ERROR_VAR, Util.stackTraceToString(e));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JavaParameter[] getInputParameters() {
        JavaParameter[] jp = new JavaParameter[2];
        jp[0] =
            new JavaParameter(WIRemover.INDEX_NAME_VAR,
                "Nome do índice a ser usado");
        jp[1] =
            new JavaParameter(WIRemover.IDLIST_VAR,
                "IDs dos documentos a serem excluídos",
                "Lista dos IDs dos documentos separados por vírgula a serem excluídos do índice.");
        return jp;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JavaParameter[] getOutputParameters() {
        JavaParameter[] jp = new JavaParameter[1];
        jp[0] =
            new JavaParameter(WIRemover.ERROR_VAR,
                "Variável que conterá o stack trace se ocorrer algum erro "
                + "durante a remoção de informações do índice.");
        return jp;
    }
}
