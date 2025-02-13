/*
 * WEBINSIDE - Ferramenta de produtividade Java
 * Copyright (c) 2011-2012 LINEWEB Solu��es Tecnol�gicas Ltda.
 * Copyright (c) 2009-2010 Inc�gnita Intelig�ncia Digital Ltda.
 *
 * Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 * sob os termos da GNU LESSER GENERAL PUBLIC LICENSE (LGPL) conforme publicada 
 * pela Free Software Foundation; vers�o 2.1 da Licen�a.
 * Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 * NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 * ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 * 
 * Consulte a GNU LGPL para mais detalhes.
 * Voc� deve ter recebido uma c�pia da GNU LGPL junto com este programa; se n�o, 
 * veja em http://www.gnu.org/licenses/ 
 */

package br.com.webinside.runtime.function.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class NodeTable {

	private String name;
	private List<NodeColumn> columns;
	private Map primaryKeys;
	private Map importedKeys;
	private Map exportedKeys;

	public NodeTable(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public List<NodeColumn> getColumns() {
		return columns;
	}

	public void setColumns(List columns) {
		this.columns = columns;
	}

	public void addColumn(NodeColumn column) {
		if (columns == null) {
			columns = new ArrayList();
		}
		columns.add(column);
	}
	
	public Map getExportedKeys() {
		return exportedKeys;
	}

	public void setExportedKeys(Map exportedKeys) {
		this.exportedKeys = exportedKeys;
	}

	public Map getImportedKeys() {
		return importedKeys;
	}

	public void setImportedKeys(Map importedKeys) {
		this.importedKeys = importedKeys;
	}

	public Map getPrimaryKeys() {
		return primaryKeys;
	}

	public String getPrimaryKey() {
		return primaryKeys.keySet().iterator().next().toString();
	}
	
	public void setPrimaryKeys(Map primaryKeys) {
		this.primaryKeys = primaryKeys;
	}
	
	public boolean hasColumn(String col) {
		List<NodeColumn> cols = getColumns();
		for (NodeColumn nodeColumn : cols) {
			if (nodeColumn.getName().equals(col)) return true;
		}
		return false;
	}

}
