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

import java.util.StringTokenizer;
import java.io.*;
import java.net.*;
import sun.net.TransferProtocolClient;
import sun.net.TelnetInputStream;
import sun.net.TelnetOutputStream;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class FtpClient extends TransferProtocolClient {
    /** DOCUMENT ME! */
    public static final int FTP_PORT = 21;
    private static int FTP_CONTINUE = 1;
    private static int FTP_SUCCESS = 2;
    private static int FTP_ERROR = 5;
    /** socket for data transfer */
    private Socket dataSocket = null;
    private boolean replyPending = false;
    private boolean binaryMode = false;
    /** user name for login */
    String usr = null;
    /** password for login */
    String passw = null;
    /** last command issued */
    String command;
    /** The last reply code from the ftp daemon. */
    int lastReplyCode;
    /** Welcome message from the server, if any. */
    public String welcomeMsg;

    /**
     * New FTP client connected to host <i>host</i>.
     *
     * @param host DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public FtpClient(String host) throws IOException {
        super();
        openServer(host, FTP_PORT);
    }

    /**
     * New FTP client connected to host <i>host</i>, port <i>port</i>.
     *
     * @param host DOCUMENT ME!
     * @param port DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public FtpClient(String host, int port) throws IOException {
        super();
        openServer(host, port);
    }

    /**
     * Create an uninitialized FTP client.
     */
    public FtpClient() {
    }

    // issue the QUIT command to the FTP server and close the connection.
    public void closeServer() throws IOException {
        if (serverIsOpen()) {
            issueCommand("QUIT");
            super.closeServer();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param cmd DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    protected int issueDataCommand(String cmd) {
        command = cmd;
        replyPending = false;
        sendServer(cmd + "\r\n");
        return FTP_SUCCESS;
    }

    /**
     * DOCUMENT ME!
     *
     * @param cmd DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    protected int issueCommand(String cmd) throws IOException {
        command = cmd;
        if (replyPending) {
            readReply();
        }
        replyPending = false;
        sendServer(cmd + "\r\n");
        return readReply();
    }

    /**
     * DOCUMENT ME!
     *
     * @param cmd DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws FtpProtocolException DOCUMENT ME!
     */
    protected void issueCommandCheck(String cmd) throws IOException {
        if (issueCommand(cmd) == FTP_ERROR) {
            throw new FtpProtocolException(cmd);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws FtpLoginException DOCUMENT ME!
     * @throws FileNotFoundException DOCUMENT ME!
     */
    protected int readReply() throws IOException {
        lastReplyCode = readServerResponse();
        switch (lastReplyCode / 100) {
        case 1:
            replyPending = true;
            return FTP_CONTINUE;

        case 2:
            replyPending = false;
            return FTP_SUCCESS;

        case 3:
            replyPending = false;
            return FTP_SUCCESS;

        case 5:
            if (lastReplyCode == 530) {
                if (usr == null) {
                    throw new FtpLoginException("Not logged in");
                }
                return FTP_ERROR;
            }
            if (lastReplyCode == 550) {
                throw new FileNotFoundException(command + ": "
                    + getResponseString());
            }
        }
        return FTP_ERROR;
    }

    // Abre uma conecçao secundária
    protected Socket openDataConnection(String cmd)
        throws IOException {
        ServerSocket portSocket;
        String portCmd;
        InetAddress myAddress = InetAddress.getLocalHost();
        byte[] addr = myAddress.getAddress();
        IOException e;
        portSocket = new ServerSocket(0, 1);
        portCmd = "PORT ";

        /* append host addr */
        for (int i = 0; i < addr.length; i++) {
            portCmd = portCmd + (addr[i] & 0xFF) + ",";
        }

        /* append port number */
        portCmd =
            portCmd + ((portSocket.getLocalPort() >>> 8) & 0xff) + ","
            + (portSocket.getLocalPort() & 0xff);
        // Erro de conexão
        if (issueCommand(portCmd) == FTP_ERROR) {
            e = new FtpProtocolException("PORT");
            portSocket.close();
            throw e;
        }
        if (issueDataCommand(cmd) == FTP_ERROR) {
            e = new FtpProtocolException(cmd);
            portSocket.close();
            throw e;
        }
        dataSocket = portSocket.accept();
        portSocket.close();
        readReply();
        return dataSocket;
    }

    // Abre uma conecçao secundária PASV
    protected Socket openDataConnectionPASV(String cmd)
        throws IOException {
        IOException e;
        if (issueCommand("PASV") == FTP_ERROR) {
            e = new FtpProtocolException("PASV");
            throw e;
        }
        String resp = this.getResponseString();
        int ini = resp.indexOf("(") + 1;
        int fin = resp.indexOf(")");
        resp = resp.substring(ini, fin).replace(',', '.');
        fin = 0;
        for (int cont = 0; cont < 4; fin = fin + 1) {
            if (resp.charAt(fin) == '.') {
                cont = cont + 1;
            }
        }
        String newhost = resp.substring(0, fin - 1);
        ini = fin;
        for (int cont = 0; cont < 1; fin = fin + 1) {
            if (resp.charAt(fin) == '.') {
                cont = cont + 1;
            }
        }
        String newport1 = resp.substring(ini, fin - 1);
        int np1 = Integer.parseInt(newport1);
        String newport2 = resp.substring(fin, resp.length());
        int np2 = Integer.parseInt(newport2);
        int npfinal = (np1 << 8) | np2;
        if (issueDataCommand(cmd) == FTP_ERROR) {
            e = new FtpProtocolException(cmd);
            throw e;
        }
        dataSocket = new Socket(newhost, npfinal);
        if (dataSocket == null) {
            e = new FtpProtocolException("Null DataSocket");
            throw e;
        }
        readReply();
        return dataSocket;
    }

    /* public methods */
    /**
     * open a FTP connection to host <i>host</i>.
     *
     * @param host DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public void openServer(String host) throws IOException {
        int port = FTP_PORT;
        openServer(host, port);
    }

    /**
     * open a FTP connection to host <i>host</i> on port <i>port</i>.
     *
     * @param host DOCUMENT ME!
     * @param port DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     * @throws FtpProtocolException DOCUMENT ME!
     */
    public void openServer(String host, int port)
        throws IOException {
        super.openServer(host, port);
        if (readReply() == FTP_ERROR) {
            throw new FtpProtocolException("Welcome message");
        }
    }

    // login user to a host with username <i>user</i> and password
    public void login(String user, String password)
        throws IOException {
        if (!serverIsOpen()) {
            throw new FtpLoginException("not connected to host");
        }
        this.usr = user;
        this.passw = password;
        if (issueCommand("USER " + user) == FTP_ERROR) {
            throw new FtpLoginException("user");
        }
        if ((password != null)
                    && (issueCommand("PASS " + password) == FTP_ERROR)) {
            throw new FtpLoginException("password");
        }

        // keep the welcome message around so we can
        // put it in the resulting HTML page.
        String l;
        for (int i = 0; i < serverResponse.size(); i++) {
            l = (String) serverResponse.elementAt(i);
            if (l != null) {
                if (l.charAt(3) != '-') {
                    break;
                }

                // get rid of the "230-" prefix
                l = l.substring(4);
                if (welcomeMsg == null) {
                    welcomeMsg = l;
                } else {
                    welcomeMsg += l;
                }
            }
        }
    }

    /**
     * GET a file from the FTP server
     *
     * @param filename DOCUMENT ME!
     * @param pasv DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public TelnetInputStream get(String filename, boolean pasv)
        throws IOException {
        Socket s;
        try {
            if (!pasv) {
                s = openDataConnection("RETR " + filename);
            } else {
                s = openDataConnectionPASV("RETR " + filename);
            }
        } catch (FileNotFoundException fileException) {
            // Desloca-se pelos diretorios
            cd("/");
            StringTokenizer t = new StringTokenizer(filename, "/");
            String pathElement = null;
            while (t.hasMoreElements()) {
                pathElement = t.nextToken();
                if (!t.hasMoreElements()) {
                    break;
                }
                try {
                    cd(pathElement);
                } catch (FtpProtocolException e) {
                    throw fileException;
                }
            }
            if (pathElement != null) {
                if (!pasv) {
                    s = openDataConnection("RETR " + pathElement);
                } else {
                    s = openDataConnectionPASV("RETR " + pathElement);
                }
            } else {
                throw fileException;
            }
        }
        return new TelnetInputStream(s.getInputStream(), binaryMode);
    }

    /**
     * PUT a file to the FTP server
     *
     * @param filename DOCUMENT ME!
     * @param pasv DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public TelnetOutputStream put(String filename, boolean pasv)
        throws IOException {
        Socket s;
        if (!pasv) {
            s = openDataConnection("STOR " + filename);
        } else {
            s = openDataConnectionPASV("STOR " + filename);
        }
        return new TelnetOutputStream(s.getOutputStream(), binaryMode);
    }

    /**
     * LIST files on a remote FTP server
     *
     * @param pasv DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public TelnetInputStream list(boolean pasv) throws IOException {
        Socket s = null;
        if (!pasv) {
            s = openDataConnection("LIST");
        } else {
            s = openDataConnectionPASV("LIST");
        }
        return new TelnetInputStream(s.getInputStream(), binaryMode);
    }

    /**
     * CD to a specific directory on a remote FTP server
     *
     * @param remoteDirectory DOCUMENT ME!
     *
     * @throws IOException DOCUMENT ME!
     */
    public void cd(String remoteDirectory) throws IOException {
        issueCommandCheck("CWD " + remoteDirectory);
    }

    /**
     * Set transfer type to 'I'
     *
     * @throws IOException DOCUMENT ME!
     */
    public void binary() throws IOException {
        issueCommandCheck("TYPE I");
        binaryMode = true;
    }

    /**
     * Set transfer type to 'A'
     *
     * @throws IOException DOCUMENT ME!
     */
    public void ascii() throws IOException {
        issueCommandCheck("TYPE A");
        binaryMode = false;
    }
}
