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

package br.com.webinside.runtime.function.database;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import br.com.webinside.runtime.core.EngFunction;
import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.database.impl.ConnectionSql;
import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.function.DateFormat;
import br.com.webinside.runtime.function.TextFormat;
import br.com.webinside.runtime.integration.DatabaseAliases;
import br.com.webinside.runtime.integration.DatabaseHandler;
import br.com.webinside.runtime.integration.InterfaceHeaders;
import br.com.webinside.runtime.integration.InterfaceParameters;
import br.com.webinside.runtime.integration.JavaParameter;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

public class Persist extends AbstractConnectorDB implements InterfaceParameters {
	
	public void execute(WIMap wiMap, DatabaseAliases databases, 
			InterfaceHeaders headers) throws UserException {
		String className = getClass().getName();
		DatabaseHandler dh = null;
		try {
			String reset = wiMap.get("tmp.persist.reset").trim();
			if (reset.equalsIgnoreCase("true")) doReset(wiMap);
			String database = wiMap.get("tmp.persist.database").trim();
			if (database.equals("")) {
				database = "principal";
			}
			dh = databases.get(database);
			if (dh == null) {
				String msg = "Get database error (" + database + ")";
				String pageId = wiMap.get("wi.page.id");
				getParams().getErrorLog().write(className, "Page: " + pageId, msg);
				return;
			}
			String table = wiMap.get("tmp.persist.table").trim();
			String variable = wiMap.get("tmp.persist.object").trim();
			if (!variable.equals("")) {
				execute(wiMap, dh, table, variable);
			}
		} catch (Exception err) {
        	EngFunction.invalidateTransaction(wiMap, err.toString());
			String pageId = wiMap.get("wi.page.id");
			String table = wiMap.get("tmp.persist.table");
			String message = "Page: " + pageId + ", Table: " + table;
			getParams().getErrorLog().write(className, message, err);
			if (dh != null && !dh.getErrorMessage().equals("")) {
				String resp = wiMap.get("tmp.persist.resp").trim();
				wiMap.put(resp + ".ok()", "false");
				wiMap.put(resp, dh.getErrorMessage());
			}
		}
	}
	
	private void execute(WIMap wiMap, DatabaseHandler dh, String table, String variable) 
	throws Exception {
		boolean insert = true;
		StringBuffer whereSql = new StringBuffer(); 
		NodeTable nodeTable = getNodeTable(dh, table);
		List keys = new ArrayList(nodeTable.getPrimaryKeys().keySet());
		for (Iterator it = keys.iterator(); it.hasNext();) {
			String key = (String) it.next();
			if (whereSql.length() > 0) {
				whereSql.append(" and ");
			}
			whereSql.append(key  + " = ?|" + variable + "." + key + "|");
		}
		String query = "select 'true' as found from " + table + " where " + whereSql;
		ResultSet rsInsert = dh.execute(query , wiMap);
		if (rsInsert.next() > 0) insert = false;
		doBeforePersist(wiMap, dh, insert);
		StringBuffer insertKeySql = new StringBuffer();
		StringBuffer insertValueSql = new StringBuffer(); 
		StringBuffer updateSql = new StringBuffer(); 
		List columns = nodeTable.getColumns();
		for (Iterator it = columns.iterator(); it.hasNext();) {
			NodeColumn column = (NodeColumn) it.next();
			String type = column.getType().toLowerCase();
			if (type.indexOf("unsigned") > -1) {
				type = type.substring(0, type.indexOf("unsigned")).trim();
			}			
			String varName = variable + "." + column.getName();
			if (insert && !wiMap.containsKey(varName)) {
				if ((type.equals("char") || type.equals("varchar")) 
						&& !column.isNullable()) {
					wiMap.put(varName, "");
				}
				if (type.equals("bit") || type.startsWith("int") ||
						type.equals("float") || type.equals("decimal")) {
					wiMap.put(varName, "");
				}
			}
			if (!wiMap.containsKey(varName) || type.indexOf("blob") > -1) continue;
			if (insert && column.isAutoIncrement()) continue;
			if (!insert && keys.contains(column.getName())) continue;
			String value = filter(wiMap, variable, column);
			// insert key
			if (insertKeySql.length() > 0) {
				insertKeySql.append(", ");
			}
			insertKeySql.append(column.getName());
			// insert value
			if (insertValueSql.length() > 0) {
				insertValueSql.append(", ");
			}
			insertValueSql.append(value);
			// update
			if (updateSql.length() > 0) {
				updateSql.append(", ");
			}
			updateSql.append(column.getName() + " = " + value);
		}
		query = "";
		if (insert) {
			query = "insert into " + table + " (" + insertKeySql + ") " +
					"values (" + insertValueSql + ")";
		} else {
			query = "update " + table + " set " + updateSql + 
				" where " + whereSql;
		}
		String debug = wiMap.get("tmp.persist.debug").trim();
		if (debug.equalsIgnoreCase("true")) {
			wiMap.put("tmp.persist.statement", query);
		}
		ConnectionSql dbSql = (ConnectionSql)dh.getDatabaseConnection();
		int respCode = -1;
		// Não deve processar quando update quando a tabela 
		// contem apenas com as chaves primarias
		if (insert || updateSql.length() > 0) {
			wiMap.remove("wi.transaction.none");
			if (insert) dbSql.returnGeneratedKeys();
	        try {
				respCode = dh.executeUpdate(query, wiMap);
	        } catch (Exception err) {
	        	queryException(wiMap, err, dh);
	        	respCode = EngFunction.errorCodeSQL(err);
	        }
			if (respCode < 0) {
	        	EngFunction.invalidateTransaction(wiMap, dh.getErrorMessage());
	            dh.updateLog(wiMap, false);
			}	
		}
		if (insert && respCode >= 0) {
			// recuperar chave inserida pelo generatedKeys
			ResultSet rsKey = dbSql.getGeneratedKeys();
			if (rsKey.next() > 0) {
				for (int i = 0; i < keys.size(); i++) {
					String key = (String) keys.get(i);
					wiMap.put(variable + "." + key, rsKey.column(i + 1));
				}
			}
		}
		if (respCode >= 0) doAfterPersistOk(wiMap, dh, insert);
		String targetVar = wiMap.get("tmp.persist.target_var").trim();
		if (!targetVar.equals("")) {
			String targetValue = wiMap.get(variable + "." + keys.get(0));
			wiMap.put(targetVar, targetValue);
		}
		String resp = wiMap.get("tmp.persist.resp").trim();
		String msgInsert = wiMap.get("tmp.persist.msg.insert").trim();
		if (msgInsert.equals("")) {
			msgInsert = "Registro inserido com sucesso";
		}
		String msgUpdate = wiMap.get("tmp.persist.msg.update").trim();
		if (msgUpdate.equals("")) {
			msgUpdate = "Registro atualizado com sucesso";
		}
		if (!resp.equals("")) {
			if (respCode >= 0) {
				wiMap.put(resp + ".ok()", "true");
				if (insert) {
					wiMap.put(resp, msgInsert);
					wiMap.put(resp + ".insert()", "true");
				} else {
					wiMap.put(resp, msgUpdate);
					wiMap.put(resp + ".insert()", "false");
				}	
			} else {
				wiMap.put(resp + ".ok()", "false");
				wiMap.put(resp, dh.getErrorMessage());
			}
		}
	}

	protected void doBeforePersist(WIMap wiMap, DatabaseHandler dh, boolean insert)
	throws Exception {
		// Persist da LINEWEB sobrescreve esse método
	}

	protected void doAfterPersistOk(WIMap wiMap, DatabaseHandler dh, boolean insert)
	throws Exception {
		// Persist da LINEWEB sobrescreve esse método
	}
	
	private String filter(WIMap wiMap, String variable, NodeColumn column) {
		String token = variable + "." + column.getName();
		String tokenValue = wiMap.get(token).trim();
		String fArgs[] = {tokenValue, "msword"};
		String filterValue = new TextFormat().execute(fArgs);
		if (!tokenValue.equals(filterValue)) {
			wiMap.put(token, filterValue);
			tokenValue = filterValue;
		}
		String value = "?|" + token + "|";
		String type = column.getType().toLowerCase();
		if (type.indexOf("unsigned") > -1) {
			type = type.substring(0, type.indexOf("unsigned")).trim();
		}
		if (tokenValue.equals("null")) {
			value = "null";
		}
		if (type.equals("date") || type.equals("datetime")) {
			if (tokenValue.equals("")) {
				value = "null";
			} else if (tokenValue.equals("now")) {
				value = "current_timestamp";
			} else if (tokenValue.indexOf("/") > -1) {
				String mask = type.equals("date") ? "ymd" : "ymdhms";
				value = "?|" + token + "." + mask + "|";
				String[] args = {tokenValue,"FMT" + mask};
				try {
					String resp = new DateFormat().execute(args);
					if (resp.equals("")) resp = "null";
					wiMap.put(token + "." + mask, resp);
				} catch (Exception err) {
					value = "null";
				}
			}
		} else if (type.startsWith("int") ||
					type.equals("float") || 
					type.equals("decimal")) {
			if (tokenValue.equals("")) {
				value = (column.isNullable() ? "null" : "0");
			} else if (tokenValue.indexOf(',') > -1) {
				value = "?|" + token + ".clr|";
				String aux = StringA.changeChars(tokenValue, ",.", ".");
				wiMap.put(token + ".clr", aux);
			}
		} else if (type.equals("bit")) {
			value = (tokenValue.equals("1") ? "1" : "0");
		}
		return value;
	}
		
    private void queryException(WIMap wiMap, Exception ex, DatabaseHandler db) {
        wiMap.put("wi.sql.query", db.getExecutedSQL());
        wiMap.put("wi.sql.error", db.getErrorMessage());
        String jspFile = wiMap.get("wi.jsp.filename");
        String description = "Persist " + wiMap.get("tmp.persist.table");
        String msgDetail = db.getErrorMessage() + 
        	"\r\n--- SQL ---\r\n" + db.getExecutedSQL();
        WIMap psMap = db.getExecutedSQLParams(wiMap); 
        if (psMap.keySet().size() > 0) {
        	msgDetail += "\r\n--- PARAMS ---\r\n";
        	msgDetail += psMap.toString();
        }	
        getParams().getErrorLog().write("Page: " + jspFile, description, msgDetail);
        if (!getParams().getPage().getErrorPageName().equals("")) {
        	getParams().setRequestAttribute("wiException", ex);
        }
    }    
		
	public JavaParameter[] getInputParameters() {
		JavaParameter[] in = new JavaParameter[9];
		in[0] = new JavaParameter("tmp.persist.database", "BD (principal)");
		in[1] = new JavaParameter("tmp.persist.table", "Tabela");
		in[2] = new JavaParameter("tmp.persist.object", "Variável Objeto");
		in[3] = new JavaParameter("tmp.persist.resp", "Variável Resposta");
		in[4] = new JavaParameter("tmp.persist.msg.insert", "Inserção OK");
		in[5] = new JavaParameter("tmp.persist.msg.update", "Atualização OK");
		in[6] = new JavaParameter("tmp.persist.target_var", "Variável Destino do ID");
		in[7] = new JavaParameter("tmp.persist.debug", "Ativar o modo debug");
		in[8] = new JavaParameter("tmp.persist.reset", "Resetar tabelas");
		return in;
	}

	public JavaParameter[] getOutputParameters() {
		return new JavaParameter[0];
	}
	
}
