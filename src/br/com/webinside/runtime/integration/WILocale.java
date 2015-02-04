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

package br.com.webinside.runtime.integration;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import br.com.webinside.runtime.util.StringA;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class WILocale {
    /** DOCUMENT ME! */
    private static final String[] locales = {"pt_BR", "en_US"};

    public static Locale getDefault(String loc) {
        Locale locale = Locale.getDefault();
        if (!loc.equals("")) {
        	loc = StringA.changeChars(loc, "-", "_");
        	String lang = StringA.piece(loc, "_", 1);
        	String country = StringA.piece(loc, "_", 2);
            locale = new Locale(lang, country);
        }
        return locale;
    }

    public static Locale getDefault(HttpServletRequest request) {
        String loc = "";
        Locale locale = new Locale("pt", "br");
        try {
            loc = (String) request.getHeader("accept-language");
            loc = StringA.piece(loc, ",", 1);
        	loc = StringA.changeChars(loc, "-", "_");
            if (loc.equals("en")) {
                loc = "en_US";
            }
        } catch (Exception e) {
            return locale;
        }
        for (int i = 0; i < locales.length; i++) {
            if (loc.equalsIgnoreCase(locales[i])) {
            	String lang = StringA.piece(locales[i], "_", 1);
            	String country = StringA.piece(locales[i], "_", 2);
                return new Locale(lang, country);
            }
        }
        return locale;
    }
}
