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
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.webinside.runtime.database.ErrorCode;
import br.com.webinside.runtime.net.ClientSocket;
import br.com.webinside.runtime.util.ErrorLog;
import br.com.webinside.runtime.util.MjavaRepository;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class ConnectionMjavaClient {
    private String srvHost;
    private int srvPort;
    private String nspace;
    private String user;
    private String pass;
    private Map headers;
    private Map[] rows;
    private int update;
    private boolean updateflag;
    private String errorMsg;
    private ConnectionMjavaPort mPort;
    private String filter = "";
    private ConnectionMjava parent;
    private static final int DEFAULT_TIMEOUT = 30000; 
    private static final int MAX_ROWS = 10000; 

    /**
     * Creates a new MjavaClient object.
     *
     * @param mjava DOCUMENT ME!
     */
    public ConnectionMjavaClient(ConnectionMjava mjava) {
        this(mjava.getAlias(), mjava.getUser(), mjava.getPass());
        parent = mjava;
    }

    /**
     * Creates a new MjavaClient object.
     *
     * @param alias DOCUMENT ME!
     * @param user DOCUMENT ME!
     * @param pass DOCUMENT ME!
     */
    public ConnectionMjavaClient(String alias, String user, String pass) {
        String fullhost = StringA.piece(alias, "/", 3);
        srvHost = StringA.piece(fullhost, ":", 1).trim();
        srvPort = 3500;
        try {
            String sprt = StringA.piece(fullhost, ":", 2).trim();
            srvPort = Integer.parseInt(sprt);
        } catch (NumberFormatException err) {
        }
        nspace = StringA.piece(alias, "/", 4).trim();
        if (user == null) {
            user = "";
        }
        this.user = user;
        if (pass == null) {
            pass = "";
        }
        this.pass = pass;
        reset();
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
    protected Map getHeaders() {
        return headers;
    }

    private void reset() {
        reset(true);
    }

    private void reset(boolean cleanErrorMsg) {
        headers = new HashMap();
        rows = new Map[0];
        update = -1;
        updateflag = false;
        if (cleanErrorMsg) {
            errorMsg = "";
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param sql DOCUMENT ME!
     * @param wiMap DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected int executeUpdate(String sql, WIMap wiMap) {
        execute(sql, wiMap, 0, 0, false);
        if (!updateflag) {
            update=-11;
            if (errorMsg.equals("")) {
              errorMsg="(-11)Routine does not execute ^MJWUPD";
            }            
        }
        return update;
    }

    /**
     * DOCUMENT ME!
     *
     * @param sql DOCUMENT ME!
     * @param wiMap DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected Map[] executeSQL(String sql, WIMap wiMap) {
        execute(sql, wiMap, 0, 0, true);
        return rows;
    }

    /**
     * DOCUMENT ME!
     *
     * @param sql DOCUMENT ME!
     * @param wiMap DOCUMENT ME!
     * @param from DOCUMENT ME!
     * @param size DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected Map[] executeSQL(String sql, WIMap wiMap, int from, int size) {
        execute(sql, wiMap, from, size, true);
        return rows;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected String getVersionCode() {
        if (mPort == null) {
            mPort = new ConnectionMjavaPort(this, srvHost, srvPort);
        }
        String resposta = "";
        int cont = 0;
        while ((resposta.equals("")) && (cont < 3)) {
            if (cont > 0) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException interr) {
                	// ignorado
                }
            }
            cont = cont + 1;
            resposta = mPort.getVersionCode();
            errorMsg = "(-26)" + mPort.getErrorMsg();
            if (errorMsg.indexOf("Connection Error") > -1) {
                return "0";
            }
        }
        if (resposta.equals("")) {
            resposta = "0";
        }
        return resposta;
    }

    /**
     * DOCUMENT ME!
     *
     * @param filter DOCUMENT ME!
     */
    protected void setFilter(String filter) {
        if (filter == null) {
            filter = "";
        }
        this.filter = filter;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected String getFilter() {
        return filter;
    }

    private void execute(String sql, WIMap wiMap, int from, int size,
        boolean isSelect) {
        reset();
        if (from < 0) {
            from = 0;
        }
        if (size < 0) {
            size = 0;
        }
        if (sql == null) {
            sql = "";
        }
        if (wiMap == null) {
            wiMap = new WIMap();
        }
        Map toSend = createVariables(sql, wiMap);
        int porta = 0;
        ClientSocket sock = null;
        try {
        	SlavePort slavePort = new SlavePort();
        	slavePort.fromList = true;
        	while ((sock == null) && (slavePort.fromList)) {
        		slavePort = getPort();
        		porta = slavePort.number;
                if (porta <= 0) {
                    return;
                }
                sock = getConnection(porta);
        	}
            if (sock == null) {
                errorMsg =
                    "(-23)Connection Error - Slave Port";
                ErrorLog log = getErrorLog();
                if (log != null) {
                    log.write("MjavaClient", "1", errorMsg);
                }
                return;
            }
            sock.writeln("<mjava>");
            sock.readln();
            writeHeader(sock, toSend);
            sock.writeln("<type>");
            sock.readln();
            if (isSelect) {
                sock.writeln("<select from=\"" + from + "\" size=\"" + size
                    + "\"/>");
            } else {
                sock.writeln("<update/>");
            }
            sock.readln();
            sock.writeln("</type>");
            sock.readln();
            sock.writeln("<variables>");
            sock.readln();
            writeData(sock, toSend);
            sock.writeln("</variables>");
            sock.readln();
            sock.writeln("</mjava>");
            sock.readln();
            String mjIdent = StringA.piece(sql, "(", 1).trim();
            boolean persistent = readData(mjIdent, sock);
            sock.close();
            if (!errorMsg.trim().equals("")) {
                reset(false);
                if (!errorMsg.trim().startsWith("(")) {
                    errorMsg = "(-11)" + errorMsg;
                    update = -11;
                }
            } else if (persistent) {
            	Map hosts = MjavaRepository.mjavaPersistent;
              	synchronized (hosts) {
                	List ports = (List) hosts.get(srvHost + ":" + srvPort);
              		if (ports == null) {
              			ports = new ArrayList();
              			hosts.put(srvHost + ":" + srvPort, ports);
              		}
              		ports.add(new Integer(porta));
                }	
            }
        } catch (IOException err) {
            sock.close();
            reset();
            forceClose(porta);
            errorMsg = "(-24)Communication Error - Slave";
            ErrorLog log = getErrorLog();
            if (log != null) {
                log.write("MjavaClient", "2", errorMsg);
            }
        }
    }

    private void forceClose(int porta) {
  	  try {
  		if (porta > 0) {
	  	    ClientSocket sock = new ClientSocket(srvHost, porta);
	  	    sock.writeln("<close/>");
	  	    sock.close();
  		}    
  	  } catch (IOException err) {}
    }
        
    private SlavePort getPort() {
        if (mPort == null) {
            mPort = new ConnectionMjavaPort(this, srvHost, srvPort);
        }
        SlavePort slavePort = new SlavePort();
    	Map hosts = MjavaRepository.mjavaPersistent;
        synchronized (hosts) {
        	List ports = (List) hosts.get(srvHost + ":" + srvPort);
	        if (ports != null && ports.size() > 0) {
	        	slavePort.fromList = true;
	        	slavePort.number = ((Integer)ports.remove(0)).intValue(); 
	        	return slavePort;
	        }
        }    
        int porta = 0;
        int cont = 0;
        while ((porta <= 0) && (cont < 3)) {
            if (cont > 0) {
                try {
                    Thread.sleep(250);
                } catch (InterruptedException interr) {
                	// ignorado
                }
            }
            cont = cont + 1;
            porta = mPort.getPort(user, pass);
            if (porta == ErrorCode.MJAVA_CONNECTIONERROR) {
                cont = 3;
            }
        }
        if (porta == 0) {
            errorMsg = "(-22)Mjava Server is Full";
            ErrorLog log = getErrorLog();
            if (log != null) {
                log.write("MjavaClient", "3", errorMsg);
            }
        } else if (porta < 0) {
            errorMsg = "(-22)" + mPort.getErrorMsg();
            ErrorLog log = getErrorLog();
            if (log != null) {
                log.write("MjavaClient", "4", errorMsg);
            }
        }
    	slavePort.number = porta; 
    	return slavePort;
    }

    private Map createVariables(String sql, WIMap wiMap) {
        Map toSend = new HashMap();
        int from = sql.indexOf("(");
        if (from == -1) {
            from = sql.length();
        } else {
            from = from + 1;
        }
        int inc = 0;
        while (from < sql.length()) {
            int pos = sql.indexOf(",", from + inc);
            if (pos == -1) {
                pos = sql.length();
            }
            if (sql.charAt(pos - 1) == ')') {
                pos = pos - 1;
            }
            String node = StringA.mid(sql, from, pos - 1);
            node = StringA.changeChars(node, "\"'", "");
            String akey = StringA.piece(node, "=", 1).trim();
            String key = StringA.mid(akey, 1, akey.length() - 2).trim();
            String value = StringA.piece(node, "=", 2, 0).trim();
            if (!key.equals("")) {
                if (key.endsWith(".")) {
                    createVariablesRecursive(toSend, wiMap, key, "");
                } else {
                    if (value.equals("")) {
                        toSend.put(key, wiMap.get(key));
                    } else {
                        toSend.put(key, value);
                    }
                }
            }
            inc = 0;
            from = pos + 1;
        }
        toSend.put("wi.mjava.ident", StringA.piece(sql, "(", 1).trim());
        toSend.put("wi.mjava.session", wiMap.get("wi.session.id"));
        toSend.put("wi.mjava.nspace", nspace);
        toSend.put("wi.mjava.user", user);
        toSend.put("wi.mjava.pass", pass);
        toSend.put("wi.mjava.proj", wiMap.get("wi.proj.id"));
        toSend.put("wi.mjava.page", wiMap.get("wi.page.id"));
        return toSend;
    }

    private void createVariablesRecursive(Map toSend, WIMap wiMap,
        String key, String prefix) {
        if (toSend == null) {
            return;
        }
        WIMap aux = null;
        try {
            aux = (WIMap) wiMap.getObj(key);
        } catch (ClassCastException err) {
        }
        if (aux == null) {
            return;
        }
        Iterator it = aux.getInternalMap().keySet().iterator();
        while (it.hasNext()) {
            String node = (String) it.next();
            if (!node.trim().equals("")) {
                if (node.trim().endsWith(".")) {
                    createVariablesRecursive(toSend, aux, node, prefix + key);
                } else {
                    toSend.put(prefix + key + node, aux.get(node));
                }
            }
        }
    }

    private ClientSocket getConnection(int slavePort) {
        ClientSocket sock = null;
        try {
            int cont = 0;
            while ((sock == null) && (cont < 3)) {
                try {
                    if (cont > 0) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException interr) {
                        	// ignorado
                        }
                    }
                    cont = cont + 1;
                    sock = new ClientSocket(srvHost, slavePort);
                } catch (SocketException err) {
                    sock = null;
                }
            }
            if (sock != null) {
                sock.setTimeout(DEFAULT_TIMEOUT);
            }
        } catch (IOException err) {
            sock = null;
        }
        return sock;
    }

    private boolean readData(String mjIdent, ClientSocket sock) 
    		throws IOException {
        boolean persistent = false;
        boolean limitDeny= false;
    	String linha = "";        
        StringA aux = new StringA();
        StringA auxhead = new StringA();
        while (!linha.equals("</mjava>") && !limitDeny) {
            linha = sock.readln();
            if (linha == null) { 
            	throw new IOException("Mjava finalizator tag was not received"); 
            }
            aux.set(linha);
            if (linha.startsWith("<persistent")) {
            	persistent = true;
            } else if (linha.startsWith("<error")) {
                aux.set(aux.piece(">", 2));
                String msg = aux.piece("<", 1);
                aux.setXml(msg);
                aux.set(aux.change("<", "&lt;"));
                aux.set(aux.change(">", "&gt;"));
                errorMsg = aux.toString();
            } else if (linha.startsWith("<update")) {
                updateflag = true;
                aux.set(aux.piece(">", 2));
                String msg = aux.piece("<", 1);
                aux.setXml(msg);
                int resp = -1;
                if (aux.toString().trim().equalsIgnoreCase("true")) {
                    resp = 0;
                }
                try {
                    if (resp < 0) {
                        resp = Integer.parseInt(aux.toString().trim());
                    }
                    update = resp;
                } catch (NumberFormatException err) {
                    errorMsg =
                        "(-25)Wrong Update Response: " + aux.toString().trim();
                    update = -25;
                }
            } else if (linha.startsWith("<header")) {
                String seq = aux.piece("seq=\"", 2);
                seq = StringA.piece(seq, "\"", 1);
                String msg = aux.piece(">", 2);
                msg = StringA.piece(msg, "<", 1);
                aux.setXml(msg.toUpperCase());
                if ((!seq.trim().equals(""))
                            && (!aux.toString().trim().equals(""))) {
                    headers.put(seq.trim(), aux.toString().trim());
                    headers.put(aux.toString().trim(), seq.trim());
                }
            } else if ((linha.startsWith("<row")) && (!linha.equals("<rows>"))) {
                String msg = aux.piece(">", 2);
                msg = StringA.piece(msg, "<", 1);
                aux.setXml(msg);
                int len = rows.length;
            	if (len == MAX_ROWS) {
            		limitDeny = true;
            		if (parent != null && parent.getErrorLog() != null) {
            			ErrorLog log = parent.getErrorLog();
            			log.write(getClass().getName(), "MJIDENT - " + mjIdent, 
            					"Exceded " + MAX_ROWS + " rows");
            		}	
            	}	
                if (len < MAX_ROWS) {
	                Map[] newhash = new HashMap[len + 1];
	                for (int i = 0; i < len; i++) {
	                    newhash[i] = rows[i];
	                }
	                newhash[len] = new HashMap();
	                rows = newhash;
                }
            } else if (linha.startsWith("<column")) {
                String seq = aux.piece("seq=\"", 2);
                seq = StringA.piece(seq, "\"", 1).trim();
                String head = aux.piece("header=\"", 2);
                head = StringA.piece(head, "\"", 1).trim();
                auxhead.setXml(head.toUpperCase());
                if ((!seq.trim().equals(""))
                            && (!auxhead.toString().trim().equals(""))) {
                    headers.put(seq.trim(), auxhead.toString().trim());
                    headers.put(auxhead.toString().trim(), seq.trim());
                }
                String msg = aux.piece(">", 2);
                msg = StringA.piece(msg, "<", 1);
                aux.setXml(msg);
                if ((rows.length > 0) && (!seq.equals(""))) {
                    Map auxhash = rows[rows.length - 1];
                    auxhash.put(seq, aux.toString());
                    String header = (String) headers.get(seq);
                    if ((header == null) || (header.equals(""))) {
                        headers.put(seq, seq);
                    } else {
                        auxhash.put(header.trim().toLowerCase(), aux.toString());
                    }
                }
            }
        }
        return persistent;
    }

    private void writeData(ClientSocket sock, Map toSend) throws IOException {
        Iterator lista = toSend.keySet().iterator();
        while (lista.hasNext()) {
            String key = (String) lista.next();
            String value = (String) toSend.get(key);
            key = StringA.getXml(key);
            value = StringA.getXml(value);
            sendVariable(sock, key, value);
        }
    }

    private void sendVariable(ClientSocket sock, String key, String value) 
    	throws IOException {
        int tam = 25000; // (Global Overflow)
        if (value.length() > tam) {
            value = value.substring(0, tam);
        }
        String aux = value;
        boolean ini = true;
        int pos = -1;
        while (pos < aux.length()) {
            int qnt = 399;
            if (pos == -1) {
                pos = 0;
            }
            int end = pos + qnt;
            int pini = aux.lastIndexOf("&", end);
            int pfin = aux.lastIndexOf(";", end);
            if (pini > pfin) {
                end = aux.indexOf(";", end);
            }
            if (end == -1) {
                end = pos + qnt;
            }
            String line = "<variable";
            if (ini) {
                line = line + " id=\"" + key + "\"";
            }
            String varValue = StringA.mid(aux, pos, end);
            varValue = StringA.changeChars(varValue, filter, "");
            sock.writeln(line + ">" + varValue + "</variable>");
            sock.readln();
            ini = false;
            pos = end + 1;
        }
    }

    private void writeHeader(ClientSocket sock, Map toSend)
        throws IOException {
        String ident = (String) toSend.get("wi.mjava.ident");
        sock.writeln("<ident>" + StringA.getXml(ident) + "</ident>");
        sock.readln();
        String user1 = (String) toSend.get("wi.mjava.user");
        sock.writeln("<user>" + StringA.getXml(user1) + "</user>");
        sock.readln();
        String pass1 = (String) toSend.get("wi.mjava.pass");
        sock.writeln("<pass>" + StringA.getXml(pass1) + "</pass>");
        sock.readln();
        String session = (String) toSend.get("wi.mjava.session");
        sock.writeln("<session>" + StringA.getXml(session) + "</session>");
        sock.readln();
        String nspace1 = (String) toSend.get("wi.mjava.nspace");
        sock.writeln("<nspace>" + StringA.getXml(nspace1) + "</nspace>");
        sock.readln();
        String proj = (String) toSend.get("wi.mjava.proj");
        sock.writeln("<proj>" + StringA.getXml(proj) + "</proj>");
        sock.readln();
        String page = (String) toSend.get("wi.mjava.page");
        sock.writeln("<page>" + StringA.getXml(page) + "</page>");
        sock.readln();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected ErrorLog getErrorLog() {
        return parent.getErrorLog();
    }
    
    private class SlavePort {
    	int number = 0;
    	boolean fromList = false;
    }
}
