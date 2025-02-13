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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.InflaterInputStream;

import javax.servlet.http.HttpServletResponse;

import br.com.webinside.runtime.component.AbstractDownload;
import br.com.webinside.runtime.component.DownloadDatabase;
import br.com.webinside.runtime.component.DownloadFtp;
import br.com.webinside.runtime.component.DownloadLocal;
import br.com.webinside.runtime.component.DownloadRef;
import br.com.webinside.runtime.component.Host;
import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.integration.DatabaseHandler;
import br.com.webinside.runtime.integration.Producer;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.net.ClientFtp;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.7 $
 */
public class CoreDownload extends CoreCommon {
    /** DOCUMENT ME! */
    private DownloadRef dowref;

    /**
     * Creates a new CoreDownload object.
     *
     * @param wiParams DOCUMENT ME!
     * @param dowref DOCUMENT ME!
     */
    public CoreDownload(ExecuteParams wiParams, DownloadRef dowref) {
        this.wiParams = wiParams;
        this.dowref = dowref;
        element = dowref;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        if (!isValidCondition()) {
            return;
        }
        if (!wiParams.getProject().getDownloads().containsKey(dowref.getId())) {
            wiParams.includeCode("/downloads/" + dowref.getId());
        }
        AbstractDownload dow =
            (AbstractDownload) wiParams.getProject().getDownloads().getElement(dowref
                        .getId());
        if (dow == null) {
            return;
        }
        if (dow instanceof DownloadLocal) {
            local((DownloadLocal) dow);
        } else if (dow instanceof DownloadFtp) {
            ftp((DownloadFtp) dow);
        } else if (dow instanceof DownloadDatabase) {
            database((DownloadDatabase) dow);
        }
        writeLog();
    }

    private void local(DownloadLocal dow) {
    	String dir = Producer.execute(wiMap, dow.getDirectory());
        dir = StringA.changeChars(dir, "\\", "/");
        if (!dir.endsWith("/")) {
            dir = dir + "/";
        }
        if ((!new File(dir).exists()) || (!new File(dir).isDirectory())) {
            String msg = "Directory not found (" + dir + ")";
            String jspFile = wiMap.get("wi.jsp.filename");
            wiParams.getErrorLog().write(jspFile, dow.getDescription(), msg);
            if (!wiParams.getPage().getErrorPageName().equals("")) {
                Exception ex = new FileNotFoundException(msg);
                wiParams.setRequestAttribute("wiException", ex);
            }
            return;
        }
        String file = Producer.execute(wiMap, dow.getFile());
        String realname = Producer.execute(wiMap, dow.getRealname());
        localCore(dow, wiMap, dir, file, realname);
    }

    private void localCore(AbstractDownload dow, WIMap auxcontext, String dir,
        String file, String realname) {
        if (!dir.endsWith("/")) {
            dir = dir + "/";
        }
        if (realname == null) {
            realname = "";
        }
        boolean active = false;
        if (dow.getActivePage().equals("ON")) {
            active = true;
        }
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(auxcontext);
        File fl = new File(dir, file);
        if ((fl.exists()) && (fl.isFile()) && (fl.length() > 0)) {
            prod.setInput(dow.getMime());
            wiParams.getProducer().setParam(prod);
            wiParams.getProducer().execute();
            String forcedMime = prod.getOutput();
            String mime = MimeType.get(forcedMime);
            if (mime.equals("")) {
                int last = realname.lastIndexOf(".");
                if (last == -1) {
                    last = realname.length();
                }
                mime = StringA.mid(realname, last + 1, realname.length());
                mime = MimeType.get(mime);
            }
            if (mime.equals("")) {
                mime = "application/octet-stream";
            }
            wiParams.setContentType(mime);
            HttpServletResponse response = wiParams.getHttpResponse();
            if (response != null) {
	            if (!realname.trim().equals("")) {
	            	String dispname = "inline; filename=\""; 
	            	if (forcedMime.trim().equalsIgnoreCase("attachment")) {
	                    dispname = "attachment; filename=\"";            		
	            	}
	            	dispname += StringA.getUsAscii(realname) + "\"";
	                response.setHeader("Content-disposition", dispname);
	            }
	            if (!active) {
	            	response.setContentLength((int) fl.length());
	            }
	            try  {
	                response.flushBuffer();
	            } catch (IOException err) {}  
            }
            new RtmExport(wiParams).sendFile(wiMap, fl.getAbsolutePath(), active);
            wiParams.setRequestAttribute("wiExit", "true");            
        } else {
            sendNoFile(dow.getNoFile(), active);
        }
    }

    private void ftp(DownloadFtp dow) {
        String remotedir = Producer.execute(wiMap, dow.getDirectory());
        remotedir = StringA.changeChars(remotedir, "\\", "/");
        String file = Producer.execute(wiMap, dow.getFile()).trim();
        Host host = wiParams.getProject().getHosts().getHost(dow.getHostId());
        ClientFtp ftp = null;
        boolean ok = true;
        if ((host == null) || (!host.getProtocol().equals("FTP"))) {
            ok = false;
        } else {
            ftp = new ClientFtp(host.getAddress(), host.getUser(),
                    host.getPass());
            if (!ftp.isConnected()) {
                RtmFunction.hostError(wiParams, dow.getHostId());
                return;
            }
        }
        if (ok) {
            String tmpfull = Function.rndTmpFile("dow", "tmp");
            int last = tmpfull.lastIndexOf("/");
            String tmpdir = StringA.mid(tmpfull, 0, last - 1);
            String tmpfile = StringA.mid(tmpfull, last + 1, tmpfull.length());
            ftp.get(remotedir, file, tmpdir, tmpfile);
            ftp.close();
            localCore(dow, wiMap, tmpdir, tmpfile, file);
            new File(tmpfull).delete();
        }
    }

    private void database(DownloadDatabase dow) {
        ProducerParam prod = new ProducerParam();
        WIMap auxcontext = wiMap.cloneMe();
        prod.setWIMap(auxcontext);
        String dbalias = dow.getDatabase();
        DatabaseHandler db = wiParams.getDatabaseAliases().get(dbalias);
        if ((db == null) || (!db.isConnected())) {
            RtmFunction.databaseError(wiParams, dbalias);
            return;
        }
        db.setCharFilter(RtmFunction.cleanSpace(dow.getSqlFilter()), "");
        Exception exrs = null;
        ResultSet rs = null;
    	auxcontext.put("wi.download", "true");
        try {
            rs = db.execute(dow.getSql(), auxcontext);
        } catch (Exception err) {
            exrs = err;
        }
		wiMap.remove("wi.download");
        String mytemp = Function.rndTmpFile("dow", "tmp");
        try {
            if ((rs != null) && (rs.next() > 0)) {
                FileOutputStream myout = new FileOutputStream(mytemp);
               	rs.columnBin(myout, 1);
                myout.close();
                String[] names = rs.columnNames();
                for (int i = 1; i < names.length; i++) {
                    String value = rs.column(i + 1);
                    auxcontext.put(names[i], value);
                    //deprecated = auxcontext.put((i + 1) + "", value);
                }
                wiParams.getDatabaseAliases().closeAll();
                if (dow.isBase64()) {
                    String mytemp2 = Function.rndTmpFile("dow", "tmp");
                    FileInputStream in = new FileInputStream(mytemp);
                    FileOutputStream out = new FileOutputStream(mytemp2);
                    Function.copyStream(Base64.getDecoder().wrap(in), out);
                    out.close();
                    in.close();
                	new File(mytemp).delete();
                	mytemp = mytemp2;
                }
                if (dow.isZip()) {
                    String mytemp2 = Function.rndTmpFile("dow", "tmp");
                    InflaterInputStream in = 
                    	new InflaterInputStream(new FileInputStream(mytemp));
                    FileOutputStream out = new FileOutputStream(mytemp2);
                    Function.copyStream(in, out);
                    Function.closeStream(in);
                    Function.closeStream(out);
                	new File(mytemp).delete();
                	mytemp = mytemp2;
                }
            } else if (rs == null) {
            	queryException(exrs, db, dow.getDescription());
            	if (wiParams.getRequestAttribute("wiException") != null) {
                	return;
            	}
            }
        } catch (IOException err) {
        	err.printStackTrace();
        	// ignorado
        }
        prod.setInput(dow.getField());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String realname = prod.getOutput().trim();
        int last = mytemp.lastIndexOf("/");
        String tmpdir = StringA.mid(mytemp, 0, last - 1);
        String tmpfile = StringA.mid(mytemp, last + 1, mytemp.length());
        localCore(dow, auxcontext, tmpdir, tmpfile, realname);
        new File(mytemp).delete();
    }

    private void sendNoFile(String nofile, boolean exec) {
        if (nofile.trim().equals("")) return;
        String pnofile = Producer.execute(wiMap, nofile.trim());
        pnofile = StringA.changeChars(pnofile, "\\", "/");
        int fim = pnofile.lastIndexOf(".");
        String ext = StringA.mid(pnofile, fim + 1, pnofile.length() - 1);
        String mime = MimeType.get(ext);
        if (!MimeType.getType(ext).toLowerCase().equals("text")) {
            exec = false;
        }
        if (mime.equals("")) {
            mime = "application/octet-stream";
        }
        wiParams.setContentType(mime);
        fim = pnofile.lastIndexOf("/");
        String filename = "inline; filename=\""
            + StringA.mid(pnofile, fim + 1, pnofile.length() - 1) + "\"";
        File enviar = new File(pnofile);
        HttpServletResponse response = wiParams.getHttpResponse();
        if (response != null) {
            response.setHeader("Content-disposition", filename);
            response.setContentLength((int) enviar.length());
            try {
                response.flushBuffer();
            } catch (IOException err) {}  
        }
        new RtmExport(wiParams).sendFile(wiMap, enviar.getAbsolutePath(), exec);
        wiParams.setRequestAttribute("wiExit", "true");        
    }
}
