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

import java.util.Map;
import br.com.webinside.runtime.component.AbstractGrid;
import br.com.webinside.runtime.component.AbstractGridLinear;
import br.com.webinside.runtime.component.GridHtml;
import br.com.webinside.runtime.component.JavaGrid;
import br.com.webinside.runtime.integration.InterfaceGrid;
import br.com.webinside.runtime.integration.JavaParameter;
import br.com.webinside.runtime.integration.ProducerParam;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class CoreJavaGrid extends CoreCommon {
    private JavaGrid javagrid;

    /**
     * Creates a new CoreJavaGrid object.
     *
     * @param wiParams DOCUMENT ME!
     * @param javagrid DOCUMENT ME!
     */
    public CoreJavaGrid(ExecuteParams wiParams, JavaGrid javagrid) {
        this.wiParams = wiParams;
        this.javagrid = javagrid;
        element = javagrid;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        if (!isValidCondition()) {
            return;
        }
        String name = javagrid.getClassName().trim();
        if (name.equals("")) {
            return;
        }
        Map[] array = null;
        String realId = javagrid.getGridId().trim();
        if (!wiParams.getProject().getGrids().containsKey(realId)) {
            wiParams.includeCode("/grids/" + realId + "/grid.jsp");
        }
        AbstractGrid grid =
            (AbstractGrid) wiParams.getProject().getGrids().getElement(realId);
        if ((grid == null) || !(grid instanceof GridHtml)) {
            return;
        }
        int type = 1;
        try {
            int limit = 0;
            try {
                GridHtml gridhtml = (GridHtml) grid;
                ProducerParam prod = new ProducerParam();
                prod.setWIMap(wiMap);
                prod.setInput(gridhtml.getLimit());
                wiParams.getProducer().setParam(prod);
                wiParams.getProducer().execute();
                limit = Integer.parseInt(prod.getOutput().trim());
                if (limit < 0) {
                    limit = 0;
                }
            } catch (NumberFormatException err) {
            }

            // procura pela classe
            Class cl = getUserClass(name);
            if (cl != null) {
                wiMap.put("grid.limit", limit + "");
                wiMap.put("grid.id", grid.getId().trim());
                InterfaceGrid interf = (InterfaceGrid) cl.newInstance();
                JavaParameter[] params = javagrid.getInputParameters();
                EngFunction.putJavaParameters(wiMap, params);
                array = interf.execute(wiMap, wiParams.getDatabaseAliases());
                type = interf.returnType();
                if ((type != 1) && (type != 0) && (type != -1)) {
                    type = 1;
                }
            } else {
                wiParams.getErrorLog().write(name, "JAVAGRID",
                    "Class Not Found: " + name);
            }
        } catch (Exception err) {
            wiParams.getErrorLog().write(name, "JAVAGRID", err);
			if (!wiParams.getPage().getErrorPageName().equals("")) {
				wiParams.setRequestAttribute("wiException", err);
			}
        } catch (Error err) {
            wiParams.getErrorLog().write(name, "JAVAGRID", err);
        }
        if (array != null) {
	        GridLinearNavigator linear = new GridLinearNavigator(wiParams);
	        linear.execute((AbstractGridLinear) grid, array, type, false);
	        writeLog();
        }
    }

    private Class getUserClass(String name) {
        ClassLoader classloader = wiParams.getClassLoader();
        Class cl = null;
        try {
            cl = classloader.loadClass(name);
        } catch (ClassNotFoundException err) {
        }
        return cl;
    }
}
