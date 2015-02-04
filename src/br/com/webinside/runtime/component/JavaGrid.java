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
import java.util.List;
import org.jdom.Element;

import br.com.webinside.runtime.integration.JavaParameter;
import br.com.webinside.runtime.xml.ErrorCode;
import br.com.webinside.runtime.xml.XMLFunction;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class JavaGrid extends AbstractActionElement {

	private static final long serialVersionUID = 1L;

    /** DOCUMENT ME! */
    protected Element javagrid;

    /**
     * Creates a new JavaGrid object.
     */
    public JavaGrid() {
        javagrid = new Element("JAVAGRID");
        javagrid.setAttribute("SEQ", "");
    }

    /**
     * Creates a new JavaGrid object.
     *
     * @param element DOCUMENT ME!
     */
    public JavaGrid(Element element) {
        if ((element == null) || (!element.getName().equals("JAVAGRID"))) {
            element = new Element("JAVAGRID");
        }
        if (element.getAttribute("SEQ") == null) {
            element.setAttribute("SEQ", "");
        }
        this.javagrid = element;
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
        element.addContent(javagrid);
        return ErrorCode.NOERROR;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     */
    protected void setSeq(String seq) {
        javagrid.getAttribute("SEQ").setValue(seq);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSeq() {
        return javagrid.getAttribute("SEQ").getValue();
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setCondition(String value) {
        XMLFunction.setElemValue(javagrid, "CONDITION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getCondition() {
        return XMLFunction.getElemValue(javagrid, "CONDITION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setPluginName(String value) {
        XMLFunction.setElemValue(javagrid, "PLUGIN", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPluginName() {
        return XMLFunction.getElemValue(javagrid, "PLUGIN");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setPluginNamespace(String value) {
        XMLFunction.setElemValue(javagrid, "NAMESPACE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPluginNamespace() {
        return XMLFunction.getElemValue(javagrid, "NAMESPACE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setClassName(String value) {
        XMLFunction.setElemValue(javagrid, "CLASSNAME", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getClassName() {
        return XMLFunction.getElemValue(javagrid, "CLASSNAME");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setDescription(String value) {
        XMLFunction.setElemValue(javagrid, "DESCRIPTION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDescription() {
        return XMLFunction.getElemValue(javagrid, "DESCRIPTION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setGridId(String value) {
        XMLFunction.setElemValue(javagrid, "GRIDID", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getGridId() {
        return XMLFunction.getElemValue(javagrid, "GRIDID");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AbstractActionElement cloneMe() {
        JavaGrid obj = new JavaGrid((Element) javagrid.clone());
        return obj;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString() {
        return XMLFunction.elementToString(javagrid);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public JavaParameter[] getInputParameters() {
        List ret = new ArrayList();
        Element paramsElement = javagrid.getChild("PARAMETERS");
        if (paramsElement == null) {
            paramsElement = new Element("PARAMETERS");
        }
        List v = XMLFunction.getElemsByName(paramsElement, "PARAMETER", false);
        for (int i = 0; i < v.size(); i++) {
            Element e = (Element) v.get(i);
            JavaParameter param = new JavaParameter(e.getAttributeValue("ID"));
            param.setValue(e.getAttributeValue("VALUE"));
            ret.add(param);
        }
        return (JavaParameter[]) ret.toArray(new JavaParameter[ret.size()]);
    }

    /**
     * DOCUMENT ME!
     *
     * @param params DOCUMENT ME!
     */
    public void setInputParameters(JavaParameter[] params) {
        Element paramsElement = new Element("PARAMETERS");
        javagrid.removeChildren("PARAMETERS");
        if (params.length > 0) {
            javagrid.addContent(paramsElement);
        }
        for (int i = 0; i < params.length; i++) {
            JavaParameter param = params[i];
            if (param.getValue().equals("")) {
                continue;
            }
            if (param.getValue().equalsIgnoreCase("|" + param.getVarId() + "|")) {
                continue;
            }
            Element paramElement = new Element("PARAMETER");
            paramsElement.addContent(paramElement);
            XMLFunction.setAttrValue(paramElement, "ID", param.getVarId());
            XMLFunction.setAttrValue(paramElement, "VALUE", param.getValue());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toJSP() {
        StringBuffer resp = new StringBuffer();
        resp.append(CompFunction.toJSP(this, false));
        String cond = getCondition();
        resp.append(CompFunction.jspJavaParameters(getInputParameters(), cond));
        resp.append(CompFunction.jspCore("JavaGrid", "javagrid" + getSeq()));
        return resp.toString();
    }
}
