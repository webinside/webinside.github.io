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

import br.com.webinside.runtime.util.*;

/**
 * Transforma pipes e funções em código JSP.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.2 $
 */
public class JspPipeConverter {
    /** Identificador especial para desativar a troca dos pipes */
    public static final String CHANGE_PIPES_DISABLE = "CHANGE_PIPES_DISABLE";
    /** Identificador especial para ativar a troca dos pipes */
    public static final String CHANGE_PIPES_ENABLE = "CHANGE_PIPES_ENABLE";
	private boolean changePipes;
    private boolean inScriptlet;
	private int foundWiTag;

    /**
     * Execute o processamento do texto.
     *
     * @param text o texto a ser utilizado.
     *
     * @return o texto processado.
     */
    public String execute(String text) {
        if (text == null) {
            text = "";
        }
    	changePipes = true;
        inScriptlet = false;
        foundWiTag = -1;
        StringBuffer resp = new StringBuffer();
        int pos = 0;
        int from = 0;
        StringA outResp = new StringA();
        while ((pos = find(text, from, outResp)) > -1) {
            int end = pos - 1;
            boolean isCombo = false;

            // Testa se é uma |combo.XXX| dentro de um <option>. 
            if (StringA.mid(text, pos, pos + 6).equals("|combo.")) {
                int pre = text.lastIndexOf("<", pos);
                if (pre > -1 && text.substring(pre).toUpperCase().startsWith("<OPTION")) {
                    end = pre - 1;
                    isCombo = true;
                }
            }

            // Concatena o texto anterior à posição localizada. 
            resp.append(StringA.mid(text, from, end));
            if (outResp.length() > 0) {
                resp.append(outResp.toString());
                from = pos + outResp.length();
                continue;
            }

            // Identifica se é uma função.
            boolean isFunc = StringA.mid(text, pos, pos + 1).equals("|$");
            int last = Function.lastPipePos(text, pos);

            // Recupera o objeto entre os delimitadores.
            String obj = StringA.mid(text, pos, last);
            if (obj.equals("|-|")) {
                resp.append("|");
            } else if (obj.equals("||")) {
                resp.append("||");
            } else {
	            // Efetua a troca do objeto por seu código correspondente.
	            if (changePipes && (foundWiTag == -1)) {
	                if (isFunc) {
                        changeFunction(resp, obj);
	                } else {
                        changePipeValue(resp, obj);
	                }
	            } else {
	            	resp.append(obj);
	            }
            } 
            from = last + 1;
            // Testa se é uma combo seguinda de </option> para omitir. 
            if (isCombo) {
                int posCombo = text.indexOf("<", from);
                if (posCombo > -1 && text.substring(posCombo).toUpperCase().startsWith("</OPTION")) {
                    from = text.indexOf(">", posCombo) + 1;
                }
            }
        }

        // Concatena resto do texto.
        resp.append(StringA.mid(text, from, text.length()));
        return resp.toString();
    }

    private int find(String text, int from, StringA outResp) {
        // OutResp é um buffer que deve ser mantido na página, 
        // pois o | é removido. 
        int pipe = text.indexOf("|", from);
        // usado em scriptlet
        int ini1 = text.indexOf("<%", from);
        int fim1 = text.indexOf("%>", from);
        // usado para desabilitar a troca dos pipes
        int p1 = text.indexOf(CHANGE_PIPES_DISABLE, from);
        int p2 = text.indexOf(CHANGE_PIPES_ENABLE, from);
        // usado na taglib do wi
        int wi1 = text.indexOf("<wi:", from);
        int tag1 = text.indexOf("<tag:", from);
		int wi2 = text.indexOf(">", from);
		// inicio da logica
        int size = 0;
        int pos = pipe;
        // procurando scriptlet
        if ((ini1 > -1) && (ini1 < pos)) {
            inScriptlet = true;
            pos = ini1;
            size = 2;
        }
        if ((fim1 > -1) && (fim1 < pos)) {
            inScriptlet = false;
            pos = fim1;
            size = 2;
        }
        // procurando CHANGE_PIPES
        if ((p1 > -1) && (p1 < pos)) {
            changePipes = false;
            size = CHANGE_PIPES_DISABLE.length();
            pos = p1;
        }
        if ((p2 > -1) && (p2 < pos)) {
            changePipes = true;
            size = CHANGE_PIPES_ENABLE.length();
            pos = p2;
        }
        // procurando WITAG
		if ((foundWiTag > -1) && (wi2 > -1) && (wi2 < pos)) {
			while ((wi2 > -1) && !isValidMark(text, wi2)) {
				wi2 = text.indexOf(">", wi2 + 1);				
			}
			if (wi2 == -1) {
				wi2 = text.length();
			}
			foundWiTag = -1;
			size = 1;
			pos = wi2;
		}
		if ((wi1 > -1) && (wi1 < pos)) {
			foundWiTag = wi1;
			size = 4;
			pos = wi1;
		}
		if ((tag1 > -1) && (tag1 < pos)) {
			foundWiTag = tag1;
			size = 5;
			pos = tag1;
		}
		
        // Size indica quantos caracteres devem ser armazenados. 	
        if (size > 0) {
            outResp.set(StringA.mid(text, pos, (pos + size) - 1));
        } else {
            outResp.set("");
        }
        return pos;
    }

    private boolean isValidMark(String text, int pos) {
		int eq = text.indexOf("=", foundWiTag);
		if ((eq > -1) && (eq < pos)) {
			char c = '"';
			int t1 = text.indexOf("\"", eq);
			if (t1 == -1) {
				t1 = text.length();
			}
			int t2 = text.indexOf("'", eq);
			if (t2 > -1 && t2 < t1) {
				c = '\'';
			}
			String sub = StringA.mid(text, eq, pos);
			if ((StringA.count(sub, c) % 2) != 0) {
				return false; 
			}
		}
		return true;
    }
    
    private void changePipeValue(StringBuffer resp, String obj) {
        obj = StringA.changeChars(obj, "|\"", "");
        if (obj.equalsIgnoreCase("wizard")) {
            return;
        }
        if (obj.indexOf("?") > -1) {
            String impVars = StringA.change(obj, "\"", "");
            impVars = StringA.change(impVars, "\\", "\\\\");
            if (!inScriptlet) {
            	resp.append("<w:importParameters expr=\"" + impVars+ "\"/>");
            }
        }
        if (!obj.toLowerCase().startsWith("combo.")) {
            obj = StringA.piece(obj, "?", 1).trim();
        }
        obj = StringA.change(obj, "\"", "");
        if (!obj.equals("")) {
            boolean show = true;
            if (obj.toLowerCase().startsWith("grid.") && !isGridNavigator(obj)) {
                // usado para processar um grid
                if (!inScriptlet) {
                    show = false;
                    String grid = StringA.piece(obj, ".", 2, 0);
                    resp.append("<w:generateGrid name=\"" + grid + "\"/>");
                }
            }
            if (obj.toLowerCase().startsWith("combo.")) {
                // usado para processar uma combo
                if (!inScriptlet) {
                    show = false;
                    String combo = StringA.piece(obj, ".", 2, 0); 
                    resp.append("<w:generateCombo name=\"" + combo + "\"/>");
                }
            }
            if (show) {
                // usado para mostrar o conteúdo de uma variável
                if (inScriptlet) {
                    resp.append("wiMap.get(\"" + obj + "\")");
                } else {
                    resp.append("<wi:out var=\"" + obj + "\"/>");
                }
            }
        }
    }

    private void changeFunction(StringBuffer resp, String func) {
        func = clearFunction(func);
        if (inScriptlet) {
            func = StringA.change(func, "\r", "\\r");
            func = StringA.change(func, "\n", "\\n");
            resp.append("IntFunction.executeFunctionWI(\"" + func + "\", "
                + "wiParams.getWIMap())");
        } else {
            resp.append("<wi:function expr=\"" + func + "\"/>");
        }
    }

    private String clearFunction(String func) {
        func = StringA.mid(func, 2, func.length());
        int last = func.lastIndexOf("$");
        if (last == -1) {
            last = func.length();
        }
        func = StringA.mid(func, 0, last - 1);
        func = StringA.change(func, "\\", "\\\\");
        func = StringA.change(func, "\"", "\\\"");
        return func;
    }

    private boolean isGridNavigator(String grid) {
        grid = grid.toLowerCase().trim();
        if (grid.endsWith(".bgcolor")) {
            return true;
        }
        if (grid.endsWith(".next")) {
            return true;
        }
        if (grid.endsWith(".prev")) {
            return true;
        }
        if (grid.endsWith(".from")) {
            return true;
        }
        if (grid.endsWith(".to")) {
            return true;
        }
        if (grid.endsWith(".limit")) {
            return true;
        }
        if (grid.endsWith(".size")) {
            return true;
        }
        if (grid.endsWith(".rowcount")) {
            return true;
        }
        if (grid.endsWith(".hasmore")) {
            return true;
        }
        int dot = grid.lastIndexOf(".");
        String end = StringA.mid(grid, dot, grid.length());
        if (end.startsWith(".txt")) {
            return true;
        }
        if (end.startsWith(".link")) {
            return true;
        }
        return false;
    }
}
