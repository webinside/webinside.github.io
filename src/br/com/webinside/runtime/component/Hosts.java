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

import java.util.*;

import org.jdom.*;

import br.com.webinside.runtime.xml.*;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class Hosts {
    private static Object sync = new Object();
    private Element hosts;
    private Element parent;
    private AbstractProject project;

    /**
     * Creates a new Hosts object.
     *
     * @param parent DOCUMENT ME!
     * @param hosts DOCUMENT ME!
     */
    public Hosts(Element parent, Element hosts) {
        if ((hosts == null) || (!hosts.getName().equals("HOSTS"))) {
            hosts = new Element("HOSTS");
        }
        this.hosts = hosts;
        this.parent = parent;
    }

    /**
     * DOCUMENT ME!
     *
     * @param host DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int addHost(Host host) {
        synchronized (sync) {
            int ret = ErrorCode.NOERROR;
            if (host == null) {
                ret = ErrorCode.NULL;
            } else {
                String name = host.getId();
                if (name.equals("")) {
                    ret = ErrorCode.EMPTY;
                } else if (XMLFunction.getChildByAttribute(hosts, "HOST", "ID",
                                name) != null) {
                    ret = ErrorCode.EXIST;
                } else {
                    if (parent.getChild("HOSTS") == null) {
                        parent.addContent(new Element("HOSTS"));
                        hosts = parent.getChild("HOSTS");
                    }
                    ret = host.insertInto(hosts);
                }
            }
            return ret;
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getIds() {
        List ret = new ArrayList();
        List list = hosts.getChildren("HOST");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            	// Não deve ocorrer
            	err.printStackTrace(System.err);
            }
            if (ele != null) {
                ret.add(XMLFunction.getAttrValue(ele, "ID"));
            }
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List get() {
        List ret = new ArrayList();
        List list = hosts.getChildren("HOST");
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new Host(ele));
            }
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param id DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Host getHost(String id) {
        if ((id == null) || id.equals("")) {
            return null;
        }
        Element host = XMLFunction.getChildByAttribute(hosts, "HOST", "ID", id);
        if (host == null) {
            return null;
        } else {
            return new Host(host);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param id DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeHost(String id) {
        if (id == null) {
            return ErrorCode.NULL;
        }
        if (id.equals("")) {
            return ErrorCode.EMPTY;
        }
        Element host = XMLFunction.getChildByAttribute(hosts, "HOST", "ID", id);
        if (host == null) {
            return ErrorCode.NOEXIST;
        }
        hosts.removeContent(host);
        if (hosts.getChildren().size() == 0) {
        	((Element) hosts.getParent()).removeChild("HOSTS");
        }
        if (parent != null) {
            Element profiles = parent.getChild("PROFILES");
            XMLFunction.removeRecursiveChild(profiles, "HOST", "ID", id);
        }
        return ErrorCode.NOERROR;
    }

    public boolean hasSMTP() {
        List list = hosts.getChildren("HOST");
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
            	Element host = (Element) i.next();
                String prot = XMLFunction.getElemValue(host, "PROTOCOL");
                if (prot.startsWith("SMTP")) return true;
            } catch (ClassCastException err) {
            	// Não deve ocorrer
            	err.printStackTrace(System.err);
            }
        }
        return false;
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
     * @param project DOCUMENT ME!
     */
    public void setProject(AbstractProject project) {
        this.project = project;
    }
}
