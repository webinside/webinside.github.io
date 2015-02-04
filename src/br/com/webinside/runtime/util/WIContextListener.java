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

package br.com.webinside.runtime.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.jdom.Document;
import org.jdom.Element;

import br.com.webinside.runtime.database.DatabaseManager;
import br.com.webinside.runtime.xml.Inputter;

/**
 * @author Luiz Ruiz (alterado por Geraldo Moraes)
 */
public class WIContextListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {
    	ServletContext sc = event.getServletContext();
    	ClassLoader cl = Thread.currentThread().getContextClassLoader();
    	sc.setAttribute("classloader", cl);
    	if ("true".equalsIgnoreCase(sc.getInitParameter("persistApp"))) { 
    		deserializeApp(sc);
    	}
    	callContextListeners(event, true);
    }

    public void contextDestroyed(ServletContextEvent event) {
    	ServletContext sc = event.getServletContext();
    	if ("true".equalsIgnoreCase(sc.getInitParameter("persistApp"))) { 
    		serializeApp(sc);
    	}
    	closePoolConnetions(sc);
    	closeLogs(sc);
    	callContextListeners(event, false);
    }
    
    private void closeLogs(ServletContext sc) {
        AbstractLog.closeAll();
    }

    private void closePoolConnetions(ServletContext sc) {
    	DatabaseManager.closePoolConnetions();
    }

    private void serializeApp(ServletContext sc) {
        try {
            WIMap appMap = (WIMap) sc.getAttribute("webinside");
        	String path = sc.getRealPath("");
        	File f = new File(path + "/WEB-INF/app.ser");
            if (appMap == null) {
            	f.delete();
            	return;
            }
            FileOutputStream fos = new FileOutputStream(f);            
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(appMap);
            oos.close();
            fos.close();
        } catch (Exception err) {
        	err.printStackTrace();
        }
    }

    private void deserializeApp(ServletContext sc) {
        try {
        	String path = sc.getRealPath("");
        	File f = new File(path + "/WEB-INF/app.ser");
        	if (!f.exists()) return;
            FileInputStream fis = new FileInputStream(f);            
            ObjectInputStream ois = new ObjectInputStream(fis);
            WIMap appMap = (WIMap) ois.readObject();
            sc.setAttribute("webinside", appMap);
            ois.close();
            fis.close();
        } catch (Exception err) { 
        	err.printStackTrace();
        }
    }
   
    private void callContextListeners(ServletContextEvent event, boolean init) {
    	ServletContext sc = event.getServletContext();
    	String path = sc.getRealPath("");
    	File f = new File(path + "/WEB-INF/web-wi.xml");
    	if (!f.exists()) return;
    	Document doc = new Inputter().input(f);
    	if (doc != null) {
    		Element root = doc.getRootElement();
    		List list = root.getChildren("listener");
    		for (Iterator it = list.iterator(); it.hasNext();) {
				Element listener = (Element) it.next();
				try {
					String className = listener.getChildText("listener-class");
					Class clazz = Class.forName(className);
					ServletContextListener scl = 
						(ServletContextListener) clazz.newInstance();
					if (init) {
						scl.contextInitialized(event);
					} else {
						scl.contextDestroyed(event);
					}
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
    	}
    	
    }
    
}
