package br.com.webinside.runtime.lw.sql;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;

import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.function.database.NodeTable;
import br.com.webinside.runtime.integration.DatabaseHandler;
import br.com.webinside.runtime.integration.JavaParameter;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

public class Persist extends br.com.webinside.runtime.function.database.Persist {

	@Override
	protected void doBeforePersist(WIMap wiMap, DatabaseHandler dh, boolean insert)
	throws Exception {
		String table = wiMap.get("tmp.persist.table").trim();
		String variable = wiMap.get("tmp.persist.object").trim();
		NodeTable nodeTable = getNodeTable(dh, table);
		List<String> keys = new ArrayList(nodeTable.getPrimaryKeys().keySet());
		if (nodeTable.hasColumn("fk_empresa")) {
			String varName = variable + ".fk_empresa";
			if (wiMap.get(varName).equals("")) {
				wiMap.put(varName, wiMap.get("pvt.id_empresa"));
			}
		}
		wiMap.remove(variable + ".ts_rnd_key");
		wiMap.remove(variable + ".ts_rnd_key_old");
		if (nodeTable.hasColumn("ts_rnd_key")) {
			String stmp = StringA.change(variable, "tmp.", "stmp.");
			String updRndKey = wiMap.get(stmp + ".ts_rnd_key").toLowerCase();
			if (insert || updRndKey.equals("update") || updRndKey.length() == 20) {
				if (!insert) {
					String varKey = variable + "." + keys.get(0);
					String varTarget = variable + ".ts_rnd_key_old";
					DBUtil.loadRndKey(wiMap, dh, nodeTable, varKey, varTarget);
				}
				String newRndKey = Function.randomKey().toLowerCase();
				if (updRndKey.length() == 20) newRndKey = updRndKey;
				wiMap.put(variable + ".ts_rnd_key", newRndKey);
			}
		}	
	}
	
	@Override
	protected void doAfterPersistOk(WIMap wiMap, DatabaseHandler dh, boolean insert)
	throws Exception {
		String table = wiMap.get("tmp.persist.table").trim();
		String variable = wiMap.get("tmp.persist.object").trim();
		NodeTable nodeTable = getNodeTable(dh, table);
		List<String> keys = new ArrayList(nodeTable.getPrimaryKeys().keySet());
		// Recuperando a chave randomica
		String varRndKey = variable + ".ts_rnd_key";
		if (nodeTable.hasColumn("ts_rnd_key") && wiMap.get(varRndKey).equals("")) {
			String varKey = variable + "." + keys.get(0);
			String varTarget = variable + ".ts_rnd_key";
			DBUtil.loadRndKey(wiMap, dh, nodeTable, varKey, varTarget);
		}
		// Criando a busca textual
		String textualCols = wiMap.get("tmp.persist.textual_cols").trim();
		if (!textualCols.equals("") && keys.size() == 1) {
			int keyVal = Integer.parseInt(wiMap.get(variable + "." + keys.get(0)));
			String query = "select " + textualCols + " from " + table;
			query += " where " + keys.get(0) + " = " + keyVal;
			ResultSet rs = dh.execute(query, wiMap);
			StringBuilder textual = new StringBuilder();
			if (rs.next() > 0) {
				for (int i=1; i<rs.getMetaData().getColumnCount()+1; i++) {
					String value = Jsoup.parse(rs.column(i)).text().trim();
					if (value.equals("")) continue;
					if (textual.length() > 0) textual.append(" <=> ");
					textual.append(value);
				}
			}	
			WIMap auxMap = new WIMap();
			auxMap.put("wi.updatelog", "true");
			auxMap.put("colecao", table.replace("tb_", "").trim());
			String keyVar = variable + "." + keys.get(0);
			auxMap.put("ident", wiMap.get(keyVar));
			auxMap.put("conteudo", textual.toString());
			String call = "call sp_base_textual(?|colecao|,?|ident|,0,?|conteudo|)";
			dh.executeUpdate(call, auxMap);
		}	
		// Auditoria do LWSolution
		wiMap.put("tmp.audit.database", dh.getId());
		wiMap.put("tmp.audit.table", table);
		wiMap.put("tmp.audit.type", (insert ? "C" : "U"));
		for (int i = 0; i < keys.size(); i++) {
			String key = (String) keys.get(i);
			wiMap.put("tmp.audit.key" + (i+1), wiMap.get(variable + "." + key));
		}
		new Audit().execute(getParams());
	}

	@Override
	public JavaParameter[] getInputParameters() {
		JavaParameter[] orig = super.getInputParameters();
		JavaParameter[] in = new JavaParameter[orig.length+1];
		System.arraycopy(orig,0,in,0,orig.length);
		in[orig.length] = new JavaParameter("tmp.persist.textual_cols", "Colunas de Busca Textual");
		return in;
	}
		
}
