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

import org.jdom.Element;

import br.com.webinside.runtime.xml.XMLFunction;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public abstract class AbstractEvent implements ProjectElement {

	private static final long serialVersionUID = 1L;
	
	/** DOCUMENT ME! */
    public static final String TYPE_SELECT = "SELECT";
    /** DOCUMENT ME! */
    public static final String TYPE_UPDATE = "UPDATE";
    /** DOCUMENT ME! */
    public static final String DIRECTORY = "events";
    /** DOCUMENT ME! */
    protected Element event;
    private AbstractProject project;

    /**
     * Creates a new AbstractEvent object.
     *
     * @param id DOCUMENT ME!
     */
    public AbstractEvent(String id) {
        event = new Element("EVENT");
        event.setAttribute("ID", id);
    }

    /**
     * Creates a new AbstractEvent object.
     *
     * @param id DOCUMENT ME!
     * @param element DOCUMENT ME!
     */
    public AbstractEvent(String id, Element element) {
        if ((element == null) || (!element.getName().equals("EVENT"))) {
            element = new Element("EVENT");
        }
        if (element.getAttribute("ID") == null) {
            element.setAttribute("ID", id);
        }
        this.event = element;
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
        if (obj instanceof AbstractEvent) {
            String id = ((AbstractEvent) obj).getId();
            if (id.equalsIgnoreCase(getId())) {
                return true;
            }
        }
        return false;
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
    public String getId() {
        return event.getAttributeValue("ID");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setDescription(String value) {
        XMLFunction.setElemValue(event, "DESCRIPTION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDescription() {
        return XMLFunction.getElemValue(event, "DESCRIPTION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setCondition(String value) {
        XMLFunction.setElemValue(event, "CONDITION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getCondition() {
        return XMLFunction.getElemValue(event, "CONDITION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setDatabase(String value) {
        XMLFunction.setElemValue(event, "DATABASE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDatabase() {
        return XMLFunction.getElemValue(event, "DATABASE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setSql(String value) {
        XMLFunction.setElemValue(event, "SQL", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSql() {
        return XMLFunction.getElemValue(event, "SQL");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setSqlFilter(String value) {
        XMLFunction.setElemValue(event, "SQLFILTER", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSqlFilter() {
        return XMLFunction.getElemValue(event, "SQLFILTER");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AbstractEvent cloneMe() {
        AbstractEvent obj = null;
        if (this instanceof EventSelect) {
            obj = new EventSelect(getId(), (Element) event.clone());
        } else if (this instanceof EventUpdate) {
            obj = new EventUpdate(getId(), (Element) event.clone());
        }
        return obj;
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     */
    protected void setType(String type) {
        event.setAttribute("TYPE", type);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getType() {
        String ret = "";
        if (this instanceof EventUpdate) {
            ret = TYPE_UPDATE;
        }
        if (this instanceof EventSelect) {
            ret = TYPE_SELECT;
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toJSP() {
        StringBuffer resp = new StringBuffer();
        String cName = this.getClass().getSimpleName();
        resp.append("<w:setProjectElement\n");
    	resp.append("  type=\"" + cName + "\" name=\"event_" + getId() + "\"\n");
        resp.append("/><jsp:useBean\n");
        resp.append("  id=\"event_" + getId() + "\" ");
        resp.append("type=\"br.com.webinside.runtime.component." + cName + "\"\n");
        resp.append("/>");        
        resp.append(CompFunction.setProperties(this, "event_" + getId()));
        return resp.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Element getElement() {
        return event;
    }
}
