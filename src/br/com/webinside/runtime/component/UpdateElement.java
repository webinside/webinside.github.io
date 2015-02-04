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

import br.com.webinside.runtime.xml.ErrorCode;
import br.com.webinside.runtime.xml.XMLFunction;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.3 $
 */
public class UpdateElement extends AbstractActionElement {

	private static final long serialVersionUID = 1L;

    /** DOCUMENT ME! */
    protected Element update;

    /**
     * Creates a new Update object.
     */
    public UpdateElement() {
        update = new Element("UPDATE");
        update.setAttribute("SEQ", "");
    }

    /**
     * Creates a new Update object.
     *
     * @param seq DOCUMENT ME!
     */
    public UpdateElement(String seq) {
        if (seq == null) {
            seq = "";
        }
        update = new Element("UPDATE");
        update.setAttribute("SEQ", seq);
    }

    /**
     * Creates a new Update object.
     *
     * @param element DOCUMENT ME!
     */
    public UpdateElement(Element element) {
        if ((element == null) || (!element.getName().equals("UPDATE"))) {
            element = new Element("UPDATE");
        }
        if (element.getAttribute("SEQ") == null) {
            element.setAttribute("SEQ", "");
        }
        this.update = element;
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
        element.addContent(update);
        return ErrorCode.NOERROR;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     */
    protected void setSeq(String seq) {
        update.getAttribute("SEQ").setValue(seq);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSeq() {
        return update.getAttribute("SEQ").getValue();
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setDescription(String value) {
        XMLFunction.setElemValue(update, "DESCRIPTION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDescription() {
        return XMLFunction.getElemValue(update, "DESCRIPTION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setCondition(String value) {
        XMLFunction.setElemValue(update, "CONDITION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getCondition() {
        return XMLFunction.getElemValue(update, "CONDITION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setWIObj(String value) {
        XMLFunction.setElemValue(update, "WIOBJ", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getWIObj() {
        return XMLFunction.getElemValue(update, "WIOBJ");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setPrefix(String value) {
        XMLFunction.setElemValue(update, "PREFIX", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPrefix() {
        return XMLFunction.getElemValue(update, "PREFIX");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setSize(String value) {
        XMLFunction.setElemValue(update, "SIZE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSize() {
        return XMLFunction.getElemValue(update, "SIZE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setRowCondition(String value) {
        XMLFunction.setElemValue(update, "ROWCONDITION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getRowCondition() {
        return XMLFunction.getElemValue(update, "ROWCONDITION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setObjectID(String value) {
        XMLFunction.setElemValue(update, "OBJECTID", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getObjectID() {
        return XMLFunction.getElemValue(update, "OBJECTID");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setDatabase(String value) {
        XMLFunction.setElemValue(update, "DATABASE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDatabase() {
        return XMLFunction.getElemValue(update, "DATABASE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setSql(String value) {
        XMLFunction.setElemValue(update, "SQL", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSql() {
        return XMLFunction.getElemValue(update, "SQL");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setSqlFilter(String value) {
        XMLFunction.setElemValue(update, "SQLFILTER", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSqlFilter() {
        return XMLFunction.getElemValue(update, "SQLFILTER");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setMessageTrue(String value) {
        XMLFunction.setElemValue(update, "MSGTRUE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getMessageTrue() {
        return XMLFunction.getElemValue(update, "MSGTRUE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setNoMessage(String value) {
        XMLFunction.setElemValue(update, "MSGFALSE", "NOMESSAGE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getNoMessage() {
        return XMLFunction.getElemValue(update, "MSGFALSE", "NOMESSAGE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param code DOCUMENT ME!
     * @param value DOCUMENT ME!
     */
    public void setMessageFalse(String code, String value) {
        if (code.equals("") || value.equals("")) {
            return;
        }
        if ((code == null) || (value == null)) {
            return;
        }
        Element msgs = update.getChild("MSGFALSE");
        if (msgs == null) {
            update.addContent(new Element("MSGFALSE"));
            msgs = update.getChild("MSGFALSE");
        }
        Element msg =
            XMLFunction.getChildByAttribute(msgs, "MESSAGE", "CODE", code);
        if (msg == null) {
            msgs.addContent(new Element("MESSAGE").setAttribute("CODE", code));
            msg = XMLFunction.getChildByAttribute(msgs, "MESSAGE", "CODE", code);
        }
        msg.setText(value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getMessages() {
        List ret = new ArrayList();
        Element msgs = update.getChild("MSGFALSE");
        if (msgs == null) {
            return ret;
        }
        List list = msgs.getChildren("MESSAGE");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                String code = ele.getAttribute("CODE").getValue();
                String value = ele.getText();
                ret.add("[" + code + "] - " + value);
            }
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param code DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getMessageFalse(String code) {
        if (code.equals("")) {
            return "";
        }
        if (code == null) {
            return "";
        }
        Element msgs = update.getChild("MSGFALSE");
        if (msgs == null) {
            return "";
        }
        Element msg =
            XMLFunction.getChildByAttribute(msgs, "MESSAGE", "CODE", code);
        if (msg == null) {
            return "";
        }
        return msg.getText();
    }

    /**
     * DOCUMENT ME!
     *
     * @param code DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeMessageFalse(String code) {
        if (code.equals("")) {
            return ErrorCode.EMPTY;
        }
        if (code == null) {
            return ErrorCode.NULL;
        }
        Element msgs = update.getChild("MSGFALSE");
        if (msgs == null) {
            return ErrorCode.NOEXIST;
        }
        Element msg =
            XMLFunction.getChildByAttribute(msgs, "MESSAGE", "CODE", code);
        if (msg == null) {
            return ErrorCode.NOEXIST;
        }
        msgs.removeContent(msg);
        return ErrorCode.NOERROR;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AbstractActionElement cloneMe() {
        UpdateElement obj = new UpdateElement((Element) update.clone());
        return obj;
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setAutoCommit(String value) {
        XMLFunction.setElemValue(update, "AUTOCOMMIT", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getAutoCommit() {
        return XMLFunction.getElemValue(update, "AUTOCOMMIT");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setNoException(String value) {
        XMLFunction.setElemValue(update, "NOEXCEPTION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getNoException() {
        return XMLFunction.getElemValue(update, "NOEXCEPTION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setFromObject(String value) {
        XMLFunction.setElemValue(update, "FROMOBJECT", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getFromObject() {
        return XMLFunction.getElemValue(update, "FROMOBJECT");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toJSP() {
        StringBuffer resp = new StringBuffer();
        resp.append(CompFunction.toJSP(this, false));
        String cName = getClass().getSimpleName();
        String varName = cName.toLowerCase() + getSeq();
        resp.append(CompFunction.jspErrorMessages(getMessages(), varName));
        resp.append(CompFunction.jspCore(cName, varName));
        return resp.toString();
    }
}
