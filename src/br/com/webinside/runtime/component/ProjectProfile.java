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

import br.com.webinside.runtime.xml.ErrorCode;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class ProjectProfile {
    /** DOCUMENT ME! */
    Element profile;

    /**
     * Creates a new ProjectProfile object.
     */
    public ProjectProfile() {
        profile = new Element("PROFILE");
        profile.setAttribute("ID", "");
    }

    /**
     * Creates a new ProjectProfile object.
     *
     * @param id DOCUMENT ME!
     */
    public ProjectProfile(String id) {
        if (id == null) {
            id = "";
        }
        profile = new Element("PROFILE");
        profile.setAttribute("ID", id);
    }

    /**
     * Creates a new ProjectProfile object.
     *
     * @param element DOCUMENT ME!
     */
    public ProjectProfile(Element element) {
        if ((element == null) || (!element.getName().equals("PROFILE"))) {
            element = new Element("PROFILE");
        }
        if (element.getAttribute("ID") == null) {
            element.setAttribute("ID", "");
        }
        this.profile = element;
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
        if (obj instanceof ProjectProfile) {
            String id = ((ProjectProfile) obj).getId();
            if (id.equalsIgnoreCase(getId())) {
                return true;
            }
        }
        return false;
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
        element.addContent(profile);
        return ErrorCode.NOERROR;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getId() {
        return profile.getAttribute("ID").getValue();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Element getElement() {
        return profile;
    }
}
