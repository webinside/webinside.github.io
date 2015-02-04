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

import java.util.Comparator;

/**
 * Classe que implementa um Comparator para ser usado com String e que suporta
 * delimitador, comparação em minúsculo e comparação como de número como
 * inteiro.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.1 $
 *
 * @since 3.0
 */
public class StringComparator implements Comparator {
    private int position;
    private String delimiter;
    private boolean ignoreCase = false;
    private boolean integer = false;

    /**
     * Efetua a comparação entre 2 textos utilizando os critérios definidos.
     *
     * @param t1 o primeiro texto.
     * @param t2 o segundo texto.
     *
     * @return o resultado da comparação. O retorno será 0 (zero) se forem
     *         iguais, menor que zero se t1 for antes de t2 e maior que zero
     *         se t2 for antes de t1.
     */
    public int compare(Object t1, Object t2) {
        int ret = 0;
        if ((t1 != null) && (t2 != null)) {
            try {
                String s1 = (String) t1;
                String s2 = (String) t2;

                if (delimiter != null) {
                    s1 = StringA.piece(s1, delimiter, position);
                    s2 = StringA.piece(s2, delimiter, position);
                }
                if (integer) {
                    Integer i1 = new Integer(s1.trim());
                    Integer i2 = new Integer(s2.trim());
                    ret = i1.compareTo(i2);
                } else if (ignoreCase) {
                    ret = s1.toLowerCase().compareTo(s2.toLowerCase());
                } else {
                    ret = s1.compareTo(s2);
                }
            } catch (Exception e) {
                // Não deve ocorrer.
                e.printStackTrace(System.err);
            }
        }
        return ret;
    }

    /**
     * Define se a comparação deve ser feita usando apenas uma parte do texto
     * extraida baseada num dado delimitador.
     *
     * @param delimiter o delimitador a ser utilizado.
     * @param position a posição a ser utilizada.
     */
    public void setPiece(String delimiter, int position) {
        if ((position > 0) && (delimiter != null) && !"".equals(delimiter)) {
            this.delimiter = delimiter;
            this.position = position;
        }
    }

    /**
     * Define se a comparação deve ser feita como um parse para número inteiro.
     *
     * @param value indica se a comparação deve ser como inteiro.
     */
    public void setInteger(boolean value) {
        this.integer = value;
    }

    /**
     * Define se a comparação deve ser em minúsculo.
     *
     * @param value indica se a comparação deve ser um minúsculo.
     */
    public void setIgnoreCase(boolean value) {
        this.ignoreCase = value;
    }
}
