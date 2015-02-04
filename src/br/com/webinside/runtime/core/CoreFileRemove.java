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

import java.io.*;
import br.com.webinside.runtime.component.*;
import br.com.webinside.runtime.integration.*;
import br.com.webinside.runtime.net.*;
import br.com.webinside.runtime.util.*;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
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
        if ((!dir.exists()) || (!dir.isDirectory())) {
            String msg = "Directory not found (" + dir + ")";
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
                EngFunction.hostError(wiParams, remftp.getHostId());
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
