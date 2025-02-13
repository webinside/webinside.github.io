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

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import br.com.webinside.runtime.component.ListElement;
import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.integration.DatabaseHandler;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class CoreListElement extends CoreCommon {
    /** DOCUMENT ME! */
    private static final String nullkey = "internal_null_list";
    private ListElement list;

    /**
     * Creates a new CoreListElement object.
     *
     * @param wiParams DOCUMENT ME!
     * @param list DOCUMENT ME!
     */
    public CoreListElement(ExecuteParams wiParams, ListElement list) {
        this.wiParams = wiParams;
        this.list = list;
        element = list;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        if (!isValidCondition()) {
            return;
        }
        String dbalias = list.getDatabase();
        DatabaseHandler db = wiParams.getDatabaseAliases().get(dbalias);
        if ((db == null) || (!db.isConnected())) {
            RtmFunction.databaseError(wiParams, dbalias);
            return;
        }
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        db.setCharFilter(RtmFunction.cleanSpace(list.getSqlFilter()), "");
        long ini = new Date().getTime();
        Exception exrs = null;
        ResultSet rs = null;
        try {
            rs = db.execute(list.getSql(), wiMap);
        } catch (Exception err) {
            exrs = err;
        }
        long fim = new Date().getTime();
        dbTime(db, wiMap, ini, fim, "list " + list.getDescription());
        if (rs != null) {
            Map datacols = new HashMap();
            int countpos = rs.next();
            while (countpos > 0) {
                WIMap auxhash = wiMap.cloneMe();
                auxhash.putAll(rs.columns(""));
                auxhash.put("rowid", countpos);
                auxhash.put("rowid0", countpos - 1);
                prod.setWIMap(auxhash);
                String[] names = rs.columnNames();
                for (int i = 0; i <= names.length; i++) {
                    String name = nullkey;
                    String item = "";
                    if (i != names.length) {
                        name = names[i].trim();
                        item = rs.column(i + 1);
                    }
                    prod.setInput(list.getPre());
                    wiParams.getProducer().setParam(prod);
                    wiParams.getProducer().execute();
                    String tkpre = StringA.showLineBreak(prod.getOutput());
                    prod.setInput(list.getPos());
                    wiParams.getProducer().setParam(prod);
                    wiParams.getProducer().execute();
                    String tkpos = StringA.showLineBreak(prod.getOutput());
                    StringBuffer reg = new StringBuffer();
                    reg.append(tkpre).append(item).append(tkpos);
                    prod.setInput(list.getSep());
                    wiParams.getProducer().setParam(prod);
                    wiParams.getProducer().execute();
                    String tksep = StringA.showLineBreak(prod.getOutput());
                    if (countpos > 1) {
                      	StringBuffer before = (StringBuffer) datacols.get(name);
                      	if (before != null) {
                      		before.append(tksep).append(reg);
                      		reg = before;	
                      	}
                    }
                    datacols.put(name, reg);
                }
                countpos = rs.next();
            }
            String wiobj = list.getWIObj().trim();
            Iterator it = datacols.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                if (!key.toLowerCase().equals(nullkey)) {
                    StringBuffer value = (StringBuffer) datacols.get(key);
                    wiMap.put(wiobj + "." + key, value.toString());
                }
            }
            String check = list.getPre() + list.getPos() + list.getSep();
            if (check.indexOf("|") != -1) {
				StringBuffer value = (StringBuffer) datacols.get(nullkey);
				if (value != null) {
					wiMap.put(wiobj, value.toString());
				}
			}
        } else {
        	RtmFunction.invalidateTransaction(wiMap, db.getErrorMessage());
        	queryException(exrs, db, list.getDescription());
        }
        writeLog();
    }
}
