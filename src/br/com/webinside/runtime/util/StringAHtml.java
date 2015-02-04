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

package br.com.webinside.runtime.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Classe que tranforma um texto para o formato HTML e vice-versa. No formato
 * HTML carateres especiais são convertidos com &...;
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.1 $
 *
 * @since 3.0
 */
public class StringAHtml extends StringA {
    /** Caracteres no formato HTML. */
    private static final String[] HTML_CHARS =
    {
        "&aacute;", "&Aacute;", "&eacute;", "&Eacute;", "&iacute;", "&Iacute;",
        "&oacute;", "&Oacute;", "&uacute;", "&Uacute;", "&agrave;", "&Agrave;",
        "&egrave;", "&Egrave;", "&igrave;", "&Igrave;", "&ograve;", "&Ograve;",
        "&ugrave;", "&Ugrave;", "&acirc;", "&Acirc;", "&ecirc;", "&Ecirc;",
        "&icirc;", "&Icirc;", "&ocirc;", "&Ocirc;", "&ucirc;", "&Ucirc;",
        "&auml;", "&Auml;", "&euml;", "&Euml;", "&iuml;", "&Iuml;", "&ouml;",
        "&Ouml;", "&uuml;", "&Uuml;", "&atilde;", "&Atilde;", "&otilde;",
        "&Otilde;", "&ccedil;", "&Ccedil;"
    };
    /** Símbolos no formato HTML. */
    private static final String[] HTML_SYMBOLS =
    {"&quot;", "&nbsp;", "&lt;", "&gt;", "&amp;"};
    /** Caracteres no formato de texto. */
    private static final String[] TEXT_CHARS =
    {
        "á", "Á", "é", "É", "í", "Í", "ó", "Ó", "ú", "Ú", "à", "À", "è", "È",
        "ì", "Ì", "ò", "Ò", "ù", "Ù", "â", "Â", "ê", "Ê", "î", "Î", "ô", "Ô",
        "û", "Û", "ä", "Ä", "ë", "Ë", "ï", "Ï", "ö", "Ö", "ü", "Ü", "ã", "Ã",
        "õ", "Õ", "ç", "Ç"
    };
    /** Símbolos no formato de texto. */
    private static final String[] TEXT_SYMBOLS = {"\"", " ", "<", ">", "&"};

    /**
     * Cria um novo objeto da classe.
     */
    public StringAHtml() {
    }

    /**
     * Cria um novo objeto da classe.
     *
     * @param text o texto inicial como String.
     */
    public StringAHtml(String text) {
        this.set(text);
    }

    /**
     * Cria um novo objeto da classe.
     *
     * @param text o texto inicial como StringA.
     */
    public StringAHtml(StringA text) {
        this.set(text);
    }

    /**
     * Converte um texto no formato HTML com & para o formato normal com os
     * caracteres especiais. Tanto converte os simbolos (ex: &lt;,&gt;) como
     * converte as letras acentuadas (ex: &aacute;).
     *
     * @return texto normal com simbolos e caracteres acentuados.
     */
    public String htmlToTextFull() {
        return htmlToText(true, true);
    }

    /**
     * Converte um texto no formato HTML com & para o formato normal com os
     * caracteres especiais.
     *
     * @param chars indica se converterá os caracteres (ex: &aacute;).
     * @param symbols indica se converterá os simbolos(ex: &lt;,&gt;).
     *
     * @return texto normal com caracteres especiais.
     */
    public String htmlToText(boolean chars, boolean symbols) {
        return htmlToText(this.toString(), chars, symbols);
    }

    /**
     * Converte um texto no formato HTML com & para o formato normal com os
     * caracteres especiais.
     *
     * @param text o texto no formato html a ser processado.
     * @param chars indica se converterá os caracteres (ex: &aacute;).
     * @param symbols indica se converterá os simbolos (ex: &lt;,&gt;).
     *
     * @return texto normal com caracteres especiais.
     */
    public static String htmlToText(String text, boolean chars, boolean symbols) {
        Map map = new HashMap();
        if (text == null) {
            text = "";
        }
        if (symbols) {
            for (int i = 0; i < HTML_SYMBOLS.length; i++) {
                map.put(HTML_SYMBOLS[i], TEXT_SYMBOLS[i]);
            }
        }
        if (chars) {
            for (int i = 0; i < HTML_CHARS.length; i++) {
                map.put(HTML_CHARS[i], TEXT_CHARS[i]);
            }
        }
        return StringA.xmlEntityToText(text, map);
    }

    /**
     * Converte um texto normal com caracteres especiais para o formato HTML
     * com &. Tanto converte os simbolos (ex: &lt;&lg;) como converte as
     * letras acentuadas (ex: áé).
     *
     * @return texto no formato HTML com simbolos e caracteres  acentuados
     *         convertidos.
     */
    public String textToHtmlFull() {
        return textToHtml(true, true);
    }

    /**
     * Converte um texto normal com caracteres especiais para o formato HTML
     * com &.
     *
     * @param chars indica se converterá os caracteres (ex: áé).
     * @param symbols indica se converterá os simbolos (ex: &lt;&lg;).
     *
     * @return texto no formato HTML com caracteres  especiais convertidos.
     */
    public String textToHtml(boolean chars, boolean symbols) {
        return textToHtml(this.toString(), chars, symbols);
    }

    /**
     * Converte um texto normal com caracteres especiais para o formato HTML
     * com &.
     *
     * @param text o texto a ser processado.
     * @param chars indica se converterá os caracteres (ex: áé).
     * @param symbols indica se converterá os simbolos (ex: &lt;&lg;).
     *
     * @return texto no formato HTML com caracteres  especiais convertidos.
     */
    public static String textToHtml(String text, boolean chars, boolean symbols) {
        Map map = new HashMap();
        if (text == null) {
            text = "";
        }
        if (symbols) {
            for (int i = 0; i < TEXT_SYMBOLS.length; i++) {
                map.put(TEXT_SYMBOLS[i], HTML_SYMBOLS[i]);
            }
        }
        if (chars) {
            for (int i = 0; i < TEXT_CHARS.length; i++) {
                map.put(TEXT_CHARS[i], HTML_CHARS[i]);
            }
        }
        return StringA.textToXmlEntity(text, map, true);
    }
}
