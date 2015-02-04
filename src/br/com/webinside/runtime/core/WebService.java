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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPMessage;

import br.com.webinside.runtime.component.AbstractProject;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.I18N;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

/**
 * Classe principal de execução do servidor de WebServices do WI.
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class WebService extends HttpServlet {

	private static final long serialVersionUID = 1L;
    private static MessageFactory msgFactory = null;

	public void init() throws ServletException {
		super.init();
		String tmpDir = 
			getServletContext().getInitParameter("java.io.tmpdir");
    	Function.setTmpDir(tmpDir);
        try {
            msgFactory = MessageFactory.newInstance();
        } catch (Exception ex) {
            // Não deve dar erro.
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
    public void doGet(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException {
        WebServiceParams params = getWebServiceParams(request, response);
        doHtml(params);
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
    public void doPost(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException {
        WebServiceParams params = getWebServiceParams(request, response);
        String ct = request.getHeader("content-type");
        String mime = StringA.piece(ct, ";", 1).trim();
        boolean isXML = mime.equalsIgnoreCase("text/xml");
        if (isXML) {
            doXml(params);
        } else {
            doHtml(params);
        }
    }
    
    private WebServiceParams getWebServiceParams(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException {
        String projId = StringA.piece(request.getRequestURI(), "/", 2);
        request.setAttribute("wiProject", projId);
        ExecuteParams wiParams =
            new ExecuteParams(request, response, getServletContext());
        WIMap constants = new WIMap();
        Constants.populate(getServletContext(), constants);
        wiParams.setErrorLog(constants);
        request.setAttribute("wiParams", wiParams);
        String jsp = wiParams.getWICVS() + "/project.jsp";
        getServletContext().getRequestDispatcher(jsp).include(request, response);
        WebServiceParams params = new WebServiceParams(wiParams);
        String serviceId = StringA.piece(request.getRequestURI(), "/", 3);
        params.setService(StringA.piece(serviceId, ".", 1));
        return params;
    }

    private void doHtml(WebServiceParams params) throws IOException {
        HttpServletRequest request = params.getWiParams().getHttpRequest();
        HttpServletResponse response = params.getWiParams().getHttpResponse();
        boolean isWSDL = false;
        if (request.getRequestURI().toLowerCase().indexOf(".wsdl") > -1) {
            isWSDL = true;
        }
        if (isWSDL) {
            response.setStatus(HttpServletResponse.SC_OK);
            AbstractProject proj = params.getWiParams().getProject();
            br.com.webinside.runtime.component.WebService service = null;
            if (proj == null) {
                String message = i18n("Projeto não encontrado");
                new Export(params.getWiParams()).showMessage(message);
            } else {
                String serviceId = params.getService();
                params.getWiParams().includeCode("/webservices/" + serviceId);
                service =
                    (br.com.webinside.runtime.component.WebService) proj.getWebServices()
                            .getElement(serviceId);
                if (service == null) {
                    String message = i18n("WebService não encontrado");
                    new Export(params.getWiParams()).showMessage(message);
                } else {
                    response.setContentType("text/xml");
                    new WebServiceWsdl(params, service).Wsdl(response.getWriter(),
                        request);
                }
            }
        } else {
            String message = "WebService Connector Running";
            new Export(params.getWiParams()).showMessage(message);
        }
    }

    private void doXml(WebServiceParams params) throws ServletException {
        try {
            HttpServletRequest request = 
            	params.getWiParams().getHttpRequest();
            HttpServletResponse response =
                params.getWiParams().getHttpResponse();
            MimeHeaders headers = WebServiceFunction.getHeaders(request);
            InputStream is = request.getInputStream();
            params.requestObj = msgFactory.createMessage(headers, is);
            params.requestMsg = WebServiceFunction.toString(params.requestObj);
            params.responseObj = msgFactory.createMessage();
    		params.responseObj.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");
    		params.responseObj.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "iso-8859-1");
            getMethod(params);
            new WebServiceCore(params).execute();
            if (!params.debugId.equals("")) {
            	debugMessage(params, true);
            }
            if (params.responseObj != null) {
                SOAPMessage reply = params.responseObj;
                SOAPEnvelope env = reply.getSOAPPart().getEnvelope();
                env.addNamespaceDeclaration("xsi",
                    "http://www.w3.org/2001/XMLSchema-instance");
                env.addNamespaceDeclaration("xsd",
                    "http://www.w3.org/2001/XMLSchema");
                if (reply.saveRequired()) {
                    reply.saveChanges();
                }
                WebServiceFunction.putHeaders(reply.getMimeHeaders(), response);
                response.setStatus(HttpServletResponse.SC_OK);
                OutputStream os = response.getOutputStream();
                reply.writeTo(os);
                os.flush();
                if (!params.debugId.equals("")) {
                	debugMessage(params, false);
                }
            } else {            	
                response.setStatus(HttpServletResponse.SC_NO_CONTENT);
            }
        } catch (Exception ex) {
            throw new ServletException("JAXM POST failed " + ex.getMessage(), ex);
        }
    }

    private void getMethod(WebServiceParams params) {
        try {
        	String msg = params.requestMsg;
        	int p1 = msg.indexOf(":");
        	int p2 = msg.indexOf("xmlns");
            params.setMethod(msg.substring(p1 + 1, p2).trim());
        } catch (Exception err) {
        	// ignorado
        }
    }

    private void debugMessage(WebServiceParams params, boolean request) {
    	String dir = params.getWiParams().getErrorLog().getParentDir();
    	String name = "wsserver-" + params.debugId;
    	if (request) {
    		name += "-request.log";
    	} else {
    		name += "-response.log";
    	}
    	try {
        	FileOutputStream out = new FileOutputStream(new File(dir, name));
        	if (request) {
            	params.requestObj.writeTo(out);
        	} else {
            	params.responseObj.writeTo(out);
        	}
        	out.close();
        } catch (Exception err) {
        	// ignorado
        }
    }

    private String i18n(String text) {
        return new I18N().get(text);
    }
}
