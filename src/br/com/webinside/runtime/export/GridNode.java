package br.com.webinside.runtime.export;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import br.com.webinside.runtime.component.GridSql;
import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.integration.Producer;
import br.com.webinside.runtime.util.WIMap;
import br.com.webinside.runtime.util.WISession;

public class GridNode implements Serializable {

	private static final long serialVersionUID = 1L;
	
	public static String SESSION_GRIDS_MAP = "wiSessionGridsMap";
	
	private String exportIn;
	private String exportOut;
	private String sql;
	private Map<String, String> params;
	
	public GridNode() {
		exportIn = "";
		exportOut = "";
		sql = "";
		params = new HashMap<String, String>();
	}

	public void setExportOut(String exportOut) {
		this.exportOut = exportOut;
	}
	
	public String getExportIn() {
		return exportIn;
	}

	public String getExportOut() {
		return exportOut;
	}
	
	public String getSql() {
		return sql;
	}

	public Map<String, String> getParams() {
		return params;
	}
	
	public static synchronized Map<String, GridNode> getSessionMap(ExecuteParams wiParams) {
		WISession session = wiParams.getWISession(); 
		Map<String, GridNode> sessionMap = (Map) session.getAttribute(SESSION_GRIDS_MAP);
		if (sessionMap == null) {
			sessionMap = new HashMap<String, GridNode>();
			session.setAttribute(SESSION_GRIDS_MAP, sessionMap);
		}
		return sessionMap;
	}
	
	public static void include(ExecuteParams wiParams, WIMap wiMap, 
			GridSql grid, String executedSql) {
		if (grid.getExportOut().trim().equals("")) {
			exclude(wiParams, grid);
			return;
		}
		Map<String, String> params = new HashMap<String, String>();
		params.put("tmp.disableMaxRows", wiMap.get("tmp.disableMaxRows"));
		Set<Map.Entry> entrySet = wiMap.getAsMap().entrySet();
		for (Map.Entry<String, String> entry : entrySet) {
			String key = entry.getKey().toLowerCase();
			if (executedSql.toLowerCase().indexOf("|" + key + "|") > -1) {
				params.put(key, entry.getValue());
			}
		}
		GridNode gnode = new GridNode();
		gnode.exportIn = Producer.execute(wiMap, grid.getExportIn());
		gnode.exportOut = grid.getExportOut();
		gnode.sql = executedSql;
		gnode.params = params;
		getSessionMap(wiParams).put(grid.getId(), gnode);
	}

	public static void exclude(ExecuteParams wiParams, GridSql grid) {
		getSessionMap(wiParams).remove(grid.getId());
	}

}
