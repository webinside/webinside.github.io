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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.simple.JSONValue;

import br.com.webinside.runtime.component.ObjectElement;
import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.integration.DatabaseHandler;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.5 $
 */
public class CoreObjectElement extends CoreCommon {
    private ObjectElement object;
    private String prefix = "";
    private String prefixPos = "";

    /**
     * Creates a new CoreObjectElement object.
     *
     * @param wiParams DOCUMENT ME!
     * @param object DOCUMENT ME!
     */
    public CoreObjectElement(ExecuteParams wiParams, ObjectElement object) {
        this.wiParams = wiParams;
        this.object = object;
        element = object;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        if (!isValidCondition()) {
            return;
        }
        String wiobj = object.getWIObj().trim();
        if (wiobj.toLowerCase().startsWith("wi.")) {
            return;
        }
        if (wiobj.endsWith(".")) {
            wiobj = StringA.mid(wiobj, 0, wiobj.length() - 2);
        }
        String dbalias = object.getDatabase();
        DatabaseHandler db = wiParams.getDatabaseAliases().get(dbalias);
        if ((db == null) || (!db.isConnected())) {
            EngFunction.databaseError(wiParams, dbalias);
            return;
        }
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        // usado quando vem de um update
        WIMap auxhash = wiMap.cloneMe();
        String subPrefix = StringA.changeChars(prefix, "[]", "");
        if (!prefixPos.equals("")) {
            auxhash.put(subPrefix + "[" + prefixPos + "].index()", prefixPos);
        }
        db.setCharFilter(EngFunction.cleanSpace(object.getSqlFilter()), "");
        Exception exrs = null;
        long ini = new Date().getTime();
        ResultSet rs = null;
        try {
            rs = db.execute(changePrefix(), auxhash);
        } catch (Exception err) {
            exrs = err;
        }
        long fim = new Date().getTime();
        dbTime(db, auxhash, ini, fim, "object " + object.getDescription());
        if (rs != null) {
            StringBuffer cnames = new StringBuffer();
            String[] names = rs.columnNames();
            for (int i = 0; i < names.length; i++) {
                wiMap.remove(wiobj + "." + names[i]);
                //deprecated = wiMap.remove(wiobj + "." + (i + 1));
                if (cnames.length() > 0) {
                    cnames.append(",");
                }
                cnames.append(names[i]);
            }
        	wiMap.put(wiobj + ".columnNames()", cnames.toString());
            int colIds = Function.parseInt(object.getColumnIDs().trim());
            if (colIds < 0) colIds = 0;
            wiMap.remove(wiobj + ".size()"); 
            Set keys = new HashSet(wiMap.getAsMap().keySet());
            for (Iterator i = keys.iterator(); i.hasNext();) {
				String key = (String) i.next();
				if (key.startsWith(wiobj + "[")) {
                    wiMap.remove(key);
                }
			}
            List jsonList = new ArrayList();
            int pos = 0;
            while ((pos = rs.next()) > 0) {
            	Map jsonMap = new LinkedHashMap();
                for (int i = 0; i < names.length; i++) {
                    String value = rs.column(i + 1);
                    if (pos == 1) {
	                    wiMap.put(wiobj + "." + names[i], value);
	                    //deprecated = wiMap.put(wiobj + "." + (i + 1), value);
                    }    
                    jsonMap.put(names[i], value);
                }
                if (object.isUsejson()) {
            		wiMap.put(wiobj + ".json", JSONValue.toJSONString(jsonMap));
                	jsonList.add(jsonMap);
                }
                if (!object.isMultiple()) break;
                if (colIds > 0) {
                	String key = "";
                    for (int i = 0; i < colIds; i++) {
                    	key += "." + rs.column(i + 1) ;
                    }
                    for (int i = colIds; i < names.length; i++) {
                        String value = rs.column(i + 1);
                        wiMap.put(wiobj + "." + names[i] + key , value);
                    }
                } else {
                    wiMap.put(wiobj + ".size()", pos);
                    for (int i = 0; i < names.length; i++) {
                        String key = wiobj + "[" + pos + "]."; 
                        String value = rs.column(i + 1);
                        wiMap.put(key + names[i], value);
                    }
                }    
            }
        	if (object.isUsejson()) {
        		if (object.isMultiple()) {
            		String jsonText = JSONValue.toJSONString(jsonList);
            		jsonText = StringA.change(jsonText, "},", "},\r\n");
            		wiMap.put(wiobj + ".json", jsonText);
        		} else if (jsonList.size() == 0) {
        			wiMap.put(wiobj + ".json", "null");
        		}	
        	}
        } else {
        	EngFunction.invalidateTransaction(wiMap, db.getErrorMessage());
        	queryException(exrs, db, object.getDescription());
        }
        writeLog();
    }

    /**
     * DOCUMENT ME!
     *
     * @param prefix DOCUMENT ME!
     * @param pos DOCUMENT ME!
     */
    public void setPrefix(String prefix, String pos) {
        if (prefix == null) {
            prefix = "";
        }
        this.prefix = prefix;
        if (pos == null) {
            pos = "";
        }
        this.prefixPos = pos;
    }

    private String changePrefix() {
        if (prefix.equals("")) {
            return object.getSql();
        }
        String aux = object.getSql();
        String result = "";
        if (prefix.endsWith("[]")) {
            String subprefix = StringA.mid(prefix, 0, prefix.length() - 2);
            result =
                StringA.change(aux, "|" + prefix + ".",
                    "|" + subprefix + prefixPos + "].", false);
        } else {
            result =
                StringA.change(aux, "|" + prefix + ".",
                    "|" + prefix + prefixPos + ".", false);
        }
        return result;
    }
}
