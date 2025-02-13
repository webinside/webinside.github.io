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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

import br.com.webinside.runtime.util.AbstractLog;
import br.com.webinside.runtime.util.StringA;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.4 $
 */
public class LogsGenerator extends AbstractLog {

	private LogsGenerator(File f) {
		super(f);
	}
	
	/**
	 * Cria um novo LogsGenerator.
	 *
	 * @param parentDir o diretório do arquivo de log.
	 * @param filename o nome do arquivo de log.
	 *
	 * @return o objeto LogsGenerator ou null se o arquivo não pôde ser construído.
	 */
	public static LogsGenerator getInstance(String parentDir, String filename) {
		File f = new File(parentDir, filename);
		LogsGenerator logsGenerator = null;
		if ((parentDir != null) && (filename != null)) {
			String path = f.getAbsolutePath();
			try {
				logsGenerator = 
					(LogsGenerator) AbstractLog.logPool.get(path);
				if (logsGenerator != null && !f.isFile()) {
					logsGenerator.close();
					logsGenerator = null;
				}
			} catch (ClassCastException err) {
				err.printStackTrace(System.err);
				// Nunca deve ocorrer.
			}
			if (logsGenerator == null) {
				logsGenerator = new LogsGenerator(f);
				AbstractLog.logPool.put(path, logsGenerator);
			}
		}
		return logsGenerator;
	}

	public void write(Map<String, String> atributes, String content) {
        if (getOut() != null) {
	        synchronized (AbstractLog.logPool) {
	            try {
	                StringBuffer msg = new StringBuffer();
	                msg.append("<LOG ").append(logDate());
	                for (Map.Entry<String, String> entry : atributes.entrySet()) {
	                	String key = entry.getKey().toUpperCase().trim();
	                	String value = entry.getValue();
		                if (value != null && !value.trim().equals("")) {
		                	String xmlValue = StringA.getXml(value.trim());
		                    msg.append(" " + key + "=\"").append(xmlValue).append("\"");
		                }
					}
	                if (content != null && !content.trim().equals("")) {
		                msg.append(">\r\n");
	                    msg.append("  " + content.trim() + "\r\n");
		                msg.append("</LOG>\r\n");
	                } else {
		                msg.append("\\>\r\n");
	                }
	                getOut().write(msg.toString());
	                getOut().flush();
	            } catch (FileNotFoundException err) {
					err.printStackTrace(System.err);
					// Nunca deve ocorrer
	            } catch (IOException err) {
	                close();
	            }
	        }
        }
    }
	
	public void write(Map<String, String> atributes, String text, String detail) {
        StringBuffer content = new StringBuffer();
        if (text != null && !text.equals("")) {
        	content.append("<TEXT><![CDATA[").append(text).append("]]></TEXT>\r\n");
        }
        if (detail != null && !detail.equals("")) {
        	content.append("<DETAIL><![CDATA[").append(detail).append("]]></DETAIL>\r\n");
        }
        write(atributes, content.toString());
    }
	
}
