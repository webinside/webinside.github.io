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
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.servlet.ServletRequestContext;

import br.com.webinside.runtime.component.AbstractProject;
import br.com.webinside.runtime.component.Page;
import br.com.webinside.runtime.component.ReportRef;
import br.com.webinside.runtime.database.DatabaseThread;
import br.com.webinside.runtime.function.SecureLinkManager;
import br.com.webinside.runtime.integration.Condition;
import br.com.webinside.runtime.integration.IntFunction;
import br.com.webinside.runtime.integration.LogsGenerator;
import br.com.webinside.runtime.integration.Producer;
import br.com.webinside.runtime.integration.Validator;
import br.com.webinside.runtime.net.FileUpload;
import br.com.webinside.runtime.net.SmtpMessageThread;
import br.com.webinside.runtime.util.CrossContext;
import br.com.webinside.runtime.util.CrossContextFactory;
import br.com.webinside.runtime.util.FileIO;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.I18N;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;
import br.com.webinside.runtime.util.WISession;
import br.com.webinside.runtime.util.WIVersion;

/**
 * Classe principal de execução do WI. Controla a execução dos arquivos WSP.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.4 $
 *
 * @since 3.0   <br>
 *        Request parameters used by WI: wiProject, wiPage, wiParams
 *        wiRedirect, wiExit, wiException, wiInitialTime, wiJspFilename
 */
public class ExecuteServlet extends HttpServlet {

	public static final String INCLUDE_REQUEST_URI = "javax.servlet.include.request_uri";
	public static final String SESSION_REQS_MAP = "wiSessionRequestsMap";
	
	private static final long serialVersionUID = 1L;
	protected static final List jspList = new ArrayList();
	private static final Map syncTokens = Collections.synchronizedMap(new HashMap());
	private static String initialProjId = null;
	private static int maxSesReqsLog = 10;
	private static int maxSesReqsBlock = 10;
    
	public void init() throws ServletException {
		super.init();
        MimeType.readFile(getClass().getClassLoader());
		ServletContext sc = getServletContext();
		String tmpDir = sc.getInitParameter("java.io.tmpdir");
    	Function.setTmpDir(tmpDir);
        String tmpdir = Function.tmpDir();
        Function.removeFiles(tmpdir, "pop*");
        Function.removeDir(tmpdir + ".wi");
        initialProjId = sc.getInitParameter("projectId");
        if (initialProjId != null && initialProjId.trim().equals("")) { 
        	initialProjId = null;
        }
    	String strMaxSessionReqs = sc.getInitParameter("maxSessionRequests");
    	if (strMaxSessionReqs != null) {
    		String[] strArray = strMaxSessionReqs.split(",");
    		int val1 = Function.parseInt(strArray[0]);
    		if (val1 > 0) {
    			maxSesReqsLog = val1;
    			maxSesReqsBlock = val1;
    		}
    		if (strArray.length > 1) {
	    		int val2 = Function.parseInt(strArray[1]);
	    		if (val2 > 0) maxSesReqsBlock = val2;
    		}	
    		if (maxSesReqsLog > maxSesReqsBlock) {
    			maxSesReqsLog = maxSesReqsBlock;
    		}
    	}
    	File pf = new File(sc.getRealPath("/project.jsp"));
    	if (!pf.isFile()) {
    		File web = new File(sc.getRealPath("/WEB-INF/web.xml"));
    		FileIO fio = new FileIO(web.getAbsolutePath(), 'r');
    		StringA line = new StringA();
    		while (fio.readLine(line) > 0) {
    			if (line.trim().startsWith("<url-pattern>") && 
    					line.indexOf(".jsp") > -1) {
    				String jsp = line.trim().replace("<url-pattern>", "");
    				jsp = jsp.replace("</url-pattern>", "").trim();
    				jspList.add(jsp);
    			}
    		}
    	}
	}

    /**
     * DOCUMENT ME!
     *
     * @param request DOCUMENT ME!
     * @param response DOCUMENT ME!
     *
     * @throws ServletException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        doPost(request, response);
    }

    /**
     * DOCUMENT ME!
     *
     * @param request DOCUMENT ME!
     * @param response DOCUMENT ME!
     *
     * @throws ServletException DOCUMENT ME!
     * @throws IOException DOCUMENT ME!
     */
    public void doPost(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
		SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");	            
        Function.setThreadName(df.format(new Date()) + " - Start of Execute");
        
        // bloqueio de requisicoes
    	HttpSession session = request.getSession();
    	boolean exit = controlSessionRequestsIni(request, response, session);
    	if (exit) {
			Function.setThreadName(df.format(new Date()) + " - End of Execute");
    		return;
    	}
    	
        // inicializações do sistema
        long initialTime = System.currentTimeMillis();
        request.setAttribute("wiInitialTime", Long.valueOf(initialTime));
        // identificação do projeto e página
        String requestURI = StringA.piece(request.getRequestURI(),";",1);
        String includeURI = (String) request.getAttribute(INCLUDE_REQUEST_URI);
        String URI = requestURI;
        if (includeURI != null) {
            URI = includeURI;
        }
        if (initialProjId != null && !URI.startsWith("/" + initialProjId)) {
      		URI = "/" + initialProjId + URI;
        }
        String projId = StringA.piece(URI, "/", 2);
        String pageId = StringA.piece(URI, "/", 3, 0);
        String requestPageId = StringA.piece(requestURI, "/", 3, 0);
        String ctxProj = new File(getServletContext().getRealPath("")).getName();
        ctxProj = StringA.piece(ctxProj, ".war", 1);
        if (initialProjId == null && !projId.equals(ctxProj)) {
        	projId = ctxProj;
        	pageId = StringA.piece(URI, "/", 2, 0);
        	requestPageId = StringA.piece(requestURI, "/", 2, 0);
        } 
        pageId = StringA.change(pageId, ".wsp", "");
        requestPageId = StringA.change(requestPageId, ".wsp", "");
        if (pageId.equals("")) {
			Function.setThreadName(df.format(new Date()) + " - End of Execute");
    		return;
        }
        
        // controle de arquivos jsp protegidos
        boolean restricted = false;
        if (pageId.equals("project")) {
            restricted = true;
        }
        if (pageId.endsWith("_pre") || pageId.endsWith("_pos")) {
            restricted = true;
        }
        if (pageId.startsWith("combos/") || pageId.startsWith("grids/")) {
            restricted = true;
        }
        if (pageId.startsWith("downloads/") || pageId.startsWith("uploads/")) {
            restricted = true;
        }
        if (restricted) {
			Function.setThreadName(df.format(new Date()) + " - End of Execute");
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        // processando a resposta de um pedido do securelink
        if (pageId.equals("WISecureLink")) {
        	String token = request.getParameter("token");
        	if (token != null) {
        		String json = SecureLinkManager.getTokenJson(token);
        		response.getWriter().write(json);
        	}
        	session.invalidate();
			Function.setThreadName(df.format(new Date()) + " - End of Execute");
        	return;
        }

        // inicializando o context
        request.setAttribute("wiProject", projId);
        ExecuteParams parentParams = 
        	(ExecuteParams) request.getAttribute("wiParams");
        ExecuteParams wiParams =
            new ExecuteParams(request, response, getServletContext());
        request.setAttribute("wiParams", wiParams);
        // preparação de arquivos			
        String projPrev = request.getParameter("wi.proj.prev");
        if (projPrev == null) projPrev = "";
        String pagePrev = request.getParameter("wi.page.prev");
        if (pagePrev == null) pagePrev = "";
        String jspPage = "/" + pageId + ".jsp";
        String jspPre = "/" + pageId + "_pre.jsp";
        String jspPos = "";
        if (!pagePrev.equals("")) {
            jspPos = "/" + pagePrev + "_pos.jsp";
        }

        // carregando o projeto 
        try {
            ServletContext sc = getServletContext();
            String jspLink = "/project.jsp";
            sc.getRequestDispatcher(jspLink).include(request, response);
            if (wiParams.getProject() == null) {
                String dest = "/" + projId + "/project.jsp";
                response.sendRedirect(dest);
                return;
            }
            WIMap constants = new WIMap();
            Constants.populate(getServletContext(), constants);
            wiParams.setErrorLog(constants);
    		String loginPage = wiParams.getProject().getLoginPage();
    		loginPage = StringA.piece(loginPage, ".wsp", 1).trim();
            if (pageId.equals("index") && !loginPage.equals("")) {
            	String index = getServletContext().getRealPath("index.jsp");
            	if (!new File(index).isFile()) {
            		pageId = loginPage;
            		jspPage = "/" + loginPage + ".jsp";
            		jspPre = "/" + loginPage + "_pre.jsp";
            	}
            }
            // Valida versão do projeto
            String jspVersion = wiParams.getProject().getVersion();
            if (!jspVersion.equals(WIVersion.VERSION)) {
                String msg =
                    new I18N().get("Versão Inválida") + "<br>\r\n" + "WI-JAR: "
                    + WIVersion.VERSION + "<br>WI-JSP: " + jspVersion;
                new RtmExport(wiParams).showMessage(msg);
                return;
            }
            // Carregando Threads
            if (!DatabaseThread.isPoolEmpty()) {
            	DatabaseThread.execute(projId);
            }	
            if (wiParams.getProject().getHosts().hasSMTP()) {
            	SmtpMessageThread.execute(projId);
            }	
            // controle do FileUpload
            ServletRequestContext reqCtx = new ServletRequestContext(request);
            if (ServletFileUpload.isMultipartContent(reqCtx)) {
                fileUpload(wiParams);
                Map httpParameters = wiParams.getHttpParameters(); 
                projPrev = (String) httpParameters.get("wi.proj.prev");
                if (projPrev == null) projPrev = "";
                pagePrev = (String) httpParameters.get("wi.page.prev");
                if (pagePrev == null) pagePrev = "";
                if (!pagePrev.equals("")) {
                    jspPos = "/" + pagePrev + "_pos.jsp";
                }
            }

            // criando e populando o contexto e tratando sessão expirada
            String auxJspName = (String) request.getAttribute("wiJspFilename");
            if (auxJspName == null || includeURI != null) {
                request.setAttribute("wiJspFilename", "/" + pageId);
            }
            try {
            	File f = new File(getServletContext().getRealPath(jspPage));
            	if (f.exists() || jspList.contains(jspPage)) { 
            		RequestDispatcher rd = getServletContext().getRequestDispatcher(jspPage); 
            		rd.include(request, response);
            	}	
            } catch (ServletException err) {
            	Throwable t = err.getRootCause();
            	if (t instanceof ServletException) {
            		throw (ServletException)t;
            	}
            	throw err;
            }
            if (pageId.endsWith("emptyLoginRolesPage")) {
        		new RtmExport(wiParams).showMessage("Login denied role page not found");
        		return;
            }
            if (pageId.endsWith("EventConnector") || 
            		pageId.endsWith("ReportConnector")) {
            	pageId = pageId.endsWith("EventConnector") ? 
            			"EventConnector" : "ReportConnector" ;
            	boolean doIt = request.getMethod().equalsIgnoreCase("POST");
            	if (request.getParameter("tmp.reportShow") != null) {
            		doIt = true;
            	}
            	if (doIt) {
            		initConnectorHandler(wiParams, pageId);
            	} else {
            		new RtmExport(wiParams).showMessage(pageId  + " Running");
            		return;
            	}
            }
            // pagina nao encontrada
            if (wiParams.getPage() == null) {
                response.sendRedirect("/" + projId + jspPage);
                return;
            }

            // registra o timestamp do request
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
            wiParams.getWIMap().put("wi.request.timestamp", sdf.format(new Date()));

            // insere no contexto a pagina pai quando vem de um include 
            if (includeURI != null) {
            	wiParams.getWIMap().put("wi.page.parent", requestPageId);
            }
            
            // identifica o projeto e a pagina que será executada
            Function.setThreadName(df.format(new Date()) + " - Proj:" + 
            		wiParams.getProject().getId() + ",Page:" + 
            		wiParams.getPage().getId());

            // identificando jspFilename				
            String jspFilename = (String) request.getAttribute("wiJspFilename");
            if (jspFilename != null) {
                wiParams.getWIMap().put("wi.jsp.filename", jspFilename);
            }
           
            // validação do controle de acesso
            boolean isOk = new ExecuteSecurity(wiParams).check();
            engineLog(wiParams, isOk);
            if (!isOk) {
                return;
            }

        	// valida se a sessão expirou
        	if (wiParams.getWISession().isNew() && includeURI == null) {
        		String sec = wiParams.getPage().getSecurity().trim();
                if (sec.equals("ON") && !RtmFunction.bypassSecurity(wiParams)) {
                    if (!loginPage.trim().equals("")) {
                    	WIMap wiMap = wiParams.getWIMap();
                    	wiMap.remove("tmp.msgsecurevar");
                    	wiMap.put("tmp.msglogin", "Login Expirado");
                    	wiParams.sendRedirect(loginPage + ".wsp", wiMap, true);
                    	redirect(wiParams);
                    } else {
                    	response.sendError(HttpServletResponse.SC_FORBIDDEN);
                    }
                	return;
                }
        	}

            // desabilitando cache do browser
            if (!wiParams.getPage().getBrowserCache().equals("ON")) {
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Cache-Control", "must-revalidate, no-store");
                response.setDateHeader("Expires", 0);
            }

            // ajusta wi.page.prev vindo de um redirecionamento  
            preRedirect(wiParams.getWIMap());
            // gerando o controle de wiToken	
            pageToken(wiParams);
            // recupera o map de variaveis
            WIMap wiMap = wiParams.getWIMap();
            // processa pos-pagina
            if (wiParams.getPage().isErrorPage()
                        || pageId.equals("EventConnector")) {
                jspPos = "";
            }
            if (!jspPos.equals("") && includeURI == null) {
                Page origPage = wiParams.getPage();
                wiParams.setParameter(ExecuteParamsEnum.PAGE, null);
                // Carrega a definição da página anterior para saber 
                // se é a de login ou se pode ser acessada sem login
                String jspPosDef = "/" + pagePrev + ".jsp";
                doJsp(request, response, wiParams, jspPosDef, false);
                if (RtmFunction.checkLoginForPosPage(wiParams)) {
                	Page posPage = wiParams.getPage();
                	boolean tokenOK = wiMap.get("wi.token.ok").equals("true");
                	if (!posPage.isCheckPosToken()) tokenOK = true;
                	if (!tokenOK) {
                		String dest = posPage.getId() + ".wsp";
                        wiParams.sendRedirect(dest, wiMap, true);
                	}
                	// suporte a syncronismo ativado
                	Object sync = getSyncObj(wiParams, jspPos, "pos");
                	synchronized (sync) {
                		wiMap.put("wi.page.type", "pos");
	                    // processa pos-pagina da página comum
	                	String prepospage = wiParams.getProject().getPrePosPage(); 
	                	if (tokenOK && !prepospage.equals("")) {
	                		String jspFile = "/" + prepospage + "_pos.jsp";
	                        doJsp(request, response, wiParams, jspFile, false);
	                        if (exits(wiParams)) {
	                            wiParams.setParameter(ExecuteParamsEnum.PAGE, origPage);
	                            endTransaction(wiParams);
	                            return;
	                        }
	                	}
	                	// Se tem permissão executa o pos da pagina
		            	if (tokenOK && checkRoles(wiParams)) {
	            			checkValidations(wiParams, false);
	            			if (wiMap.get("wi.validation.ok").equals("true")) {
			            		doJsp(request, response, wiParams, jspPos, false);
	            			}
		            	}	
		            	wiMap.remove("wi.page.type");
                	}	
                }
                wiParams.setParameter(ExecuteParamsEnum.PAGE, origPage);
                endTransaction(wiParams);
            }
            if (exits(wiParams)) {
                return;
            }
            // controle de pagina com login
            if (wiParams.getProject().getLoginActive().equals("ON")) {
            	boolean doLogin = pagePrev.equals(loginPage) 
            			&& wiMap.get("pvt.login.accept").equals("true");
            	if (includeURI == null) wiMap.remove("pvt.login.accept");
            	if (pageId.equals(loginPage)) {
            		// remove o SingleSignOn
                	String ssoId = RtmFunction.getSingleSignOnId(wiParams);
                    if (!ssoId.equals("")) {
	                    String addr = wiParams.getHttpRequest().getRemoteAddr();
	                	CrossContext cross = CrossContextFactory.getInstance();
	                	if (cross != null) cross.removeSSO(ssoId + "-" + addr, projId);
                    }	
            		// remove o login
                	ExecuteLogin.removeLogin(wiMap);
            		wiMap.put("pvt.login.accept", "true");            		
            	}
            	boolean isLogged = new ExecuteLogin(wiParams).execute(doLogin);
            	if (!isLogged) { 
            		String projAux = projPrev;
            		String pageAux = pagePrev;
                    if (projAux.equals("")) projAux = projId;
                    if (pageAux.equals("")) pageAux = pageId;
                    wiMap.put("wi.proj.prev.bkp", projAux);
                    wiMap.put("wi.page.prev.bkp", pageAux);
            		exits(wiParams);
            		return;
            	}
            }

            // controle MD5 e SHA1
            securityCrypto(wiParams);
            if (pageId.equals("EventConnector")) {
                new EventCore().execute(wiParams);
            } if (pageId.equals("ReportConnector")) {
            	Class rpCL = Class.forName("br.com.webinside.runtime.report.CoreReport");
            	Constructor cons = rpCL.getConstructor(ExecuteParams.class, ReportRef.class);
            	((CoreCommon)cons.newInstance(wiParams, null)).execute();
            } else {
                boolean errorPage = wiParams.getPage().isErrorPage();
            	// suporte a syncronismo ativado
                Object sync = getSyncObj(wiParams, jspPre, "pre");
            	synchronized (sync) {
	                // processa pre-pagina da página comum
	            	String prepospage = wiParams.getProject().getPrePosPage(); 
	            	if (!prepospage.equals("")) {
	            		String jspFile = "/" + prepospage + "_pre.jsp";
	            		doJsp(request, response, wiParams, jspFile, errorPage);
	                    if (exits(wiParams)) {
	                    	endTransaction(wiParams);
	                        return;
	                    }
	            	}
	                // Se tem permissão executa o pre da pagina
	            	if (checkRoles(wiParams)) {
	            		checkValidations(wiParams, true);
            			doJsp(request, response, wiParams, jspPre, errorPage);
	            	}	
	                endTransaction(wiParams);
	                if (exits(wiParams)) {
	                    return;
	                }
				}
                // executa o sincronismo do contexto para que os 
                // includes funcionem corretamente
            	new Context(wiParams).syncWIMap();
                // processa pagina
                if (wiParams.getOutputStream(false) == null) {
                    request.setAttribute("wiPage", pageId);
                    wiParams.setContentType(wiParams.getPage().getMime());
                    doJsp(request, response, wiParams, jspPage, errorPage);
                    redirect(wiParams);
                    exception(wiParams);
                }
            }
        } catch (Exception err) {
	    	try {
	    		session.getAttributeNames();
	    		// Tratamento para detectar sessao invalidada
	            if (err instanceof IOException) {
	                throw (IOException) err;
	            }
	            if (err instanceof ServletException) {
	                throw (ServletException) err;
	            }
	            throw new ServletException(err);
	    	} catch (IllegalStateException isex) { 
				responseBlocked(response, projId, "INVALIDATED");
	    	}
        } finally {
        	boolean invalidate = false;
        	if (wiParams != null && wiParams.getWIMap() != null) {
        		String sesssionId = wiParams.getWIMap().get("wi.session.id");
            	invalidate = sesssionId.equals("invalidate");
        	}
            try {
    			pageTimeLog(wiParams, initialTime);
                exitCore(wiParams, parentParams);
            } catch (Exception err) {
                StringWriter out = new StringWriter();
                out.write("ERROR CLOSING DATABASE POOL\n");
                err.printStackTrace(new PrintWriter(out));
                System.err.println(out.toString());
            }
            controlSessionRequestsEnd(request, response, session);
            try {
            	if (invalidate) session.invalidate();
			} catch (IllegalStateException isex) { }	
			Function.setThreadName(df.format(new Date()) + " - End of Execute");
        }
    }

    private boolean controlSessionRequestsIni(HttpServletRequest request, 
    		HttpServletResponse response, HttpSession session) throws IOException {
		long thdId = Thread.currentThread().getId();
    	ReqsMapNode duplicatedRequestNode = null;
    	synchronized (session) {
    		Map<Long, ReqsMapNode> reqsMap = getReqsMap(request.getSession());
			if (reqsMap != null && !reqsMap.containsKey(thdId)) {
				ReqsMapNode node = new ReqsMapNode(request);
				if (reqsMap.size() + 1 > maxSesReqsLog) {
					String type = (reqsMap.size()  + 1 > maxSesReqsBlock) ? "BLOCK-MAX" : "LOG-MAX";
		        	String projId = StringA.piece(request.getRequestURI(), "/", 2);
		    		if (initialProjId != null && !initialProjId.equals("")) projId = initialProjId;
					logBlockReq(request, reqsMap, type, projId, node.url, node.params);					
					if (reqsMap.size() + 1 > maxSesReqsBlock) {
						responseBlocked(response, projId, type);
						session.invalidate();
						return true;
					}
				}
				if (duplicatedRequest(reqsMap, node)) duplicatedRequestNode = node;
				reqsMap.put(thdId, node);
			}
		}
		if (duplicatedRequestNode != null) {
			Function.sleep(2000);
		}
		synchronized (session) {
    		Map<Long, ReqsMapNode> reqsMap = getReqsMap(request.getSession());
			if (reqsMap != null && duplicatedRequestNode != null) {
        		String url = duplicatedRequestNode.url;
        		String params = duplicatedRequestNode.params;
				String projId = StringA.piece(request.getRequestURI(), "/", 2);
				if (initialProjId != null && !initialProjId.equals("")) projId = initialProjId;
	        	for (long nodeThdId : new HashSet<Long>(reqsMap.keySet())) {
	        		if (nodeThdId == thdId) continue;
	        		ReqsMapNode node = reqsMap.get(nodeThdId);
	    			if (node.url.equals(url) && node.params.equals(params) && 
	    					node.time > duplicatedRequestNode.time) {
	    				if (duplicatedRequestNode.log) {
	    					logBlockReq(request, reqsMap, "BLOCK-DUPLICATED", projId, url, params);
	    				}	
	        			responseBlocked(response, projId, "BLOCK-DUPLICATED");
	    				reqsMap.remove(thdId);
		        		return true;
    				}
	    		}
			}	
		}
		return false;
    }

    private void controlSessionRequestsEnd(HttpServletRequest request, 
    		HttpServletResponse response, HttpSession session) {
		synchronized (session) {
			try {
				long thdId = Thread.currentThread().getId();
				Map reqsMap = (Map) session.getAttribute(SESSION_REQS_MAP);
				if (reqsMap != null) {
					ReqsMapNode mapNode = (ReqsMapNode) reqsMap.get(thdId);
					if (mapNode != null && mapNode.reqId == request.hashCode()) {
						reqsMap.remove(thdId);
					}
				}
			} catch (IllegalStateException isex) { }	
		}	
	}
    
    private Map<Long, ReqsMapNode> getReqsMap(HttpSession session) {
		Map<Long, ReqsMapNode> reqsMap = null;
		try {
			reqsMap = (Map) session.getAttribute(SESSION_REQS_MAP);
			if (reqsMap == null) {
				reqsMap = new LinkedHashMap();
				session.setAttribute(SESSION_REQS_MAP, reqsMap);
			}
		} catch (IllegalStateException isex) { }
    	return reqsMap;
    }
    
	private void doJsp(HttpServletRequest request, HttpServletResponse response,
			ExecuteParams wiParams, String jsp, boolean errorPage) 
			throws ServletException, IOException {
		if (!errorPage) {
			String jspId = StringA.piece(jsp, ".jsp", 1);
		    wiParams.getWIMap().put("wi.jsp.filename", jspId);
		    request.setAttribute("wiJspFilename", jspId);
		}                
		File f = new File(getServletContext().getRealPath(jsp));
		if (f.exists() || jspList.contains(jsp)) { 
			RequestDispatcher rd = getServletContext().getRequestDispatcher(jsp); 
			rd.include(request, response);
		}	
	}

	private void endTransaction(ExecuteParams wiParams) {
    	new CoreTransactionElement(wiParams, null).endTransaction(); 
	}
	
    private boolean exits(ExecuteParams wiParams)
        throws Exception {
        resetCondBlock(wiParams);
        redirect(wiParams);
        exception(wiParams);
        HttpServletRequest request = wiParams.getHttpRequest();
        if (request.getAttribute("wiExit") != null) {
            return true;
        }
        return false;
    }

    private void exitCore(ExecuteParams wiParams, ExecuteParams parentParams) {
        if (wiParams != null) {
        	if (!wiParams.isJspInclude()) {
            	IntFunction.clearSVMap(wiParams.getWISession());
        	}
            wiParams.getDatabaseAliases().closeAll();
            boolean valid = wiParams.getWISession().isValid();
            if (wiParams.getWIMap() != null && valid) {
                new Context(wiParams).putWIMap(wiParams.getWIMap());
            }
            wiParams.clear();
            ExecuteParams.set(null);
            if (parentParams != null) {
            	HttpServletRequest request = parentParams.getHttpRequest();
            	request.setAttribute("wiParams", parentParams);
            	ExecuteParams.set(parentParams);
            } 
        }
    }

    private boolean duplicatedRequest(Map<Long, ReqsMapNode> reqsMap, ReqsMapNode node) {
    	for (ReqsMapNode auxNode : reqsMap.values()) {
			if (auxNode.url.equals(node.url) && 
					auxNode.params.equals(node.params)) {
				return true;
			}
		}
    	return false;
    }
    
    private void engineLog(ExecuteParams wiParams, boolean isOk) {
        String engineLog = wiParams.getProject().getRequestLog();
        if (engineLog.trim().equals("")) {
            return;
        }
        String logDir = wiParams.getErrorLog().getParentDir();
        LogsGenerator log = LogsGenerator.getInstance(logDir, "engine.log");
        Map<String, String> logAttrs = new LinkedHashMap<String, String>();
        logAttrs.put("PAGE", wiParams.getPage().getId());
        logAttrs.put("IP", wiParams.getHttpRequest().getRemoteAddr());
        String text = null; 
        if (!isOk) {
            text = "Request Denied by CoreSecurity";
        }
        String detail = null;
        if (engineLog.trim().equals("FULL")) {
        	detail = getRequestParamsDetail(wiParams);
        }
        log.write(logAttrs, text, detail);
    }

    private void pageTimeLog(ExecuteParams wiParams, long initialTime) {
		String pageTimeLog = 
			getServletContext().getInitParameter("pageTimeLog");
    	if (pageTimeLog != null && wiParams != null) {
            long now = System.currentTimeMillis();
			int seg = Function.parseInt(pageTimeLog.trim());
			if (now - initialTime >= seg * 1000) {
                String logDir = wiParams.getErrorLog().getParentDir();
                LogsGenerator log = 
                	LogsGenerator.getInstance(logDir, "pagetime.log");
                Map<String, String> logAttrs = new LinkedHashMap<String, String>();
                logAttrs.put("PAGE", wiParams.getPage().getId());
                logAttrs.put("IP", wiParams.getHttpRequest().getRemoteAddr());
                String text = "Time: " + (now - initialTime) + " ms";
            	String detail = getRequestParamsDetail(wiParams);
                log.write(logAttrs, text, detail);
			}
    	}
    }
    
    private String getRequestParamsDetail(ExecuteParams wiParams) {
        StringA aux = new StringA();
        aux.append("REQUEST PARAMS:\r\n");
        Map params = wiParams.getHttpParameters();
        Iterator it = params.keySet().iterator();
        while (it.hasNext()) {
            String name = (String) it.next();
            String value = (String) params.get(name);
            aux.append(name + " = " + value + "\r\n");
        }
        return aux.toString();
    }
    
    private void preRedirect(WIMap wiMap) {
        String projprev = wiMap.get("wi.proj.prev").trim();
        String projprevBkp = wiMap.get("wi.proj.prev.bkp").trim();
        String pageprev = wiMap.get("wi.page.prev").trim();
        String pageprevBkp = wiMap.get("wi.page.prev.bkp").trim();
        if (projprev.equals("") && !projprevBkp.equals("")) {
            wiMap.put("wi.proj.prev", projprevBkp);
        }
        if (pageprev.equals("") && !pageprevBkp.equals("")) {
            wiMap.put("wi.page.prev", pageprevBkp);
        }
        wiMap.remove("wi.proj.prev.bkp");
        wiMap.remove("wi.page.prev.bkp");
    }

    private void securityCrypto(ExecuteParams wiParams) {
        String loginPag = StringA.piece(wiParams.getProject().getLoginPage(), ".wsp", 1);
        if (wiParams.getProject().getLoginCrypto().equals("MD5")) {
            if (wiParams.getPage().getId().equals(loginPag)) {
                wiParams.getWIMap().put("wi.pwd.md5", Function.randomKey());
            }
        }
        if (wiParams.getProject().getLoginCrypto().equals("SHA1")) {
            if (wiParams.getPage().getId().equals(loginPag)) {
                wiParams.getWIMap().put("wi.pwd.sha1", Function.randomKey());
            }
        }
    }

    private void pageToken(ExecuteParams wiParams) {
        HttpServletRequest request = wiParams.getHttpRequest();
        WIMap context = wiParams.getWIMap();
        String received = context.get("wi.token.received");
        String proj = wiParams.getProject().getId();
        String page = wiParams.getPage().getId();
        String projprev = context.get("wi.token.proj");
        if (projprev.equals("")) {
            projprev = context.get("wi.proj.prev").toLowerCase();
        }
        if (projprev.equals("")) projprev = proj;
        String pageprev = context.get("wi.token.page");
        if (pageprev.equals("")) {
            pageprev = context.get("wi.page.prev").toLowerCase();
        }
        if ((!pageprev.equals("")) && (!received.equals(""))) {
            String oldkey =
                (String) pageTokenMap(wiParams).get(projprev + "_" + pageprev);
            if (oldkey == null) {
                oldkey = "";
            }
            Boolean ok = (Boolean) request.getAttribute("wiTokenOk");
            if (ok != null && ok) oldkey = received;
            if (received.equalsIgnoreCase(oldkey)) {
                context.put("wi.token.ok", "true");
                if (ok == null || !ok) {
                	request.setAttribute("wiTokenOk", true);
                	pageTokenMap(wiParams).remove(projprev + "_" + pageprev);
                }	
            } else {
                context.put("wi.token.ok", "false");
            }
        }

        // Gerando token
        String token = (String) request.getAttribute("wiToken");
        if (token == null || token.equals("")) {
        	token = Function.randomKey();
        	request.setAttribute("wiToken", token);
        }
        context.put("wi.token", token);
        pageTokenMap(wiParams).put(proj + "_" + page.toLowerCase(), token);
    }

    private Map pageTokenMap(ExecuteParams wiParams) {
		WISession wisession = wiParams.getWISession();
		Map map = (Map) wisession.getAttribute("wiTokenMap");
		if (map == null) {
	    	synchronized (wisession.getHttpSession()) {
	        	map = (Map) wisession.getAttribute("wiTokenMap");
	        	if (map == null) {
	        		map = new HashMap();
	        		wisession.setAttribute("wiTokenMap", map);
	        	}
			}
		}
    	return map;
    }
    
    private void fileUpload(ExecuteParams wiParams) throws FileUploadException {
        FileUpload fileUpload = new FileUpload();
        fileUpload.parse(wiParams.getHttpRequest());
        wiParams.setParameter(ExecuteParamsEnum.FILE_UPLOAD, fileUpload);
        Map haux = fileUpload.getFieldsMap();
        for (Iterator it = haux.keySet().iterator(); it.hasNext();) {
            String name = (String) it.next();
            List vValues = (List) haux.get(name);
            String[] values = new String[0];
            values = (String[]) vValues.toArray(values);
            wiParams.setHttpParameter(name, values);
        }
    }

    private void exception(ExecuteParams wiParams)
        throws ServletException {
        HttpServletRequest request = wiParams.getHttpRequest();
        Exception exception = (Exception) request.getAttribute("wiException");
        if (exception != null) {
            throw new ServletException(exception);
        }
    }

    private void resetCondBlock(ExecuteParams wiParams) {
    	wiParams.getWIMap().remove("wi.block.cond");
    	wiParams.getWIMap().remove("wi.block.var");
    }	
        
    private void redirect(ExecuteParams wiParams)
        throws IOException {
        HttpServletRequest request = wiParams.getHttpRequest();
        HttpServletResponse response = wiParams.getHttpResponse();
        String redirect = (String) request.getAttribute("wiRedirect");
        if (redirect != null) {
            request.setAttribute("wiExit", "true");
            response.sendRedirect(redirect);
        }
    }
    
    private void initConnectorHandler(ExecuteParams wiParams, String id) 
    throws Exception {
		AbstractProject proj = wiParams.getProject(); 
        Page page = new Page(id);
        page.setTitle(id);
        page.setSecurity("OFF");
        page.setNoLogin("ON");
        page.setMime("html");
        wiParams.setParameter(ExecuteParamsEnum.PAGE, page);
        WIMap wiMap = new Context(wiParams).getWIMap(true);
        wiParams.setParameter(ExecuteParamsEnum.WI_MAP, wiMap);
        wiParams.getDatabaseAliases().setLog(proj.getSqlLog());
        wiParams.getDatabaseAliases().loadDatabases(proj);
    }
    
    private Object getSyncObj(ExecuteParams wiParams, String jsp, String type) {
    	Object sync = new Object();
    	AbstractProject prj = wiParams.getProject();
    	String pageId = jsp.substring(0, jsp.indexOf("_" + type + ".jsp"));
    	pageId = StringA.piece(pageId, "/", 2, 0);
    	Page page = (Page) prj.getPages().getElement(pageId);
    	if (page != null) {
	    	if ((type.equals("pre") && page.isSyncPre()) ||
	    		(type.equals("pos") && page.isSyncPos())) {
            	sync = syncTokens.get(jsp);
            	if (sync == null) {
	 	    		synchronized (syncTokens) {
		            	sync = syncTokens.get(jsp);
		            	if (sync == null) {
		            		sync = new Object();
		            		syncTokens.put(jsp, sync);
		            	}
					}
            	}	
	    	}
    	}	
    	return sync;
    }
    
    private boolean checkRoles(ExecuteParams wiParams) {
    	AbstractProject proj = wiParams.getProject();
    	Page page = wiParams.getPage();
    	if (page.getRoles().trim().equals("") || 
    			page.getNoLogin().equals("ON") ||
    			!proj.getLoginActive().equals("ON")) return true;
    	WIMap wiMap = wiParams.getWIMap();
    	String[] roles = page.getRoles().split(",");
    	String[] mods = wiMap.get("pvt.login.role.modules").split(",");
		for (String role : roles) {
			String pageRole = Producer.execute(wiMap, role).trim();
			for (String mod : mods) {
				String modRole = "module_" + mod.trim();
				if (modRole.equalsIgnoreCase(pageRole)) return true;
			}
		}
    	int size = Function.parseInt(wiMap.get("pvt.login.role.size()"));
    	for (int i = 1; i <= size; i++) {
			String loginRole = wiMap.get("pvt.login.role[" + i + "].name");
			for (String role : roles) {
				String pageRole = Producer.execute(wiMap, role).trim();
				if (loginRole.equalsIgnoreCase(pageRole)) return true;
			}
		}
        wiMap.put("tmp.denied_page_role", page.getId());
        String rolesPage = wiParams.getProject().getLoginRolesPage();
        if (rolesPage.equals("")) rolesPage = "emptyLoginRolesPage.wsp";
        wiParams.sendRedirect(rolesPage, wiMap, true);
    	return false;
    }

    private void checkValidations(ExecuteParams wiParams, boolean pre) {
		WIMap wiMap = wiParams.getWIMap();
		if (pre) {
			String condition = wiParams.getPage().getPreValidationCondition();
			if (wiMap.get("tmp.action").equals("validate")) {
				condition = "true";
			}
			if (!new Condition(wiMap, condition).execute()) return;
		}
		if (wiMap.get("wi.validation.ok").equals("")) {
	    	Validator.validate(wiParams);
		}
    }
    
    private void logBlockReq(HttpServletRequest request, Map<Long, ReqsMapNode> reqsMap,
    		String type, String projId, String url, String params) {
		long thdId = Thread.currentThread().getId();
		String logDir = getServletContext().getRealPath("/WEB-INF/logs");
		LogsGenerator log = LogsGenerator.getInstance(logDir, "requests-blocked.log");
        Map<String, String> logAttrs = new LinkedHashMap<String, String>();
		logAttrs.put("THREAD", thdId + "");
		logAttrs.put("TYPE", type);
		logAttrs.put("PRJ", projId);
		logAttrs.put("URL", url);
		logAttrs.put("IP", request.getRemoteAddr());
		logAttrs.put("SESSION", request.getSession().getId());
		StringBuilder content = new StringBuilder();
		content.append("  <PARAMS><![CDATA[" + params + "]]></PARAMS>\r\n");
		content.append("  <HEADER NAME=\"user-agent\" VALUE=\"" + 
				StringA.getXml(request.getHeader("user-agent")) + "\"\\>\r\n");
		content.append("  <HEADER NAME=\"referer\" VALUE=\"" + 
				StringA.getXml(request.getHeader("referer")) + "\"\\>\r\n");
		for (Long nodeThdId : reqsMap.keySet()) {
    		if (nodeThdId == thdId) continue;
			ReqsMapNode auxNode = reqsMap.get(nodeThdId);
			content.append("  <RUNNING THREAD=\"" + nodeThdId + "\"");
			if (auxNode.url.equals(url) && auxNode.params.equals(params)) {
				String time = new SimpleDateFormat("HH:mm:ss.SSS").format(auxNode.time);
				content.append(" DUPLICATED=\"TRUE\" START=\"" + time + "\"/>\r\n");
				auxNode.log = false;
			} else {
				content.append(" URL=\"" + StringA.getXml(auxNode.url) + "\">\r\n");
				content.append("    <PARAMS><![CDATA[" + auxNode.params + "]]></PARAMS>\r\n");
				content.append("  </RUNNING>\r\n");
			}	
		}
		log.write(logAttrs, content.toString());
    }
    
    private void responseBlocked(HttpServletResponse response, String prjId, String type) 
    throws IOException {
		response.setContentType("text/html");
		response.getWriter().println("WI5 - Blocked Session Request (" + type + ")\r\n");
		if (type.equals("BLOCK-MAX")) {
			String loc = "/" + prjId + "/wi5_blocked_request.html?type=" + type;
			response.getWriter().println("<script>top.location='" + loc + "'</script>\r\n");
		}	
    }
    
    public static String strParams(Map<String, String[]> params) {
    	StringBuilder sb = new StringBuilder();
    	for (Map.Entry<String, String[]> entry : params.entrySet()) {
    		if (sb.length() != 0 ) sb.append("&");
			sb.append(entry.getKey() + "=");
			for(String value : entry.getValue()) {
	    		if (sb.charAt(sb.length()-1) != '=') sb.append(",");
			    sb.append(value.trim());
			}
    	}
   		return sb.toString();
    }    
    
    private class ReqsMapNode {
    	
    	private long reqId;
    	private String url;
    	private String params;
    	private long time;
    	private boolean log;
    	
    	private ReqsMapNode(HttpServletRequest request) {
    		reqId = request.hashCode();
    		url = request.getMethod() + ":" + request.getRequestURL().toString();
    		if (request.getQueryString() != null) url += "?" + request.getQueryString();
    		params = strParams(request.getParameterMap());
    		this.time = new Date().getTime();
    		this.log = true;
    	}	

		@Override
		public String toString() {
			return url;
		}
    	
    }

}
