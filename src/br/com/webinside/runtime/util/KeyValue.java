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

/**
 * Classe que corresponde a um Bean de chave e valor.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.1 $
 *
 * @since 3.0
 */
public class KeyValue implements Serializable {

	private static final long serialVersionUID = 1L;
    private String key;
    private Object value;

    /**
     * Cria um novo KeyValue.
     */
    public KeyValue() {
        key = "";
    }

    /**
     * Cria um novo KeyValue.
     *
     * @param k a chave.
     * @param v o valor.
     */
    public KeyValue(String k, Object v) {
        setKey(k);
        value = v;
    }

    /**
     * Define a chave.
     *
     * @param k a chave.
     */
    public void setKey(String k) {
        if (k != null) {
            key = k;
        }
    }

    /**
     * Retorna a chave.
     *
     * @return a chave.
     */
    public String getKey() {
        return key;
    }

    /**
     * Define o valor.
     *
     * @param v o valor.
     */
    public void setValue(Object v) {
        value = v;
    }

    /**
     * Retorna o valor.
     *
     * @return o valor.
     */
    public Object getValue() {
        return value;
    }

    /**
     * Retorna o valor como uma String.
     *
     * @return o valor.
     */
    public String getValueString() {
        String v = value.toString();
        if (v == null) {
            v = "";
        }
        return v;
    }
}
