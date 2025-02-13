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

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.MessagingException;
import javax.mail.Store;

import br.com.webinside.runtime.component.Host;
import br.com.webinside.runtime.component.MailKill;
import br.com.webinside.runtime.integration.IntFunction;
import br.com.webinside.runtime.integration.Producer;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.util.StringA;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.4 $
 */
public class CoreMailKill extends CoreCommon {

	private MailKill kill;
    Store store = null;

    /**
     * Creates a new CoreMailKill object.
     *
     * @param wiParams DOCUMENT ME!
     * @param kill DOCUMENT ME!
     */
    public CoreMailKill(ExecuteParams wiParams, MailKill kill) {
        this.wiParams = wiParams;
        this.kill = kill;
        element = kill;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        if (!isValidCondition()) return;
        try {
        	store = null;
        	mailKill();
        	if (store != null) {
        		store.close();
        	}
        } catch (MessagingException err) {
        	wiParams.getErrorLog().write(getClass().getName(), "execute", err);
        }
    }    
        
    private void mailKill() throws MessagingException {    
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        Host host = wiParams.getProject().getHosts().getHost(kill.getHostId());
        if (host != null &&
        		(host.getProtocol().startsWith("POP3") || 
        				host.getProtocol().startsWith("IMAP"))) {
        	store = IntFunction.getStoreConnection(wiMap, host);
        	if (store == null) {
                RtmFunction.hostError(wiParams, kill.getHostId());
        		return;
        	}
        }
        prod.setInput(kill.getIdList());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String ids = prod.getOutput();
        String fld = Producer.execute(wiMap, kill.getFolder()).trim();
        if (fld.equals("")) fld = "INBOX";
        Folder folder = store.getFolder(fld);
        folder.open(Folder.READ_WRITE);
        int cont = StringA.count(ids, ',');
        for (int i = 1; i <= (cont + 1); i++) {
            String tid = StringA.piece(ids, ",", i).trim();
            int id = 0;
            try {
                id = Integer.parseInt(tid);
                if (id < 0) id = 0;
            } catch (NumberFormatException err) { }
            if (id > 0) {
            	folder.getMessage(id).setFlag(Flags.Flag.DELETED, true);
            }
        }
        folder.close(true);
        writeLog();
    }
}
