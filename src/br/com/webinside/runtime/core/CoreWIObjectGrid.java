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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.com.webinside.runtime.component.AbstractGrid;
import br.com.webinside.runtime.component.AbstractGridLinear;
import br.com.webinside.runtime.component.GridHtml;
import br.com.webinside.runtime.component.GridRef;
import br.com.webinside.runtime.component.WIObjectGrid;
import br.com.webinside.runtime.integration.Condition;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class CoreWIObjectGrid extends CoreCommon {
    private WIObjectGrid wiobjgrid;

    /**
     * Creates a new CoreWIObjectGrid object.
     *
     * @param wiParams DOCUMENT ME!
     * @param wiobjgrid DOCUMENT ME!
     */
    public CoreWIObjectGrid(ExecuteParams wiParams, WIObjectGrid wiobjgrid) {
        this.wiParams = wiParams;
        this.wiobjgrid = wiobjgrid;
        element = wiobjgrid;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        if (!isValidCondition()) {
            return;
        }
        String obj = wiobjgrid.getWIObjName().trim();
        if (obj.equals("")) {
            return;
        }
        if (!wiParams.getProject().getGrids().containsKey(wiobjgrid.getGridId())) {
            wiParams.includeCode("/grids/" + wiobjgrid.getGridId() + "/grid.jsp");
        }
        AbstractGrid grid =
            (AbstractGrid) wiParams.getProject().getGrids().getElement(wiobjgrid.getGridId()
                        .trim());
        if ((grid == null) || !(grid instanceof GridHtml)) {
            return;
        }
        List mapList = new ArrayList();
        int size = 0;
        try {
        	String txtsize = wiMap.get(obj + ".size()");
            size = Function.parseInt(txtsize);
        	if (txtsize.equals("")) {
        		size = Function.getArraySize(wiMap, obj);
        	}
        } catch (NumberFormatException err) { }
        for (int i = 0; i < size; i++) {
            WIMap aux = wiMap.cloneMe();
            aux.put("rowid", (i + 1));
            aux.put("rowid0", i);
            WIMap sub = null;
            String key = obj + "[" + (i + 1) + "].";
            try {
                sub = (WIMap) aux.getObj(key);
            } catch (Exception err) { }
            if (sub == null) {
                sub = new WIMap();
            }
            clean(aux, obj);
            Iterator it = new HashSet(sub.getInternalMap().keySet()).iterator();
            if (it.hasNext()) {
                String ch = (String) it.next();
                if (ch.endsWith(".")) {
                    try {
                        key = key + ch;
                        sub = (WIMap) sub.getObj(ch);
                    } catch (Exception err) { }
                }
            }
            aux.putAll(sub.getAsMap());
            recursive(aux);
            if (wiParams.mustExit()) {
                return;
            }
            String cond = wiobjgrid.getRegisterCondition();
            // Compatibilidade com grids antigos sem o registerCondition
            if (cond.equals("")) cond = "true";
            if (new Condition(aux, cond).execute()) {
            	mapList.add(aux.getAsMap());
            }	
        }
        Map[] array = (Map[])mapList.toArray(new Map[0]);
        GridRef ref = new GridRef();
        ref.setCondition("true");
        ref.setId(wiobjgrid.getGridId());
        ref.setSubId(wiobjgrid.getSubId());
        GridLinearNavigator linear = new GridLinearNavigator(wiParams);
        linear.setGridRef(ref);
        linear.execute((AbstractGridLinear) grid, array, 1, false);
        writeLog();
    }

    private void recursive(WIMap aux) {
    	AbstractGrid grd =
            (AbstractGrid) wiParams.getProject().getGrids().getElement(wiobjgrid
                        .getGridId());
        if ((grd == null) || !(grd instanceof GridHtml)) {
            return;
        }
        String modelo = ((GridHtml) grd).getContentRegister();
        int from = 0;
        int pos;
        while ((pos = modelo.indexOf("|grid.", from)) > -1) {
            int end = modelo.indexOf("|", pos + 1);
            if (end == -1) {
                end = modelo.length();
            }
            String name = StringA.mid(modelo, pos + 6, end - 1);
            WIObjectGrid objgrid = new WIObjectGrid();
            objgrid.setGridId(StringA.piece(name, "?", 1));
            String p2 = StringA.piece(name, "?", 2);
            p2 = StringA.piece(p2, "&", 1).toLowerCase();
            objgrid.setWIObjName(StringA.piece(p2, "id=", 2));
            objgrid.setCondition("true");
            List lista = wiParams.getPage().getPrePage().getWIObjectGrids();
            for (int i = 0; i < lista.size(); i++) {
                WIObjectGrid og = (WIObjectGrid) lista.get(i);
                if (og.getGridId().equalsIgnoreCase(name)) {
                    objgrid.setCondition(og.getCondition());
                    objgrid.setWIObjName(og.getWIObjName());
                    break;
                }
            }
            wiParams.includeCode("/grids/" + objgrid.getGridId() + "/grid.jsp");
            WIMap origMap = wiParams.getWIMap();
            wiParams.setParameter(ExecuteParams.WI_MAP, aux);
            CoreWIObjectGrid core = new CoreWIObjectGrid(wiParams, objgrid);
            core.execute();
            wiParams.setParameter(ExecuteParams.WI_MAP, origMap);
            from = end + 1;
        }
    }

    private void clean(WIMap aux, String obj) {
        int count = StringA.count(obj, '.');
        if (count > 0) {
            int last = obj.lastIndexOf(".");
            String prefix = StringA.mid(obj, 0, last);
            String sufix = StringA.mid(obj, last + 1, obj.length());
            try {
                aux = (WIMap) aux.getObj(prefix);
            } catch (Exception err) {
                aux = new WIMap();
            }
            obj = sufix;
        }
        Iterator it = new HashSet(aux.getInternalMap().keySet()).iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (key.equalsIgnoreCase(obj + ".")
                        || key.toLowerCase().startsWith(obj + "[")) {
                aux.remove(key);
            }
        }
    }
}
