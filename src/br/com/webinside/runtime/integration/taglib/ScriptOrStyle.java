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

package br.com.webinside.runtime.integration.taglib;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import org.apache.commons.codec.digest.DigestUtils;

import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.integration.Producer;
import br.com.webinside.runtime.util.WIMap;

/**
 * Classe que implementa um TagLib para a fun��o Script ou Style.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.6 $
 */
public abstract class ScriptOrStyle extends TagSupport {

	private static final long serialVersionUID = 1L;
	
	public static final String WI_REQ_LIST_KEY = "wiScriptOrStyleReqList";
	
	protected String type;
	private String path;
	private String media;
	private String useParent;
	private boolean rnd = true;
	
    /**
     * Carrega um script caso ele exista e ainda n�o tenha sido carregado.
     *
     * @return a flag para n�o processar o body
     *
     * @throws JspException em caso de uma exce��o jsp.
     */
    public int doStartTag() throws JspException {
    	HttpServletRequest req = (HttpServletRequest) pageContext.getRequest(); 
    	String sId = req.getSession(false).getId();
        Object obj = req.getAttribute("wiParams");
        if (obj instanceof ExecuteParams) {
            ExecuteParams wiParams = (ExecuteParams) obj;
            try {
            	boolean emptyPath = (path == null);
            	List<String> list = (List) req.getAttribute(WI_REQ_LIST_KEY);
            	if (list == null) {
            		list = new ArrayList<String>();
            		req.setAttribute(WI_REQ_LIST_KEY, list);
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
            	long fileTime = 0;
            	WIMap wiMap = wiParams.getWIMap();
        		path = Producer.execute(wiMap, path).trim();
            	boolean ok = true;
            	if (!path.startsWith("http")) {
            		if (!path.startsWith("/")) {
            			path = "/" + wiMap.get("wi.proj.id") + "/"  + path;
            		}
            		ServletContext sc = pageContext.getServletContext(); 
            		String webapps = new File(sc.getRealPath("")).getParent();
            		File file = new File(webapps, path); 
            		fileTime = file.lastModified();
            		if (fileTime == 0 && emptyPath) ok = false;
            	}
            	if (list.contains(path)) ok = false;
            	if (ok) {
            		list.add(path);
            		JspWriter pw = pageContext.getOut();
            		if (rnd) {
            	    	String rnd = DigestUtils.md5Hex((fileTime + "").getBytes());
            			if (fileTime == 0) rnd = sId;
            			path += "?rnd=" + rnd.toLowerCase().substring(0, 10);
            		}
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
    
    private void reset() {
    	type = null;
		path = null;
		media = null;
	    useParent = null;
	    rnd = true;
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
