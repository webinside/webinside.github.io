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

import br.com.webinside.runtime.database.DatabaseDrivers;
import br.com.webinside.runtime.xml.*;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class JdbcAlias {
		
    private Element alias;

    /**
     * Creates a new JdbcAlias object.
     */
    public JdbcAlias() {
        alias = new Element("JDBC");
    }

    /**
     * Creates a new JdbcAlias object.
     *
     * @param ele DOCUMENT ME!
     */
    public JdbcAlias(Element ele) {
        if ((ele == null) || !ele.getName().equals("JDBC")) {
            alias = new Element("JDBC");
        } else {
            alias = ele;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getID() {
        return XMLFunction.getAttrValue(alias, "ID");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDescription() {
        return XMLFunction.getElemValue(alias, "DESCRIPTION");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getClassName() {
        return XMLFunction.getElemValue(alias, "CLASS");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getUrl() {
        return XMLFunction.getElemValue(alias, "URL");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getTemplate() {
        return XMLFunction.getElemValue(alias, "TEMPLATE");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getClassType() {
        return XMLFunction.getAttrValue(alias, "CLASS", "TYPE").toUpperCase();
    }
    
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean usePreparedStatement() {
    	String aux = XMLFunction.getElemValue(alias, "PREPAREDSTATEMENT"); 
        return !aux.equalsIgnoreCase("false");
    }
    
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getValidationQuery() {
        return XMLFunction.getElemValue(alias, "VALIDATIONQUERY");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getVersion() {
        return DatabaseDrivers.getVersion(this);
    }
}
