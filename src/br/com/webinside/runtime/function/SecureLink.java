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

import br.com.webinside.runtime.integration.AbstractFunction;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class SecureLink extends AbstractFunction {

    public SecureLink() { }

    public String execute(String[] args) {
    	String url = "";
    	String mask = "";
    	if (args.length >= 1) url = args[0].trim();
    	if (args.length >= 2) mask = args[1].trim();
    	
    	String server = getWiMap().get("wi.server.name");
    	String projId = getWiMap().get("wi.proj.id");
    	SecureLinkManager.SLNode slnode = SecureLinkManager.getSession(getWiMap());
    	String token = slnode.addTokenMask(mask);
		
    	if (url.equals("")) return "empty_url";
    	if (url.indexOf("?") == -1) url += "?";
    	if (!url.endsWith("?") && !url.endsWith("&")) url +="&";
        return url + "wi.slink=" + server + "/" + projId + "/" + token;
    }
}
