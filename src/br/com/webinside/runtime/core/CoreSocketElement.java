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

import java.io.IOException;
import br.com.webinside.runtime.component.Host;
import br.com.webinside.runtime.component.SocketElement;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.net.ClientSocket;
import br.com.webinside.runtime.util.StringA;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class CoreSocketElement extends CoreCommon {
    private SocketElement socket;

    /**
     * Creates a new CoreSocketElement object.
     *
     * @param wiParams DOCUMENT ME!
     * @param socket DOCUMENT ME!
     */
    public CoreSocketElement(ExecuteParams wiParams, SocketElement socket) {
        this.wiParams = wiParams;
        this.socket = socket;
        element = socket;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        if (!isValidCondition()) {
            return;
        }
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        Host host =
            wiParams.getProject().getHosts().getHost(socket.getHostId());
        ClientSocket realsocket = null;
        if ((host != null) && (host.getProtocol().equals("SOCKET"))) {
            prod.setInput(host.getAddress());
            wiParams.getProducer().setParam(prod);
            wiParams.getProducer().execute();
            String strhost = prod.getOutput().trim();
            int port = 0;
            try {
                port = Integer.parseInt(host.getPort().trim());
                if (port < 0) {
                    port = 0;
                }
            } catch (NumberFormatException err) {
            }
            int timeout = 60000;
            try {
                timeout = Integer.parseInt(socket.getTimeout().trim()) * 1000;
                if (timeout < 1000) {
                	timeout = 1000;
                }
            } catch (NumberFormatException err) {
            }
            try {
                if (port > 0) {
                    realsocket = new ClientSocket(strhost, port);
					if (!realsocket.isConnected()) {
						realsocket = null;
					} else {
						realsocket.setTimeout(timeout);
					}
                }
            } catch (IOException err) {
                wiParams.getErrorLog().write("CoreSocket", "1", err);
                wiMap.put("wi.error", err.getMessage());
                realsocket = null;
            }
        }
        if (realsocket == null) {
            RtmFunction.hostError(wiParams, socket.getHostId());
            return;
        }
        String sendobj = StringA.changeChars(socket.getSendObj(), "| ", "");
        String readobj = StringA.changeChars(socket.getReadObj(), "| ", "");
        prod.setInput("|" + sendobj + "|");
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        try {
            realsocket.writeln(prod.getOutput());
            StringBuffer response = new StringBuffer();
            String linha = "";
            while ((linha = realsocket.readln()) != null) {
                if (response.length() > 0) {
                    response.append("\r\n");
                }
                response.append(linha);
            }
            if (socket.getDecodeXML().equals("ON")) {
                new CoreXmlImport(wiMap, readobj).execute(response.toString());
            } else {
                wiMap.put(readobj, response.toString());
            }
        } catch (IOException err) {
            wiMap.put("wi.error", err.getMessage());
            String jspFile = wiMap.get("wi.jsp.filename");
            wiParams.getErrorLog().write(jspFile, socket.getDescription(),
                err.getMessage());
            if (!wiParams.getPage().getErrorPageName().equals("")) {
                wiParams.setRequestAttribute("wiException", err);
            }
        }
        writeLog();
    }
}
