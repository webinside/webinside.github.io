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
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class WIIndexer extends AbstractConnector implements InterfaceParameters {
    //parâmetros de entrada
    /** DOCUMENT ME! */
    public static final String ACTION_VAR = "tmp.acao";
    /** DOCUMENT ME! */
    public static final String INDEX_NAME_VAR = "tmp.indexName";
    /** DOCUMENT ME! */
    public static final String DIR_VAR = "tmp.dirName";
    /** DOCUMENT ME! */
    public static final String PROPERTIES_VAR = "tmp.properties";
    /** DOCUMENT ME! */
    public static final String FILE_MASK_VAR = "tmp.mask";
    /** DOCUMENT ME! */
    public static final String RECURSIVE_VAR = "tmp.recursive";
    /** DOCUMENT ME! */
    public static final String PARSE_HTML_VAR = "tmp.parseHTML";

    //parâmetro de saída
    /** DOCUMENT ME! */
    public static final String ERROR_VAR = "tmp.indexer.error";

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
            // indica qual a ação a ser feita: criar um indice (create),
            // reindexar (update) ou ler a configuração (read_conf) de um já existente.
            int action = 0;
            String acao = context.get(WIIndexer.ACTION_VAR).toLowerCase();
            if (acao.equals("create")) {
                action = Indexer.CREATE;
            } else if (acao.equals("update")) {
                action = Indexer.UPDATE;
            } else if (acao.equals("read_conf")) {
                action = Indexer.READ_CONF;
            }

            // indica se sera criado um indice hierarquico
            //boolean indHierarquico = !parm1.get("tmp.indHierarquico").equals("");
            // caminho onde serao colocados os arquivos do indice
            String indexName =
                context.get("wi.proj.path") + "/WEB-INF/index/"
                + context.get(WIIndexer.INDEX_NAME_VAR);

            Indexer ind = new Indexer(indexName, action);

            if (action == Indexer.UPDATE) {
                ind.reindex();
            } else if (action == Indexer.CREATE) {
                //int dsType = context.get("tmp.dsType").equals("dir")	? Indexer.DS_DIR : Indexer.DS_DB;
                int dsType = Indexer.DS_DIR;
                String ds =
                    (dsType == Indexer.DS_DIR) ? context.get(WIIndexer.DIR_VAR)
                                               : context.get("tmp.url");
                ind.setIndexType(dsType);
                ind.setDataSource(ds);
                ind.setParseHTML((!context.get(WIIndexer.PARSE_HTML_VAR).equals("")));
                ind.setStoreContent((!context.get("tmp.storeContent").equals("")));

                String extraFields = context.get(WIIndexer.PROPERTIES_VAR);
                if (!extraFields.equals("")) {
                    ind.setExtraFields(extraFields);
                }
                /*if (dsType == Indexer.DS_DB) {
                   ind.setQuery(context.get("tmp.query"));
                   ind.setPrimaryKeys(context.get("tmp.keys"));
                   ind.setIndexedColumns(context.get("tmp.ic"));
                   } else*/
                if (dsType == Indexer.DS_DIR) {
                    ind.setRecursive((!context.get(WIIndexer.RECURSIVE_VAR)
                                .equals("")));
                    String mask = context.get(WIIndexer.FILE_MASK_VAR);
                    if (!mask.equals("")) {
                        ind.setMask(mask);
                    }
                }
                ind.doIt();
            } else if (action == Indexer.READ_CONF) {
                context.put(WIIndexer.DIR_VAR, ind.getDataSource());
                context.put(WIIndexer.PROPERTIES_VAR, ind.getExtraFields());
                context.put(WIIndexer.FILE_MASK_VAR, ind.getMask());
                context.put(WIIndexer.RECURSIVE_VAR,
                    String.valueOf(ind.getRecursive()));
                context.put(WIIndexer.PARSE_HTML_VAR,
                    String.valueOf(ind.getParseHTML()));
            }
        } catch (Exception e) {
            this.getParams().getErrorLog().write("WIIndexer", "Error", e);
            context.put(WIIndexer.ERROR_VAR, Util.stackTraceToString(e));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JavaParameter[] getInputParameters() {
        JavaParameter[] jp = new JavaParameter[7];
        jp[0] =
            new JavaParameter(WIIndexer.ACTION_VAR,
                "Ação a ser executada pelo conector",
                "create: cria um novo índice. read_conf: lê as configurações de um índice.");
        jp[1] =
            new JavaParameter(WIIndexer.DIR_VAR, "Repósitório dos documentos",
                "Diretório onde ficam armazenados os documentos antes de serem indexados.");
        jp[2] =
            new JavaParameter(WIIndexer.FILE_MASK_VAR,
                "Extensões de arquivos a serem indexados",
                "Lista de extensões de arquivos separados por vírgula.");
        jp[3] =
            new JavaParameter(WIIndexer.INDEX_NAME_VAR,
                "Nome do índice a ser criado");
        jp[4] =
            new JavaParameter(WIIndexer.PARSE_HTML_VAR,
                "Processar conteúdo HTML",
                "Indica se o processamento de conteúdo HTML deve ser ativado");
        jp[5] =
            new JavaParameter(WIIndexer.PROPERTIES_VAR,
                "Propriedades adicionais ao documento",
                "Lista das propriedades separadas por vírgula a serem adicionadas a um documento");
        jp[6] =
            new JavaParameter(WIIndexer.RECURSIVE_VAR,
                "Pesquisar recursivamente no repositório");
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
            new JavaParameter(WIIndexer.ERROR_VAR,
                "Variável que conterá o stack trace se ocorrer algum erro durante a criação do índice.");
        return jp;
    }
}
