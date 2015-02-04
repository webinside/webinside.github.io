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

import java.math.BigDecimal;

import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

// Supports =,!=,==,>,>=,!>,<,<=,!< and ()
// Pode usar conjuntos ...&&...&&...  ("and" todos)
// Pode usar conjuntos ...||...||... ("or" todos)
public class Condition {
    private WIMap wiMap;
    private String expression;
    private Producer producer = new Producer();

    /**
     * Creates a new Condition object.
     *
     * @param wiMap DOCUMENT ME!
     * @param expression DOCUMENT ME!
     */
    public Condition(WIMap wiMap, String expression) {
        if (wiMap == null) {
            wiMap = new WIMap();
        }
        if (expression == null) {
            expression = "";
        }
        this.wiMap = wiMap.cloneMe();
        this.expression = expression;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    private boolean isValid() {
        int open = StringA.count(expression, '(');
        int close = StringA.count(expression, ')');
        if (open != close) {
            return false;
        }
        return true;
    }

    public boolean execute() {
        if (!isValid()) return false;
        String aux = cleanExpression();
        return recursive(aux);
    }

    private boolean inPipe(String aux, int pos) {
        if (pos == -1) return false;
        if (aux.equals("")) return false;
        boolean resp = inFunction(aux, pos);
        if (!resp) {
            String txt = StringA.mid(aux, 0, pos);
            int before = StringA.count(txt, '|');
            if ((before % 2) == 0) {
                return false;
            }
        }
        return true;
    }

    private boolean inText(String aux, int pos) {
        if (pos == -1) return false;
        if (aux.equals("")) return false;
        String txt = StringA.mid(aux, 0, pos);
        int before = StringA.count(txt, '"');
        if ((before % 2) == 0) {
        	return false;
        }
        return true;
    }
    
    private boolean inFunction(String aux, int pos) {
        if (pos == -1) return false;
        if (aux.equals("")) return false;
        int openBefore = StringA.count(StringA.mid(aux, 0, pos), "|$", false);
        int closeBefore = StringA.count(StringA.mid(aux, 0, pos), "$|", false);
        int openAfter =
            StringA.count(StringA.mid(aux, pos, aux.length()), "|$", false);
        int closeAfter =
            StringA.count(StringA.mid(aux, pos, aux.length()), "$|", false);
        return ((openBefore - closeBefore) != (openAfter - closeAfter));
    }

    private boolean checkOr(String aux, int pos) {
        if (pos == -1) return false;
        if (aux.equals("")) return false;
    	String p1 = StringA.mid(aux, 0, pos - 1);
    	int count = StringA.count(p1, '|');
    	return (count % 2 == 0);
    }
    
    private String cleanExpression() {
        String aux = expression;
        int pos = aux.lastIndexOf("(", aux.length());
        int last = aux.indexOf(")", pos);
        while ((pos > -1) && (last > -1)) {
            while (inPipe(aux, pos)) {
                pos = aux.lastIndexOf("(", pos - 1);
            }
            last = aux.indexOf(")", pos);
            while (inPipe(aux, last)) {
                last = aux.indexOf(")", last + 1);
                if (last == -1) {
                    last = aux.length();
                    break;
                }
            }
            if (inText(aux, last)) {
                pos = aux.lastIndexOf("(", pos - 1);
            	continue;
            }
            String start = StringA.mid(aux, 0, pos - 1);
            String mid = StringA.mid(aux, pos + 1, last - 1);
            String end = StringA.mid(aux, last + 1, aux.length() - 1);
            boolean bpart = recursive(mid);
            aux = start + " " + bpart + " " + end;
            pos = aux.lastIndexOf("(", aux.length());
        }
        return aux;
    }

    private boolean recursive(String condition) {
        int pos = -1;
        while ((pos = condition.indexOf("||", pos + 1)) > -1) {
            if (!inFunction(condition, pos)) {
            	if (checkOr(condition, pos)) {
            		return or(condition);
            	}	
            }
        }
        pos = -1;
        while ((pos = condition.indexOf("&&", pos + 1)) > -1) {
            if (!inFunction(condition, pos)) {
                return and(condition);
            }
        }
        pos = -1;
        while ((pos = condition.indexOf(">", pos + 1)) > -1) {
            if (!inFunction(condition, pos)) {
                if ((pos > 0) && (condition.charAt(pos - 1) == '!')) {
                    pos = pos - 1;
                }
                return number(condition, pos, 1);
            }
        }
        pos = -1;
        while ((pos = condition.indexOf("<", pos + 1)) > -1) {
            if (!inFunction(condition, pos)) {
                if ((pos > 0) && (condition.charAt(pos - 1) == '!')) {
                    pos = pos - 1;
                }
                return number(condition, pos, -1);
            }
        }
        pos = -1;
        while ((pos = condition.indexOf("=", pos + 1)) >= 0) {
            if (!inFunction(condition, pos)) {
                if (pos > 0) {
                	char before = condition.charAt(pos - 1); 
                	if (before == '!' || before == '#') {
                		pos = pos - 1;
                	}
                }
                if (!inPipe(condition, pos)) {
                    return equal(condition, pos);
                }
            }
        }
        ProducerParam param = new ProducerParam();
        param.setInput(condition);
        param.setWIMap(wiMap);
        producer.setParam(param);
        producer.execute();
        String result = param.getOutput();
        if (result.trim().equalsIgnoreCase("TRUE")) {
            return true;
        }
        return false;
    }

    // Usado para igual "="(uppercase), "==" (exact), 
    // "#=" (natural key) e "!=" (diferente)
    private boolean equal(String cond, int pos) {
        ProducerParam param = new ProducerParam();
        param.setWIMap(wiMap);
        producer.setParam(param);
        String seq = StringA.mid(cond, pos, pos + 1);
        boolean inverse = false; 
        int size = 1;
        if (seq.equals("==") || seq.equals("#=") || seq.equals("!=")) {
        	if (seq.equals("!=")) inverse = true;
        	size = 2;
        }
        param.setInput(StringA.mid(cond, 0, pos - 1));
        producer.execute();
        String po1 = cleanText(param.getOutput());
        param.setInput(StringA.mid(cond, pos + size, cond.length()));
        producer.execute();
        String po2 = cleanText(param.getOutput());
        if (seq.equals("#=")) {
        	BigDecimal ipo1 = new BigDecimal(0);
        	BigDecimal ipo2 = new BigDecimal(0);
            try {
                ipo1 = new BigDecimal(po1);
            } catch (NumberFormatException err) { /*ignorado*/ }
            try {
                ipo2 = new BigDecimal(po2);
            } catch (NumberFormatException err) { /*ignorado*/ }
            return (ipo1.compareTo(ipo2) == 0);
        }
        if (seq.equals("==")) return po1.equals(po2);
        return po1.equalsIgnoreCase(po2) ^ inverse;
    }

    // Usado para: ">", ">=", "!>" e "<", "<=", "!>"
    private boolean number(String cond, int pos, int compare) {
        ProducerParam param = new ProducerParam();
        param.setWIMap(wiMap);
        producer.setParam(param);
        String seq = StringA.mid(cond, pos, pos + 1);
        boolean inverse = false; 
        int size = 1;
        if (seq.equals(">=") || seq.equals("!>")) {
        	if (seq.equals(">=")) compare = compare * (-1);
        	inverse = true;
        	size = 2;
        } else if (seq.equals("<=") || seq.equals("!<")) {
        	if (seq.equals("<=")) compare = compare * (-1);
        	inverse = true;
        	size = 2;
        }
        param.setInput(StringA.mid(cond, 0, pos - 1));
        producer.execute();
        String po1 = cleanNumber(param.getOutput());
        param.setInput(StringA.mid(cond, pos + size, cond.length()));
        producer.execute();
        String po2 = cleanNumber(param.getOutput());
        BigDecimal ipo1 = new BigDecimal(0);
        BigDecimal ipo2 = new BigDecimal(0);
        try {
            ipo1 = new BigDecimal(po1);
        } catch (NumberFormatException err) { /*ignorado*/ }
        try {
            ipo2 = new BigDecimal(po2);
        } catch (NumberFormatException err) { /*ignorado*/ }
        return (ipo1.compareTo(ipo2) == compare) ^ inverse;
    }

    private boolean and(String cond) {
        int ini = 0;
        int pos = -1;
        boolean resp = true;
        while ((pos = cond.indexOf("&&", pos + 1)) > -1) {
            if (!inFunction(cond, pos)) {
                String piece = StringA.mid(cond, ini, pos - 1);
                if (!recursive(piece)) resp = false;
                ini = pos + 2;
            }
        }
        String piece = StringA.mid(cond, ini, cond.length());
        if (!recursive(piece)) resp = false;
        return resp;
    }

    private boolean or(String cond) {
        int ini = 0;
        int pos = -1;
        boolean resp = false;
        while ((pos = cond.indexOf("||", pos + 1)) > -1) {
            if (!inFunction(cond, pos)) {
            	if (checkOr(cond, pos)) {
	                String piece = StringA.mid(cond, ini, pos - 1);
	                if (recursive(piece)) resp = true;
	                ini = pos + 2;
            	}
            }
        }
        String piece = StringA.mid(cond, ini, cond.length());
        if (recursive(piece)) resp = true;
        return resp;
    }

    private String cleanText(String text) {
    	String resp = text.trim();
    	if (resp.startsWith("\"") && resp.endsWith("\"")) {
    		resp = StringA.mid(resp, 1, resp.length() - 2);
    	}
    	return resp.trim();
    }

    private String cleanNumber(String number) {
        String aux = "0123456789.-";
        StringA response = new StringA();
        for (int i = 0; i < number.length(); i++) {
            char let = number.charAt(i);
            if (aux.indexOf(let) > -1) {
                response.append(let);
            }
        }
        String ini = response.piece(".", 1);
        String end = response.piece(".", 2, 0);
        end = new StringA(end).changeChars(".", "");
        if (end.equals("")) {
            end = "0";
        }
        return ini + "." + end;
    }
    
}
