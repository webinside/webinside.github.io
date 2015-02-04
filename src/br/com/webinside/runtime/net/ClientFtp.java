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
import sun.net.TelnetInputStream;
import sun.net.TelnetOutputStream;
import br.com.webinside.runtime.util.*;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class ClientFtp {
    private static int bsize = 2048;
    /** DOCUMENT ME! */
    private static final int CONNECTED = 1;
    /** DOCUMENT ME! */
    private static final int UNKNOWN = 0;
    /** DOCUMENT ME! */
    private static final int LOGINERROR = -1;
    private FtpClient ftp;
    private int status;
    private boolean passive;

    /**
     * Creates a new ClientFtp object.
     *
     * @param host DOCUMENT ME!
     * @param user DOCUMENT ME!
     * @param pass DOCUMENT ME!
     */
    public ClientFtp(String host, String user, String pass) {
        if (host == null) {
            host = "";
        }
        if (user == null) {
            user = "";
        }
        if (pass == null) {
            pass = "";
        }
        passive = true;
        status = UNKNOWN;
        try {
            ftp = new FtpClient(host.trim(), 21);
            try {
                ftp.login(user, pass);
                status = CONNECTED;
            } catch (FtpLoginException err) {
                ftp.closeServer();
                status = LOGINERROR;
            }
        } catch (IOException err) {
            System.err.println(getClass().getName() + ": " + err);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param passive DOCUMENT ME!
     */
    public void setPassive(boolean passive) {
        this.passive = passive;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getStatus() {
        return status;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isConnected() {
        if (status == CONNECTED) {
            return true;
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param path DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public FtpEntry[] listFull(String path) {
        if (path == null) {
            return new FtpEntry[0];
        }
        FtpEntry[] resposta = new FtpEntry[0];
        if (status != CONNECTED) {
            return resposta;
        }
        try {
            if (path.equals("")) {
                path = "/";
            }
            ftp.ascii();
            ftp.cd(StringA.change(path, "\\", "/"));
            TelnetInputStream arqin = ftp.list(passive);
            int tam = 0;
            byte[] bt = new byte[bsize];
            StringA linha = new StringA();
            while ((tam = arqin.read(bt, 0, bsize)) > 0) {
                linha.append(asLine(bt, tam));
                int pos = 0;
                int fim = linha.length() - 1;
                while (pos <= fim) {
                    int next = linha.indexOf("\n", pos);
                    String parte = "";
                    if (next > -1) {
                        int add = 1;
                        if (linha.indexOf("\r", next - 1) == (next - 1)) {
                            next = next - 1;
                            add = 2;
                        }
                        parte = linha.mid(pos, next - 1);
                        pos = next + add;
                    } else {
                        pos = fim + 1;
                    }

                    // Traz linhas completas
                    if (parte.length() >= 55) {
                        FtpEntry tmpentry = new FtpEntry();
                        tmpentry.setEntry(parte);
                        resposta = add(resposta, tmpentry);
                    }
                }

                // Filtra para o Append de Linha
                int from = linha.lastIndexOf("\n");
                linha.set(linha.mid(from + 1, fim));
            }

            // Residuo na linha
            if (linha.length() >= 55) {
                FtpEntry tmpentry = new FtpEntry();
                tmpentry.setEntry(linha.toString());
                resposta = add(resposta, tmpentry);
            }
            arqin.close();
            ftp.readReply();
        } catch (IOException err) {
        }
        return resposta;
    }

    private FtpEntry[] maskedList(String path, String mask, boolean upcase,
        boolean dir) {
        if ((path == null) || (mask == null)) {
            return new FtpEntry[0];
        }
        FtpEntry[] temp = listFull(path);
        FtpEntry[] resposta = new FtpEntry[0];
        for (int i = 0; i < temp.length; i++) {
            boolean ok = false;
            if ((dir) && (temp[i].isDirectory())) {
                ok = true;
            }
            if ((!dir) && (temp[i].isFile())) {
                ok = true;
            }
            if (ok) {
                String nome = temp[i].getName();
                boolean mok = Function.validateMask(nome, mask, true);
                if (upcase) {
                    mok = Function.validateMask(nome.toUpperCase(),
                            mask.toUpperCase(), true);
                }
                if (mok) {
                    resposta = add(resposta, temp[i]);
                }
            }
        }
        return resposta;
    }

    /**
     * DOCUMENT ME!
     *
     * @param path DOCUMENT ME!
     * @param mask DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public FtpEntry[] listFiles(String path, String mask) {
        return maskedList(path, mask, false, false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param path DOCUMENT ME!
     * @param mask DOCUMENT ME!
     * @param upcase DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public FtpEntry[] listFiles(String path, String mask, boolean upcase) {
        return maskedList(path, mask, upcase, false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param path DOCUMENT ME!
     * @param mask DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public FtpEntry[] listDirs(String path, String mask) {
        return maskedList(path, mask, false, true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param path DOCUMENT ME!
     * @param mask DOCUMENT ME!
     * @param upcase DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public FtpEntry[] listDirs(String path, String mask, boolean upcase) {
        return maskedList(path, mask, upcase, true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param path DOCUMENT ME!
     * @param mask DOCUMENT ME!
     * @param upcase DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean delete(String path, String mask, boolean upcase) {
        if (status != CONNECTED) {
            return false;
        }
        if ((path == null) || (mask == null)) {
            return false;
        }
        try {
            FtpEntry[] lista = listFiles(path, mask, upcase);
            boolean sit = true;
            for (int i = 0; i <= (lista.length - 1); i++) {
                ftp.issueCommand("DELE " + lista[i]);
                int resp = ftp.lastReplyCode;
                if (resp != 250) {
                    sit = false;
                }
            }
            return sit;
        } catch (IOException err) {
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param path DOCUMENT ME!
     * @param mask DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean delete(String path, String mask) {
        return delete(path, mask, false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param remotesource DOCUMENT ME!
     * @param mask DOCUMENT ME!
     * @param remotetarget DOCUMENT ME!
     * @param upcase DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean move(String remotesource, String mask, String remotetarget,
        boolean upcase) {
        String remoteorig = remotesource;
        String remotedest = remotetarget;
        if (status != CONNECTED) {
            return false;
        }
        if ((remoteorig == null) || (mask == null) || (remotedest == null)) {
            return false;
        }
        remoteorig = StringA.change(remoteorig, "\\", "/");
        remotedest = StringA.change(remotedest, "\\", "/");
        if (!remotedest.endsWith("/")) {
            remotedest = remotedest + '/';
        }
        try {
            FtpEntry[] lista = listFiles(remoteorig, mask, upcase);
            boolean sit = true;
            for (int i = 0; i < lista.length; i++) {
                ftp.cd(remoteorig);
                ftp.sendServer("RNFR " + lista[i] + "\r\n");
                int resp = ftp.readServerResponse();
                if (resp == 350) {
                    ftp.sendServer("RNTO " + remotedest + lista[i] + "\r\n");
                    resp = ftp.readServerResponse();
                    if (resp != 250) {
                        sit = false;
                    }
                } else {
                    sit = false;
                }
            }
            return sit;
        } catch (IOException err) {
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param remotesource DOCUMENT ME!
     * @param mask DOCUMENT ME!
     * @param remotetarget DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean move(String remotesource, String mask, String remotetarget) {
        return move(remotesource, mask, remotetarget, false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param remotepath DOCUMENT ME!
     * @param source DOCUMENT ME!
     * @param target DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean rename(String remotepath, String source, String target) {
        String orig = source;
        String dest = target;
        if (status != CONNECTED) {
            return false;
        }
        if ((remotepath == null) || (orig == null) || (dest == null)) {
            return false;
        }
        remotepath = StringA.change(remotepath, "\\", "/");
        if (remotepath.equals("")) {
            remotepath = "/";
        }
        try {
            boolean sit = false;
            ftp.cd(remotepath);
            ftp.sendServer("RNFR " + orig + "\r\n");
            int resp = ftp.readServerResponse();
            if (resp == 350) {
                ftp.sendServer("RNTO " + dest + "\r\n");
                resp = ftp.readServerResponse();
                if (resp == 250) {
                    sit = true;
                }
            }
            return sit;
        } catch (IOException err) {
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param fullpath DOCUMENT ME!
     * @param recursive DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean mkdir(String fullpath, boolean recursive) {
        if (!recursive) {
            return mkdir(fullpath);
        }
        if ((status != CONNECTED) || (fullpath == null)) {
            return false;
        }
        fullpath = StringA.change(fullpath, "\\", "/");
        if (!fullpath.startsWith("/")) {
            fullpath = "/" + fullpath;
        }
        if (!fullpath.endsWith("/")) {
            fullpath = fullpath + "/";
        }
        int cnt = StringA.count(fullpath, '/');
        boolean ret = false;
        for (int i = 1; i < cnt; i++) {
            String pie = StringA.piece(fullpath, "/", 1, i + 1);
            if (pie.trim().equals("")) {
                continue;
            }
            ret = mkdir(pie);
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param fullpath DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean mkdir(String fullpath) {
        if ((status != CONNECTED) || (fullpath == null)) {
            return false;
        }
        fullpath = StringA.change(fullpath, "\\", "/");
        if (fullpath.endsWith("/")) {
            fullpath = StringA.mid(fullpath, 0, fullpath.length() - 2);
        }
        try {
            int slash = fullpath.lastIndexOf("/", fullpath.length());
            String parent = StringA.mid(fullpath, 0, slash - 1).trim();
            if (parent.equals("")) {
                parent = "/";
            }
            String newdir =
                StringA.mid(fullpath, slash + 1, fullpath.length() - 1);
            ftp.cd(parent);
            ftp.sendServer("MKD " + newdir.trim() + "\r\n");
            String resp = ftp.readServerResponse() + "";
            boolean sit = false;
            if (resp.startsWith("25")) {
                sit = true;
            }
            return sit;
        } catch (IOException err) {
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param fullpath DOCUMENT ME!
     * @param recursive DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean rmdir(String fullpath, boolean recursive) {
        if (!recursive) {
            return rmdir(fullpath);
        }
        if ((status != CONNECTED) || (fullpath == null)) {
            return false;
        }
        fullpath = StringA.change(fullpath, "\\", "/");
        if (!fullpath.startsWith("/")) {
            fullpath = "/" + fullpath;
        }
        if (!fullpath.endsWith("/")) {
            fullpath = fullpath + "/";
        }
        int cnt = StringA.count(fullpath, '/');
        boolean ret = false;
        for (int i = cnt + 1; i > 0; i--) {
            String pie = StringA.piece(fullpath, "/", 1, i);
            if (pie.trim().equals("")) {
                continue;
            }
            ret = rmdir(pie);
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param fullpath DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean rmdir(String fullpath) {
        if ((status != CONNECTED) || (fullpath == null)) {
            return false;
        }
        fullpath = StringA.change(fullpath, "\\", "/");
        if (fullpath.endsWith("/")) {
            fullpath = StringA.mid(fullpath, 0, fullpath.length() - 2);
        }
        try {
            int slash = fullpath.lastIndexOf("/", fullpath.length());
            String parent = StringA.mid(fullpath, 0, slash - 1).trim();
            if (parent.equals("")) {
                parent = "/";
            }
            String remdir =
                StringA.mid(fullpath, slash + 1, fullpath.length() - 1);
            ftp.cd(parent);
            ftp.sendServer("RMD " + remdir.trim() + "\r\n");
            String resp = ftp.readServerResponse() + "";
            boolean sit = false;
            if (resp.startsWith("25")) {
                sit = true;
            }
            return sit;
        } catch (IOException err) {
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param fullpath DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean existDir(String fullpath) {
        if ((status != CONNECTED) || (fullpath == null)) {
            return false;
        }
        fullpath = StringA.change(fullpath, "\\", "/");
        if (fullpath.endsWith("/")) {
            fullpath = StringA.mid(fullpath, 0, fullpath.length() - 2);
        }
        try {
            ftp.cd("/");
            ftp.sendServer("CWD " + fullpath.trim() + "\r\n");
            String resp = ftp.readServerResponse() + "";
            boolean sit = false;
            if ((resp.startsWith("2")) || (resp.startsWith("5"))) {
                sit = true;
            }
            return sit;
        } catch (IOException err) {
            return false;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param sourcepath DOCUMENT ME!
     * @param source DOCUMENT ME!
     * @param targetpath DOCUMENT ME!
     * @param target DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean get(String sourcepath, String source, String targetpath,
        String target) {
        if (status != CONNECTED) {
            return false;
        }
        if ((sourcepath == null) || (source == null) || (targetpath == null)
                    || (target == null)) {
            return false;
        }
        if ((source.equals("")) || (target.equals(""))) {
            return false;
        }
        sourcepath = StringA.change(sourcepath, "\\", "/");
        if (sourcepath.equals("")) {
            sourcepath = "/";
        }
        targetpath = StringA.change(targetpath, "\\", "/");
        if (!targetpath.endsWith("/")) {
            targetpath = targetpath + "/";
        }
        File fl = new File(targetpath);
        if (!fl.exists()) {
            return false;
        }
        fl = new File(targetpath + target);
        fl.delete();
        boolean sit = false;
        try {
            ftp.binary();
            ftp.cd(sourcepath);
            TelnetInputStream arqin = ftp.get(source, passive);
            FileOutputStream file = new FileOutputStream(targetpath + target);
            int tam = 0;
            byte[] bt = new byte[bsize];
            while ((tam = arqin.read(bt, 0, bsize)) > 0) {
                file.write(bt, 0, tam);
            }
            file.close();
            arqin.close();
            ftp.readReply();
            fl = new File(targetpath + target);
            if (fl.exists()) {
                sit = true;
            }
        } catch (IOException err) {
        }
        return sit;
    }

    /**
     * DOCUMENT ME!
     *
     * @param remotedir DOCUMENT ME!
     * @param mask DOCUMENT ME!
     * @param localdir DOCUMENT ME!
     * @param upcase DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int get(String remotedir, String mask, String localdir,
        boolean upcase) {
        if (status != CONNECTED) {
            return -1;
        }
        if ((remotedir == null) || (mask == null) || (localdir == null)) {
            return -1;
        }
        localdir = StringA.change(localdir, "\\", "/");
        if (!localdir.endsWith("/")) {
            localdir = localdir + "/";
        }
        File fl = new File(localdir);
        if (!fl.exists()) {
            return -1;
        }
        if (remotedir.equals("")) {
            remotedir = "/";
        }
        FtpEntry[] lista = listFiles(remotedir, mask, upcase);
        int resp = 0;
        for (int i = 0; i < lista.length; i++) {
            String name = lista[i].getName();
            if (get(remotedir, name, localdir, name)) {
                resp = resp + 1;
            }
        }
        return resp;
    }

    /**
     * DOCUMENT ME!
     *
     * @param remotedir DOCUMENT ME!
     * @param mask DOCUMENT ME!
     * @param localdir DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int get(String remotedir, String mask, String localdir) {
        return get(remotedir, mask, localdir, false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param sourcepath DOCUMENT ME!
     * @param source DOCUMENT ME!
     * @param targetpath DOCUMENT ME!
     * @param target DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean send(String sourcepath, String source, String targetpath,
        String target) {
        if (status != CONNECTED) {
            return false;
        }
        if ((sourcepath == null) || (source == null) || (targetpath == null)
                    || (target == null)) {
            return false;
        }
        if ((source.equals("")) || (target.equals(""))) {
            return false;
        }
        sourcepath = StringA.change(sourcepath, "\\", "/");
        if (!sourcepath.endsWith("/")) {
            sourcepath = sourcepath + "/";
        }
        targetpath = StringA.change(targetpath, "\\", "/");
        if (targetpath.equals("")) {
            targetpath = "/";
        }
        File fl = new File(sourcepath + source);
        if ((!fl.exists()) || (fl.isDirectory())) {
            return false;
        }
        boolean sit = false;
        try {
            ftp.binary();
            ftp.cd(targetpath);
            TelnetOutputStream arqout = ftp.put(target, passive);
            FileInputStream file = new FileInputStream(sourcepath + source);
            int size = file.available();
            int total = 0;
            while (total < size) {
                byte[] bt = new byte[bsize];
                int tam = size - total;
                if (tam > bsize) {
                    tam = bsize;
                }
                file.read(bt, 0, tam);
                total = total + tam;
                arqout.write(bt, 0, tam);
                arqout.flush();
            }
            file.close();
            arqout.close();
            ftp.readReply();
            sit = true;
        } catch (IOException err) {
        }
        return sit;
    }

    /**
     * DOCUMENT ME!
     *
     * @param localdir DOCUMENT ME!
     * @param mask DOCUMENT ME!
     * @param remotedir DOCUMENT ME!
     * @param upcase DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int send(String localdir, String mask, String remotedir,
        boolean upcase) {
        if (status != CONNECTED) {
            return -1;
        }
        if ((remotedir == null) || (mask == null) || (localdir == null)) {
            return -1;
        }
        localdir = StringA.change(localdir, "\\", "/");
        if (!localdir.endsWith("/")) {
            localdir = localdir + "/";
        }
        File fl = new File(localdir);
        if (!fl.exists()) {
            return -1;
        }
        if (remotedir.equals("")) {
            remotedir = "/";
        }
        String[] lista = fl.list();
        if (lista == null) {
            lista = new String[0];
        }
        int resp = 0;
        for (int i = 0; i < lista.length; i++) {
            fl = new File(localdir + lista[i]);
            if (!fl.isDirectory()) {
                String lst = lista[i];
                String msk = mask;
                if (upcase) {
                    lst = lst.toUpperCase();
                    msk = msk.toUpperCase();
                }
                if (Function.validateMask(lst, msk, true)) {
                    if (send(localdir, lista[i], remotedir, lista[i])) {
                        resp = resp + 1;
                    }
                }
            }
        }
        return resp;
    }

    /**
     * DOCUMENT ME!
     *
     * @param localdir DOCUMENT ME!
     * @param mask DOCUMENT ME!
     * @param remotedir DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int send(String localdir, String mask, String remotedir) {
        return send(localdir, mask, remotedir, false);
    }

    private String asLine(byte[] bt, int tam) {
        if (bt == null) {
            return "";
        }
        StringBuffer resp = new StringBuffer();
        for (int i = 0; i < tam; i++) {
            char let = (char) bt[i];
            resp.append(let);
        }
        return resp.toString();
    }

    private FtpEntry[] add(FtpEntry[] array, FtpEntry item) {
        if (array == null) {
            array = new FtpEntry[0];
        }
        if (item == null) {
            return array;
        }
        FtpEntry[] aux = new FtpEntry[array.length + 1];
        for (int i = 0; i < array.length; i++) {
            aux[i] = array[i];
        }
        aux[array.length] = item;
        return aux;
    }

    /**
     * DOCUMENT ME!
     */
    public void close() {
        try {
            if (status == CONNECTED) {
                ftp.closeServer();
            }
        } catch (IOException err) {
        }
        status = UNKNOWN;
    }
}
