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

import javax.servlet.http.HttpServletResponse;

import br.com.webinside.runtime.util.StringA;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.5 $
 */
public class ExecuteSecurity {
    private ExecuteParams wiParams;

    /**
     * Creates a new ExecuteSecurity object.
     *
     * @param wiParams DOCUMENT ME!
     */
    public ExecuteSecurity(ExecuteParams wiParams) {
        this.wiParams = wiParams;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean check() {
    	if (wiParams.isJspInclude()) {
    		return true;
    	}
        boolean pageSec = true;
        if (wiParams.getPage().getSecurity().trim().equals("OFF")) {
            pageSec = false;
        }
        if (wiParams.getPage().getSysPage().equals("ON")) {
            wiParams.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return checkReferer(pageSec);
    }

    private boolean checkReferer(boolean pageSec) {
        String ref = wiParams.getHttpRequest().getHeader("referer");
        if (ref == null) ref = "";
        boolean ok = false;
        if (ref != null) {
            int qmark = ref.indexOf("?");
            if (qmark == -1) {
                qmark = ref.length();
            }
            int hash = ref.indexOf("#");
            if ((hash != -1) && (hash < qmark)) {
                qmark = hash;
            }
            int dot = ref.lastIndexOf(".", qmark);
            String ext = StringA.mid(ref, dot + 1, qmark - 1);
            int cont = 0;
            if (ext.toLowerCase().equals("wsp")) {
                cont = cont + 1;
            }
            String srvname =
                RtmFunction.getServerName(wiParams.getHttpRequest());
            String host = StringA.piece(ref, "/", 3);
            int port = wiParams.getHttpRequest().getServerPort();
            if (host.indexOf(":") > -1) {
                srvname = srvname + ":" + port;
            }
            if (srvname.equals(host)) {
                cont = cont + 1;
            }
            if (cont == 2) {
                ok = true;
            }
        }
        if ((!ok) && (pageSec)) {
        	if (RtmFunction.bypassSecurity(wiParams)) return true;
        	wiParams.sendError(HttpServletResponse.SC_FORBIDDEN);
            return false;
        }
        return true;
    }
}
