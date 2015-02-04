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

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.core.GridLinearNavigator;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

/**
 * Classe que recupera uma variável do wi.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.9 $
 */
public class Out extends TagSupport {

	private static final long serialVersionUID = 1L;
	private String var;
	private String debug;

    /**
     * Executa a transformação.
     *
     * @return a flag para não processar o body
     *
     * @throws JspException em caso de uma exceção jsp.
     */
    public int doStartTag() throws JspException {
    	if ("wi.random".equals(getVar())) {
            String key = Function.randomKey();
            try {
    		    pageContext.getOut().print(key);
            } catch (IOException err) {
            	// ignorado
            }
            reset();
    		return SKIP_BODY;
    	}
        Object obj = pageContext.getRequest().getAttribute("wiParams");
        if (obj instanceof ExecuteParams) {
            ExecuteParams wiParams = (ExecuteParams) obj;
    		WIMap wiMap = wiParams.getWIMap();
            try {
                if (getDebug() != null || getVar() == null) {
                	if (getDebug() != null && getDebug().equalsIgnoreCase("full")) {
	            		pageContext.getOut().println("<pre id=\"wioutDebug\">");
	            		pageContext.getOut().print(wiMap.getAsText("*", true));
	            		pageContext.getOut().println("</pre>");
                	} else if (getDebug() != null && getDebug().equalsIgnoreCase("var")) {
	            		pageContext.getOut().println("<pre id=\"wioutDebug\">");
	            		pageContext.getOut().print(wiMap.get("tmp.debug"));
	            		pageContext.getOut().println("</pre>");
                	} else debugDetailed(wiParams, wiMap);	
                } else if (getVar() != null) {
                	String resp = wiMap.get(getVar());
                	HttpServletRequest request = wiParams.getHttpRequest();
                	if (getVar().startsWith("grid.") && 
                			getVar().indexOf(".link") > -1) {
                		request.setAttribute("wiGridHtmlFormOk", "true");
                	}
                	if (getVar().equals("wi.grid.form")) {
                		resp = wiGridHtmlForm(request, wiMap);
                	}
            		pageContext.getOut().print(resp);
                }
            } catch (Exception err) {
            	wiParams.getErrorLog().write("Out", "taglib", err);
            	throw new JspException(err);
            }
        }
        reset();
        return SKIP_BODY;
    }

    private void debugDetailed(ExecuteParams wiParams, WIMap wiMap) throws Exception {
    	PrintWriter out = new PrintWriter(pageContext.getOut());
    	WIMap aux = wiMap.cloneMe();
    	aux.remove("pvt.svlist");
    	aux.remove("wi.date.");
    	aux.remove("wi.header.");
    	aux.remove("wi.request.");
    	aux.remove("wi.server.");
    	aux.remove("wi.i18n");
    	aux.remove("wi.headers");
    	aux.remove("wi.functions");
    	aux.remove("wi.numberlist");
    	aux.remove("wi.servletcontext");
        List<String> keys = new ArrayList(wiMap.keySet());
        for (String key : keys) {
        	String txt = StringA.piece(key, ".", StringA.count(key, '.') + 1);
        	if (key.indexOf(".tag_") > -1) aux.remove(key);
        	if (key.startsWith("grid.")) {
        		if (key.indexOf(".link") > -1) aux.remove(key);
        		if (wiMap.get(key).toLowerCase().trim().startsWith("<table")) {
        			aux.remove(key);
        		}
        	}
        	if (Function.parseInt(txt) > 0) aux.remove(key);
        	if (txt.equals("columnnames()")) aux.remove(key);
		}
		out.println("<div id=\"wioutDebug\">");
		out.println("<pre id=\"wioutTmp\">");
		out.print(aux.getAsText("stmp.*", true));
		out.print(aux.getAsText("tmp.*", true));
		out.print(aux.getAsText("grid.*", true));
		if (wiParams.getProject().isRequestScope()) {
			out.print(aux.getAsText("", true));
		}
		out.println("</pre>");
		out.println("<pre id=\"wioutPvt\">");
		if (!wiParams.getProject().isRequestScope()) {
			out.print(aux.getAsText("", true));
		}
		out.print(aux.getAsText("pvt.*", true));
		out.println("</pre>");
		out.println("<pre id=\"wioutWi\">");
		out.print(aux.getAsText("wi.*", true));
		out.println("</pre>");
		out.println("<div class=\"clear\"></div>");
		out.println("</div>");
    }
    
    private String wiGridHtmlForm(HttpServletRequest req, WIMap wiMap) {
    	if (!wiMap.get("wi.page.parent").equals("")) return "";
    	String wghfOk = (String)req.getAttribute("wiGridHtmlFormOk");
    	if (wghfOk != null && wghfOk.equals("true")) {
        	String resp = (String) req.getAttribute("wiGridHtmlForm");
        	resp = (resp == null ? "" : resp.trim());
        	if (resp.equals("")) {
        		resp = GridLinearNavigator.navForm(wiMap, "");
        	}
        	return resp;
    	}
    	return "";
    }
        
    private void reset() {
    	var = null;
    	debug = null;
    }
    
    /**
     * Retorna a variável wi que retornará o valor.
     *
     * @return a variável wi que retornará o valor.
     */
    public String getVar() {
        return var;
    }

    /**
     * Define a variável wi que retornará o valor.
     *
     * @param string a variável wi que retornará o valor.
     */
    public void setVar(String string) {
        var = string;
    }

    /**
     * Retorna se está no modo debug e qual tipo de debug.
     *
     * @return o debug a ser utilizado.
     */
	public String getDebug() {
		return debug;
	}

    /**
     * Define se está no modo debug e qual tipo de debug.
     *
     * @param string o debug a ser utilizado.
     */
	public void setDebug(String debug) {
		this.debug = debug;
	}
	
}
