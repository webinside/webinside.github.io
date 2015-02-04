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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;

import br.com.webinside.modules.Util;
import br.com.webinside.runtime.integration.*;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class SearchObject extends WISearcher implements InterfaceParameters {
    /** DOCUMENT ME! */
    private static final String OBJECT_ID_VAR = "tmp.prefix";

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
            Hashtable[] ht = getResultsIntoHashtable(context);

            //prefixo que será usado como identificador do objeto a ser criado
            String prefix = context.get(SearchObject.OBJECT_ID_VAR);

            //popula o contexto com variáveis baseadas no prefixo que foi passado pela
            //variável tmp.prefix, os valores dessas variáveis serão prrenchidos de
            //acordo com os resultados vindo da pesquisa
            for (int i = 0; i < ht.length; i++) {
                Enumeration keys = ht[i].keys();
                while (keys.hasMoreElements()) {
                    String key = (String) keys.nextElement();
                    String value = (String) ht[i].get(key);
                    String newKey =
                        prefix + '[' + String.valueOf(i + 1) + "]." + key;
                    context.put(newKey, value);
                    if (i == 0) {
                        context.put(prefix + '.' + key, value);
                    }
                }
            }
            context.put(prefix + ".size()", ht.length);
        } catch (Exception e) {
            context.put(WISearcher.ERROR_VAR, Util.stackTraceToString(e));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     */
    public void executeOld(WIMap context) {
        try {
            String in = context.get("tmp.indexName");
            String indexName = context.get("wi.proj.root") + "/index/" + in;
            WISearcher ms = new WISearcher(indexName);

            String query = context.get("tmp.search.query");
            ms.setQuery(WISearchUtil.limparConteudo(query));
            // coloca no contexto a variável "tmp.search.query" que indica a
            // expressão de busca
            context.put("tmp.search.query", query);

            long inicio = System.currentTimeMillis();
            ms.doIt();
            long tempo = System.currentTimeMillis() - inicio;

            // coloca no contexto a variável "tmp.search.time" que indica a
            // duração da realização da pesquisa
            context.put("tmp.search.time", String.valueOf(tempo));

            int count = ms.getHitsCount();

            // deprecated - usar tmp.searchResult.count por ser coloquialmente mais lógico
            context.put("tmp.search.count", String.valueOf(count));
            // coloca no contexto a variável "tmp.searchResult.count" que retorna
            // a quantidade de documentos encontrada pela busca
            context.put("tmp.searchResult.count", String.valueOf(count));

            // prefixo que será usado para identificação do objeto
            String prefix = context.get(SearchObject.OBJECT_ID_VAR);

            // resultado da busca...
            Hashtable[] ht = ms.getResults();

            // a variavel "tmp.searchResult.orderBy" indica o nome de uma das propriedades
            // do documento que será usada como base para ordenação do resultado da busca
            String orderBy = context.get("tmp.searchResult.orderBy");

            // a variavel "tmp.searchResult.reverse" indica se a ordenação do resultado
            // da busca será feita de maneira descendente (reversa). O valor default para
            // essa variável é false.
            // Essa variável só tem função quando em uso conjunto com "tmp.searchResult.orderBy".
            boolean reverse =
                context.get("tmp.searchResult.reverse").equalsIgnoreCase("true");

            // a variavel "tmp.searchResult.caseSensitive" indica se a ordenação do resultado
            // da busca será "sensível ao caso". O valor default para essa variável é false.
            // Essa variável só tem função quando em uso conjunto com "tmp.searchResult.orderBy".
            boolean caseSensitive =
                context.get("tmp.searchResult.caseSensitive").equalsIgnoreCase("true");
            if (!orderBy.equals("")) {
                Arrays.sort(ht,
                    new WIComparator(orderBy, reverse, caseSensitive));
            }

            //popula o contexto com variáveis baseadas num prefixo que foi passado pela
            //variável tmp.prefix cujos valores serão de acordo com os resultados...
            for (int i = 0; i < ht.length; i++) {
                Enumeration keys = ht[i].keys();
                while (keys.hasMoreElements()) {
                    String key = (String) keys.nextElement();
                    String value = (String) ht[i].get(key);
                    String newKey =
                        prefix + '[' + String.valueOf(i + 1) + "]." + key;
                    context.put(newKey, value);
                    if (i == 0) {
                        context.put(prefix + '.' + key, value);
                    }
                }
            }

            context.put(prefix + ".size()", ht.length);
        } catch (Exception e) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            context.put(WISearcher.ERROR_VAR, sw.toString());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JavaParameter[] getInputParameters() {
        List l = getInputJavaParameters();
        l.add(new JavaParameter(SearchObject.OBJECT_ID_VAR, "Objeto"));
        JavaParameter[] jp = new JavaParameter[l.size()];
        l.toArray(jp);
        return jp;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JavaParameter[] getOutputParameters() {
        List l = getOutputJavaParameters();
        JavaParameter[] jp = new JavaParameter[l.size()];
        l.toArray(jp);
        return jp;
    }
}
