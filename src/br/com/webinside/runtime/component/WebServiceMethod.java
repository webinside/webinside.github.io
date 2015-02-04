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
import org.jdom.Element;

import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.xml.ErrorCode;
import br.com.webinside.runtime.xml.XMLFunction;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class WebServiceMethod {
    private static Object sync = new Object();
    /** DOCUMENT ME! */
    protected Element method;
    private Element faults;

    /**
     * Creates a new WebServiceMethod object.
     */
    public WebServiceMethod() {
        method = new Element("METHOD");
        method.setAttribute("ID", "");
    }

    /**
     * Creates a new WebServiceMethod object.
     *
     * @param name DOCUMENT ME!
     */
    public WebServiceMethod(String name) {
        if (name == null) {
            name = "";
        }
        method = new Element("METHOD");
        method.setAttribute("ID", name);
    }

    /**
     * Creates a new WebServiceMethod object.
     *
     * @param element DOCUMENT ME!
     */
    public WebServiceMethod(Element element) {
        if ((element == null) || (!element.getName().equals("METHOD"))) {
            element = new Element("METHOD");
        }
        if (element.getAttribute("ID") == null) {
            element.setAttribute("ID", "");
        }
        this.method = element;
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
        element.addContent(method);
        return ErrorCode.NOERROR;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     */
    protected void setId(String seq) {
        method.getAttribute("ID").setValue(seq);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getId() {
        return method.getAttribute("ID").getValue();
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setDescription(String value) {
        XMLFunction.setElemValue(method, "DESCRIPTION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDescription() {
        return XMLFunction.getElemValue(method, "DESCRIPTION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setObjIn(String value) {
        XMLFunction.setElemValue(method, "WIOBJIN", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getObjIn() {
        return XMLFunction.getElemValue(method, "WIOBJIN");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setXmlIn(String value) {
        XMLFunction.setElemValue(method, "XMLIN", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getXmlIn() {
        return XMLFunction.getElemValue(method, "XMLIN");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setPage(String value) {
        XMLFunction.setElemValue(method, "PAGE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPage() {
        return XMLFunction.getElemValue(method, "PAGE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setXmlOut(String value) {
        XMLFunction.setElemValue(method, "XMLOUT", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getXmlOut() {
        return XMLFunction.getElemValue(method, "XMLOUT");
    }

    /**
     * DOCUMENT ME!
     *
     * @param fault DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int addFault(WebServiceFault fault) {
        synchronized (sync) {
            int ret = ErrorCode.NOERROR;
            if (fault == null) {
                ret = ErrorCode.NULL;
            } else {
                faults = method.getChild("FAULTS");
                if (faults == null) {
                    method.addContent(new Element("FAULTS"));
                    faults = method.getChild("FAULTS");
                }
                if (XMLFunction.getChildByAttribute(faults, "FAULT", "SEQ",
                                fault.getSeq()) != null) {
                    ret = ErrorCode.EXIST;
                } else {
                    if (fault.getSeq().equals("")) {
                        int i;
                        for (i = 1;
                                    XMLFunction.getChildByAttribute(faults,
                                        "FAULT", "SEQ", i + "") != null; i++) {
                        }
                        fault.setSeq(i + "");
                    }
                    if (fault.getCode().trim().equals("")) {
                        fault.setCode("SOAP-ENV:Server");
                    }
                    ret = fault.insertInto(faults);
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
    public List getFaults() {
        List ret = new ArrayList();
        faults = method.getChild("FAULTS");
        if (faults == null) {
            return ret;
        }
        List list = faults.getChildren("FAULT");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new WebServiceFault(ele));
            }
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public WebServiceFault getFault(String seq) {
        if (seq == null) {
            return null;
        }
        if (seq.equals("")) {
            return null;
        }
        faults = method.getChild("FAULTS");
        if (faults == null) {
            return null;
        }
        Element fault =
            XMLFunction.getChildByAttribute(faults, "FAULT", "SEQ", seq);
        if (fault == null) {
            return null;
        }
        return new WebServiceFault(fault);
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeFault(String seq) {
        WebServiceFault fault = getFault(seq);
        if (fault == null) {
            return ErrorCode.NULL;
        }
        faults = method.getChild("FAULTS");
        if (faults == null) {
            return ErrorCode.NOEXIST;
        }
        faults.removeContent(fault.fault);
        if (faults.getChildren().size() == 0) {
            method.removeContent(faults);
        }
        return ErrorCode.NOERROR;
    }

    /**
     * DOCUMENT ME!
     *
     * @param order DOCUMENT ME!
     */
    public void setFaultOrder(String order) {
        faults = method.getChild("FAULTS");
        if (faults == null) {
            return;
        }
        List ix = StringA.pieceAsList(order, ",", 0, 0, true);
        Element newFaults = new Element("FAULTS");
        int lix = faults.getChildren().size();
        int lor = ix.size();
        int max = (lix < lor) ? lix
                              : lor;
        for (int i = 0; i < max; i++) {
            int j = 0;
            try {
                j = Integer.parseInt((String) ix.get(i));
                if (j > (lix - 1)) {
                    continue;
                }
            } catch (NumberFormatException err) {
            }
            newFaults.getChildren().add(faults.getChildren().get(j));
        }
        method.removeContent(faults);
        method.addContent(newFaults);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public WebServiceMethod cloneMe() {
        WebServiceMethod obj = new WebServiceMethod((Element) method.clone());
        return obj;
    }
}
