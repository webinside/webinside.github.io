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

import br.com.webinside.runtime.util.*;
import br.com.webinside.runtime.xml.*;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class Host {
    /** DOCUMENT ME! */
    protected Element host;

    /**
     * Creates a new Host object.
     */
    public Host() {
        host = new Element("HOST");
        host.setAttribute("ID", "");
    }

    /**
     * Creates a new Host object.
     *
     * @param id DOCUMENT ME!
     */
    public Host(String id) {
        if (id == null) {
            id = "";
        }
        host = new Element("HOST");
        host.setAttribute("ID", id);
    }

    /**
     * Creates a new Host object.
     *
     * @param element DOCUMENT ME!
     */
    public Host(Element element) {
        if ((element == null) || (!element.getName().equals("HOST"))) {
            element = new Element("HOST");
        }
        if (element.getAttribute("ID") == null) {
            element.setAttribute("ID", "");
        }
        this.host = element;
    }
    
    public Element getElement() {
        return host;
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
        if (obj instanceof Host) {
            String id = ((Host) obj).getId();
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
        element.addContent(host);
        return ErrorCode.NOERROR;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getId() {
        return host.getAttribute("ID").getValue();
    }

    /**
     * DOCUMENT ME!
     *
     * @param id DOCUMENT ME!
     */
    public void setId(String id) {
        if (id == null) {
            id = "";
        }
        host.getAttribute("ID").setValue(id);
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setDescription(String value) {
        XMLFunction.setElemValue(host, "DESCRIPTION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDescription() {
        return XMLFunction.getElemValue(host, "DESCRIPTION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setAddress(String value) {
        setAddress(value, "");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     * @param profile DOCUMENT ME!
     */
    public void setAddress(String value, String profile) {
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();
        Element aux = host;
        if (!profile.equals("")) {
            aux = getProfileHost(profile, true);
        }
        XMLFunction.setElemValue(aux, "ADDRESS", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getAddress() {
        return getAddress("");
    }

    /**
     * DOCUMENT ME!
     *
     * @param profile DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getAddress(String profile) {
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();
        Element aux = host;
        if (!profile.equals("")) {
            aux = getProfileHost(profile, false);
        }
        return XMLFunction.getElemValue(aux, "ADDRESS");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setProtocol(String value) {
        XMLFunction.setElemValue(host, "PROTOCOL", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getProtocol() {
        return XMLFunction.getElemValue(host, "PROTOCOL");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setPort(String value) {
        setPort(value, "");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     * @param profile DOCUMENT ME!
     */
    public void setPort(String value, String profile) {
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();
        Element aux = host;
        if (!profile.equals("")) {
            aux = getProfileHost(profile, true);
        }
        XMLFunction.setElemValue(aux, "PORT", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPort() {
        return getPort("");
    }

    /**
     * DOCUMENT ME!
     *
     * @param profile DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPort(String profile) {
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();
        Element aux = host;
        if (!profile.equals("")) {
            aux = getProfileHost(profile, false);
        }
        return XMLFunction.getElemValue(aux, "PORT");
    }

    /**
     * DOCUMENT ME!
     *
     * @param id DOCUMENT ME!
     * @param pass DOCUMENT ME!
     */
    public void setUser(String id, String pass) {
        setUser(id, pass, "");
    }

    /**
     * DOCUMENT ME!
     *
     * @param id DOCUMENT ME!
     * @param pass DOCUMENT ME!
     * @param profile DOCUMENT ME!
     */
    public void setUser(String id, String pass, String profile) {
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();
        Element aux = host;
        if (!profile.equals("")) {
            aux = getProfileHost(profile, true);
        }
        aux.removeChild("USER");
        if (!id.equals("") || !pass.equals("")) {
            XMLFunction.setAttrValue(aux, "USER", "ID", id);
            Encrypter pw = new Encrypter(pass);
            XMLFunction.setAttrValue(aux, "USER", "PASS", pw.encodeDES());
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getUser() {
        return getUser("");
    }

    /**
     * DOCUMENT ME!
     *
     * @param profile DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getUser(String profile) {
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();
        Element aux = host;
        if (!profile.equals("")) {
            aux = getProfileHost(profile, false);
        }
        return XMLFunction.getAttrValue(aux, "USER", "ID");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPass() {
        return getPass("");
    }

    /**
     * DOCUMENT ME!
     *
     * @param profile DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPass(String profile) {
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();
        Element aux = host;
        if (!profile.equals("")) {
            aux = getProfileHost(profile, false);
        }
        String pass = XMLFunction.getAttrValue(aux, "USER", "PASS");
        Encrypter pw = new Encrypter(pass);
        return pw.decodeDES();
    }

    /**
     * DOCUMENT ME!
     *
     * @param profile DOCUMENT ME!
     */
    public void cloneInProfile(String profile) {
        Element ele = getProfileHost(profile, true);
        Host aux = new Host(ele);
        aux.setAddress(getAddress());
		aux.setPort(getPort());
        aux.setUser(getUser(), getPass());
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Host cloneMe() {
        Host obj = new Host((Element) host.clone());
        return obj;
    }

    private Element getProfileHost(String profile, boolean create) {
        Element project = (Element) host.getParent();
        while ((project != null) && !project.getName().equals("PROJECT")) {
            project = (Element) project.getParent();
        }
        if (project == null) {
            return null;
        }
        ProjectProfile projProf =
            AbstractProject.getProfile(project, profile, create);
        if (projProf == null) {
            return null;
        }
        Element profEle = projProf.getElement();
        Element hosts = profEle.getChild("HOSTS");
        if (hosts == null) {
            if (create) {
                hosts = new Element("HOSTS");
                profEle.addContent(hosts);
            } else {
                return null;
            }
        }
        Element ret =
            XMLFunction.getChildByAttribute(hosts, "HOST", "ID", getId());
        if ((ret == null) && create) {
            ret = new Element("HOST");
            ret.setAttribute("ID", getId());
            hosts.addContent(ret);
        }
        return ret;
    }
}
