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

import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.soap.MimeHeaders;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPConstants;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPPart;

import br.com.webinside.runtime.component.AbstractGrid;
import br.com.webinside.runtime.component.AbstractProject;
import br.com.webinside.runtime.component.GridXmlOut;
import br.com.webinside.runtime.component.Page;
import br.com.webinside.runtime.component.WebService;
import br.com.webinside.runtime.component.WebServiceFault;
import br.com.webinside.runtime.component.WebServiceMethod;
import br.com.webinside.runtime.integration.Condition;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.util.I18N;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class WebServiceCore {
    private WebServiceParams params;
    private AbstractProject project;
    private WebService servico;
    private WebServiceMethod metodo;
    private WIMap wiMap = new WIMap();

    /**
     * Creates a new WebServiceCore object.
     *
     * @param params DOCUMENT ME!
     */
    public WebServiceCore(WebServiceParams params) {
        this.params = params;
        this.project = params.getWiParams().getProject();
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        if (!validRequest()) {
            return;
        }
        Page page = new Page(metodo.getPage());
        params.getWiParams().setParameter(ExecuteParamsEnum.PAGE, page);
        try {
            wiMap = new Context(params.getWiParams()).getWIMap(false);
        } catch (Exception err) {
            return;
        }
        params.getWiParams().setParameter(ExecuteParamsEnum.WI_MAP, wiMap);
    	try {
        	String prefix = metodo.getObjIn().trim(); 
        	if (!prefix.equals("")) {
        		wiMap.put(prefix + ".wsmessage()", params.requestMsg);
        	}	
        } catch (Exception err) {
        	// ignorado
        }
        receiveParameters();
        if (!validateConditions()) {
            return;
        }
        String sessionId = params.getWiParams().getWISession().getId();
        MimeHeaders headers = params.responseObj.getMimeHeaders();
        headers.addHeader("JSESSIONID", sessionId);
        boolean result = executePage();
        String debug = wiMap.get("pvt.debug.webservice").trim();
        if (!debug.equalsIgnoreCase("true")) {
        	debug = wiMap.get("tmp.debug.webservice").trim();
        }
        if (debug.trim().equalsIgnoreCase("true")) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
        	params.debugId = sdf.format(new Date()); 
        }
        if (result) {
            sendResponse();
        }
        new Context(params.getWiParams()).putWIMap(wiMap);
    }

    private boolean validRequest() {
        if (project == null) {
            return sendFault(i18n("Projeto não encontrado"));
        }
        if (params.getService().equals("")) {
            return sendFault(i18n("WebService não especificado"));
        }
        if (params.getMethod().equals("")) {
            return sendFault(i18n("Método não especificado"));
        }
        params.getWiParams().includeCode("/webservices/" + params.getService());
        servico =
            (WebService) project.getWebServices().getElement(params.getService());
        if (servico == null) {
            return sendFault(i18n("WebService não encontrado"));
        }
        metodo = servico.getMethod(params.getMethod());
        if (metodo == null) {
            return sendFault(i18n("Método não encontrado"));
        }
        return true;
    }

    private boolean sendFault(String text) {
        try {
            String code = "SOAP-ENV:Server";
            SOAPPart part = params.responseObj.getSOAPPart();  
            SOAPBody body = part.getEnvelope().getBody();
            SOAPFault fault = body.addFault();
            fault.setFaultCode(code);
            fault.setFaultString(text);
        } catch (SOAPException err) {
        }
        return false;
    }

    private void receiveParameters() {
        // load template
        WIMap template = new WIMap();
        CoreXmlImport impTemplate = new CoreXmlImport(template, metodo.getId());
        String header = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n";
        String fullXML = header + "<" + metodo.getId() + ">\r\n";
        fullXML += produceRequestGridXML(metodo.getXmlIn());
        fullXML += ("\r\n</" + metodo.getId() + ">\r\n");
        impTemplate.execute(fullXML);
        // load received
        WIMap received = new WIMap();
        CoreXmlImport impReceived =
            new CoreXmlImport(received, params.getMethod());
        fullXML = header + params.requestMsg.trim();
        fullXML = StringA.change(fullXML, "SOAP:", "", false);
        fullXML = StringA.change(fullXML, "SOAP-ENV:", "", false);
        fullXML = StringA.change(fullXML, "xsi:", "", false);
        fullXML = StringA.change(fullXML, "xsd:", "", false);
        impReceived.execute(fullXML);
        // populate context
        Map htable = received.getAsMap();
        for (Iterator it = htable.keySet().iterator(); it.hasNext();) {
            String key = (String) it.next();
            if (template.containsKey(clear(key)) && !key.endsWith(".")) {
                String name = StringA.piece(key, ".", 2, 0);
                String tpaux = template.get(clear(key));
                String tprefix = StringA.changeChars(tpaux, "|", "").trim();
                String prefix = "";
                if (!metodo.getObjIn().trim().equals("")) {
                    prefix = metodo.getObjIn().trim() + ".";
                }
                if (prefix.equals("")) {
                    prefix = tprefix;
                    name = "";
                }
                wiMap.put(prefix + name, (String) htable.get(key));
            }
        }
    }

    private String clear(String key) {
        StringA resp = new StringA();
        int from = 0;
        int pos = 0;
        while ((pos = key.indexOf("[", from)) > -1) {
            int end = key.indexOf("]", pos);
            if (end == -1) {
                end = key.length();
            }
            resp.append(StringA.mid(key, from, pos - 1));
            from = end + 1;
        }
        resp.append(StringA.mid(key, from, key.length()));
        return resp.toString();
    }

    private boolean validateConditions() {
        List list = metodo.getFaults();
        for (int i = 0; i < list.size(); i++) {
            WebServiceFault fault = (WebServiceFault) list.get(i);
            Condition cond =
                new Condition(wiMap, fault.getCondition());
            if (cond.execute()) {
                try {
                	SOAPPart part = params.responseObj.getSOAPPart();
                    SOAPEnvelope env = part.getEnvelope();
                    SOAPFault soapfault = env.getBody().addFault();
                    soapfault.setFaultCode(fault.getCode());
                    soapfault.setFaultString(fault.getMessage());
                } catch (SOAPException err) {
                }
                return false;
            }
        }
        return true;
    }

    private boolean executePage() {
        StringWriter screen = new StringWriter();
        ExecuteParams execParams = params.getWiParams();
        execParams.setParameter(ExecuteParamsEnum.OUT_WRITER, screen);
        execParams.getDatabaseAliases().setLog(project.getSqlLog());
        execParams.getDatabaseAliases().loadDatabases(project);
        execParams.setRequestAttribute("wiWebService", "true");
        execParams.includePrePage(execParams.getPage(), true);
        execParams.getDatabaseAliases().closeAll();
        if (screen.toString().length() > 0) {
            sendFault(screen.toString());
            return false;
        }
        return true;
    }

    private void sendResponse() {
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        prod.setXml(true);
        prod.setInput(produceResponseGridXML());
        params.getProducer().setParam(prod);
        params.getProducer().execute();
        responseMessage(prod.getOutput());
    }

    private void responseMessage(String textXML) {
        try {
            SOAPEnvelope env = params.responseObj.getSOAPPart().getEnvelope();
            Name name = env.createName(metodo.getId() + "Response");
            SOAPElement ele = env.getBody().addChildElement(name);
            ele.addNamespaceDeclaration("ns1", "urn:webinside");
            ele.setPrefix("ns1");            
            ele.setEncodingStyle(SOAPConstants.URI_NS_SOAP_ENCODING);
            int from = 0;
            int pos = -1;
            while ((pos = textXML.indexOf("<", from)) > -1) {
                int end = textXML.indexOf(">", pos);
                if (end == -1) {
                    end = textXML.length();
                }
                String fulltag = StringA.mid(textXML, pos + 1, end - 1).trim();
                if (!fulltag.startsWith("/")) {
                    String text = null;
                    String tag = StringA.piece(fulltag, " ", 1);
                    if (fulltag.endsWith("/")) {
                        tag = StringA.changeChars(tag, "/", "").trim();
                        String key = tag;
                        if (!ele.getLocalName().equals(name.getLocalName())) {
                        	SOAPElement auxele = ele;
                        	while (!auxele.getLocalName().equals(name.getLocalName())) {
                        		key = auxele.getLocalName() + "." + key;
                        		auxele = auxele.getParentElement();
                        	}
                        }
                        text = wiMap.get(metodo.getObjIn() + "." + key);                        
                    }
                    ele = ele.addChildElement(tag);
                    if (fulltag.indexOf("type=") > -1) {
                        int p1 = fulltag.indexOf("\"");
                        int p2 = fulltag.lastIndexOf("\"");
                        if ((p1 > -1) && (p2 > -1)) {
                            String type =
                                StringA.mid(fulltag, p1 + 1, p2 - 1).trim();
                            if (!type.startsWith("xsd:")) {
                                type = "xsd:" + type;
                            }
                            ele.addAttribute(env.createName("type", "xsi", ""),
                                type);
                        }
                    }
                    int pos2 = textXML.indexOf("<", end);
                    if (pos2 == -1) {
                        pos2 = textXML.length();
                    }
                    if (text == null) {
                        StringA textwil = new StringA();
                        textwil.set(StringA.mid(textXML, end + 1, pos2 - 1)
                                    .trim());
                        text = textwil.toString();
                    }
                    ele.addTextNode(text);
					if (fulltag.endsWith("/")) {
						ele = ele.getParentElement();
					}	
                } else {
                    ele = ele.getParentElement();
                }
                from = end + 1;
            }
        } catch (SOAPException err) {
        }
    }

    private String produceRequestGridXML(String template) {
        List lista = RtmFunction.listPipeNames(template);
        ExecuteParams wiParams = params.getWiParams();
        for (int i = 0; i < lista.size(); i++) {
            String id = (String) lista.get(i);
            if (id.toLowerCase().startsWith("grid.")) {
                String realId = StringA.mid(id, 5, id.length() - 1);
                if (!wiParams.getProject().getGrids().containsKey(realId)) {
                    wiParams.includeCode("/grids/" + realId + "/grid.jsp");
                }
                AbstractGrid grid =
                    (AbstractGrid) wiParams.getProject().getGrids().getElement(realId);
                if (grid == null || !(grid instanceof GridXmlOut)) continue;
                GridXmlOut gridXml = (GridXmlOut)grid;
                StringBuffer segment = new StringBuffer();
                addNode(segment, gridXml.getRoot(), true);
                addNode(segment, gridXml.getChild(), true);
                segment.append(gridXml.getXmlTemplate().trim() + "\r\n");
                addNode(segment, gridXml.getChild(), false);
                addNode(segment, gridXml.getRoot(), false);
                String gridText = produceRequestGridXML(segment.toString());
                template = 
                	StringA.change(template, "|grid." + realId + "|", gridText);
            }
        }
        return template;
    }
    
    private void addNode(StringBuffer segment, String node, boolean open) {
    	if (node == null || node.trim().equals("")) return;
    	if (open) {
    		segment.append("<");
    	} else {
    		segment.append("</");
    	}
       	segment.append(node + ">\r\n");
    }
    
    private String produceResponseGridXML() {
    	String template = metodo.getXmlOut();
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        prod.setInput(template);
        List lista = RtmFunction.listPipeNames(template);
        for (int i = 0; i < lista.size(); i++) {
            String id = (String) lista.get(i);
            if (!id.toLowerCase().startsWith("grid.")) {
                prod.addProtectedPipe(id);
            }
        }
        params.getProducer().setParam(prod);
        params.getProducer().execute();
        return prod.getOutput();
    }

    private String i18n(String text) {
        return new I18N().get(text);
    }
}
