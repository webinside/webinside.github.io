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

import br.com.webinside.runtime.integration.AbstractConnector;
import br.com.webinside.runtime.integration.DatabaseAliases;
import br.com.webinside.runtime.integration.IntFunction;
import br.com.webinside.runtime.integration.InterfaceHeaders;
import br.com.webinside.runtime.integration.InterfaceParameters;
import br.com.webinside.runtime.integration.JavaParameter;
import br.com.webinside.runtime.util.WIMap;

public class LoginRoles extends AbstractConnector implements InterfaceParameters {

	public LoginRoles() { }

    public void execute(WIMap wiMap, DatabaseAliases databases,
        InterfaceHeaders headers) {
    	String var = wiMap.get("tmp.role_var").trim();
    	if (!var.equals("")) {
        	wiMap.put(var, wiMap.get("tmp.role_value").trim());
            IntFunction.loginRoles(getParams());
    	}
    }

	@Override
	public JavaParameter[] getInputParameters() {
        JavaParameter[] params = new JavaParameter[2];
        params[0] = new JavaParameter("tmp.role_var", "Variável do perfil");
        params[1] = new JavaParameter("tmp.role_value", "Valor do perfil");
        return params;
	}

	@Override
	public JavaParameter[] getOutputParameters() {
		return new JavaParameter[0];
	}
    
}
