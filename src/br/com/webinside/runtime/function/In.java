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

package br.com.webinside.runtime.function;

import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.integration.AbstractFunction;

/**
 * Title: In<br>
 * Description: Classe que recebe um texto, uma lista de elementos e um delimitador
 *              (opcional). A saída é true caso o texto seja um dos elementos da
 *              lista, caso contrário será false<br>
 * Copyright:    Copyright (c) 2006<br>
 * Company: Infox<br>
 * @author  Daniel
 * @version 1.0
 * @see br.com.webinside.runtime.integration.AbstractFunction
 */

public class In extends AbstractFunction {
    
   /**
   * Construtor default da classe In
   */

    public In() { }
    
   /**
   * Método que retorna true casa o texto passado esteja contido na lista passada
   * na entrada entrada.
   * O último parâmetro é opcional, sendo que o separador default usado
   * é a vígula.
   * @param args - vetor de parametros na seguinte ordem:<br>
   *    1 - texto a ser procurado<br>
   *    2 - lista de valores<br>
   *    3 - separador da lista de entrada <b>(opcional - default:';')</b><br>
   */

    public String execute(String[] args) throws UserException {
        String separador = ":";
        if (args.length > 2) {
            separador = args[2];
            if (separador.equals("comma")) separador = ",";
        }
        String texto = separador + args[0] + separador;
        String lista = separador + args[1] + separador;
        if (lista.indexOf(texto) >= 0) {
            return "true";
        } else {
            return "false";
        }
    }
    
    public static void main(String[] args) throws Exception {
        String[] teste = {"CA","SV;AT;MG;CG", ";"};
        System.out.println(new In().execute(teste));
    }   
}
