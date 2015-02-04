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

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;
import java.util.Map;

import br.com.webinside.runtime.database.ErrorCode;
import br.com.webinside.runtime.net.ClientSocket;
import br.com.webinside.runtime.util.ErrorLog;
import br.com.webinside.runtime.util.MjavaRepository;
import br.com.webinside.runtime.util.StringA;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class ConnectionMjavaPort {
    private String srvHost;
    private int srvPort;
    private String errorMsg;
    private ClientSocket tmpSocket;
    private ConnectionMjavaClient parent;
    private static final int DEFAULT_TIMEOUT = 30000; 

    /**
     * Creates a new MjavaPort object.
     *
     * @param mClient DOCUMENT ME!
     * @param host DOCUMENT ME!
     * @param port DOCUMENT ME!
     */
    protected ConnectionMjavaPort(ConnectionMjavaClient mClient, String host, int port) {
        parent = mClient;
        tmpSocket = null;
        this.srvHost = host;
        this.srvPort = port;
        errorMsg = "";
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected String getErrorMsg() {
        return this.errorMsg;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected String getVersionCode() {    	
        synchronized (MjavaRepository.mjavaStaticHosts) {
            errorMsg = "";
            String response = "";
            boolean status = getConnection();
            if (status) {
                response = getVersionCodePriv();
            }
            tmpSocket = null;
            return response;
        }
    }

    private String getVersionCodePriv() {
    	Map staticHosts = MjavaRepository.mjavaStaticHosts;
        try {
            tmpSocket.writeln("<mjava release\"/>");
            String resp = tmpSocket.readln();
            String aux = StringA.piece(resp, "release=\"", 2);
            String aux2 = StringA.piece(resp, "type=\"", 2);
            if (StringA.piece(aux2, "\"", 1).equals("ON")) {
                staticHosts.put(srvHost + ":" + srvPort, tmpSocket.getProperties());
            } else {
                tmpSocket.close();
            }
            return StringA.piece(aux, "\"", 1);
        } catch (IOException err) {
            tmpSocket.close();
            staticHosts.remove(srvHost + ":" + srvPort);
            return "";
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param user DOCUMENT ME!
     * @param pass DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected int getPort(String user, String pass) {
        synchronized (MjavaRepository.mjavaStaticHosts) {
            errorMsg = "";
            int response = ErrorCode.MJAVA_CONNECTIONERROR;
            boolean status = getConnection();
            if (status) {
                response = getPortPriv(user, pass);
            }
            tmpSocket = null;
            return response;
        }
    }

    private int getPortPriv(String user, String pass) {
        int response = ErrorCode.MJAVA_COMMUNICATIONERROR;
        user = StringA.getXml(user);
        pass = StringA.getXml(pass);
    	Map staticHosts = MjavaRepository.mjavaStaticHosts;    	
        try {
            tmpSocket.writeln("<mjava user=\"" + user + "\" pass=\"" + pass
                + "\"/>");
            String resp = tmpSocket.readln();
            String aux = StringA.piece(resp, "type=\"", 2);
            if (StringA.piece(aux, "\"", 1).equals("ON")) {            	
                staticHosts.put(srvHost + ":" + srvPort, tmpSocket.getProperties());
            } else {
                tmpSocket.close();
            }
            aux = StringA.piece(resp, "port=\"", 2);
            try {
                response = Integer.parseInt(StringA.piece(aux, "\"", 1));
            } catch (NumberFormatException err) {
                tmpSocket.close();
                staticHosts.remove(srvHost + ":" + srvPort);
                errorMsg = "Login Error";
                response = ErrorCode.MJAVA_LOGINERROR;
            }
        } catch (IOException err) {
            tmpSocket.close();
            staticHosts.remove(srvHost + ":" + srvPort);
            errorMsg = "Communication Error - Server";
        }
        return response;
    }

    private boolean getConnection() {
    	Map staticHosts = MjavaRepository.mjavaStaticHosts;    	
        try {
            Map properties = (Map) staticHosts.get(srvHost + ":" + srvPort);
            if (properties != null) {
	            tmpSocket = new ClientSocket(properties);
            }    
        } catch (Exception err) {
        	// ignorado 
        }
        if (tmpSocket == null) {
            int cont = 0;
            while ((tmpSocket == null) && (cont < 3)) {
                if (cont > 0) {
                    try {
                        Thread.sleep(250);
                    } catch (InterruptedException err) {
                    }
                }
                cont = cont + 1;
                try {
                    tmpSocket = new ClientSocket(srvHost, srvPort);
                    tmpSocket.setTimeout(DEFAULT_TIMEOUT);
                } catch (UnknownHostException err) {
                    ErrorLog log = parent.getErrorLog();
                    if (log != null) {
                        log.write("MjavaPort", "1", err);
                    }
                    errorMsg = err.toString();
                    return false;
                } catch (NoRouteToHostException err) {
                    ErrorLog log = parent.getErrorLog();
                    if (log != null) {
                        log.write("MjavaPort", "2", err);
                    }
                    errorMsg = err.toString();
                    return false;
                } catch (IOException err) {
                    tmpSocket = null;
                    staticHosts.remove(srvHost + ":" + srvPort);
                }
            }
        }
        if (tmpSocket == null) {
            errorMsg = "Connection Error - Server";
            return false;
        }
        return true;
    }
}
