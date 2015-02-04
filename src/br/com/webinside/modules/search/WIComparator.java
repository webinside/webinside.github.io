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

package br.com.webinside.modules.search;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Hashtable;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class WIComparator implements Comparator, Serializable {

	private static final long serialVersionUID = 1L;
    private String key;
    private boolean reverse;
    private boolean caseSensitive;

    /**
     * Creates a new WIComparator object.
     *
     * @param key DOCUMENT ME!
     */
    public WIComparator(String key) {
        this.key = key;
        reverse = false;
        caseSensitive = false;
    }

    /**
     * Creates a new WIComparator object.
     *
     * @param key DOCUMENT ME!
     * @param reverse DOCUMENT ME!
     */
    public WIComparator(String key, boolean reverse) {
        this(key);
        this.reverse = reverse;
    }

    /**
     * Creates a new WIComparator object.
     *
     * @param key DOCUMENT ME!
     * @param reverse DOCUMENT ME!
     * @param caseSensitive DOCUMENT ME!
     */
    public WIComparator(String key, boolean reverse, boolean caseSensitive) {
        this(key, reverse);
        this.caseSensitive = caseSensitive;
    }

    /**
     * DOCUMENT ME!
     *
     * @param o1 DOCUMENT ME!
     * @param o2 DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int compare(Object o1, Object o2) {
        int result = 0;

        Hashtable ht1 = (Hashtable) o1;
        Hashtable ht2 = (Hashtable) o2;

        String str1 = ht1.get(key).toString();
        String str2 = ht2.get(key).toString();

        if ((str1 != null) && (str2 != null)) {
            if (!caseSensitive) {
                str1 = str1.toLowerCase();
                str2 = str2.toLowerCase();
            }
            result = (reverse) ? str2.compareTo(str1)
                               : str1.compareTo(str2);
        }

        return result;
    }

    /**
     * DOCUMENT ME!
     *
     * @param obj DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean equals(Object obj) {
        return obj.equals(this);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isCaseSensitive() {
        return caseSensitive;
    }

    /**
     * DOCUMENT ME!
     *
     * @param caseSensitive DOCUMENT ME!
     */
    public void setCaseSensitive(boolean caseSensitive) {
        this.caseSensitive = caseSensitive;
    }

    /**
     * DOCUMENT ME!
     *
     * @param key DOCUMENT ME!
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String getKey() {
        return key;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean isReverse() {
        return reverse;
    }

    /**
     * DOCUMENT ME!
     *
     * @param reverse DOCUMENT ME!
     */
    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }
}
