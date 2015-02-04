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

package br.com.webinside.runtime.function;

import java.io.PrintWriter;
import java.sql.Types;

import javax.servlet.http.HttpServletResponse;

import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.integration.AbstractConnector;
import br.com.webinside.runtime.integration.DatabaseAliases;
import br.com.webinside.runtime.integration.DatabaseHandler;
import br.com.webinside.runtime.integration.InterfaceHeaders;
import br.com.webinside.runtime.integration.InterfaceParameters;
import br.com.webinside.runtime.integration.JavaParameter;
import br.com.webinside.runtime.util.I18N;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

/**
 * Conector que gera um arquivo CSV a partir de um comando sql.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.1 $
 */
public class SqlToCSV extends AbstractConnector implements InterfaceParameters {

	public boolean exit() {
		return true;
	}

	public void execute(WIMap wiMap, DatabaseAliases databases,
			InterfaceHeaders headers) throws UserException {
		String className = getClass().getName();
		DatabaseHandler dh = null;
		try {
			String database = wiMap.get("tmp.sqltocsv.database").trim();
			if (database.equals("")) database = "principal";
			dh = databases.get(database);
			if (dh == null) {
				String msg = "Get database error (" + database + ")";
				String pageId = wiMap.get("wi.page.id");
				getParams().getErrorLog().write(className, "Page: " + pageId, msg);
				return;
			}
			String queryvar = wiMap.get("tmp.sqltocsv.queryvar").trim();
			String query = wiMap.get(queryvar).trim();
			dh.setCharFilter("", "");
			execute(dh, wiMap, query);
		} catch (Exception err) {
			err.printStackTrace();
			String pageId = wiMap.get("wi.page.id");
			getParams().getErrorLog().write(className, "Page: " + pageId, err);
		}
	}
	
	private void execute(DatabaseHandler dh, WIMap wiMap, String query) 
	throws Exception {
        String format = wiMap.get("tmp.sqltocsv.format");
        String csvSep = (format.equals("US") ? "," : ";");
		HttpServletResponse response = getParams().getHttpResponse();
        response.setContentType("application/octetstream");
		String fname = wiMap.get("tmp.sqltocsv.name");
		if (fname.equals("")) fname = "dados";
        String dispname = "attachment; filename=\"" + fname + ".csv\"";
        response.setHeader("Content-disposition", dispname);
		PrintWriter out = new PrintWriter(getWriter());
		ResultSet rs = dh.execute(query, wiMap);
		String names[] = rs.columnNames();
		for (int i = 0; i < names.length; i++) {
			if (i > 0) out.print(csvSep);
			String name = names[i];
			if (wiMap.get("tmp.sqltocsv.label_utf8").equalsIgnoreCase("true")) {
				name = new String(name.getBytes(), "utf-8");
			}
			out.print(filter(wiMap, csvSep, Types.VARCHAR, name));
		}
		out.println();
		out.flush();
		while (rs.next() > 0) {
			for (int i = 0; i < names.length; i++) {
				int type = Types.VARCHAR;
				if (rs.getMetaData() != null) {
					type = 	rs.getMetaData().getColumnType(i + 1);
				}
				if (i > 0) out.print(csvSep);
				out.print(filter(wiMap, csvSep, type, rs.column(i + 1)));
			}
			out.println();
			out.flush();
		}
	}
	
	private String filter(WIMap wiMap, String csvSep, int type, String value) {
        if (value.indexOf("\"") != -1) {
            value = StringA.change(value, "\"", "\"\"");
        }
        if (value.indexOf(csvSep) != -1) {
            value = "\"" + value + "\"";
        }
        if (isDate(type)) {
        	value = dateFormat(wiMap, value, false);
        }
        if (isTimestamp(type)) {
        	value = dateFormat(wiMap, value, true);
        }
        if (isNumber(type)) {
            if (csvSep.equals(";")) {
                value = value.replace('.', ',');
            }
        }
        return value;
	}

	public boolean isDate(int type) {
        return (type == Types.DATE);
    }

    public boolean isTimestamp(int type) {
        return (type == Types.TIMESTAMP);
    }
    
    public boolean isNumber(int type) {
        return ((type == Types.BIGINT) || (type == Types.DECIMAL)
                || (type == Types.DOUBLE) || (type == Types.FLOAT)
                || (type == Types.INTEGER) || (type == Types.NUMERIC)
                || (type == Types.REAL) || (type == Types.SMALLINT)
                || (type == Types.TINYINT));
    }

    private String dateFormat(WIMap wiMap, String value, boolean time) {
        String[] param = new String[4];
        param[0] = value;
        param[1] = "FMT";
        param[2] = "yyyy-MM-dd";
        if (time) {
            param[2] = "yyyy-MM-dd HH:mm:ss";
        }
        param[3] = i18n(wiMap, "dd/MM/yyyy");
        if (time) {
            param[3] = i18n(wiMap, "dd/MM/yyyy HH:mm:ss");
        }
        return new br.com.webinside.runtime.function.DateFormat().execute(param);
    }
    
    public String i18n(WIMap wiMap, String text) {
        I18N i18n = (I18N) wiMap.getObj("wi.i18n");
        return i18n.get(text);
    }

	public JavaParameter[] getInputParameters() {
		JavaParameter[] in = new JavaParameter[5];
		in[0] = new JavaParameter("tmp.sqltocsv.database", "BD (principal)");
		in[1] = new JavaParameter("tmp.sqltocsv.queryvar", "Variável do SQL");
		in[2] = new JavaParameter("tmp.sqltocsv.name", "Nome do CSV (dados)");
		in[3] = new JavaParameter("tmp.sqltocsv.format", "Formato (BR ou US)");
		in[4] = new JavaParameter("tmp.sqltocsv.label_utf8", "Label UTF8");
		return in;
	}

	public JavaParameter[] getOutputParameters() {
		return new JavaParameter[0];
	}
	
}
