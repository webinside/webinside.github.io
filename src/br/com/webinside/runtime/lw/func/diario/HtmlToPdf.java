package br.com.webinside.runtime.lw.func.diario;

import java.io.File;
import java.util.List;

import org.apache.pdfbox.pdmodel.PDDocument;

import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.function.HtmlToPdfCore;
import br.com.webinside.runtime.integration.AbstractConnector;
import br.com.webinside.runtime.integration.DatabaseAliases;
import br.com.webinside.runtime.integration.DatabaseHandler;
import br.com.webinside.runtime.integration.InterfaceHeaders;
import br.com.webinside.runtime.integration.InterfaceParameters;
import br.com.webinside.runtime.integration.JavaParameter;
import br.com.webinside.runtime.lw.img.ImgEnum;
import br.com.webinside.runtime.lw.img.ImgUtil;
import br.com.webinside.runtime.util.FileIO;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.WIMap;

public class HtmlToPdf extends AbstractConnector implements InterfaceParameters {
	
	private boolean exit = false;
	
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
	    	String key = wiMap.get("tmp.pdf_key");
	    	String idPub = idPublicacao(dh, wiMap);
	    	String pdfVisual = getParams().getWIMap().get("tmp.pdf_visual").trim();
	    	exit = !pdfVisual.equalsIgnoreCase("true");
	    	String pubTmp = wiMap.get("pvt.lwpath.pub-tmp");
	    	File pdfDir = new File(pubTmp, "/diario/visual/p" + idPub + "-" + key);
	    	if (pdfVisual.equalsIgnoreCase("true")) {
	    		if (wiMap.get("tmp.action").equals("visual")) {
	    			Function.removeDir(pdfDir.getAbsolutePath());
	    		}
	    		if (new File(pdfDir,"diario.pdf").exists()) {
		    		FileIO io = new FileIO(new File(pdfDir, "size.txt").getAbsolutePath(), FileIO.READ);
		    		int max = Function.parseInt(io.readText().trim());
		    		wiMap.put("tmp.pdf_pages_total", max);
	    			int nrPage = Function.parseInt(wiMap.get("tmp.id_pagina_visual")) - 1;
	    			if (nrPage >= max) nrPage = max - 1;
	    			if (!new File(pdfDir, "pg-" + nrPage + ".png").isFile()) {
	    				String pg = "[" + nrPage + "]";
	    				List<String> cmd = ImgUtil.cmdPdfToImg(pg, ImgEnum.PNG_GRAY, "x1200");
						ImgUtil.execute(cmd, pdfDir.getAbsolutePath(), "diario", "pg", false);
						new File(pdfDir, "pg.png").renameTo(new File(pdfDir, "pg-" + nrPage + ".png"));
	    			}
	    			return;
	    		}
	    	}
			String wspPage = wiMap.get("tmp.pdf_page");
			if (wspPage.indexOf("?") == -1) wspPage += "?";
			wspPage += "&tmp.key=" + key;
			File tmpPdf = HtmlToPdfCore.generatePage(getParams(), wspPage);
	        String name = wiMap.get("tmp.pdf_name").trim();
	        if (name.equals("")) name = "diario";
	        if (!name.endsWith(".pdf")) name += ".pdf";
        	File tmpPdfFinal = tmpPdf;
        	if (true) {
        		tmpPdfFinal = new File(Function.rndTmpFile("tmp", "pdf"));
    	        String ltitle = wiMap.get("tmp.pdf_ltitle").trim();
    	        String ctitle = wiMap.get("tmp.pdf_ctitle").trim();
    	        String rtitle = wiMap.get("tmp.pdf_rtitle").trim();
    	        String pType = wiMap.get("tmp.pdf_pubtype").trim();
    	        HtmlToPdfTabular htpt = 
    	        		new HtmlToPdfTabular(getParams(), ltitle, ctitle, rtitle, pType);
    	        htpt.execute(tmpPdf, tmpPdfFinal);
        	}	
	    	if (pdfVisual.equalsIgnoreCase("true")) {
	    		countPagesTotal(wiMap, tmpPdfFinal);
    			pdfDir.mkdirs();
	    		tmpPdfFinal.renameTo(new File(pdfDir,"diario.pdf"));
	            tmpPdf.delete();
	    		FileIO io = new FileIO(new File(pdfDir, "size.txt").getAbsolutePath(), FileIO.WRITE);
	    		io.writeText(wiMap.get("tmp.pdf_pages_total"));
    			int nrPage = Function.parseInt(wiMap.get("tmp.id_pagina_visual")) - 1;
				String pg = "[" + nrPage + "]";
				List<String> cmd = ImgUtil.cmdPdfToImg(pg, ImgEnum.PNG_GRAY, "x1200");
				ImgUtil.execute(cmd, pdfDir.getAbsolutePath(), "diario", "pg", false);
				new File(pdfDir, "pg.png").renameTo(new File(pdfDir, "pg-" + nrPage + ".png"));
	    	} else {
	            HtmlToPdfCore.exportPdf(getParams(), tmpPdfFinal, name);
	            tmpPdfFinal.delete();
	            tmpPdf.delete();
	    	}
		} catch (Exception err) {
			err.printStackTrace();
			String pageId = wiMap.get("wi.page.id");
			getParams().getErrorLog().write(className, "Page: " + pageId, err);
		}
	}
			
	private String idPublicacao(DatabaseHandler dh, WIMap wiMap) throws Exception {
		String query = "select id_publicacao from tb_diario_publicacao";
		query += " where ts_rnd_key = ?|tmp.pdf_key| and st_removido = 0";
		ResultSet rsKey = dh.execute(query, wiMap);
		return rsKey.columnNext(1);
	}
	
	private void countPagesTotal(WIMap wiMap, File pdf) throws Exception {
		PDDocument pdfDoc = PDDocument.load(pdf);
		int pageTotal = pdfDoc.getNumberOfPages();
		wiMap.put("tmp.pdf_pages_total", pageTotal + "");
		pdfDoc.close();
	}
	
    @Override
	public boolean exit() {
    	return exit;
	}

	public JavaParameter[] getInputParameters() {
        JavaParameter[] params = new JavaParameter[8];
        params[0] = new JavaParameter("tmp.pdf_key", "Chave da Publicação");
        params[1] = new JavaParameter("tmp.pdf_page", "Pagina WSP");
        params[2] = new JavaParameter("tmp.pdf_name", "Nome ao salvar");
        params[3] = new JavaParameter("tmp.pdf_ltitle", "Título do diário (left)");
        params[4] = new JavaParameter("tmp.pdf_ctitle", "Título do diário (center)");
        params[5] = new JavaParameter("tmp.pdf_rtitle", "Título do diário (right)");
        params[6] = new JavaParameter("tmp.pdf_pubtype", "Tipo(R/E/S)");
        params[7] = new JavaParameter("tmp.pdf_visual", "Visual(true/false)");
        return params;
    }

    public JavaParameter[] getOutputParameters() {
        JavaParameter[] outParam = new JavaParameter[1];
        outParam[0] = new JavaParameter("tmp.pdf_xhtml", "Conteúdo XHTML"); 
        return outParam;
    }
	   
}
