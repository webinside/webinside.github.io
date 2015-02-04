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

import java.util.Iterator;
import java.util.List;
import org.jdom.Element;

import br.com.webinside.runtime.util.WIMap;
import br.com.webinside.runtime.xml.ErrorCode;
import br.com.webinside.runtime.xml.XMLFunction;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class ReportRef extends AbstractActionElement {

	private static final long serialVersionUID = 1L;

	/** DOCUMENT ME! */
    public Element reportRef;

    /**
     * Creates a new ReportRef object.
     */
    public ReportRef() {
        reportRef = new Element("REPORTREF");
        reportRef.setAttribute("SEQ", "");
    }

    /**
     * Creates a new ReportRef object.
     *
     * @param element DOCUMENT ME!
     */
    public ReportRef(Element element) {
        if ((element == null) || (!element.getName().equals("REPORTREF"))) {
            element = new Element("REPORTREF");
        }
        if (element.getAttribute("SEQ") == null) {
            element.setAttribute("SEQ", "");
        }
        this.reportRef = element;
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
        element.addContent(reportRef);
        return ErrorCode.NOERROR;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     */
    protected void setSeq(String seq) {
        reportRef.getAttribute("SEQ").setValue(seq);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSeq() {
        return reportRef.getAttribute("SEQ").getValue();
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setCondition(String value) {
        XMLFunction.setElemValue(reportRef, "CONDITION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getCondition() {
        return XMLFunction.getElemValue(reportRef, "CONDITION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setOutputDir(String value) {
        XMLFunction.setElemValue(reportRef, "OUTPUTDIR", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getOutputDir() {
        return XMLFunction.getElemValue(reportRef, "OUTPUTDIR");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setId(String value) {
        XMLFunction.setElemValue(reportRef, "REPORTID", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getId() {
        return XMLFunction.getElemValue(reportRef, "REPORTID");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AbstractActionElement cloneMe() {
        ReportRef obj = new ReportRef((Element) reportRef.clone());
        return obj;
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setDescription(String value) {
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDescription() {
        return "Report: " + getId();
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setOutputFormat(String value) {
        XMLFunction.setElemValue(reportRef, "OUTPUTFORMAT", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getOutputFormat() {
        return XMLFunction.getElemValue(reportRef, "OUTPUTFORMAT");
    }

    /**
     * DOCUMENT ME!
     *
     * @param params DOCUMENT ME!
     */
    public void setParameters(WIMap params) {
        if ((params == null) || params.isEmpty()) {
            return;
        }
        Element paramsElement = new Element("PARAMETERS");
        reportRef.removeChildren("PARAMETERS");
        reportRef.addContent(paramsElement);

        for (Iterator i = params.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            String value = params.get(key);
            if (value.equals("")) {
                continue;
            }
            if (value.equalsIgnoreCase("|" + key + "|")) {
                continue;
            }
            Element paramElement = new Element("PARAMETER");
            paramsElement.addContent(paramElement);
            XMLFunction.setAttrValue(paramElement, "ID", key);
            XMLFunction.setAttrValue(paramElement, "VALUE", value);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public WIMap getParameters() {
        WIMap ret = new WIMap(true);
        Element paramsElement = reportRef.getChild("PARAMETERS");
        if (paramsElement == null) {
            paramsElement = new Element("PARAMETERS");
        }
        List v = XMLFunction.getElemsByName(paramsElement, "PARAMETER", false);
        for (int i = 0; i < v.size(); i++) {
            Element e = (Element) v.get(i);
            ret.put(e.getAttributeValue("ID"), e.getAttributeValue("VALUE"));
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString() {
        return XMLFunction.elementToString(reportRef);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toJSP() {
        StringBuffer resp = new StringBuffer();
        resp.append(CompFunction.toJSP(this, false));
        WIMap params = getParameters();
        for (Iterator i = params.keySet().iterator(); i.hasNext();) {
            String key = (String) i.next();
            if (key != null) {
                key = CompFunction.filterTagAttribute(key);
                String value = 
                	CompFunction.filterTagAttribute(params.get(key).trim());
	            resp.append("<w:setPropertyByMethod\n");
	            resp.append("  name=\"report" + getSeq() + "\"");
	            resp.append(" method=\"reportParameter\"");
	            resp.append(" arg1=\"" + key + "\" arg2=\"" + value + "\"\n/>");
            }
        }
        resp.append(CompFunction.jspCore("Report", "report" + getSeq()));
        return resp.toString();
    }
}
