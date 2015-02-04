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

import java.lang.reflect.Method;

import br.com.webinside.runtime.component.Connector;
import br.com.webinside.runtime.integration.IntFunction;
import br.com.webinside.runtime.integration.InterfaceConnector;
import br.com.webinside.runtime.integration.JavaParameter;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.7 $
 */
public class CoreConnector extends CoreCommon {
    private Connector connector;

    /**
     * Creates a new CoreConnector object.
     *
     * @param wiParams DOCUMENT ME!
     * @param connector DOCUMENT ME!
     */
    public CoreConnector(ExecuteParams wiParams, Connector connector) {
        this.wiParams = wiParams;
        this.connector = connector;
        element = connector;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        if (!isValidCondition()) return;
        String lblpage = "PREPAGE";
        if (!connector.isInPrePage()) {
            lblpage = "POSPAGE";
        }
        String proj = wiMap.get("wi.proj.id");
        String page = wiMap.get("wi.page.id");
        lblpage = "Proj:" + proj + ", Page:" + page + ", Where:" + lblpage;
        String name = connector.getClassName().trim();
        if (name.equals("")) return;
        if (wiMap.get("pvt.developer").equalsIgnoreCase("lineweb")) {
        	name = name.replace("function.database.Persist", "lw.sql.Persist");
        }	
        try {
            // procura pela classe
            Class cl = getUserClass(name);
            if (cl != null) {
                JavaParameter[] params = connector.getInputParameters();
                EngFunction.putJavaParameters(wiMap, params);
                boolean exit = false;
                if (IntFunction.useCompat(cl)) {
                	Class c = Class.forName("br.com.itx.engine.Compat");
                	Method m = c.getMethod("connector", Class.class, ExecuteParams.class);
                	exit = (Boolean) m.invoke(c.newInstance(), cl, wiParams);
                } else {
	                InterfaceConnector i = (InterfaceConnector) cl.newInstance();
	                i.execute(wiParams);
	                exit = i.exit(); 
                }
                if (exit) {
                	wiParams.setRequestAttribute("wiExit", "true");
            	}			
            } else {
                wiParams.getErrorLog().write(name, lblpage,
                    "Class Not Found: " + name);
            }
        } catch (Exception err) {
            wiParams.getErrorLog().write(name, lblpage, err);
			if (!wiParams.getPage().getErrorPageName().equals("")) {
				wiParams.setRequestAttribute("wiException", err);
			}
        } catch (Error err) {
            wiParams.getErrorLog().write(name, lblpage, err);
        }
        writeLog();
    }

    private Class getUserClass(String name) {
        ClassLoader classloader = wiParams.getClassLoader();
        Class cl = null;
        try {
            cl = classloader.loadClass(name);
        } catch (ClassNotFoundException err) {
        	//ignorado
        }
        return cl;
    }
}
