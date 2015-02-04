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

import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.integration.AbstractConnector;
import br.com.webinside.runtime.integration.DatabaseAliases;
import br.com.webinside.runtime.integration.DatabaseHandler;
import br.com.webinside.runtime.integration.InterfaceHeaders;
import br.com.webinside.runtime.integration.InterfaceParameters;
import br.com.webinside.runtime.integration.JavaParameter;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.WIMap;

public class Audit extends AbstractConnector implements InterfaceParameters {

	public void execute(WIMap wiMap, DatabaseAliases databases, 
			InterfaceHeaders headers) throws UserException {
		String className = getClass().getName();
		DatabaseHandler dh = null;
		try {
			String database = wiMap.get("tmp.audit.database").trim();
			if (database.equals("")) database = "principal";
			dh = databases.get(database);
			if (dh == null) {
				String msg = "Get database error (" + database + ")";
				String pageId = wiMap.get("wi.page.id");
				getParams().getErrorLog().write(className, "Page: " + pageId, msg);
				return;
			}
			WIMap auxMap = new WIMap();
			auxMap.put("wi.updatelog", "true");
			auxMap.put("ip", wiMap.get("wi.session.ip"));
			auxMap.put("session", wiMap.get("wi.session.id"));
			String table = wiMap.get("tmp.audit.table").trim();
			auxMap.put("table", table.replace("tb_", "").trim());
			String key1 = wiMap.get("tmp.audit.key1").trim();
			auxMap.put("key1", Function.parseInt(key1) + "");
			String key2 = wiMap.get("tmp.audit.key2").trim();
			auxMap.put("key2", Function.parseInt(key2) + "");
			int user = Function.parseInt(wiMap.get("pvt.login.id_usuario").trim());
			auxMap.put("user", (user == 0 ? "null" : user + ""));
			String type = wiMap.get("tmp.audit.type").toUpperCase().trim();
			if ("CUAR".indexOf(type) > -1) {
				auxMap.put("type", type);
				String naoVigente = "update tb_base_auditoria set st_vigente = false " +
						"where ts_tabela = ?|table| and nr_registro1 = ?|key1| and " +
						"nr_registro2 = ?|key2| and tp_auditoria = ?|type|";
				dh.executeUpdate(naoVigente, auxMap);
				String insert = "insert into tb_base_auditoria " +
						"(ts_tabela, nr_registro1, nr_registro2, tp_auditoria, " +
						"dt_auditoria, ts_end_ip, ts_sessao, fk_usuario, st_vigente) " +
						"values (?|table|, ?|key1|, ?|key2|, ?|type|, now(), ?|ip|," +
						"?|session|, ?|user|, true)";
				dh.executeUpdate(insert, auxMap);
			}
		} catch (Exception err) {
			String pageId = wiMap.get("wi.page.id");
			String table = wiMap.get("tmp.audit.table");
			String message = "Page: " + pageId + ", Table: " + table;
			getParams().getErrorLog().write(className, message, err);
		}
	}
		
	public JavaParameter[] getInputParameters() {
		JavaParameter[] in = new JavaParameter[5];
		in[0] = new JavaParameter("tmp.audit.database", "BD (principal)");
		in[1] = new JavaParameter("tmp.audit.table", "Tabela");
		in[2] = new JavaParameter("tmp.audit.key1", "Registro 1 (chave primaria)");
		in[3] = new JavaParameter("tmp.audit.key2", "Registro 2 (chave primaria)");
		in[4] = new JavaParameter("tmp.audit.type", "Tipo (Create, Upd, Aut/Acc, Remove)");
		return in;
	}

	public JavaParameter[] getOutputParameters() {
		return new JavaParameter[0];
	}
	
}
