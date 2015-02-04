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
public abstract class AbstractGrid implements ProjectElement {

	private static final long serialVersionUID = 1L;
	
	/** DOCUMENT ME! */
    public static final String TYPE_SQL = "SQL";
    /** DOCUMENT ME! */
    public static final String TYPE_HTML = "HTML";
    /** DOCUMENT ME! */
    public static final String TYPE_XMLOUT = "XMLOUT";
    /** DOCUMENT ME! */
    public static final String DIRECTORY = "grids";
    /** DOCUMENT ME! */
    protected Element grid;
    private AbstractProject project;

    /**
     * Creates a new AbstractGrid object.
     *
     * @param id DOCUMENT ME!
     */
    public AbstractGrid(String id) {
        if (id == null) {
            id = "";
        }
        grid = new Element("GRID");
        grid.setAttribute("ID", id);
    }

    /**
     * Creates a new AbstractGrid object.
     *
     * @param id DOCUMENT ME!
     * @param element DOCUMENT ME!
     */
    public AbstractGrid(String id, Element element) {
        if ((element == null) || (!element.getName().equals("GRID"))) {
            element = new Element("GRID");
        }
        if (element.getAttribute("ID") == null) {
            element.setAttribute("ID", id);
        }
        this.grid = element;
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
        if (obj instanceof AbstractGrid) {
            String id = ((AbstractGrid) obj).getId();
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
        return grid.getAttributeValue("ID");
    }

    /**
     * DOCUMENT ME!
     *
     * @param id DOCUMENT ME!
     */
    public void setId(String id) {
        grid.setAttribute("ID", id);
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setDescription(String value) {
        XMLFunction.setElemValue(grid, "DESCRIPTION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDescription() {
        return XMLFunction.getElemValue(grid, "DESCRIPTION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setExecute(String value) {
        XMLFunction.setElemValue(grid, "EXECUTE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getExecute() {
        return XMLFunction.getElemValue(grid, "EXECUTE");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AbstractGrid cloneMe() {
        AbstractGrid obj = null;
        if (this instanceof GridHtml) {
            obj = new GridHtml(getId(), (Element) grid.clone());
        } else if (this instanceof GridSql) {
            obj = new GridSql(getId(), (Element) grid.clone());
        } else if (this instanceof GridXmlOut) {
            obj = new GridXmlOut(getId(), (Element) grid.clone());
        }
        return obj;
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     */
    protected void setType(String type) {
        grid.setAttribute("TYPE", type);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getType() {
        String ret = "";
        if (this instanceof GridHtml) {
            ret = TYPE_HTML;
        } else if (this instanceof GridSql) {
            ret = TYPE_SQL;
        } else if (this instanceof GridXmlOut) {
            ret = TYPE_XMLOUT;
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
    	resp.append("  type=\"" + cName + "\" name=\"grid_" + getId() + "\"\n");
        resp.append("/><jsp:useBean\n");
        resp.append("  id=\"grid_" + getId() + "\" ");
        resp.append("type=\"br.com.webinside.runtime.component." + cName + "\"\n");
        resp.append("/>");        
        resp.append(CompFunction.setProperties(this, "grid_" + getId()));
        return resp.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Element getElement() {
        return grid;
    }
}
