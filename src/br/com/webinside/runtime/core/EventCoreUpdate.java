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

import java.io.IOException;
import br.com.webinside.runtime.component.*;
import br.com.webinside.runtime.integration.*;
import br.com.webinside.runtime.util.*;

//TODO - Logar sempre que der erro e tentar passar a mensagem de erro para aparecer no debug do WIEvent

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class EventCoreUpdate {
    private ExecuteParams wiParams;
    private EventUpdate update;

    /**
     * Creates a new EventCoreUpdate object.
     *
     * @param param DOCUMENT ME!
     * @param update DOCUMENT ME!
     */
    public EventCoreUpdate(ExecuteParams param, EventUpdate update) {
        this.wiParams = param;
        this.update = update;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int execute() {
        String wiobj = update.getWIObj().trim();
        if (wiobj.toLowerCase().startsWith("wi.")) {
            return 0;
        }
        if (wiobj.toLowerCase().startsWith("grid.")) {
            return 0;
        }
        if (wiobj.toLowerCase().startsWith("combo.")) {
            return 0;
        }
        String cond = update.getCondition();
        if (!new Condition(wiParams.getWIMap(), cond).execute()) {
            try {
                wiParams.getHttpResponse().sendError(601);
            } catch (IOException e) {
            	// ignorado.
            }
            return 0;
        }
        String dbalias = update.getDatabase();
        DatabaseHandler db =
            wiParams.getDatabaseAliases().get(dbalias);
        if ((db == null) || (!db.isConnected())) {
            try {
                wiParams.getHttpResponse().sendError(602);
            } catch (IOException e) {
            	// ignorado
            }
            return 0;
        }
        int resp = 1;
        if (update.getPrefix().trim().equals("")) {
            unique(db);
        } else {
            resp = multiple(db);
        }
        return resp;
    }

    private void unique(DatabaseHandler db) {
        String wiobj = update.getWIObj().trim();
        String aux = update.getSql();
        boolean autocommit = update.getAutoCommit().equals("ON");
        db.autocommit(autocommit);
        boolean allok = true;
        int count = StringA.count(aux, ';');
        for (int i = 1; i <= (count + 1); i++) {
            String sqlpart = StringA.piece(aux, ";", i);
            if (!sqlpart.trim().equals("")) {
                uniqueCore(wiobj, sqlpart, wiParams.getWIMap(), db);
            }
            if (!db.getErrorMessage().trim().equals("")) {
                allok = false;
            }
            if ((!autocommit) && (!allok)) {
                break;
            }
        }
        if (!autocommit) {
            if (allok) {
                db.commit();
            } else {
                db.rollback();
            }
        }
        db.autocommit(true);
    }

    private void uniqueCore(String wiobj, String sqlpart, WIMap auxhash,
        DatabaseHandler db) {
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(auxhash);
        db.setCharFilter(EngFunction.cleanSpace(update.getSqlFilter()), "");
        int result = -1;
        try {
            result = db.executeUpdate(sqlpart, auxhash);
        } catch (Exception err) {
        	auxhash.put("wi.sql.error", db.getErrorMessage());
            db.updateLog(auxhash, false);
            wiParams.getErrorLog().write("EventConnector", "uniqueCore: " + 
            		update.getId(), err);
	        try {
	            wiParams.getHttpResponse().sendError(603);
	        } catch (IOException e) {
	        	//ignorado
	        }
        }
        if (result >= 0) {
            prod.setInput(update.getMessageTrue());
            wiParams.getProducer().setParam(prod);
            wiParams.getProducer().execute();
            if (!wiobj.toLowerCase().startsWith("tmp.")) {
                wiParams.getWIMap().put(wiobj, prod.getOutput());
            }
            wiParams.getWIMap().put(wiobj, prod.getOutput());
        } else {
//            String text = onError(result, auxhash);
//            wiParams.getWIMap().put(wiobj, text);
//            String k1 = StringA.getXml("wi.sql.query");
//            String v1 = StringA.getXml(db.getExecutedSQL());
//            wiParams.getWIMap().put(k1, v1);
//            String k2 = StringA.getXml("wi.sql.error");
//            String v2 = StringA.getXml(db.getErrorMessage());
//            wiParams.getWIMap().put(k2, v2);
        }
    }

    private int multiple(DatabaseHandler db) {
		WIMap wiMap = wiParams.getWIMap();
        String wiobj = update.getWIObj().trim();
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
        boolean autocommit = update.getAutoCommit().equals("ON");
        db.autocommit(autocommit);
        for (int i = 1; i <= size; i++) {
            if (!wiobj.toLowerCase().startsWith("tmp.")) {
                wiMap.put(wiobj + ".size()", i);
            }
            wiMap.put(wiobj + ".size()", i);
            wiMap.put("wi.sql.query", "");
            wiMap.put("wi.sql.error", "");
            String aux = update.getRowCondition();
            String rcond =
                StringA.change(aux, "|" + prefix + ".", "|" + prefix + i + ".",
                    false);
            if (!new Condition(wiMap, rcond).execute()) {
                continue;
            }
            String myaux = update.getSql();
            boolean allok = true;
            int count = StringA.count(myaux, ';');
            for (int a = 1; a <= (count + 1); a++) {
                String sqlpart = StringA.piece(myaux, ";", a);
                if (!sqlpart.trim().equals("")) {
                    multipleCore(prefix, wiobj, i, sqlpart, wiMap, db);
                }
                if (!db.getErrorMessage().trim().equals("")) {
                    allok = false;
                }
                if ((!autocommit) && (!allok)) {
                    break;
                }
            }
            if (!autocommit) {
                if (allok) {
                    db.commit();
                } else {
                    db.rollback();
                }
            }
        }
        db.autocommit(true);
        return size;
    }

    private void multipleCore(String prefix, String wiobj, int i,
        String sqlpart, WIMap auxhash, DatabaseHandler db) {
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(auxhash);
        String subprefix = StringA.changeChars(prefix, "[]", "");
        auxhash.put(subprefix + "[" + i + "].index()", i);
        String sqlpart2 = changePrefix(sqlpart, prefix, i);
        db.setCharFilter(EngFunction.cleanSpace(update.getSqlFilter()), "");
        int result = -1;
        try {
            result = db.executeUpdate(sqlpart2, auxhash);
        } catch (Exception err) {
        	auxhash.put("wi.sql.error", db.getErrorMessage());
            db.updateLog(auxhash, false);
            wiParams.getErrorLog().write("EventConnector", "multipleCore: " 
            		+ update.getId(), err);
	        try {
	            wiParams.getHttpResponse().sendError(603);
	        } catch (IOException e) {
	        	// ignorado
	        }
        }
        if (result >= 0) {
            prod.setInput(update.getMessageTrue());
            wiParams.getProducer().setParam(prod);
            wiParams.getProducer().execute();
            if (!wiobj.toLowerCase().startsWith("tmp.")) {
                wiParams.getWIMap().put(wiobj + "[" + i + "]",
                    prod.getOutput());
            }
            wiParams.getWIMap().put(wiobj + "[" + i + "]", prod.getOutput());
        } else {
//            String text = onError(result, auxhash);
//            String k1 = StringA.getXml("wi.sql.query");
//            String v1 = StringA.getXml(db.getExecutedSQL());
//            String k2 = StringA.getXml("wi.sql.error");
//            String v2 = StringA.getXml(db.getErrorMessage());
//            wiParams.getReadResponse().add("<read id=\"" + k1 + "\">" + v1
//                + "</read>");
//            wiParams.getReadResponse().add("<read id=\"" + k2 + "\">" + v2
//                + "</read>");
//            if (!wiobj.toLowerCase().startsWith("tmp.")) {
//                wiParams.getWIMap().put(wiobj + "[" + i + "]",
//                    text);
//            }
//            wiParams.getWIMap().put(wiobj + "[" + i + "]", text);
        }
    }

/*
    private int getErrorCodeSQL(Exception ex) {
        int result = -1;
        if (ex instanceof SQLException) {
            result = ((SQLException) ex).getErrorCode();
            if (result > 0) {
                result = -result;
            }
            if (result == 0) {
                result = -1;
            }
        }
        return result;
    }

    private String onError(int result, WIMap wiMap) {
        String text = update.getMessageFalse(result + "");
        if (text.trim().equals("")) {
            ProducerParam prod = new ProducerParam();
            prod.setWIMap(wiMap);
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
*/
    
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
}
