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

import javax.servlet.http.*;

import br.com.webinside.runtime.integration.*;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class RtmHeaders implements InterfaceHeaders {
	
    private ExecuteParams param;

    /**
     * Creates a new Headers object.
     *
     * @param param DOCUMENT ME!
     */
    public RtmHeaders(ExecuteParams param) {
        this.param = param;
    }

    /**
     * DOCUMENT ME!
     *
     * @param mime DOCUMENT ME!
     */
    public void setContentType(String mime) {
        param.setContentType(mime);
    }

    /**
     * DOCUMENT ME!
     *
     * @param length DOCUMENT ME!
     */
    public void setContentLength(int length) {
        if (length < 0) {
            length = 0;
        }
        param.setContentLength(length);
    }

    /**
     * DOCUMENT ME!
     *
     * @param filename DOCUMENT ME!
     */
    public void setFilename(String filename) {
        HttpServletResponse response = param.getHttpResponse();
        if ((response == null) || (filename == null)) {
            filename = "";
        }
        String value = "inline; filename=" + filename;
        response.setHeader("Content-disposition", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     * @param value DOCUMENT ME!
     */
    public void setHeader(String name, String value) {
        if (name == null) {
            name = "";
        }
        if (value == null) {
            value = "";
        }
        HttpServletResponse response = param.getHttpResponse();
        if ((response == null) || name.equals("")) {
            return;
        }
        response.setHeader(name, value);
    }

    /**
     * DOCUMENT ME!
     *
     * @param url DOCUMENT ME!
     */
    public void sendRedirect(String url) {
        sendRedirect(url, false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param url DOCUMENT ME!
     * @param fromProj DOCUMENT ME!
     */
    public void sendRedirect(String url, boolean fromProj) {
        if ((url == null) || (url.equals(""))) {
            return;
        }
        param.sendRedirect(url, null, fromProj);
    }
}
