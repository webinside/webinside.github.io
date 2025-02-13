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


/**
 */
import java.io.*;
import java.util.*;

import br.com.webinside.modules.Util;
import br.com.webinside.runtime.integration.*;
import br.com.webinside.runtime.util.WIMap;

import org.htmlparser.*;
import org.htmlparser.util.*;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class WIHighlighter extends AbstractConnector
    implements InterfaceParameters {
    //parâmetros de saída
    /** DOCUMENT ME! */
    private static final String ELAPSED_TIME_VAR = "tmp.highlighter.time";
    /** DOCUMENT ME! */
    private static final String ERROR_VAR = "tmp.highlighter.error";

    //parâmetros de entrada
    /** DOCUMENT ME! */
    private static final String CSS_STYLE_VAR = "tmp.highlighter.style";
    /** DOCUMENT ME! */
    private static final String SEARCH_QUERY_VAR = "tmp.search.query";
    /** DOCUMENT ME! */
    private static final String CONTENT_VAR = "tmp.content";
    /** DOCUMENT ME! */
    static final String DEFAULT_STYLE = "font-weight: bold; background-color: ";
    /** DOCUMENT ME! */
    static final String QTOKENS = " \"-";
    /** DOCUMENT ME! */
    static final String TOKENS = " =\"'\r\n;:.,_?!<>()[]{}|-+*/";
    /** DOCUMENT ME! */
    static final String[] COLORS = {"aqua", "lime", "red", "yellow"};
    /** DOCUMENT ME! */
    Vector vPalavras;
    /** DOCUMENT ME! */
    Vector vFrases;
    /** DOCUMENT ME! */
    String query;
    /** DOCUMENT ME! */
    String style = null;

    /**
     * Creates a new WIHighlighter object.
     */
    public WIHighlighter() {
    }

    /**
     * Creates a new WIHighlighter object.
     *
     * @param query DOCUMENT ME!
     */
    public WIHighlighter(String query) {
        this.query = query;
        parseQuery();
    }

    /**
     * DOCUMENT ME!
     *
     * @param queryString DOCUMENT ME!
     */
    public void parseQuery(String queryString) {
        this.query = queryString;
        parseQuery();
    }

    private String globs(String text, String color) {
        StringBuffer result = new StringBuffer("<span style=\"");
        if (style == null) {
            result.append(DEFAULT_STYLE).append(color);
        } else {
            result.append(style);
        }
        result.append("\">").append(text).append("</span>");

        return result.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param reader DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws HTMLParserException DOCUMENT ME!
     */
    public String highlight(HTMLReader reader) throws HTMLParserException {
        //processa o conteúdo html
        HTMLParser parser = new HTMLParser(reader);
        HTMLNode node = null;
        StringBuffer results = new StringBuffer();

        //vetor com os termos simples da query de pesquisa
        Vector v = getPalavras();

        //para cada termo simples é associado uma cor
        int vSize = v.size();
        String[] colors = new String[vSize];
        for (int i = 0; i < vSize; i++) {
        	String cor = getParams().getWIMap().get("tmp.search.color").trim();
        	if (cor.equals("")) colors[i] = COLORS[i % COLORS.length];
        	else colors[i] = cor;
        }

        //vetor com os termos compostos (frases) da query de pesquisa
        Vector frases = getFrases();

        for (HTMLEnumeration e = parser.elements(); e.hasMoreNodes();) {
            node = e.nextHTMLNode();
            //se o nó html for um conteúdo textual, processe-o
            if (node instanceof HTMLStringNode) {
                //o conteúdo textual
                String texto = Translate.decode(node.toPlainTextString());

                //toquenização do texto
                Vector txt = new Vector();
                StringTokenizer st = new StringTokenizer(texto, TOKENS, true);
                while (st.hasMoreTokens()) {
                    txt.add(st.nextToken());
                }

                //examina cada palavra do texto verificando se faz parte de um dos 
                //termos da query de pesquisa, em caso afirmativo o termo (simples ou
                //composto) deverá ser destacado.
                int j = 0;
                while (j < txt.size()) {
                    //uma palavra do texto
                    String palavra = (String) txt.elementAt(j);
                    boolean achou = false;
                    StringBuffer sb = new StringBuffer();

                    for (int i = 0; (i < frases.size()) && !achou; i++) {
                        String[] frase = (String[]) frases.elementAt(i);
                        sb = new StringBuffer();
                        boolean continuar = true;

                        //int k = 0;
                        int cont = j;
                        for (int k = 0; (k < frase.length) && continuar; k++) {
                            //se a palavra for um dos tokens ignore-o
                            while ((TOKENS.indexOf(palavra) > -1) && (k > 0)) {
                                sb.append(palavra);
                                if (++cont < txt.size()) {
                                    palavra = (String) txt.elementAt(cont);
                                } else {
                                    break;
                                }
                            }

                            //se a palavra não for um dos tokens e ainda coincidir
                            //com um dos termos de uma frase continue a busca
                            if (WISearchUtil.limparConteudo(palavra)
                                        .equalsIgnoreCase(frase[k])) {
                                sb.append(palavra);
                                if (++cont < txt.size()) {
                                    palavra = (String) txt.elementAt(cont);
                                }
                            } else {
                                continuar = false;
                            }
                        }
                        achou = continuar;
                        if (achou) {
                            j = cont - 1;
                        }
                    }
                    boolean destacou = false;
                    if (achou) {
                        results.append(globs(Translate.encode(sb.toString()),
                                "yellow"));
                        //results.append("<b style=\"background-color:yellow\">" + Translate.encode(sb.toString()) + "</b>");
                    } else {
                        palavra = (String) txt.elementAt(j);
                        //int k = 0;
                        for (int k = 0; (k < vSize) && !destacou; k++) {
                            if (v.elementAt(k).toString().equalsIgnoreCase(WISearchUtil
                                            .limparConteudo(palavra))) {
                                results.append(globs(Translate.encode(palavra),
                                        colors[k]));
                                //results.append("<b style=\"background-color:"	+ colors[k] + "\">"
                                //+ Translate.encode(palavra)	+ "</b>");
                                destacou = true;
                            }
                        }
                        if (!destacou) {
                            results.append(Translate.encode(palavra));
                        }
                    }
                    j++;
                }
            } else {
                results.append(node.toHTML());
            }
        }
        return results.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Vector getFrases() {
        return vFrases;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Vector getPalavras() {
        return vPalavras;
    }

    private void parseQuery() {
        vPalavras = new Vector();
        vFrases = new Vector();
        StringTokenizer st = new StringTokenizer(query, QTOKENS, true);
        Vector v = new Vector();

        //boolean in = false;
        boolean comecoFrase = false;
        boolean fimFrase = true;
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (QTOKENS.indexOf(token) > -1) {
                if (token.equals("\"") && fimFrase) {
                    comecoFrase = true;
                    fimFrase = false;
                } else if (token.equals("\"") && comecoFrase) {
                    fimFrase = true;
                    comecoFrase = false;
                }
            } else {
                if (comecoFrase) {
                    v.add(token);
                } else {
                    vPalavras.add(token);
                }
            }
            if (fimFrase && !v.isEmpty()) {
                int vSize = v.size();
                String[] str = new String[vSize];
                for (int i = 0; i < vSize; i++) {
                    str[i] = (String) v.elementAt(i);
                }
                vFrases.add(str);
                v.clear();
            }
        }
    }

    /**
     * Returns the style.
     *
     * @return String
     */
    public String getStyle() {
        return style;
    }

    /**
     * Sets the style.
     *
     * @param style The style to set
     */
    public void setStyle(String style) {
        this.style = style;
    }

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
            //legado...
            String content = context.get("file.content");
            if (content.equals("")) {
                content = context.get(WIHighlighter.CONTENT_VAR);
            }

            //legado...
            String qry = context.get("queryDestaque");
            if (qry.equals("")) {
                qry = context.get(WIHighlighter.SEARCH_QUERY_VAR);
            }

            String cssStyle = context.get(WIHighlighter.CSS_STYLE_VAR);
            if (!cssStyle.equals("")) {
                setStyle(cssStyle);
            }

            parseQuery(WISearchUtil.limparConteudo(qry).toLowerCase());
            StringReader sr = new StringReader(content);
            long inicio = System.currentTimeMillis();
            context.put(WIHighlighter.CONTENT_VAR,
                highlight(new HTMLReader(sr, content.length())));
            context.put(WIHighlighter.ELAPSED_TIME_VAR,
                String.valueOf(System.currentTimeMillis() - inicio));

            //legado...
            context.put("queryDestaque", "");
        } catch (Exception e) {
            context.put(WIHighlighter.ERROR_VAR, Util.stackTraceToString(e));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JavaParameter[] getInputParameters() {
        JavaParameter[] jp = new JavaParameter[4];
        jp[0] = new JavaParameter(WIHighlighter.CONTENT_VAR,
                "Conteúdo a ser destacado");
        jp[1] = new JavaParameter(WIHighlighter.CSS_STYLE_VAR,
                "Estilo CSS do destaque");
        jp[2] = new JavaParameter(WIHighlighter.SEARCH_QUERY_VAR,
                "<em>Query</em> com os termos do destaque");
        jp[3] = new JavaParameter("tmp.search.color",
                "Cor do destaque");
        return jp;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JavaParameter[] getOutputParameters() {
        JavaParameter[] jp = new JavaParameter[2];
        jp[0] =
            new JavaParameter(WIHighlighter.ERROR_VAR,
                "Variável que conterá o stack trace se ocorrer algum erro "
                + "durante o destaque de termos.");
        jp[1] =
            new JavaParameter(WIHighlighter.ELAPSED_TIME_VAR,
                "Variável que conterá o tempo em milisegundos que "
                + "foi gasto durante o destaque de termos.");
        return jp;
    }
}
