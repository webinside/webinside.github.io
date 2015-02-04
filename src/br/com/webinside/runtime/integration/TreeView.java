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

package br.com.webinside.runtime.integration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.StringComparator;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class TreeView {
    /** DOCUMENT ME! */
    private static final String KEYLIST =
        "0!#$&(),-./123456789;<=>@ABCDEFGHIJKLMNOPQRSTUVWXYZ[]"
        + "^_`abcdefghijklmnopqrstuvwxyz{}~¡¢£¤¥§¨©ª«¬­®¯°±²³´µ¶·¸¹º»"
        + "¼½¾¿ÀÁÂÃÄÅÆÇÈÉÊËÌÍÎÏÐÑÒÓÔÕÖ×ØÙÚÛÜÝÞßàáâãäåæçèéêëìíîïðñòóôõö÷øùúûüýþÿŒœŠšŸŽžƒˆ˜";

    /**
     * DOCUMENT ME!
     *
     * @param data DOCUMENT ME!
     * @param title DOCUMENT ME!
     * @param keys DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String get(WIMap data, String title, List keys) {
        StringA ret = new StringA();
        ret.append("initTree(\"");
        ret.append(title);
        ret.append("\",\"*\",\"\");\r\n");
        ret.append(getKey(keys, data, ""));
        ret.append("end_Tree();\r\n");
        return ret.toString();
    }

    /**
     * DOCUMENT ME!
     *
     * @param data DOCUMENT ME!
     * @param title DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public static String get(WIMap data, String title) {
        StringA ret = new StringA();
        ret.append("initTree(\"");
        ret.append(title);
        ret.append("\",\"*\",\"\");\r\n");
        ret.append(getKey(data, ""));
        ret.append("end_Tree();\r\n");
        return ret.toString();
    }

    private static String getKey(List keys, WIMap data, String tvKey) {
        StringA ret = new StringA();
        int count = 0;
        String delim = data.getDelimiter() + "";
        List newKeys = new ArrayList();
        for (int i = 0; i < keys.size(); i++) {
            String key = (String) keys.get(i);
            if (key.indexOf(delim) > -1) {
                continue;
            }
            String link = data.get(key);
            if (count == KEYLIST.length()) {
                count = 0;
            }
            String thisKey = tvKey + KEYLIST.charAt(count++);
            if ("%".equals(thisKey)) {
                thisKey = tvKey + KEYLIST.charAt(count++);
            }
            String keyProd = StringA.piece(link, "~", 2);
            if (keyProd.equals("")) {
                keyProd = key;
            }
            keyProd = StringA.change(keyProd, "|nodeID|", thisKey, false);
            link = StringA.piece(link, "~", 1);
            link = StringA.change(link, "|nodeID|", thisKey, false);
            boolean isBook = (data.getObj(key + delim) instanceof WIMap);
            boolean isLast = isLast(i, keys, delim);
            if (isBook) {
                if (isLast) {
                    ret.append("lastBook(\"");
                } else {
                    ret.append("sub_Book(\"");
                }
                ret.append(keyProd);
                ret.append("\",\"");
                ret.append(thisKey);
                ret.append("\",\"");
                ret.append(link);
                ret.append("\");\r\n");
                String pKey = StringA.piece(key, delim, 1) + delim;
                WIMap newData = (WIMap) data.getObj(pKey);
                newKeys.clear();
                for (int j = i + 1; j < keys.size(); j++) {
                    String tmp = (String) keys.get(j);
                    if (!tmp.startsWith(pKey)) {
                        break;
                    }
                    tmp = StringA.mid(tmp, pKey.length(), tmp.length());
                    newKeys.add(tmp);
                }
                ret.append(getKey(newKeys, newData, thisKey));
            } else {
                if (isLast) {
                    ret.append("lastPage(\"");
                } else {
                    ret.append("sub_Page(\"");
                }
                ret.append(keyProd + "\",\"" + thisKey + "\",\"");
                ret.append(link + "\");\r\n");
            }
        }
        ret.append("end_Book();\r\n");
        return ret.toString();
    }

    private static boolean isLast(int i, List keys, String delim) {
        boolean ret = false;
        if (keys.size() == 0) {
            return true;
        }
        String key = (String) keys.get(i);
        if (key.equals(keys.get(keys.size() - 1))) {
            ret = true;
        }
        if (((String) keys.get(keys.size() - 1)).startsWith(key + delim)) {
            ret = true;
        }
        return ret;
    }

    private static String getKey(WIMap data, String tvKey) {
        StringA ret = new StringA();
        int count = 0;
        StringComparator sc = new StringComparator();
        sc.setPiece(data.getDelimiter() + "", 1);
        Set keys = new TreeSet(sc);
        keys.addAll(data.getInternalMap().keySet());
        Iterator en = keys.iterator();
        while (en.hasNext()) {
            String key = (String) en.next();
            String link = data.get(key + "link");
            data.remove(key + "link");
            Object keyObj = data.getObj(key);
            if (keyObj instanceof WIMap) {
                if (((WIMap) keyObj).keySet().size() == 0) {
                    key = StringA.mid(key, 0, key.length() - 2);
                }
            }
            if (count == KEYLIST.length()) {
                count = 0;
            }
            String thisKey = tvKey + KEYLIST.charAt(count++);
            if ("%".equals(thisKey)) {
                thisKey = tvKey + KEYLIST.charAt(count++);
            }
            String keyProd = StringA.change(key, "|nodeID|", thisKey, false);
            link = StringA.change(link, "|nodeID|", thisKey, false);
            if (key.endsWith(data.getDelimiter() + "")) {
                if (en.hasNext()) {
                    ret.append("sub_Book(\"");
                } else {
                    ret.append("lastBook(\"");
                }
                ret.append(StringA.mid(keyProd, 0, key.length() - 2));
                ret.append("\",\"" + thisKey + "\",\"");
                ret.append(link);
                ret.append("\");\r\n");
                ret.append(getKey((WIMap) data.getObj(key), thisKey));
            } else {
                if (en.hasNext()) {
                    ret.append("sub_Page(\"");
                } else {
                    ret.append("lastPage(\"");
                }
                ret.append(keyProd);
                ret.append("\",\"" + thisKey + "\",\"");
                ret.append(link);
                ret.append("\");\r\n");
            }
        }
        ret.append("end_Book();\r\n");
        return ret.toString();
    }
}
