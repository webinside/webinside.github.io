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

package br.com.webinside.runtime.function;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.webinside.runtime.integration.AbstractFunction;
import br.com.webinside.runtime.util.WIMap;

/**
 * save -> |$nl(venda,|vl_venda|[,cbr2])$|
 * show -> |$nl(venda,sum|avg|count[,cbr2])$| 
 */
public class NumberList extends AbstractFunction {

	/** DOCUMENT ME! */
    private static final String SUM = "sum";
    /** DOCUMENT ME! */
    private static final String AVG = "avg";
    /** DOCUMENT ME! */
    private static final String COUNT = "count";
    /** DOCUMENT ME! */
    private static final String MAX = "max";
    /** DOCUMENT ME! */
    private static final String MIN = "min";

    /**
     * Creates new NumberList
     */
    public NumberList() {
    }

    /**
     * Creates new NumberList
     */
    public NumberList(WIMap wiMap) {
    	setWiMap(wiMap);
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String execute(String[] args) {
        if (args.length < 2) return "";

        String key = args[0].toLowerCase();
        String value = args[1];
        String result = "";
		boolean empty = false; 

        try {
        	if (value.equalsIgnoreCase(SUM)) {
                result = getSum(getList(key)) + "";
        	} else if (value.equalsIgnoreCase(AVG)) {
        		List<BigDecimal> list = getList(key);
        		if (list.size() > 0) {
        			BigDecimal sum = getSum(list).setScale(3);
        			BigDecimal divisor = new BigDecimal(list.size());
                    result = sum.divide(divisor,RoundingMode.HALF_EVEN) + "";
        		} else {
        			result = "0";
        		}
        	} else if (value.equalsIgnoreCase(COUNT)) {
        		result = getList(key).size() + "";
        	} else if (value.equalsIgnoreCase(MAX) || 
        			value.equalsIgnoreCase(MIN)) {
            	BigDecimal aux = null;
        		int flag = (value.equalsIgnoreCase(MAX) ? 1 : -1);
            	for (BigDecimal number : getList(key)) {
            		if (aux == null || number.compareTo(aux) == flag) {
            			aux = number;
            		}
        		}
            	if (aux == null) aux = new BigDecimal(0);
        		result = aux + "";
        	} else {
        		if (args.length == 3 && args[2].equals("empty")) {
        			empty = true;
        		}
        		if (value.equalsIgnoreCase("empty")) {
        			empty = true;
        			value = "";
        		}
        		if (value.equals("")) value = "0";
        		WIMap wiMap = getWiMap();
            	if (getWiMap().getObj("super.") != null) {
            		wiMap = (WIMap)getWiMap().getObj("super.");
            	}
            	try {
	        		while (wiMap != null) {
	    				getList(wiMap,key).add(new BigDecimal(value));
	    				wiMap = (WIMap) wiMap.getObj("super.");
	        		}
            	} catch (NumberFormatException err) {
            		value="#error#";
            	}
                if (!empty) result = value;
        	}
        	if (value.indexOf("#") == -1 && args.length == 3) {
        		String[] params = {result, args[2]};
                NumberFormat nf = new NumberFormat(getWiMap());
                if (!empty) result = nf.execute(getWiParams(), params);
        	}
        } catch (Exception e) {
        	e.printStackTrace(System.err);
        }
        return result;
    }

    private List<BigDecimal> getList(String key) {
    	WIMap wiMap = getWiMap();
    	if (getWiMap().getObj("super.") != null) {
    		wiMap = (WIMap)getWiMap().getObj("super.");
    	}
    	return getList(wiMap, key);
    }

    private List<BigDecimal> getList(WIMap wiMap, String key) {
    	Map<String,List<BigDecimal>> listMap = (Map)wiMap.getObj("wi.numberlist");
    	if (listMap == null) {
    		listMap = new HashMap<String, List<BigDecimal>>();
    		wiMap.putObj("wi.numberlist", listMap);
    	}
    	List<BigDecimal> list = listMap.get(key);
    	if (list == null) {
    		list = new ArrayList<BigDecimal>();
    		listMap.put(key, list);
    	}
    	return list;
    }
    
    private BigDecimal getSum(List<BigDecimal> list) {
    	BigDecimal aux = new BigDecimal(0);
    	for (BigDecimal number : list) {
    		aux = aux.add(number);
		}
    	return aux;
    }
    
}
