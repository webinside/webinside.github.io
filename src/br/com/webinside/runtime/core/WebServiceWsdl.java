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

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import org.jdom.Attribute;
import org.jdom.Element;

import br.com.webinside.runtime.component.AbstractGrid;
import br.com.webinside.runtime.component.AbstractProject;
import br.com.webinside.runtime.component.GridXmlOut;
import br.com.webinside.runtime.component.WebService;
import br.com.webinside.runtime.component.WebServiceMethod;
import br.com.webinside.runtime.util.StringA;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class WebServiceWsdl {
    private WebServiceParams params;
    private AbstractProject project;
    private WebService service;
    private Map complexes;
    private List elements = new ArrayList();
    private List complexTypes = new ArrayList();
    private List messages = new ArrayList();

    /**
     * Creates a new WebServiceWsdl object.
     *
     * @param proj DOCUMENT ME!
     * @param service DOCUMENT ME!
     */
    public WebServiceWsdl(WebServiceParams params, WebService service) {
    	this.params = params;
        this.project = params.getWiParams().getProject();
        this.service = service;
        complexes = new HashMap();
    }

    /**
     * DOCUMENT ME!
     *
     * @param out DOCUMENT ME!
     * @param request DOCUMENT ME!
     */
    public void Wsdl(PrintWriter out, HttpServletRequest request) {
        String srvname = RtmFunction.getServerName(request);
        int serverPort = RtmFunction.getServerPort(request);        
        String prot = "http://";
        if (serverPort == 443) {
            prot = "https://";
        }
        String namespace =        	
            prot + srvname + "/" + project.getId() + "/" + service.getId()
            + ".wsdl";
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
        out.print("<definitions xmlns=\"http://schemas.xmlsoap.org/wsdl/\"");
        out.print(
            " xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:xsd0=\""
            + namespace + "\"");
        out.print(" xmlns:soap=\"http://schemas.xmlsoap.org/wsdl/soap/\"");
        out.print(" xmlns:tns=\"" + namespace + "\"");
        out.println(" name=\"" + service.getId() + "\" targetNamespace=\""
            + namespace + "\">");
        List methods = service.getMethods();
        for (int i = 0; i < methods.size(); i++) {
            WebServiceMethod metodo = (WebServiceMethod) methods.get(i);
            wsdlParts(metodo.getXmlIn(), metodo.getId() + "Request");
            wsdlParts(metodo.getXmlOut(), metodo.getId() + "Response");
        }
        out.println("<types>");
        out.println(
            "<xsd:schema elementFormDefault=\"qualified\" targetNamespace=\""
            + namespace + "\">");
        for (int i = 0; i < elements.size(); i++) {
            out.println(elements.get(i));
        }
        for (int i = 0; i < complexTypes.size(); i++) {
            out.println(complexTypes.get(i));
        }
        out.println("</xsd:schema>");
        out.println("</types>");
        for (int i = 0; i < messages.size(); i++) {
            out.println(messages.get(i));
        }
        out.println("<portType name=\"" + service.getId() + "_PortType\">");
        for (int i = 0; i < methods.size(); i++) {
            WebServiceMethod met = (WebServiceMethod) methods.get(i);
            out.println("<operation name=\"" + met.getId() + "\">");
            out.println("<input message=\"tns:" + met.getId() + "Request\"/>");
            out.println("<output message=\"tns:" + met.getId() + "Response\"/>");
            out.println("</operation>");
        }
        out.println("</portType>");
        out.println("<binding name=\"" + service.getId()
            + "_Binding\" type=\"tns:" + service.getId() + "_PortType\">");
        out.println(
            "<soap:binding style=\"rpc\" transport=\"http://schemas.xmlsoap.org/soap/http\"/>");
        for (int i = 0; i < methods.size(); i++) {
            WebServiceMethod met = (WebServiceMethod) methods.get(i);
            out.println("<operation name=\"" + met.getId() + "\">");
            out.println("<soap:operation soapAction=\"" + met.getId() + "\"/>");
            out.println("<input>");
            out.println(
                "<soap:body encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"");
            out.println("namespace=\"" + namespace + "\" use=\"encoded\"/>");
            out.println("</input>");
            out.println("<output>");
            out.println(
                "<soap:body encodingStyle=\"http://schemas.xmlsoap.org/soap/encoding/\"");
            out.println("namespace=\"" + namespace + "\" use=\"encoded\"/>");
            out.println("</output>");
            out.println("</operation>");
        }
        out.println("</binding>");
        out.println("<service name=\"" + service.getId() + "\">");
        out.println("<documentation>WSDL File for " + service.getId()
            + "</documentation>");
        out.println("<port binding=\"tns:" + service.getId()
            + "_Binding\" name=\"" + service.getId() + "_Port\">");
        out.println("<soap:address location=\""
            + StringA.piece(namespace, ".wsdl", 1) + ".ws\"/>");
        out.println("</port></service>");
        out.println("</definitions>");
        out.flush();
    }

    private void wsdlParts(String text, String metname) {
        Element wsdl = makeElement(importGrids(text));
        try {
            messages.add("<message name=\"" + metname + "\">");
            recursiveParts(metname, wsdl, "", "", false);
            messages.add("</message>");
        } catch (Exception err) {
        }
    }

    private void recursiveParts(String metname, Element node, String elename,
        String shortname, boolean complex) {
        if ((node == null) || node.getChildren().isEmpty()) {
            return;
        }
        if (!elename.equals("")) {
            if (complexes.containsKey(elename)) {
                return;
            }
            if (!complex) {
                elements.add("<xsd:element name=\"" + elename + "\">");
                elements.add("<xsd:complexType>");
                elements.add("<xsd:sequence>");
            } else {
                complexTypes.add("<xsd:complexType name=\"" + elename + "\">");
                complexTypes.add("<xsd:sequence>");
            }
            complexes.put(elename, "");
        }
        Iterator it = node.getChildren().iterator();
        while (it.hasNext()) {
            Element ele = (Element) it.next();
            String name = ele.getName();
            String type = "string";
            if (ele.getAttribute("type") != null) {
                type = ele.getAttribute("type").getValue();
            }
            if (!type.equals("")) {
                if (!type.startsWith("xsd:")) {
                    type = "xsd:" + type;
                }
            } else {
                type = "xsd:string";
            }
            if (elename.equals("")) {
                // message
                boolean rec = false;
                if (!ele.getChildren().isEmpty()) {
                    rec = true;
                }
                if (!rec) {
                    messages.add("<part name=\"" + name + "\" type=\"" + type
                        + "\"/>");
                } else {
                    messages.add("<part name=\"" + name + "\" element=\"xsd0:"
                        + metname + "_parent_" + name + "\"/>");
                    Element child = new Element("Root");
                    child.addContent((Element) ele.clone());
                    recursiveParts(metname, child, metname + "_parent_" + name,
                        name, false);
                }
            } else {
                String param = getParam(ele);
                if (complex) {
                    // complextype comum
                    if (!ele.getChildren().isEmpty()) {
                        String refname = elename + "_" + name;
                        if (refname.startsWith(metname + "_parent")) {
                            refname = metname + "_" + name;
                        }
                        complexTypes.add("<xsd:element " + param + " name=\""
                            + name + "\" type=\"xsd0:" + refname + "\"/>");
                    } else {
                        complexTypes.add("<xsd:element " + param + " name=\""
                            + name + "\" type=\"" + type + "\"/>");
                    }
                } else {
                    String refname = elename + "_" + shortname;
                    if (refname.startsWith(metname + "_parent")) {
                        refname = metname + "_" + shortname;
                    }
                    if (!ele.getChildren().isEmpty()) {
                        // elemento em array
                        elements.add("<xsd:element " + param + " name=\""
                            + name + "\" type=\"xsd0:" + refname + "\"/>");
                    } else {
                        // complextype no elemento
                        elements.add("<xsd:element " + param + " name=\""
                            + name + "\" type=\"" + type + "\"/>");
                    }
                }
            }
        }
        if (!elename.equals("")) {
            if (!complex) {
                elements.add("</xsd:sequence>");
                elements.add("</xsd:complexType>");
                elements.add("</xsd:element>");
            } else {
                complexTypes.add("</xsd:sequence>");
                complexTypes.add("</xsd:complexType>");
            }
            it = node.getChildren().iterator();
            while (it.hasNext()) {
                Element ele = (Element) it.next();
                if (!ele.getChildren().isEmpty()) {
                    String refname = elename + "_" + ele.getName();
                    if (refname.startsWith(metname + "_parent")) {
                        refname = metname + "_" + shortname;
                    }
                    recursiveParts(metname, ele, refname, ele.getName(), true);
                }
            }
        }
    }

    private String getParam(Element ele) {
        String min = "0";
        String max = "1";
        if (ele.getAttribute("minoccurs") != null) {
            min = ele.getAttribute("minoccurs").getValue();
        }
        if (ele.getAttribute("maxoccurs") != null) {
            max = ele.getAttribute("maxoccurs").getValue();
        }
        return "minOccurs=\"" + min + "\" maxOccurs=\"" + max + "\"";
    }

    private Element makeElement(String text) {
        Element parent = new Element("Root");
        Element node = null;
        try {
            int from = 0;
            int pos = 0;
            while ((pos = text.indexOf("<", from)) > -1) {
                int end = text.indexOf(">", pos + 1);
                if (end == -1) {
                    end = text.length();
                }
                String fulltag = StringA.mid(text, pos + 1, end - 1).trim();
                String name = StringA.piece(fulltag.trim(), " ", 1);
                name = StringA.changeChars(name, "/", "");
                if (fulltag.startsWith("/")) {
                    if ((node != null) && node.getName().equals(name)) {
                        node = (Element) node.getParent();
                    }
                } else {
                    Element child = new Element(name);
                    if (node == null) {
                        parent.addContent(child);
                    } else {
                        node.addContent(child);
                    }
                    node = child;
                    putAttributes(node, fulltag);
                }
                if (fulltag.endsWith("/") && (node != null)) {
                    node = (Element) node.getParent();
                }
                from = end + 1;
            }
        } catch (Exception err) {
        }
        return parent;
    }

    private void putAttributes(Element ele, String fulltag) {
        int spc = fulltag.indexOf(" ");
        if (spc > -1) {
            fulltag = StringA.mid(fulltag, spc, fulltag.length());
        }
        String low = fulltag.toLowerCase();
        int from = 0;
        int ig;
        while ((ig = low.indexOf("=", from)) > -1) {
            int ant = low.lastIndexOf("\"", ig);
            if (ant == -1) {
                ant = low.lastIndexOf("'", ig);
            }
            int pos = low.indexOf("\"", ig);
            if (pos == -1) {
                pos = low.indexOf("'", ig);
                pos = low.indexOf("'", pos + 1);
            } else {
                pos = low.indexOf("\"", pos + 1);
            }
            if (pos == -1) {
                pos = low.length();
            }
            String name = StringA.mid(fulltag, ant + 1, ig - 1);
            name = StringA.changeChars(name, "/\"' ", "");
            String value = StringA.mid(fulltag, ig + 1, pos);
            value = StringA.changeChars(value, "/\"' ", "");
            if (!name.equals("") && !value.equals("")) {
                ele.setAttribute(new Attribute(name.toLowerCase(), value));
            }
            from = pos + 1;
        }
    }

    private String importGrids(String text) {
        if (text == null) {
            return "";
        }
        String lowtext = text.toLowerCase();
        StringA aux = new StringA();
        StringA value = new StringA();
        int from = 0;
        int pos;
        while ((pos = lowtext.indexOf("|grid.", from)) > -1) {
            int last = lowtext.indexOf("|", pos + 1);
            if (last == -1) {
                last = lowtext.length();
            }
            aux.append(StringA.mid(text, from, pos - 1));
            String id = StringA.mid(text, pos + 1, last - 1);
            id = StringA.piece(id, "grid.", 2);
            if (!project.getGrids().containsKey(id)) {
                params.getWiParams().includeCode("/grids/" + id + "/grid.jsp");
            }
            AbstractGrid grid =
                (AbstractGrid) project.getGrids().getElement(id);
            if ((grid != null) && (grid instanceof GridXmlOut)) {
                GridXmlOut xmlout = (GridXmlOut) grid;
                String root = xmlout.getRoot().trim();
                String child = xmlout.getChild().trim();
                value.set("");
                if (!root.equals("")) {
                    value.append("<" + root + ">\r\n");
                }
                if (!child.equals("")) {
                    value.append("<" + child + ">\r\n");
                }
                value.append(xmlout.getXmlTemplate());
                if (!child.equals("")) {
                    value.append("</" + child + ">\r\n");
                }
                if (!root.equals("")) {
                    value.append("</" + root + ">\r\n");
                }
                aux.append(importGrids(value.toString()));
            }
            from = last + 1;
        }
        aux.append(StringA.mid(text, from, text.length()));
        return aux.toString();
    }
}
