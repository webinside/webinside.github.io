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

import java.util.Locale;

import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.util.WIMap;

public abstract class AbstractFunction implements InterfaceFunction {
	
	private ExecuteParams wiParams;
	private WIMap wiMap;

    public abstract String execute(String[] args)
        throws UserException;

    public String execute(ExecuteParams wiParams, String[] args) 
    throws UserException {
        this.wiParams = wiParams;
        if (wiMap == null && wiParams != null) {
        	wiMap = wiParams.getWIMap();
        }
        for (int i = 0; i < args.length; i++) {
            args[i] = Producer.execute(wiMap, args[i]);
        }
        return execute(args);
    }
    
    public void setWiMap(WIMap wiMap) {
		this.wiMap = wiMap;
	}

	public WIMap getWiMap() {
		return wiMap;
    }

    public ExecuteParams getWiParams() {
		return wiParams;
	}

    public Locale getLocale() {
    	return IntFunction.getLocale(getWiMap());
    }

}
