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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletContext;

import br.com.webinside.runtime.core.ExecuteParams;

/**
 * Classe utilizada para manipular arquivos do WI de log de erros.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.5 $
 *
 * @since 3.0
 */
public class ErrorLog extends AbstractLog {

	protected ErrorLog(File f) {
		super(f);
	}
	
	/**
	 * Cria um novo ErrorLog.
	 *
	 * @param parentDir o diretório onde será criado o log. 
	 *
	 * @return o objeto ErrorLog.
	 */
	public static ErrorLog getInstance(String parentDir) {
		return getInstance(parentDir, "webinside.log");
	}

	/**
	 * Cria um novo ErrorLog.
	 *
	 * @param parentDir o diretório do arquivo de log.
	 * @param filename o nome do arquivo de log.
	 *
	 * @return o objeto ErrorLog ou null se o arquivo não pôde ser construído.
	 */
	public static ErrorLog getInstance(String parentDir, String filename) {
		File f = new File(parentDir, filename);
		ErrorLog errorLog = null;
		if ((parentDir != null) && (filename != null)) {
			String path = f.getAbsolutePath();
			try {
				errorLog = (ErrorLog) AbstractLog.logPool.get(path);
			} catch (ClassCastException err) {
				err.printStackTrace(System.err);
				// Nunca deve ocorrer.
			}
			if (errorLog == null) {
				errorLog = new ErrorLog(f);
				AbstractLog.logPool.put(path, errorLog);
			}
		}
		return errorLog;
	}

    /**
     * Efetua o registro do erro no arquivo de log.
     *
     * @param className o nome da classe que gerou o erro.
     * @param id um identificador para facilitar a localização.
     * @param err o erro gerado.
     */
    public void write(String className, String id, Throwable err) {
        StringWriter str = new StringWriter();
        if (err.getCause() != null) {
            String msg = err.getMessage();
            if (msg !=null && !msg.trim().equals("")) {
                str.write(err.getCause().toString() + ": " + msg + "\r\n");
            }
            err.getCause().printStackTrace(new PrintWriter(str));
        } else {
            err.printStackTrace(new PrintWriter(str));
        }
        writeLog(StringA.piece(str.toString(), ".HttpServlet.", 1) + "...",
            className, id);
    }

    /**
     * Efetua o registro do erro no arquivo de log.
     *
     * @param className o nome da classe que gerou o erro.
     * @param id um identificador para facilitar a localização.
     * @param err o erro gerado.
     */
    public void write(String className, String id, Error err) {
        StringWriter str = new StringWriter();
        err.printStackTrace(new PrintWriter(str));
        writeLog(StringA.piece(str.toString(), ".HttpServlet.", 1) + "...",
            className, id);
    }

    /**
     * Efetua o registro do erro no arquivo de log.
     *
     * @param className o nome da classe que gerou o erro.
     * @param id um identificador para facilitar a localização.
     * @param message a mensagem do erro gerado.
     */
    public void write(String className, String id, String message) {
        writeLog(message, className, id);
    }

    private void writeLog(String message, String cl, String id) {
        if (getOut() != null) {
            synchronized (AbstractLog.logPool) {
                try {
                	boolean useConsole = false;
                    StringBuffer msg = new StringBuffer();
                    msg.append("<LOG " + logDate() + " CLASS=\"");
                    msg.append(StringA.getXml(cl) + " - " + StringA.getXml(id));
                    msg.append("\">\r\n");
                	ExecuteParams wiParams = ExecuteParams.get();
                	if (wiParams != null && wiParams.getWIMap() != null) {
                    	WIMap wiMap = wiParams.getWIMap();
                    	ServletContext sc = wiParams.getServletContext();
                        if (sc != null) {
                        	String extScKey = "errorLogVariablesExternal";
                        	String extVars = getELV(sc, wiMap, extScKey);
                        	if (!extVars.equals("")) {
                        		String fid = 
                        			Function.getDate("yyyyMMdd_HHmmss_SSS");
                        		String fname = "vars_" + fid + ".log";
                        		String file = getParentDir() + "/" + fname;
                        		new FileIO(file, 'W').writeText(extVars);
                                msg.append("<VARSFILE>");
                        		msg.append(fname);
                                msg.append("</VARSFILE>\r\n");
                        	}
                        	String scKey = "errorLogVariables";
                        	String vars = getELV(sc, wiMap, scKey);
                        	if (!vars.equals("")) {
                                msg.append("<VARS><![CDATA[\r\n");
                        		msg.append(vars);
                                msg.append("]]</VARS>\r\n");
                        	}
                        }
                		String eoc = wiMap.get("pvt.debug.errorOnConsole");
                    	if (eoc.equalsIgnoreCase("true")) {
                    		useConsole = true;
                    	}
                		String userAgent =  wiMap.get("wi.request.user-agent");
                		if (!userAgent.equals("")) {
                			msg.append("<USER-AGENT>" + userAgent);
                			msg.append("</USER-AGENT>\r\n");
                		}	
                        String parentPage = wiMap.get("wi.jsp.filename_parent");
                        if (!parentPage.equals("")) {
                        	msg.append("<PARENT-PAGE>" + parentPage);
                        	msg.append("</PARENT-PAGE>\r\n");
                        }
                	}
                    msg.append("<TEXT><![CDATA[" + message + "]]></TEXT>\r\n");
                    msg.append("</LOG>\r\n");
                    String fullMessage = msg.toString();
                    if (useConsole) {
                    	System.out.println(fullMessage.trim());
                    }
                    getOut().write(fullMessage);
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
        
    private String getELV(ServletContext sc, WIMap wiMap, String scKey) {
    	StringBuffer msg = new StringBuffer();
        String elv = sc.getInitParameter(scKey);
        if (elv != null && !elv.trim().equals("")) {
            String[] elvArray = elv.split(",");
            if (elvArray.length > 0) {
                for (int i = 0; i < elvArray.length; i++) {
                	String key = elvArray[i].trim();
                	if (key.endsWith("*") || key.endsWith(".")) {
                		msg.append(wiMap.getAsText(key, false));
                	} else {
                		msg.append(key + " = ");
                		msg.append(wiMap.get(key)).append("\r\n");
                	}
                }
            }
        }
        return msg.toString().trim();
    }
        
}
