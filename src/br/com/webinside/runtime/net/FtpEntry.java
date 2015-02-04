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

package br.com.webinside.runtime.net;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class FtpEntry {
    private String entryStr;
    private List entryList;

    /**
     * Creates a new FtpEntry object.
     */
    public FtpEntry() {
        entryList = new ArrayList();
        setEntry("");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getEntry() {
        return entryStr;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isFile() {
        char let = ' ';
        String aux = entryStr;
        if (aux == null) {
            aux = "";
        }
        if (aux.length() > 0) {
            let = aux.toLowerCase().charAt(0);
        }
        if (let == '-') {
            return true;
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isDirectory() {
        char let = ' ';
        String aux = entryStr;
        if (aux.length() > 0) {
            let = aux.toLowerCase().charAt(0);
        }
        if (let == 'd') {
            return true;
        }
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getPermission() {
        String aux = getToken(1);
        return StringA.mid(aux, 1, aux.length() - 1);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getOwn() {
        return getToken(3);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getGroup() {
        return getToken(4);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getSize() {
        return getToken(5);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getMonth() {
        return getToken(6);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDay() {
        String day = getToken(7);
        if (day.length() == 1) {
            day = "0" + day;
        }
        return day;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getYear() {
        String aux = getToken(8);
        if (aux.indexOf(":") == -1) {
            return aux;
        }
        return Function.getDate("yyyy");
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getHour() {
        String aux = getToken(8);
        if (aux.indexOf(":") > -1) {
            return aux;
        }
        return "";
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getDate() {
        return getMonth() + " " + getDay() + " " + getYear();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getName() {
        return getToken(9);
    }

    /**
     * DOCUMENT ME!
     *
     * @param entry DOCUMENT ME!
     */
    protected void setEntry(String entry) {
        if (entry == null) {
            return;
        }
        entryStr = entry;
        StringTokenizer entryTk = new StringTokenizer(entry);
        while (entryTk.hasMoreTokens()) {
            String aux = entryTk.nextToken();
            entryList.add(aux);
        }
    }

    private String getToken(int index) {
        if (index < 1) {
            return "";
        }
        if (entryList.size() < index) {
            return "";
        }
        return (String) entryList.get(index - 1);
    }
}
