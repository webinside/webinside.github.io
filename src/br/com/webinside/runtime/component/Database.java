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

import br.com.webinside.runtime.util.Encrypter;
import br.com.webinside.runtime.xml.ErrorCode;
import br.com.webinside.runtime.xml.XMLFunction;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class Database {
    /** DOCUMENT ME! */
    protected Element database;

    /**
     * Creates a new Database object.
     */
    public Database() {
        database = new Element("DATABASE");
        database.setAttribute("ID", "");
    }

    /**
     * Creates a new Database object.
     *
     * @param name DOCUMENT ME!
     */
    public Database(String name) {
        if (name == null) {
            name = "";
        }
        database = new Element("DATABASE");
        database.setAttribute("ID", name);
    }

    /**
     * Creates a new Database object.
     *
     * @param element DOCUMENT ME!
     */
    public Database(Element element) {
        if ((element == null) || (!element.getName().equals("DATABASE"))) {
            element = new Element("DATABASE");
        }
        if (element.getAttribute("ID") == null) {
            element.setAttribute("ID", "");
        }
        this.database = element;
    }

    public Element getElement() {
        return database;
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
        if (obj instanceof Database) {
            String id = ((Database) obj).getId();
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
        element.addContent(database);
        return ErrorCode.NOERROR;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getId() {
        return database.getAttribute("ID").getValue();
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
        database.getAttribute("ID").setValue(id);
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setDescription(String value) {
        XMLFunction.setElemValue(database, "DESCRIPTION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDescription() {
        return XMLFunction.getElemValue(database, "DESCRIPTION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setType(String value) {
        setType(value, "");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     * @param profile DOCUMENT ME!
     */
    public void setType(String value, String profile) {
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();
        Element aux = database;
        if (!profile.equals("")) {
            aux = getProfileDatabase(profile, true);
        }
        XMLFunction.setElemValue(aux, "TYPE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getType() {
        return getType("");
    }

    /**
     * DOCUMENT ME!
     *
     * @param profile DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getType(String profile) {
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();
        Element aux = database;
        if (!profile.equals("")) {
            aux = getProfileDatabase(profile, false);
        }
        return XMLFunction.getElemValue(aux, "TYPE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setAlias(String value) {
        setAlias(value, "");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     * @param profile DOCUMENT ME!
     */
    public void setAlias(String value, String profile) {
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();
        Element aux = database;
        if (!profile.equals("")) {
            aux = getProfileDatabase(profile, true);
        }
        XMLFunction.setElemValue(aux, "ALIAS", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getAlias() {
        return getAlias("");
    }

    /**
     * DOCUMENT ME!
     *
     * @param profile DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getAlias(String profile) {
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();
        Element aux = database;
        if (!profile.equals("")) {
            aux = getProfileDatabase(profile, false);
        }
        return XMLFunction.getElemValue(aux, "ALIAS");
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
        Element aux = database;
        if (!profile.equals("")) {
            aux = getProfileDatabase(profile, true);
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
        Element aux = database;
        if (!profile.equals("")) {
            aux = getProfileDatabase(profile, false);
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
        Element aux = database;
        if (!profile.equals("")) {
            aux = getProfileDatabase(profile, false);
        }
        String pass = XMLFunction.getAttrValue(aux, "USER", "PASS");
        Encrypter pw = new Encrypter(pass);
        return pw.decodeDES();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPassEnc() {
        return XMLFunction.getAttrValue(database, "USER", "PASS");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setConnectionTimeout(String minutes) {
    	setConnectionTimeout(minutes, "");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     * @param profile DOCUMENT ME!
     */
    public void setConnectionTimeout(String minutes, String profile) {
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();
        Element aux = database;
        if (!profile.equals("")) {
            aux = getProfileDatabase(profile, true);
        }
        XMLFunction.setElemValue(aux, "TIMEOUT", minutes + "");
    }

    /**
     * DOCUMENT ME!
     *
     * @param seconds DOCUMENT ME!
     */
    public void setQueryTimeout(String seconds) {
    	setQueryTimeout(seconds, "");
    }

    /**
     * DOCUMENT ME!
     *
     * @param seconds DOCUMENT ME!
     * @param profile DOCUMENT ME!
     */
    public void setQueryTimeout(String seconds, String profile) {
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();
        Element aux = database;
        if (!profile.equals("")) {
            aux = getProfileDatabase(profile, true);
        }
        XMLFunction.setElemValue(aux, "QUERYTIMEOUT", seconds + "");
    }

    /**
     * DOCUMENT ME!
     *
     * @param seconds DOCUMENT ME!
     */
    public void setLoginTimeout(String seconds) {
    	setLoginTimeout(seconds, "");
    }

    /**
     * DOCUMENT ME!
     *
     * @param seconds DOCUMENT ME!
     * @param profile DOCUMENT ME!
     */
    public void setLoginTimeout(String seconds, String profile) {
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();
        Element aux = database;
        if (!profile.equals("")) {
            aux = getProfileDatabase(profile, true);
        }
        XMLFunction.setElemValue(aux, "LOGINTIMEOUT", seconds + "");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setQTMessage(String value) {
    	setQTMessage(value, "");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     * @param profile DOCUMENT ME!
     */
    public void setQTMessage(String value, String profile) {
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();
        Element aux = database;
        if (!profile.equals("")) {
            aux = getProfileDatabase(profile, true);
        }
        XMLFunction.setElemValue(aux, "QTMESSAGE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setDBTimeLog(int seconds) {
    	setDBTimeLog(seconds, "");
    }    
    
    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     * @param profile DOCUMENT ME!
     */
    public void setDBTimeLog(int seconds, String profile) {
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();
        Element aux = database;
        if (!profile.equals("")) {
            aux = getProfileDatabase(profile, true);
        }
        XMLFunction.setElemValue(aux, "DBTIMELOG", seconds + "");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getConnectionTimeout() {
        return getConnectionTimeout("");
    }

    /**
     * DOCUMENT ME!
     *
     * @param profile DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getConnectionTimeout(String profile) {
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();
        Element aux = database;
        if (!profile.equals("")) {
            aux = getProfileDatabase(profile, false);
        }
        return XMLFunction.getElemValue(aux, "TIMEOUT");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getQueryTimeout() {
        return getQueryTimeout("");
    }

    /**
     * DOCUMENT ME!
     *
     * @param profile DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getQueryTimeout(String profile) {
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();
        Element aux = database;
        if (!profile.equals("")) {
            aux = getProfileDatabase(profile, false);
        }
        return XMLFunction.getElemValue(aux, "QUERYTIMEOUT");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getLoginTimeout() {
        return getLoginTimeout("");
    }

    /**
     * DOCUMENT ME!
     *
     * @param profile DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getLoginTimeout(String profile) {
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();
        Element aux = database;
        if (!profile.equals("")) {
            aux = getProfileDatabase(profile, false);
        }
        return XMLFunction.getElemValue(aux, "LOGINTIMEOUT");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getQTMessage() {
        return getQTMessage("");
    }

    /**
     * DOCUMENT ME!
     *
     * @param profile DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getQTMessage(String profile) {
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();
        Element aux = database;
        if (!profile.equals("")) {
            aux = getProfileDatabase(profile, false);
        }
        return XMLFunction.getElemValue(aux, "QTMESSAGE");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getDBTimeLog() {
        return getDBTimeLog("");
    }

    /**
     * DOCUMENT ME!
     *
     * @param profile DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int getDBTimeLog(String profile) {
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();
        Element aux = database;
        if (!profile.equals("")) {
            aux = getProfileDatabase(profile, false);
        }
        String time = XMLFunction.getElemValue(aux, "DBTIMELOG");
        try {
        	return Integer.parseInt(time);
        } catch (NumberFormatException err) {
        	// desconsiderado
        }
        return 10;
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setMaxConnections(String value) {
        setMaxConnections(value, "");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     * @param profile DOCUMENT ME!
     */
    public void setMaxConnections(String value, String profile) {
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();
        Element aux = database;
        if (!profile.equals("")) {
            aux = getProfileDatabase(profile, true);
        }
        XMLFunction.setElemValue(aux, "MAXCONNECTIONS", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getMaxConnections() {
        return getMaxConnections("");
    }

    /**
     * DOCUMENT ME!
     *
     * @param profile DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getMaxConnections(String profile) {
        if (profile == null) {
            profile = "";
        }
        profile = profile.trim();
        Element aux = database;
        if (!profile.equals("")) {
            aux = getProfileDatabase(profile, false);
        }
        return XMLFunction.getElemValue(aux, "MAXCONNECTIONS");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Database cloneMe() {
        Database obj = new Database((Element) database.clone());
        return obj;
    }

    private Element getProfileDatabase(String profile, boolean create) {
        Element project = (Element) database.getParent();
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
        Element databases = profEle.getChild("DATABASES");
        if (databases == null) {
            if (create) {
                databases = new Element("DATABASES");
                profEle.addContent(databases);
            } else {
                return null;
            }
        }
        Element db =
            XMLFunction.getChildByAttribute(databases, "DATABASE", "ID", getId());
        if ((db == null) && create) {
            db = new Element("DATABASE");
            db.setAttribute("ID", getId());
            databases.addContent(db);
        }
        return db;
    }

    /**
     * DOCUMENT ME!
     *
     * @param profile DOCUMENT ME!
     */
    public void cloneInProfile(String profile) {
        Element ele = getProfileDatabase(profile, true);
        Database aux = new Database(ele);
        aux.setType(getType());
        aux.setAlias(getAlias());
        aux.setUser(getUser(), getPass());
        aux.setConnectionTimeout(getConnectionTimeout());
        aux.setMaxConnections(getMaxConnections());
        aux.setDBTimeLog(getDBTimeLog());
        aux.setQueryTimeout(getQueryTimeout());
        aux.setQTMessage(getQTMessage());
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString() {
        return XMLFunction.elementToString(database);
    }
}
