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

import java.util.ArrayList;
import java.util.List;

import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.integration.AbstractConnector;
import br.com.webinside.runtime.integration.DatabaseAliases;
import br.com.webinside.runtime.integration.InterfaceHeaders;
import br.com.webinside.runtime.integration.InterfaceParameters;
import br.com.webinside.runtime.integration.JavaParameter;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.WIMap;

public class ObjPivot extends AbstractConnector implements InterfaceParameters {

    public ObjPivot() { }

	@Override
	public void execute(WIMap wiMap, DatabaseAliases databases, InterfaceHeaders headers) 
	throws UserException {
        String wiobj = wiMap.get("tmp.wiobj").trim();
        String objkey = wiMap.get("tmp.objkey").trim();
        String objtot = wiMap.get("tmp.objtot").trim();
    	double total = 0;
        int size = Function.parseInt(wiMap.get(wiobj + ".size()"));
        for (int i = 1 ; i <= size ; i++) {
        	WIMap auxMap = (WIMap) wiMap.getObj(wiobj + "[" + i + "].");
        	String grp = auxMap.get(objkey);
        	if (!grp.equals("")) {
                List keys = new ArrayList(auxMap.keySet());
                for (int z = 0; z < keys.size(); z++) {
                    String auxkey = (String) keys.get(z);
                    if (!auxkey.equals(objkey)) {
                    	String value = auxMap.get(auxkey);
                    	wiMap.put(wiobj + "." + grp + "." + auxkey, value);
                    }
                    if (auxkey.equals(objtot)) {
                    	total += Function.parseDouble(auxMap.get(auxkey));
                    }
                }    
        	}
        }
    	wiMap.put(wiobj + ".total", total + "");
    }

    public JavaParameter[] getInputParameters() {
        JavaParameter[] params = new JavaParameter[3];
        params[0] = new JavaParameter("tmp.wiobj", "WI Object");
        params[1] = new JavaParameter("tmp.objkey", "Chave do Pivot");
        params[2] = new JavaParameter("tmp.objtot", "Chave do Valor Total");
        return params;
    }

    public JavaParameter[] getOutputParameters() {
        return new JavaParameter[0];
    }
    
}
