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

import java.util.ArrayList;
import java.util.List;

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

public class Remove extends AbstractConnectorDB implements InterfaceParameters {

	public void execute(WIMap wiMap, DatabaseAliases databases, 
			InterfaceHeaders headers) throws UserException {
		String className = getClass().getName();
		DatabaseHandler dh = null;
		try {
			String database = wiMap.get("tmp.remove.database").trim();
			if (database.equals("")) database = "principal";
			dh = databases.get(database);
			if (dh == null) {
				String msg = "Get database error (" + database + ")";
				String pageId = wiMap.get("wi.page.id");
				getParams().getErrorLog().write(className, "Page: " + pageId, msg);
				return;
			}
			WIMap auxMap = new WIMap();
			String key1 = wiMap.get("tmp.remove.key1").trim();
			auxMap.put("key1", Function.parseInt(key1) + "");
			String key2 = wiMap.get("tmp.remove.key2").trim();
			auxMap.put("key2", Function.parseInt(key2) + "");
			String table = wiMap.get("tmp.remove.table");
			NodeTable nodeTable = getNodeTable(dh, table);
			List keys = new ArrayList(nodeTable.getPrimaryKeys().keySet());
			int respCode = 0;
			if (nodeTable.hasColumn("st_removido")) {
				boolean hasAtivo = nodeTable.hasColumn("st_ativo");
				String remove = "update " + table + " set st_removido=true";
				if (hasAtivo) remove += ", st_ativo=false";
				remove += " where " + keys.get(0) + " = ?|key1|";
				respCode = dh.executeUpdate(remove, auxMap);
			} else {
				String remove = "delete from " + table + " where ";
				for (int i = 0; i < keys.size(); i++) {
					String key = (String) keys.get(i);
					if (i > 0) remove += " and "; 
					remove += key + " = ?|key" + (i+1) + "|";
				}
				respCode = dh.executeUpdate(remove, auxMap);
			}
			String respVar = wiMap.get("tmp.remove.resp").trim();
			if (!respVar.equals("")) {
				if (respCode >= 0) {
					wiMap.put(respVar + ".ok()", "true");
					wiMap.put(respVar, "Registro removido com sucesso");
				} else {
					wiMap.put(respVar + ".ok()", "false");
					wiMap.put(respVar, dh.getErrorMessage());
				}
			}
			// Recuperando a chave randomica
			if (nodeTable.hasColumn("ts_rnd_key")) {
				String varTarget = respVar + ".ts_rnd_key";
				DBUtil.loadRndKey(wiMap, dh, nodeTable, "tmp.remove.key1", varTarget);
			}			
			// Removendo busca textual
			if (keys.size() == 1) {
				WIMap auxMap2 = new WIMap();
				auxMap2.put("wi.updatelog", "true");
				auxMap2.put("colecao", table.replace("tb_", "").trim());
				auxMap2.put("ident", key1);
				String call = "call sp_base_textual(?|colecao|,?|ident|,0,null)";
				dh.executeUpdate(call, auxMap2);
			}	
			// Auditoria do LWSolution
			wiMap.put("tmp.audit.database", database);
			wiMap.put("tmp.audit.table", table);
			wiMap.put("tmp.audit.type", "R");
			wiMap.put("tmp.audit.key1", key1);
			wiMap.put("tmp.audit.key2", key2);
			new Audit().execute(getParams());
		} catch (Exception err) {
			String pageId = wiMap.get("wi.page.id");
			String table = wiMap.get("tmp.remove.table");
			String message = "Page: " + pageId + ", Table: " + table;
			getParams().getErrorLog().write(className, message, err);
		}
	}
	
	public JavaParameter[] getInputParameters() {
		JavaParameter[] in = new JavaParameter[5];
		in[0] = new JavaParameter("tmp.remove.database", "BD (principal)");
		in[1] = new JavaParameter("tmp.remove.table", "Tabela");
		in[2] = new JavaParameter("tmp.remove.key1", "Registro 1 (chave primaria)");
		in[3] = new JavaParameter("tmp.remove.key2", "Registro 2 (chave primaria)");
		in[4] = new JavaParameter("tmp.remove.resp", "Variável Resposta");
		return in;
	}

	public JavaParameter[] getOutputParameters() {
		return new JavaParameter[0];
	}
	
}
