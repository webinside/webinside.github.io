/*
 * WEBINSIDE - Ferramenta de produtividade Java
 * Copyright (c) 2011-2012 LINEWEB Solu��es Tecnol�gicas Ltda.
 * Copyright (c) 2009-2010 Inc�gnita Intelig�ncia Digital Ltda.
 *
 * Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 * sob os termos da GNU LESSER GENERAL PUBLIC LICENSE (LGPL) conforme publicada 
 * pela Free Software Foundation; vers�o 2.1 da Licen�a.
 * Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 * NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 * ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 * 
 * Consulte a GNU LGPL para mais detalhes.
 * Voc� deve ter recebido uma c�pia da GNU LGPL junto com este programa; se n�o, 
 * veja em http://www.gnu.org/licenses/ 
 */

package br.com.webinside.runtime.util;

/**
 * Classe que codifica as senhas do WI usando uma chave espec�fica.
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
    
    public static void main(String[] args) {
        Encrypter pw = new Encrypter("82afe04628fe6147");
        System.out.println(pw.decodeDES());
    }
    
}
