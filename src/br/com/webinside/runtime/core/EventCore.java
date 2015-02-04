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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;

import br.com.webinside.runtime.component.AbstractEvent;
import br.com.webinside.runtime.component.AbstractProject;
import br.com.webinside.runtime.component.EventSelect;
import br.com.webinside.runtime.component.EventUpdate;
import br.com.webinside.runtime.exception.SessionTimeoutException;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WISession;
import br.com.webinside.runtime.xml.Inputter;

/**
 * DOCUMENT ME!
 *
 * @author Luiz Ruiz
 * @version $Revision: 1.1 $
 */
public class EventCore {
    /** Map com as variáveis a serem lidas */
    private Map readMap = new HashMap();
    private static String errorPage;

    /**
     * Método principal
     *
     * @param wiParams Parâmetros do WI
     */
    public void execute(ExecuteParams wiParams) {
	    String tname = Thread.currentThread().getName();
		SimpleDateFormat df = 
			new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
		WISession s = wiParams.getWISession();
        wiParams.getHttpResponse().setContentType("text/xml; charset=ISO-8859-1");
        PrintWriter out = wiParams.getWriter();
		if (s.isNew()) {
	        out.println("###SESSION_EXPIRED###:");
	        out.println(getErrorPage(wiParams));
	        return;
		}
        Function.setThreadName(df.format(new Date()) + 
        		" - Start of EventCore of " + wiParams.getProject().getId());    	
        out.println("<response>");
        if (wiParams.getHttpParameters().containsKey("tmp.wievent.update")) {
            update(wiParams);
        }
        if (wiParams.getHttpParameters().containsKey("tmp.wievent.read")) {
            read(wiParams);
        }
        if (wiParams.getHttpParameters().containsKey("tmp.wievent.select")) {
            select(wiParams);
        }
        readMap.put("wi.sql.query", wiParams.getWIMap().get("wi.sql.query"));
        readMap.put("wi.sql.error", wiParams.getWIMap().get("wi.sql.error"));
        if (!readMap.isEmpty()) {
            out.println("<reads>");
            for (Iterator i = readMap.keySet().iterator(); i.hasNext();) {
                String key = (String) i.next();
                String value = (String) readMap.get(key);
                if (!value.equals("")) {
                    out.println("<input name='" + key + "' value='" + value
                        + "' />");
                }
            }
            out.println("</reads>");
        }
        out.println("</response>");
        out.close();
	    Function.setThreadName(tname);    
    }

    private static String getErrorPage(ExecuteParams wiParams) {
        if (null == errorPage) {
            String path = 
                wiParams.getServletContext().getRealPath("/WEB-INF/web.xml");
            Inputter in = new Inputter();
            Document doc = in.input(new File(path));
            Element root = doc.getRootElement();
            List children = root.getChildren("error-page", root.getNamespace());
            for (Iterator it = children.iterator(); it.hasNext();) {
                Element e = (Element) it.next();
                Element type = e.getChild("exception-type", e.getNamespace());
                if (null == type) {
                    continue;
                }
                String text = type.getText();
                if (SessionTimeoutException.class.getName().equals(text)) {
                    Element location = e.getChild("location", e.getNamespace());
                    if (null != location) {
                        errorPage = "/" + wiParams.getProject().getId() 
                        	+ location.getText();
                        break;
                    }
                }
            }
        }
        return errorPage;
    }

    private void read(ExecuteParams wiParams) {
        String key =
            (String) wiParams.getHttpParameters().get("tmp.wievent.read");
        StringA aux = new StringA();
        aux.setXml(key);
        key = aux.toString().toLowerCase();
        List keys = StringA.pieceAsList(key, ",", 0, 0, false);
        for (int i = 0; i < keys.size(); i++) {
            String id = (String) keys.get(i);
            String value = wiParams.getWIMap().get(id);
            if (!value.equals("")) {
                readMap.put(id, value);
            }
        }
    }

    private void update(ExecuteParams wiParams) {
        AbstractProject project = wiParams.getProject();
        String update =
            (String) wiParams.getHttpParameters().get("tmp.wievent.update");
        List keys = StringA.pieceAsList(update, ",", 0, 0, false);
        for (int i = 0; i < keys.size(); i++) {
            String key = ((String) keys.get(i)).trim();
            wiParams.getWIMap().put("wi.event.id", update);
            if (!project.getEvents().containsKey(key)) {
                wiParams.includeCode("/events/" + key + ".jsp");
            }
            AbstractEvent evt =
                (AbstractEvent) project.getEvents().getElement(key);
            if (!(evt instanceof EventUpdate)) {
                return;
            }
            int quant = 
                new EventCoreUpdate(wiParams, (EventUpdate) evt).execute();
            String obj = ((EventUpdate) evt).getWIObj();
            if (quant > 0) {
                String resp = wiParams.getWIMap().get(obj);
                readMap.put(obj, resp);
                for (int j = 1; j <= quant; j++) {
                    resp = wiParams.getWIMap().get(obj + "." + j);
                    if (!resp.equals("")) {
                    	readMap.put(obj, resp);
                    }	
                }
            }
        }
    }

    private void select(ExecuteParams wiParams) {
        String key =
            (String) wiParams.getHttpParameters().get("tmp.wievent.select");
        key = key.trim();
        wiParams.getWIMap().put("wi.event.id", key);
        AbstractProject project = wiParams.getProject();
        if (!project.getEvents().containsKey(key)) {
            wiParams.includeCode("/events/" + key + ".jsp");
        }
        AbstractEvent evt = (AbstractEvent) project.getEvents().getElement(key);
        if (!(evt instanceof EventSelect)) {
            try {
                wiParams.getHttpResponse().sendError(600);
            } catch (IOException e) {
            }
        } else {
            new EventCoreSelect(wiParams, (EventSelect) evt).execute();
        }
    }
}
