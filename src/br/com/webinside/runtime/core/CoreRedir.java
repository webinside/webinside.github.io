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

import br.com.webinside.runtime.component.AbstractRedir;
import br.com.webinside.runtime.component.Page;
import br.com.webinside.runtime.component.RedirConditional;
import br.com.webinside.runtime.component.RedirSql;
import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.integration.DatabaseHandler;
import br.com.webinside.runtime.integration.IntFunction;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.4 $
 */
public class CoreRedir extends CoreCommon {
    private AbstractRedir redir;

    /**
     * Creates a new CoreRedir object.
     *
     * @param wiParams DOCUMENT ME!
     * @param redir DOCUMENT ME!
     */
    public CoreRedir(ExecuteParams wiParams, AbstractRedir redir) {
        this.wiParams = wiParams;
        this.redir = redir;
        element = redir;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        if (!isValidCondition()) {
            return;
        }
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        if (redir instanceof RedirConditional) {
            String url = redir.getGo().trim();
            if (!url.trim().equals("")) {
                setPreSource();
                prod.setInput(url);
                wiParams.getProducer().setParam(prod);
                wiParams.getProducer().execute();
                String dest = prod.getOutput();
                if (redir.hasReturn() && (dest.indexOf(".wsp") != -1)) {
                    IntFunction.importParameters(wiMap, dest);
                    recursiveCore(dest);
                } else {
                    wiParams.sendRedirect(dest, wiMap, true);
                }
            }
        } else if (redir instanceof RedirSql) {
            sql((RedirSql) redir);
        }
        writeLog();
    }

    private void sql(RedirSql redirsql) {
        String dbalias = redirsql.getDatabase();
        DatabaseHandler db = wiParams.getDatabaseAliases().get(dbalias);
        if ((db == null) || (!db.isConnected())) {
            EngFunction.databaseError(wiParams, dbalias);
            return;
        }
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        db.setCharFilter(EngFunction.cleanSpace(redirsql.getSqlFilter()), "");
        long ini = new Date().getTime();
        Exception exrs = null;
        ResultSet rs = null;
        try {
            rs = db.execute(redirsql.getSql(), wiMap);
        } catch (Exception err) {
            exrs = err;
        }
        long fim = new Date().getTime();
        dbTime(db, wiMap, ini, fim, "redir " + redir.getDescription());
        if (rs != null) {
            int pos = rs.next();
            String wiobj = redirsql.getWIObj().trim();
            if ((!wiobj.toLowerCase().startsWith("wi.")) && (
                            !wiobj.equals("")
                        )) {
                String[] names = rs.columnNames();
                for (int i = 0; i < names.length; i++) {
                    String value = "";
                    if (pos == 1) value = rs.column(i + 1);
                    wiMap.put(wiobj + "." + names[i], value);
                    //deprecated = wiMap.put(wiobj + "." + (i + 1), value);
                }
            }
            boolean bgo = false;
            if ((pos == 1) && (redirsql.getWhen().trim().equals("FOUND"))) {
                bgo = true;
            }
            if ((pos != 1) && (redirsql.getWhen().trim().equals("NOTFOUND"))) {
                bgo = true;
            }
            String url = redir.getGo().trim();
            if ((bgo) && (!url.trim().equals(""))) {
                setPreSource();
                prod.setInput(url);
                wiParams.getProducer().setParam(prod);
                wiParams.getProducer().execute();
                String dest = prod.getOutput();
                if (redir.hasReturn() && (dest.indexOf(".wsp") != -1)) {
                    recursiveCore(dest);
                } else {
                	wiParams.sendRedirect(dest, wiMap, true);
                }
            }
        } else {
        	queryException(exrs, db, redir.getDescription());
        }
    }

    private void setPreSource() {
        String projprev = wiMap.get("wi.proj.prev");
        String pageprev = wiMap.get("wi.page.prev");
        wiMap.put("wi.proj.prev.bkp", projprev);
        wiMap.put("wi.page.prev.bkp", pageprev);
    }

    private void recursiveCore(String fsource) {
    	String name = StringA.piece(fsource, ".wsp", 1);
    	if (!name.startsWith("/")) {
    		name = "/" + name;
    	}
    	// Esse core recursivo tem que usar o contexto principal
        WIMap origMap = wiParams.getWIMap();
        String jspfile = origMap.get("wi.jsp.filename");
        origMap.put("wi.jsp.filename_parent", jspfile);
        origMap.put("wi.jsp.filename", name + "_pre");
        String redirProj = origMap.get("wi.redir.proj");
        String redirPage = origMap.get("wi.redir.page");
       	origMap.put("wi.redir.proj", origMap.get("wi.proj.id"));
       	origMap.put("wi.redir.page", origMap.get("wi.page.id"));
    	if (origMap.get("wi.page.type").equals("pos")) {
    		origMap.put("wi.redir.proj", origMap.get("wi.proj.prev"));
    		origMap.put("wi.redir.page", origMap.get("wi.page.prev"));
    	}
        String bkCond = origMap.get("wi.block.cond");
        String bkVar = origMap.get("wi.block.var");
        origMap.remove("wi.block.cond");
        origMap.remove("wi.block.var");
        wiParams.includePrePage(new Page(name));
       	origMap.put("wi.block.cond", bkCond);
       	origMap.put("wi.block.var", bkVar);
       	origMap.put("wi.redir.proj", redirProj);
       	origMap.put("wi.redir.page", redirPage);
        origMap.put("wi.jsp.filename", jspfile);
        origMap.remove("wi.jsp.filename_parent");
    }
    
}
