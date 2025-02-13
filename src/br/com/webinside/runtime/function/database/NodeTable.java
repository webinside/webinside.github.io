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
