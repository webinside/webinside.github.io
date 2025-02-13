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

package br.com.webinside.runtime.integration;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import br.com.webinside.runtime.component.*;
import br.com.webinside.runtime.core.*;
import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.util.*;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class Producer {
    private ProducerParam params;
    private boolean recursiveVariable;
    private Map recursiveHash;

    /**
     * Creates a new Producer object.
     */
    public Producer() {
    }

    /**
     * Creates a new Producer object.
     *
     * @param params DOCUMENT ME!
     */
    public Producer(ProducerParam params) {
        this.params = params;
    }

    /**
     * DOCUMENT ME!
     *
     * @param param DOCUMENT ME!
     */
    public void setParam(ProducerParam param) {
        this.params = param;
    }

    /**
     * Executa o producer.
     * <p>
     * Para processar fuções e ativar outros recursos utilize:
     * <pre>
     * ProducerParam param = new ProducerParam();
     * ...
     * Producer prod = new Producer();
     * ou
     * Producer prod = wiParams.getProducer(); 
     * ...
     * prod.setParam(param);
     * prod.execute();
     * </pre>
     */
    public void execute() {
        if ((params == null) || (params.inputR == null)) {
            return;
        }
        if (params.outputS == null) {
            params.outputS = new StringA();
        } else {
            params.outputS.set("");
        }
        try {
            String line = null;
            recursiveHash = new HashMap();
            while ((line = params.inputR.readLine()) != null) {
                recursiveHash.clear();
                if (line.indexOf("|grid.") > -1 && line.indexOf(".link") > -1) {
                	ExecuteParams params = ExecuteParams.get();
                	if (params != null) {
                		HttpServletRequest req = params.getHttpRequest();
                		req.setAttribute("wiGridHtmlFormOk", "true");
                	}	
                }
                line = change(line);
                if (params.recursive) {
                    while (recursiveVariable) {
                        line = change(line);
                    }
                }
                if ((params.protectedPipe == null)
                            || !params.protectedPipe.contains("-")) {
                    line = StringA.change(line, "|-|", "|");
                }
                if (params.outputW != null) {
                    params.outputW.println(line);
                    params.outputW.flush();
                } else if (params.outputS != null) {
                    params.outputS.append(line);
                    params.outputS.append("\r\n");
                }
            }
        } catch (IOException err) {
        	// não é testado.
        }
        if (params.outputW == null && !params.outputS.toString().equals("")) {
            StringBuffer aux = params.outputS.getBuffer();
            char last = aux.charAt(aux.length()-1); 
        	while ( last == '\n' || last == '\r') {
        		aux.delete(aux.length()-1, aux.length());
                if (aux.length() > 0 ) {
                    last = aux.charAt(aux.length()-1);
                } else {
                    last = ' ';
                }
            }
        }
    }

    private String change(String line) {
        Map tmpHash = new HashMap();
        recursiveVariable = false;
        if (line == null) {
            return null;
        }
        StringBuffer response = new StringBuffer();
        int from = 0;
        int found = 0;
        while ((found = line.indexOf("|", from)) > -1) {
            response.append(StringA.mid(line, from, found - 1));            
            int pipe = Function.lastPipePos(line, found);
            String token = StringA.mid(line, found + 1, pipe - 1);
            if (StringA.mid(line, from, pipe).endsWith("|")) {
                from = pipe + 1;
            } else {
                from = pipe;
            }
            if (found >= 6) {
            	String ps = StringA.mid(line, found - 6, found - 5) + 
            					StringA.mid(line, found - 1, found - 1);
            	if (ps.equals("?[]") && params.protectedPipe != null && 
            			params.protectedPipe.contains("?||")) {
            		response.append(StringA.mid(line, found, from - 1));            
            		continue;
            	}
            }	
            boolean questionMark = false;
            if ((found > 0) && (line.charAt(found - 1) == '?')) {
                questionMark = true;
            }
            String value = "";
            // atribuição não é aceita dentro de função
            if (token.indexOf("?") > -1 && (
                    !token.startsWith("$") && !token.endsWith("$")
                    )) {
                IntFunction.importParameters(params.getWIMap(), token);
                if (!token.toLowerCase().startsWith("combo.")) {
                    token = new StringA(token).piece("?", 1);
                }
                if (token.equals("")) {
                    continue;
                }
            }
            if (token.equals("")) {
                value = "||";
            } else if (token.startsWith("grid.")
                        && generateGrid(token, response)) {
                continue;
            } else if (questionMark && (params.protectedPipe != null)
                        && params.protectedPipe.contains("?||")) {
            	if (!token.startsWith("$")) {
            		value = "|" + token + "|";
                } else {
                    int pos = line.indexOf("$|", found + 1);
                    if (pos == -1) pos = line.length();
                    value = StringA.mid(line, found, pos + 1);
                    from = pos + 2;
                }
            } else if ((params.protectedPipe != null)
                        && params.protectedPipe.contains(token.toLowerCase())) {
                value = "|" + token + "|";
            } else if (token.equals("-")) {
                value = "|-|";
            } else if ((token.startsWith("*")) && (token.endsWith("*"))) {
                value = "|" + token + "|";
            } else if (token.startsWith("$")) {
                StringA auxLine = new StringA(line);
                StringA auxvalue = new StringA();
                from = function(auxLine, found, auxvalue);
                line = auxLine.toString();
                response.append(auxvalue.toString());
            } else {
                if (recursiveHash.containsKey(token)) {
                    if (params.isXml) {
                        response.append(StringA.getXml(params.emptyString));
                    } else {
                        response.append(params.emptyString);
                    }
                    continue;
                }
                tmpHash.put(token, "");
                if (params.wiMap != null) {
                    value = params.wiMap.get(token);
                } else {
                    value = "";
                }
                recursiveVariable = true;
                value =
                    StringA.changeChars(value, params.filterIn, params.filterOut);
                if (value.equals("")) {
                    boolean ini = false;
                    if (params.emptyStart != null) {
                        for (int i = 0; i < params.emptyStart.size(); i++) {
                            if (token.startsWith(
                                            (String) params.emptyStart.get(i))) {
                                ini = true;
                                value = (String) params.emptyValue.get(i);
                                break;
                            }
                        }
                    }
                    if (!ini) {
                        value = params.emptyString;
                    }
                }
            }
            if (params.isXml) {
                response.append(StringA.getXml(value));
            } else {
                response.append(value);
            }
        }
        response.append(StringA.mid(line, from, line.length()));
        Iterator it = tmpHash.keySet().iterator();
        while (it.hasNext()) {
            recursiveHash.put(it.next(), "");
        }
        return response.toString();
    }

    private boolean generateGrid(String token, StringBuffer response) {
        String grid = StringA.piece(token, "grid.", 2, 0, false);
        boolean gen = false;
        if (params.generategrid != null) {
            for (int i = 0; i < params.generategrid.size(); i++) {
                String grname = (String) params.generategrid.get(i);
                if (grname.equalsIgnoreCase(grid)) {
                    gen = true;
                    break;
                }
            }
        }
        if (!gen || (params.outputW == null) || (params.execparam == null)) {
            return false;
        }
        params.outputW.println(response.toString());
        params.outputW.flush();
        response.delete(0, response.length());
        int type = 0;
        if (params.execparam != null) {
            AbstractProject proj = params.execparam.getProject();
            if (proj != null) {
                if (proj.getGrids().getElement(grid) instanceof GridSql) {
                    type = 1;
                }
                if (proj.getGrids().getElement(grid) instanceof GridXmlOut) {
                    type = 2;
                }
            }
        }
        if ((type == 1) || (type == 2)) {
            // SQL
            GridRef ele = new GridRef();
            ele.setId(grid);
            ele.setCondition("true");
            ele.setGenerateInPage(false);
            CoreGrid core = new CoreGrid(params.execparam, ele);
            core.execute();
        } else {
            // WIObject
            WIObjectGrid ele = new WIObjectGrid();
            ele.setGridId(grid);
            String id = params.getWIMap().get("id");
            params.getWIMap().remove("id");
            ele.setWIObjName(id);
            ele.setCondition("true");
            CoreWIObjectGrid core = new CoreWIObjectGrid(params.execparam, ele);
            core.execute();
        }
        return true;
    }

    private int function(StringA line, int pos, StringA auxvalue) {
        if (line == null) {
            return -1;
        }
    	int subFunc = pos + 2;
    	int ct1 = 0;
    	int ct2 = 0;
    	int end = -1;
    	boolean fim = false;
    	while (!fim && end == -1) {
			do {
	    		end = line.indexOf("$|", subFunc);
	    		if (end > -1) {
	    			String aux = line.mid(pos, end + 1);
	    			ct1 = StringA.count(aux, "|$", true);
	    			ct2 = StringA.count(aux, "$|", true);
	    			subFunc = end + 2;
	    		}
			} while (end > -1 && ct1 != ct2);
			if (end == -1) {				
				try {
					subFunc = line.length();	
					String newLine = params.inputR.readLine();
    				if (newLine != null) {
    					line.append("\r\n" + newLine);
    				} else {
    					fim = true;
    				}
				} catch (IOException err) {
					fim = true;
				}
			}		
    	}
        if (end == -1) {
            end = line.length();
        }
        if (!fim) { 
	        String express = line.mid(pos + 2, end - 1);
	        try {
	        	params.getWIMap().put("wi.sql.filterin", params.filterIn);
	        	params.getWIMap().put("wi.sql.filterout", params.filterOut);
	            String resp = "";
	            if (!express.toLowerCase().startsWith("wi.longtextcolumns")) {
	            	ExecuteParams wiParams = ExecuteParams.get();
	            	if (wiParams == null) {
	            		wiParams = new ExecuteParams();
	            		WIMap wiMap = params.getWIMap(); 
	            		wiParams.setParameter(ExecuteParamsEnum.WI_MAP, wiMap);
	            	}
	                resp = IntFunction.executeFunctionWI(express, 
	                		wiParams, params.wiMap);
	            }    
	            auxvalue.set(resp);
	        	params.getWIMap().remove("wi.sql.filterin");
	        	params.getWIMap().remove("wi.sql.filterout");
	        } catch (UserException err) {
	            auxvalue.set("|$" + express + "$|");
	        } catch (Exception err) {
	        	String proj = params.getWIMap().get("wi.proj.id");
	        	String page = params.getWIMap().get("wi.page.id");
	        	String msg = getClass().getName() + ": " + proj + 
	        		"/" + page + " - " + express + " - " + err;
	        	System.err.println(msg);
	        	err.printStackTrace(System.err);
	        }
        }    
        return end + 2;
    }

    /**
     * Produz os pipes em um texto.
     * <p>
     * Caso pelo menos 1 dos parametros seja nulo o retorno será vazio.
     * 
     * @param wiMap coleção de variáveis a ser utilizada.
     * @param text texto a ser produzido.
     *
     * @return texto produzido ou vazio se nulo.
     * @see #execute()
     */
    public static String execute(WIMap wiMap, String text) {
        if (text == null) {
            return "";
        }
        if (wiMap == null) {
            wiMap = new WIMap();
        }
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        prod.setInput(text);
        prod.addProtectedPipe("?||");
        String filterIn = wiMap.get("wi.sql.filterin");
        String filterOut = wiMap.get("wi.sql.filterout");
        prod.setCharFilter(filterIn, filterOut);
        new Producer(prod).execute();
        return prod.getOutput();
    }
}
