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

import br.com.webinside.runtime.component.TransactionElement;
import br.com.webinside.runtime.integration.IntFunction;
import br.com.webinside.runtime.integration.Producer;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class CoreTransactionElement extends CoreCommon {

	private TransactionElement transaction;

    /**
     * Creates a new CoreCombo object.
     *
     * @param wiParams DOCUMENT ME!
     * @param comboref DOCUMENT ME!
     */
    public CoreTransactionElement(ExecuteParams wiParams, 
    		TransactionElement transaction) {
        this.wiParams = wiParams;
        this.transaction = transaction;
        element = transaction;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        if (!isValidCondition()) {
            return;
        }
        endTransaction();
        if (transaction.isStart()) {
        	WIMap wiMap = wiParams.getWIMap();
        	wiMap.put("wi.transaction.id", Function.randomKey());        	
        	wiMap.put("wi.transaction.wiobj", transaction.getWIObj());
        	wiMap.put("wi.transaction.msgtrue", transaction.getMessageTrue());
        	wiMap.put("wi.transaction.msgfalse", transaction.getMessageFalse());
        	wiMap.put("wi.transaction.msgnone", transaction.getMessageNone());
        	wiMap.put("wi.transaction.status", "true");
        	wiMap.put("wi.transaction.none", "true");
			wiParams.getDatabaseAliases().autocommitAll(false);
        }
        writeLog();
    }
        
    public void endTransaction() {
    	WIMap wiMap = wiParams.getWIMap();
    	String status = wiMap.get("wi.transaction.status");
    	String none = wiMap.get("wi.transaction.none");
    	if (!status.equals("")) {
        	String wiobj = wiMap.get("wi.transaction.wiobj");
        	if (none.equals("true")) {
    			String msgnone = wiMap.get("wi.transaction.msgnone");
    			msgnone = Producer.execute(wiMap, msgnone);
    			wiMap.put(wiobj, msgnone);
            } else {
	    		if (status.equals("true")) {
	    			wiParams.getDatabaseAliases().commitAll();
	    			String msgtrue = wiMap.get("wi.transaction.msgtrue");
	    			msgtrue = Producer.execute(wiMap, msgtrue);
	    			if (msgtrue.trim().equals("")) msgtrue = "true";
	    			wiMap.put(wiobj, msgtrue);
	            	wiMap.put(wiobj + ".status()", "true");
	            	wiMap.put(wiobj + ".ok()", "true");
	    		} else {
	    			wiParams.getDatabaseAliases().rollbackAll();
	    			String msgfalse = wiMap.get("wi.transaction.msgfalse");
	    			msgfalse = Producer.execute(wiMap, msgfalse);
	    			if (msgfalse.trim().equals("")) msgfalse = status;
	    			wiMap.put(wiobj, msgfalse);
	            	wiMap.put(wiobj + ".status()", status);
	            	wiMap.put(wiobj + ".ok()", "false");
		        	IntFunction.setMessageError(wiMap, wiobj, msgfalse);
	    		}
        	}	
    		wiMap.remove("wi.transaction.");
			wiParams.getDatabaseAliases().autocommitAll(true);
    	}    	
    }

}
