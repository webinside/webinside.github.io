/*
 * WEBINSIDE - Ferramenta de produtividade Java
 * Copyright (c) 2011-2012 LINEWEB Solu��es Tecnol�gicas Ltda.
 * Copyright (c) 2009-2010 Inc�gnita Intelig�ncia Digital Ltda.
 *
 * Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 * sob os termos da GNU LESSER GENERAL PUBLIC LICENSE (LGPL) conforme publicada 
 * pela Free Software Foundation; vers�o 2.1 da Licen�a.
 * Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 * NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 * ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 * 
 * Consulte a GNU LGPL para mais detalhes.
 * Voc� deve ter recebido uma c�pia da GNU LGPL junto com este programa; se n�o, 
 * veja em http://www.gnu.org/licenses/ 
 */

package br.com.webinside.runtime.component;

import org.jdom.*;

import br.com.webinside.runtime.xml.*;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.5 $
 */
public class ObjectElement extends AbstractActionElement {

	private static final long serialVersionUID = 1L;

    /** DOCUMENT ME! */
    protected Element object;

    /**
     * Creates a new ObjectElement object.
     */
    public ObjectElement() {
        object = new Element("OBJECT");
        object.setAttribute("SEQ", "");
    }

    /**
     * Creates a new ObjectElement object.
     *
     * @param seq DOCUMENT ME!
     */
    public ObjectElement(String seq) {
        if (seq == null) {
            seq = "";
        }
        object = new Element("OBJECT");
        object.setAttribute("SEQ", seq);
    }

    /**
     * Creates a new ObjectElement object.
     *
     * @param element DOCUMENT ME!
     */
    public ObjectElement(Element element) {
        if ((element == null) || (!element.getName().equals("OBJECT"))) {
            element = new Element("OBJECT");
        }
        if (element.getAttribute("SEQ") == null) {
            element.setAttribute("SEQ", "");
        }
        this.object = element;
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
        element.addContent(object);
        return ErrorCode.NOERROR;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     */
    protected void setSeq(String seq) {
        object.getAttribute("SEQ").setValue(seq);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSeq() {
        return object.getAttribute("SEQ").getValue();
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setDescription(String value) {
        XMLFunction.setElemValue(object, "DESCRIPTION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDescription() {
        return XMLFunction.getElemValue(object, "DESCRIPTION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setCondition(String value) {
        XMLFunction.setElemValue(object, "CONDITION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getCondition() {
        return XMLFunction.getElemValue(object, "CONDITION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setWIObj(String value) {
        XMLFunction.setElemValue(object, "WIOBJ", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getWIObj() {
        return XMLFunction.getElemValue(object, "WIOBJ");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setDatabase(String value) {
        XMLFunction.setElemValue(object, "DATABASE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDatabase() {
        return XMLFunction.getElemValue(object, "DATABASE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setSql(String value) {
        XMLFunction.setElemValue(object, "SQL", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSql() {
        return XMLFunction.getElemValue(object, "SQL");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setSqlFilter(String value) {
        XMLFunction.setElemValue(object, "SQLFILTER", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSqlFilter() {
        return XMLFunction.getElemValue(object, "SQLFILTER");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AbstractActionElement cloneMe() {
        ObjectElement obj = new ObjectElement((Element) object.clone());
        return obj;
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setFromObject(String value) {
        XMLFunction.setElemValue(object, "FROMOBJECT", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getFromObject() {
        return XMLFunction.getElemValue(object, "FROMOBJECT");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setColumnIDs(String value) {
        XMLFunction.setElemValue(object, "COLUMNIDS", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getColumnIDs() {
        return XMLFunction.getElemValue(object, "COLUMNIDS");
    }
    
    public boolean isMultiple() {
        return XMLFunction.getElemValue(object, "MULTIPLE").equals("ON");
    }
    
    public void setMultiple(boolean multiple) {
        XMLFunction.setElemValue(object, "MULTIPLE", (multiple ? "ON" : ""));
    }

    public String getProcjson() {
        return XMLFunction.getElemValue(object, "PROCJSON");
    }
    
    public void setProcjson(String procjson) {
        XMLFunction.setElemValue(object, "PROCJSON", procjson);
    }
    
    // LEGADO - Pode ser removido - compatibilidade anteriores a 5.1.4 (Diario Prefeitura)
    public boolean isUsejson() {
        return XMLFunction.getElemValue(object, "USEJSON").equals("ON");
    }
    public void setUsejson(boolean multiple) {
        XMLFunction.setElemValue(object, "USEJSON", (multiple ? "ON" : ""));
    }

}
