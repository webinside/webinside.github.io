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

import java.io.File;

import br.com.webinside.runtime.core.EngFunction;
import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.integration.AbstractFunction;
import br.com.webinside.runtime.integration.IntFunction;
import br.com.webinside.runtime.integration.Producer;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.util.FileIO;
import br.com.webinside.runtime.util.StringA;

// Include(String file, Boolean absolutePath)
public class Include extends AbstractFunction {

	public String execute(String[] args) throws UserException {
		String resp = "";
		if (args.length > 0) {
			String usrFile = StringA.change(args[0], "../", "/");
			File file = new File(getWiMap().get("wi.proj.path"), usrFile);
			if (args.length > 1 && args[1].equalsIgnoreCase("true")) {
				file = new File(usrFile) ;
			}
			if (file.isFile()) {
				FileIO fio = new FileIO(file.getAbsolutePath(), 'r');
				String content = fio.readText();
				try {
					findElement(content, "combo");
					findElement(content, "grid");
				} catch (Exception ex) {
					throw new UserException("Include Error", ex);
				}	
				resp = produce(content);
				fio.close();
			}
		}
		return resp;
	}
	
	private String produce(String content) {
		ProducerParam prod = new ProducerParam();
		prod.setWIMap(getWiMap());
		prod.setInput(content);
		Producer producer = new Producer(prod);
		producer.execute();
		return prod.getOutput().trim();
	}

	private void findElement(String content, String type) throws Exception {
        String line = content.toLowerCase();
		int pos = 0;
		int found = line.indexOf("|" + type + ".", pos);
		while (found > -1) {
		    int end = line.indexOf("|", found + 1);
		    if (end == -1) {
		        end = line.length();
		    }
		    String name = StringA.mid(content, found + 1, end - 1);
		    name = StringA.piece(name, type + ".", 2, 0, false);
		    if (name.indexOf("?") > -1) {
            	IntFunction.importParameters(getWiParams(), name);
		    }
		    String realname = StringA.piece(name, "?", 1);
		    Producer producerOrig = getWiParams().getProducer();
		    getWiParams().setParameter(ExecuteParams.PRODUCER, new Producer());
		    if (type.equals("combo")) {
		    	EngFunction.generateCombo(getWiParams(), realname, false);
		    } else if (type.equals("grid")) {
		    	getWiParams().getWIMap().put("grid.generateInPage", false + "");
		    	EngFunction.generateGrid(getWiParams(), realname, false);
		    	getWiParams().getWIMap().remove("grid.generateInPage");
		    }
		    getWiParams().setParameter(ExecuteParams.PRODUCER, producerOrig);
		    pos = end + 1;
		    found = line.indexOf("|" + type + ".", pos);
		}
	}
	
}
