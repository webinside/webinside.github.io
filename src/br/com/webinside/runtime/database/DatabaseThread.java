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
 * @version $Revision: 1.3 $
 */
public class DatabaseThread extends Thread {

	protected static boolean databaseStart = true;
	public static String threadName;
	public static Date lastCycleTime;
	
	protected static final Map databasePool = 
    	Collections.synchronizedMap(new HashMap());
	
	public static synchronized void execute(String projId) {
	  if (databaseStart != true) return;
  	  databaseStart = false;
	  new DatabaseThread(projId).start();
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
    
    public static boolean isPoolEmpty() {
    	return databasePool.isEmpty();
    }
    
}
