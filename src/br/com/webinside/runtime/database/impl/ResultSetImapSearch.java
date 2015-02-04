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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.search.AndTerm;
import javax.mail.search.BodyTerm;
import javax.mail.search.FromStringTerm;
import javax.mail.search.SearchTerm;
import javax.mail.search.SubjectTerm;

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
public class ResultSetImapSearch extends ResultSetNoSql 
	implements ResultSetJava {
	
	public void execute(AbstractProject project, WIMap wiMap, String params) {
    	Map headers = new HashMap();
    	headers.put("1", "Id");
    	headers.put("2", "Subject");
    	headers.put("3", "Date");
    	headers.put("4", "Size");
    	headers.put("5", "From");
    	setHeadersNoSQL(headers);
    	List rows = new ArrayList();
        try { 
        	String hst = StringA.piece(params, ",", 1);
        	String fld = StringA.piece(params, ",", 2);
        	String qsubject = StringA.piece(params, ",", 3);
        	String qbody = StringA.piece(params, ",", 4);
        	String qfrom = StringA.piece(params, ",", 5);
        	String hostId = Producer.execute(wiMap, hst).trim();
        	Host host = project.getHosts().getHost(hostId);
        	if (host == null) {
        		throw new NullPointerException("Host not found");
        	}
        	Store store = IntFunction.getStoreConnection(wiMap, host);
        	if (store != null) {
                fld = Producer.execute(wiMap, fld).trim();
                if (fld.equals("")) fld = "INBOX";
                Folder folder = store.getFolder(fld);
                folder.open(Folder.READ_ONLY);
                SearchTerm term = null;
                qsubject = Producer.execute(wiMap, qsubject).trim();
                if (!qsubject.equals("")) {
                	term = addTerm(term, new SubjectTerm(qsubject));
                }
                qbody = Producer.execute(wiMap, qbody).trim();
                if (!qbody.equals("")) {
                	term = addTerm(term, new BodyTerm(qbody));
                }	
                qfrom = Producer.execute(wiMap, qfrom).trim();
                if (!qfrom.equals("")) {
                	term = addTerm(term, new FromStringTerm(qfrom));
                }
                Message[] msgs = null;
                if (term != null) {
	                msgs = folder.search(term);
	        	    executeMessages(rows, wiMap, msgs);
	        	    folder.close(false);
                }
		        store.close();
        	}
        } catch (MessagingException ex) {
        	ExecuteParams wiParams = ExecuteParams.get();
        	if (wiParams != null && wiParams.getErrorLog() != null) {
        		wiParams.getErrorLog().write("ImapSearch", "execute", ex);
        	}	
        }
        setRowsNoSQL((Map[])rows.toArray(new Map[0]));
	}
	
    private void executeMessages(List rows, WIMap wiMap, Message[] msgs) 
    	throws MessagingException {
		for (int i = 0; msgs != null && i < msgs.length; i++) {
			Message m = msgs[i];
    		Map aux = new HashMap();
    		aux.put("1", m.getMessageNumber() + "");
    		aux.put("2", m.getSubject());
			aux.put("3", "");
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
			Date dt = m.getSentDate();
			if (dt != null) aux.put("3", sdf.format(dt));
			aux.put("4", m.getSize() + "");
			aux.put("5", "");
			if (m.getFrom() != null && m.getFrom().length > 0) {
				aux.put("5", m.getFrom()[0].toString());
			}	
    		rows.add(aux);
		}
	}
    
    private SearchTerm addTerm(SearchTerm term, SearchTerm newTerm) {
    	if (term == null) return newTerm;
    	term = new AndTerm(term, newTerm);
    	return term;
    }
    
}
