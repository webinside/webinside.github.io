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
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContext;

import br.com.webinside.runtime.util.FileIO;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class Constants {

    public static void populate(ServletContext sContext, WIMap wiMap) {
    	if (sContext == null) return;
    	ConstantsNode node = (ConstantsNode) sContext.getAttribute("wi.constants");
    	if (node == null) {
    		node = (new Constants()).new ConstantsNode();
    		sContext.setAttribute("wi.constants", node);
    	}
    	String dir = sContext.getRealPath("/WEB-INF");
    	File xml = new File(dir, "constants.xml");    	
    	if (xml.lastModified() > node.timeXml) {
    		node.timeXml = xml.lastModified();
    		node.constantsXml = populateFromXml(xml);
    	}
    	File props = new File(dir, "constants.props");    	
    	if (props.lastModified() > node.timeProps) {
    		node.timeProps = props.lastModified();
    		node.constantsProps = populateFromProps(props);
    	}
       	wiMap.putAll(node.constantsXml);
       	wiMap.putAll(node.constantsProps);
    }

    public static void populate(String dir, WIMap wiMap) {
    	File xml = new File(dir, "constants.xml");
   		Map<String, String> mapXml = populateFromXml(xml);
   		wiMap.putAll(mapXml);
    	File props = new File(dir, "constants.props");
   		Map<String, String> mapProps = populateFromProps(props);
   		wiMap.putAll(mapProps);
    }
    
    private static Map<String, String> populateFromXml(File xmlFile) {
    	Map<String, String> map = new HashMap<String, String>();
    	if (xmlFile.isFile()) {
    		WIMap wiMap = new WIMap();
        	FileIO fio = new FileIO(xmlFile.getAbsolutePath(), FileIO.READ);
        	new CoreXmlImport(wiMap, "").execute(fio.readText());
        	map = wiMap.getAsMap();
        	filterMap(map);
    	}
    	return map;
    }
    
    private static Map<String, String> populateFromProps(File propsFile) {
    	Map<String, String> map = new HashMap<String, String>();
    	if (propsFile.isFile()) {
    		Properties props = new Properties();
        	try {
        		InputStream in = new FileInputStream(propsFile);
        		props.load(in);
        	} catch (IOException e) {
        		// ignorado
        	}
        	for (String key : props.stringPropertyNames()) {
        		map.put(key, props.getProperty(key).trim());
        	}
        	filterMap(map);
    	}
    	return map;
    }
    
    private static void filterMap(Map<String,String> map) {
    	for (String key : new HashSet<String>(map.keySet())) {
    		key = key.toLowerCase().trim();
    		String newKey = "";
			if (key.startsWith("wi.")) newKey = StringA.change(key, "wi.", "tmp.wi_");
			if (key.startsWith("wi_")) newKey = StringA.change(key, "wi_", "tmp.wi_");
			if (key.startsWith("grid_")) newKey = StringA.change(key, "grid_", "grid.");
			if (key.startsWith("combo_")) newKey = StringA.change(key, "combo_", "combo.");
			if (key.startsWith("tmp_")) newKey = StringA.change(key, "tmp_", "tmp.");
			if (key.startsWith("stmp_")) newKey = StringA.change(key, "stmp_", "stmp.");
			if (key.startsWith("pvt_")) newKey = StringA.change(key, "pvt_", "pvt.");
			if (key.startsWith("app_")) newKey = StringA.change(key, "app_", "app.");
			if (!newKey.equals("")) {
				map.put(newKey, map.remove(key));
			}
		}
    }
    
    private class ConstantsNode {
    	
    	private Map<String, String> constantsXml = new HashMap<String, String>();
    	private Map<String, String> constantsProps = new HashMap<String, String>();
    	private long timeXml = 0;
    	private long timeProps = 0;
    	
    }
    
}
