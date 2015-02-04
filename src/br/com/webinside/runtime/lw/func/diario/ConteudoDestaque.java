package br.com.webinside.runtime.lw.func.diario;

import java.io.File;

import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.function.HtmlToPdfCore;
import br.com.webinside.runtime.integration.AbstractConnector;
import br.com.webinside.runtime.integration.DatabaseAliases;
import br.com.webinside.runtime.integration.DatabaseHandler;
import br.com.webinside.runtime.integration.InterfaceHeaders;
import br.com.webinside.runtime.integration.InterfaceParameters;
import br.com.webinside.runtime.integration.JavaParameter;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.WIMap;

public class ConteudoDestaque extends AbstractConnector implements InterfaceParameters {
	
	@Override
	public void execute(WIMap wiMap, DatabaseAliases databases,
			InterfaceHeaders headers) throws UserException {
		String className = getClass().getName();
		DatabaseHandler dh = null;
		try {
			String database = "principal";
			dh = databases.get(database);
			if (dh == null) {
				String msg = "Get database error (" + database + ")";
				String pageId = wiMap.get("wi.page.id");
				getParams().getErrorLog().write(className, "Page: " + pageId, msg);
				return;
			}
			int idCont = Function.parseInt(wiMap.get("tmp.diario.id_conteudo").trim());
			String query = "select ts_rnd_key from tb_diario_conteudo";
			query += " where id_conteudo = ?|tmp.diario.id_conteudo|";
			ResultSet rsKey = dh.execute(query, wiMap);
			String contKey = rsKey.columnNext(1);
			int idPag = Function.parseInt(wiMap.get("tmp.diario.id_pagina").trim());
			if (idPag < 1) idPag = 1;
			String pdfFolder = wiMap.get("pvt.lwpath.priv") + "/diario/destaque";
			File pdfFile = new File(pdfFolder + "/c" + idCont + "p" + idPag + ".pdf");
			pdfFile.delete();
			if (wiMap.get("tmp.diario.st_destaque").trim().equals("1")) {
				String page = "diario/pub_pdf_destaque.wsp?tmp.key=" + contKey + "&tmp.id_pagina="+idPag;
				File tmpPdf = HtmlToPdfCore.generatePage(getParams(), page);
				pdfFile.getParentFile().mkdirs();
	        	tmpPdf.renameTo(pdfFile);
			}
		} catch (Exception err) {
			err.printStackTrace();
			String pageId = wiMap.get("wi.page.id");
			getParams().getErrorLog().write(className, "Page: " + pageId, err);
		}
	}
			
    @Override
	public boolean exit() {
    	return false;
	}

	public JavaParameter[] getInputParameters() {
        JavaParameter[] params = new JavaParameter[3];
        params[0] = new JavaParameter("tmp.diario.id_conteudo", "ID do Conteúdo");
        params[1] = new JavaParameter("tmp.diario.id_pagina", "ID da Página");
        params[2] = new JavaParameter("tmp.diario.st_destaque", "Destaque (Bit)");
        return params;
    }

    public JavaParameter[] getOutputParameters() {
        JavaParameter[] outParam = new JavaParameter[0];
        return outParam;
    }
	   
}
