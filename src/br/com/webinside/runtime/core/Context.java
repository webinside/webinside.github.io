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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import br.com.webinside.runtime.component.Page;
import br.com.webinside.runtime.exception.SessionTimeoutException;
import br.com.webinside.runtime.function.SecureLinkManager;
import br.com.webinside.runtime.function.sv.SVNode;
import br.com.webinside.runtime.integration.IntFunction;
import br.com.webinside.runtime.integration.WILocale;
import br.com.webinside.runtime.util.Compat;
import br.com.webinside.runtime.util.ErrorLog;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.I18N;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;
import br.com.webinside.runtime.util.WISession;
import br.com.webinside.runtime.util.WIVersion;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.27 $
 */
public class Context {
	
    private ExecuteParams wiParams;

    /**
     * Creates a new Context object.
     *
     * @param wiParams DOCUMENT ME!
     */
    public Context(ExecuteParams wiParams) {
        this.wiParams = wiParams;
    }

    /**
     * DOCUMENT ME!
     *
     * @param impParams DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     * @throws SessionTimeoutException DOCUMENT ME!
     */
    public WIMap getWIMap(boolean impParams) throws Exception {
        if (wiParams.getWISession() == null) {
            return null;
        }
        if (wiParams.getWISession().isNew()) {
            boolean expired = false;
            String pagePrev = (String) wiParams.getHttpParameters().get("wi_page_prev");
            if (pagePrev != null && !pagePrev.trim().equals("")) {
            	expired = true;
            }
            pagePrev = (String) wiParams.getHttpParameters().get("wi.page.prev");
            if (pagePrev != null && !pagePrev.trim().equals("")) {
            	expired = true;
            }
            if (expired && !wiParams.getPage().isErrorPage()) {
                String loginPage = wiParams.getProject().getLoginPage();
                if (!loginPage.trim().equals("")) {
                	WIMap wiMap = wiParams.getWIMap();
                	if (wiMap == null) {
                		wiMap = new WIMap();
                		wiParams.setParameter(ExecuteParamsEnum.WI_MAP, wiMap);
                	}
                	wiMap.remove("tmp.msgsecurevar");
                	wiMap.put("tmp.msglogin", "Login Expirado");
                	wiParams.sendRedirect(loginPage + ".wsp", wiMap, true);
                } else {
                    throw new SessionTimeoutException("Sessão Expirou");
                }
            }
        }
        boolean isWML = false;
        Page page = wiParams.getPage(); 
        if (page != null) {
            if (page.getMime().equalsIgnoreCase("wml")) {
                isWML = true;
            }
            if (page.getMime().equalsIgnoreCase("text/vnd.wap.wml")) {
                isWML = true;
            }
        }
        if (!wiParams.getWISession().isValid() || isWML) {
            wiParams.getWISession().makeSession();
        }
        String projMapId = "wiruntime-" + wiParams.getProject().getId();
        WIMap wiMap = null;
        try {
            WIMap sessionMap = (WIMap) wiParams.getWISession().getAttribute(projMapId);
            if (sessionMap != null) {
            	wiMap = sessionMap.cloneMe();
                String redirId =
                    (String) wiParams.getHttpParameters().get("wi.redirect");
                if (redirId != null && !redirId.equals("")) {
                	String redirKey = "wi.redirect." + redirId + ".";
                    WIMap sub = (WIMap) sessionMap.getObj(redirKey);
                    if (sub != null) {
                    	wiMap.putAll(sub.getAsMap());
                    	wiMap.remove("redirectTime");
                    }
                }
                cleanRedirect(sessionMap);
                wiMap.remove("wi.redirect.");
            }
        } catch (ClassCastException err) {
        	// não deve ocorrer.
        }
        if (wiMap == null) {
        	wiMap = new WIMap();
            // contexto app
            WIMap appMap = null;
            try {
            	ServletContext sc = wiParams.getServletContext(); 
            	appMap = (WIMap) sc.getAttribute("webinside");
                if (appMap != null) {
                	appMap = appMap.cloneMe();
                }
            } catch (ClassCastException err) { }
            if (appMap == null) {
            	appMap = new WIMap();
            }
            wiMap.putObj("app.", appMap);
        }
        wiMap.setChangedKeys(true);
        Constants.populate(wiParams.getServletContext(), wiMap);
        populateWI(wiMap);
        wiMap.putObj("wi.functions", wiParams.getProject().getFunctionsMap());
        if (wiParams.getWISession().isNew()) {
        	WIMap sessionMap = getSessionMap(projMapId);
            IntFunction.loadInitParams(sessionMap, wiParams.getProject());
            IntFunction.loadInitParams(wiMap, wiParams.getProject());
        }
        wiMap.putObj("wi.servletcontext", wiParams.getServletContext());
        importHeaders(wiMap);
        if (impParams) {
        	if (wiParams.getHttpParameters().containsKey("wi.slink")) {
            	String slink = (String) wiParams.getHttpParameters().get("wi.slink");
                SecureLinkManager.importToken(slink, wiMap);
        	}
            importParameters(wiMap);
    		// recuperar tmp's enviados ao include usando http request
        	HttpServletRequest req = wiParams.getHttpRequest();
        	if (req != null && req.getAttribute("wiTmpMap") != null) {
        		WIMap reqMap = (WIMap) req.getAttribute("wiTmpMap");
        		Iterator it = reqMap.keySet().iterator();
        		while (it.hasNext()) {
        			String key = (String) it.next();
        			String value = reqMap.get(key);
                    if (value.trim().equals("")) {
                        wiMap.remove(key);
                    } else {
                        wiMap.put(key, value);
                    }
        		}
        	}
        }
        return wiMap;
    }

    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     */
    public void populateWI(WIMap context) {
        String prjId = "";
        if (wiParams.getProject() != null) {
            prjId = wiParams.getProject().getId();
            context.put("wi.proj.id", prjId);
            context.put("wi.proj.title", wiParams.getProject().getTitle());
            String projParent = wiParams.getProject().getParentId();
            if (!projParent.equals("")) { 
            	context.put("wi.proj.parent", projParent);
            }	
        }
        String pageId = "";
        if (wiParams.getPage() != null) {
            pageId = wiParams.getPage().getId();
            context.put("wi.page.id", pageId);
            int last = pageId.lastIndexOf("/");
            context.put("wi.page.name",
                StringA.mid(pageId, last + 1, pageId.length() - 1));
            context.put("wi.page.title", wiParams.getPage().getTitle());
        }
        context.put("wi.session.id", wiParams.getWISession().getId());
        context.put("wi.session.ip", wiParams.getHttpRequest().getRemoteAddr());
        context.put("wi.session.host", wiParams.getHttpRequest().getRemoteHost());
        HttpServletRequest req = wiParams.getHttpRequest();
        String servername = RtmFunction.getServerName(req);
        context.put("wi.server.host", servername);
        int serverPort = RtmFunction.getServerPort(req);
        context.put("wi.server.port", serverPort);
        String serverProt = RtmFunction.getServerProt(req, serverPort);
        context.put("wi.server.prot", serverProt);
        String serverUrl = servername;
        if ((serverPort != 80) && (serverPort != 443)
                    && (serverUrl.indexOf(":") == -1)) {
            serverUrl = serverUrl + ":" + serverPort;
        }
        context.put("wi.server.url", serverProt + serverUrl);
        String localhost = serverProt + "localhost" + ":" + req.getServerPort();
        context.put("wi.server.localhost", localhost);
        String shortname = "wiserver";
        try {
        	shortname = InetAddress.getLocalHost().getHostName();
        	shortname = StringA.piece(shortname.toLowerCase(), ".", 1);
        } catch (UnknownHostException e) { }	
        context.put("wi.server.name", shortname);
        populateProjPath(context);        
        Long initialTime =
            (Long) wiParams.getHttpRequest().getAttribute("wiInitialTime");
        if (initialTime != null) {
            context.put("wi.request.initialtime", initialTime.toString());
        }
        String querystring = wiParams.getHttpRequest().getQueryString();
        if (querystring == null) querystring = "";
        context.put("wi.request.querystring", querystring);
        String reqURL = serverProt + serverUrl + "/" + prjId + "/" + pageId + ".wsp";
        if (!querystring.equals("")) {
        	reqURL = reqURL + "?" + wiParams.getHttpRequest().getQueryString();
        }
        context.put("wi.request.url", reqURL);
        String reqURI = wiParams.getHttpRequest().getRequestURI();
        if (reqURI == null) reqURI = "";
        reqURI = StringA.piece(reqURI,";",1);
        context.put("wi.request.uri", reqURI);
        String agent = wiParams.getHttpRequest().getHeader("user-agent");   
        if (agent != null && !agent.equals("")) {
            context.put("wi.request.user-agent", agent);
            context.put("wi.browser", browser(agent.toLowerCase()));
        }
        String requestParams = "";
        if (wiParams.getHttpParameters() != null) {
            Iterator it = wiParams.getHttpParameters().keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                if (!requestParams.equals("")) {
                    requestParams = requestParams + ", ";
                }
                requestParams = requestParams + key;
            }
            context.put("wi.request.parameters", requestParams);
        }
        Locale loc = WILocale.getDefault("pt-br");
        if (context.get("wi.builder").equals("true")) {
        	loc = WILocale.getDefault(wiParams.getHttpRequest());
            String bldLang = context.get("wi.builder.locale");
            if (!bldLang.equals("browser")) {
            	loc =  WILocale.getDefault(bldLang);
            }
        }
		context.put("wi.locale", loc.toString().toLowerCase());
        context.put("wi.language", loc.getLanguage().toLowerCase());
		context.putObj("wi.i18n", new I18N(loc));
		TimeZone zone = TimeZone.getDefault();
		if (!wiParams.getProject().getTimezone().equals("")) {
			zone = TimeZone.getTimeZone(wiParams.getProject().getTimezone());
		}
		Calendar calendar = Calendar.getInstance(zone);
        context.put("wi.date.internal", calendar.getTimeInMillis() + "");
        context.put("wi.date.wdaynum", calendar.get(Calendar.DAY_OF_WEEK));
        String mask = "dd,MM,yyyy,HH,mm,ss,EEEE,MMMM";
        String dtAux = Function.getDate(calendar.getTime(), mask, loc, zone);
        String dia = StringA.piece(dtAux, ",", 1);
        context.put("wi.date.day", dia);
        String weekday = StringA.piece(dtAux, ",", 7);
        context.put("wi.date.wday", weekday);
        String nmes = StringA.piece(dtAux, ",", 2);
        context.put("wi.date.ymonth", nmes);
        String tmes = StringA.piece(dtAux, ",", 8);
        context.put("wi.date.month", tmes);
        String ano = StringA.piece(dtAux, ",", 3);
        context.put("wi.date.year", ano);
        context.put("wi.date.ansi", ano + "-" + nmes + "-" + dia);
        context.put("wi.date.dmy", dia + "/" + nmes + "/" + ano);
        context.put("wi.date.ymd", ano + nmes + dia);
        context.put("wi.date.mdy", nmes + "/" + dia + "/" + ano);
        String hora = StringA.piece(dtAux, ",", 4);
        context.put("wi.date.hour", hora);
        String min = StringA.piece(dtAux, ",", 5);
        context.put("wi.date.min", min);
        String seg = StringA.piece(dtAux, ",", 6);
        context.put("wi.date.sec", seg);
        context.put("wi.date.hm", hora + ":" + min);
        context.put("wi.date.hms", hora + ":" + min + ":" + seg);
        context.put("wi.version", WIVersion.VERSION);
        context.put("wi.novalidation", "true");
    }

    public void populateProjPath(WIMap context) {
        if (wiParams.getServletContext() != null) {
        	ServletContext sc = wiParams.getServletContext();
        	String prjId = context.get("wi.proj.id");
            String prjDir = sc.getRealPath("");
            String webappsDir = new File(sc.getRealPath("")).getParent();
            context.put("wi.proj.path", prjDir);
            context.put("wi.webapps.path", webappsDir);
            context.put("wi.proj.id", prjId);
        }
    }

    private WIMap getSessionMap(String projMapId) {
    	ServletContext sc = wiParams.getServletContext();
    	WIMap appMap = null;
    	try {
            appMap = (WIMap) sc.getAttribute("webinside");
        } catch (ClassCastException err) { }
        if (appMap == null) {
            appMap = new WIMap();
            sc.setAttribute("webinside", appMap);
        }
        WIMap sessionMap = null;
        synchronized (wiParams.getWISession().getHttpSession()) {
            // contexto principal
        	WISession wisession = wiParams.getWISession(); 
            try {
                sessionMap = (WIMap) wisession.getAttribute(projMapId);
            } catch (ClassCastException err) { }
            if (sessionMap == null) {
                sessionMap = new WIMap();
                wiParams.getWISession().setAttribute(projMapId, sessionMap);
            }
            sessionMap.putObj("app.", appMap);
        }
        return sessionMap;
    }
    
    private void syncWIMap(WIMap wiMap, WIMap sessionMap) {
        Iterator it = wiMap.getChangedKeys().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (!canExport(key)) {
                continue;
            }
            Object value = wiMap.getObj(key);
            if (value == null) {
                sessionMap.remove(key);
            } else {
                if (!key.endsWith(".") && (value instanceof String)) {
                    sessionMap.put(key, (String) value);
                } else {
                    sessionMap.putObj(key, value);
                }
            }
        }
    }
    
    public void syncWIMap() {
    	WIMap wiMap = wiParams.getWIMap();
    	if (wiMap != null) syncWIMap(wiMap);
    }
    
    public void syncWIMap(WIMap wiMap) {
        if (!wiParams.getWISession().isValid()) return;
        String projMapId = "wiruntime-" + wiParams.getProject().getId();
        syncWIMap(wiMap, getSessionMap(projMapId));
        if (wiParams.getHttpRequest() != null) {
        	WIMap tmpMap = getTmpMap(wiMap);
        	wiParams.getHttpRequest().setAttribute("wiTmpMap", tmpMap);
        }
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param context DOCUMENT ME!
     */
    public void putWIMap(WIMap wiMap) {
        if (!wiParams.getWISession().isValid()) return;
        String prjId = wiParams.getProject().getId();
        String projMapId = "wiruntime-" + prjId;
        WIMap sessionMap = getSessionMap(projMapId);
        syncWIMap(wiMap, sessionMap);
        String md5 = wiMap.get("wi.pwd.md5");
        if (!md5.equals("")) {
            sessionMap.put("wi.pwd.md5", md5);
        } else {
            sessionMap.remove("wi.pwd.md5");
        }
        String sha1 = wiMap.get("wi.pwd.sha1");
        if (!sha1.equals("")) {
            sessionMap.put("wi.pwd.sha1", sha1);
        } else {
            sessionMap.remove("wi.pwd.sha1");
        }
        String redirID = wiMap.get("wi.redirect");
        if (!redirID.equals("")) {
            WIMap subobj = new WIMap();
            subobj.put("redirectTime", new Date().getTime() + "");
            subobj.putObj("tmp.", wiMap.getObj("tmp."));
            subobj.putObj("stmp.", wiMap.getObj("stmp."));
            subobj.put("wi.token.proj", wiMap.get("wi.proj.id"));
            subobj.put("wi.token.page", wiMap.get("wi.page.id"));
            transpose(wiMap, subobj, "wi.token.received");
        	if (wiMap.get("wi.token.ok").equals("true")) {
        		subobj.put("wi.token.received", wiMap.get("wi.token"));
        	}
            transpose(wiMap, subobj, "wi.redir.proj");
           	transpose(wiMap, subobj, "wi.redir.page");
            transpose(wiMap, subobj, "wi.error");
            transpose(wiMap, subobj, "wi.sql.valid");
            transpose(wiMap, subobj, "wi.sql.query");
            transpose(wiMap, subobj, "wi.sql.error");
            transpose(wiMap, subobj, "wi.proj.prev.bkp");
            transpose(wiMap, subobj, "wi.page.prev.bkp");
            transpose(wiMap, subobj, "wi.validation.ok");
            transpose(wiMap, subobj, "wi.validation.messages");
            sessionMap.putObj("wi.redirect." + redirID + ".", subobj);
        }
        // resincronismo para cluster
        WIMap appMap = (WIMap)sessionMap.getObj("app.");
        wiParams.getServletContext().setAttribute("webinside", appMap);
        wiParams.getWISession().setAttribute(projMapId, sessionMap);
        Compat.legacySession(wiParams.getWISession(), prjId, sessionMap);
    }
    
    private void transpose(WIMap source, WIMap target, String key) {
    	String value = source.get(key);
    	if (!value.equals("")) {
    		target.put(key, value);
    	}
    }
    
    private boolean canExport(String key) {
        if (key.startsWith("app.") || key.startsWith("pvt.")) return true;
        if (key.startsWith("grid.")) {
            boolean ok = false;
            if (key.endsWith(".showall") || key.endsWith(".showicon")) ok = true;
            if (key.endsWith(".txtfirst") || key.endsWith(".txtfirstoff")) ok = true;
            if (key.endsWith(".txtback") || key.endsWith(".txtbackoff")) ok = true;
            if (key.endsWith(".txtgo") || key.endsWith(".txtgooff")) ok = true;
            if (key.endsWith(".txtlast") || key.endsWith(".txtlastoff")) ok = true;
            if (key.endsWith(".txtmid") || key.endsWith(".linkindexsize")) ok = true;
            if (ok) return true;
        }
        if (key.startsWith("super.")) return false;
        String reserved = "wi.,tmp.,stmp.,tmp[,grid.,combo.";
        for (int a = 1; a <= (StringA.count(reserved, ',') + 1); a++) {
            if (key.startsWith(StringA.piece(reserved, ",", a))) {
            	return false;
            }
        }
        return true;
    }

    private void importParameters(WIMap wiMap) {
    	Set<String> svSet = new TreeSet();
    	if (wiParams.getProject() != null) {
    		svSet = wiParams.getProject().getSecureVars();
    	}
    	WISession session = wiParams.getWISession();
        if (session.isValid()) {
        	Map<String, SVNode> svMap = IntFunction.getSVMap(session);
        	for (SVNode svNode : svMap.values()) {
				svNode.addPage(wiMap.get("wi.page.id"));
			}
        }
        String prefix = (String) wiParams.getHttpParameters().get("wi.prefix");
        wiMap.put("wi.prefix", prefix);
        Iterator it = wiParams.getHttpParameters().keySet().iterator();
        while (it.hasNext()) {
            String name = (String) it.next();
            String value = (String) wiParams.getHttpParameters().get(name);
            name = name.toLowerCase().trim();
            if (session.isValid() && IntFunction.isSecureVar(svSet, name)) {
            	SVNode svNode = IntFunction.getSVNode(session, name);
            	String pValue = value.trim();
            	value = svNode.getValue(pValue);
            	if (!pValue.equals("") && value.equals("")) {
                	boolean first = wiMap.get("tmp.msgsecurevar").equals("");
                	if (first) wiMap.put("tmp.msgsecurevar", name + "=" + pValue);
                	ErrorLog log = wiParams.getErrorLog();
                	Object iru = wiParams.getRequestAttribute(ExecuteServlet.INCLUDE_REQUEST_URI);
                	if (log != null && iru == null) {
//                		String msg = "Not found: " + name + "=" + pValue;
//                		log.write(getClass().getName(), "SecureVar", msg);
                	}
            	}
            }
            importParameters(wiMap, name, value);
        }
        // parametros do workflow
        if (wiParams.getRequestAttribute("wfParameters") != null) {
        	Map wfMap = (Map) wiParams.getRequestAttribute("wfParameters");
            prefix = (String) wfMap.get("wi.prefix");
            wiMap.put("wi.prefix", prefix);
        	it = wfMap.keySet().iterator();
        	while (it.hasNext()) {
        		String name = (String) it.next();
        		String value = (String) wfMap.get(name);
        		if (value != null) {
                    name = name.toLowerCase().trim();
                    importParameters(wiMap, name, value);
        		}
        	}
        }
        wiMap.remove("wi.prefix");
    }
    
    private void importParameters(WIMap wiMap, String name, String value) {
        if (name.equals("jsessionid")) {
            return;
        }  
        if (name.equals("wi_proj_prev")) {
            name = "wi.proj.prev";
        }
        if (name.equals("wi_page_prev")) {
            name = "wi.page.prev";
        }
        if (name.startsWith("grid_")) {
            name = StringA.change(name, '_', '.');
        }
        if ((name.startsWith("tmp_")) && (name.length() > 4)) {
            name = "tmp." + name.substring(4, name.length());
        }
    	String prefix = wiMap.get("wi.prefix");
        if (!prefix.equals("") && (name.indexOf(".") == -1)) {
            name = prefix + "." + name;
        }
        if (name.startsWith("wi.")) {
            boolean ok = false;
            if (name.equals("wi.token")) {
                name = "wi.token.received";
                ok = true;
            }
            if (name.endsWith(".email.attid")) {
                ok = true;
            }
            if (name.endsWith(".proj.prev")) {
                ok = true;
            }
            if (name.endsWith(".page.prev")) {
                ok = true;
            }
            if (name.endsWith(".developer")) {
                ok = true;
            }
            if (!ok) {
                return;
            }
        }
        if (name.startsWith("pvt.") || name.startsWith("stmp.") || 
        		name.startsWith("tmp.debug.") || name.startsWith("app.")) {
            return;
        }
        if (name.startsWith("grid.")) {
            boolean ok = false;
            if (name.endsWith(".showall")) ok = true;
            if (name.endsWith(".next")) ok = true;
            if (name.endsWith(".txtgo")) ok = true;
            if (name.endsWith(".txtgooff")) ok = true;
            if (name.endsWith(".txtmid")) ok = true;
            if (name.endsWith(".txtback")) ok = true;
            if (name.endsWith(".txtbackoff")) ok = true;
            if (name.endsWith(".linkindexsize")) ok = true;
            if (!ok) return;
        }
        if (name.startsWith("combo.")) {
            return;
        }
        // Testar existencia de sqlfilter
        if (wiMap.get("pvt.sqlfilter." + name).equalsIgnoreCase("number")) {
        	value = Function.parseInt(value.trim()) + "";
        }
        if (wiMap.get("pvt.sqlfilter." + name).equalsIgnoreCase("text")) {
        	value = StringA.changeChars(value, "%*?'\"", "");
        }
        if (wiParams.getProject().isTmpRequestVar()) {
            boolean concat = true;
            if (name.startsWith("wi.")) concat = false;
            if (name.startsWith("tmp.")) concat = false;
            if (name.startsWith("grid.")) concat = false;
            if (concat) name = "tmp." + name;
        }
        // Um parametro recebido como vazio deve ser armazenado para o Persist
        // poder detectar as variaveis que foram enviadas.
        if (value.trim().equals("")) {
            wiMap.put(name, "");
        } else {
            wiMap.put(name, value);
        }
    }

    private void importHeaders(WIMap context) {
        StringBuffer all = new StringBuffer();
        Enumeration keys = wiParams.getHttpRequest().getHeaderNames();
        while (keys.hasMoreElements()) {
            String key = (String) keys.nextElement();
            String value = wiParams.getHttpRequest().getHeader(key);
            if (value == null) {
                value = "";
            }
            context.put("wi.header." + key.trim(), value);
            if (all.length() > 0) {
                all.append(", ");
            }
            all.append(key.trim());
        }
        context.put("wi.headers", all.toString());
    }
    
    private void cleanRedirect(WIMap wiMapReal) {
    	WIMap obj = (WIMap)wiMapReal.getObj("wi.redirect.");
    	if (obj != null) {
    		List ids = new ArrayList(obj.getInternalMap().keySet());
    		for (Iterator iter = ids.iterator(); iter.hasNext();) {
				String redirId = (String) iter.next();
	    		String key = "wi.redirect." + redirId + "redirectTime";
	    		long time = Function.parseLong(wiMapReal.get(key));
	    		long now = new Date().getTime();
	    		if ((time == 0) || (time + 60000 < now)) {
	    			wiMapReal.remove("wi.redirect." + redirId);
	    		}
			}
    	}
    }
    
    private WIMap getTmpMap(WIMap wiMap) {
    	WIMap auxMap = new WIMap();
    	// adiciona tmp's removidos
    	Iterator it = wiMap.getChangedKeys().iterator();
		while (it.hasNext()) {
			String key = (String) it.next();
			if (key.startsWith("tmp.")) auxMap.put(key, "");
		}
    	// adiciona tmp's existentes
    	it = wiMap.keySet().iterator();
    	while (it.hasNext()) {
			String key = (String) it.next();
			if (key.startsWith("tmp.")) auxMap.put(key, wiMap.get(key));
    	}
    	return auxMap;
    }
    
    private String browser(String userAgent) {
    	if(userAgent.indexOf("opera") > -1) {
    		return "opera";
    	} else if(userAgent.indexOf("chrome") > -1) {
    		return "chrome";
    	} else if(userAgent.indexOf("safari") > -1) {
    		return "safari";
    	} else if(userAgent.indexOf("firefox") > -1) {
    		return "firefox";
    	} else if(userAgent.indexOf("msie 7") > -1) {
    		return "ie7";
    	} else if(userAgent.indexOf("msie 8") > -1) {
    		return "ie8";
    	} else if(userAgent.indexOf("msie 9") > -1) {
    		return "ie9";
    	} else {
    		return "";
    	}	
    }
    
}
