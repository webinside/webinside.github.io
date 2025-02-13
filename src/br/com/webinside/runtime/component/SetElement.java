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

import org.jdom.*;

import br.com.webinside.runtime.xml.*;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class SetElement extends AbstractActionElement {

	private static final long serialVersionUID = 1L;

    /** DOCUMENT ME! */
    protected Element set;

    /**
     * Creates a new SetElement object.
     */
    public SetElement() {
        set = new Element("SET");
        set.setAttribute("SEQ", "");
    }

    /**
     * Creates a new SetElement object.
     *
     * @param seq DOCUMENT ME!
     */
    public SetElement(String seq) {
        if (seq == null) {
            seq = "";
        }
        set = new Element("SET");
        set.setAttribute("SEQ", seq);
    }

    /**
     * Creates a new SetElement object.
     *
     * @param element DOCUMENT ME!
     */
    public SetElement(Element element) {
        if ((element == null) || (!element.getName().equals("SET"))) {
            element = new Element("SET");
        }
        if (element.getAttribute("SEQ") == null) {
            element.setAttribute("SEQ", "");
        }
        this.set = element;
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
        element.addContent(set);
        return ErrorCode.NOERROR;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     */
    protected void setSeq(String seq) {
        set.getAttribute("SEQ").setValue(seq);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSeq() {
        return set.getAttribute("SEQ").getValue();
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setDescription(String value) {
        XMLFunction.setElemValue(set, "DESCRIPTION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDescription() {
        return XMLFunction.getElemValue(set, "DESCRIPTION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setCondition(String value) {
        XMLFunction.setElemValue(set, "CONDITION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getCondition() {
        return XMLFunction.getElemValue(set, "CONDITION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setWIObj(String value) {
        XMLFunction.setElemValue(set, "WIOBJ", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getWIObj() {
        return XMLFunction.getElemValue(set, "WIOBJ");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setIfTrue(String value) {
        XMLFunction.setElemValue(set, "IFTRUE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getIfTrue() {
        return XMLFunction.getElemValue(set, "IFTRUE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setEnableFalse(String value) {
        XMLFunction.setElemValue(set, "ENABLEFALSE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getEnableFalse() {
        return XMLFunction.getElemValue(set, "ENABLEFALSE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setIfFalse(String value) {
        XMLFunction.setElemValue(set, "IFFALSE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getIfFalse() {
        return XMLFunction.getElemValue(set, "IFFALSE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setRecursive(String value) {
        XMLFunction.setElemValue(set, "RECURSIVE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getRecursive() {
        return XMLFunction.getElemValue(set, "RECURSIVE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setEncode(String value) {
        XMLFunction.setElemValue(set, "ENCODE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getEncode() {
        return XMLFunction.getElemValue(set, "ENCODE");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AbstractActionElement cloneMe() {
        SetElement obj = new SetElement((Element) set.clone());
        return obj;
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setFromObject(String value) {
        XMLFunction.setElemValue(set, "FROMOBJECT", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getFromObject() {
        return XMLFunction.getElemValue(set, "FROMOBJECT");
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setDisableProduce(boolean value) {
        XMLFunction.setElemValue(set, "DISABLEPRODUCE", value ? "ON"
                                                        : "");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isDisableProduce() {
        return XMLFunction.getElemValue(set, "DISABLEPRODUCE").equals("ON");
    }

    
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public void setDecodeJson(boolean value) {
        XMLFunction.setElemValue(set, "DECODEJSON", value ? "ON"
                                                        : "");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isDecodeJson() {
        return XMLFunction.getElemValue(set, "DECODEJSON").equals("ON");
    }

}
