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

package br.com.webinside.runtime.component.taglib;

import java.io.File;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import br.com.webinside.runtime.core.CoreCommonInterface;
import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.exception.GenericConnectionException;
import br.com.webinside.runtime.util.FileIO;
import br.com.webinside.runtime.util.WIMap;

/**
 * Classe que implementa um TagLib para executar uma lógica do WI (Core).
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.3 $
 */
public class ExecuteCore extends TagSupport {

	private static final long serialVersionUID = 1L;
	private String type;
    private String name;
    private boolean exit;

    /**
     * Executa a função.
     *
     * @return a flag para não processar o body
     *
     * @throws JspException em caso de uma exceção jsp.
     */
    public int doStartTag() throws JspException {
        exit = false;
        Object obj = pageContext.getRequest().getAttribute("wiParams");
        if (obj instanceof ExecuteParams) {
            ExecuteParams wiParams = (ExecuteParams) obj;
            try {
                Object jspElement = pageContext.getAttribute(getName());
            	String timestamp = "";
            	String description = "";
            	if (wiParams.getWIMap() != null) {
                	String debug = wiParams.getWIMap().get("pvt.debug.core").trim(); 
                    if (!debug.equalsIgnoreCase("true")) {
                    	debug = wiParams.getWIMap().get("tmp.debug.core").trim();
                    }
        			if (debug.equalsIgnoreCase("true")) {
        				Thread.sleep(100);
                        SimpleDateFormat sdf = 
                        	new SimpleDateFormat("yyyyMMddHHmmssSSSS");
                        timestamp = sdf.format(new Date());
                        description = coreDescription(jspElement);
                	}
            	}
            	long initTime = new Date().getTime();
                if (!timestamp.equals("")) {
                	debugCore(wiParams, description, true, timestamp, 0);
                }
                String fname = "br.com.webinside.runtime.core.Core" + getType();
                if (fname.endsWith("CoreReport")) {
                	fname = fname.replace(".core.", ".report.");
                }
                Class cl = Class.forName(fname);
                Class[] args = new Class[2];
                args[0] = ExecuteParams.class;
                boolean isAbstract = false;
                if (getType().equals("Cookie") || getType().equals("Redir")
                            || getType().equals("FileList")
                            || getType().equals("FileRemove")) {
                    isAbstract = true;
                }
                if (isAbstract) {
                    String absName = "Abstract" + getType();
                    args[1] = Class.forName("br.com.webinside.runtime.component." + absName);
                } else {
                    args[1] = jspElement.getClass();
                }
                Object[] initargs = new Object[] { wiParams, jspElement };
                Object aux = cl.getConstructor(args).newInstance(initargs);
                CoreCommonInterface cci = (CoreCommonInterface)aux;
                cci.execute();
                if (wiParams.getRequestAttribute("wiException") != null) {
                    Exception ex =
                        (Exception) wiParams.getRequestAttribute("wiException");
                    if (!(ex instanceof GenericConnectionException)) {
                        throw ex;
                    }
                }
                if (!timestamp.equals("")) {
                	debugCore(wiParams, description, false, timestamp, initTime);
                }
                String wiExit = (String) wiParams.getRequestAttribute("wiExit");
                if ((wiExit != null) && (wiExit.equals("true"))) {
                    exit = true;
                }
            } catch (Exception err) {
                wiParams.getErrorLog().write("ExecuteCore", "taglib", err);
            	wiParams.removeRequestAttribute("wiException");
                throw new JspException(err);
            }
        }
        return SKIP_BODY;
    }

    /**
     * Finaliza a tag.
     *
     * @return se a página deve ser processada.
     *
     * @throws JspException em caso de uma exceção jsp.
     */
    public int doEndTag() throws JspException {
        return ((exit) ? SKIP_PAGE
                       : EVAL_PAGE);
    }

    /**
     * Name indica o nome do objeto a ser utilizado.
     *
     * @return o nome do objeto a ser utilizado.
     */
    public String getName() {
        return name;
    }

    /**
     * Name indica o nome do objeto a ser utilizado.
     *
     * @param name indica o nome do objeto a ser utilizado.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Tipo de lógica a executar.
     *
     * @return o tipo de lógica a executar.
     */
    public String getType() {
        return type;
    }

    /**
     * Tipo de lógica a executar.
     *
     * @param type indica o tipo de lógica a executar.
     */
    public void setType(String type) {
        this.type = type;
    }
    
    private void debugCore(ExecuteParams wiParams, String description,
    		boolean before, String timestamp, long initTime) {
    	WIMap wiMap = wiParams.getWIMap();
    	String filter = wiMap.get("pvt.core.filter");
    	if (filter.trim().equals("")) {
    		filter = "*";
    	}
    	String reqtime = wiMap.get("wi.request.timestamp");
    	String session = wiMap.get("wi.session.id");
    	String logsDir = wiParams.getErrorLog().getParentDir();
    	File dest = new File(logsDir, "/debug-" + reqtime + "-" + session);
    	dest.mkdirs();
    	String f = dest.getAbsolutePath() + "/element-" + timestamp + "-" + type;
    	if (before) {
    		f += "_1.txt";
    	} else {
    		f += "_2.txt";
    	}
    	StringBuffer resp = new StringBuffer();
    	resp.append("# " + description);
		long now = new Date().getTime();
    	if (!wiMap.get("wi.debug.core.status").equals("")) {
        	resp.append(" (not processed)");
        	wiMap.remove("wi.debug.core.status");
    	} else {
        	if (initTime > 0 && now - initTime > 1000) {
            	resp.append(" (" + ((now - initTime) / 1000) + " seg)");
        	} else if (initTime > 0) {
            	resp.append(" (less than 1 seg)");
        	}
    	}
    	resp.append("\r\n\r\n");
    	StringTokenizer st = new StringTokenizer(filter, ",");
    	while (st.hasMoreTokens()) {
    		String token = st.nextToken().trim().toLowerCase();
    		resp.append(wiMap.getAsText(token, false));
    	}
    	new FileIO(f,'W').writeText(resp.toString());
    }

    private String coreDescription(Object jspElement) {
    	try {
    		Method m = jspElement.getClass().getMethod("getDescription");
    		if (m != null) {
    			Object desc = m.invoke(jspElement);
    			if (desc != null) {
    				return desc.toString();
    			}
    		}
    	} catch (Throwable t) { 
    		// ignorado
    	}
    	return "";
    }
    
}
