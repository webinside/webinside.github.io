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

package br.com.webinside.runtime.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;

import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;
import br.com.webinside.runtime.xml.Inputter;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class CoreXmlImport {

    private WIMap wiMap;
    private String key;

    /**
     * Creates a new CoreXmlImport object.
     *
     * @param wiMap DOCUMENT ME!
     * @param key DOCUMENT ME!
     */
    public CoreXmlImport(WIMap wiMap, String key) {
        this.wiMap = wiMap;
        if (key == null) {
            key = "";
        }
        this.key = key.trim();
    }

    /**
     * DOCUMENT ME!
     *
     * @param textXml DOCUMENT ME!
     */
    public Document execute(String textXml) {
        if (textXml == null) {
            return null;
        }
        if (key.endsWith(".")) {
            key = StringA.mid(key, 0, key.length() - 2);
        }
        Inputter in = new Inputter();
        Document doc = in.input(textXml);
        if (!in.getErrorMsg().equals("")) {
            String error = in.getErrorMsg();
            String kerror = "error()";
            if (!key.equals("")) {
                kerror = key + ".error()";
            }
            wiMap.put(kerror, error);
        }
        if (doc != null) {
            if (!key.equals("")) {
                wiMap.put(key + ".root()", doc.getRootElement().getName());
            }
            int size = doc.getRootElement().getChildren().size();
            if (!key.equals("") && (size > 0)) {
                wiMap.put(key + ".size()", size + "");
            }
			List parents = new ArrayList();
			if (!key.equals("")) {
			  parents.add(key);
			}
			getElements(doc.getRootElement(), parents);
        }
        return doc;
    }

	private void getElements(Element ele, List parents) {
	  Iterator it = ele.getChildren().iterator();
	  int cont=0;
	  while (it.hasNext()) {
		Element child = null;
		try {
		  child = (Element)it.next();
		} catch (ClassCastException err) {}
		if (child!=null) {
		  cont = cont+1;
		  List names = new ArrayList();
		  for (int i=0;i<parents.size();i++) {
			  String parent = (String)parents.get(i);
			  names.add(parent+"."+child.getName());
			  if (!parent.equals(key) && repeatedChildren(ele)) {	
				names.add(parent+"[" + cont + "]."+child.getName());
			  }
		  }
		  if (names.size()==0) {
			  names.add(child.getName());
		  }
		  attributes(child, names);
		  int qnt = child.getChildren().size();
		  if (qnt>0) {
			for (int i=0;i<names.size();i++) {
			  wiMap.put((String)names.get(i)+".size()", qnt);   		  	
			}
		  }
		  if (qnt==0) {
			String value = child.getText();	
			for (int i=0;i<names.size();i++) {
			  wiMap.put((String)names.get(i), value);   		  	
			}
		  } else {
			getElements(child, names);
		  }
		}
	  }
	}

	private boolean repeatedChildren(Element ele) {
	  Set children = new HashSet();
	  Iterator i2 = ele.getChildren().iterator();
	  while (i2.hasNext()) {
		Element child = null;
		try { 
		  child = (Element)i2.next();
		} catch (ClassCastException err) {}
		if (child!=null) {
		  children.add(child.getName());
		}
	  }
	  return (children.size()==1);
	}

	private void attributes(Element ele, List names) {
	  Iterator it = ele.getAttributes().iterator();
	  while (it.hasNext()) {
		Attribute attr = (Attribute)it.next();
		for (int i=0;i<names.size();i++) {
		  String name = names.get(i) + ".attr(" + attr.getName() + ")";
		  wiMap.put(name,attr.getValue());
		}
	  }
	}
	
}
