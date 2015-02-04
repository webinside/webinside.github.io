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

import java.util.StringTokenizer;

import org.jsoup.Jsoup;

import br.com.webinside.runtime.integration.AbstractFunction;
import br.com.webinside.runtime.util.Function;
import br.com.webinside.runtime.util.StringA;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.12 $
 */
public class TextFormat extends AbstractFunction {
    /** DOCUMENT ME! */
    private static final String REPLACE = "rep";
    /** DOCUMENT ME! */
    private static final String TRIM = "trm";
    /** DOCUMENT ME! */
    private static final String CGI = "cgi";
    /** DOCUMENT ME! */
    private static final String FILTERED = "flt";
    /** DOCUMENT ME! */
    private static final String LOWERCASE = "lwc";
    /** DOCUMENT ME! */
    private static final String UPPERCASE = "upc";
    /** DOCUMENT ME! */
    private static final String UPPERCASE_FILTERED = "ucf";
    /** DOCUMENT ME! */
    private static final String LOWERCASE_FILTERED = "lcf";
    /** DOCUMENT ME! */
    private static final String ENTER2BR = "2br";
    /** DOCUMENT ME! */
    private static final String TRUNCATE = "trunc";
    /** DOCUMENT ME! */
    private static final String LEFT = "left";
    /** DOCUMENT ME! */
    private static final String FILE = "file";
    /** DOCUMENT ME! */
    private static final String LEN = "len";
    /** DOCUMENT ME! */
    private static final String LABEL = "lbl";
    /** DOCUMENT ME! */
    private static final String CAPITALIZE = "cap";
    /** DOCUMENT ME! */
    private static final String SPLIT = "split";
    /** DOCUMENT ME! */
    private static final String IDENT = "ident";
    /** DOCUMENT ME! */
    private static final String IN_LIST = "inlist";
    /** DOCUMENT ME! */
    private static final String NOTAGS = "notags";
    /** DOCUMENT ME! */
    private static final String TAGVALUE = "tagvalue";
    /** DOCUMENT ME! */
    private static final String MSWORD = "msword";
        
    /**
     * Creates a new TextFormat object.
     */
    public TextFormat() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String execute(String[] args) {
        // Usado para campos CDATA no GridXmlOut
        if (args.length == 1) {
            return args[0];
        }

        if (args.length < 2) {
            return "";
        }

        String text = args[0];
        String action = args[1];
        String result = "";

        // Se foram passados apenas dois argumentos, entao veja quais sao as
        // possiveis acoes que podem ser realizadas
        if (args.length == 2) {
            if (action.equalsIgnoreCase(UPPERCASE_FILTERED)) {
                result = StringA.getUsAscii(text).toUpperCase();
            } else if (action.equalsIgnoreCase(LOWERCASE_FILTERED)) {
                result = StringA.getUsAscii(text).toLowerCase();
            } else if (action.equalsIgnoreCase(FILTERED)) {
                result = StringA.getUsAscii(text);
            } else if (action.equalsIgnoreCase(TRIM)) {
                result = doTrim(text);
            } else if (action.equalsIgnoreCase(CGI)) {
                result = StringA.getCgi(text);
            } else if (action.equalsIgnoreCase(UPPERCASE)) {
                result = text.toUpperCase();
            } else if (action.equalsIgnoreCase(LOWERCASE)) {
                result = text.toLowerCase();
            } else if (action.equalsIgnoreCase(ENTER2BR)) {
            	result = StringA.changeChars(text, "\r", "");
                result = StringA.change(result, "\n", "<BR>\n");
            } else if (action.equalsIgnoreCase(FILE)) {
            	result = StringA.changeChars(text, "\r\n\\/:*?\"'<>|ºª°@#$%&", "");
            	result = StringA.getUsAscii(doTrim(result.toLowerCase()));
            	result = StringA.changeChars(result, " ", "_");
	        } else if (action.equalsIgnoreCase(LEN)) {
	        	result = text.trim().length() + "";
	        } else if (action.equalsIgnoreCase(CAPITALIZE)) {
	        	String[] tokens = text.trim().toLowerCase().split("\\s");
	        	for(int i = 0; i < tokens.length; i++){
	        		if (!result.equals("")) result += " ";
	        	    char cap = Character.toUpperCase(tokens[i].charAt(0));
	        	    result +=  cap + tokens[i].substring(1, tokens[i].length());
	        	}	       
	        } else if (action.equalsIgnoreCase(IDENT)) {
	        	int size = StringA.count(text, '.');
	        	return StringA.left("", size, "-");
	        } else if (action.equalsIgnoreCase(NOTAGS)) {
	            return Jsoup.parse(text).text();
	        } else if (action.equalsIgnoreCase(TAGVALUE)) {
	            return StringA.change(text, "\"", "&quot;");
	        } else if (action.equalsIgnoreCase(MSWORD)) {
	        	String s = text;
	    		s = s.replace((char)145, (char)'\''); // left single
	    		s = s.replace((char)8216, (char)'\''); // left single
	    		s = s.replace((char)146, (char)'\''); // right single
	    		s = s.replace((char)8217, (char)'\''); // right single
	    		s = s.replace((char)147, (char)'\"'); // left double
	    		s = s.replace((char)8220, (char)'\"'); // left double
	    		s = s.replace((char)148, (char)'\"'); // right double
	    		s = s.replace((char)8221, (char)'\"'); // right double
	    		s = s.replace((char)150, (char)'-'); // em dash
	    		s = s.replace((char)8211, (char)'-'); // em dash    
	    		s = s.replace((char)151, (char)'-'); // em dash
	    		s = s.replace((char)8212, (char)'-'); // em dash    
	    		return s;
	        } 
        } else if (args.length == 3) {
        	if (action.equalsIgnoreCase(LEFT)) {
        		int size = Function.parseInt(args[2]);
        		result = doLeft(text, size, "");
	        } else if (action.equalsIgnoreCase(LABEL)) {
	        	String type = args[2].trim();
	        	if (type.equalsIgnoreCase("date")) {
	        		String[] args2 = { args[0], "FMTdmy" };
	        		result = new DateFormat().execute(args2);
	        	} else if (type.equalsIgnoreCase("datehm")) {
	        		String[] args2 = { args[0], "FMTdmyhm" };
	        		result = new DateFormat().execute(args2);
	        	} else if (type.equalsIgnoreCase("datehms")) {
	        		String[] args2 = { args[0], "FMTdmyhms" };
	        		result = new DateFormat().execute(args2);
	        	} else if (type.toLowerCase().startsWith("timesec")) {
	        		String newName = type.toLowerCase().replace("time", "t"); 
	        		String[] args2 = { args[0], "FMT" + newName };
	        		result = new DateFormat().execute(args2);
	        	} else if (type.toLowerCase().startsWith("decimal")) {
	        		String mask = type.toLowerCase().replace("decimal", "cbr"); 
	        		if (mask.equals("cbr")) mask = "cbr2";
	        		String[] args2 = { args[0], mask };
	        		result = new NumberFormat().execute(args2); 
	        	} else {
	        		result = args[0];
	        	}
	        	if (result.equals("")) result = "&nbsp;";
        	} else if (action.equalsIgnoreCase(SPLIT)) {
        		result = split(text, Function.parseInt(args[2]), " ");
	        } else if (action.equalsIgnoreCase(IDENT)) {
	        	int size = StringA.count(text, '.');
	        	result = StringA.left("", size, args[2]);
	        } else if (action.equalsIgnoreCase(IN_LIST)) {
	        	result = inList(text, args[2]);
        	}
        } else if (args.length == 4) {
        	if (action.equalsIgnoreCase(LEFT)) {
        		int size = Function.parseInt(args[2]);
        		result = doLeft(text, size, args[3]);
        	} else if (action.equalsIgnoreCase(REPLACE)) {
                result = doReplace(text, args[2], args[3]);
            } else if (action.equalsIgnoreCase(TRUNCATE)) {
                int tam = Integer.parseInt(args[2]);
                result = (text.length() > tam) ? 
                		(text.substring(0, tam) + args[3]) : text;
        	} else if (action.equalsIgnoreCase(SPLIT)) {
        		result = split(text, Function.parseInt(args[2]), args[3]);
        	} else if (action.equalsIgnoreCase(LABEL)) {
        		if (args[2].trim().equalsIgnoreCase(IN_LIST)) {
        			result = inList(text, args[3]);
        		}	
        	}
        }
        return result;
    }

    private String doReplace(String text, String oldSeq, String newSeq) {
        return StringA.change(text, StringA.showLineBreak(oldSeq),
            StringA.showLineBreak(newSeq));
    }

    private String doTrim(String str) {
        java.util.StringTokenizer st = new java.util.StringTokenizer(str, " ");
        StringBuffer sb = new StringBuffer();
        boolean firstToken = true;
        while (st.hasMoreTokens()) {
            if (!firstToken) {
                sb.append(' ');
            }
            sb.append(st.nextToken());
            if (firstToken) {
                firstToken = false;
            }
        }
        return sb.toString();
    }

    private String doLeft(String str, int size, String compl) {
    	if (str == null) str = "";
    	int len = str.length();
    	if (len <= size) return str;
    	if (str.charAt(size-1) != ' ') {
    		int newSize = str.lastIndexOf(" ", size-1);
    		if (newSize > -1) {
    			size = newSize;
    		}
    	}
    	return str.substring(0, size).trim() + compl;
    }
    
    private String split(String text, int qnt, String sep) {
    	if (qnt<1) return text;
    	StringBuilder resp = new StringBuilder();
    	for (int i = 0; i < text.length(); i++) {
    		if (i%qnt == 0 && resp.length() > 0) resp.append(sep);
			resp.append(text.charAt(i));
		}
    	return resp.toString();
    }

    private String inList(String text, String values) {
    	StringTokenizer st = new StringTokenizer(values, ":");
    	while (st.hasMoreTokens()) {
    		String token = st.nextToken();
    		String p1 = StringA.piece(token, "=", 1).trim();
    		String p2 = StringA.piece(token, "=", 2).trim();
    		if (text.equalsIgnoreCase(p1)) return p2;
    	}
    	return "";
    }
    
}
