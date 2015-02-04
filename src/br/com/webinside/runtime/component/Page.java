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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;

import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.xml.XMLFunction;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class Page implements Comparable, ProjectElement, Serializable {

	private static final long serialVersionUID = 1L;
	/** DOCUMENT ME! */
    public static final String DIRECTORY = "pages";
    /** DOCUMENT ME! */
    protected Element page;
    /** DOCUMENT ME! */
    protected AbstractProject project;

    /**
     * Creates a new Page object.
     *
     * @param id DOCUMENT ME!
     */
    public Page(String id) {
        page = new Element("PAGE");
        if (id.startsWith("/")) {
        	id = StringA.mid(id, 1, id.length());
        }
        page.setAttribute("ID", id);
    }

    /**
     * Creates a new Page object.
     *
     * @param id DOCUMENT ME!
     * @param element DOCUMENT ME!
     */
    public Page(String id, Element element) {
        if ((element == null) || (!element.getName().equals("PAGE"))) {
            element = new Element("PAGE");
        }
        if (element.getAttribute("ID") == null) {
            element.setAttribute("ID", id);
        }
        this.page = element;
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
        if (obj instanceof Page) {
            String id = ((Page) obj).getId();
            if (id.equalsIgnoreCase(getId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @param project DOCUMENT ME!
     */
    public void setProject(AbstractProject project) {
        this.project = project;
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
     * @return DOCUMENT ME!
     */
    public Element getElement() {
        return page;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getId() {
        return page.getAttributeValue("ID");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AbstractPageAction getPrePage() {
        return new PrePageAction(this, page.getChild("PREPAGE"));
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AbstractPageAction getPosPage() {
        return new PosPageAction(this, page.getChild("POSPAGE"));
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setTitle(String value) {
        XMLFunction.setElemValue(page, "DEFINITION", "TITLE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getTitle() {
        return XMLFunction.getElemValue(page, "DEFINITION", "TITLE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setMime(String value) {
        XMLFunction.setElemValue(page, "DEFINITION", "MIME", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setRoles(String value) {
        XMLFunction.setElemValue(page, "DEFINITION", "ROLES", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getRoles() {
        return XMLFunction.getElemValue(page, "DEFINITION", "ROLES");
    }
    
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getMime() {
        return XMLFunction.getElemValue(page, "DEFINITION", "MIME");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setSecurity(String value) {
        XMLFunction.setElemValue(page, "DEFINITION", "SECURITY", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSecurity() {
        return XMLFunction.getElemValue(page, "DEFINITION", "SECURITY");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setNoLogin(String value) {
        XMLFunction.setElemValue(page, "DEFINITION", "NOLOGIN", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getNoLogin() {
        return XMLFunction.getElemValue(page, "DEFINITION", "NOLOGIN");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setBrowserCache(String value) {
        XMLFunction.setElemValue(page, "DEFINITION", "BROWSERCACHE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getBrowserCache() {
        return XMLFunction.getElemValue(page, "DEFINITION", "BROWSERCACHE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setRerender(String value) {
        XMLFunction.setElemValue(page, "DEFINITION", "RERENDER", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getRerender() {
        return XMLFunction.getElemValue(page, "DEFINITION", "RERENDER");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setSysPage(String value) {
        XMLFunction.setElemValue(page, "DEFINITION", "SYSPAGE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSysPage() {
        return XMLFunction.getElemValue(page, "DEFINITION", "SYSPAGE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param pageName DOCUMENT ME!
     */
    public void setErrorPageName(String pageName) {
        XMLFunction.setElemValue(page, "DEFINITION", "ERRORPAGE", pageName);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getErrorPageName() {
        return XMLFunction.getElemValue(page, "DEFINITION", "ERRORPAGE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setErrorPage(boolean value) {
        XMLFunction.setElemValue(page, "DEFINITION", "ISERRORPAGE",
            value ? "ON"
                  : "");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isErrorPage() {
        return XMLFunction.getElemValue(page, "DEFINITION", "ISERRORPAGE")
                .equals("ON");
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setSyncPre(boolean value) {
        XMLFunction.setElemValue(page, "DEFINITION", "SYNCPRE",
            value ? "ON"
                  : "");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isSyncPre() {
        return XMLFunction.getElemValue(page, "DEFINITION", "SYNCPRE").equals("ON");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setSyncPos(boolean value) {
        XMLFunction.setElemValue(page, "DEFINITION", "SYNCPOS",
            value ? "ON"
                  : "");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isSyncPos() {
        return XMLFunction.getElemValue(page, "DEFINITION", "SYNCPOS").equals("ON");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setCheckPosToken(boolean value) {
        XMLFunction.setElemValue(page, "DEFINITION", "CHECKPOSTOKEN",
            value ? "ON"
                  : "");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isCheckPosToken() {
        return XMLFunction.getElemValue(page, "DEFINITION", "CHECKPOSTOKEN").equals("ON");
    }
    
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isUsePersistOnWizard() {
        return XMLFunction.getElemValue(page, "DEFINITION", "USEPERSISTONWIZARD").equals("ON");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setUsePersistOnWizard(boolean value) {
        XMLFunction.setElemValue(page, "DEFINITION", "USEPERSISTONWIZARD",
            value ? "ON"
                  : "");
    }

    /**
     * DOCUMENT ME!
     *
     * @param id DOCUMENT ME!
     * @param className DOCUMENT ME!
     */
    public void addValidation(String id, String value) {
        if ((id == null) || id.equals("") || (value == null)) {
            return;
        }
        Element ele = page.getChild("VALIDATIONS");
        if (ele == null) {
            ele = new Element("VALIDATIONS");
            page.addContent(ele);
        }
        Element child =
            XMLFunction.getChildByAttribute(ele, "VALIDATION", "ID", id);
        if (child == null) {
            child = new Element("VALIDATION");
            child.setAttribute("ID", id.toLowerCase());
            ele.addContent(child);
        }
        child.setAttribute("VALUE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @param id DOCUMENT ME!
     */
    public void removeValidation(String id) {
        Element ele = page.getChild("VALIDATIONS");
        if ((id == null) || (ele == null)) {
            return;
        }
        Element child =
            XMLFunction.getChildByAttribute(ele, "VALIDATION", "ID", id);
        if (child != null) {
            ele.removeContent(child);
        }
        if (ele.getChildren().size() == 0) {
            page.removeChild("VALIDATIONS");
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param id DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getValidation(String id) {
        Element ele = page.getChild("VALIDATIONS");
        if ((id == null) || (ele == null)) {
            return "";
        }
        Element child =
            XMLFunction.getChildByAttribute(ele, "VALIDATION", "ID", id.toLowerCase());
        if (child != null) {
            String resp = child.getAttributeValue("VALIDATION");
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
     * @return DOCUMENT ME!
     */
    public Map getValidationsMap() {
        Map resp = new LinkedHashMap();
        Element ele = page.getChild("VALIDATIONS");
        if (ele == null) {
            return resp;
        }
        List list = ele.getChildren("VALIDATION");
        if (list == null) {
            return resp;
        }
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Element val = (Element) it.next();
            String id = val.getAttributeValue("ID").toLowerCase();
            resp.put(id, val.getAttributeValue("VALUE"));
        }
        return resp;
    }

    public int moveValidation(int index, boolean forward) {
    	List list = null;
        Element ele = page.getChild("VALIDATIONS");
        if (ele != null) {
            list = ele.getChildren("VALIDATION");
        }
        if (list != null) {
        	if (forward) {
        		if (index < list.size() - 1) {
        			list.add(index + 1, list.remove(index));
        			return index + 1;
        		}
        	} else if (index > 0) {
        		list.add(index - 1, list.remove(index));
        		return index - 1;
        	}
        }
        return index;
    }
    
    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setPreValidationCondition(String value) {
        XMLFunction.setElemValue(page, "DEFINITION", "PREVALIDATIONCONDITION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPreValidationCondition() {
        return XMLFunction.getElemValue(page, "DEFINITION", "PREVALIDATIONCONDITION");
    }
    
    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString() {
        return "<PAGE ID=\"" + getId() + "\">";
    }

    /**
     * DOCUMENT ME!
     *
     * @param obj DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int compareTo(Object obj) {
        if (!(obj instanceof Page)) {
            return 0;
        }
        return this.getId().compareTo(((Page) obj).getId());
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Element getPage() {
        return page;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toJSP() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public Document getTemplate() {
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getType() {
        return "";
    }
}
