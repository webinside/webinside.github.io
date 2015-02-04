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

package br.com.webinside.runtime.xml;

import java.io.File;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Document;
import org.jdom.Element;

import br.com.webinside.runtime.util.StringA;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class XMLFunction {

    private XMLFunction() {}
	
	/**
     * DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     * @param child DOCUMENT ME!
     * @param attr DOCUMENT ME!
     * @param value DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Element getChildByAttribute(Element parent, String child,
        String attr, String value) {
        if ((parent == null) || (child == null) || (attr == null)) {
            return null;
        }
        if (value == null) {
            value = "";
        }
        List list = parent.getChildren(child);
        Element ele = null;
        Iterator i = list.iterator();
        while (i.hasNext()) {
            try {
                ele = (Element) i.next();
            } catch (ClassCastException err) {
            }
            if (ele != null) {
                Attribute childattr = ele.getAttribute(attr);
                if (childattr == null) {
                    return null;
                }
                if (childattr.getValue().equalsIgnoreCase(value)) {
                    return ele;
                }
            }
        }
        return null;
    }

    /**
     * DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     * @param name DOCUMENT ME!
     * @param sensitive DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static List getElemsByName(Element parent, String name,
        boolean sensitive) {
        List response = new ArrayList();
        if ((parent == null) || (name == null)) {
            return response;
        }
        if (!sensitive) {
            name = name.toUpperCase();
        }
        List lst = parent.getContent();
        Iterator it = lst.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof Element) {
                Element ele = (Element) obj;
                String elename = ele.getName();
                if (!sensitive) {
                    elename = elename.toUpperCase();
                }
                if (elename.equals(name)) {
                    response.add(ele);
                }
                List aux = getElemsByName(ele, name, sensitive);
                for (int i = 0; i < aux.size(); i++) {
                    response.add(aux.get(i));
                }
            }
        }
        return response;
    }

    /**
     * DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     * @param attr DOCUMENT ME!
     * @param value DOCUMENT ME!
     * @param sensitive DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static List getElemsByAttr(Element parent, String attr,
        String value, boolean sensitive) {
        List response = new ArrayList();
        if ((parent == null) || (attr == null)) {
            return response;
        }
        if (value == null) {
            value = "";
        }
        value = value.toUpperCase();
        if (!sensitive) {
            attr = attr.toUpperCase();
        }
        List lst = parent.getContent();
        Iterator it = lst.iterator();
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof Element) {
                Element ele = (Element) obj;
                boolean ok = false;
                List attlst = ele.getAttributes();
                Iterator attit = attlst.iterator();
                while ((attit.hasNext()) && (!ok)) {
                    Attribute attaux = (Attribute) attit.next();
                    String attname = attaux.getName();
                    String attvalue = attaux.getValue().toUpperCase();
                    if (!sensitive) {
                        attname = attname.toUpperCase();
                    }
                    if ((attr.equals(attname)) && (value.equals(attvalue))) {
                        ok = true;
                    }
                }
                if (ok) {
                    response.add(ele);
                }
                List aux = getElemsByAttr(ele, attr, value, sensitive);
                for (int i = 0; i < aux.size(); i++) {
                    response.add(aux.get(i));
                }
            }
        }
        return response;
    }

    /**
     * DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     * @param element DOCUMENT ME!
     * @param value DOCUMENT ME!
     */
    public static void setElemValue(Element parent, String element, String value) {
        if (parent == null) {
            return;
        }
        if (value == null) {
            value = "";
        }
        Element ele = parent.getChild(element);
        if (ele == null) {
            if (!value.equals("")) {
                ele = new Element(element);
                parent.addContent(ele);
            }
        } else {
            if (value.equals("")) {
                parent.removeChild(element);
                return;
            }
        }
        if (ele != null) {
            ele.setText(value);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     * @param element DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String getElemValue(Element parent, String element) {
        if (parent == null) {
            return "";
        }
        Element ele = parent.getChild(element);
        if (ele == null) {
            return "";
        }
        return ele.getText();
    }

    /**
     * DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     * @param child DOCUMENT ME!
     * @param element DOCUMENT ME!
     * @param value DOCUMENT ME!
     */
    public static void setElemValue(Element parent, String child,
        String element, String value) {
        if (value == null) {
            value = "";
        }
        Element def = parent.getChild(child);
        if (def == null) {
            parent.addContent(new Element(child));
            def = parent.getChild(child);
        }
        Element ele = def.getChild(element);
        if (ele == null) {
            if (!value.equals("")) {
                ele = new Element(element);
                def.addContent(ele);
            } else {
                if ((def.getChildren().size() == 0)
                            && (def.getAttributes().size() == 0)) {
                    parent.removeChild(child);
                }
                return;
            }
        } else {
            if (value.equals("")) {
                def.removeChild(element);
                if (!def.getChildren().iterator().hasNext()) {
                    parent.removeChild(child);
                }
                return;
            }
        }
        if (ele != null) {
            ele.setText(value);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     * @param child DOCUMENT ME!
     * @param element DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String getElemValue(Element parent, String child,
        String element) {
        if ((parent == null) || (parent.getChild(child) == null)) {
            return "";
        }
        Element def = parent.getChild(child);
        if (def.getChild(element) == null) {
            return "";
        }
        return def.getChild(element).getText();
    }

    /**
     * DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     * @param element DOCUMENT ME!
     * @param attrib DOCUMENT ME!
     * @param value DOCUMENT ME!
     */
    public static void setAttrValue(Element parent, String element,
        String attrib, String value) {
        if ((attrib == null) || (attrib.equals("") || (parent == null))) {
            return;
        }
        if (value == null) {
            value = "";
        }
        Element ele = parent.getChild(element);
        if (ele == null) {
            if (!value.equals("")) {
                ele = new Element(element);
                parent.addContent(ele);
                ele = parent.getChild(element);
                ele.setAttribute(attrib, value);
            }
        } else {
            if (value.equals("")) {
                ele.removeAttribute(attrib);
                int cnt = 0;
                if (ele.getAttributes().size() == 0) {
                    cnt++;
                }
                if ("".equals(ele.getText())) {
                    cnt++;
                }
                if (!ele.getChildren().iterator().hasNext()) {
                    cnt++;
                }
                if (cnt == 3) {
                    parent.removeChild(element);
                }
                return;
            }
        }
        if (ele != null) {
            Attribute atr = ele.getAttribute(attrib);
            if (atr != null) {
                ele.removeAttribute(attrib);
            }
            ele.setAttribute(attrib, value);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param element DOCUMENT ME!
     * @param attrib DOCUMENT ME!
     * @param value DOCUMENT ME!
     */
    public static void setAttrValue(Element element, String attrib, String value) {
        if ((element == null) || (attrib == null) || (attrib.equals(""))) {
            return;
        }
        if (value == null) {
            value = "";
        }
        if (value.equals("")) {
            element.removeAttribute(attrib);
        } else {
            Attribute atr = element.getAttribute(attrib);
            if (atr != null) {
                element.removeAttribute(attrib);
            }
            element.setAttribute(attrib, value);
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     * @param element DOCUMENT ME!
     * @param attrib DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String getAttrValue(Element parent, String element,
        String attrib) {
        if (parent == null) {
            return "";
        }
        Element ele = parent.getChild(element);
        if (ele == null) {
            return "";
        }
        Attribute attr = ele.getAttribute(attrib);
        if (attr == null) {
            return "";
        }
        return attr.getValue();
    }

    /**
     * DOCUMENT ME!
     *
     * @param element DOCUMENT ME!
     * @param attrib DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String getAttrValue(Element element, String attrib) {
        if (element == null) {
            return "";
        }
        Attribute attr = element.getAttribute(attrib);
        if (attr == null) {
            return "";
        }
        String ret = attr.getValue();
        if (ret == null) {
            ret = "";
        }
        return ret;
    }

    /**
     * DOCUMENT ME!
     *
     * @param element DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String elementToString(Element element) {
        StringWriter sw = new StringWriter();
        Outputter.output(element, sw);
        return sw.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param dir DOCUMENT ME!
     * @param rootType DOCUMENT ME!
     * @param subType DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static Set getFileIDs(File dir, String rootType, String subType) {
        if ((rootType == null) || (dir == null) || !dir.isDirectory()) {
            return null;
        }
        if (subType == null) {
            subType = "";
        }
        Set set = new TreeSet();
        getFileIDs(dir, rootType, subType, set, "");
        return set;
    }

    private static void getFileIDs(File dir, String rootType, String subType,
        Set set, String prefix) {
        File[] fls = dir.listFiles();
        if (fls == null) {
            fls = new File[0];
        }
        for (int i = 0; i < fls.length; i++) {
            File fl = fls[i];
            if (fl.isDirectory()) {
                String newPrefix = fl.getName();
                if (!prefix.equals("")) {
                    newPrefix = prefix + "/" + newPrefix;
                }
                getFileIDs(fl, rootType, subType, set, newPrefix);
            } else {
                if (!fl.getName().endsWith(".xml")) {
                    continue;
                }
                Inputter inp = new Inputter();
                Document doc = inp.input(fl);
                if (doc != null) {
                    Element root = doc.getRootElement();
                    if (root.getName().equals(rootType)) {
                        if (!subType.equals("")) {
                            if (root.getAttribute("TYPE") != null) {
                                String stype = root.getAttributeValue("TYPE");
                                if (!subType.equals(stype)) {
                                    continue;
                                }
                            }
                        }
                        String name = StringA.piece(fl.getName(), ".xml", 1);
                        if (!prefix.equals("")) {
                            name = prefix + "/" + name;
                        }
                        set.add(name);
                    }
                }
            }
        }
    }

    /**
     * DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     * @param child DOCUMENT ME!
     * @param element DOCUMENT ME!
     * @param value DOCUMENT ME!
     */
    public static void setCDATAValue(Element parent, String child,
        String element, String value) {
        if ((parent == null) || (child == null)) {
            return;
        }
        Element newParent = parent.getChild(child);
        if (newParent == null) {
            newParent = new Element(child);
            parent.addContent(newParent);
        }
        setCDATAValue(newParent, element, value);
    }

    /**
     * DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     * @param child DOCUMENT ME!
     * @param value DOCUMENT ME!
     */
    public static void setCDATAValue(Element parent, String child, String value) {
        if ((parent == null) || (child == null)) {
            return;
        }
        if (value == null) {
            value = "";
        }
        if (value == null) {
            value = "";
        }

        List attributes = null;
        if (parent.getChild(child) != null) {
            attributes = parent.getChild(child).getAttributes();
        }
        parent.removeChild(child);
        Element ele = new Element(child);
        ele.addContent(new CDATA(value));
        for (int i = 0; (attributes != null) && (i < attributes.size()); i++) {
            Attribute att = (Attribute) attributes.get(i);
            ele.setAttribute(att.getName(), att.getValue());
        }
        parent.addContent(ele);
    }

    /**
     * DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     * @param child DOCUMENT ME!
     * @param element DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String getCDATAValue(Element parent, String child,
        String element) {
        if ((parent == null) || (child == null)) {
            return "";
        }
        return XMLFunction.getCDATAValue(parent.getChild(child), element);
    }

    /**
     * DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     * @param child DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String getCDATAValue(Element parent, String child) {
        if ((parent == null) || (child == null)) {
            return "";
        }
        String resp = parent.getChildText(child);
        if (resp == null) {
            resp = "";
        }
        return resp;
    }

    /**
     * DOCUMENT ME!
     *
     * @param parent DOCUMENT ME!
     * @param name DOCUMENT ME!
     * @param attr DOCUMENT ME!
     * @param value DOCUMENT ME!
     */
    public static void removeRecursiveChild(Element parent, String name,
        String attr, String value) {
        if ((parent == null) || (name == null) || (attr == null)) {
            return;
        }
        if (value == null) {
            value = "";
        }
        List remove = new ArrayList();
        List lst = parent.getChildren();
        for (int i = 0; i < lst.size(); i++) {
            Object obj = lst.get(i);
            if (!(obj instanceof Element)) {
                continue;
            }
            Element ele = (Element) obj;
            if (ele.getName().equals(name)) {
                String aValue = ele.getAttributeValue(attr);
                if (value.equals(aValue)) {
                    remove.add(ele);
                }
            } else {
                removeRecursiveChild(ele, name, attr, value);
            }
        }
        for (int i = 0; i < remove.size(); i++) {
            parent.removeContent((Element) remove.get(i));
        }
    }
    
    public static void addChild(Element parent, String element, Element child) {
        if ((parent != null) && (child != null) && (element != null)) {
            Element ele = parent.getChild(element);
            if (ele == null) {
                ele = new Element(element);
                parent.addContent(ele);
            }
            ele.addContent(child);
        }
    }
}
