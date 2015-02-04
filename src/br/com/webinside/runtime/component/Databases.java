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

import java.util.ArrayList;
import java.util.List;
import org.jdom.Element;

import br.com.webinside.runtime.xml.ErrorCode;
import br.com.webinside.runtime.xml.XMLFunction;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class Databases {
    private static Object sync = new Object();
    private Element databases;
    private Element parent;
    private AbstractProject project;

    /**
     * Creates a new Databases object.
     *
     * @param parent DOCUMENT ME!
     * @param databases DOCUMENT ME!
     */
    public Databases(Element parent, Element databases) {
        if ((databases == null) || (!databases.getName().equals("DATABASES"))) {
            databases = new Element("DATABASES");
        }
        this.databases = databases;
        this.parent = parent;
    }
    
    public Element getElement() {
        return databases;
    }

    /**
     * DOCUMENT ME!
     *
     * @param database DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int addDatabase(Database database) {
        synchronized (sync) {
            int ret = ErrorCode.NOERROR;
            if (database == null) {
                ret = ErrorCode.NULL;
            } else {
                String name = database.getId();
                if (name.equals("")) {
                    ret = ErrorCode.EMPTY;
                } else if (XMLFunction.getChildByAttribute(databases,
                                "DATABASE", "ID", name) != null) {
                    ret = ErrorCode.EXIST;
                } else {
                    if (parent.getChild("DATABASES") == null) {
                        parent.addContent(new Element("DATABASES"));
                        databases = parent.getChild("DATABASES");
                    }
                    ret = database.insertInto(databases);
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
        List list = databases.getChildren("DATABASE");
        Element ele = null;
        for (int i = 0; i < list.size(); i++) {
            try {
                ele = (Element) list.get(i);
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
        List list = databases.getChildren("DATABASE");
        Element ele = null;
        for (int i = 0; i < list.size(); i++) {
            try {
                ele = (Element) list.get(i);
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                ret.add(new Database(ele));
            }
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Database getDatabase(String name) {
        if ((name == null) || name.equals("")) {
            return null;
        }
        if (name.equals("WI-JAVA")) {
            Database db = new Database();
            db.setId("WI-JAVA");
            db.setType("JAVA");
            return db;
        }
        Element dbElement = null;
        dbElement =
            XMLFunction.getChildByAttribute(databases, "DATABASE", "ID", name);
        if (dbElement == null) {
            return null;
        } else {
            return new Database(dbElement);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param name DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeDatabase(String name) {
        if (name == null) {
            return ErrorCode.NULL;
        }
        if (name.equals("")) {
            return ErrorCode.EMPTY;
        }
        Element dbElement =
            XMLFunction.getChildByAttribute(databases, "DATABASE", "ID", name);
        if (dbElement == null) {
            return ErrorCode.NOEXIST;
        }
        databases.removeContent(dbElement);
        if (databases.getChildren().size() == 0) {
        	((Element) databases.getParent()).removeChild("DATABASES");
        }
        if (parent != null) {
            Element profiles = parent.getChild("PROFILES");
            XMLFunction.removeRecursiveChild(profiles, "DATABASE", "ID", name);
        }
        return ErrorCode.NOERROR;
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
