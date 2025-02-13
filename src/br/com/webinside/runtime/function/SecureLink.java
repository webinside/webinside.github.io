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
