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

package br.com.webinside.runtime.core;

import java.util.HashSet;
import java.util.Iterator;
import java.util.StringTokenizer;

import br.com.webinside.runtime.component.KillElement;
import br.com.webinside.runtime.integration.IntFunction;
import br.com.webinside.runtime.integration.Producer;
import br.com.webinside.runtime.util.StringA;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.3 $
 */
public class CoreKillElement extends CoreCommon {
    private KillElement kill;

    /**
     * Creates a new CoreKill object.
     *
     * @param wiParams DOCUMENT ME!
     * @param kill DOCUMENT ME!
     */
    public CoreKillElement(ExecuteParams wiParams, KillElement kill) {
        this.wiParams = wiParams;
        this.kill = kill;
        element = kill;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        if (!isValidCondition()) {
            return;
        }
        boolean loginOk = wiMap.get("pvt.login.accept").equals("true");
        String wiobjects = kill.getWIObj().trim();
        StringTokenizer tks = new StringTokenizer(wiobjects, ",");
        while (tks.hasMoreTokens()) {
            String wiobj = tks.nextToken().trim();
            if (wiobj.toLowerCase().startsWith("wi.")) {
                if (wiobj.equalsIgnoreCase("wi.session.id")) {
                    if (!wiParams.getWISession().hasAttributeWithPrefix("bld-")) {
                    	wiParams.getWIMap().put("wi.session.id", "invalidate");
                    }
                }
                continue;
            }
            if (wiobj.indexOf("*") > -1) {
                if (wiobj.equals("*.*")) {
                    remove(1);
                } else if (wiobj.startsWith("*")) {
                    remove(2);
                } else {
                    String objkey = StringA.piece(wiobj, "*", 1).trim();
                    if (objkey.endsWith(".")) {
                        IntFunction.killObjAndVector(wiMap, objkey);
                    }
                }
            } else {
            	wiobj = Producer.execute(wiMap, wiobj);
                IntFunction.killObjAndVector(wiMap, wiobj);
            }
        }
        if (loginOk) {
        	wiMap.put("pvt.login.accept", "true");
        }
        writeLog();
    }

    // 1 = Remove *.*
    // 2 = Remove * (*pes representa o mesmo que *)
    private void remove(int type) {
        Iterator it = new HashSet(wiMap.getInternalMap().keySet()).iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (!key.startsWith("wi.")) {
	            if (type == 1) {
	                wiMap.remove(key);
	            } else {
	                if (type == 2) {
	                    if (!key.endsWith(".")) {
	                        wiMap.remove(key);
	                    }
	                }
	            }
            }    
        }
    }
}
