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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import br.com.webinside.runtime.component.AbstractGridLinear;
import br.com.webinside.runtime.component.GridRef;
import br.com.webinside.runtime.component.GridSql;
import br.com.webinside.runtime.function.sv.SVNode;
import br.com.webinside.runtime.integration.IntFunction;
import br.com.webinside.runtime.integration.InterfaceGrid;
import br.com.webinside.runtime.integration.Producer;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.integration.taglib.ScriptOrStyle;
import br.com.webinside.runtime.util.I18N;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;
import br.com.webinside.runtime.util.WISession;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.5 $
 */
public class GridLinearNavigator {
    /** DOCUMENT ME! */
    private ExecuteParams wiParams;
    private WIMap wiMap;
    private GridRef gridRef;

    /**
     * Creates a new GridLinearNavigator object.
     *
     * @param wiParams DOCUMENT ME!
     */
    public GridLinearNavigator(ExecuteParams wiParams) {
        this.wiParams = wiParams;
        if (wiParams != null) {
            wiMap = wiParams.getWIMap();
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param gridRef DOCUMENT ME!
     */
    public void setGridRef(GridRef gridRef) {
        this.gridRef = gridRef;
    }

    /**
     * DOCUMENT ME!
     *
     * @param grid DOCUMENT ME!
     * @param array DOCUMENT ME!
     * @param arrayType em InterfaceGrid - COMPLETE, HAS_MORE_ROWS, NO_MORE_ROWS 
     * @param directout DOCUMENT ME!
     */
    public void execute(AbstractGridLinear grid, Map[] array, int arrayType,
        boolean directout) {
        if (array == null) {
            array = new Map[0];
        }
        String wiobj = "grid." + grid.getId().trim().toLowerCase();
        int limit = 0;
        try {
            ProducerParam prod = new ProducerParam();
            prod.setWIMap(wiMap);
            prod.setInput(grid.getLimit().trim());
            new Producer(prod).execute();
            limit = Integer.parseInt(prod.getOutput().trim());
            if (limit < 0) {
                limit = 0;
            }
        } catch (NumberFormatException err) { }
        int from = 1;
        try {
            String nextid = "grid." + grid.getId().trim() + ".next";
            from = Integer.parseInt(wiMap.get(nextid.trim()));
            if (from < 1) {
                from = 1;
            }
        } catch (NumberFormatException err) {
        	// erro desconsiderado
        }
        wiMap.put(wiobj + ".from", from);
        int to = (from + limit) - 1;
        if (arrayType == InterfaceGrid.COMPLETE) {
        	if (wiMap.get(wiobj + ".rowcount").equals("")) {
        		wiMap.put(wiobj + ".rowcount", array.length + "");
        	}
            if (limit == 0) {
                to = array.length;
            }
            if (to > array.length) {
                to = array.length;
            }
        } else {
            to = (from + array.length) - 1;
        }
        wiMap.put(wiobj + ".to", to);
        if (limit > 0) {
            wiMap.put(wiobj + ".next", to + 1);
        }
        int prev = from - limit;
        if (prev < 1) {
            prev = 1;
        }
        wiMap.put(wiobj + ".prev", prev);
        int size = to - from + 1;
        if (size < 0) {
            size = 0;
        }
        wiMap.put(wiobj + ".size", size);
        wiMap.put(wiobj + ".limit", limit);
        Map[] segArray = new Map[0];
        if (arrayType != InterfaceGrid.COMPLETE) {
            segArray = new Map[array.length];
            for (int a = 0; a < array.length; a++) {
                Map haux = array[a];
                if (haux == null) haux = new HashMap();
                segArray[a] = haux;
                if (!haux.containsKey("rowid")) {
                	haux.put("rowid", (from + a) + "");
                }
                if (!haux.containsKey("rowid0")) {
                    haux.put("rowid0", ((from + a) - 1) + "");
                }
                haux.put("rowseq", (a + 1) + "");
            }
        } else if (from <= to) {
            segArray = new Map[to - from + 1];
            int rowseq = 0;
            for (int a = from; a <= to; a++) {
                rowseq = rowseq + 1;
                Map haux = array[a - 1];
                if (haux == null) haux = new HashMap();
                segArray[a - from] = haux;
                if (!haux.containsKey("rowid")) {
                	haux.put("rowid", a + "");
                }
                if (!haux.containsKey("rowid0")) {
                    haux.put("rowid0", (a - 1) + "");
                }
                haux.put("rowseq", rowseq + "");
            }
        }
        GridLinearProducer linear =
            new GridLinearProducer(wiParams, grid, gridRef);
        linear.execute(segArray, directout);
        if (wiParams.mustExit()) {
            return;
        }
        if ((to == 0) || (to <= limit)) {
            prev = 0;
        }
        if (((to + 1) > array.length) && 
        		(arrayType != InterfaceGrid.HAS_MORE_ROWS)) {
            to = -1;
        }
        if (limit > 0) {
            navigator(grid, wiobj, prev, to + 1);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param grid DOCUMENT ME!
     * @param wiobj DOCUMENT ME!
     * @param prev DOCUMENT ME!
     * @param next DOCUMENT ME!
     */
    public void navigator(AbstractGridLinear grid, String wiobj, 
    		int prev, int next) {
        makeForm(grid);
        String comp = "";
        if (gridRef != null && !gridRef.getSubId().equals("")) {
        	comp = "." + gridRef.getSubId();
        }
        String txtPISize = wiMap.get(wiobj + comp + ".linkindexsize");
        if (txtPISize.trim().equals("")) {
            txtPISize = wiMap.get(wiobj + ".linkindexsize");
        }
        if (txtPISize.trim().equals("")) {
            txtPISize = wiMap.get("grid.linkindexsize");
        }
        int PISize = asInteger(txtPISize);
        String pageIndex = "";
        if (PISize > 0) {
            pageIndex = makePageIndex(wiobj, comp, PISize);
            wiMap.put(wiobj + comp + ".linkindex", pageIndex);
        }
        String txtTotal = wiMap.get(wiobj + ".rowcount");
        int total = asInteger(txtTotal);
        String txtLimit = wiMap.get(wiobj + ".limit");
        int perPage = asInteger(txtLimit);
        int lastPageId = 0;
        if ((total > 0) && (perPage > 0)) {
            lastPageId = ((total / perPage) * perPage) + 1;
            if ((total % perPage) == 0) {
                lastPageId = lastPageId - perPage;
            }
            wiMap.put(wiobj + ".lastpage", lastPageId + "");
        }
        boolean showIcon = getGridVar(wiobj, "showicon").equalsIgnoreCase("true");
        boolean showAll = getGridVar(wiobj, "showall").equalsIgnoreCase("true");
        if (showIcon) showAll = true;
        String txtfirst = getGridVar(wiobj, "txtfirst");
        if (txtfirst.equals("")) {
            txtfirst = "<b>" + i18n("Primeiro") + "</b>";
            if (showIcon) txtfirst = "<b></b>";
        }
        String txtback = getGridVar(wiobj, "txtback");
        if (txtback.equals("")) {
            txtback = "<b>" + i18n("Anterior") + "</b>";
            if (showIcon) txtback = "<b></b>";
        }
        String txtmid = getGridVar(wiobj, "txtmid");
        if (txtmid.equals("")) {
            txtmid = "<b class='nav_sep'>-</b>";
            if (showIcon) txtmid = "";
        }
        String txtgo = getGridVar(wiobj, "txtgo");
        if (txtgo.trim().equals("")) {
            txtgo = "<b>" + i18n("Próximo") + "</b>";
            if (showIcon) txtgo = "<b></b>";
        }
        String txtlast = getGridVar(wiobj, "txtlast");
        if (txtlast.equals("")) {
            txtlast = "<b>" + i18n("Último") + "</b>";
            if (showIcon) txtlast = "<b></b>";
        }
        String type = "javascript:submitWIGrid";
        if (wiParams.getPage().getRerender().equals("ON")) {
        	type = "javascript:rerenderWIGrid";
        }
        String pageAction = "<a href=\"" + type + "('" + wiobj + comp + "',";
        // link - first
        String linkfirst = getGridVar(wiobj, "txtfirstoff");
        if (showAll && linkfirst.equals("")) {
        	linkfirst = "<a href=\"#\" class='nav_firstoff'>" + txtfirst + "</a>";
        }
        if (lastPageId > 1 && (!showAll || prev > 0)) {
            linkfirst = pageAction + "1)\" class='nav_first'>" + txtfirst + "</a>";
        }
        wiMap.put(wiobj + comp + ".linkfirst", linkfirst);
        // link - back
        String linkback = getGridVar(wiobj, "txtbackoff");
        if (showAll && linkback.equals("")) {
        	linkback = "<a href=\"#\" class='nav_backoff'>" + txtback + "</a>";
        }
        if (prev > 0) {
            linkback = pageAction + prev + ")\" class='nav_back'>" + txtback + "</a>";
        }
        wiMap.put(wiobj + comp + ".linkback", linkback);
        wiMap.put(wiobj + comp + ".hasmore", "false");
        // link - go
        String linkgo = getGridVar(wiobj, "txtgooff");
        if (showAll && linkgo.equals("")) {
        	linkgo = "<a href=\"#\" class='nav_gooff'>" + txtgo + "</a>";
        }
        if (next > 0) {
            linkgo = pageAction + next + ")\" class='nav_go'>" + txtgo + "</a>";
            wiMap.put(wiobj + comp + ".hasmore", "true");
        }
        wiMap.put(wiobj + comp + ".linkgo", linkgo);
        // link - last
        String linklast = getGridVar(wiobj, "txtlastoff");
        if (showAll && linklast.equals("")) {
        	linklast = "<a href=\"#\" class='nav_lastoff'>" + txtlast + "</a>";
        }
        if (lastPageId > 1 && (!showAll || next > 0)) {
            linklast = pageAction + lastPageId + ")\" class='nav_last'>" + txtlast + "</a>";
        }
        wiMap.put(wiobj + comp + ".linklast", linklast);
        // montagem dos links completos
    	txtmid = "\n" + txtmid + "\n";
        String link = linkback + txtmid + linkgo;
        String linkindexed = "";
        if (!pageIndex.equals("")) {
            linkindexed = linkback + txtmid + pageIndex + txtmid + linkgo;
        }
        if (linkgo.trim().equals("")) {
            link = linkback;
            if (!pageIndex.equals("")) {
                linkindexed = linkback + txtmid + pageIndex;
            }
        }
        if (linkback.trim().equals("")) {
            link = linkgo;
            if (!pageIndex.equals("")) {
                linkindexed = pageIndex + txtmid + linkgo;
            }
        }
        if ((linkgo.trim().equals("")) && (linkback.trim().equals(""))) {
            link = ""; 
            linkindexed = "";
        }
        wiMap.put(wiobj + comp + ".link", link);
        wiMap.put(wiobj + comp + ".linkindexed", linkindexed);
        String linkfull = "";
        if (!linkindexed.equals("")) {
        	linkfull = linkfirst + txtmid + linkindexed + txtmid + linklast;
        }	
        wiMap.put(wiobj + comp + ".linkfull", linkfull);
    }

    private String makePageIndex(String wiobj, String comp, int PISize) {
        StringBuffer resp = new StringBuffer();
        String type = "javascript:submitWIGrid";
        if (wiParams.getPage().getRerender().equals("ON")) {
        	type = "javascript:rerenderWIGrid";
        }
        String pageAction = "<a href=\"" + type + "('" + wiobj + comp + "',";
        String txtTotal = wiMap.get(wiobj + comp + ".rowcount");
        int total = asInteger(txtTotal);
        if (total == 0) {
            return "";
        }
        String txtFrom = wiMap.get(wiobj + comp + ".from");
        int from = asInteger(txtFrom);
        String txtLimit = wiMap.get(wiobj + comp + ".limit");
        int perPage = asInteger(txtLimit);
        if (perPage < 1) {
            return "";
        }
        int pgcount = (total / perPage) + 1;
        if ((total % perPage) == 0) {
            pgcount = total / perPage;
        }
        int me = (from / perPage) + 1;
        if ((from % perPage) == 0) {
            me = from / perPage;
        }
        int semi = PISize / 2;
        int start = me - semi;
        int desvio = 1;
        if ((PISize % 2) == 1) {
            desvio = 0;
        }
        int end = (me + semi) - desvio;
        if (start < 1) {
            end = end + ((start * (-1)) + 1);
        }
        if (end > pgcount) {
            start = start - (end - pgcount);
        }
        if (start < 1) {
            start = 1;
        }
        if (end > pgcount) {
            end = pgcount;
        }
        for (int i = start; (i <= end) && (end > 1); i++) {
            if (i == me) {
                resp.append("<b class='nav_page'>" + i + "</b>\n");
            } else {
                int next = ((i - 1) * perPage) + 1;
                resp.append(pageAction + next + ")\" class='nav_index'>" + i + "</a>\n");
            }
        }
        return resp.toString().trim();
    }

    private String i18n(String text) {
        return new I18N().get(text);
    }

    private int asInteger(String value) {
        try {
            int ret = Integer.parseInt(value);
            if (ret < 0) {
                ret = 0;
            }
            return ret;
        } catch (NumberFormatException err) {
            return 0;
        }
    }

    private void makeForm(AbstractGridLinear grid) {
    	HttpServletRequest request = wiParams.getHttpRequest();
    	String form = (String) request.getAttribute("wiGridHtmlForm");
    	form = (form == null ? "" : form.trim());
        if (form.equals("")) form = makeForm();
        if (grid instanceof GridSql) {
            GridSql grdSql = (GridSql) grid;
            String ignore = grdSql.getIgnoreVars();
            int cnt = StringA.count(ignore, ',');
            for (int i = 1; i <= (cnt + 1); i++) {
                String var = StringA.piece(ignore, ",", i).toLowerCase().trim();
                if (!var.equals("")) {
                    form = removeFormItem(form, var);
                }
            }
        }
        request.setAttribute("wiGridHtmlForm", form);
    }

    private String makeForm() {
        Set<String> svSet = wiParams.getProject().getSecureVars();
        StringBuffer form = new StringBuffer();
        WIMap aux = new WIMap();
        aux.putObj("tmp.", wiMap.getObj("tmp."));
        aux.putObj("stmp.", wiMap.getObj("stmp."));
        aux.putObj("grid.", wiMap.getObj("grid."));
        boolean debug = debug(wiMap);
        Map all = aux.getAsMap();
        Iterator it = all.keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            String value = aux.get(key);
            boolean valid = true;
            if (key.endsWith(".") || key.endsWith("()")) valid  = false;
            if (key.startsWith("tmp.tag_")) valid  = false;
            if (key.startsWith("tmp.rerender")) valid = false;
            if (key.startsWith("grid.")) {
                valid = false;
            	if (key.endsWith(".next")) valid = true;
    			if (key.endsWith(".link.debug")) valid = true;
            	if (key.endsWith(".rerender")) valid = true;
            }
            if (valid) {
	            // a variavel grid.<nome>.next so deve ser colocada no form se
	            // vier pelo request, senão dá erro navegar 2 grids na mesma página
	            if (key.startsWith("grid.") && key.endsWith(".next")) {
	                value = (String) wiParams.getHttpParameters().get(key);
	                if (value == null) value = "";
	            }
	            if (value.length() < 100) {
	                if (!value.equals("")) {
	                    value = StringA.changeLinebreak(value, "\\n");
	                	WISession session = wiParams.getWISession();
	                    if (IntFunction.isSecureVar(svSet, key) && session.isValid()) {
                        	SVNode svNode = IntFunction.getSVNode(session, key);
                        	value = svNode.addValue(wiMap.get("wi.page.id"), value);
	                    }
	                    form.append(makeFormItem(key, value, debug));
	                }
	            }
            }    
        }
        return navForm(wiMap, form.toString());
    }

    private String removeFormItem(String form, String name) {
        String item = makeFormItem(name, null, debug(wiMap));
        int ini = form.indexOf(item);
        while (ini > -1) {
            // começa a busca a partir de value="
            int from = form.indexOf("value=\"", ini) + 7;
            int qt = -1;
            while (qt == -1) {
                qt = form.indexOf("\"", from);
                if (form.charAt(qt - 1) == '\\') {
                    from = qt + 1;
                    qt = -1;
                }
            }
            String p1 = StringA.mid(form, 0, ini - 1);
            // incrementa o \n> do final
            String p2 = StringA.mid(form, qt + 3, form.length());
            form = p1 + p2;
            ini = form.indexOf(item);
        }
        return form;
    }

    private static String makeFormItem(String name, String value, boolean debug) {
        StringBuffer item = new StringBuffer();
        String type = "hidden";
        if (debug) {
            item.append(name + " = ");
        	type = "text";
        }
        item.append("<input type=\"" + type + "\" name=\"");
        item.append(name);
        if (!name.endsWith(".") || value != null) {
        	item.append("\"");
        }
        if (value != null) {
            value = StringA.change(value, "\"", "&quot;");
            item.append(" value=\"").append(value).append("\">\n");
        }
        if (debug) item.append("<br/>\n");
        return item.toString();
    }
    
    public static String navForm(WIMap wiMap, String content) {
    	String action = wiMap.get("wi.request.uri");
    	StringBuffer aux = new StringBuffer();
    	gridNavScript(aux, wiMap);
        aux.append("<span id=\"wiGridNav\">");
        aux.append("<form id=\"wiFormGridNav\" name=\"wiFormGridNav\"");
        aux.append(" method=\"POST\" action=\"" + action + "\"");
        String type = (debug(wiMap) ? "inline" : "none");
        aux.append(" style=\"display:" + type + "\">\n");
        if (!content.trim().equals("")) {
        	aux.append(content.trim() + "\n");
        }	
        aux.append("</form></span>");
    	return aux.toString();
    }
    
    private static void gridNavScript(StringBuffer aux, WIMap wiMap) {
    	HttpServletRequest req = ExecuteParams.get().getHttpRequest();
    	String path = "/" + wiMap.get("wi.proj.id") + "/js/gridnav.js";
    	List<String> list = (List) req.getAttribute(ScriptOrStyle.WI_REQ_LIST_KEY);
    	if (list != null && list.contains(path)) return;
        aux.append("<script src='" + path + "'");
        aux.append(" type='text/javascript'></script>\n");
    }
    
    private String getGridVar(String grid, String var) {
    	String resp = wiMap.get(grid + "." + var).trim();
    	if (resp.equals("") || 
    			(var.equals("showall") && !resp.equalsIgnoreCase("true"))) {
    		resp = wiMap.get("grid." + var).trim();
    	}
    	return resp;
    }
    
    private static boolean debug(WIMap wiMap) {
    	String debug = wiMap.get("grid.link.debug");
    	if (debug.trim().equalsIgnoreCase("true")) return true;
    	return false;
    }

}
