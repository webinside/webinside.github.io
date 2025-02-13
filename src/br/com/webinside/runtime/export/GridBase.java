package br.com.webinside.runtime.export;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import br.com.webinside.runtime.component.AbstractGrid;
import br.com.webinside.runtime.component.GridSql;
import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.core.RtmFunction;
import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.integration.DatabaseHandler;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

public abstract class GridBase {

	protected ExecuteParams wiParams;
	protected GridSql gridSql;
	protected GridNode node;
	private List<String[]> exportInList;
	private List<String[]> exportOutList;

	public GridBase(ExecuteParams wiParams) {
		this.wiParams = wiParams;
	}
	
	public void execute(String gridId) throws IOException {
        if (!wiParams.getProject().getGrids().containsKey(gridId)) {
            wiParams.includeCode("/grids/" + gridId + "/grid.jsp");
        }
        AbstractGrid grid = (AbstractGrid) wiParams.getProject().getGrids().get(gridId);
        if (grid == null || !(grid instanceof GridSql)) return;
		gridSql = (GridSql)grid;
		node = GridNode.getSessionMap(wiParams).get(gridSql.getId());
		if (node == null) return;
		node.setExportOut(gridSql.getExportOut());
        String dbalias = gridSql.getDatabase();
        DatabaseHandler db = wiParams.getDatabaseAliases().get(dbalias);
        if ((db == null) || (!db.isConnected())) {
            RtmFunction.databaseError(wiParams, dbalias);
            return;
        }
        db.setCharFilter(RtmFunction.cleanSpace(gridSql.getSqlFilter()), "");
        WIMap wiMap = wiParams.getWIMap();
        wiMap.putAll(node.getParams());
        ResultSet rs = null;
        try {
            rs = db.execute(node.getSql(), wiMap);
            before(rs);
            int pos = 0;
            while ((pos = rs.next()) > 0) {
            	iteration(rs, pos);
            }
        } catch (Exception ex) {
            String msgDetail = db.getErrorMessage() + 
            	"\r\n--- SQL ---\r\n" + db.getExecutedSQL();
            WIMap psMap = db.getExecutedSQLParams(wiMap); 
            if (psMap.keySet().size() > 0) {
            	msgDetail += "\r\n--- PARAMS ---\r\n";
            	msgDetail += psMap.toString();
            }	
            wiParams.getErrorLog().write("WIExport GridXls ", "Grid " + grid.getId(), msgDetail);
        } catch (Throwable err) {
        	err.printStackTrace();
        } finally {
            after();
        }
	}

	protected abstract void before(ResultSet rs) throws Exception;

	protected abstract void iteration(ResultSet rs, int pos) throws Exception;

	protected abstract void after() throws IOException;

	protected List<String[]> getExportInList(GridNode node) {
		if (exportInList == null) {
			exportInList = new ArrayList<String[]>();
			execExportList(exportInList, node.getExportIn());
		}
		return exportInList;
	}
	
	protected String getExportInLogo(GridNode node) {
		for (String[] str : getExportInList(node)) {
			if (str[0].equalsIgnoreCase("logo")) return str[1];
		}
		return "";
	}
 	
	protected List<String[]> getExportOutList(GridNode node) {
		if (exportOutList == null) {
			exportOutList = new ArrayList<String[]>();
			execExportList(exportOutList, node.getExportOut());
		}
		return exportOutList;
	}

	protected void execExportList(List<String[]> list, String content) {
		try {
			BufferedReader br = new BufferedReader(new StringReader(content));
			String line= null;
			while ((line = br.readLine()) != null) {
				if (line.trim().startsWith("#") || line.indexOf("=") == -1) continue;
				int pos = line.indexOf("=");
				String[] str = new String[2];
				str[0] = StringA.mid(line, 0, pos-1).trim();
				str[1] = StringA.mid(line, pos + 1, line.length()).trim();
				if (!str[1].equals("")) list.add(str);
			}
		} catch (IOException e) { }	
	}
	
}
