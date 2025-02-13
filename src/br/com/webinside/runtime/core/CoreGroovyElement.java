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

package br.com.webinside.runtime.core;

import groovy.lang.Binding;
import groovy.lang.GroovyShell;

import java.io.File;

import br.com.webinside.runtime.component.GroovyElement;
import br.com.webinside.runtime.integration.IntFunction;
import br.com.webinside.runtime.util.FileIO;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.4 $
 */
public class CoreGroovyElement extends CoreCommon {
	
	private static String importScript;
	private static long importTime;
	
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
        loadImport();
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
	        String code = importScript.trim() + "\r\n" + groovy.getCode();
	        GroovyShell shell = new GroovyShell(binding);
	        binding.setVariable("shell", shell);
	        Object value = shell.evaluate(code);
	        if (value != null) {
	        	returnMessage(variable, value.toString(), true);
	        }
            wiMap.put(variable + ".ok()", "true");
        } catch (Throwable err) {
        	String desc = groovy.getDescription().replace("-", "").trim();
        	returnMessage(variable, "Erro ao executar: " + desc, false);
            wiMap.put(variable + ".ok()", "false");
        	wiMap.put(variable + ".error", err.getMessage());
        	IntFunction.setMessageError(wiMap, variable, "Erro ao executar: " + desc);
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
    
    private void loadImport() {
    	String prjPath = wiMap.get("wi.proj.path");
    	File importFile = new File(prjPath,"/WEB-INF/groovy/import.groovy");
    	if (!importFile.isFile()) {
    		importFile.getParentFile().mkdirs();
    		StringBuilder sb = new StringBuilder();
    		sb.append("import java.io.*;\r\n");
    		sb.append("import java.util.*;\r\n");
    		sb.append("import br.com.webinside.runtime.util.*;\r\n");
    		sb.append("import br.com.webinside.runtime.database.*;\r\n");
    		sb.append("import br.com.webinside.runtime.integration.*;\r\n");
    		sb.append("\r\n");
    		sb.append("def wiGroovyScript(name) {\r\n");
    		sb.append("  String prjPath = wiMap.get(\"wi.proj.path\");\r\n");
    		sb.append("  String script = prjPath + \"/WEB-INF/groovy/\" + name + \".groovy\";\r\n");
    		sb.append("  return shell.parse(new File(script));\r\n");
    		sb.append("}\r\n");
    		FileIO io = new FileIO(importFile.getAbsolutePath(), 'W');
    		io.writeText(sb.toString());
    	}
    	if (importScript == null || importTime < importFile.lastModified()) {
    		FileIO io = new FileIO(importFile.getAbsolutePath(), 'R');
    		importScript = io.readText();
    		importTime = importFile.lastModified();
    	}
    }
    
}
