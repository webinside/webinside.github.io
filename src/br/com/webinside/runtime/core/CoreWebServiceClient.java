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
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.Name;
import javax.xml.soap.SOAPConnection;
import javax.xml.soap.SOAPConnectionFactory;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPFault;
import javax.xml.soap.SOAPMessage;

import org.jdom.Document;

import br.com.webinside.runtime.component.GridRef;
import br.com.webinside.runtime.component.GridXmlOut;
import br.com.webinside.runtime.component.Host;
import br.com.webinside.runtime.component.Hosts;
import br.com.webinside.runtime.component.WebServiceClient;
import br.com.webinside.runtime.integration.Producer;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.net.NetFunction;
import br.com.webinside.runtime.net.ssl.HostnameVerifier;
import br.com.webinside.runtime.net.ssl.SSLSocketFactory;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.xml.Outputter;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class CoreWebServiceClient extends CoreCommon {
	
	private static final Map sessions = 
		Collections.synchronizedMap(new HashMap());
	private static SSLSocketFactory socketFactory;
	private static HostnameVerifier hostnameVerifier;
	
    private WebServiceClient webservice;
    private boolean pre = true;

    
    /**
     * Creates a new CoreWebServiceClient object.
     *
     * @param wiParams DOCUMENT ME!
     * @param webservice DOCUMENT ME!
     */
    public CoreWebServiceClient(ExecuteParams wiParams,
        WebServiceClient webservice) {
        this.wiParams = wiParams;
        this.webservice = webservice;
        element = webservice;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        if (!isValidCondition()) {
            return;
        }
        Hosts hosts = wiParams.getProject().getHosts(); 
        Host host = hosts.getHost(webservice.getHostId());
    	String service = webservice.getService().trim();
    	service = Producer.execute(wiMap, service);
    	String abort = wiMap.get("app.abort.webservice").trim();
        String errorMsg = null;
        if ((host != null) && (host.getProtocol().equals("WEBSERVICE"))) {
            try {
            	String endpoint = EngFunction.getEndpoint(host, 
            			service, wiParams);
                HttpURLConnection urlcon = 
                	NetFunction.openConnection(endpoint);
                urlcon.connect();
                urlcon.disconnect();
            } catch (IOException err) {
            	errorMsg = err + "";
            }
        }
        String sendXmlBody = produceGridXML(webservice.getSendXML());
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        prod.setXml(true);
        prod.setInput(sendXmlBody);
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        sendXmlBody = prod.getOutput().trim();
        wiMap.put("wi.wsclient.request", sendXmlBody);
        String wsDescription = webservice.getDescription();
        if (errorMsg != null) {
        	if (abort.equalsIgnoreCase("false")) {
                wiMap.put("wi.wsclient.error", errorMsg);
                String jspFile = wiMap.get("wi.jsp.filename");
                wiParams.getErrorLog().write(jspFile, wsDescription, errorMsg);
        	} else { 
        		EngFunction.hostError(wiParams, webservice.getHostId());
        	}	
            return;
        }
        try {
            SOAPConnectionFactory factory = SOAPConnectionFactory.newInstance();
            SOAPConnection connection = factory.createConnection();
            MessageFactory msgfactory = MessageFactory.newInstance();
            SOAPMessage message = msgfactory.createMessage();
            message.setProperty(SOAPMessage.WRITE_XML_DECLARATION, "true");
            message.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, "iso-8859-1");
            String action = webservice.getSoapAction();
            if (!action.equals("")) {
            	message.getMimeHeaders().addHeader("SOAPAction", action);
            }
            SOAPEnvelope env = message.getSOAPPart().getEnvelope();
            requestMessage(sendXmlBody, env);
            message.saveChanges();
        	String endpoint = EngFunction.getEndpoint(host, 
        			service, wiParams);
        	// suporte para funcionar com https
        	if (endpoint.startsWith("https://") && socketFactory == null) {
	            try {
	            	socketFactory = new SSLSocketFactory();
	            	hostnameVerifier = new HostnameVerifier();        	
	                HttpsURLConnection.setDefaultSSLSocketFactory(socketFactory);
	                HttpsURLConnection.setDefaultHostnameVerifier(hostnameVerifier);
	            } catch (Exception e) {
	                // nunca deve ocorrer
	            }
        	}
            String debug = wiParams.getWIMap().get("pvt.debug.webservice").trim();
            if (!debug.equalsIgnoreCase("true")) {
            	debug = wiParams.getWIMap().get("tmp.debug.webservice").trim();
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
            String debugId = sdf.format(new Date());
            if (debug.trim().equalsIgnoreCase("true")) {
            	debugMessage(message, debugId + "-request");
            }
            String skey = 
            	endpoint + "-" + wiParams.getWIMap().get("wi.session.id");
            if (sessions.get(skey) != null) {
            	endpoint += ";jsessionid=" + (String)sessions.get(skey);
            }
            SOAPMessage reply = connection.call(message, endpoint);
            String[] jsession = reply.getMimeHeaders().getHeader("JSESSIONID");
            if (jsession != null && jsession[0] != null) {
            	sessions.put(skey, jsession[0]);
            }
            if (debug.trim().equalsIgnoreCase("true")) {
            	debugMessage(reply, debugId + "-response");
            }
            if (reply == null) {
            	String msg = "SOAP-ENV: Content length of reply was zero";
                throw new SOAPException(msg);
            }
            SOAPFault fault =
                reply.getSOAPPart().getEnvelope().getBody().getFault();
            if (fault != null) {
                String errmsg =
                    fault.getFaultCode() + " - " + fault.getFaultString();
                throw new SOAPException(errmsg);
            }
            responseMessage(reply);
            connection.close();
        } catch (SOAPException err) {
        	errorMsg = err.getMessage();
            wiMap.put("wi.wsclient.error", errorMsg);
            String jspFile = wiMap.get("wi.jsp.filename");
            wiParams.getErrorLog().write(jspFile, wsDescription, errorMsg);
            if (!wiParams.getPage().getErrorPageName().equals("")) {
                wiParams.setRequestAttribute("wiException", err);
            }
        }
        writeLog();
    }

    private void debugMessage(SOAPMessage message, String label) {
    	String dir = wiParams.getErrorLog().getParentDir();
    	String name = "wsclient-" + label + ".log";
    	try {
        	FileOutputStream out = new FileOutputStream(new File(dir, name));
        	message.writeTo(out);
        	out.close();
        } catch (Exception err) {
        	// ignorado
        }
    }
    
    private void requestMessage(String textXML, SOAPEnvelope env) {
        try {
            String method = webservice.getMethod().trim();
            method = Producer.execute(wiMap, method).trim();
            String namespace = webservice.getNamespace().trim();
            Name name = env.createName(method, "ns1", namespace);
            SOAPElement ele = env.getBody().addChildElement(name);
            StringA tagValue = new StringA();
            int from = 0;
            int pos = -1;
            while ((pos = textXML.indexOf("<", from)) > -1) {
                int end = textXML.indexOf(">", pos);
                if (end == -1) {
                    end = textXML.length();
                }
                String tag = StringA.mid(textXML, pos + 1, end - 1).trim();
                if (!tag.startsWith("/")) {
                    tag = StringA.piece(tag, " ", 1);
                    ele = ele.addChildElement(tag);
                    int pos2 = textXML.indexOf("<", end);
                    if (pos2 == -2) {
                        pos2 = textXML.length();
                    }
                    String tagText = StringA.mid(textXML, end + 1, pos2 - 1);
                    tagValue.setXml(tagText.trim());
                    ele.addTextNode(tagValue.toString());
                    if (tag.endsWith("/")) {
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

    private void responseMessage(SOAPMessage reply) {
        String fullXML = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\r\n";
        String msgBody = WebServiceFunction.toString(reply).trim();
        int p1 = msgBody.indexOf("<", 1);
        if (p1 == -1) return;
    	String name = msgBody.substring(0, p1);
        if (name.indexOf(" ") > -1) {
        	name = name.substring(0, name.indexOf(" ")) + ">";
        }
        msgBody = name + msgBody.substring(p1, msgBody.length());
        fullXML = fullXML + includeNamespace(msgBody);
        fullXML = StringA.change(fullXML, "SOAP:", "", false);
        fullXML = StringA.change(fullXML, "SOAP-ENV:", "", false);
        fullXML = StringA.change(fullXML, "xsi:", "", false);
        fullXML = StringA.change(fullXML, "xsd:", "", false);
        String wiobj = StringA.changeChars(webservice.getWIObj(), "| ", "");
        CoreXmlImport impReceived = new CoreXmlImport(wiMap, wiobj);
        Document doc = impReceived.execute(fullXML);
        if (doc != null) {
	        StringWriter writer = new StringWriter();
	        Outputter.outputContent(doc.getRootElement(), writer);
	        wiMap.put("wi.wsclient.response", writer.toString());
        }    
    }

    private String includeNamespace(String xmlText) {
        int ini = xmlText.indexOf("<");
        int fim = xmlText.indexOf(">");
        String mid = StringA.mid(xmlText, ini, fim);
        if (mid.indexOf(":") > -1) {
            String ns = StringA.mid(mid, ini + 1, mid.indexOf(":") - 1);
            String p1 = StringA.mid(xmlText, 0, fim - 1);
            String p2 = StringA.mid(xmlText, fim, xmlText.length());
            String xmlns = "xmlns:" + ns + "=\"webinside:soapResponse\"";
            if (mid.indexOf("xmlns:" + ns) == -1) {
                xmlText = p1 + " " + xmlns + p2;
            }
        }
        return xmlText;
    }

    private String produceGridXML(String text) {
        String template = text;
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        prod.setInput(template);
        List lista = EngFunction.listPipeNames(template.toString());
        List refs = new ArrayList();
        for (int i = 0; i < lista.size(); i++) {
            String id = (String) lista.get(i);
            if (id.toLowerCase().startsWith("grid.")) {
                String xmlid = StringA.piece(id, "grid.", 2, 0, false);
                if (!wiParams.getProject().getGrids().containsKey(xmlid)) {
                    wiParams.includeCode("/grids/" + xmlid + "/grid.jsp");
                }
                GridXmlOut gridxml =
                    (GridXmlOut) wiParams.getProject().getGrids().getElement(xmlid);
                if (gridxml == null) {
                    continue;
                }
                refs.clear();
                if (pre) {
                    refs = wiParams.getPage().getPrePage().getGridRefs();
                }
                boolean found = false;
                for (int a = 0; a < refs.size(); a++) {
                    GridRef ref = (GridRef) refs.get(a);
                    if (ref.getId().equalsIgnoreCase(xmlid)) {
                        found = true;
                    }
                }
                if (found) {
                    continue;
                }
                CoreGridXmlOut xmlout = new CoreGridXmlOut(wiParams, gridxml);
                xmlout.execute(false);
            } else {
                prod.addProtectedPipe(id);
            }
        }
        wiParams.getProducer().setParam(prod);
        wiParams.getProducer().execute();
        return prod.getOutput();
    }
}
