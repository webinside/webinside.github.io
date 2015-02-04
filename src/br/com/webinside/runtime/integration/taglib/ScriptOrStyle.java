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

package br.com.webinside.runtime.integration.taglib;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.integration.Producer;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.WIMap;
import br.com.webinside.runtime.util.WISession;

/**
 * Classe que implementa um TagLib para a função Script ou Style.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.2 $
 */
public abstract class ScriptOrStyle extends TagSupport {

	private static final long serialVersionUID = 1L;
	
	public static final String WI_LIST_KEY = "wiScriptOrStyleList";
	public static final String WI_RND_KEY = "wiScriptOrStyleRnd";
	
	protected String type;
	private String path;
	private String media;
	private String useParent;
	private boolean rnd;
	
    /**
     * Carrega um script caso ele exista e ainda não tenha sido carregado.
     *
     * @return a flag para não processar o body
     *
     * @throws JspException em caso de uma exceção jsp.
     */
    public int doStartTag() throws JspException {
    	ServletRequest req = pageContext.getRequest(); 
        Object obj = req.getAttribute("wiParams");
        if (obj instanceof ExecuteParams) {
            ExecuteParams wiParams = (ExecuteParams) obj;
            try {
            	List<String> list = (List) req.getAttribute(WI_LIST_KEY);
            	if (list == null) {
            		list = new ArrayList<String>();
            		req.setAttribute(WI_LIST_KEY, list);
            	}
            	if (path == null) {
            		path = "/|wi.proj.id|/|wi.page.id|.js";
            		if (type.equals("style")) {
                		path = "/|wi.proj.id|/|wi.page.id|.css";
            		}
            		if (useParent != null && 
            				useParent.trim().equalsIgnoreCase("true")) {
            			path = "/|wi.proj.id|/|wi.page.parent|.js";
                		if (type.equals("style")) {
                    		path = "/|wi.proj.id|/|wi.page.parent|.css";
                		}
            		}
            	}
            	WIMap wiMap = wiParams.getWIMap();
        		path = Producer.execute(wiMap, path).trim();
            	boolean ok = true;
            	if (!path.startsWith("http:")) {
            		if (!path.startsWith("/")) {
            			path = "/" + wiMap.get("wi.proj.id") + "/"  + path;
            		}
            		ServletContext sc = pageContext.getServletContext(); 
            		String webapps = new File(sc.getRealPath("")).getParent();
            		if (!new File(webapps, path).isFile()) ok = false;
            	}
            	if (list.contains(path)) ok = false;
            	if (ok) {
            		list.add(path);
            		JspWriter pw = pageContext.getOut();
            		if (rnd) path += "?rnd=" + getRND(wiParams);
            		if (type.equals("script")) {
            			pw.print("<script type='text/javascript'"); 
            			pw.print(" src='" + path + "'>//</script>");
            		} else {
            			pw.print("<link type='text/css' rel='stylesheet'");
            			if (media != null && !media.trim().equals("")) {
            				pw.print(" media='" + media + "'");
            			}
            			pw.print(" href='" + path + "'/>");
            		}
            	}
            } catch (Exception err) {
            	wiParams.getErrorLog().write("Script", "taglib", err);
            	throw new JspException(err);
            }
        }
        reset();
        return SKIP_BODY;
    }
    
    private String getRND(ExecuteParams wiParams) {
		String rnd = null;
		WISession session = wiParams.getWISession();
		synchronized (session.getHttpSession()) {
			rnd = (String) session.getAttribute(WI_RND_KEY);
			if (rnd == null) {
				rnd = Function.randomKey(10);
				session.setAttribute(WI_RND_KEY, rnd);
			}
		}
    	return rnd;
    }
    
    private void reset() {
    	type = null;
		path = null;
		media = null;
	    useParent = null;
	    rnd = false;
    }

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getMedia() {
		return media;
	}

	public void setMedia(String media) {
		this.media = media;
	}

	public String getUseParent() {
		return useParent;
	}

	public void setUseParent(String useParent) {
		this.useParent = useParent;
	}

	public boolean isRnd() {
		return rnd;
	}

	public void setRnd(boolean rnd) {
		this.rnd = rnd;
	}
    
}
