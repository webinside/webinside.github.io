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

/**
 * Classe que codifica as senhas do WI usando uma chave específica.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.4 $
 *
 * @since 3.0
 */
public class Encrypter {
    private static String key = "webintegrator";
    private String text;

    /**
     * Cria um novo Encrypter.
     *
     * @param t o texto a ser manipulado.
     */
    public Encrypter(String t) {
        text = t;
    }

    /**
     * Codifica o texto para o formato DES.
     *
     * @return o texto codificado.
     */
    public String encodeDES() {
        Crypto crp = new Crypto(key);
        return crp.encodeDES(text);
    }

    /**
     * Decodifica o texto no formato DES.
     *
     * @return o texto decodificado.
     */
    public String decodeDES() {
        Crypto crp = new Crypto(key);
        return crp.decodeDES(text);
    }

    /**
     * Define a chave a ser utilizada.
     *
     * @param k a chave a ser utilizada.
     */
    public static void changeKey(String k) {
        if (k != null) {
            key = k;
        }
    }
}
