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

import br.com.webinside.runtime.component.AbstractPageAction;
import br.com.webinside.runtime.component.ObjectElement;
import br.com.webinside.runtime.component.UpdateElement;
import br.com.webinside.runtime.integration.Condition;
import br.com.webinside.runtime.integration.DatabaseHandler;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.3 $
 */
public class CoreUpdateElement extends CoreCommon {
    private UpdateElement update;
    private AbstractPageAction parent;

    /**
     * Creates a new CoreUpdate object.
     *
     * @param wiParams DOCUMENT ME!
     * @param update DOCUMENT ME!
     */
    public CoreUpdateElement(ExecuteParams wiParams, UpdateElement update) {
        this.wiParams = wiParams;
        this.update = update;
        element = update;
    }

    /**
     * Creates a new CoreUpdate object.
     *
     * @param wiParams DOCUMENT ME!
     * @param update DOCUMENT ME!
     * @param parent DOCUMENT ME!
     */
    public CoreUpdateElement(ExecuteParams wiParams, UpdateElement update,
        AbstractPageAction parent) {
        this.wiParams = wiParams;
        this.update = update;
        this.parent = parent;
        element = update;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        if (!isValidCondition()) {
            return;
        }
        String wiobj = update.getWIObj().trim();
        if (wiobj.toLowerCase().startsWith("wi.")) {
            return;
        }
        String dbalias = update.getDatabase();
        DatabaseHandler db = wiParams.getDatabaseAliases().get(dbalias);
        if ((db == null) || (!db.isConnected())) {
            EngFunction.databaseError(wiParams, dbalias);
            return;
        }
        if (update.getPrefix().trim().equals("")) {
            unique(db);
        } else {
            multiple(db);
        }
        writeLog();
    }

    private void unique(DatabaseHandler db) {
        WIMap auxhash = callObject(update.getPrefix().trim(), "");
        if (wiParams.mustExit()) {
            return;
        }
        String aux = update.getSql();
        boolean autocommit = update.getAutoCommit().equals("ON");
        boolean transaction = true;
		wiMap.remove("wi.transaction.none");
        if (wiMap.get("wi.transaction.status").equals("")) {
        	transaction = false;
        	db.autocommit(autocommit);
        }
        boolean allok = true;
        int count = StringA.count(aux, ';');        
        int p1 = 1;
        int p2 = 1;
        while (p2 <= (count + 1)) {
        	String sqlpart = StringA.piece(aux, ";", p1, p2);                
            int ct1 = StringA.count(sqlpart, "|$", true);
            int ct2 = StringA.count(sqlpart, "$|", true);
            if (ct1 != ct2 && p2 < (count +1)) {
            	p2++;
            	continue;
            }            	
            if (!sqlpart.trim().equals("")) {
                uniqueCore(sqlpart, auxhash, db);
            }
            if (!db.getErrorMessage().trim().equals("")) {
                allok = false;
            }
            if (!allok && wiMap.get("wi.transaction.status").equals("true")) {
            	wiMap.put("wi.transaction.status", db.getErrorMessage());
            }
            if ((!autocommit || transaction) && (!allok)) {
                break;
            }
            p2++;
            p1 = p2;
        }
        if (!transaction) {
        	if (!autocommit) {
        		if (allok) db.commit();
        		else db.rollback();
        	}
        	db.autocommit(true);
        }	
    }

    private void uniqueCore(String sqlpart, WIMap auxhash, DatabaseHandler db) {
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(auxhash);
        db.setCharFilter(EngFunction.cleanSpace(update.getSqlFilter()), "");
        long ini = new Date().getTime();
        Exception ex = null;
        int result = 0;
        try {
            result = db.executeUpdate(sqlpart, auxhash);
        } catch (Exception err) {
        	if (!update.getNoException().equals("ON")) {
                result = EngFunction.errorCodeSQL(err);
                ex = err;
        	}
        }
        long fim = new Date().getTime();
        dbTime(db, auxhash, ini, fim, "update " + update.getDescription());
        String sqlmsg = db.getErrorMessage();
        auxhash.put("wi.sql.msg", StringA.piece(sqlmsg, ")", 2, 0).trim());
        String wiobj = update.getWIObj().trim();
        wiMap.put(wiobj + ".status()", result);
        if (result >= 0) {
            wiMap.put(wiobj + ".ok()", "true");
            prod.setInput(update.getMessageTrue());
            wiParams.getProducer().setParam(prod);
            wiParams.getProducer().execute();
            wiMap.put(wiobj, prod.getOutput());
        } else {
            wiMap.put(wiobj + ".ok()", "false");
            onErrorCore(wiobj, result, db, ex, auxhash);
            db.updateLog(wiMap, false);
        }
    }

    private void multiple(DatabaseHandler db) {
        String prefix = update.getPrefix().trim();
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        int size = 0;
        try {
            String txtsize = update.getSize();
            prod.setInput(txtsize);
            wiParams.getProducer().setParam(prod);
            wiParams.getProducer().execute();
            txtsize = prod.getOutput().trim();
            size = Function.parseInt(txtsize);
        	if (txtsize.equals("")) {
        		size = Function.getArraySize(wiMap, prefix);
        	}
        } catch (NumberFormatException err) { 
        	// ignorado
        }
        int globalstatus = -1; // 1 = tudo ok, 0 = false, -1 = todos empty
        String wiobj = update.getWIObj().trim();
        boolean autocommit = update.getAutoCommit().equals("ON");
        boolean transaction = true;
		wiMap.remove("wi.transaction.none");
        if (wiMap.get("wi.transaction.status").equals("")) {
        	transaction = false;
        	db.autocommit(autocommit);
        }  
        for (int i = 1; i <= size; i++) {
            wiMap.put(wiobj + ".size()", i);
            wiMap.put("wi.sql.query", "");
            wiMap.put("wi.sql.error", "");
            String rcond = changePrefix(update.getRowCondition(), prefix, i);
            if (!new Condition(wiMap, rcond).execute()) {
                continue;
            }
            if (globalstatus != 0) {
                globalstatus = 1;
            }
            WIMap auxhash = callObject(update.getPrefix().trim(), i + "");
            if (wiParams.mustExit()) {
                return;
            }
            String myaux = update.getSql();
            boolean allok = true;
            int count = StringA.count(myaux, ';');
            if (myaux.trim().endsWith(";")) {
            	count = count - 1;
            }
            int p1 = 1;
            int p2 = 1;
            while (p2 <= (count + 1)) {
                String sqlpart = StringA.piece(myaux, ";", p1, p2);                
                int ct1 = StringA.count(sqlpart, "|$", true);
                int ct2 = StringA.count(sqlpart, "$|", true);
                if (ct1 != ct2) {
                	p2++;
                	continue;
                }            	
                if (!sqlpart.trim().equals("")) {
                    allok =
                        multipleCore(prefix, wiobj, i, sqlpart, auxhash, db);
                }
                if (!db.getErrorMessage().trim().equals("")) {
                    allok = false;
                }
                if (!allok && wiMap.get("wi.transaction.status").equals("true")) {
                	wiMap.put("wi.transaction.status", db.getErrorMessage());
                }
                if ((!autocommit || transaction) && (!allok)) {
                    break;
                }
                p2++;
                p1 = p2;
            }
            if (!allok) {
                globalstatus = 0;
            }
            if (!transaction) {
            	if (!autocommit) {
            		if (allok) db.commit();
            		else db.rollback();
            	}
            }	
        }
        if (!transaction) {
        	db.autocommit(true);
        }
        if (globalstatus == 1) {
            prod.setInput(update.getMessageTrue());
            wiParams.getProducer().setParam(prod);
            wiParams.getProducer().execute();
            wiMap.put(wiobj, prod.getOutput());
            wiMap.put(wiobj + ".ok()", "true");
        } else if (globalstatus == 0) {
        	String msg = onErrorMessage(-1, wiMap);
            if (msg.equals("-1")) msg = "false";
            wiMap.put(wiobj, msg);
            wiMap.put(wiobj + ".ok()", "false");
        }
    }

    private boolean multipleCore(String prefix, String wiobj, int i,
        String sqlpart, WIMap auxhash, DatabaseHandler db) {
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(auxhash);
        String subprefix = StringA.changeChars(prefix, "[]", "");
        auxhash.put(subprefix + "[" + i + "].index()", i);
        String sqlpart2 = sqlpart;
        if (db.getDatabaseConnection().getType().equals("MJAVA")) {
            multipleMjava(auxhash, sqlpart, i);
        } else {
            sqlpart2 = changePrefix(sqlpart, prefix, i);
        }
        db.setCharFilter(EngFunction.cleanSpace(update.getSqlFilter()), "");
        long ini = new Date().getTime();
        Exception ex = null;
        int result = 0;
        try {
            result = db.executeUpdate(sqlpart2, auxhash);
        } catch (Exception err) {
        	if (!update.getNoException().equals("ON")) {
	            result = EngFunction.errorCodeSQL(err);
	            ex = err;
        	}    
        }
        long fim = new Date().getTime();
        dbTime(db, auxhash, ini, fim, "update " + update.getDescription());
        String sqlmsg = db.getErrorMessage();
        auxhash.put("wi.sql.msg", StringA.piece(sqlmsg, ")", 2, 0).trim());
        wiMap.put(wiobj + "[" + i + "].status()", result);
        if (result >= 0) {
            wiMap.put(wiobj + "[" + i + "].ok()", "true");
            prod.setInput(update.getMessageTrue());
            wiParams.getProducer().setParam(prod);
            wiParams.getProducer().execute();
            wiMap.put(wiobj + "[" + i + "].message", prod.getOutput());
            return true;
        } else {
            wiMap.put(wiobj + "[" + i + "].ok()", "false");
            onErrorCore(wiobj + "[" + i + "].message", result, db, ex, auxhash);
            db.updateLog(wiMap, false);
        }
        return false;
    }

    private WIMap callObject(String prefix, String pos) {
        String pageobj = update.getObjectID().trim();
        ObjectElement oe = null;
        if ((!pageobj.trim().equals("")) && (parent != null)) {
            try {
                oe = (ObjectElement) parent.getObjectByID(pageobj).cloneMe();
            } catch (NullPointerException err) { 
            	// ignored
            }
        }
        WIMap auxcontext = wiMap.cloneMe();
        WIMap origMap = wiParams.getWIMap();
        if (oe != null) {
            wiParams.setParameter(ExecuteParams.WI_MAP, auxcontext);
            oe.setCondition("true");
            CoreObjectElement cobj = new CoreObjectElement(wiParams, oe);
            cobj.setPrefix(prefix, pos);
            cobj.execute();
            wiParams.setParameter(ExecuteParams.WI_MAP, origMap);
        }
        return auxcontext;
    }

    private void onErrorCore(String wiobj, int result, DatabaseHandler db, 
    		Exception ex, WIMap auxhash) {
        String text = onErrorMessage(result, auxhash);
        wiMap.put("wi.sql.query", db.getExecutedSQL());
        wiMap.put("wi.sql.error", db.getErrorMessage());
        wiMap.put(wiobj, text);
        String jspFile = wiMap.get("wi.jsp.filename");
        String description = update.getDescription();
        String msgDetail = db.getErrorMessage() + 
        	"\r\n--- SQL ---\r\n" + db.getExecutedSQL();
        WIMap psMap = db.getExecutedSQLParams(auxhash); 
        if (psMap.keySet().size() > 0) {
        	msgDetail += "\r\n--- PARAMS ---\r\n";
        	msgDetail += psMap.toString();
        }	
        wiParams.getErrorLog().write("Page: " + jspFile, description, msgDetail);
        if (!wiParams.getPage().getErrorPageName().equals("")) {
            wiParams.setRequestAttribute("wiException", ex);
        }
    }

    private String onErrorMessage(int result, WIMap context) {
        if (result > 0) {
            result = result * (-1);
        }
        String text = update.getMessageFalse(result + "");
        if (text.trim().equals("")) {
            ProducerParam prod = new ProducerParam();
            prod.setWIMap(context);
            prod.setInput(update.getNoMessage());
            wiParams.getProducer().setParam(prod);
            wiParams.getProducer().execute();
            text = prod.getOutput();
        }
        if (text.trim().equals("")) {
            text = result + "";
        }
        return text;
    }

    private String changePrefix(String text, String prefix, int pos) {
        if (prefix == null) {
            prefix = "";
        }
        String result = "";
        if (prefix.endsWith("[]")) {
            String subprefix = StringA.mid(prefix, 0, prefix.length() - 2);
            result =
                StringA.change(text, "|" + prefix, "|" + subprefix + pos + "]",
                    false);
        } else {
            result =
                StringA.change(text, "|" + prefix + ".",
                    "|" + prefix + pos + ".", false);
        }
        return result;
    }

    private void multipleMjava(WIMap auxhash, String sql, int seq) {
        int from = 0;
        int pos = 0;
        while ((pos = sql.indexOf("[]", from)) > -1) {
            int ini = sql.lastIndexOf("|", pos);
            int fim = sql.indexOf("|", pos);
            if ((ini > -1) && (fim > -1)) {
                String key = StringA.mid(sql, ini + 1, fim - 1);
                String nKey = StringA.change(key, "[]", "[" + seq + "]");
                String value = auxhash.get(nKey);
                auxhash.put(key, value);
            }
            from = pos + 2;
        }
    }
    
}
