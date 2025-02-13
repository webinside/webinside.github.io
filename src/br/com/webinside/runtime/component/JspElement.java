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

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jdom.Element;

import br.com.webinside.runtime.integration.JspPipeConverter;
import br.com.webinside.runtime.xml.ErrorCode;
import br.com.webinside.runtime.xml.XMLFunction;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class JspElement extends AbstractActionElement {

	private static final long serialVersionUID = 1L;

    /** DOCUMENT ME! */
    protected Element jspElement;

    /**
     * Creates a new JspElement object.
     */
    public JspElement() {
        jspElement = new Element("JSPELEMENT");
        jspElement.setAttribute("SEQ", "");
    }

    /**
     * Creates a new JspElement object.
     *
     * @param seq DOCUMENT ME!
     */
    public JspElement(String seq) {
        if (seq == null) {
            seq = "";
        }
        jspElement = new Element("JSPELEMENT");
        jspElement.setAttribute("SEQ", seq);
    }

    /**
     * Creates a new JspElement object.
     *
     * @param element DOCUMENT ME!
     */
    public JspElement(Element element) {
        if ((element == null) || (!element.getName().equals("JSPELEMENT"))) {
            element = new Element("JSPELEMENT");
        }
        if (element.getAttribute("SEQ") == null) {
            element.setAttribute("SEQ", "");
        }
        this.jspElement = element;
    }

    /**
     * DOCUMENT ME!
     *
     * @param element DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected int insertInto(Element element) {
        if (element == null) {
            return ErrorCode.NULL;
        }
        element.addContent(jspElement);
        return ErrorCode.NOERROR;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     */
    protected void setSeq(String seq) {
        jspElement.getAttribute("SEQ").setValue(seq);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSeq() {
        return jspElement.getAttribute("SEQ").getValue();
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setDescription(String value) {
        XMLFunction.setElemValue(jspElement, "DESCRIPTION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDescription() {
        return XMLFunction.getElemValue(jspElement, "DESCRIPTION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setCondition(String value) {
        XMLFunction.setElemValue(jspElement, "CONDITION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getCondition() {
        return XMLFunction.getElemValue(jspElement, "CONDITION");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getTemplate() {
        return XMLFunction.getElemValue(jspElement, "TEMPLATE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setTemplate(String value) {
        XMLFunction.setElemValue(jspElement, "TEMPLATE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setCode(String value) {
        XMLFunction.setCDATAValue(jspElement, "CODE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getCode() {
        return XMLFunction.getElemValue(jspElement, "CODE");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AbstractActionElement cloneMe() {
        JspElement obj = new JspElement((Element) jspElement.clone());
        return obj;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Map getParameters() {
        Map ret = new HashMap();
        Element paramsElement = jspElement.getChild("PARAMETERS");
        if (paramsElement == null) {
            paramsElement = new Element("PARAMETERS");
        }
        List v = XMLFunction.getElemsByName(paramsElement, "PARAMETER", false);
        for (int i = 0; i < v.size(); i++) {
            Element e = (Element) v.get(i);
            String key = e.getAttributeValue("ID");
            ret.put(key, e.getAttributeValue("VALUE"));
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param params DOCUMENT ME!
     */
    public void setParameters(Map params) {
        Element paramsElement = new Element("PARAMETERS");
        jspElement.removeChildren("PARAMETERS");
        if (!params.isEmpty()) {
            jspElement.addContent(paramsElement);
        }
        for (Iterator i = params.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            Element paramElement = new Element("PARAMETER");
            paramsElement.addContent(paramElement);
            XMLFunction.setAttrValue(paramElement, "ID", key);
            XMLFunction.setAttrValue(paramElement, "VALUE",
                (String) params.get(key));
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toJSP() {
        StringBuffer resp = new StringBuffer();
        String exp = CompFunction.filterTagAttribute(getCondition()).trim();
        if (exp.equalsIgnoreCase("false")) return "";
        if (!exp.equalsIgnoreCase("true") || useValidation) {
            resp.append("<wi:if test=\"").append(exp).append("\" validation=\"true\">");
        }
        String code = getCode().trim();
        code = new JspPipeConverter().execute(code).trim();
        resp.append(code);
        if (!exp.equals("true")) {
        	resp.append("</wi:if>");
        }
        return resp.toString();
    }
}
