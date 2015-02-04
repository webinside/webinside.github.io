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

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.webinside.runtime.integration.DatabaseAliases;
import br.com.webinside.runtime.integration.InterfaceGrid;
import br.com.webinside.runtime.util.WIMap;

/**
 * Grid Java de exemplo
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.1 $
 */
public class GridSample implements InterfaceGrid {

	private int returntype;

    public Map[] execute(WIMap wiMap, DatabaseAliases dbAliases) {
        returntype = HAS_MORE_ROWS;
        int from = 1;
        int limit = 0;
        try {
        	limit = Integer.parseInt(wiMap.get("grid.limit"));
            String next = "grid." + wiMap.get("grid.id") + ".next";
            from = Integer.parseInt(wiMap.get(next));
        } catch (NumberFormatException err) {
        	// ignorado.
        }
        List completa = new ArrayList();
        Enumeration e = System.getProperties().keys();
        while (e.hasMoreElements()) {
        	String key = (String) e.nextElement();
        	String value = System.getProperty(key);
            Map aux = new HashMap();
            aux.put("chave", key);
            aux.put("valor", value);
            completa.add(aux);
        }
        if (limit == 0) {
        	returntype = COMPLETE;
        	return (Map[])completa.toArray(new Map[0]);        	
        }
        List parcial = new ArrayList();
        for (int i = from - 1; i < completa.size() && i < from + limit - 1; i++) {
        	parcial.add(completa.get(i));
        }
        if (from + limit > completa.size()) {
            returntype = NO_MORE_ROWS;
        }
        return (Map[])parcial.toArray(new Map[0]);
    }

    public int returnType() {
        return returntype;
    }

}
