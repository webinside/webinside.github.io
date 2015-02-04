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

package br.com.webinside.runtime.integration;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.exception.UserException;

/**
 * Classe abstrata para validação de variaveis no servidor
 * 
 * @author Geraldo Moraes
 * @version 1.0
 */
public abstract class AbstractValidation implements InterfaceValidation {
	
	private static Map<String, Properties> messages = 
		new HashMap<String, Properties>();
	
	private ExecuteParams wiParams;

    public abstract String execute(String var, String[] args)
        throws UserException;
    
	protected String messagesName() {
		return "br.com.webinside.runtime.validation.Messages";
	}

	protected String defaultLocale() {
		return "pt_BR";
	}

    public String execute(ExecuteParams wiParams, String var, String[] args) 
    throws UserException {
        this.wiParams = wiParams;
        for (int i = 0; i < args.length; i++) {
            args[i] = Producer.execute(wiParams.getWIMap(), args[i]);
        }
        return execute(var, args);
    }
    
    public ExecuteParams getWiParams() {
		return wiParams;
	}

    public Locale getLocale() {
    	return IntFunction.getLocale(wiParams.getWIMap());
    }
    
    public String getMessage(String key, Object...args) {
    	try {
        	Properties props = loadMessages(messagesName() + "_" + getLocale());
        	if (props.isEmpty()) {
        		// ler mensagens para locale padrao
        		props = loadMessages(messagesName() + "_" + defaultLocale());
        	}
        	String resp = props.getProperty(key);
        	if (resp == null) resp = "";
        	return String.format(resp.trim(), args);
	    } catch (Exception err) {
	    	wiParams.getErrorLog().write("Validation", "getMessage", err);
	    	err.printStackTrace();
	    }
    	return "";
    }
     
    private Properties loadMessages(String key) throws Exception {
    	Properties props = messages.get(key);
    	if (props == null) {
    		String propsFile = key.replaceAll("\\.", "/") + ".properties";
    		props = IntFunction.loadProperties(props, propsFile);
    		messages.put(key, props);
    	}	
    	return props;
    }
    
}
