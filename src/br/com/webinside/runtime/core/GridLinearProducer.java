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

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import br.com.webinside.runtime.component.AbstractGridLinear;
import br.com.webinside.runtime.component.GridRef;
import br.com.webinside.runtime.component.GridSql;
import br.com.webinside.runtime.component.Page;
import br.com.webinside.runtime.integration.Producer;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.I18N;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.6 $
 */
public class GridLinearProducer {
    /** DOCUMENT ME! */
    public boolean exit = false;
    private ExecuteParams wiParams;
    private AbstractGridLinear grid;
    private GridRef gdref;
    private WIMap wiMap;
    private int hquant = 1;
    private Producer producer = new Producer();

    /**
     * Creates a new GridLinearProducer object.
     *
     * @param wiParams DOCUMENT ME!
     * @param grid DOCUMENT ME!
     * @param gdref DOCUMENT ME!
     */
    public GridLinearProducer(ExecuteParams wiParams, AbstractGridLinear grid,
        GridRef gdref) {
        this.wiParams = wiParams;
        if (wiParams != null) {
            this.wiMap = wiParams.getWIMap();
        }
        this.grid = grid;
        this.gdref = gdref;
    }

    /**
     * DOCUMENT ME!
     *
     * @param array DOCUMENT ME!
     * @param directout DOCUMENT ME!
     */
    public void execute(Map[] array, boolean directOut) {
        try {
            ProducerParam prod = new ProducerParam();
            prod.setWIMap(wiMap);
            prod.setInput(grid.getColSize().trim());
            producer.setParam(prod);
            producer.execute();
            hquant = Integer.parseInt(prod.getOutput().trim());
            if (hquant < 1) {
                hquant = 1;
            }
        } catch (NumberFormatException err) {
        	// ignorado
        }
        PrintWriter out = null;
        StringWriter sw = new StringWriter();
        if (directOut) {
        	out = wiParams.getWriter();
        } else {
        	out = new PrintWriter(sw, true);
        }
        // Clonagem para suporte a função NumberList
        WIMap pageWIMap = wiParams.getWIMap(); 
        if (pageWIMap.get("rowid").equals("")) {
        	wiMap = pageWIMap.cloneMe();
            wiMap.putObj("super.", pageWIMap);
            wiParams.setParameter(ExecuteParams.WI_MAP, wiMap);
            // verificar
        }
        wiMap.remove("wi.numberlist");
        // Definindo o id do grid
        String grdId = grid.getId().trim();
        String subId = "";
        if (gdref != null) {
            grdId = gdref.getId().trim();
            subId = gdref.getSubId().trim();
        }
        String grdIdFull = "grid." + grdId;
        if (!subId.equals("") && (grdId.indexOf("?") == -1)) {
        	grdIdFull = "grid." + grdId + "." + subId;
        }
        // Inicio do processamento
        if (wiMap.get(grdIdFull + ".returnempty").equals("true")) {
        	wiMap.put("returnempty", "true");
        }
        String startValue = 
        	getJspContent(wiMap.cloneMe(), "start", directOut);
        if (exit) {
            return;
        }
        if (hquant > 1 && grid.getColDisp().equals("V")) {
        	int pos = 0;
        	int len = array.length;
        	while (len % hquant > 0) len++;
        	Map[] auxArray = new Map[len];
        	for (int i = 0; i < hquant; i++) {
            	for (int a = i; a < len; a = a + hquant) {
            		if (pos < array.length) {
            			auxArray[a] = array[pos];
            		}	
            		pos++;
            	}
        	}
        	array = auxArray;
        }
        String designMode = "<script>var designMode = false;</script>\n";
        if (grid.getStyle().equals("")) {
            designMode = "";
        }
        out.print(designMode);
        out.print(startValue);
        if (array.length > 0) {
            for (int i = 0; i < array.length; i++) {
            	if (!validContentLimit(sw)) {
            		break;
            	}
                WIMap auxMap = wiMap.cloneMe();
                auxMap.putAll(array[i]);
                // monta a variação da cor do grid
                if (((i / hquant) % 2) != 0) {
                    ProducerParam color = new ProducerParam();
                    color.setWIMap(auxMap);
                    color.setInput(grid.getColor());
                    producer.setParam(color);
                    producer.execute();
                    String strColor = color.getOutput().trim();
                    if (!strColor.trim().equals("")) {
                      auxMap.put("grid.bgcolor", 
                      		"background-color:" + strColor + "");
                    }
                }                

                // Recursividade de operações no contexto
                // Para uso interno no TR
                if (executePrePage(auxMap)) {
                    return;
                }
                
                String rowStartValue = 
                	getJspContent(auxMap, "rowstart", directOut);  
                if (exit) {
                    return;
                }                
                out.print(rowStartValue);
                // Start Register
                String field = "register";
                int cont = 1;      
                while (cont <= hquant) {
                    // Recursividade de operações no contexto
                    // Para uso no TD
                    if (cont > 1) {
                    	if (executePrePage(auxMap)) {
                    		return;
                    	}
                    }
                    // usado em deslocamento vertical
                    if (array[i] == null) {
                    	field = "noregister";
                    }
                	String regValue = 
                		getJspContent(auxMap, field, directOut);
                    if (exit) {
                        return;
                    }
                    out.print(regValue);
                    cont = cont + 1;
                    if (cont <= hquant) {
                        if (i < (array.length - 1)) {
                            i = i + 1;
                            auxMap = wiMap.cloneMe();
                            auxMap.putAll(array[i]);
                        } else {
                        	field = "noregister";
                        }
                    }
                }

                // End Register
                String rowEndValue = 
                	getJspContent(auxMap, "rowend", directOut);  
                if (exit) {
                    return;
                }
                out.print(rowEndValue);
            }
        } else {
            String noRowValue = 
            	getJspContent(wiMap.cloneMe(), "norow", directOut);  
            if (exit) {
                return;
            }
            out.print(noRowValue);
        }
        String endValue = 
        	getJspContent(wiMap.cloneMe(), "end", directOut);        
        if (exit) {
            return;
        }
        out.print(endValue);
        wiParams.setParameter(ExecuteParams.WI_MAP, pageWIMap);
        if (!directOut) {
        	pageWIMap.put(grdIdFull, sw.toString());
            if (!subId.equals("") && (grdId.indexOf("?") == -1)) {
            	pageWIMap.remove("grid." + grdId);
            }
        }
    }

    private boolean executePrePage(WIMap auxMap) {
        String name = grid.getExecute();
        if (name.equals("")) return false;
    	if (!name.startsWith("/")) {
    		name = "/" + name;
    	}
        WIMap orig = wiParams.getWIMap();
        auxMap.put("wi.jsp.filename_parent", orig.get("wi.jsp.filename"));
        auxMap.put("wi.jsp.filename", name + "_pre");
        wiParams.setParameter(ExecuteParams.WI_MAP, auxMap);
        HttpServletRequest request = wiParams.getHttpRequest();
        String wiPage = (String)request.getAttribute("wiPage");        
       	request.setAttribute("wiPage", null);
    	boolean remove = (wiParams.getRequestAttribute("wiGrid") == null);
    	wiParams.setRequestAttribute("wiGrid", "true");
        wiParams.includePrePage(new Page(name));
		request.setAttribute("wiPage", wiPage);        
        if (remove) {
        	wiParams.removeRequestAttribute("wiGrid");
        }	
        wiParams.setParameter(ExecuteParams.WI_MAP, orig);
        if (request.getAttribute("wiExit") != null) {
            exit = true;
        }
        return exit;
    }
    
    private String getJspContent(WIMap auxMap, String type, boolean directOut) {
    	PrintWriter origWriter = wiParams.getWriter();
    	StringWriter str = new StringWriter();
    	wiParams.setParameter(ExecuteParams.OUT_WRITER, new PrintWriter(str));
        WIMap origMap = wiParams.getWIMap();
        // definindo estados especiais para as variaveis
    	auxMap.put("grid.generateInPage", "false");
        if (gdref != null) {
        	auxMap.put("grid.generateInPage", gdref.isGenerateInPage() + "");
        }
        if (auxMap != wiParams.getWIMap()) {
        	auxMap.putObj("super.", wiParams.getWIMap());
        }	
        wiParams.setParameter(ExecuteParams.WI_MAP, auxMap);
    	boolean remove = (wiParams.getRequestAttribute("wiGrid") == null);
    	wiParams.setRequestAttribute("wiGrid", "true");
    	// executando a parte do grid
    	if ((grid instanceof GridSql) && ((GridSql)grid).isRecursive()) {
    		auxMap.remove("grid." + grid.getId());
    	}
    	String file = 
    		wiParams.getWICVS() + "/grids/" + grid.getId() + "/" + type + ".jsp";
        try {
        	File f = new File(wiParams.getServletContext().getRealPath(file));
        	if (f.exists() || Execute.jspList.contains(file)) { 
        		RequestDispatcher rd = 
        			wiParams.getServletContext().getRequestDispatcher(file);
        		rd.include(wiParams.getHttpRequest(), wiParams.getHttpResponse());
        	}	
        } catch (Exception err) {
			wiParams.setRequestAttribute("wiException", err);
        }
        // retornando as variaveis ao seu estado normal
        if (remove) {
        	wiParams.removeRequestAttribute("wiGrid");
        }	
        wiParams.setParameter(ExecuteParams.OUT_WRITER, origWriter);
        auxMap.remove("grid.generateInPage");
        auxMap.remove("super.");
        wiParams.setParameter(ExecuteParams.WI_MAP, origMap);
        if (!directOut) {
        	origMap.put("grid." + grid.getId(), 
        		auxMap.get("grid." + grid.getId()));
        }	
        if (wiParams.getRequestAttribute("wiExit") != null) {
        	exit = true;
        }
    	return str.toString();
    }
    
    private boolean validContentLimit(StringWriter sw) {
    	if (gdref != null && sw.getBuffer().length() > 0) {
	    	int cl = 5000000;
	    	String id = gdref.getId();
	    	String text = wiMap.get("grid." + id + ".contentlimit");
	    	if (!text.trim().equals("") && Function.parseInt(text) > 0) {
	    		cl = Function.parseInt(text) * 1000000;
	    	}
	    	if (sw.getBuffer().length() > cl) {
		        String label = 
		        	new I18N().get("Conteúdo do Grid excedeu limite");
		        String msg = "Grid " + id + " content limit exceded\r\n";
            	wiParams.getErrorLog().write("GridLinearProducer", "Execute", 
    				msg + wiMap);
	    		sw.getBuffer().insert(0, "<font color='red'>" + label + "</font>");
	    		return false;
	    	}
    	}
    	return true;
    }
    
}
