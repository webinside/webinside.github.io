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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Folder;
import javax.mail.Header;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.internet.MimeUtility;

import br.com.webinside.runtime.component.AbstractGrid;
import br.com.webinside.runtime.component.AbstractGridLinear;
import br.com.webinside.runtime.component.GridHtml;
import br.com.webinside.runtime.component.GridSql;
import br.com.webinside.runtime.component.Host;
import br.com.webinside.runtime.component.MailList;
import br.com.webinside.runtime.component.ProjectElementsMap;
import br.com.webinside.runtime.integration.IntFunction;
import br.com.webinside.runtime.integration.Producer;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.4 $
 */
public class CoreMailList extends CoreCommon {

	private MailList list;
    Store store = null;

    /**
     * Creates a new CoreMailList object.
     *
     * @param wiParams DOCUMENT ME!
     * @param list DOCUMENT ME!
     */
    public CoreMailList(ExecuteParams wiParams, MailList list) {
        this.wiParams = wiParams;
        this.list = list;
        element = list;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
    	if (!isValidCondition()) return;
        try {
        	store = null;
        	mailList();
        	if (store != null) {
        		store.close();
        	}
        } catch (MessagingException err) {
        	wiParams.getErrorLog().write(getClass().getName(), "execute", err);
        }
    }    
    	
    private void mailList() throws MessagingException {    	
        Host host = wiParams.getProject().getHosts().getHost(list.getHostId());
        if (host != null &&
        		(host.getProtocol().startsWith("POP3") || 
        				host.getProtocol().startsWith("IMAP"))) {
        	store = IntFunction.getStoreConnection(wiMap, host);
        	if (store == null) {
                RtmFunction.hostError(wiParams, list.getHostId());
        		return;
        	}
        }
        if (!wiParams.getProject().getGrids().containsKey(list.getGridId())) {
            wiParams.includeCode("/grids/" + list.getGridId() + "/grid.jsp");
        }
        ProjectElementsMap grids = wiParams.getProject().getGrids(); 
        AbstractGrid grid = (AbstractGrid)grids.getElement(list.getGridId());
        if ((grid != null) && !(grid instanceof GridHtml)) {
            grid = null;
        } else if (grid instanceof GridSql) {
            grid = null;
        }
        if (grid == null) {
            return;
        }
        String fld = Producer.execute(wiMap, list.getFolder()).trim();
        if (fld.equals("")) fld = "INBOX";
        Folder folder = store.getFolder(fld);
        folder.open(Folder.READ_ONLY);
        int countid = 0;
        List mapList = new ArrayList();
        Message[] lista = folder.getMessages();
        for (int i = 0; i < lista.length; i++) {
        	Message mail = lista[i];
            String size = mail.getSize() + "";
            add(countid, mapList, mail, size);
        }
        folder.close(false);
		Map[] array = (Map[])mapList.toArray(new Map[0]); 
        GridLinearNavigator linear = new GridLinearNavigator(wiParams);
        linear.execute((AbstractGridLinear) grid, array, 1, false);
        writeLog();
    }

    private void add(int countid, List mapList, Message mail, String size) 
    		throws MessagingException {
        countid = countid + 1;
        Map aux = new HashMap();
        aux.put("rowid", countid + "");
        aux.put("rowid0", countid - 1 + "");
		aux.put("size", size + "");
       	Enumeration e = mail.getAllHeaders();
    	while (e.hasMoreElements()) {
    		Header header = (Header)e.nextElement();
    		String value = header.getValue();
    		try {
    			value = MimeUtility.decodeText(value);
    		} catch (UnsupportedEncodingException uex) { }	
            aux.put(header.getName(), value);
        }
    	aux.put("id", mail.getMessageNumber() + "");
    	aux.put("folder", mail.getFolder().getFullName());
        mapList.add(aux);
    }
}
