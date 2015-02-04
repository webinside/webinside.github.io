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
import br.com.webinside.runtime.util.WIMap;

public class SVList extends AbstractFunction {
	
	final private String KEY = "wi5_svlist_initialized"; 
	
    public String execute(String[] args) {
		WIMap wiMap = getWiParams().getWIMap();
		WIMap superMap = (WIMap)wiMap.getObj("super.");
		if (superMap != null) wiMap = superMap;
    	if (args.length == 2) {
        	String action = args[0].toLowerCase().trim();
        	String value = args[1].toLowerCase().trim();
        	if (action.equals("put")) {
        		String init = (String)getWiParams().getRequestAttribute(KEY);
        		if (init == null) {
        			getWiParams().setRequestAttribute(KEY, "true");
        			wiMap.put("pvt.svlist", "");
        		}
        		String svlist = wiMap.get("pvt.svlist");
        		if (svlist.length() > 0) svlist += ",";
        		svlist += value;
        		wiMap.put("pvt.svlist", svlist);
        		return value;
        	} else if (action.equals("get")) {
        		String[] arr = wiMap.get("pvt.svlist").split(",");
        		for (int i = 0; i < arr.length; i++) {
					if (arr[i].equals(value)) return value;
				}
        	}
    	}
        return "";
    }
    
}
