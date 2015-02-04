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

package br.com.webinside.runtime.net;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

import br.com.webinside.runtime.util.FileIO;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class ClientSocket {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private FileIO log;

    /**
     * Creates a new ClientSocket object.
     *
     * @param host DOCUMENT ME!
     * @param port DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public ClientSocket(String host, int port) throws IOException {
        socket = new Socket(host, port);
        socket.setSoTimeout(10000); // 10 segundos
        out = new PrintWriter(socket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * Creates a new ClientSocket object.
     *
     * @param me DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public ClientSocket(Socket me) throws IOException {
        socket = me;
        socket.setSoTimeout(10000); // 10 segundos
        out = new PrintWriter(socket.getOutputStream());
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * Creates a new ClientSocket object.
     *
     * @param properties DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public ClientSocket(Map properties) {
    	socket = (Socket) properties.get("socket");
    	in = (BufferedReader) properties.get("in");
    	out = (PrintWriter) properties.get("out");
    }
    
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isConnected() {
        if (socket != null) {
            return true;
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param mille DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public void setTimeout(int mille) throws IOException {
        if (socket != null) {
            socket.setSoTimeout(mille);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param line DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public void writeln(String line) {
        if (socket != null && out != null) {
            out.print(line + "\r\n");
            out.flush();
            if (log != null) {
            	log.writeln("WRITE: " + line);
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public String readln() throws IOException {
        if (socket != null && in != null) {
            String line = in.readLine();
            if (log != null) {
            	log.writeln("READ: " + line);
            }
            return line;
        } else {
        	return null;
        }
    }
    
    /**
     * DOCUMENT ME!
     */
    public void close() {
        try {
	    	if (out != null) {
	            out.close();
	    	}
        } catch (Exception err) {
        	// ignorado
        }
        out = null;
        try {
        	if (in != null) {
        		in.close();
        	}
        } catch (Exception err) {
        	// ignorado
        }
        in = null;
        try {
        	if (socket != null) {
        		socket.close();
        	}
        } catch (Exception err) {
        	// ignorado
        }
        socket = null;
    }

    public Map getProperties() {
    	Map resp = new HashMap();
    	resp.put("socket", socket);
    	resp.put("in", in);    	
    	resp.put("out", out);    	
    	return resp;
    }
    
    public FileIO getLog() {
    	return log;
    }
    
    public void setLog(FileIO log) {
    	this.log = log;
    }
        
}
