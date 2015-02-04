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

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

import javax.imageio.ImageIO;

import org.imgscalr.Scalr;
import org.imgscalr.Scalr.Rotation;

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
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.WIMap;

public class ConteudoRecorte extends AbstractConnector implements InterfaceParameters {

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
			String action = wiMap.get("tmp.diario.tipo").toLowerCase().trim();
			int idCont = Function.parseInt(wiMap.get("tmp.diario.id_conteudo").trim());
			int idPag = Function.parseInt(wiMap.get("tmp.diario.id_pagina").trim());
			if (idPag < 1) idPag = 1;
			String query = "select ts_rnd_key from tb_diario_conteudo";
			query += " where id_conteudo = ?|tmp.diario.id_conteudo|";
			ResultSet rsKey = dh.execute(query, wiMap);
			String contKey = rsKey.columnNext(1);
			String pngFolder = wiMap.get("pvt.lwpath.pub") + "/diario/imagens";
			String imgFolder = pngFolder + "/c" + idCont + "-" + contKey;
			String pngFile = imgFolder + "/pg-" + (idPag-1) + ".png";
			if (action.equals("save")) {
				int idWtotal = Function.parseInt(wiMap.get("tmp.nr_wtotal").trim());
				int idHtotal = Function.parseInt(wiMap.get("tmp.nr_htotal").trim());
				int idWinicial = Function.parseInt(wiMap.get("tmp.nr_winicial").trim());
				int idHinicial = Function.parseInt(wiMap.get("tmp.nr_hinicial").trim());
				int idWfinal = Function.parseInt(wiMap.get("tmp.nr_wfinal").trim());
				int idHfinal = Function.parseInt(wiMap.get("tmp.nr_hfinal").trim());
				if (idHtotal > 0) {
					BufferedImage orig = ImageIO.read(new File(pngFile));
					int w1 = Math.round(idWinicial * orig.getWidth() / idWtotal);
					int h1 = Math.round(idHinicial * orig.getHeight() / idHtotal);
					int w2 = Math.round(idWfinal * orig.getWidth() / idWtotal);
					int h2 = Math.round(idHfinal * orig.getHeight() / idHtotal);
					BufferedImage crop = orig.getSubimage(w1, h1, w2 - w1, h2 - h1);			
					ImageIO.write(crop, "png", new File(pngFile));			
					orig.flush();
				}
			} else if (action.equals("desfazer") || 
					action.equals("direita") || action.equals("esquerda")) {
				String pdfFolder = wiMap.get("pvt.lwpath.priv") + "/diario/conteudo";
				String pdfFile = pdfFolder + "/file-" + idCont + ".pdf";
				String tmpFolder = Function.rndTmpFolder("pdf");
		        String tmpFile = tmpFolder + "/file.pdf";
		        Function.copyFile(pdfFile, tmpFile, true);		
		        String page = "[" + (idPag-1) + "]";
				List<String> cmd = ImgUtil.cmdPdfToImg(page, ImgEnum.PNG_GRAY);
				ImgUtil.execute(cmd, tmpFolder, "file", "pg", false);
	        	String srcFile = tmpFolder + "/pg.png";
				Function.copyFile(srcFile, pngFile, true);
				Function.removeDir(tmpFolder);
				if (action.equals("direita") || action.equals("esquerda")) {
					BufferedImage orig = ImageIO.read(new File(pngFile));
					BufferedImage rotate = null;
					if (action.equalsIgnoreCase("direita")) {
						rotate = Scalr.rotate(orig, Rotation.CW_90);
					} else {
						rotate = Scalr.rotate(orig, Rotation.CW_270);
					}
					ImageIO.write(rotate, "png", new File(pngFile));
					orig.flush();
				}	
			}
		} catch (Exception err) {
			String pageId = wiMap.get("wi.page.id");
			getParams().getErrorLog().write(className, "Page: " + pageId, err);
		}
	}
		
	public JavaParameter[] getInputParameters() {
		JavaParameter[] in = new JavaParameter[9];
		in[0] = new JavaParameter("tmp.diario.id_conteudo", "ID do Conteúdo");
		in[1] = new JavaParameter("tmp.diario.id_pagina", "ID da Página");
		in[2] = new JavaParameter("tmp.diario.tipo", "Tipo Ação");
		in[3] = new JavaParameter("tmp.diario.wtotal", "W Total");
		in[4] = new JavaParameter("tmp.diario.htotal", "H Total");
		in[5] = new JavaParameter("tmp.diario.winicial", "W Inicial");
		in[6] = new JavaParameter("tmp.diario.wfinal", "W Final");
		in[7] = new JavaParameter("tmp.diario.hinicial", "H Inicial");
		in[8] = new JavaParameter("tmp.diario.hfinal", "H Final");
		return in;
	}

	public JavaParameter[] getOutputParameters() {
		return new JavaParameter[0];
	}
	
}
