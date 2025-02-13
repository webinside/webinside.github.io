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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.jdom.CDATA;
import org.jdom.Element;

import br.com.webinside.runtime.util.StringComparator;
import br.com.webinside.runtime.xml.XMLFunction;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.10 $
 */
public abstract class AbstractProject implements Serializable {

	private static final long serialVersionUID = 1L;
    /** DOCUMENT ME! */
    protected String prjname;
    /** DOCUMENT ME! */
    protected Element project;
    /** DOCUMENT ME! */
    protected AbstractProject parent;

    /**
     * Creates a new AbstractProject object.
     *
     * @param id DOCUMENT ME!
     */
    public AbstractProject(String id) {
        project = new Element("PROJECT");
        prjname = id.trim();
    }

    /**
     * Creates a new AbstractProject object.
     *
     * @param id DOCUMENT ME!
     * @param element DOCUMENT ME!
     */
    public AbstractProject(String id, Element element) {
        if ((element == null) || (!element.getName().equals("PROJECT"))) {
            element = new Element("PROJECT");
        }
        this.project = element;
        prjname = id.trim();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Element getElement() {
        return project;
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setVersion(String value) {
        XMLFunction.setElemValue(project, "WIVERSION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getVersion() {
        return XMLFunction.getElemValue(project, "WIVERSION");
    }
     
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getId() {
        return prjname;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract Databases getDatabases();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract Hosts getHosts();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract ProjectElementsMap getCombos();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract ProjectElementsMap getGrids();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract ProjectElementsMap getEvents();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract ProjectElementsMap getPages();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract ProjectElementsMap getDownloads();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract ProjectElementsMap getUploads();

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public abstract ProjectElementsMap getWebServices();

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setTitle(String value) {
        XMLFunction.setElemValue(project, "DEFINITION", "TITLE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getTitle() {
        return XMLFunction.getElemValue(project, "DEFINITION", "TITLE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setJavaPackage(String value) {
        XMLFunction.setElemValue(project, "DEFINITION", "JAVAPACKAGE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getJavaPackage() {
        return XMLFunction.getElemValue(project, "DEFINITION", "JAVAPACKAGE");
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setTimezone(String value) {
        XMLFunction.setElemValue(project, "DEFINITION", "TIMEZONE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getTimezone() {
        return XMLFunction.getElemValue(project, "DEFINITION", "TIMEZONE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setMakeCompatible(boolean value) {
        String msg = (value == true) ? "ON"
                                     : "";
        XMLFunction.setElemValue(project, "DEFINITION", "MAKECOMPATIBLE", msg);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isMakeCompatible() {
    	return XMLFunction.getElemValue(project, "DEFINITION", "MAKECOMPATIBLE")
    			.equals("ON");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setTmpRequestVar(boolean value) {
        String msg = (value == true) ? "ON" : "";
        XMLFunction.setElemValue(project, "DEFINITION", "TMPREQUESTVAR", msg);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isTmpRequestVar() {
        return XMLFunction.getElemValue(project, "DEFINITION", "TMPREQUESTVAR")
                .equals("ON");
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     */
    public void setRequestLog(String type) {
        XMLFunction.setElemValue(project, "DEFINITION", "REQUESTLOG", type);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getRequestLog() {
        return XMLFunction.getElemValue(project, "DEFINITION", "REQUESTLOG");
    }

    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     */
    public void setDBLogDatabase(String type) {
        XMLFunction.setElemValue(project, "DBLOG", "DATABASE", type);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDBLogDatabase() {
        return XMLFunction.getElemValue(project, "DBLOG", "DATABASE");
    }
    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     */

    public void setDBLogTable(String type) {
        XMLFunction.setElemValue(project, "DBLOG", "TABLE", type);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDBLogTable() {
        return XMLFunction.getElemValue(project, "DBLOG", "TABLE");
    }

    
    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     */
    public void setSqlLog(String type) {
        XMLFunction.setElemValue(project, "DEFINITION", "SQLLOG", type);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSqlLog() {
        return XMLFunction.getElemValue(project, "DEFINITION", "SQLLOG");
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setLoginCrypto(String value) {
        XMLFunction.setElemValue(project, "LOGIN", "CRYPTO", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getLoginCrypto() {
    	if (XMLFunction.getElemValue(project, "LOGIN", "MD5").equals("ON")) {
    		XMLFunction.setElemValue(project, "LOGIN", "MD5", "");
    		setLoginCrypto("MD5");
    	}
    	return XMLFunction.getElemValue(project, "LOGIN", "CRYPTO");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setParentId(String value) {
        XMLFunction.setElemValue(project, "DEFINITION", "PARENT", value.trim());
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getParentId() {
        return XMLFunction.getElemValue(project, "DEFINITION", "PARENT");
    }

    /**
     * DOCUMENT ME!
     *
     * @param id DOCUMENT ME!
     * @param className DOCUMENT ME!
     */
    public void addFunction(String id, String className) {
        if ((id == null) || id.equals("") || (className == null)) {
            return;
        }
        Element ele = project.getChild("FUNCTIONS");
        if (ele == null) {
            ele = new Element("FUNCTIONS");
            project.addContent(ele);
        }
        Element child =
            XMLFunction.getChildByAttribute(ele, "FUNCTION", "ID", id);
        if (child == null) {
            child = new Element("FUNCTION");
            child.setAttribute("ID", id);
            ele.addContent(child);
        }
        child.setAttribute("CLASS", className);
    }

    public void addDBLogColumn(String id, String value) {
        if ((id == null) || id.equals("") || (value == null)) {
            return;
        }
        Element ele = project.getChild("DBLOG");
        if (ele == null) {
            ele = new Element("DBLOG");
            project.addContent(ele);
        }
        Element cols = ele.getChild("COLUMNS");
        if (cols == null) {
        	cols = new Element("COLUMNS");
        	ele.addContent(cols);
        }
        Element child =
            XMLFunction.getChildByAttribute(cols, "COLUMN", "ID", id);
        if (child == null) {
            child = new Element("COLUMN");
            child.setAttribute("ID", id);
            cols.addContent(child);
        }
        child.setAttribute("VALUE", value);
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param id DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getFunction(String id) {
        Element ele = project.getChild("FUNCTIONS");
        if ((id == null) || (ele == null)) {
            return "";
        }
        Element child =
            XMLFunction.getChildByAttribute(ele, "FUNCTION", "ID", id);
        if (child != null) {
            String resp = child.getAttributeValue("CLASS");
            if (resp == null) {
                resp = "";
            }
            return resp;
        } else {
            return "";
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param id DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDBLogColumn(String id) {
        Element ele = project.getChild("DBLOG");
        if ((id == null) || (ele == null)) return "";
        Element cols = ele.getChild("COLUMNS");
        if (cols == null) return "";
        Element child =
            XMLFunction.getChildByAttribute(cols, "COLUMN", "ID", id);
        if (child != null) {
            String resp = child.getAttributeValue("VALUE");
            if (resp == null) resp = "";
            return resp;
        } else {
            return "";
        }
    }
    
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Set getFunctionsId() {
        StringComparator sc = new StringComparator();
        sc.setIgnoreCase(true);
        Set resp = new TreeSet(sc);
        Element ele = project.getChild("FUNCTIONS");
        if (ele == null) {
            return resp;
        }
        List list = ele.getChildren("FUNCTION");
        if (list == null) {
            return resp;
        }
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Element func = (Element) it.next();
            resp.add(func.getAttributeValue("ID"));
        }
        return resp;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Set getDBLogColumnsId() {
        Set resp = new LinkedHashSet();
        Element ele = project.getChild("DBLOG");
        if (ele == null) return resp;
        Element cols = ele.getChild("COLUMNS");
        if (cols == null) return resp;
        List list = cols.getChildren("COLUMN");
        if (list == null) return resp;
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Element col = (Element) it.next();
            resp.add(col.getAttributeValue("ID"));
        }
        return resp;
    }
    
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Map getFunctionsMap() {
        Map resp = new HashMap();
        Element ele = project.getChild("FUNCTIONS");
        if (ele == null) {
            return resp;
        }
        List list = ele.getChildren("FUNCTION");
        if (list == null) {
            return resp;
        }
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Element func = (Element) it.next();
            String id = func.getAttributeValue("ID").toLowerCase();
            String className = func.getAttributeValue("CLASS");
            resp.put(id, className);
        }
        return resp;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Map getDBLogColumnsMap() {
        Map resp = new LinkedHashMap();
        Element ele = project.getChild("DBLOG");
        if (ele == null) return resp;
        Element cols = ele.getChild("COLUMNS");
        if (cols == null) return resp;
        List list = cols.getChildren("COLUMN");
        if (list == null) return resp;
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Element col = (Element) it.next();
            String id = col.getAttributeValue("ID");
            String value = col.getAttributeValue("VALUE");
            resp.put(id, value);
        }
        return resp;
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param id DOCUMENT ME!
     */
    public void removeFunction(String id) {
        Element ele = project.getChild("FUNCTIONS");
        if ((id == null) || (ele == null)) {
            return;
        }
        Element child =
            XMLFunction.getChildByAttribute(ele, "FUNCTION", "ID", id);
        if (child != null) {
            ele.removeContent(child);
        }
        if (ele.getChildren().size() == 0) {
            project.removeChild("FUNCTIONS");
        }
    }

    /**
     * DOCUMENT ME!
     */
    public void removeDBLog() {
        Element ele = project.getChild("DBLOG");
        if (ele == null) return;
        project.removeContent(ele);
    }    
        
    /**
     * DOCUMENT ME!
     *
     * @param id DOCUMENT ME!
     */
    public void removeDBLogColumn(String id) {
        Element ele = project.getChild("DBLOG");
        if ((id == null) || (ele == null)) return;
        Element cols = ele.getChild("COLUMNS");
        if (cols == null) return;
        Element child =
            XMLFunction.getChildByAttribute(cols, "COLUMN", "ID", id);
        if (child != null) {
            cols.removeContent(child);
        }
        if (cols.getChildren().size() == 0) {
            ele.removeChild("COLUMNS");
        }
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param id DOCUMENT ME!
     * @param value DOCUMENT ME!
     */
    public void addInitParam(String id, String value) {
        addInitParam(id, value, "");
    }

    /**
     * DOCUMENT ME!
     *
     * @param id DOCUMENT ME!
     * @param value DOCUMENT ME!
     * @param profile DOCUMENT ME!
     */
    public void addInitParam(String id, String value, String profile) {
        if ((id == null) || id.equals("") || (value == null)) {
            return;
        }
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();
        Element ele = null;
        if (profile.equals("")) {
            ele = project.getChild("INIT-PARAMS");
            if (ele == null) {
                ele = new Element("INIT-PARAMS");
                project.addContent(ele);
            }
        } else {
            Element profEle = getProfile(profile, true).getElement();
            ele = profEle.getChild("INIT-PARAMS");
            if (ele == null) {
                ele = new Element("INIT-PARAMS");
                profEle.addContent(ele);
            }
        }
        Element child =
            XMLFunction.getChildByAttribute(ele, "INIT-PARAM", "ID", id);
        if (child == null) {
            child = new Element("INIT-PARAM");
            child.setAttribute("ID", id);
            ele.addContent(child);
        }
        child.removeChild("VALUE");
        Element valueEle = new Element("VALUE");
        valueEle.addContent(new CDATA(value));
        child.addContent(valueEle);
    }

    /**
     * DOCUMENT ME!
     *
     * @param id DOCUMENT ME!
     * @param profile DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getInitParam(String id, String profile) {
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();
        Element ele = null;
        if (profile.equals("")) {
            ele = project.getChild("INIT-PARAMS");
        } else {
            ProjectProfile projProf = getProfile(profile, false);
            if (projProf != null) {
                Element profEle = projProf.getElement();
                ele = profEle.getChild("INIT-PARAMS");
            }
        }
        if ((id == null) || (ele == null)) {
            return "";
        }

        Element child =
            XMLFunction.getChildByAttribute(ele, "INIT-PARAM", "ID", id);
        if (child != null) {
            return child.getChildText("VALUE");
        } else {
            return "";
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param profile DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Set getInitParamIds(String profile) {
        Set resp = new HashSet();
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();
        Element ele = null;
        if (profile.equals("")) {
            ele = project.getChild("INIT-PARAMS");
        } else {
            ProjectProfile projProf = getProfile(profile, false);
            if (projProf != null) {
                Element profEle = projProf.getElement();
                ele = profEle.getChild("INIT-PARAMS");
            }
        }

        if (ele == null) {
            return resp;
        }
        List list = ele.getChildren("INIT-PARAM");
        if (list == null) {
            return resp;
        }
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Element func = (Element) it.next();
            resp.add(func.getAttributeValue("ID"));
        }
        return resp;
    }

    /**
     * DOCUMENT ME!
     *
     * @param id DOCUMENT ME!
     * @param profile DOCUMENT ME!
     */
    public void removeInitParam(String id, String profile) {
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();

        Element ele = null;
        Element profEle = null;
        if (profile.equals("")) {
            ele = project.getChild("INIT-PARAMS");
        } else {
            ProjectProfile projProf = getProfile(profile, false);
            if (projProf != null) {
                profEle = projProf.getElement();
                ele = profEle.getChild("INIT-PARAMS");
            }
        }
        if ((id == null) || (ele == null)) {
            return;
        }

        Element child =
            XMLFunction.getChildByAttribute(ele, "INIT-PARAM", "ID", id);
        if (child != null) {
            ele.removeContent(child);
        }
        if (ele.getChildren().size() == 0) {
            if (profEle == null) {
                project.removeChild("INIT-PARAMS");
            } else {
                profEle.removeChild("INIT-PARAMS");
            }
        }
    }
    
    public void addSecureVar(String var) {
        if ((var == null) || var.trim().equals("")) return;
        Element ele = project.getChild("SECURE-VARS");
        if (ele == null) {
            ele = new Element("SECURE-VARS");
            project.addContent(ele);
        }
        Element child = new Element("SECURE-VAR");
        child.setText(var);
        ele.addContent(child);
    }

    public Set getSecureVars() {
        Set resp = new TreeSet();
        Element ele = project.getChild("SECURE-VARS");
        if (ele == null) return resp;
        List list = ele.getChildren("SECURE-VAR");
        if (list == null) return resp;
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Element var = (Element) it.next();
            resp.add(var.getValue());
        }
        return resp;
    }

    public void removeSecureVars() {
    	project.removeChild("SECURE-VARS");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setTemplate(String value) {
        XMLFunction.setElemValue(project, "DEFINITION", "TEMPLATE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getTemplate() {
        return XMLFunction.getElemValue(project, "DEFINITION", "TEMPLATE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setLogsdir(String value) {
        XMLFunction.setElemValue(project, "DEFINITION", "LOGSDIR", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getlogsdir() {
        return XMLFunction.getElemValue(project, "DEFINITION", "LOGSDIR");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setPrePosPage(String value) {
        XMLFunction.setElemValue(project, "DEFINITION", "PREPOSPAGE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPrePosPage() {
        return XMLFunction.getElemValue(project, "DEFINITION", "PREPOSPAGE");
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setLoginDatabase(String value) {
        XMLFunction.setElemValue(project, "LOGIN", "DATABASE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getLoginDatabase() {
        return XMLFunction.getElemValue(project, "LOGIN", "DATABASE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setLoginSql(String value) {
        if (!value.equals("") && getLoginSql().equals("")) {
            setLoginActive("ON");
            setLoginSqlFilter("%*?'\"");
        }
        if (value.equals("")) {
        	Element login = project.getChild("LOGIN");
        	if (login != null) login.removeChild("SQL");
        } else {
        	XMLFunction.setCDATAValue(project, "LOGIN", "SQL", value);
        }	
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getLoginSql() {
        return XMLFunction.getCDATAValue(project, "LOGIN", "SQL");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setLoginMessage(String value) {
        XMLFunction.setElemValue(project, "LOGIN", "MESSAGE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getLoginMessage() {
        return XMLFunction.getElemValue(project, "LOGIN", "MESSAGE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setLoginSqlFilter(String value) {
        XMLFunction.setElemValue(project, "LOGIN", "SQLFILTER", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getLoginSqlFilter() {
        return XMLFunction.getElemValue(project, "LOGIN", "SQLFILTER");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setLoginPage(String value) {
        XMLFunction.setElemValue(project, "LOGIN", "NOLOGIN", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getLoginPage() {
        return XMLFunction.getElemValue(project, "LOGIN", "NOLOGIN");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setLoginChangePass(String value) {
        XMLFunction.setElemValue(project, "LOGIN", "CHANGEPASS", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getLoginChangePass() {
        return XMLFunction.getElemValue(project, "LOGIN", "CHANGEPASS");
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param type DOCUMENT ME!
     */
    public void setLoginType(String type) {
        if (project.getChild("LOGIN") == null) {
            project.addContent(new Element("LOGIN"));
        }
        Element login = project.getChild("LOGIN");
        XMLFunction.setAttrValue(login, "TYPE", type);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getLoginType() {
        if (project.getChild("LOGIN") == null) {
            return "";
        }
        return XMLFunction.getAttrValue(project, "LOGIN", "TYPE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setLoginActive(String value) {
        if (project.getChild("LOGIN") == null) {
            project.addContent(new Element("LOGIN"));
        }
        Element login = project.getChild("LOGIN");
        login.removeAttribute("ACTIVE");
        value = value.toUpperCase();
        if (value.equals("ON")) {
            login.setAttribute("ACTIVE", value);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getLoginActive() {
        if (project.getChild("LOGIN") == null) {
            return "";
        }
        if (project.getChild("LOGIN").getAttribute("ACTIVE") == null) {
            return "";
        }
        return project.getChild("LOGIN").getAttribute("ACTIVE").getValue();
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setLoginRolesDatabase(String value) {
        XMLFunction.setElemValue(project, "LOGIN", "ROLESDATABASE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getLoginRolesDatabase() {
        return XMLFunction.getElemValue(project, "LOGIN", "ROLESDATABASE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setLoginRolesSql(String value) {
        if (!value.equals("") && getLoginSql().equals("")) {
            setLoginRolesSqlFilter("%*?'\"");
        }
        if (value.equals("")) {
        	Element login = project.getChild("LOGIN");
        	if (login != null) login.removeChild("ROLESSQL");
        } else {
        	XMLFunction.setCDATAValue(project, "LOGIN", "ROLESSQL", value);
        }	
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getLoginRolesSql() {
        return XMLFunction.getCDATAValue(project, "LOGIN", "ROLESSQL");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setLoginRolesSqlFilter(String value) {
        XMLFunction.setElemValue(project, "LOGIN", "ROLESSQLFILTER", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getLoginRolesSqlFilter() {
        return XMLFunction.getElemValue(project, "LOGIN", "ROLESSQLFILTER");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setLoginRolesPage(String value) {
        XMLFunction.setElemValue(project, "LOGIN", "ROLESPAGE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getLoginRolesPage() {
        return XMLFunction.getElemValue(project, "LOGIN", "ROLESPAGE");
    }
    
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getProfiles() {
        List resp = new ArrayList();
        Element profiles = project.getChild("PROFILES");
        if (profiles != null) {
            List ls = profiles.getChildren();
            for (int i = 0; i < ls.size(); i++) {
                Element profile = (Element) ls.get(i);
                resp.add(new ProjectProfile(profile));
            }
        }
        return resp;
    }

    /**
     * DOCUMENT ME!
     *
     * @param profile DOCUMENT ME!
     * @param create DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public ProjectProfile getProfile(String profile, boolean create) {
        return getProfile(project, profile, create);
    }

    /**
     * DOCUMENT ME!
     *
     * @param project DOCUMENT ME!
     * @param profile DOCUMENT ME!
     * @param create DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    protected static ProjectProfile getProfile(Element project, String profile,
        boolean create) {
        Element profiles = project.getChild("PROFILES");
        if ((profiles == null)) {
            if (create) {
                profiles = new Element("PROFILES");
                project.addContent(profiles);
            } else {
                return null;
            }
        }
        Element profEle =
            XMLFunction.getChildByAttribute(profiles, "PROFILE", "ID", profile);
        if ((profEle == null) && create) {
            profEle = new Element("PROFILE");
            profEle.setAttribute("ID", profile);
            profiles.addContent(profEle);
        }
        return new ProjectProfile(profEle);
    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the parent.
     */
    public AbstractProject getParent() {
        return parent;
    }

    /**
     * DOCUMENT ME!
     *
     * @param parent The parent to set.
     */
    public void setParent(AbstractProject parent) {
        this.parent = parent;
    }
    
    // LEGADO - Pode ser removido - compatibilidade anteriores a 5.1.4 (Diario Prefeitura)
    public void setRequestScope(boolean value) {
        String msg = (value == true) ? "ON" : "";
        XMLFunction.setElemValue(project, "DEFINITION", "REQUESTSCOPE", msg);
    }
    
}
