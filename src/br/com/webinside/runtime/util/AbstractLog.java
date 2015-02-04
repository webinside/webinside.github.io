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

package br.com.webinside.runtime.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Classe abstrata para gerar logs.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.1 $
 */
public abstract class AbstractLog {
    private File file;
    private FileWriter out;
    // o pool pode ser acessado por quem herdar essa classe
    protected static final Map logPool = 
    	Collections.synchronizedMap(new HashMap());

    /**
     * Construtor padrão.
     */
	protected AbstractLog(File f) {
		file = f;
		try {
			f.getParentFile().mkdirs();        	
			out = new FileWriter(file, true);
		} catch (IOException err) {
			out = null;
		}
	}
	
    /**
     * Retorna o FileWriter. 
     */
	public FileWriter getOut() {
		return out;
	}
    
    /**
	 * Retorna o diretório do arquivo de log.
	 *
	 * @return o diretório do arquivo de log.
	 */
	public String getParentDir() {
		String dir = file.getParent();
		dir = StringA.change(dir, '\\', '/');
		if (!dir.endsWith("/")) {
			dir = dir + "/";
		}
		return dir;
	}

    /**
     * Limpar o arquivo de log.
     */
    protected void clear() {
    	synchronized (logPool) {
	        if (out != null) {
	            try {
	            	out.close();
	    			out = new FileWriter(file);
	    			out.write("");
	    			out.close();
	    			out = new FileWriter(file, true);
	            } catch (Exception err) {
	                err.printStackTrace(System.err);
	                // Nunca deve ocorrer.
	            }
	        }
    	}
    }

    /**
     * Liberar o arquivo de log.
     */
    protected void close() {
    	synchronized (logPool) {
    		logPool.remove(file.getAbsolutePath());
	        if (out != null) {
	            try {
	                out.close();
	                out = null;
	            } catch (Exception err) {
	                err.printStackTrace(System.err);
	                // Nunca deve dar erro.
	            }
	        }
    	}
    }
        
    /**
     * Retorna os atributos date e time para usar no log. 
     *
     * @return os atributos date e time.
     */
    public static String logDate() {
        String sdate = Function.getDate("yyyy-MM-dd,HH:mm:ss");
        return "DATE=\"" + sdate.substring(0, 10) + "\" TIME=\""
        + sdate.substring(11, sdate.length()) + "\"";
    }

    /**
     * Limpar um arquivo de log.
     */
    public static void clear(File f) {
    	AbstractLog log = (AbstractLog)logPool.get(f.getAbsolutePath());
    	if (log != null) {
    		log.clear();
    	} else {
    		try {
    			FileWriter out = new FileWriter(f);
				out.write("");
				out.close();
            } catch (Exception err) {
                err.printStackTrace(System.err);
                // Nunca deve ocorrer.
            }
    	}
    }

    /**
     * Liberar um arquivo de log.
     */
    public static void close(File f) {
    	AbstractLog log = (AbstractLog)logPool.get(f.getAbsolutePath());
    	if (log != null) {
    		log.close();
    	}
    }

    /**
     * Liberar todos os arquivo de log.
     */
    public static void closeAll() {
    	synchronized (logPool) {
    	    Map m = new HashMap(logPool);
    	    try {
                for (Iterator it = m.entrySet().iterator(); it.hasNext();) {
                    Map.Entry e = (Entry) it.next();
                    AbstractLog log = (AbstractLog) e.getValue();
                    log.close();
                } 
            } catch (Exception e) {
                e.printStackTrace();
            } 
    	}
    }
        
}
