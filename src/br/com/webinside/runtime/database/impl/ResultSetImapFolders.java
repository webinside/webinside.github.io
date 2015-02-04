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

package br.com.webinside.runtime.database.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;

import br.com.webinside.runtime.component.AbstractProject;
import br.com.webinside.runtime.component.Host;
import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.database.ResultSetJava;
import br.com.webinside.runtime.integration.IntFunction;
import br.com.webinside.runtime.integration.Producer;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class ResultSetImapFolders extends ResultSetNoSql 
	implements ResultSetJava {
	
	public void execute(AbstractProject project, WIMap wiMap, String params) {
    	Map headers = new HashMap();
    	headers.put("1", "Name");
    	headers.put("2", "Fullname");
    	headers.put("3", "Url");
    	setHeadersNoSQL(headers);
    	List rows = new ArrayList();
        try { 
        	String hst = StringA.piece(params, ",", 1);
        	String hostId = Producer.execute(wiMap, hst).trim();
        	Host host = project.getHosts().getHost(hostId);
        	if (host == null) {
        		throw new NullPointerException("Host not found");
        	}
        	Store store = IntFunction.getStoreConnection(wiMap, host);
        	if (store != null) {
		        Folder rf =  store.getDefaultFolder();
		        executeFolder(rows, wiMap, rf);
		        store.close();
        	}
        } catch (MessagingException ex) {
        	ExecuteParams wiParams = ExecuteParams.get();
        	if (wiParams != null && wiParams.getErrorLog() != null) {
        		wiParams.getErrorLog().write("ImapFolders", "execute", ex);
        	}	
        }
        setRowsNoSQL((Map[])rows.toArray(new Map[0]));
	}

    private void executeFolder(List rows, WIMap wiMap, Folder rf) 
    	throws MessagingException {
    	Folder[] f = rf.list("%");
    	if (f != null) {
	    	for (int i = 0; i < f.length; i++) {
	    		Folder folder = f[i];
	    		Map aux = new HashMap();
	    		aux.put("1", folder.getName());
	    		aux.put("2", folder.getFullName());
	    		aux.put("3", folder.getURLName().toString());
	    		rows.add(aux);
	            if ((folder.getType() & Folder.HOLDS_FOLDERS) != 0) {
	            	executeFolder(rows, wiMap, folder);
	            }	
	    	}
    	}	
    }

}
