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

package br.com.webinside.runtime.function;

import java.util.StringTokenizer;

import javax.servlet.http.Cookie;

import br.com.webinside.runtime.core.RtmFunction;
import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.integration.AbstractConnector;
import br.com.webinside.runtime.integration.DatabaseAliases;
import br.com.webinside.runtime.integration.IntFunction;
import br.com.webinside.runtime.integration.InterfaceHeaders;
import br.com.webinside.runtime.integration.JavaParameter;
import br.com.webinside.runtime.util.CrossContext;
import br.com.webinside.runtime.util.CrossContextFactory;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.WIMap;

public class SingleSignOnConnector extends AbstractConnector {
	
	public void execute(WIMap wiMap, DatabaseAliases databases, 
			InterfaceHeaders headers) throws UserException {
		String projId =  wiMap.get("wi.proj.id");
        String wilogin = wiMap.get("pvt.wilogin");
        String addr = getParams().getHttpRequest().getRequestedSessionId();
    	String ssoId = RtmFunction.getSingleSignOnId(getParams());
        if (wilogin.equals(addr)) {
        	if (ssoId.equals("")) ssoId = Function.randomKey();
        	Cookie cookie = new Cookie("JSSOID", ssoId);
        	cookie.setPath("/");
        	getParams().getHttpResponse().addCookie(cookie);
        	WIMap aux = new WIMap();
    		aux.putObj("pvt.login.", wiMap.getObj("pvt.login."));
        	IntFunction.killObjAndVector(aux, "pvt.login.role.");
            String exportObjs = wiMap.get("tmp.exportObjs");
            StringTokenizer st = new StringTokenizer(exportObjs, ",");
            while (st.hasMoreTokens()) {
            	String token = st.nextToken().toLowerCase().trim();
            	if (!token.startsWith("wi.") && 
            			!token.startsWith("pvt.login.")) {
            		aux.putObj(token, wiMap.getObj(token));
            	}	
            }
        	CrossContext cross = CrossContextFactory.getInstance();
        	if (cross != null) {
        		cross.createSSO(ssoId + "-" + addr, projId, aux.getAsMap());
        	} else {
        		String className = getClass().getName();
        		String msg = "wi-crosscontext.jar not found in Tomcat lib";
        		getParams().getErrorLog().write(className, "Execute", msg);
        	}
        }
    }
    	
    public boolean exit() {
        return false;
    }

    public JavaParameter[] getInputParameters() {
        JavaParameter[] params = new JavaParameter[1];
        params[0] = new JavaParameter("tmp.exportObjs", 
        			"Exportar objetos", "");
        return params;
    }

    public JavaParameter[] getOutputParameters() {
        return new JavaParameter[0];
    }
    
}
