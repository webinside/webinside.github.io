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

import br.com.webinside.runtime.integration.AbstractFunction;
import br.com.webinside.runtime.util.StringA;

public class GenericFormat extends AbstractFunction {
    /** DOCUMENT ME! */
    private static final String CPF = "cpf";
    /** DOCUMENT ME! */
    private static final String CNPJ = "cnpj";

    public String execute(String[] args) {
    	StringA resp = new StringA();
        if (args.length == 2) {
        	String value = args[0];
        	String action = args[1];
            if (action.equalsIgnoreCase(CPF)) {
            	while (value.length() < 11) {
            		value = 0 + value;
            	}
            	String p1 = StringA.mid(value,0,2);
            	String p2 = StringA.mid(value,3,5);
            	String p3 = StringA.mid(value,6,8);
            	String p4 = StringA.mid(value,9,10);
            	resp.set(p1 + "." + p2 + "." + p3 + "-" + p4);
            } else if (action.equalsIgnoreCase(CNPJ)) {
            	while (value.length() < 14) {
            		value = 0 + value;
            	}
            	String p1 = StringA.mid(value,0,1);
            	String p2 = StringA.mid(value,2,4);
            	String p3 = StringA.mid(value,5,7);
            	String p4 = StringA.mid(value,8,11);
            	String p5 = StringA.mid(value,12,13);
            	resp.set(p1 + "." + p2 + "." + p3 + "/" + p4 + "-" + p5);
            }	
        }
        return resp.toString();
    }

}
