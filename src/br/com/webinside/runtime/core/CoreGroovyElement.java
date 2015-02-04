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

import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import br.com.webinside.runtime.component.GroovyElement;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class CoreGroovyElement extends CoreCommon {
    private GroovyElement groovy;

    /**
     * Creates a new CoreSetElement object.
     *
     * @param wiParams DOCUMENT ME!
     * @param set DOCUMENT ME!
     */
    public CoreGroovyElement(ExecuteParams wiParams, GroovyElement groovy) {
        this.wiParams = wiParams;
        this.groovy = groovy;
        element = groovy;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        if (!isValidCondition()) {
            return;
        }
    	String variable = groovy.getVariable().toLowerCase().trim();
    	if (variable.equals("")) variable = "tmp.groovy";
        try {
	        Binding binding = new Binding();
	        binding.setVariable("wiMap", wiMap);
	        binding.setVariable("wiParams", wiParams);
	        binding.setVariable("databases", wiParams.getDatabaseAliases());
	        binding.setVariable("project", wiParams.getProject());
	        binding.setVariable("page", wiParams.getPage());
	        binding.setVariable("context", wiParams.getServletContext());
	        binding.setVariable("session", wiParams.getWISession());
	        binding.setVariable("request", wiParams.getHttpRequest());
	        binding.setVariable("response", wiParams.getHttpResponse());
	        binding.setVariable("errorLog", wiParams.getErrorLog());
	        GroovyShell shell = new GroovyShell(binding);
	        binding.setVariable("shell", shell);
	        Object value = shell.evaluate(groovy.getCode());
	        if (value != null) {
	        	returnMessage(variable, value.toString(), true);
	        }
            wiMap.put(variable + ".ok()", "true");
        } catch (Throwable err) {
        	String desc = groovy.getDescription().replace("-", "").trim();
        	returnMessage(variable, "Erro ao executar: " + desc, false);
            wiMap.put(variable + ".ok()", "false");
        	wiMap.put(variable + ".error", err.getMessage());
        	if (err instanceof Exception) {
        		String name = getClass().getSimpleName();
        		String id = "page:" + wiMap.get("wi.page.id");
            	wiParams.getErrorLog().write(name, id, (Exception)err);
        	} else {
        		err.printStackTrace(System.out);
        	}
        }
        writeLog();
    }
    
    private void returnMessage(String variable, String message, boolean force) {
    	String ret = variable;
    	if (variable.equals("tmp.groovy")) {
    		ret += ".return";
    	}
    	if (wiMap.get(ret).equals("") || force) {
        	wiMap.put(ret, message);
    	}
    }
    
}
