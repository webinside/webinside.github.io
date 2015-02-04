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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.integration.AbstractConnector;
import br.com.webinside.runtime.integration.DatabaseAliases;
import br.com.webinside.runtime.integration.DatabaseHandler;
import br.com.webinside.runtime.integration.InterfaceHeaders;
import br.com.webinside.runtime.integration.InterfaceParameters;
import br.com.webinside.runtime.integration.JavaParameter;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

public class Recursive extends AbstractConnector implements InterfaceParameters {

	private static Map syncMap = Collections.synchronizedMap(new HashMap());
	
	public void execute(WIMap wiMap, DatabaseAliases databases, 
			InterfaceHeaders headers) throws UserException {
		String className = getClass().getName();
		DatabaseHandler dh = null;
		boolean commit = false;
		try {
			String projId = wiMap.get("wi.proj.id").trim();
			String database = wiMap.get("tmp.persist.database").trim();
			if (database.equals("")) {
				database = "principal";
			}
			dh = databases.get(database);
			if (dh == null) {
				String pageId = wiMap.get("wi.page.id");
				String msg = "Get database error (" + database + ")";
				getParams().getErrorLog().write(className, "Page: " + pageId, msg);
				return;
			}
			if (dh.isAutocommit()) {
				dh.autocommit(false);
				commit = true;
			}
			String table = wiMap.get("tmp.persist.table").trim();
			String type = wiMap.get("tmp.persist.type").trim();
			if (type.equals("")) type = "save";
			int recId = Function.parseInt(wiMap.get("tmp.persist.rec_id").trim());
			if (recId < 1 && !type.equalsIgnoreCase("order")) {
				String pageId = wiMap.get("wi.page.id");
				String msg = "Recursivo ID cannot be zero in " + table;
				getParams().getErrorLog().write(className, "Page: " + pageId, msg);
				return;
			}
			String mapKey = projId + "-" + database + "-" + table;
			synchronized (syncMap) {
				if (!syncMap.containsKey(mapKey)) {
					syncMap.put(mapKey, new Object());
				}
			}
			synchronized (syncMap.get(mapKey)) {
				if (type.equalsIgnoreCase("save")) {
					save(wiMap, dh, table);
					String cOrd = wiMap.get("tmp.persist.rec_col_order");
					if (!cOrd.trim().equals("")) {
						columnOrder(wiMap, dh, table, cOrd);
					}
				} else if (type.equalsIgnoreCase("remove")) {
					remove(wiMap, dh, table);
				} else if (type.equalsIgnoreCase("removeall")) {
					removeall(wiMap, dh, table);
				} else if (type.equalsIgnoreCase("order")) {
					order(wiMap, dh, table);
				}
			}
			if (dh != null && commit) dh.commit();
		} catch (Exception err) {
			if (dh != null && commit) dh.rollback();
			String pageId = wiMap.get("wi.page.id");
			String table = wiMap.get("tmp.persist.table");
			String message = "Page: " + pageId + ", Table: " + table;
			getParams().getErrorLog().write(className, message, err);
		} finally {
			if (dh != null && commit) {
				dh.autocommit(true);
			}
		}
	}
	
	private void save(WIMap wiMap, DatabaseHandler dh, String table) 
	throws Exception {
		int nrId = Function.parseInt(wiMap.get("tmp.persist.rec_nr").trim());
		String sql = "select * from " + table + 
			" where id_recursivo = ?|tmp.persist.rec_id|";
		ResultSet rs = dh.execute(sql, wiMap);
		if (rs.next() > 0) {
			sql = "update " + table + " set " +
			"fk_recursivo_pai = ?[nul]|tmp.persist.rec_pai|, ";
			if (nrId > 0) sql += "nr_recursivo = ?|tmp.persist.rec_nr|, ";
			sql += "nr_repositorio = ?|tmp.persist.rec_rep_nr| " +
			"where id_recursivo = ?|tmp.persist.rec_id|";
		} else {
			if (nrId == 0) {
				String sql2 = "select ifnull(max(nr_recursivo),0) + 1 from " + table 
					+ " where ifnull(fk_recursivo_pai,'') = ?|tmp.persist.rec_pai|" 
					+ " and nr_repositorio = ?|tmp.persist.rec_rep_nr|";
				ResultSet rs2 = dh.execute(sql2, wiMap);
				rs2.next();
				wiMap.put("tmp.persist.rec_nr", rs2.column(1) + "");
			}
			sql = "insert into " + table + " values " +
			"(?|tmp.persist.rec_id|,?[nul]|tmp.persist.rec_pai|," +
			"?|tmp.persist.rec_nr|,?|tmp.persist.rec_rep_nr|,'','','')";
		}
		dh.executeUpdate(sql, wiMap);
		updateMe(wiMap, dh, table, wiMap.get("tmp.persist.rec_id"));
		updateChildren(wiMap, dh, table, wiMap.get("tmp.persist.rec_id"));
	}

	private void remove(WIMap wiMap, DatabaseHandler dh, String table) 
	throws Exception {
		// localiza os filhos dessa chave
		String[] children = new String[0];
		String sql = "select group_concat(id_recursivo) from " + table + 
			" where find_in_set(?|tmp.persist.rec_id|,ts_recursivo_pai) > 0" +
			" order by ts_ordem";
		ResultSet rs = dh.execute(sql, wiMap);
		if (rs.next() > 0) {
			children = rs.column(1).split(",");
		}
		// ajusta os filhos diretos
		String auxtable = "(select * from " + table + ") t2";
		sql = "update " + table + " t1 set fk_recursivo_pai = " +
				"(select fk_recursivo_pai from " + auxtable + " where" +
				" t2.id_recursivo = ?|tmp.persist.rec_id|) " +
				"where fk_recursivo_pai = ?|tmp.persist.rec_id|";
		dh.executeUpdate(sql, wiMap);
		// remove a chave 
		sql = "delete from " + table + " where id_recursivo = ?|tmp.persist.rec_id|";
		dh.executeUpdate(sql, wiMap);
		// atualiza os filhos
		for (int i = 0; i < children.length; i++) {
			updateMe(wiMap, dh, table, children[i]);
		}
	}

	// Para localizar os IDs removidos é só verificar os IDS que existem na 
	// tabela principal e não existem na recursividade
	private void removeall(WIMap wiMap, DatabaseHandler dh, String table) 
	throws Exception {
		String sql = "delete from " + table + " where id_recursivo = ?|tmp.persist.rec_id|" +
				" or find_in_set(?|tmp.persist.rec_id|,ts_recursivo_pai) > 0" +
				" order by ts_ordem desc";
		dh.executeUpdate(sql, wiMap);
	}

	private void columnOrder(WIMap wiMap, DatabaseHandler dh, String table, String col) 
	throws Exception {
		String tabAux = StringA.change(table, "_recursivo", "");
		String idAux = StringA.change(tabAux, "tb_", "id_");
		String sql = "select id_recursivo from " + tabAux + " t1 " 
				+ " inner join " + table + " t2 on (" + idAux + " = id_recursivo) " 
				+ " where ifnull(fk_recursivo_pai,'') = ?|tmp.persist.rec_pai|" 
				+ " and nr_repositorio = ?|tmp.persist.rec_rep_nr|"
				+ " order by t1." + col;
		ResultSet rs = dh.execute(sql, wiMap);
		StringBuilder resp = new StringBuilder();
		int count = 1;
		while (rs.next() > 0) {
			if (resp.length() > 0) resp.append(",");
			resp.append(rs.column(1) + "-" + count);
			count++;
		}
		wiMap.put("tmp.persist.rec_order", resp.toString());
		order(wiMap, dh, table);
	}
	
	private void order(WIMap wiMap, DatabaseHandler dh, String table) 
	throws Exception {
		String[] nodes = wiMap.get("tmp.persist.rec_order").split(",");
		for (int i = 0; i < nodes.length; i++) {
			String node = nodes[i].trim();
			if (node.indexOf("-") > -1) {
				String[] nodeList = node.split("-");
				if (nodeList.length > 1) {
					WIMap auxMap = new WIMap();
					int id = Function.parseInt(nodeList[0].trim());
					auxMap.put("tmp.rec_id", id + "");
					int nr = Function.parseInt(nodeList[1].trim());
					auxMap.put("tmp.rec_nr", nr + "");
					if (id > 0) {
						String sql = "update " + table + 
						" set nr_recursivo = ?|tmp.rec_nr|" +
						" where id_recursivo = ?|tmp.rec_id|";
						dh.executeUpdate(sql, auxMap);
						updateMe(wiMap, dh, table, id + "");
						updateChildren(wiMap, dh, table, id + "");
					}
				}
			}
		}
	}

	private void updateMe(WIMap wiMap, DatabaseHandler dh, 
			String table, String recId) throws Exception {
		WIMap auxMap = new WIMap();
		auxMap.put("tmp.rec_id", recId);
		String auxtable = "(select * from " + table + ") t2";
		String sql = "update " + table + " t1 set " +
		"ts_ordem = (select concat(t2.ts_ordem,lpad(t1.nr_recursivo,5,0)) from " + auxtable + 
		" where t2.id_recursivo = t1.fk_recursivo_pai union select lpad(t1.nr_recursivo,5,0) limit 1), " +
		"ts_recursivo = (select concat_ws('.',t2.ts_recursivo,t1.nr_recursivo) from " + auxtable + 
		" where t2.id_recursivo = t1.fk_recursivo_pai union select t1.nr_recursivo limit 1), " +
		"ts_recursivo_pai = (select if(t2.ts_recursivo_pai='',t1.fk_recursivo_pai,concat_ws(',',t1.fk_recursivo_pai,t2.ts_recursivo_pai))" +
		" from " + auxtable + " where t2.id_recursivo = t1.fk_recursivo_pai union select '' limit 1) " +
		"where id_recursivo = ?|tmp.rec_id|";
		dh.executeUpdate(sql, auxMap);		
	}
	
	private void updateChildren(WIMap wiMap, DatabaseHandler dh, 
			String table, String recId) throws Exception {
		String[] children = new String[0];
		WIMap auxMap = new WIMap();
		auxMap.put("tmp.rec_id", recId);
		String sql = "select group_concat(id_recursivo) from " + table + 
			" where find_in_set(?|tmp.rec_id|,ts_recursivo_pai) > 0" +
			" order by ts_ordem";
		ResultSet rs = dh.execute(sql, auxMap);
		if (rs.next() > 0) {
			children = rs.column(1).split(",");
		}
		for (int i = 0; i < children.length; i++) {
			updateMe(wiMap, dh, table, children[i]);
		}
	}
	
	public JavaParameter[] getInputParameters() {
		JavaParameter[] in = new JavaParameter[9];
		in[0] = new JavaParameter("tmp.persist.database", "BD (principal)");
		in[1] = new JavaParameter("tmp.persist.table", "Tabela");
		in[2] = new JavaParameter("tmp.persist.type", "Tipo (save/remove/removeall/order)");
		in[3] = new JavaParameter("tmp.persist.rec_id", "Recursivo ID");
		in[4] = new JavaParameter("tmp.persist.rec_pai", "Recursivo Pai");
		in[5] = new JavaParameter("tmp.persist.rec_nr", "Recursivo NR");
		in[6] = new JavaParameter("tmp.persist.rec_rep_nr", "Repositório NR");
		in[7] = new JavaParameter("tmp.persist.rec_col_order", "Ordenar pela Coluna");
		in[8] = new JavaParameter("tmp.persist.rec_order", "Lista de Ordem (ID-NR,...,ID-NR)");
		return in;
	}

	public JavaParameter[] getOutputParameters() {
		return new JavaParameter[0];
	}
	
}
