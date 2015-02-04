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
 * @version $Revision: 1.1 $
 */
public class SmtpMessageThread extends Thread {
    private static final int retries = 6;
    private static final int waitminutes = 30; // minutes
	private static boolean smtpStart = true;
	private static final Map smtpPool = 
    	Collections.synchronizedMap(new HashMap());

    /**
     * DOCUMENT ME!
     */
    public static synchronized void execute(String projId) {
        if (smtpStart) {
        	smtpStart = false;
            new SmtpMessageThread(projId).start();
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
