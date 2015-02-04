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
public class EventUpdate extends AbstractEvent {

	private static final long serialVersionUID = 1L;

    private static Document template;

    /**
     * Creates a new EventUpdate object.
     *
     * @param id DOCUMENT ME!
     */
    public EventUpdate(String id) {
        super(id);
        this.setType("UPDATE");
    }

    /**
     * Creates a new EventUpdate object.
     *
     * @param id DOCUMENT ME!
     * @param element DOCUMENT ME!
     */
    public EventUpdate(String id, Element element) {
        super(id, element);
        this.setType("UPDATE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setWIObj(String value) {
        XMLFunction.setElemValue(event, "WIOBJ", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getWIObj() {
        return XMLFunction.getElemValue(event, "WIOBJ");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setPrefix(String value) {
        XMLFunction.setElemValue(event, "PREFIX", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPrefix() {
        return XMLFunction.getElemValue(event, "PREFIX");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setSize(String value) {
        XMLFunction.setElemValue(event, "SIZE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSize() {
        return XMLFunction.getElemValue(event, "SIZE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setRowCondition(String value) {
        XMLFunction.setElemValue(event, "ROWCONDITION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getRowCondition() {
        return XMLFunction.getElemValue(event, "ROWCONDITION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setMessageTrue(String value) {
        XMLFunction.setElemValue(event, "MSGTRUE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getMessageTrue() {
        return XMLFunction.getElemValue(event, "MSGTRUE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setNoMessage(String value) {
        XMLFunction.setElemValue(event, "MSGFALSE", "NOMESSAGE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getNoMessage() {
        return XMLFunction.getElemValue(event, "MSGFALSE", "NOMESSAGE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param code DOCUMENT ME!
     * @param value DOCUMENT ME!
     */
    public void setMessageFalse(String code, String value) {
        if ((code.equals("")) || (value.equals(""))) {
            return;
        }
        if ((code == null) || (value == null)) {
            return;
        }
        Element msgs = event.getChild("MSGFALSE");
        if (msgs == null) {
            event.addContent(new Element("MSGFALSE"));
            msgs = event.getChild("MSGFALSE");
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
        Element msgs = event.getChild("MSGFALSE");
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
        Element msgs = event.getChild("MSGFALSE");
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
        Element msgs = event.getChild("MSGFALSE");
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
     * @param value DOCUMENT ME!
     */
    public void setAutoCommit(String value) {
        XMLFunction.setElemValue(event, "AUTOCOMMIT", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getAutoCommit() {
        return XMLFunction.getElemValue(event, "AUTOCOMMIT");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Document getTemplate() {
        if (template == null) {
            template = CompFunction.getTemplate("event_update.xml");
        }
        return template;
    }
}
