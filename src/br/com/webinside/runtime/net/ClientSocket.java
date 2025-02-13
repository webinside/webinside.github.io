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

package br.com.webinside.runtime.net;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

import br.com.webinside.runtime.util.FileIO;
import br.com.webinside.runtime.util.Function;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.3 $
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
    	Function.closeStream(in);
    	in = null;
    	Function.closeStream(out);
    	out = null;
    	Function.closeStream(socket);
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
