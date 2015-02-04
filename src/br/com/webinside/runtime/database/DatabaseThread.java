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

package br.com.webinside.runtime.database;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class DatabaseThread extends Thread {

	protected static boolean databaseStart = true;
	public static String threadName;
	public static Date lastCycleTime;
	
	protected static final Map databasePool = 
    	Collections.synchronizedMap(new HashMap());

	
	public static synchronized void execute(String projId) {
	  if (databaseStart) {
	  	  databaseStart = false;
		  new DatabaseThread(projId).start();
	  }
	}

	private DatabaseThread(String projId) { 
	  super("WI-DatabaseThread-" + projId);
	}

    /**
     * DOCUMENT ME!
     */
    public void run() {
        try {
            while (true) {
				lastCycleTime = new Date();
                try {
                    int glbtime = 5; //minutes
                    sleep(1000 * 60 * glbtime);
                } catch (InterruptedException err) {
                	// ignorado
                }
				findConnections();
            }
        } catch (Exception error) {
        	databaseStart = true;
        }
    }

    private void findConnections() throws Exception {
        synchronized (databasePool) {
        	threadName = "thread - " + Thread.currentThread().getName();
        	Iterator it = databasePool.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry entry = (Entry) it.next();
                List connections = (List) entry.getValue();
                findExpired(connections);
            }
        }
    }

    private void findExpired(List connections) {
        if (connections != null) {
    		Iterator it = new ArrayList(connections).iterator();            
			while (it.hasNext()) {
	            DatabaseThreadNode node = (DatabaseThreadNode) it.next();
	            long now = new java.util.Date().getTime();
	            if (node.getMaxTime() < now) {
					connections.remove(node);
					if (!node.isInUse()) {
						node.getDatabaseConnection().close();
					}	
	            }
	        }
        }
    }
    
}
