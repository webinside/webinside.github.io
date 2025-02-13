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

import java.util.Comparator;

/**
 * Classe que implementa um Comparator para ser usado com String e que suporta
 * delimitador, compara��o em min�sculo e compara��o como de n�mero como
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
     * Efetua a compara��o entre 2 textos utilizando os crit�rios definidos.
     *
     * @param t1 o primeiro texto.
     * @param t2 o segundo texto.
     *
     * @return o resultado da compara��o. O retorno ser� 0 (zero) se forem
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
                    Integer i1 = Integer.valueOf(s1.trim());
                    Integer i2 = Integer.valueOf(s2.trim());
                    ret = i1.compareTo(i2);
                } else if (ignoreCase) {
                    ret = s1.toLowerCase().compareTo(s2.toLowerCase());
                } else {
                    ret = s1.compareTo(s2);
                }
            } catch (Exception e) {
                // N�o deve ocorrer.
                e.printStackTrace(System.err);
            }
        }
        return ret;
    }

    /**
     * Define se a compara��o deve ser feita usando apenas uma parte do texto
     * extraida baseada num dado delimitador.
     *
     * @param delimiter o delimitador a ser utilizado.
     * @param position a posi��o a ser utilizada.
     */
    public void setPiece(String delimiter, int position) {
        if ((position > 0) && (delimiter != null) && !"".equals(delimiter)) {
            this.delimiter = delimiter;
            this.position = position;
        }
    }

    /**
     * Define se a compara��o deve ser feita como um parse para n�mero inteiro.
     *
     * @param value indica se a compara��o deve ser como inteiro.
     */
    public void setInteger(boolean value) {
        this.integer = value;
    }

    /**
     * Define se a compara��o deve ser em min�sculo.
     *
     * @param value indica se a compara��o deve ser um min�sculo.
     */
    public void setIgnoreCase(boolean value) {
        this.ignoreCase = value;
    }
}
