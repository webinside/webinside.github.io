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

package br.com.webinside.runtime.function;

import java.io.File;

import br.com.webinside.runtime.core.ExecuteParamsEnum;
import br.com.webinside.runtime.core.RtmFunction;
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
		    getWiParams().setParameter(ExecuteParamsEnum.PRODUCER, new Producer());
		    if (type.equals("combo")) {
		    	RtmFunction.generateCombo(getWiParams(), realname, false);
		    } else if (type.equals("grid")) {
		    	getWiParams().getWIMap().put("grid.generateInPage", false + "");
		    	RtmFunction.generateGrid(getWiParams(), realname, false);
		    	getWiParams().getWIMap().remove("grid.generateInPage");
		    }
		    getWiParams().setParameter(ExecuteParamsEnum.PRODUCER, producerOrig);
		    pos = end + 1;
		    found = line.indexOf("|" + type + ".", pos);
		}
	}
	
}
