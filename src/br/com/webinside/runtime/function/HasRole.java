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

import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.integration.AbstractFunction;
import br.com.webinside.runtime.integration.Producer;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.WIMap;

/**
 * Title: HasRole
 * Description: Classe verifica se o usuario tem algum dos perfis de acesso
 * Copyright:    Copyright (c) 2010
 * @author  Geraldo Moraes
 * @version 1.0
 * @see br.com.webinside.runtime.integration.AbstractFunction
 */

public class HasRole extends AbstractFunction {
    
    public HasRole() { }
    
   /**
   * Método que retorna se o usuário possui algum dos perfis de acesso.
   * Os parametros são os perfis de acesso.
   */

    public String execute(String[] args) throws UserException {
        String separador = ":";
        if (args.length > 1) {
            separador = args[1];
            if (separador.equals("comma")) separador = ",";
        }
    	WIMap wiMap = getWiMap();
    	String[] roles = args[0].split(separador);
    	String[] mods = wiMap.get("pvt.login.role.modules").split(",");
		for (String role : roles) {
			String pageRole = Producer.execute(wiMap, role).trim();
			for (String mod : mods) {
				String modRole = "module_" + mod.trim();
				if (modRole.equalsIgnoreCase(pageRole)) return "true";
			}
		}    	
    	int size = Function.parseInt(wiMap.get("pvt.login.role.size()"));
    	for (int i = 1; i <= size; i++) {
			String loginRole = wiMap.get("pvt.login.role[" + i + "].name");
			for (String role : roles) {
				String pageRole = Producer.execute(wiMap, role).trim();
				if (loginRole.equalsIgnoreCase(pageRole)) {
					return "true";
				}
			}
		}
    	return "false";
    }
    
}
