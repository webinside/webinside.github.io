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
 * Title: OrderBy<br>
 * Description: Classe que cria um 'order by' seguro para ser usado no SQL <br>
 * Copyright:    Copyright (c) 2006<br>
 * Company: Infox<br>
 * @author  Daniel
 * @version 1.0
 * @see br.com.webinside.runtime.integration.AbstractFunction
 */

public class OrderBy extends AbstractFunction {
    
   /**
   * Construtor default da classe OrderBy
   */

    public OrderBy() { }
    
   /**
   * Método que retorna um order by seguro para ser usado nos SQLs.
   * @param args - vetor de parametros na seguinte ordem:<br>
   *    1 - campos sql separados por ':'<br>
   *    2 - variavel<br>
   */
    public String execute(String[] args) throws UserException {
    	if (args.length < 1) return "";
    	String[] campos = args[0].split(";");
    	String variable = "tmp.order";
    	if (args.length > 1) variable = args[1];
        String valor = getWiMap().get(variable).trim();
        for (int i = 0; i < campos.length; i++) {
			String[] campo = campos[i].split(":");
			String cname = campo[0].trim();
			if (valor.equalsIgnoreCase(cname)) {
				valor = orderBy(campos[i], "");
				getWiMap().put(variable, valor);
			}
			if (valor.equalsIgnoreCase(cname + " asc") ||
					valor.equalsIgnoreCase(cname + " desc")) {
				String order = "asc";
				if (valor.endsWith("desc")) {
					order = "desc"; 
				}
				return orderBy(campos, i, order);
			}
		}
        valor = orderBy(campos[0], "");
		getWiMap().put(variable, valor);
		return orderBy(campos, 0, "");
    }
    
    private String orderBy(String[] campos, int index, String order) {
    	StringBuilder aux = new StringBuilder("order by");
    	aux.append(" " + orderBy(campos[index], order));
    	for (int i = 0; i < campos.length; i++) {
			if (i != index) {
		    	aux.append(", " + orderBy(campos[i], ""));
			}
		}
    	return aux.toString();
    }
    
    private String orderBy(String campo, String order) {
		String[] aux = campo.split(":");
		if (order.equals("")) {
			order = "asc";
			if (aux.length > 1) {
				order = aux[1].trim();
			}
		}
		return aux[0].trim() + " " + order;
    }
    
}
