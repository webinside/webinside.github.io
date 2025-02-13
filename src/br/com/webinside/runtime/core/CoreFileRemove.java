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

import java.io.*;
import br.com.webinside.runtime.component.*;
import br.com.webinside.runtime.integration.*;
import br.com.webinside.runtime.net.*;
import br.com.webinside.runtime.util.*;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.3 $
 */
public class CoreFileRemove extends CoreCommon {
    private AbstractFileRemove remove;

    /**
     * Creates a new CoreFileRemove object.
     *
     * @param wiParams DOCUMENT ME!
     * @param remove DOCUMENT ME!
     */
    public CoreFileRemove(ExecuteParams wiParams, AbstractFileRemove remove) {
        this.wiParams = wiParams;
        this.remove = remove;
        element = remove;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        if (!isValidCondition()) {
            return;
        }
        if (remove instanceof FileRemoveLocal) {
            local();
        }
        if (remove instanceof FileRemoveFtp) {
            remote((FileRemoveFtp) remove);
        }
        writeLog();
    }

    private void local() {
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        prod.setInput(remove.getDirectory());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String directory = prod.getOutput();
        File dir = new File(directory);
        if (!dir.exists()) return;
        if (!dir.isDirectory()) {
            String msg = "Its not directory (" + dir + ")";
            String jspFile = wiMap.get("wi.jsp.filename");
            wiParams.getErrorLog().write(jspFile, remove.getDescription(), msg);
            if (!wiParams.getPage().getErrorPageName().equals("")) {
                Exception ex = new FileNotFoundException(msg);
                wiParams.setRequestAttribute("wiException", ex);
            }
            return;
        }
        prod.setInput(remove.getMask());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String fullMask = prod.getOutput();
        int cont = StringA.count(fullMask, ',');
        for (int i = 1; i <= (cont + 1); i++) {
            String mask = StringA.piece(fullMask, ",", i);
            if (!mask.trim().equals("")) {
            	Function.removeFiles(directory, mask);
            }	
        }
        if (remove.getRemoveDir().equals("ON")) {
            Function.removeDir(dir.toString(), false);
        }
    }

    private void remote(FileRemoveFtp remftp) {
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        prod.setInput(remove.getDirectory());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String directory = prod.getOutput();
        Host host =
            wiParams.getProject().getHosts().getHost(remftp.getHostId());
        ClientFtp ftp = null;
        if ((host != null) && (host.getProtocol().equals("FTP"))) {
            ftp = new ClientFtp(host.getAddress(), host.getUser(),
                    host.getPass());
            if (ftp.isConnected()) {
                if (!ftp.existDir(directory)) {
                    ftp.close();
                    ftp = null;
                }
            } else {
                RtmFunction.hostError(wiParams, remftp.getHostId());
                return;
            }
        }
        prod.setInput(remove.getMask());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String fullMask = prod.getOutput();
        int cont = StringA.count(fullMask, ',');
        for (int i = 1; i <= (cont + 1); i++) {
            String mask = StringA.piece(fullMask, ",", i);
            if (!mask.trim().equals("")) {
                ftp.delete(directory, mask, true);
            }	
        }
        if (remove.getRemoveDir().equals("ON")) {
            ftp.rmdir(directory, true);
        }
        ftp.close();
    }
}
