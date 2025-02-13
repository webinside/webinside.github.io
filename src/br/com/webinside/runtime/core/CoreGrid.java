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
import java.util.List;
import java.util.Map;

import br.com.webinside.runtime.component.AbstractGrid;
import br.com.webinside.runtime.component.GridHtml;
import br.com.webinside.runtime.component.GridRef;
import br.com.webinside.runtime.component.GridSql;
import br.com.webinside.runtime.component.GridXmlOut;
import br.com.webinside.runtime.component.WIObjectGrid;
import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.export.GridNode;
import br.com.webinside.runtime.integration.DatabaseHandler;
import br.com.webinside.runtime.integration.IntFunction;
import br.com.webinside.runtime.integration.Producer;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.util.StringA;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.5 $
 */
public class CoreGrid extends CoreCommon {
    private GridRef gridref;
    private boolean directout = false;

    /**
     * Creates a new CoreGrid object.
     *
     * @param wiParams DOCUMENT ME!
     * @param gridref DOCUMENT ME!
     */
    public CoreGrid(ExecuteParams wiParams, GridRef gridref) {
        this.wiParams = wiParams;
        this.gridref = gridref;
        element = gridref;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
    	boolean cond = isValidCondition();
    	boolean returnEmpty = (!cond && gridref.isReturnEmpty()); 
    	if (!cond && !returnEmpty) {
            wiParams.getWIMap().put("grid." + gridref.getId(),"");
            return;
        }
        directout = (gridref.isGenerateInPage() && !returnEmpty);
        if (directout) {
            if (wiParams.getHttpRequest().getAttribute("wiPage") == null) {
                return;
            }
        }
        String realId = StringA.piece(gridref.getId(), "?", 1);
        IntFunction.importParameters(wiMap, gridref.getId());
        if (!wiParams.getProject().getGrids().containsKey(realId)) {
            wiParams.includeCode("/grids/" + realId + "/grid.jsp");
        }
        AbstractGrid grid =
            (AbstractGrid) wiParams.getProject().getGrids().getElement(realId);
        if (grid == null) {
            return;
        }
        if (grid instanceof GridSql) {
        	if (!cond) {
        		GridSql gridsql = (GridSql) grid;
	        	gridsql.setSql("select 1 from dual");
        	}
            gridLinear(grid, returnEmpty);
        } else if (grid instanceof GridXmlOut) {
            GridXmlOut xmlout = (GridXmlOut) grid;
            CoreGridXmlOut corexmlout = new CoreGridXmlOut(wiParams, xmlout);
            corexmlout.execute(directout);
        } else if (grid instanceof GridHtml) {
            GridHtml gdhtml = (GridHtml) grid;
            if (!gdhtml.getWIType().equals("WIOBJECT")) {
                return;
            }
            WIObjectGrid objgrid = new WIObjectGrid();
            objgrid.setCondition("true");
            objgrid.setGridId(realId);
            objgrid.setWIObjName(StringA.piece(gridref.getId().toLowerCase(),
                    "id=", 2));
            CoreWIObjectGrid coreobjgrid =
                new CoreWIObjectGrid(wiParams, objgrid);
            coreobjgrid.execute();
        }
        writeLog();
    }

    private void gridLinear(AbstractGrid grid, boolean returnEmpty) {
        GridSql gridsql = (GridSql) grid;
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        String dbalias = gridsql.getDatabase();
        if (dbalias.indexOf("]-") > -1) {
            dbalias = StringA.piece(dbalias, "]-", 2);
        }
        DatabaseHandler db = wiParams.getDatabaseAliases().get(dbalias);
        if ((db == null) || (!db.isConnected())) {
            RtmFunction.databaseError(wiParams, dbalias);
            return;
        }
        String wiobj = "grid." + grid.getId().trim();
        String wiSubId = wiobj;
        if (!gridref.getSubId().trim().equals("")) {
        	wiSubId += "." + gridref.getSubId().trim();
        }
    	wiMap.put(wiobj + ".title", grid.getDescription());
        // recebendo limites
        int limit = 0;
        try {
            prod.setInput(gridsql.getLimit().trim());
            new Producer(prod).execute();
            limit = Integer.parseInt(prod.getOutput().trim());
            if (limit < 0) {
                limit = 0;
            }
        } catch (NumberFormatException err) { }
        int next = 1;
        try {
            String nextid = wiSubId + ".next";
            next = Integer.parseInt(wiMap.get(nextid.trim()));
            boolean inGrid = false;
            if (wiParams.getRequestAttribute("wiGrid") != null) {
            	inGrid = true;
            }
            if (next < 1 || inGrid) {
                next = 1;
            }
        } catch (NumberFormatException err) {
        	// ignorado
        }
        // logica SQL
        int range = limit + 1;
        if (limit == 0) {
            range = 0;
        }
        wiMap.put(wiSubId + ".limit", limit);
        db.setCharFilter(RtmFunction.cleanSpace(gridsql.getSqlFilter()), "");
        long ini = new Date().getTime();
        Exception exrs = null;
        ResultSet rs = null;
        try {
            rs = db.execute(gridsql.getSql(), wiMap, next, range);
            GridNode.include(wiParams, wiMap, gridsql, db.getExecutedSQL());
        } catch (Exception err) {
        	GridNode.exclude(wiParams, gridsql); 
            exrs = err;
        }
        long fim = new Date().getTime();
        dbTime(db, wiMap, ini, fim, "grid " + grid.getDescription());
        if (rs != null) {
            int rowcount = rs.rowCount();
            if (returnEmpty) {
            	rowcount = 0;
                wiMap.put(wiSubId + ".returnempty", "true");
            }
            if (rowcount > -1) {
                wiMap.put(wiSubId + ".rowcount", rowcount);
            }
            List mapList = new ArrayList();
            int count = 0;
            int countpos = rs.go(next);
            if (countpos == -1) {
                countpos = rs.next();
                while ((countpos < next) && (countpos > 0)) {
                    countpos = rs.next();
                }
            }
            if (returnEmpty) countpos = 0;
            if (countpos > 0) {
                wiMap.put(wiSubId + ".from", next);
            }
	    	String clText = wiMap.get(wiSubId + ".contentlimit").trim();
            int to = 0;
            while ((countpos > 0) && ((limit == 0) || (count < limit))) {
            	to = countpos;
                count = count + 1;
                Map aux = rs.columns("");
                aux.put("rowid", countpos + "");
                aux.put("rowid0", countpos - 1 + "");
                aux.put("rowseq", count + "");
                mapList.add(aux);
                countpos = rs.next();
                if (!directout && mapList.size() >= 10000 
                		&& !clText.equalsIgnoreCase("nolimit")) {
                	String tId = wiSubId.substring(5, wiSubId.length());
    		        String msg = "Grid " + tId + " content limit exceded\r\n";
                	wiParams.getErrorLog().write("CoreGrid", "Map Array", msg + wiMap);
                	break;
                } 
            }
            wiMap.put(wiSubId + ".to", to);
            if (limit > 0) {
                wiMap.put(wiSubId + ".next", to + 1);
            }
            int prev = next - limit;
            if ((next < limit) && (next > 1)) {
                prev = 1;
            }
            if (prev < 0) {
            	prev = 0;
            }
            next = 0;
            if (countpos > 0) {
                next = countpos;
            }
            wiMap.put(wiSubId + ".prev", prev);
            wiMap.put(wiSubId + ".size", count);
            Map[] array = (Map[])mapList.toArray(new Map[0]);
            GridLinearProducer glin =
                new GridLinearProducer(wiParams, gridsql, gridref);
            glin.execute(array, directout);
            if (wiParams.mustExit()) {
                return;
            }
            if (limit > 0) {
                if (wiobj.indexOf("?") == -1) {
                    GridLinearNavigator nav = new GridLinearNavigator(wiParams);
                    nav.setGridRef(gridref);
                    nav.navigator(gridsql, wiobj, prev, next);
                }
            }
        } else {
    	    wiMap.put(wiobj, "");
        	queryException(exrs, db, grid.getDescription());
        }
    }
}
