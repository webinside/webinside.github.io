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

import org.jdom.*;
import java.util.*;

/**
 * Classe que ordena os elementos Xml baseado num template.
 *
 * @author Geraldo Moraes
 * @version 1.0
 */
public class Sorter {
    private static Set orderedNodes;

    static {
        orderedNodes = Collections.synchronizedSet(new HashSet());
        orderedNodes.add("INDEX");
        orderedNodes.add("USERFIELDS");
    }

    /**
     * Ordena os elementos Xml.
     *
     * @param element o elemento pai a ser ordenado.
     * @param template o template a ser utilizado.
     *
     * @return o elemento já ordenado.
     */
    public static Element sort(Element element, Element template) {
        if ((element == null) || (template == null)) {
            return null;
        }
        return listByTemplate(element, template);
    }

    private static Element listByTemplate(Element element, Element template) {
        if ((element == null) || (template == null)) {
            return null;
        }
        if (!element.getName().equals(template.getName())) {
            return null;
        }
        Element response = new Element(element.getName());
        addAttributes(element, response);
        addSubComponents(element, response);
        List mixedContent = template.getContent();
        for (int i = 0; i < mixedContent.size(); i++) {
            Object content = mixedContent.get(i);
            if (content instanceof Element) {
                Element myele = (Element) content;
                if (orderedNodes.contains(myele.getName())) {
                    Element mychild = element.getChild(myele.getName());
                    if (mychild != null) {
                        response.addContent((Element) mychild.clone());
                    }
                } else {
                    listByElement(element, myele, response);
                }
            }
        }
        return response;
    }

    private static void listByElement(Element parent, Element template,
        Element response) {
        String tagname = template.getName();
        List lst = parent.getChildren(tagname);
        for (int i = 0; i < lst.size(); i++) {
            Element aux = (Element) lst.get(i);
            response.addContent(listByTemplate(aux, template));
        }
        parent.removeChildren(tagname);
    }

    private static void addSubComponents(Element source, Element target) {
        List lst = source.getContent();
        for (int i = 0; i < lst.size(); i++) {
            Object content = lst.get(i);
            if (content instanceof CDATA) {
                CDATA aux = new CDATA(((CDATA) content).getText());
                target.addContent(aux);
            } else if (content instanceof EntityRef) {
                EntityRef aux = new EntityRef(((EntityRef) content).getName());
                target.addContent(aux);
            } else if (content instanceof Comment) {
                Comment aux = new Comment(((Comment) content).getText());
                target.addContent(aux);
            } else if (content instanceof Text) {
                String aux = ((Text) content).getText();
                target.addContent(aux);
            }
        }
    }

    private static void addAttributes(Element source, Element target) {
        List lst = source.getAttributes();
        for (int i = 0; i < lst.size(); i++) {
            Attribute att = (Attribute) lst.get(i);
            target.setAttribute(att.getName(), att.getValue());
        }
    }
}
