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

package br.com.webinside.runtime.lw.sql;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.function.database.AbstractConnectorDB;
import br.com.webinside.runtime.function.database.NodeTable;
import br.com.webinside.runtime.integration.DatabaseAliases;
import br.com.webinside.runtime.integration.DatabaseHandler;
import br.com.webinside.runtime.integration.InterfaceHeaders;
import br.com.webinside.runtime.integration.InterfaceParameters;
import br.com.webinside.runtime.integration.JavaParameter;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.WIMap;

public class PersistPdf extends AbstractConnectorDB implements InterfaceParameters {

	public void execute(WIMap wiMap, DatabaseAliases databases, 
			InterfaceHeaders headers) throws UserException {
		String className = getClass().getName();
		DatabaseHandler dh = null;
		try {
			String database = wiMap.get("tmp.persistpdf.database").trim();
			if (database.equals("")) database = "principal";
			dh = databases.get(database);
			if (dh == null) {
				String msg = "Get database error (" + database + ")";
				String pageId = wiMap.get("wi.page.id");
				getParams().getErrorLog().write(className, "Page: " + pageId, msg);
				return;
			}
			String formField = wiMap.get("tmp.persistpdf.form_field").trim();
			if (!wiMap.get(formField).trim().endsWith(".pdf")) return;
			String table = wiMap.get("tmp.persistpdf.table");
			String keyName = wiMap.get("tmp.persistpdf.pkey").trim();
			int keyVal = Integer.parseInt(wiMap.get(keyName));
			String pdfField = wiMap.get("tmp.persistpdf.pdf_field").trim();
			NodeTable nodeTable = getNodeTable(dh, table);
			List keys = new ArrayList(nodeTable.getPrimaryKeys().keySet());
			String query = "select " + pdfField + " from " + nodeTable.getName();
			query += " where " + keys.get(0) + " = " + keyVal;
			String respMessage = "";
			ResultSet rsKey = null;
			try {
				rsKey = dh.execute(query, wiMap);
			} catch (Exception e) { 
				// ignorado
			}
			if (rsKey != null) {
				int pdfKey = 0;
				if (rsKey.next() > 0) {
					pdfKey = Function.parseInt(rsKey.column(1));
				}	
				int fkPdf = executePdf(wiMap, dh, formField);
				if (fkPdf > 0) {
					if (pdfKey > 0) {
						
					}
				} else {
					respMessage = "Arquivo pdf com tamanho vazio";
				}
			} else {
				respMessage = "Falha em PersistPdf: " + dh.getErrorMessage();
			}
			String respVar = wiMap.get("tmp.persistpdf.resp").trim();
			if (!respVar.equals("")) {
				if (respMessage.equals("")) {
					wiMap.put(respVar + ".ok()", "true");
					wiMap.put(respVar, "Arquivo PDF gravado com sucesso");
				} else {
					wiMap.put(respVar + ".ok()", "false");
					wiMap.put(respVar, respMessage);
				}
			}
			// Criando a busca textual para o PDF
			if (keys.size() == 1 && false) {
				WIMap auxMap2 = new WIMap();
				auxMap2.put("wi.updatelog", "true");
				auxMap2.put("colecao", table.replace("tb_", "").trim());
//				auxMap2.put("ident", key1);
				String call = "call sp_base_textual(?|colecao|,?|ident|,0,null)";
				dh.executeUpdate(call, auxMap2);
			}	
		} catch (Exception err) {
			String pageId = wiMap.get("wi.page.id");
			String table = wiMap.get("tmp.persistpdf.table");
			String message = "Page: " + pageId + ", Table: " + table;
			getParams().getErrorLog().write(className, message, err);
		}
	}
	
	private int executePdf(WIMap wiMap, DatabaseHandler dh, String formField) 
	throws Exception {
		String rndKey = Function.randomKey().toLowerCase();
        File pdfFile = new File(Function.tmpDir(), "pdf-" + rndKey + "/file.pdf");
        pdfFile.getParentFile().mkdirs();
        getParams().getFileUpload().saveFile(formField, pdfFile.getAbsolutePath());
        if (pdfFile.length() == 0) return 0;
        WIMap auxMap = new WIMap();
        auxMap.put("fk_empresa", wiMap.get("pvt.id_empresa"));
        auxMap.put("ts_rnd_key", rndKey);
		String table = wiMap.get("tmp.persistpdf.table").trim();
		auxMap.put("ts_tabela", table.replace("tb_", "").trim());
        auxMap.put("nr_pdf_size", pdfFile.length() + "");
        auxMap.put("ts_pdf_sha1", "");
        auxMap.put("nr_paginas", "");
        auxMap.put("st_removido", "0");
//        String update = "insert into tb_ged_pdf ()"
//        		" values ()"
        String ocrType = wiMap.get("tmp.persistpdf.ocr_type");
        if (ocrType.equalsIgnoreCase("wait")) {
        	// processar o OCR
        }
        Function.removeDir(pdfFile.getParentFile().getAbsolutePath());
        return 0;
	}
		
	public JavaParameter[] getInputParameters() {
		JavaParameter[] in = new JavaParameter[7];
		in[0] = new JavaParameter("tmp.persistpdf.database", "BD (principal)");
		in[1] = new JavaParameter("tmp.persistpdf.table", "Tabela");
		in[2] = new JavaParameter("tmp.persistpdf.pkey", "Chave Primaria)");
		in[3] = new JavaParameter("tmp.persistpdf.pdf_field", "Campo PDF");
		in[4] = new JavaParameter("tmp.persistpdf.form_field", "Campo Form");
		in[5] = new JavaParameter("tmp.persistpdf.ocr_type", "Tipo de OCR (None,Wait,Thread)");
		in[6] = new JavaParameter("tmp.persistpdf.resp", "Variável Resposta");
		return in;
	}

	public JavaParameter[] getOutputParameters() {
		return new JavaParameter[0];
	}
	
}
