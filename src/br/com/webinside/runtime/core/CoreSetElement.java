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

package br.com.webinside.runtime.core;

import java.util.Iterator;

import br.com.webinside.runtime.component.SetElement;
import br.com.webinside.runtime.integration.IntFunction;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.util.Encrypter;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.5 $
 */
public class CoreSetElement extends CoreCommon {
    private SetElement set;

    /**
     * Creates a new CoreSetElement object.
     *
     * @param wiParams DOCUMENT ME!
     * @param set DOCUMENT ME!
     */
    public CoreSetElement(ExecuteParams wiParams, SetElement set) {
        this.wiParams = wiParams;
        this.set = set;
        element = set;
    }

    /**
     * DOCUMENT ME!
     */
    public void execute() {
        if (set == null || isDisabledCondition()) {
            return;
        }
        String wiobj = set.getWIObj().trim();
        boolean exec = false;
        ProducerParam prod = new ProducerParam();
        String input = "";
        if (isValidCondition()) {
            input = set.getIfTrue();
            exec = true;
        } else if (set.getEnableFalse().equals("ON")) {
            input = set.getIfFalse();
            exec = true;
        }
        if (exec) {
            String delim = "},";
            if (input.indexOf(delim) == -1) {
                delim = ",";
            }
            if (set.getEncode().equals("ON")) {
                Encrypter en = new Encrypter(input);
                input = en.decodeDES();
            }
            prod.setWIMap(wiMap);
            if (set.getRecursive().equals("ON")) {
                prod.setRecursive(true);
            }
            String tks = wiobj;
            String tksdata = input;
            int qnt = StringA.count(tks, ",", true);
            for (int i = 1; i <= (qnt + 1); i++) {
                String subobj = StringA.piece(tks, ",", i).trim();
                if (subobj.toLowerCase().startsWith("wi.")) {
                    continue;
                }
                String subdata = "";
                if (qnt > 0) {
                    subdata = StringA.piece(tksdata, delim, i).trim();
                    if (!subdata.equals("") && (subdata.charAt(0) == '{')) {
                        subdata = StringA.mid(subdata, 1, subdata.length());
                    }
                    int len = subdata.length();
                    if (!subdata.equals("") && (
                                    subdata.charAt(len - 1) == '}'
                                )) {
                        subdata = StringA.mid(subdata, 0, len - 2);
                    }
                } else {
                    subdata = input;
                }
                if (subobj.equals("")) {
                    continue;
                }
                if (subobj.endsWith(".")) {
                    WIMap aux = null;
                    try {
                        subdata = StringA.piece(subdata, "|", 2).trim();
                        aux = (WIMap) wiMap.getObj(subdata);
                        if (aux != null) {
                            aux = aux.cloneMe();
                        }
                    } catch (ClassCastException err) { }
                    if (subdata.trim().equals("")) {
                        IntFunction.killObjAndVector(wiMap, subobj);
                    } else {
                        if (aux == null) aux = new WIMap();
                        wiMap.putObj(subobj, aux);
                        putVector(wiMap, subdata, subobj);
                    }
                } else {
                    String value = "";
                    if (subdata.toLowerCase().startsWith("|$wi.context")) {
                    	value = RtmFunction.wiContext(wiMap, subdata);
                    } else if (subdata.equalsIgnoreCase("|$wi.syncContext$|")) {
                    	Context ctx = new Context(wiParams);
                    	ctx.syncWIMap(wiMap);
                    	value = "true";
                    } else if (set.isDisableProduce()) {
                    	value = subdata;
                    } else {
                        prod.setInput(subdata);
                        wiParams.getProducer().setParam(prod);
                        wiParams.getProducer().execute();
                        value = prod.getOutput();
                    }
                    if (set.isDecodeJson()) {
                    	Function.decodeJSON(wiMap, value, subobj);
                    } else {
                        if (value.trim().equals("")) {
                            wiMap.remove(subobj);
                        } else {
                        	if (value.trim().equals("&nbsp;")) value = "";
                            wiMap.put(subobj, value);
                        }
                    }
                }
            }
        }
        writeLog();
    }

    private void putVector(WIMap context, String origem, String destino) {
        if (!origem.endsWith(".") || !destino.endsWith(".")) {
            return;
        }
        destino =
            StringA.mid(destino.toLowerCase(), 0, destino.length() - 2).trim();
        WIMap subContext = context;
        if (StringA.count(origem, '.') > 1) {
            int last = origem.lastIndexOf(".", origem.length() - 2);
            String p1 = StringA.mid(origem, 0, last);
            origem = StringA.mid(origem, last + 1, origem.length());
            try {
                subContext = (WIMap) context.getObj(p1);
            } catch (ClassCastException err) {
            }
            if (subContext == null) {
                subContext = new WIMap();
            }
        }
        origem = StringA.changeChars(origem.toLowerCase(), ". ", "");
        Iterator it = subContext.getInternalMap().keySet().iterator();
        while (it.hasNext()) {
            String key = (String) it.next();
            if (key.startsWith(origem + "[")) {
                String name = StringA.change(key, origem + "[", destino + "[");
                context.putObj(name, subContext.getObj(key));
            }
        }
    }
    
}
