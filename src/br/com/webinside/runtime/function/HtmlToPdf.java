package br.com.webinside.runtime.function;

import java.io.File;

import org.apache.commons.io.FileUtils;

import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.integration.AbstractConnector;
import br.com.webinside.runtime.integration.DatabaseAliases;
import br.com.webinside.runtime.integration.InterfaceHeaders;
import br.com.webinside.runtime.integration.InterfaceParameters;
import br.com.webinside.runtime.integration.JavaParameter;
import br.com.webinside.runtime.util.WIMap;

public class HtmlToPdf extends AbstractConnector implements InterfaceParameters {
	
	private boolean exit = false;
	
	@Override
	public void execute(WIMap wiMap, DatabaseAliases databases,
			InterfaceHeaders headers) throws UserException {
		String className = getClass().getName();
		try {
			String page = wiMap.get("tmp.pdf_page");
			File tmpPdf = HtmlToPdfCore.generatePage(getParams(), page);
	        String name = wiMap.get("tmp.pdf_name").trim();
	        if (name.equals("")) name = "report";
	        if (!name.endsWith(".pdf")) name += ".pdf";
	        if (wiMap.get("tmp.pdf_dest").trim().equals("")) {
	            HtmlToPdfCore.exportPdf(getParams(), tmpPdf, name);
	            tmpPdf.delete();
	            exit = true;
	        } else {
	        	File destFile = new File(wiMap.get("tmp.pdf_dest"),name);
	        	destFile.getParentFile().mkdirs();
	        	FileUtils.copyFile(tmpPdf, destFile);
	        	tmpPdf.delete();
	        }
		} catch (Exception err) {
			err.printStackTrace();
			String pageId = wiMap.get("wi.page.id");
			getParams().getErrorLog().write(className, "Page: " + pageId, err);
		}
	}
			
    @Override
	public boolean exit() {
    	return exit;
	}

	public JavaParameter[] getInputParameters() {
        JavaParameter[] params = new JavaParameter[3];
        params[0] = new JavaParameter("tmp.pdf_page", "Pagina WSP");
        params[1] = new JavaParameter("tmp.pdf_name", "Nome ao salvar");
        params[2] = new JavaParameter("tmp.pdf_dest", "Pasta no servidor (local)");
        return params;
    }

    public JavaParameter[] getOutputParameters() {
        return new JavaParameter[0];
    }
	   
}
