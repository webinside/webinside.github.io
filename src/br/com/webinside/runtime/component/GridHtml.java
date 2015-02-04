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
 * @version $Revision: 1.1 $
 */
public class GridHtml extends AbstractGridLinear {

	private static final long serialVersionUID = 1L;

    private static Document template;
    /** Tipos de grid Html */
    public static final String[] SUBTYPES =
    {"ATTACH", "FTP", "JAVA", "LOCAL", "POP3", "IMAP", "WIOBJECT"};

    /**
     * Creates a new GridHtml object.
     *
     * @param id DOCUMENT ME!
     */
    public GridHtml(String id) {
        super(id);
        this.setType("HTML");
    }

    /**
     * Creates a new GridHtml object.
     *
     * @param id DOCUMENT ME!
     * @param element DOCUMENT ME!
     */
    public GridHtml(String id, Element element) {
        super(id, element);
        this.setType("HTML");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setWIType(String value) {
        XMLFunction.setElemValue(grid, "TYPE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getWIType() {
        return XMLFunction.getElemValue(grid, "TYPE");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Document getTemplate() {
        if (template == null) {
            template = CompFunction.getTemplate("grid_html.xml");
        }
        return template;
    }
}
