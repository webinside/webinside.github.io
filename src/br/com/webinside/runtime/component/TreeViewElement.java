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
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;

import br.com.webinside.runtime.util.WIMap;
import br.com.webinside.runtime.xml.ErrorCode;
import br.com.webinside.runtime.xml.XMLFunction;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public class TreeViewElement extends AbstractActionElement {

	private static final long serialVersionUID = 1L;

    /** DOCUMENT ME! */
    protected Element treeview;

    /**
     * Creates a new TreeViewElement object.
     */
    public TreeViewElement() {
        treeview = new Element("TREEVIEW");
        treeview.setAttribute("SEQ", "");
    }

    /**
     * Creates a new TreeViewElement object.
     *
     * @param element DOCUMENT ME!
     */
    public TreeViewElement(Element element) {
        if ((element == null) || (!element.getName().equals("TREEVIEW"))) {
            element = new Element("TREEVIEW");
        }
        if (element.getAttribute("SEQ") == null) {
            element.setAttribute("SEQ", "");
        }
        this.treeview = element;
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
        element.addContent(treeview);
        return ErrorCode.NOERROR;
    }

    /**
     * DOCUMENT ME!
     *
     * @param seq DOCUMENT ME!
     */
    protected void setSeq(String seq) {
        treeview.getAttribute("SEQ").setValue(seq);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSeq() {
        return treeview.getAttribute("SEQ").getValue();
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setCondition(String value) {
        XMLFunction.setElemValue(treeview, "CONDITION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getCondition() {
        return XMLFunction.getElemValue(treeview, "CONDITION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setDescription(String value) {
        XMLFunction.setElemValue(treeview, "DESCRIPTION", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDescription() {
        return XMLFunction.getElemValue(treeview, "DESCRIPTION");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setStyle(String value) {
        XMLFunction.setElemValue(treeview, "STYLE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getStyle() {
        String ret = XMLFunction.getElemValue(treeview, "STYLE");
        if (ret == null) {
            ret = "win";
        }
        if (ret.equals("")) {
            ret = "win";
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setTitle(String value) {
        XMLFunction.setElemValue(treeview, "TITLE", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getTitle() {
        return XMLFunction.getElemValue(treeview, "TITLE");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setWIObj(String value) {
        XMLFunction.setElemValue(treeview, "WIOBJ", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getWIObj() {
        return XMLFunction.getElemValue(treeview, "WIOBJ");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setDatabase(String value) {
        Element db = getDB();
        db.removeAttribute("ID");
        db.setAttribute("ID", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDatabase() {
        Element db = treeview.getChild("DATABASE");
        if (db == null) {
            return "";
        }
        return db.getAttributeValue("ID");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setSql(String value) {
        Element db = getDB();
        XMLFunction.setElemValue(db, "SQL", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSql() {
        Element db = getDB();
        if (db == null) {
            return "";
        }
        return XMLFunction.getElemValue(db, "SQL");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setSqlFilter(String value) {
        Element db = getDB();
        XMLFunction.setElemValue(db, "SQLFILTER", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSqlFilter() {
        Element db = getDB();
        if (db == null) {
            return "";
        }
        return XMLFunction.getElemValue(db, "SQLFILTER");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setColumns(String value) {
        Element db = getDB();
        XMLFunction.setElemValue(db, "COLUMNS", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getColumns() {
        Element db = getDB();
        if (db == null) {
            return "";
        }
        return XMLFunction.getElemValue(db, "COLUMNS");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setLink(String value) {
        Element db = getDB();
        XMLFunction.setElemValue(db, "LINK", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getLink() {
        Element db = getDB();
        if (db == null) {
            return "";
        }
        return XMLFunction.getElemValue(db, "LINK");
    }

    /**
     * DOCUMENT ME!
     *
     * @param value DOCUMENT ME!
     */
    public void setLabel(String value) {
        Element db = getDB();
        XMLFunction.setElemValue(db, "LABEL", value);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getLabel() {
        Element db = getDB();
        if (db == null) {
            return "";
        }
        return XMLFunction.getElemValue(db, "LABEL");
    }

    /**
     * DOCUMENT ME!
     *
     * @param key DOCUMENT ME!
     * @param value DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int addContent(String key, String value) {
        if (treeview.getChild("CONTENTS") == null) {
            treeview.addContent(new Element("CONTENTS"));
        }
        if (key == null) {
            return ErrorCode.NULL;
        }
        if (key.equals("")) {
            return ErrorCode.EMPTY;
        }
        if (value == null) {
            value = "";
        }
        Element parentEle = treeview.getChild("CONTENTS");
        Element ele =
            XMLFunction.getChildByAttribute(parentEle, "CONTENT", "KEY", key);
        if (ele != null) {
            parentEle.removeContent(ele);
        }
        ele = new Element("CONTENT");
        ele.setAttribute("KEY", key);
        if (!value.equals("")) {
            ele.setAttribute("LINK", value);
        }
        parentEle.addContent(ele);
        return ErrorCode.NOERROR;
    }

    /**
     * DOCUMENT ME!
     *
     * @param key DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int removeContent(String key) {
        if (treeview.getChild("CONTENTS") == null) {
            treeview.addContent(new Element("CONTENTS"));
        }
        if (key == null) {
            return ErrorCode.NULL;
        }
        if (key.equals("")) {
            return ErrorCode.EMPTY;
        }
        Element parentEle = treeview.getChild("CONTENTS");
        Element ele =
            XMLFunction.getChildByAttribute(parentEle, "CONTENT", "KEY", key);
        if (ele != null) {
            parentEle.removeContent(ele);
        }
        return ErrorCode.NOERROR;
    }

    /**
     * DOCUMENT ME!
     */
    public void removeAllContents() {
        treeview.removeChild("CONTENTS");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public WIMap getContents() {
        WIMap content = new WIMap('~', true);
        if (treeview.getChild("CONTENTS") == null) {
            return content;
        }
        Element ele = treeview.getChild("CONTENTS");
        Iterator it = ele.getChildren().iterator();
        while (it.hasNext()) {
            Element cont = (Element) it.next();
            String key = cont.getAttributeValue("KEY");
            String link = cont.getAttributeValue("LINK");
            if (link == null) {
                link = "";
            }
            content.put(key, link);
        }
        return content;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public List getKeys() {
        List vCont = new ArrayList();
        if (treeview.getChild("CONTENTS") == null) {
            return vCont;
        }
        Element ele = treeview.getChild("CONTENTS");
        Iterator it = ele.getChildren().iterator();
        while (it.hasNext()) {
            Element cont = (Element) it.next();
            String key = cont.getAttributeValue("KEY");
            vCont.add(key);
        }
        return vCont;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AbstractActionElement cloneMe() {
        TreeViewElement obj = new TreeViewElement((Element) treeview.clone());
        return obj;
    }

    private Element getDB() {
        Element db = treeview.getChild("DATABASE");
        if (db == null) {
            treeview.addContent(new Element("DATABASE"));
            db = treeview.getChild("DATABASE");
        }
        return db;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String toString() {
        return XMLFunction.elementToString(treeview);
    }

    public String toJSP() {
        StringBuffer ret = new StringBuffer(CompFunction.toJSP(this, false));
        String cName = getClass().getSimpleName();
        String varName = cName.toLowerCase() + getSeq();
        List keys = getKeys();
        WIMap contents = getContents();
        for (int i = 0; i < keys.size(); i++) {
            String key = CompFunction.filterTagAttribute((String) keys.get(i));
            String value = CompFunction.filterTagAttribute(contents.get(key));
            ret.append("<w:setPropertyByMethod\n");
            ret.append("  name=\"" + varName + "\"");
            ret.append(" method=\"addContent\"");
            ret.append(" arg1=\"" + key + "\" arg2=\"" + value + "\"\n/>");
        }
        ret.append(CompFunction.jspCore(cName, varName));
        return ret.toString();
    }
}
