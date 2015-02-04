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

import java.util.Set;

import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.function.sv.SVNode;
import br.com.webinside.runtime.integration.IntFunction;
import br.com.webinside.runtime.integration.InterfaceFunction;
import br.com.webinside.runtime.integration.Producer;
import br.com.webinside.runtime.util.WIMap;
import br.com.webinside.runtime.util.WISession;

public class SecureVar implements InterfaceFunction {
	
	private WIMap wiMap;
	
	// sv(V/P,tmp.variavel,tmp.valor)
    public String execute(ExecuteParams wiParams, String[] args) {
    	if (wiParams == null || args.length < 2) return "";
        if (wiMap == null) wiMap = wiParams.getWIMap();
    	String type = args[0].toLowerCase().trim(); // Valor ou Parametro
        String var = args[1].toLowerCase().trim();
        Set<String> secureVars = wiParams.getProject().getSecureVars();
        if (!IntFunction.isSecureVar(secureVars, var)) {
        	System.err.println("SecureVar " + var + " not registered in project");
        	return "SecureVarNotRegistered[" + var + "]";
        }
        String value = wiMap.get(var).trim();
        if (args.length > 2) {
            value = Producer.execute(wiMap, args[2]).trim();
        }
    	WISession session = wiParams.getWISession();
        if (!value.equals("") && session.isValid()) {
        	SVNode svNode = IntFunction.getSVNode(session, var);
        	String key = svNode.addValue(wiMap.get("wi.page.id"), value);
        	return type.equals("v") ? key : var + "=" + key;
        }
        return "";
    }
    
    public void setWiMap(WIMap wiMap) {
		this.wiMap = wiMap;
	}

}
