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
import javax.servlet.ServletException;
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
 * @version $Revision: 1.7 $
 */
public class ExecuteParams {
	
    // parametros da Thread
    private static ThreadLocal thisParam = new ThreadLocal();
	
	// parametros do ExecuteParams
    private Map<ExecuteParamsEnum, Object> parameters = new HashMap();

    // parametros de compatibilidade com o request antigo do WI
    private Map httpParameters = new LinkedHashMap();;

    public ExecuteParams() {
        setParameter(ExecuteParamsEnum.CLASSLOADER, getClass().getClassLoader());
        setParameter(ExecuteParamsEnum.PRODUCER, new Producer());
        setParameter(ExecuteParamsEnum.DATABASE_ALIASES, new DatabaseAliases());
        getDatabaseAliases().setWIParams(this);
        set(this);
    }

    public ExecuteParams(HttpServletRequest request, HttpServletResponse response, 
    		ServletContext application) {
    	this();
        setParameter(ExecuteParamsEnum.HTTP_REQUEST, request);
        setParameter(ExecuteParamsEnum.HTTP_RESPONSE, response);
        setParameter(ExecuteParamsEnum.SERVLET_CONTEXT, application);
        setParameter(ExecuteParamsEnum.WI_SESSION, new WISession(request.getSession()));
        setHttpParameters();
    }

    public void setParameter(ExecuteParamsEnum type, Object obj) {
//        if (obj == null) {
//            parameters.remove(type + "");
//            return;
//        }
    	parameters.put(type, obj);    	
        if (type == ExecuteParamsEnum.PROJECT) {
            if (obj instanceof AbstractProject) {
                AbstractProject prj = (AbstractProject) obj;
                getDatabaseAliases().setLog(prj.getSqlLog());
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
	            setParameter(ExecuteParamsEnum.ERROR_LOG, errorLog);
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
	            setParameter(ExecuteParamsEnum.ERROR_LOG, errorLog);
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
        if (!url.trim().toLowerCase().startsWith("http://") &&
        		!url.trim().toLowerCase().startsWith("https://")) {
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
        return (HttpServletRequest) parameters.get(ExecuteParamsEnum.HTTP_REQUEST);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public HttpServletResponse getHttpResponse() {
        return (HttpServletResponse) parameters.get(ExecuteParamsEnum.HTTP_RESPONSE);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ServletContext getServletContext() {
        return (ServletContext) parameters.get(ExecuteParamsEnum.SERVLET_CONTEXT);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ClassLoader getClassLoader() {
        return (ClassLoader) parameters.get(ExecuteParamsEnum.CLASSLOADER);
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
        OutputStream s = (OutputStream) parameters.get(ExecuteParamsEnum.OUT_STREAM);
        if (tryOpen) {
            if (s == null) {
                if (getHttpResponse() != null) {
                    try {
                        s = getHttpResponse().getOutputStream();
                    } catch (Exception err) {
                    	System.err.println(getClass().getName() + ": " + err);
                    }
                    if (s != null) {
                        parameters.put(ExecuteParamsEnum.OUT_STREAM, s);
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
        Writer w = (Writer) parameters.get(ExecuteParamsEnum.OUT_WRITER);
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
                    parameters.put(ExecuteParamsEnum.OUT_WRITER, w);
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
        return (WISession) parameters.get(ExecuteParamsEnum.WI_SESSION);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public DatabaseAliases getDatabaseAliases() {
        return (DatabaseAliases) parameters.get(ExecuteParamsEnum.DATABASE_ALIASES);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public FileUpload getFileUpload() {
        return (FileUpload) parameters.get(ExecuteParamsEnum.FILE_UPLOAD);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AbstractProject getProject() {
        return (AbstractProject) parameters.get(ExecuteParamsEnum.PROJECT);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Page getPage() {
        return (Page) parameters.get(ExecuteParamsEnum.PAGE);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Producer getProducer() {
        return (Producer) parameters.get(ExecuteParamsEnum.PRODUCER);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ErrorLog getErrorLog() {
    	return (ErrorLog) parameters.get(ExecuteParamsEnum.ERROR_LOG);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public WIMap getWIMap() {
        return (WIMap) parameters.get(ExecuteParamsEnum.WI_MAP);
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
        setParameter(ExecuteParamsEnum.PAGE, page);
        setParameter(ExecuteParamsEnum.PRODUCER, new Producer());
        String preJSP = page.getId() + "_pre.jsp";
        if (!preJSP.startsWith("/")) {
            preJSP = "/" + preJSP;
        }
        getHttpRequest().setAttribute("wiParams", this);
        try {
        	File f = new File(getServletContext().getRealPath(preJSP));
        	if (f.exists() || ExecuteServlet.jspList.contains(preJSP)) { 
        		RequestDispatcher rd = 
        			getServletContext().getRequestDispatcher(preJSP);
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
        setParameter(ExecuteParamsEnum.PAGE, origpage);
        setParameter(ExecuteParamsEnum.PRODUCER, producer);
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
        	File f = new File(getServletContext().getRealPath(jspPage));
        	if (f.exists() || ExecuteServlet.jspList.contains(jspPage)) { 
        		RequestDispatcher rd = getServletContext().getRequestDispatcher(jspPage);
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
    	thisParam.set(executeParams);
    }

    /**
     * Retorna o ExecuteParams da Thread
     *
     * @return o ExecuteParams da Thread
     */
    public static ExecuteParams get() {
        return (ExecuteParams) thisParam.get();
    }
    
    public static ExecuteParams initInstance(HttpServletRequest request, 
    		HttpServletResponse response, ServletContext context) 
    		throws ServletException, IOException {
    	MimeType.readFile(new MimeType().getClass().getClassLoader());    	
        String projId = StringA.piece(request.getRequestURI(), "/", 2);
        request.setAttribute("wiProject", projId);
        ExecuteParams wiParams = new ExecuteParams(request, response, context);
        request.setAttribute("wiParams", wiParams);
        context.getRequestDispatcher("/project.jsp").include(request, response);
		WIMap wiMap = new WIMap();
        try {
            wiMap = new Context(wiParams).getWIMap(true);
        } catch (Exception err) { 
        	err.printStackTrace();
        }
        wiParams.setParameter(ExecuteParamsEnum.WI_MAP, wiMap);
        wiParams.setErrorLog(null);
        DatabaseAliases databases = wiParams.getDatabaseAliases();
        databases.setLog(wiParams.getProject().getSqlLog());
        databases.loadDatabases(wiParams.getProject());
        return wiParams;
    }
    
}
