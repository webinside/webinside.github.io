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
import br.com.webinside.runtime.integration.Calculate;
import br.com.webinside.runtime.util.StringA;

// Eval(String Expression, String mask)
public class Eval extends AbstractFunction {
    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String execute(String[] args) throws UserException {
        if (args.length == 0) {
            return "";
        }
        String resp = "";
        if (args[0].equalsIgnoreCase("object")) {
            if (args.length == 2) {
        		resp = getWiMap().get(args[1]);
            } else if (args.length == 3){
            	String key = args[1];
        		key = StringA.change(key, "[]", "[" + args[2]+ "]");
        		resp = getWiMap().get(key);
            }        
        } else {
            if (args.length >= 1) {
                Calculate calc = new Calculate(getWiMap(), args[0]);
                resp = calc.execute();
            }
            if (args.length == 2) {
            	String mask = args[1];
            	String[] params = null;
            	String type = mask.toLowerCase();
            	if (type.startsWith("cbr") || type.equals("round")) {
                    params = new String[2];
                    params[0] = resp;
                    params[1] = mask;
            	} else {
                    params = new String[3];
                    params[0] = resp;
                    params[1] = "FMT";
                    params[2] = mask;
            	}
                NumberFormat nf = new NumberFormat(getWiMap());
                resp = nf.execute(getWiParams(), params);
            }
        }
        return resp;
    }
}
