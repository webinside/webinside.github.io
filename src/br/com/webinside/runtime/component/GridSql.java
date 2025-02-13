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
