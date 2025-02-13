/*
 * WEBINSIDE - Ferramenta de produtividade Java
 * Copyright (c) 2011-2012 LINEWEB Solu��es Tecnol�gicas Ltda.
 * Copyright (c) 2009-2010 Inc�gnita Intelig�ncia Digital Ltda.
 *
 * Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 * sob os termos da GNU LESSER GENERAL PUBLIC LICENSE (LGPL) conforme publicada 
 * pela Free Software Foundation; vers�o 2.1 da Licen�a.
 * Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 * NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 * ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 * 
 * Consulte a GNU LGPL para mais detalhes.
 * Voc� deve ter recebido uma c�pia da GNU LGPL junto com este programa; se n�o, 
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
 * @version $Revision: 1.9 $
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
                RtmFunction.putJavaParameters(wiMap, params);
                boolean exit = false;
                if (IntFunction.useCompat(cl)) {
                	Class c = Class.forName("br.com.itx.engine.CompatImpl");
                	Method m = c.getMethod("connector", Class.class, ExecuteParams.class);
                	exit = (Boolean) m.invoke(c.getConstructor().newInstance(), cl, wiParams);
                } else {
	                InterfaceConnector i = (InterfaceConnector) cl.getConstructor().newInstance();
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
        } catch (Throwable err) {
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
