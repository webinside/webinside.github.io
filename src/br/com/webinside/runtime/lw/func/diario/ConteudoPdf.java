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

package br.com.webinside.runtime.lw.func.diario;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.StringReader;
import java.util.List;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.pdfbox.pdmodel.PDDocument;

import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.exception.UserException;
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

public class ConteudoPdf extends AbstractConnector implements InterfaceParameters {

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
			String pdfFolder = wiMap.get("pvt.lwpath.priv") + "/diario/conteudo";
			String pdfFile = pdfFolder + "/file-" + idCont + ".pdf";
			PDDocument pdfDoc = PDDocument.load(pdfFile);
			int pages = pdfDoc.getNumberOfPages();
			pdfDoc.close();
			String tmpFolder = Function.rndTmpFolder("pdf");
	        String tmpFile = tmpFolder + "/file.pdf";
	        Function.copyFile(pdfFile, tmpFile, true);
			List<String> cmd = ImgUtil.cmdPdfToImg("", ImgEnum.PNG_GRAY, 150);
			ImgUtil.execute(cmd, tmpFolder, "file", "pg", false);
			String fldKey = "c" + idCont + "-" + contKey;
			String imgFolder = wiMap.get("pvt.lwpath.pub") + "/diario/imagens/" + fldKey;
			Function.removeFiles(imgFolder, "*.png");
			File single = new File(tmpFolder, "pg.png");
			if (single.isFile()) single.renameTo(new File(tmpFolder, "pg-0.png"));
	        String[] files = Function.listDir(tmpFolder, "*.png", false, false);
			for (int i = 0; i < files.length; i++) {
	        	String srcFile = tmpFolder + "/" + files[i];
	        	String destFile = imgFolder + "/" + files[i];
	        	Function.copyFile(srcFile, destFile, true);
	        }
			ImgUtil.executeOCR(tmpFolder, "file");
			StringBuilder conteudo = new StringBuilder();
			for (int i = 0; i < pages; i++) {
				String txtFile = tmpFolder + "/ocr-" + i + ".txt";
				conteudo.append(new FileIO(txtFile, 'R').readText("UTF-8"));
			}
            String line = null;
			StringBuilder html = new StringBuilder();
            BufferedReader in = new BufferedReader(new StringReader(conteudo.toString()));
            while ((line = in.readLine()) != null) {
            	if (!line.trim().equals("")) {
            		html.append("<p>" + line.trim() + "</p>\r\n");
            	}
            }
            in.close();
			wiMap.put("tmp.diario.tx_conteudo", html.toString());
			wiMap.put("tmp.diario.nr_paginas_pdf", pages + "");
			FileInputStream fIn = new FileInputStream(pdfFile);
			wiMap.put("tmp.diario.ts_cont_sha1", DigestUtils.sha1Hex(fIn));
			fIn.close();
			String update = "update tb_diario_conteudo ";
			update += " set tx_conteudo = ?|tmp.diario.tx_conteudo|";
			update += " , ts_cont_sha1 = ?|tmp.diario.ts_cont_sha1|";
			update += " , st_conteudo_pdf = true";
			update += " , nr_paginas_pdf = ?|tmp.diario.nr_paginas_pdf|";
			update += " where id_conteudo = ?|tmp.diario.id_conteudo|";
			dh.executeUpdate(update, wiMap);
			Function.removeDir(tmpFolder);
		} catch (Exception err) {
			String pageId = wiMap.get("wi.page.id");
			getParams().getErrorLog().write(className, "Page: " + pageId, err);
		}
	}
		
	public JavaParameter[] getInputParameters() {
		JavaParameter[] in = new JavaParameter[1];
		in[0] = new JavaParameter("tmp.diario.id_conteudo", "ID do Conteúdo");
		return in;
	}

	public JavaParameter[] getOutputParameters() {
		return new JavaParameter[0];
	}
	
}
