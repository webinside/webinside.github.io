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
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jdom.Document;
import org.jdom.Element;

import br.com.webinside.runtime.component.AbstractProject;
import br.com.webinside.runtime.component.Page;
import br.com.webinside.runtime.integration.DatabaseAliases;
import br.com.webinside.runtime.integration.Producer;
import br.com.webinside.runtime.net.FileUpload;
import br.com.webinside.runtime.util.ErrorLog;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;
import br.com.webinside.runtime.util.WISession;
import br.com.webinside.runtime.xml.Inputter;
import br.com.webinside.runtime.xml.XMLFunction;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.3 $
 */
public class ExecuteParams {
	
    /** DOCUMENT ME! */
    public final static int HTTP_REQUEST = 1;
    /** DOCUMENT ME! */
    public final static int HTTP_RESPONSE = 2;
    /** DOCUMENT ME! */
    public final static int SERVLET_CONTEXT = 3;
    /** DOCUMENT ME! */
    public final static int CLASSLOADER = 4;
    /** DOCUMENT ME! */
    public final static int OUT_STREAM = 5;
    /** DOCUMENT ME! */
    public final static int OUT_WRITER = 6;
    /** DOCUMENT ME! */
    public final static int WI_SESSION = 7;
    /** DOCUMENT ME! */
    public final static int DATABASE_ALIASES = 8;
    /** DOCUMENT ME! */
    public final static int FILE_UPLOAD = 9;
    /** DOCUMENT ME! */
    public final static int PROJECT = 10;
    /** DOCUMENT ME! */
    public final static int PAGE = 11;
    /** DOCUMENT ME! */
    public final static int PRODUCER = 12;
    /** DOCUMENT ME! */
    public final static int ERROR_LOG = 13;
    /** DOCUMENT ME! */
    public final static int WI_MAP = 14;
    /** DOCUMENT ME! */
    public final static int WI_CVS = 15;

    // parametros da Thread
    private static ThreadLocal thisParam = new ThreadLocal();
	private static ThreadLocal classLoader = new ThreadLocal();
	
	// parametros do ExecuteParams
    private Map parameters = new HashMap();

    // parametros de compatibilidade com o request antigo do WI
    private Map httpParameters;

    /**
     * Creates a new ExecuteParams object.
     */
    public ExecuteParams() {
        setParameter(PRODUCER, new Producer());
        setParameter(DATABASE_ALIASES, new DatabaseAliases());
        getDatabaseAliases().setWIParams(this);
        setParameter(CLASSLOADER, getClass().getClassLoader());
        // compatibilidade
        httpParameters = new LinkedHashMap();
    }

    /**
     * Creates a new ExecuteParams object.
     *
     * @param request DOCUMENT ME!
     * @param response DOCUMENT ME!
     * @param application DOCUMENT ME!
     */
    public ExecuteParams(HttpServletRequest request,
        HttpServletResponse response, ServletContext application) {
        setParameter(PRODUCER, new Producer());
        setParameter(DATABASE_ALIASES, new DatabaseAliases());
        getDatabaseAliases().setWIParams(this);
        setParameter(HTTP_REQUEST, request);
        setParameter(HTTP_RESPONSE, response);
        setParameter(SERVLET_CONTEXT, application);
        setParameter(WI_SESSION, new WISession(request.getSession()));
        set(this);
        // compatibilidade
        httpParameters = new LinkedHashMap();
        setHttpParameters();
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     * @param obj DOCUMENT ME!
     */
    public void setParameter(int type, Object obj) {
        if (obj == null) {
            parameters.remove(type + "");
            return;
        }
        if (type == HTTP_REQUEST) {
            if (obj instanceof HttpServletRequest) {
                parameters.put(HTTP_REQUEST + "", obj);
                if (!parameters.containsKey(WI_CVS + "")) {
                    String cvs = getHttpRequest().getRequestURI();
                    if ((cvs != null) && (cvs.indexOf("WI-CVS") > -1)) {
                        cvs = StringA.piece(cvs.trim(), "/", 3, 4);
                        parameters.put(WI_CVS + "", "/" + cvs);
                    }
                }
            }
        } else if (type == HTTP_RESPONSE) {
            if (obj instanceof HttpServletResponse) {
                parameters.put(HTTP_RESPONSE + "", obj);
            }
        } else if (type == SERVLET_CONTEXT) {
            if (obj instanceof ServletContext) {
                parameters.put(SERVLET_CONTEXT + "", obj);
            }
        } else if (type == CLASSLOADER) {
            if (obj instanceof ClassLoader) {
                parameters.put(CLASSLOADER + "", obj);
                classLoader.set(obj);
            }
        } else if (type == OUT_STREAM) {
            if (obj instanceof OutputStream) {
                parameters.put(OUT_STREAM + "", obj);
            }
        } else if (type == OUT_WRITER) {
            if (obj instanceof Writer) {
                parameters.put(OUT_WRITER + "", obj);
            }
        } else if (type == WI_SESSION) {
            if (obj instanceof WISession) {
                parameters.put(WI_SESSION + "", obj);
            }
        } else if (type == DATABASE_ALIASES) {
            if (obj instanceof DatabaseAliases) {
                parameters.put(DATABASE_ALIASES + "", obj);
            }
        } else if (type == FILE_UPLOAD) {
            if (obj instanceof FileUpload) {
                parameters.put(FILE_UPLOAD + "", obj);
            }
        } else if (type == PROJECT) {
            if (obj instanceof AbstractProject) {
                AbstractProject prj = (AbstractProject) obj;
                parameters.put(PROJECT + "", prj);
                getDatabaseAliases().setLog(prj.getSqlLog());
            }
        } else if (type == PAGE) {
            if (obj instanceof Page) {
                parameters.put(PAGE + "", obj);
            }
        } else if (type == PRODUCER) {
            if (obj instanceof Producer) {
                parameters.put(PRODUCER + "", obj);
            }
        } else if (type == ERROR_LOG) {
            if (obj instanceof ErrorLog) {
                parameters.put(ERROR_LOG + "", obj);
            }
        } else if (type == WI_MAP) {
            if (obj instanceof WIMap) {
                parameters.put(WI_MAP + "", obj);
            }
        }
    }
    
    /**
     * DOCUMENT ME!
     */
    public void setErrorLog() {
    	setErrorLog(null);
    }	
    
    /**
     * DOCUMENT ME!
     *
     * @param constants DOCUMENT ME!
     */
    public void setErrorLog(WIMap constants) {
    	ServletContext application = getServletContext();
    	AbstractProject project = getProject();
    	if (project != null && !project.getlogsdir().equals("")) {
    		String logsDir = project.getlogsdir();
    		if (constants != null) {
        		logsDir = Producer.execute(constants, logsDir);
    		}
    		if (!logsDir.equals("")) {
	    		ErrorLog errorLog = ErrorLog.getInstance(logsDir);
	            setParameter(ERROR_LOG, errorLog);
	            return;
    		}    
    	} 
    	if (application != null) {
        	String path = application.getRealPath("");
        	String logsDir = path + "/WEB-INF/logs";
        	if (path.endsWith("wireport")) {
                File rptDef = new File(path + "/WEB-INF/conf/report.def");
                if (rptDef.isFile()) {
                    Document doc = new Inputter().input(rptDef);
                    if (doc != null) {
                    	Element root = doc.getRootElement();
                    	String rptDir = XMLFunction.getElemValue(root, "LOGSDIR");
                    	if (!rptDir.equals("")) {
                    		logsDir = rptDir;
                    	}
                    }
                }
        	}
    		if (!logsDir.equals("")) {
	            ErrorLog errorLog = ErrorLog.getInstance(logsDir);
	            setParameter(ERROR_LOG, errorLog);
    		}    
        }
    }	
    
    /**
     * DOCUMENT ME!
     *
     * @param mime DOCUMENT ME!
     */
    public void setContentType(String mime) {
        HttpServletResponse response = getHttpResponse();
        if (response == null) {
            return;
        }
        if (mime == null) {
            mime = "";
        }
        if (mime.indexOf("/") > -1) {
            try {
            	response.setContentType(mime);
            } catch (Exception err) { }
        } else {
	        String resp = MimeType.get(mime);
	        if (resp.equals("")) {
	            resp = "text/plain";
	        }
	        if (resp.equals("text/plain") || resp.equals("text/html")) {
	        	resp += ";charset=iso-8859-1";
	        }
	        try {
	            response.setContentType(resp);
	        } catch (Exception err) { }
        }     
    }

    /**
     * DOCUMENT ME!
     *
     * @param length DOCUMENT ME!
     */
    public void setContentLength(int length) {
        HttpServletResponse response = getHttpResponse();
        if ((response != null) && (length > 0)) {
            response.setContentLength(length);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param code DOCUMENT ME!
     */
    public void sendError(int code) {
        if (getHttpResponse() == null) {
            return;
        }
        try {
            getHttpResponse().sendError(code);
        } catch (IOException err) {
            System.err.println(getClass().getName() + ": " + err);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param url DOCUMENT ME!
     * @param wiMap DOCUMENT ME!
     * @param fromProj DOCUMENT ME!
     */
    public void sendRedirect(String url, WIMap wiMap, boolean fromProj) {
        if ((url == null) || (url.equals(""))) {
            return;
        }
        url = StringA.change(url, '\\', '/');
        if (!url.trim().toLowerCase().startsWith("http://")) {
            AbstractProject proj = getProject();
            if (proj != null) {
            	String auxUrl = StringA.piece(url, "?", 1);
                if (auxUrl.toLowerCase().endsWith(".wsp")) {
                    String redirID = Function.randomKey();
                    if (wiMap != null) {
                        wiMap.put("wi.redirect", redirID);
                    }
                    if (url.indexOf("?") == -1) {
                        url = url + "?";
                    }
                    if (!url.endsWith("&") && (!url.endsWith("?"))) {
                        url = url + "&";
                    }
                    url = url + "wi.redirect=" + redirID;
                }
                if (fromProj) {
                    if (!url.startsWith("/")) {
                        url = "/" + url;
                    }
                    url = "/" + proj.getId() + url;
                }
            }
        }
    	wiMap.put("wi.redir.proj", wiMap.get("wi.proj.id"));
    	wiMap.put("wi.redir.page", wiMap.get("wi.page.id"));
    	if (wiMap.get("wi.page.type").equals("pos")) {
        	wiMap.put("wi.redir.proj", wiMap.get("wi.proj.prev"));
        	wiMap.put("wi.redir.page", wiMap.get("wi.page.prev"));
    	}
        getHttpRequest().setAttribute("wiRedirect", url);
        getHttpRequest().setAttribute("wiExit", "true");        
    }

    // Gets dos Parametros
    public HttpServletRequest getHttpRequest() {
        return (HttpServletRequest) parameters.get(HTTP_REQUEST + "");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public HttpServletResponse getHttpResponse() {
        return (HttpServletResponse) parameters.get(HTTP_RESPONSE + "");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ServletContext getServletContext() {
        return (ServletContext) parameters.get(SERVLET_CONTEXT + "");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ClassLoader getClassLoader() {
        return (ClassLoader) parameters.get(CLASSLOADER + "");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static ClassLoader getThreadClassLoader() {
        return (ClassLoader) classLoader.get();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public PrintStream getOutputStream() {
        return getOutputStream(true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param tryOpen DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public PrintStream getOutputStream(boolean tryOpen) {
        OutputStream s = (OutputStream) parameters.get(OUT_STREAM + "");
        if (tryOpen) {
            if (s == null) {
                if (getHttpResponse() != null) {
                    try {
                        s = getHttpResponse().getOutputStream();
                    } catch (Exception err) {
                    	System.err.println(getClass().getName() + ": " + err);
                    }
                    if (s != null) {
                        parameters.put(OUT_STREAM + "", s);
                    }
                }
            }
        }
        if (s == null) {
            return null;
        }
        return new PrintStream(s);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public PrintWriter getWriter() {
        return getWriter(true);
    }

    /**
     * DOCUMENT ME!
     *
     * @param tryOpen DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public PrintWriter getWriter(boolean tryOpen) {
        Writer w = (Writer) parameters.get(OUT_WRITER + "");
        if (tryOpen) {
            if (w == null) {
                if (getHttpResponse() == null) {
                    return null;
                }
                try {
                    w = getHttpResponse().getWriter();
                } catch (Exception err) {
                	// ignorado.
                }
                if (w != null) {
                    parameters.put(OUT_WRITER + "", w);
                }
            }
        }
        if (w == null) {
            return null;
        }
        if (w instanceof PrintWriter) {
        	return (PrintWriter)w;
        }
        return new PrintWriter(w);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public WISession getWISession() {
        return (WISession) parameters.get(WI_SESSION + "");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public DatabaseAliases getDatabaseAliases() {
        return (DatabaseAliases) parameters.get(DATABASE_ALIASES + "");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public FileUpload getFileUpload() {
        return (FileUpload) parameters.get(FILE_UPLOAD + "");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AbstractProject getProject() {
        return (AbstractProject) parameters.get(PROJECT + "");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Page getPage() {
        return (Page) parameters.get(PAGE + "");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Producer getProducer() {
        return (Producer) parameters.get(PRODUCER + "");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ErrorLog getErrorLog() {
    	return (ErrorLog) parameters.get(ERROR_LOG + "");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public WIMap getWIMap() {
        return (WIMap) parameters.get(WI_MAP + "");
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     * @param obj DOCUMENT ME!
     */
    public void setRequestAttribute(String name, Object obj) {
        HttpServletRequest request = getHttpRequest();
        if ((request == null) || (name == null) || (obj == null)) {
            return;
        }
        request.setAttribute(name, obj);
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Object getRequestAttribute(String name) {
        HttpServletRequest request = getHttpRequest();
        if ((request == null) || (name == null)) {
            return null;
        }
        return request.getAttribute(name);
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     */
    public void removeRequestAttribute(String name) {
        HttpServletRequest request = getHttpRequest();
        if ((request == null) || (name == null)) {
            return;
        }
        request.removeAttribute(name);
    }

    /**
     * DOCUMENT ME!
     *
     * @param page DOCUMENT ME!
     */
    public void includePrePage(Page page) {
        includePrePage(page, false);
    }

    /**
     * DOCUMENT ME!
     *
     * @param page DOCUMENT ME!
     * @param isWebService DOCUMENT ME!
     */
    protected void includePrePage(Page page, boolean isWebService) {
        if (getServletContext() == null) {
            return;
        }
        Page origpage = getPage();
        Producer producer = getProducer();
        setParameter(ExecuteParams.PAGE, page);
        setParameter(ExecuteParams.PRODUCER, new Producer());
        String preJSP = page.getId() + "_pre.jsp";
        if (!preJSP.startsWith("/")) {
            preJSP = "/" + preJSP;
        }
        getHttpRequest().setAttribute("wiParams", this);
        try {
        	String resource = getWICVS() + preJSP;
        	File f = new File(getServletContext().getRealPath(resource));
        	if (f.exists() || Execute.jspList.contains(resource)) { 
        		RequestDispatcher rd = 
        			getServletContext().getRequestDispatcher(resource);
        		rd.include(getHttpRequest(), getHttpResponse());
        	}	
        } catch (Exception err) {
            err.printStackTrace(System.err);
        }
        Exception ex = (Exception) getHttpRequest().getAttribute("wiException");
        if (isWebService && (ex != null)) {
            getWriter().println("O servidor não pode atender esta requisição");
            if (getErrorLog() != null) {
                getErrorLog().write("ExecuteParams", "prePageError: " + preJSP,
                    ex);
            }
        }
        setParameter(ExecuteParams.PAGE, origpage);
        setParameter(ExecuteParams.PRODUCER, producer);
    }

    /**
     * DOCUMENT ME!
     *
     * @param jspPage DOCUMENT ME!
     */
    public void includeCode(String jspPage) {
        if (getServletContext() == null) {
            return;
        }
        if (!jspPage.endsWith(".jsp")) {
            jspPage = jspPage + ".jsp";
        }
        if (!jspPage.startsWith("/")) {
            jspPage = "/" + jspPage;
        }
        try {
        	String resource = getWICVS() + jspPage;
        	File f = new File(getServletContext().getRealPath(resource));
        	if (f.exists() || Execute.jspList.contains(resource)) { 
        		RequestDispatcher rd = 
        			getServletContext().getRequestDispatcher(resource);
        		rd.include(getHttpRequest(), getHttpResponse());
        	}	
        } catch (Exception err) {
            System.err.println(getClass().getName() + ": " + err);
        }
    }
    
    /**
     * Remove todos os arquivos temporarios do upload
     */
    public void clear() {
    	if (getFileUpload() != null) {
    		getFileUpload().removeFiles();
    	}
    	HttpServletRequest request = getHttpRequest();
    	if (request != null) request.removeAttribute("wiParams");
		parameters.clear();
		httpParameters.clear();
    }
    
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getWICVS() {
        String cvs = (String) parameters.get(WI_CVS + "");
        if (cvs == null) {
            cvs = "";
        }
        return cvs;
    }

    protected boolean mustExit() {
    	boolean exit = false;
    	if (getRequestAttribute("wiExit") != null) {
    		exit = true;
    	} else if (getRequestAttribute("wiException") != null) {
    		exit = true;
    	}
        return exit;
    }

    
    // compatibilidade
    protected Map getHttpParameters() {
        return httpParameters;
    }

    // compatibilidade
    protected void setHttpParameters() {
        Enumeration e = getHttpRequest().getParameterNames();
        while (e.hasMoreElements()) {
            String name = (String) e.nextElement();
            String[] values = getHttpRequest().getParameterValues(name);
            setHttpParameter(name, values);
        }
    }

    // compatibilidade
    protected void setHttpParameter(String name, String[] values) {
        String wiName = StringA.change(name.toLowerCase(), '_', '.');
        if (wiName.equals("wi.proj.prev") || wiName.equals("wi.page.prev")) {
            httpParameters.put(wiName, values[0]);
            return;
        }
        if (name.toLowerCase().startsWith("tmp_")) {
        	name = "tmp." + StringA.mid(name, 4, name.length());
        }
        if (values.length == 1) {
        	String value = new String(values[0]);
        	String uri = getHttpRequest().getRequestURI();
        	if (uri.indexOf("EventConnector") > -1) {
	       	    try {
					value = new String(value.getBytes("8859_1"), "UTF8");
				} catch (UnsupportedEncodingException e) { }
        	}	
            httpParameters.put(name, value);
        } else {
            httpParameters.put(name + ".size", values.length + "");
            String newSize =
                StringA.mid(name, 0, name.lastIndexOf(".") - 1) + ".size()";
            httpParameters.put(newSize, values.length + "");
            String value = "";
            for (int i = 0; i < values.length; i++) {
                value += ((value.equals("") ? "" : ",") + values[i]);
                httpParameters.put(name + "." + (i + 1), values[i]);
                String itemName = StringA.mid(name, 0, name.lastIndexOf(".") - 1) 
					+ "[" + (i + 1) + "]"
                    + StringA.mid(name, name.lastIndexOf("."), name.length());
                httpParameters.put(itemName, values[i]);
            }
            httpParameters.put(name, value);
        }
    }
    
    public boolean isJspInclude() {
    	String includeKey = "javax.servlet.include.request_uri";
    	HttpServletRequest request = getHttpRequest();
        String includeURI = (String) request.getAttribute(includeKey);
        return (includeURI != null);
    }

    /**
     * Define o ExecuteParams da Thread
     */
    public static void set(ExecuteParams executeParams) {
    	if (executeParams == null) {
            thisParam.set(null);
    		classLoader.set(null);
    	} else {
            thisParam.set(executeParams);
        	classLoader.set(executeParams.getClassLoader());
    	}
    }

    /**
     * Retorna o ExecuteParams da Thread
     *
     * @return o ExecuteParams da Thread
     */
    public static ExecuteParams get() {
        return (ExecuteParams) thisParam.get();
    }
    
}
