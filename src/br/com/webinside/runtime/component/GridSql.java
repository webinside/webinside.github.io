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
public class GridSql extends AbstractGridLinear {

	private static final long serialVersionUID = 1L;

    private static Document template;

    /**
     * Creates a new GridSql object.
     *
     * @param id DOCUMENT ME!
     */
    public GridSql(String id) {
        super(id);
        this.setType("SQL");
    }

    /**
     * Creates a new GridSql object.
     *
     * @param id DOCUMENT ME!
     * @param element DOCUMENT ME!
     */
    public GridSql(String id, Element element) {
        super(id, element);
        this.setType("SQL");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setDatabase(String value) {
        XMLFunction.setElemValue(grid, "DATABASE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDatabase() {
        return XMLFunction.getElemValue(grid, "DATABASE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setSql(String value) {
        XMLFunction.setElemValue(grid, "SQL", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSql() {
        return XMLFunction.getElemValue(grid, "SQL");
    }
    
	/**
	 * DOCUMENT ME!
	 *
	 * @param value DOCUMENT ME!
	 */
	public void setIgnoreVars(String value) {
		XMLFunction.setElemValue(grid, "IGNOREVARS", value);
	}

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public String getIgnoreVars() {
		return XMLFunction.getElemValue(grid, "IGNOREVARS");
	}       

	/**
	 * DOCUMENT ME!
	 *
	 * @param value DOCUMENT ME!
	 */
    public void setRecursive(boolean value) {
        String msg = (value == true) ? "ON"
                                     : "";
        XMLFunction.setElemValue(grid, "ISRECURSIVE", msg);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isRecursive() {
        return XMLFunction.getElemValue(grid, "ISRECURSIVE").equals("ON");
    }

	/**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setSqlFilter(String value) {
        XMLFunction.setElemValue(grid, "SQLFILTER", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSqlFilter() {
        return XMLFunction.getElemValue(grid, "SQLFILTER");
    }

	/**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setExportIn(String value) {
        XMLFunction.setElemValue(grid, "EXPORTIN", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getExportIn() {
        return XMLFunction.getElemValue(grid, "EXPORTIN");
    }

	/**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setExportOut(String value) {
        XMLFunction.setElemValue(grid, "EXPORTOUT", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getExportOut() {
        return XMLFunction.getElemValue(grid, "EXPORTOUT");
    }
    
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Document getTemplate() {
        if (template == null) {
            template = CompFunction.getTemplate("grid_sql.xml");
        }
        return template;
    }
}
