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
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;

import br.com.webinside.runtime.util.FileIO;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class Constants {

    /**
     * DOCUMENT ME!
     *
     * @param sContext DOCUMENT ME!
     * @param wiMap DOCUMENT ME!
     *
     */
    public static void populate(ServletContext sContext, WIMap wiMap) {
    	if (sContext == null) return;
    	Map constants = (Map) sContext.getAttribute("wi.constants");
    	if (constants == null) {
    		constants = new HashMap();
    		sContext.setAttribute("wi.constants", constants);
    		sContext.setAttribute("wi.constants.time", new Long(0));
    	}
    	Long time = (Long) sContext.getAttribute("wi.constants.time");
    	File file = new File(sContext.getRealPath("/WEB-INF/constants.xml"));    	
    	if (file.isFile() && file.lastModified() != time.longValue()) {
        	time = new Long(file.lastModified());
    		WIMap aux = new WIMap();
        	populate(file, aux);
        	constants = aux.getAsMap();
    		sContext.setAttribute("wi.constants", constants);
    		sContext.setAttribute("wi.constants.time", time);
    	}
       	wiMap.putAll(constants);
    }

    /**
     * DOCUMENT ME!
     *
     * @param file DOCUMENT ME!
     * @param wiMap DOCUMENT ME!
     *
     */
    public static void populate(File file, WIMap wiMap) {
    	if (file.isFile()) {
        	FileIO fio = new FileIO(file.getAbsolutePath(), FileIO.READ);
        	String content = fio.readText();
        	content = StringA.change(content, "wi.", "tmp.wi_");
        	content = StringA.change(content, "wi_", "tmp.wi_");
        	content = StringA.change(content, "grid_", "grid.");
        	content = StringA.change(content, "tmp_", "tmp.");
        	content = StringA.change(content, "pvt_", "pvt.");
        	content = StringA.change(content, "app_", "app.");
        	new CoreXmlImport(wiMap, "").execute(content);
    	}
    }
}
