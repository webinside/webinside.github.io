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

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class SmtpMessageThread extends Thread {
	
	public static boolean verifyFolder = true;
    private static final int retries = 6;
    private static final int waitminutes = 30; // minutes
	private static boolean smtpStart = true;
	private static final Map smtpPool = 
    	Collections.synchronizedMap(new HashMap());

    /**
     * DOCUMENT ME!
     */
    public static synchronized void execute(String projId) {
        if (smtpStart != true) return;
    	if (verifyFolder) {
    		verifyFolder = false;    		
            String tmpdir = Function.tmpDir();
            String[] lista = Function.listDir(tmpdir, "smtp-*.tmp", false);
            if (lista.length > 0) {
             	smtpStart = false;
                new SmtpMessageThread(projId).start();
            }
        }
    }

	private SmtpMessageThread(String projId) {
	  super("WI-ClientSmtpThread-" + projId);
	}

    /**
     * DOCUMENT ME!
     */
    public void run() {
        try {
            boolean starting = true;
            while (true) {
                try {
                	if (!starting) {
                		sleep(waitminutes * 60000);
                	}
                	starting = false;
                } catch (InterruptedException err) {
                }
                String tmpdir = Function.tmpDir();
                String[] tmps = Function.listDir(tmpdir, "smtp-*.tmp", false);
                for (int i = 0; i < tmps.length; i++) {
                    File smtpDir = new File(tmpdir, tmps[i]);
                    boolean ok = sendmail(smtpDir);
                    if (ok || !smtpDir.isDirectory()) {
                    	smtpPool.remove(smtpDir.getAbsolutePath());
                    } else {
                        int qnt = 0;
                        try {
                            String sqnt =
                                (String) smtpPool.get(smtpDir.getAbsolutePath());
                            if (sqnt != null) {
                                qnt = Integer.parseInt(sqnt);
                            }
                        } catch (NumberFormatException err) { }
                        if (qnt < retries) {
                        	smtpPool.put(smtpDir.getAbsolutePath(), 
                        			(qnt + 1) + "");
                        } else {
                        	smtpPool.remove(smtpDir.getAbsolutePath());
                            String pref =
                                StringA.piece(smtpDir.toString(), ".", 1);
                            File newDir = new File(pref + ".old");
                            smtpDir.renameTo(newDir);
                            String msg =
                                "SMTP host not found in " + retries
                                + " retries. Email renamed to " + newDir.getName();
                            System.err.println(getClass().getName() + ": " + msg);
                        }
                    }
                }
            }
        } catch (Exception err) {
        	smtpStart = true;
        	verifyFolder = true;
        }
    }

    private boolean sendmail(File smtpDir) {
        // Sending the mail
    	SmtpMessage message = null;
    	try {
    		File obj = new File(smtpDir , "/message.obj");
	    	FileInputStream in = new FileInputStream(obj);
	    	ObjectInputStream s = new ObjectInputStream(in);
	    	message = (SmtpMessage)s.readObject();
	    	in.close();
    	} catch (Exception err) { 
    		return false;
    	}
		return 	message.send();
    }
}
