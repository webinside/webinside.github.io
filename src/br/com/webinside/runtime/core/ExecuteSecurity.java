/*
 * WEBINSIDE - Ferramenta de produtividade Java
 * Copyright (c) 2011-2012 LINEWEB Solu��es Tecnol�gicas Ltda.
 * Copyright (c) 2009-2010 Inc�gnita Intelig�ncia Digital Ltda.
 *
 * Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 * sob os termos da GNU LESSER GENERAL PUBLIC LICENSE (LGPL) conforme publicada 
 * pela Free Software Foundation; vers�o 2.1 da Licen�a.
 * Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 * NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 * ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 * 
 * Consulte a GNU LGPL para mais detalhes.
 * Voc� deve ter recebido uma c�pia da GNU LGPL junto com este programa; se n�o, 
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
