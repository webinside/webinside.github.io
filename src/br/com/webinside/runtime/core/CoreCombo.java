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

import br.com.webinside.runtime.component.Combo;
import br.com.webinside.runtime.component.ComboRef;
import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.function.sv.SVNode;
import br.com.webinside.runtime.integration.DatabaseHandler;
import br.com.webinside.runtime.integration.IntFunction;
import br.com.webinside.runtime.integration.Producer;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;
import br.com.webinside.runtime.util.WISession;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.4 $
 */
public class CoreCombo extends CoreCommon {
    private ComboRef comboref;
    private String cboselected;

    /**
     * Creates a new CoreCombo object.
     *
     * @param wiParams DOCUMENT ME!
     * @param comboref DOCUMENT ME!
     */
    public CoreCombo(ExecuteParams wiParams, ComboRef comboref) {
        this.wiParams = wiParams;
        this.comboref = comboref;
        element = comboref;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        if (!isValidCondition()) {
        	wiMap.put("combo." + comboref.getId(), "");
            return;
        }
        WIMap auxcontext = wiMap.cloneMe();
        String realid = StringA.piece(comboref.getId(), "?", 1);
        IntFunction.importParameters(auxcontext, comboref.getId());
        if (!wiParams.getProject().getCombos().containsKey(realid)) {
            wiParams.includeCode("/combos/" + realid);
        }
        Combo combo =
            (Combo) wiParams.getProject().getCombos().getElement(realid);
        if (combo == null) {
            return;
        }
        String newSelected = wiMap.get("combo." + combo.getId() + ".selected"); 
        if (!newSelected.equals("")) {
        	comboref.setSelected(newSelected);
        }
        String dbalias = combo.getDatabase();
        DatabaseHandler db = wiParams.getDatabaseAliases().get(dbalias);
        if ((db == null) || (!db.isConnected())) {
            EngFunction.databaseError(wiParams, dbalias);
            return;
        }
        db.setCharFilter(EngFunction.cleanSpace(combo.getSqlFilter()), "");
        long ini = new Date().getTime();
        Exception exrs = null;
        ResultSet rs = null;
        try {
            rs = db.execute(combo.getSql(), auxcontext);
        } catch (Exception err) {
            exrs = err;
        }
        long fim = new Date().getTime();
        dbTime(db, auxcontext, ini, fim, "combo " + combo.getDescription());
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(auxcontext);
        String wiobj = "combo." + comboref.getId();
        if (rs != null) {
            prod.setInput(comboref.getSelected().trim());
            wiParams.getProducer().setParam(prod);
            wiParams.getProducer().execute();
            cboselected = prod.getOutput().trim();
            int rowcount = rs.rowCount();
            int limit = 0;
            try {
                prod.setInput(combo.getLimit().trim());
                new Producer(prod).execute();
                limit = Integer.parseInt(prod.getOutput().trim());
                if (limit < 0) {
                    limit = 0;
                }
            } catch (NumberFormatException err) { }
            int count = 0;
            StringA fullcombo = new StringA();
            while (rs.next() > 0 && (limit == 0 || count < limit)) {
                WIMap auxhash = wiMap.cloneMe();
                auxhash.putAll(rs.columns("", true));
                fullcombo.append(generateOption(auxhash, combo));
                count ++;
            }
            String subid = comboref.getSubID().trim();
            if (!subid.equals("")) {
            	wiobj = wiobj + "." + subid;
            }
            wiMap.put(wiobj, fullcombo.trim());
            wiMap.put(wiobj + ".limit", limit);
            wiMap.put(wiobj + ".size", count);
            if (rowcount > -1) {
                wiMap.put(wiobj + ".rowcount", rowcount);
            }
        } else {
    	    wiMap.put(wiobj, "");
        	queryException(exrs, db, combo.getDescription());
        }
        writeLog();
    }

    private String generateOption(WIMap linehash, Combo combo) {
        ProducerParam prod = new ProducerParam();
        prod.setInput(comboref.getOptionParam());
        prod.setWIMap(linehash);
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        StringA aux = new StringA("<option " + prod.getOutput() + " ");
        prod.setInput(combo.getText());
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        String valoritem = prod.getOutput();
        String valorchave = new String();
        if (!combo.getKey().trim().equals("")) {
            prod.setInput(combo.getKey());
            wiParams.getProducer().setParam(prod);
            wiParams.getProducer().execute();
            valorchave = prod.getOutput().trim();
            String securevar = comboref.getSecureVar().trim();
            if (!securevar.equals("")) {
            	WISession session = wiParams.getWISession();
                if (!valorchave.equals("") && session.isValid()) {
                	SVNode svNode = IntFunction.getSVNode(session, securevar);
                	String skey = svNode.addValue(wiMap.get("wi.page.id"), valorchave);
                    aux.append("value=\"" + skey + "\"");
                }
            } else {
                aux.append("value=\"" + valorchave + "\"");
            }
        }
        if (!cboselected.equals("")) {
        	boolean selected = false;
        	String[] keys = cboselected.split(",");
        	for (int i = 0; i < keys.length && selected == false; i++) {
        		if (keys[i].trim().equals("")) continue;
                if (combo.getKey().trim().equals("")) {
                    if (valoritem.equalsIgnoreCase(keys[i].trim())) {
                    	selected = true;
                    }
                } else {
                    if (valorchave.equalsIgnoreCase(keys[i].trim())) {
                    	selected = true;
                    }
                }
			}
            if (selected) {
                aux.append(" selected");
            }
        }
        if (valoritem.equals("")) {
            valoritem = valorchave;
        }
        aux.append(">" + valoritem + "</option>\r\n");
        return aux.toString();
    }
}
