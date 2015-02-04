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
import java.io.InputStream;
import java.sql.SQLException;
import java.util.zip.DeflaterOutputStream;

import sun.misc.BASE64Encoder;
import br.com.webinside.runtime.component.AbstractUpload;
import br.com.webinside.runtime.component.Host;
import br.com.webinside.runtime.component.UploadDatabase;
import br.com.webinside.runtime.component.UploadFtp;
import br.com.webinside.runtime.component.UploadLocal;
import br.com.webinside.runtime.component.UploadRef;
import br.com.webinside.runtime.integration.DatabaseHandler;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.net.ClientFtp;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class CoreUpload extends CoreCommon {
    private UploadRef upref;

    /**
     * Creates a new CoreUpload object.
     *
     * @param wiParams DOCUMENT ME!
     * @param upref DOCUMENT ME!
     */
    public CoreUpload(ExecuteParams wiParams, UploadRef upref) {
        this.wiParams = wiParams;
        this.upref = upref;
        element = upref;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        if (!isValidCondition() || (wiParams.getFileUpload() == null)) {
            return;
        }
        if (!wiParams.getProject().getUploads().containsKey(upref.getId())) {
            wiParams.includeCode("/uploads/" + upref.getId());
        }
        AbstractUpload upl =
            (AbstractUpload) wiParams.getProject().getUploads().getElement(upref
                        .getId());
        if (upl == null) {
            return;
        }
        String field = upl.getFormField();
        if (field.toLowerCase().startsWith("tmp_")) {
        	field = "tmp." + StringA.mid(field, 4, field.length());
        	upl.setFormField(field);
        }
        if (upl instanceof UploadLocal) {
            local((UploadLocal) upl);
        } else if (upl instanceof UploadFtp) {
            ftp((UploadFtp) upl);
        } else if (upl instanceof UploadDatabase) {
            database((UploadDatabase) upl);
        }
        writeLog();
    }

    private void local(UploadLocal upl) {
        String field = upl.getFormField();
        String sourcefile = wiMap.get(field);
        int ldot = sourcefile.lastIndexOf(".");
        if (ldot == -1) {
            ldot = sourcefile.length();
        }
        String ext = StringA.mid(sourcefile, ldot + 1, sourcefile.length() - 1);
        wiMap.put("wi.upl.ext", ext);
        boolean create = false;
        if (upl.getCreateDir().equals("ON")) {
            create = true;
        }
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        prod.setInput(upl.getDirectory());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String dir = prod.getOutput();
        dir = StringA.changeChars(dir, "\\", "/");
        if (!dir.endsWith("/")) {
            dir = dir + "/";
        }
        wiMap.put("wi.upl.path", dir);
        if ((!new File(dir).exists()) && (!create)) {
            if (!wiParams.getPage().getErrorPageName().equals("")) {
                String msg = "Directory not found (" + dir + ")";
                String jspFile = wiMap.get("wi.jsp.filename");
                wiParams.getErrorLog().write(jspFile, upl.getDescription(), msg);
                Exception ex = new FileNotFoundException(msg);
                wiParams.setRequestAttribute("wiException", ex);
            }
            return;
        }
        prod.setInput(upl.getFile());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String file = prod.getOutput();
        wiMap.put("wi.upl.filename", file);
        new File(dir).mkdirs();
        boolean ok = wiParams.getFileUpload().saveFile(field, dir + file);
        wiMap.put("wi.upl.ok", ok + "");
    }

    private void ftp(UploadFtp upl) {
        String field = upl.getFormField();
        String sourcefile = wiMap.get(field);
        int ldot = sourcefile.lastIndexOf(".");
        if (ldot == -1) {
            ldot = sourcefile.length();
        }
        String ext = StringA.mid(sourcefile, ldot + 1, sourcefile.length() - 1);
        wiMap.put("wi.upl.ext", ext);
        boolean create = false;
        if (upl.getCreateDir().equals("ON")) {
            create = true;
        }
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        Host host = wiParams.getProject().getHosts().getHost(upl.getHostId());
        ClientFtp ftp = null;
        boolean ok = true;
        if (wiParams.getFileUpload().hasFile(field)) {
            if ((host == null) || (!host.getProtocol().equals("FTP"))) {
                ok = false;
            } else {
                ftp = new ClientFtp(host.getAddress(), host.getUser(),
                        host.getPass());
                if (!ftp.isConnected()) {
                    EngFunction.hostError(wiParams, upl.getHostId());
                    return;
                }
            }
        } else {
            ok = false;
        }
        if (ok) {
            prod.setInput(upl.getDirectory());
            wiParams.getProducer().setParam(prod);
            wiParams.getProducer().execute();
            String dir = prod.getOutput();
            dir = StringA.changeChars(dir, "\\", "/");
            if (!dir.endsWith("/")) {
                dir = dir + "/";
            }
            wiMap.put("wi.upl.path", dir);
            if (create) {
                ftp.mkdir(dir, true);
            }
            prod.setInput(upl.getFile());
            wiParams.getProducer().setParam(prod);
            wiParams.getProducer().execute();
            String file = prod.getOutput();
            wiMap.put("wi.upl.filename", file);
            if (!wiParams.getFileUpload().hasFile(field)) {
                wiMap.put("wi.upl.ok", "false");
                return;
            }
            String mytemp = Function.rndTmpFile("upl", "tmp");
            wiParams.getFileUpload().saveFile(field, mytemp);
            int last = mytemp.lastIndexOf("/");
            String tmpdir = StringA.mid(mytemp, 0, last - 1);
            String tmpfile = StringA.mid(mytemp, last + 1, mytemp.length());
            boolean resp = ftp.send(tmpdir, tmpfile, dir, file);
            ftp.close();
            new File(mytemp).delete();
            if (resp) {
                wiMap.put("wi.upl.ok", "true");
            } else {
                wiMap.put("wi.upl.ok", "false");
            }
        }
    }

    private void database(UploadDatabase upl) {
        String field = upl.getFormField();
        String sourcefile = wiMap.get(field);
        int ldot = sourcefile.lastIndexOf(".");
        if (ldot == -1) {
            ldot = sourcefile.length();
        }
        String ext = StringA.mid(sourcefile, ldot + 1, sourcefile.length() - 1);
        wiMap.put("wi.upl.ext", ext);
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        String dbalias = upl.getDatabase();
        DatabaseHandler db = wiParams.getDatabaseAliases().get(dbalias);
        if ((db == null) || (!db.isConnected())) {
            EngFunction.databaseError(wiParams, dbalias);
            return;
        }
        if (!wiParams.getFileUpload().hasFile(field)) {
            wiMap.put("wi.upl.ok", "false");
            return;
        }
        boolean delete = true;
        String mytemp = Function.rndTmpFile("upl", "tmp");
        wiParams.getFileUpload().saveFile(field, mytemp);
		wiMap.remove("wi.transaction.none");
        db.setCharFilter(EngFunction.cleanSpace(upl.getSqlFilter()), "");
        int result = -1;
        try {
        	InputStream myin = new FileInputStream(mytemp);
    		if (upl.isZip()) {
    	        String mytemp2 = Function.rndTmpFile("upl", "tmp");
    			DeflaterOutputStream zip = 
    				new DeflaterOutputStream(new FileOutputStream(mytemp2));
                byte[] trecho = new byte[10240];
                int quant = 0;
                while ((quant = myin.read(trecho)) > -1) {
                    zip.write(trecho, 0, quant);
                    zip.flush();
                }
                zip.close();
                myin.close();
            	new File(mytemp).delete();
            	mytemp = mytemp2;
    			myin = new FileInputStream(mytemp);
    		}	
            if (upl.isBase64()) {
    	        String mytemp2 = Function.rndTmpFile("upl", "tmp");
                FileOutputStream out = new FileOutputStream(mytemp2);
                new BASE64Encoder().encode(myin, out);
                out.close();
                myin.close();
            	new File(mytemp).delete();
            	mytemp = mytemp2;
    			myin = new FileInputStream(mytemp);
            }
            try {
            	result = db.executeUpdate(myin, upl.getSql(), wiMap);
            } catch (Exception err) {
                result = EngFunction.errorCodeSQL(err);
            }
            if (myin != null) {
    	        myin.close();
            }
        } catch (IOException err) {
        	// Erro desconsiderado.
        }
        if (delete) {
        	new File(mytemp).delete();
        }
        if (result < 0) {
            wiMap.put("wi.upl.ok", "false");
        	EngFunction.invalidateTransaction(wiMap, db.getErrorMessage());
            Exception ex = new SQLException(db.getErrorMessage());
        	queryException(ex, db, upl.getDescription());
            db.updateLog(wiMap, false);
        } else {
            wiMap.put("wi.upl.ok", "true");
        }
    }
        
}
