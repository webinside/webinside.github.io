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

package br.com.webinside.runtime.lw.func;

import java.io.File;

import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.integration.AbstractConnector;
import br.com.webinside.runtime.integration.DatabaseAliases;
import br.com.webinside.runtime.integration.InterfaceHeaders;
import br.com.webinside.runtime.integration.InterfaceParameters;
import br.com.webinside.runtime.integration.JavaParameter;
import br.com.webinside.runtime.util.WIMap;

public class LWPath extends AbstractConnector implements InterfaceParameters {

	public void execute(WIMap wiMap, DatabaseAliases databases, 
			InterfaceHeaders headers) throws UserException {
		String webapps = wiMap.get("wi.webapps.path");
		String app = wiMap.get("pvt.ts_sigla_app").trim();
		if (!app.equals("")) {
			// publico
			String pubUrl = "lwstorage/publico/" + app;
			File pubDir = new File(webapps, pubUrl);
			pubDir.mkdirs();
			wiMap.put("pvt.lwpath.pub", pubDir.getAbsolutePath());
			wiMap.put("pvt.lwurl.pub", pubUrl);
			// privado
			File privDir = new File(webapps, "lwstorage/WEB-INF/privado/" + app);
			privDir.mkdirs();
			wiMap.put("pvt.lwpath.priv", privDir.getAbsolutePath());
			// publico-temp
			String pubTmpUrl = "lwstorage/publico-temp/" + app;
			File pubTmpDir = new File(webapps, pubTmpUrl);
			pubTmpDir.mkdirs();
			wiMap.put("pvt.lwpath.pub-tmp", pubTmpDir.getAbsolutePath());
			wiMap.put("pvt.lwurl.pub-tmp", pubTmpUrl);
			// privado-temp
			File privTmpDir = new File(webapps, "lwstorage/WEB-INF/privado-temp/" + app);
			privTmpDir.mkdirs();
			wiMap.put("pvt.lwpath.priv-tmp", privTmpDir.getAbsolutePath());
		}
	}
		
	public JavaParameter[] getInputParameters() {
		return new JavaParameter[0];
	}

	public JavaParameter[] getOutputParameters() {
		return new JavaParameter[0];
	}
	
}
