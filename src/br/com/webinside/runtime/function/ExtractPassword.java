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
import br.com.webinside.runtime.util.JavaScriptDES;
import br.com.webinside.runtime.util.StringA;

public class ExtractPassword extends AbstractFunction {
	/**
	 * Creates a new ExtractPassword object.
	 */
	public ExtractPassword() { }

	// arg[0] = oldpass into BD
	// arg[1] = received DES password
	public String execute(String[] args) {
		if ((args == null) || (args.length < 2)) {
			return "";
		}	
		if (args[0].trim().equals("")) {
			return "";
		}
		String oldpass = args[0];
		String despass = args[1];
		JavaScriptDES des = new JavaScriptDES(oldpass);
		String open = des.decode(despass);
		String ini = StringA.mid(oldpass, 0, 7);
		if (!open.startsWith(ini)) {
			return "";
		} else {
			return StringA.mid(open, 8, open.length());
		}	
	}
}
