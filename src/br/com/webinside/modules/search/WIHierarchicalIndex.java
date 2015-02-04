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

import br.com.webinside.modules.Util;
import br.com.webinside.runtime.integration.*;
import br.com.webinside.runtime.util.*;

import java.io.*;
import org.apache.lucene.index.*;
import org.apache.lucene.document.*;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class WIHierarchicalIndex extends AbstractConnector
    implements InterfaceParameters {
    //parâmetro de saída
    /** DOCUMENT ME! */
    private static final String ERROR_VAR = "tmp.hierarchicalindex.error";
    /** DOCUMENT ME! */
    private static final String SEARCH_TREE_VAR = "tmp.search.tree";

    //parâmetro de entrada
    /** DOCUMENT ME! */
    private static final String INDEX_NAME_VAR = "tmp.indexName";

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     * @param dbAlias DOCUMENT ME!
     * @param headers DOCUMENT ME!
     */
    public void execute(WIMap context, DatabaseAliases dbAlias,
        InterfaceHeaders headers) {
        String indexName =
            context.get("wi.proj.path") + "/WEB-INF/index/"
            + context.get(WIHierarchicalIndex.INDEX_NAME_VAR);
        IndexReader ir = null;
        try {
            WISearchUtil.closeIndexReader(indexName);

            Indexer ind = new Indexer(indexName, Indexer.UPDATE);
            String[] ef = Util.split(ind.getExtraFields(), ", ");
            ind.getWriter().close();

            StringBuffer sb = new StringBuffer("<SCRIPT>\r\n");
            sb.append("fields = \"");
            for (int i = 0; i < ef.length; i++) {
                sb.append(ef[i]).append("^");
            }
            sb.deleteCharAt(sb.length() - 1).append("\";\r\n");

            ir = IndexReader.open(new File(indexName));
            int countDocs = ir.numDocs();
            WIMap data = new WIMap('^', true);
            for (int i = 0; i < countDocs; i++) {
                if (!ir.isDeleted(i)) {
                    StringBuffer fld = new StringBuffer();
                    for (int j = 0; j < ef.length; j++) {
                        Field f = ir.document(i).getField(ef[j]);

                        //se não existir no índice um campo com o nome da variável
                        //então procure pelo campo com o nome da variável acrescido
                        //de ".content".
                        //Isso é necessário para manter compatibilidade com as
                        //informações geradas pelo novo esquema de indexação.
                        if (f == null) {
                            f = ir.document(i).getField(ef[j] + ".content");
                        }
                        fld.append(f.stringValue());
                        data.put(fld.toString() + "^link",
                            "javascript:sendQuery('" + fld.toString() + "')");
                        //            +fld.toString()+"');return false;\",\"target=_self");
                        fld.append("^");
                    }
                    fld.deleteCharAt(fld.length() - 1);
                }
            }
            sb.append(TreeView.get(data, new I18N().get("Todos os documentos")));
            ir.close();
            sb.append("</SCRIPT>");

            context.put(WIHierarchicalIndex.SEARCH_TREE_VAR, sb.toString());

            context.put("wi.error", "");
        } catch (Exception err) {
            context.put("wi.error", err.toString());
            context.put(WIHierarchicalIndex.ERROR_VAR,
                Util.stackTraceToString(err));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JavaParameter[] getInputParameters() {
        JavaParameter[] jp = new JavaParameter[1];
        jp[0] =
            new JavaParameter(WIHierarchicalIndex.INDEX_NAME_VAR,
                "Nome do índice a ser usado");
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
            new JavaParameter(WIHierarchicalIndex.ERROR_VAR,
                "Variável que conterá o stack trace se ocorrer "
                + "algum erro durante a criação do índice hierárquico.");
        return jp;
    }
}
