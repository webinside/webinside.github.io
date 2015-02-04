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
public class CookieWrite extends AbstractCookie {

	private static final long serialVersionUID = 1L;

	/**
     * Creates a new CookieWrite object.
     */
    public CookieWrite() {
        this.setSeq("");
    }

    /**
     * Creates a new CookieWrite object.
     *
     * @param element DOCUMENT ME!
     */
    public CookieWrite(Element element) {
        if ((element == null) || (!element.getName().equals("COOKIE"))) {
            element = new Element("COOKIE");
        }
        this.cookie = element;
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setValue(String value) {
        XMLFunction.setElemValue(cookie, "VALUE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getValue() {
        return XMLFunction.getElemValue(cookie, "VALUE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setPath(String value) {
        XMLFunction.setElemValue(cookie, "PATH", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPath() {
        return XMLFunction.getElemValue(cookie, "PATH");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setDomain(String value) {
        XMLFunction.setElemValue(cookie, "DOMAIN", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDomain() {
        return XMLFunction.getElemValue(cookie, "DOMAIN");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setMaxAge(String value) {
        XMLFunction.setElemValue(cookie, "MAXAGE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getMaxAge() {
        return XMLFunction.getElemValue(cookie, "MAXAGE");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AbstractActionElement cloneMe() {
        CookieWrite obj = new CookieWrite((Element) cookie.clone());
        return obj;
    }
}
