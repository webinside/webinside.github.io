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

package br.com.webinside.runtime.component;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;

import br.com.webinside.runtime.xml.ErrorCode;
import br.com.webinside.runtime.xml.XMLFunction;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class WebService implements ProjectElement {

	private static final long serialVersionUID = 1L;

    /** DOCUMENT ME! */
    public final static String DIRECTORY = "webservices";
    private static Document template;
    private static Object sync = new Object();
    /** DOCUMENT ME! */
    protected Element webservice;
    private AbstractProject project;
    private Element methods;

    /**
     * Creates a new WebService object.
     *
     * @param id DOCUMENT ME!
     */
    public WebService(String id) {
        if (id == null) {
            id = "";
        }
        webservice = new Element("WEBSERVICE");
        webservice.setAttribute("ID", id);
    }

    /**
     * Creates a new WebService object.
     *
     * @param id DOCUMENT ME!
     * @param element DOCUMENT ME!
     */
    public WebService(String id, Element element) {
        if ((element == null) || (!element.getName().equals("WEBSERVICE"))) {
            element = new Element("WEBSERVICE");
        }
        if (element.getAttribute("ID") == null) {
            element.setAttribute("ID", id);
        }
        this.webservice = element;
    }

    /**
     * DOCUMENT ME!
     *
     * @param obj DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof WebService) {
            String id = ((WebService) obj).getId();
            if (id.equalsIgnoreCase(getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getId() {
        return webservice.getAttributeValue("ID");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setDescription(String value) {
        XMLFunction.setElemValue(webservice, "DESCRIPTION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDescription() {
        return XMLFunction.getElemValue(webservice, "DESCRIPTION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setNamespace(String value) {
        XMLFunction.setElemValue(webservice, "NAMESPACE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getNamespace() {
        return XMLFunction.getElemValue(webservice, "NAMESPACE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param method DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int addMethod(WebServiceMethod method) {
        synchronized (sync) {
            int ret = ErrorCode.NOERROR;
            if (method == null) {
                ret = ErrorCode.NULL;
            } else {
                String name = method.getId();
                if (name.equals("")) {
                    ret = ErrorCode.EMPTY;
                } else {
                    methods = webservice.getChild("METHODS");
                    if (XMLFunction.getChildByAttribute(methods, "METHOD",
                                    "ID", name) != null) {
                        ret = ErrorCode.EXIST;
                    } else {
                        if (webservice.getChild("METHODS") == null) {
                            webservice.addContent(new Element("METHODS"));
                            methods = webservice.getChild("METHODS");
                        }
                        ret = method.insertInto(methods);
                    }
                }
            }
            return ret;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getMethods() {
        List ret = new ArrayList();
        methods = webservice.getChild("METHODS");
        if (methods == null) {
            return ret;
        }
        List list = methods.getChildren("METHOD");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new WebServiceMethod(ele));
            }
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public WebServiceMethod getMethod(String name) {
        if (name == null) {
            return null;
        }
        if (name.equals("")) {
            return null;
        }
        methods = webservice.getChild("METHODS");
        if (methods == null) {
            return null;
        }
        Element method =
            XMLFunction.getChildByAttribute(methods, "METHOD", "ID", name);
        if (method == null) {
            return null;
        }
        return new WebServiceMethod(method);
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeMethod(String seq) {
        WebServiceMethod method = getMethod(seq);
        if (method == null) {
            return ErrorCode.NULL;
        }
        methods = webservice.getChild("METHODS");
        if (methods == null) {
            return ErrorCode.NOEXIST;
        }
        methods.removeContent(method.method);
        if (methods.getChildren().size() == 0) {
            webservice.removeContent(methods);
        }
        return ErrorCode.NOERROR;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public WebService cloneMe() {
        WebService obj = new WebService(getId(), (Element) webservice.clone());
        return obj;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toJSP() {
        StringBuffer resp = new StringBuffer();
        resp.append("<w:setProjectElement\n");
    	resp.append("  type=\"WebService\" name=\"webservice_" + getId() + "\"\n");
        resp.append("/>");        
        List methodsList = getMethods();
        for (int i = 0; i < methodsList.size(); i++) {
        	String mseq = (i + 1) + "";        	
            WebServiceMethod method = (WebServiceMethod) methodsList.get(i);
            resp.append("<w:setWebServiceElement\n");
        	resp.append("  webservice=\"" + getId() + "\"");
        	resp.append(" method=\"" + method.getId() + "\"");
        	resp.append(" name=\"wsmethod" + mseq + "\"\n");
            resp.append("/><jsp:useBean\n");
            resp.append("  id=\"wsmethod" + mseq + "\" ");
            resp.append("type=\"br.com.webinside.runtime.component.WebServiceMethod\"\n");
            resp.append("/>");        	
            resp.append(CompFunction.setProperties(method, "wsmethod" + mseq));
            List faults = method.getFaults();
            for (int a = 0; a < faults.size(); a++) {
                String fseq = (a + 1) + "";
                WebServiceFault fault = (WebServiceFault) faults.get(a);
                resp.append("<w:setWebServiceElement\n");
            	resp.append("  webservice=\"" + getId() + "\"");
            	resp.append(" method=\"" + method.getId() + "\"");
            	String fName = "wsmethod" + mseq + "_fault" + fseq;  
				resp.append(" name=\"" + fName + "\"\n");
	            resp.append("/><jsp:useBean\n");
	            resp.append("  id=\"" + fName + "\" ");
	            resp.append("type=\"br.com.webinside.runtime.component.WebServiceFault\"\n");
	            resp.append("/>");        	
                resp.append(CompFunction.setProperties(fault, fName));
            }
        }
        return resp.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param project DOCUMENT ME!
     */
    public void setProject(AbstractProject project) {
        this.project = project;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AbstractProject getProject() {
        return project;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Element getElement() {
        return webservice;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Document getTemplate() {
        if (template == null) {
            template = CompFunction.getTemplate("webservice.xml");
        }
        return template;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getType() {
        return "";
    }
}
