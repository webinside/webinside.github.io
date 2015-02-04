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

package br.com.webinside.runtime.core;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.webinside.runtime.component.AbstractCookie;
import br.com.webinside.runtime.component.CookieRead;
import br.com.webinside.runtime.component.CookieWrite;
import br.com.webinside.runtime.integration.Producer;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class CoreCookie extends CoreCommon {
    /** DOCUMENT ME! */
    private AbstractCookie cookie;

    /**
     * Creates a new CoreCookie object.
     *
     * @param wiParams DOCUMENT ME!
     * @param cookie DOCUMENT ME!
     */
    public CoreCookie(ExecuteParams wiParams, AbstractCookie cookie) {
        this.wiParams = wiParams;
        this.cookie = cookie;
        element = cookie;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        if (!isValidCondition()) {
            return;
        }
        if (cookie instanceof CookieRead) {
            read((CookieRead) cookie);
        }
        if (cookie instanceof CookieWrite) {
            write((CookieWrite) cookie);
        }
        writeLog();
    }

    private void read(CookieRead cookrd) {
        String wiobj = cookrd.getWIObj();
        String name = cookie.getName().trim().toLowerCase();
        name = Producer.execute(wiMap, name).trim();
        String value = "";
        HttpServletRequest request = wiParams.getHttpRequest();
        if (request != null) {
            javax.servlet.http.Cookie[] cks = request.getCookies();
            if (cks == null) {
                cks = new javax.servlet.http.Cookie[0];
            }
            for (int i = 0; i < cks.length; i++) {
                javax.servlet.http.Cookie ck = cks[i];
                if (ck.getName().toLowerCase().equals(name)) {
                    value = ck.getValue();
                    try {
                        value = URLDecoder.decode(value, "ISO-8859-1");
                    } catch (UnsupportedEncodingException err) {
                        // Nunca vai ocorrer.	
                    }
                    break;
                }
            }
        }
        wiMap.put(wiobj, value);
    }

    private void write(CookieWrite cookwr) {
        String name = cookie.getName().trim().toLowerCase();
        name = Producer.execute(wiMap, name).trim();
        if (name.equals("")) {
            return;
        }
        String value = Producer.execute(wiMap, cookwr.getValue());
	    try {
	        value = URLEncoder.encode(value, "ISO-8859-1");
	    } catch (UnsupportedEncodingException err) {
	        // Nunca vai ocorrer.	
	    }
        String domain = Producer.execute(wiMap, cookwr.getDomain());
        String path = Producer.execute(wiMap, cookwr.getPath());
        if (path.trim().equals("")) {
            path = "/";
        }
        String maxage = Producer.execute(wiMap, cookwr.getMaxAge());
        javax.servlet.http.Cookie save =
            new javax.servlet.http.Cookie(name, value);
        save.setSecure(false);
        if (!domain.trim().equals("")) {
        	save.setDomain(domain);
        }
        save.setPath(path);
        try {
            if (!maxage.trim().equals("")) {
                save.setMaxAge(Integer.parseInt(maxage));
            }
        } catch (NumberFormatException err) {
        	// ignorado.
        }
        HttpServletResponse response = wiParams.getHttpResponse();
        response.addCookie(save);
    }
}
