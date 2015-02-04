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

package br.com.webinside.runtime.util;

import java.io.Serializable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Classe que armazena a coleção de variávies do WI. Utilizando um delimitador
 * (onde o padrão é ".") será criado um Map com uma estrutura hierárquica para
 * armazenar os dados. Como exemplo, ao armazenar "tmp.nome" será armazenado
 * no Map interno um novo WIMap tendo como chave "tmp." e nele a chave "nome".
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.5 $
 */
public class WIMap implements Serializable {
	
	private static final long serialVersionUID = 1L;
    private Map map = new LinkedHashMap();
    private char delimiter = '.';
    private boolean sensitive = false;
    private Set changedKeys;

    /**
     * Cria um novo WIMap.
     */
    public WIMap() {
    }

    /**
     * Cria um novo WIMap. Utiliza o médodo putAll(Map map).
     *
     * @param map um Map a ser utilizado.
     */
    public WIMap(Map map) {
        if (map != null) {
            putAll(map);
        }
    }

    /**
     * Cria um novo WIMap.
     *
     * @param sensitive indica se as chaves serão sensitive-case.
     */
    public WIMap(boolean sensitive) {
        this.sensitive = sensitive;
    }

    /**
     * Cria um novo WIMap.
     *
     * @param delimiter o delimitador a ser utilizado.
     */
    public WIMap(char delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Cria um novo WIMap.
     *
     * @param delimiter o delimitador a ser utilizado.
     * @param sensitive indica se as chaves serão sensitive-case.
     */
    public WIMap(char delimiter, boolean sensitive) {
        this.delimiter = delimiter;
        this.sensitive = sensitive;
    }

    /**
     * Retorna um clone do WIMap.
     *
     * @return um clone do WIMap.
     */
    public WIMap cloneMe() {
        WIMap aux = new WIMap(delimiter, sensitive);
        aux.map = new LinkedHashMap(map);
        if (changedKeys != null) {
            aux.changedKeys = new LinkedHashSet();
        }
        aux.cloneChild();
        return aux;
    }

    /**
     * Retorna se está vazio.
     *
     * @return se está vazio.
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Indica se deve ser armazenada uma lista das chaves que foram alteradas.
     *
     * @param enable ativa a lista das chaves que foram alteradas.
     */
    public void setChangedKeys(boolean enable) {
        if (enable) {
            changedKeys = new LinkedHashSet();
        } else {
            changedKeys = null;
        }
    }

    /**
     * Retorna um Set das chaves que foram alteradas.
     *
     * @return um Set das chaves que foram alteradas.
     */
    public Set getChangedKeys() {
        if (changedKeys == null) {
            return new LinkedHashSet();
        }
        return new LinkedHashSet(changedKeys);
    }

    /**
     * Retorna o delimitador.
     *
     * @return o delimitador.
     */
    public char getDelimiter() {
        return delimiter;
    }

    /**
     * Retorna se as chaves são case-sensitive.
     *
     * @return se as chaves são case-sensitive.
     */
    public boolean isSensitive() {
        return sensitive;
    }

    /**
     * Armazena um valor numa chave.
     *
     * @param key a chave a ser utilizada.
     * @param value o valor a ser armazenado.
     */
    public void putObj(String key, Object value) {
        key = spcClean(key);
        if (key.equals("") || value == null) {
            return;
        }
        if (!sensitive) {
            key = key.toLowerCase();
        }
        int pos = key.indexOf(delimiter);
        if (pos == -1) {
            map.put(key, value);
            if (changedKeys != null) {
                changedKeys.add(key);
            }
        } else {
            String subkey = StringA.mid(key, 0, pos);
            String endkey = StringA.mid(key, pos + 1, key.length());
            if (!subkey.equals(delimiter + "")) {
                if (endkey.equals("")) {
                    if (value instanceof WIMap && (value != this)) {
                        map.put(key, value);
                        if (changedKeys != null) {
                            changedKeys.add(key);
                        }
                    }
                } else {
                    WIMap aux = null;
                    try {
                        aux = (WIMap) getObj(subkey);
                    } catch (ClassCastException err) {
                        // Não deve ocorrer, mas caso aconteça será desconsiderado
                    }
                    if (aux == null) {
                        aux = new WIMap(delimiter, sensitive);
                    }
                    aux.putObj(endkey, value);
                    map.put(subkey, aux);
                    if (changedKeys != null) {
                        changedKeys.add(key);
                    }
                }
            }
        }
    }

    /**
     * Armazena um valor numa chave.
     *
     * @param key a chave a ser utilizada.
     * @param value o valor a ser armazenado.
     */
    public void put(String key, String value) {
        putObj(key, value);
    }

    /**
     * Armazena um valor numa chave.
     *
     * @param key a chave a ser utilizada.
     * @param value o valor a ser armazenado.
     */
    public void put(String key, int value) {
        putObj(key, value + "");
    }

    /**
     * Retorna o valor de uma chave como um Object.
     *
     * @param key a chave a ser utilizada.
     *
     * @return o valor da chave como um Object.
     */
    public Object getObj(String key) {
        key = spcClean(key);
        if ((key.equals("")) || (key.equals(delimiter + ""))) {
            return null;
        }
        if (!sensitive) {
            key = key.toLowerCase();
        }
        Object resp = null;
        int pos = key.indexOf(delimiter);
        if (pos == -1) {
            resp = map.get(key);
        } else {
            String subkey = StringA.mid(key, 0, pos);
            String endkey = StringA.mid(key, pos + 1, key.length());
            if (!subkey.equals(delimiter + "")) {
                WIMap aux = null;
                try {
                    aux = (WIMap) map.get(subkey);
                } catch (ClassCastException err) {
                    // Não deve ocorrer, mas caso aconteça será desconsiderado
                }
                if (aux != null) {
                    resp = aux;
                    if (!endkey.equals("")) {
                        resp = aux.getObj(endkey);
                    }
                }
            }
        }
        return resp;
    }

    /**
     * Retorna o valor de uma chave.
     *
     * @param key a chave a ser utilizada.
     *
     * @return o valor da chave.
     */
    public String get(String key) {
        String value = null;
        try {
            value = (String) getObj(key);
        } catch (ClassCastException err) {
            value = "[object]";
        }
        if (value == null) {
            value = "";
        }
        return value;
    }

    /**
     * Retorna se existe uma dada chave.
     *
     * @param key a chave a ser localizada.
     *
     * @return indica se a a chave existe.
     */
    public boolean containsKey(String key) {
        key = spcClean(key);
        if ((key.equals("")) || (key.equals(delimiter + ""))) {
            return false;
        }
        if (!sensitive) {
            key = key.toLowerCase();
        }
        boolean resp = false;
        int pos = key.indexOf(delimiter);
        if (pos == -1) {
            resp = map.containsKey(key);
        } else {
            String subkey = StringA.mid(key, 0, pos);
            String endkey = StringA.mid(key, pos + 1, key.length());
            if (!subkey.equals(delimiter + "")) {
                WIMap aux = null;
                try {
                    aux = (WIMap) map.get(subkey);
                } catch (ClassCastException err) {
                    // Não deve ocorrer, mas caso aconteça será desconsiderado
                }
                if (aux != null) {
                    if (endkey.equals("")) {
                        resp = true;
                    } else {
                        resp = aux.containsKey(endkey);
                    }
                }
            }
        }
        return resp;
    }

    /**
     * Remove uma chave.
     *
     * @param key a chave a ser removida.
     *
     * @return o valor da chave que foi removida.
     */
    public Object remove(String key) {
        key = spcClean(key);
        if ((key.equals("")) || (key.equals(delimiter + ""))) {
            return null;
        }
        if (!sensitive) {
            key = key.toLowerCase();
        }
        Object resp = null;
        int pos = key.indexOf(delimiter);
        if (pos == -1) {
            resp = map.remove(key);
            if ((resp != null) && (changedKeys != null)) {
                changedKeys.add(key);
            }
        } else {
            String subkey = StringA.mid(key, 0, pos);
            String endkey = StringA.mid(key, pos + 1, key.length());
            if (endkey.equals("")) {
                resp = map.remove(subkey);
                if ((resp != null) && (changedKeys != null)) {
                    changedKeys.add(key);
                }
            } else {
                WIMap aux = null;
                try {
                    aux = (WIMap) getObj(subkey);
                } catch (ClassCastException err) {
                    // Não deve ocorrer, mas caso aconteça será desconsiderado
                }
                if (aux != null) {
                    resp = aux.remove(endkey);
                    if ((resp != null) && (changedKeys != null)) {
                        changedKeys.add(key);
                    }
                }
            }
        }
        return resp;
    }

    /**
     * Armazena os valores de um Map que não são null.
     *
     * @param anotherMap o Map que será  utilizado.
     */
    public void putAll(Map anotherMap) {
        if (anotherMap != null) {
            Iterator it = anotherMap.keySet().iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                Object obj = anotherMap.get(key);
                if (obj != null) {
                    putObj(key, obj);
                }
            }
        }
    }

    /**
     * Retorna o Map interno.
     *
     * @return o Map interno.
     */
    public Map getInternalMap() {
        return map;
    }

    /**
     * Retorna um Map de todas as chaves.
     *
     * @return um Map de todas as chaves.
     */
    public Map getAsMap() {
        Map response = new LinkedHashMap();
        getAsMap(map, response, "");
        return response;
    }

    private void getAsMap(Map aux, Map response, String parent) {
        if (!parent.equals("")) {
            parent = parent + delimiter;
        }
        Iterator it = aux.keySet().iterator();
        while (it.hasNext()) {
            String key = spcClean((String) it.next());
            if (key.endsWith(delimiter + "")) {
                WIMap sub = null;
                try {
                    sub = (WIMap) aux.get(key);
                } catch (ClassCastException err) {
                    // Não deve ocorrer, mas caso aconteça será desconsiderado
                }
                if (sub != null) {
                    response.put(parent + key, "[object]");
                    key = StringA.piece(key, delimiter + "", 1);
                    getAsMap(sub.map, response, parent + key);
                }
            } else {
                response.put(parent + key, aux.get(key));
            }
        }
    }

    /**
     * Retorna um Set das chaves.
     *
     * @return o Set das chaves.
     */
    public Set keySet() {
        Set set = new LinkedHashSet();
        keySet(set, map, "");
        return set;
    }

    private void keySet(Set set, Map aux, String parent) {
        Iterator it = aux.keySet().iterator();
        while (it.hasNext()) {
            Object next = it.next();
            if (next instanceof String) {
                String key = spcClean((String) next);
                Object obj = aux.get(key);
                if (obj != null) {
                    if (obj instanceof WIMap) {
                        WIMap haux = null;
                        try {
                            haux = (WIMap) obj;
                        } catch (ClassCastException err) {
                            // Nunca ocorre.
                        }
                        keySet(set, haux.map, parent + key);
                    } else {
                        set.add(parent + key);
                    }
                }
            }
        }
    }

    private void cloneChild() {
        Iterator it = map.keySet().iterator();
        while (it.hasNext()) {
            Object next = it.next();
            if (next instanceof String) {
                String key = (String) next;
                Object obj = map.get(key);
                if (obj instanceof WIMap) {
                	if (key.equals("super.")) continue;
                    map.put(key, ((WIMap) obj).cloneMe());
                }
            }
        }
    }

    private String spcClean(String key) {
        if (key == null) {
            return "";
        }
        StringBuffer resp = new StringBuffer();
        for (int i = 0; i < key.length(); i++) {
            char let = key.charAt(i);
            if ((let == '\r') || (let == '\n')) {
                let = ' ';
            }
            if (let != ' ') {
                resp.append(let);
            }
        }
        return resp.toString();
    }

    /**
     * Limpa a lista de chaves alteradas.
     */
    public void clear() {
        if (changedKeys != null) {
            changedKeys.clear();
        }
    }

    /**
     * Retorna o WIMap na forma de um texto com todos seus valores.
     *
     * @return o WIMap como texto.
     */
    public String getAsText() {
        return getAsText("*", false);
    }

    /**
     * Retorna o WIMap na forma de um texto com todos seus valores.
     *
     * @return o WIMap como texto.
     */
    public String getAsHtml() {
        return getAsText("*", true);
    }
    
    /**
     * Retorna o WIMap na forma de um texto.
     *
     * @param mask a máscara que pode ser utilizada. Pode ser: "" significa
     *        somente as variáveis da raiz, "" significa todos os dados,
     *        "tmp." significa somente as variáveis de tmp, "tmp." significa
     *        todos os dados de tmp.
     *
     * @return o WIMap como texto.
     */
    public String getAsText(String mask, boolean forHtml) {
        if (mask == null) {
            mask = "*";
        }
        StringA response = new StringA();
        WIMap aux = this;
        boolean onlyvars = false;
        String init = "";
        if (mask.indexOf("*") > -1) {
            mask = StringA.piece(mask, "*", 1);
            if (!mask.equals("")) {
                if (!mask.endsWith(delimiter + "")) {
                    mask = mask + delimiter;
                }
                aux = getWIMapForText(mask);
            }
        } else if (mask.indexOf("%") > -1) {
        	init = StringA.piece(mask, "%", 1);
        	mask = "";
        } else {
            onlyvars = true;
            if (!mask.equals("")) {
                if (!mask.endsWith(delimiter + "")) {
                    mask = mask + delimiter;
                }
                aux = getWIMapForText(mask);
            }
        }
        getAsText(aux, init, mask, response, onlyvars, forHtml);
        return response.toString();
    }

    private WIMap getWIMapForText(String mask) {
        WIMap aux = null;
        Object obj = this.getObj(mask);
        try {
            if (obj != null) {
                aux = (WIMap) obj;
            }
        } catch (ClassCastException err) {
            // Não deve ocorrer
        }
        return aux;
    }

    private void getAsText(WIMap aux, String init, String prefix, 
    		StringA response, boolean onlyvars, boolean forHtml) {
        if (aux != null) {
            Set keys = new TreeSet();
            keys.addAll(aux.map.keySet());
            Iterator it = keys.iterator();
            while (it.hasNext()) {
                String key = (String) it.next();
                Object obj = aux.map.get(key);
                if (obj instanceof WIMap) {
                	if (key.equals("super.")) {
                    	response.append(key + " = [object]" + "\r\n");
                	} else if (!onlyvars) {
                        WIMap sub = (WIMap) aux.getObj(key);
                        sub.getAsText(sub, init, prefix + key, response, false, forHtml);
                    }
                } else if (obj instanceof String) {
                    String fullkey = key;
                    if (!prefix.equals("")) {
                        fullkey = prefix + key;
                    }
                    if (fullkey.startsWith(init)) {
                        String value = (String) aux.get(key);
                        if (forHtml) {
                            value = StringA.changeForHtmlTag(value.trim());
                            if (value.length() > 250) {
                            	value = value.substring(0,250).trim() + " ...";
                            }
                        } else {
                            value = StringA.changeLinebreak(value.trim(), "\\r\\n");
                        }
                    	response.append(fullkey + " = " + value + "\r\n");
                    }	
                } else {
                    String fullkey = key;
                    if (!prefix.equals("")) {
                        fullkey = prefix + key;
                    }
                    if (fullkey.startsWith(init)) {
	                    if (obj != null) {
	                    	response.append(fullkey + " = [object]" + "\r\n");
	                    } else {
	                    	response.append(fullkey + " = \r\n");                    	
	                    }
                    }    
                }
            }
        }
    }

    /**
     * Retorna o WIMap na forma de um texto.
     *
     * @return o WIMap como um texto.
     */
    public String toString() {
        return this.getAsText();
    }
}
