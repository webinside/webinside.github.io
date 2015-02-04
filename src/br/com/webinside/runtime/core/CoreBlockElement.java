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

import br.com.webinside.runtime.component.BlockElement;
import br.com.webinside.runtime.integration.Condition;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class CoreBlockElement extends CoreCommon {
    private BlockElement block;

    /**
     * Creates a new CoreSetElement object.
     *
     * @param wiParams DOCUMENT ME!
     * @param set DOCUMENT ME!
     */
    public CoreBlockElement(ExecuteParams wiParams, BlockElement block) {
        this.wiParams = wiParams;
        this.block = block;
        element = block;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
    	wiMap = wiParams.getWIMap();
    	String cond = element.getCondition();
        if (cond.trim().equalsIgnoreCase("true")) {
        	wiMap.remove("wi.block.cond");
        	wiMap.remove("wi.block.var");
        } else {
            boolean ret = new Condition(wiMap, cond).execute();
        	wiMap.put("wi.block.cond", ret + "");
        	String var = block.getVar().trim();
        	if (!var.equals("")) {
            	wiMap.put(var, ret + "");
            	wiMap.put("wi.block.var", var);
        	}
        }
        writeLog();
    }
    
}
