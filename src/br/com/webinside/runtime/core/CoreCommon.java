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

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import br.com.webinside.runtime.component.AbstractActionElement;
import br.com.webinside.runtime.component.Database;
import br.com.webinside.runtime.component.Databases;
import br.com.webinside.runtime.exception.QueryTimeoutException;
import br.com.webinside.runtime.integration.Condition;
import br.com.webinside.runtime.integration.DatabaseHandler;
import br.com.webinside.runtime.integration.LogsGenerator;
import br.com.webinside.runtime.integration.Validator;
import br.com.webinside.runtime.util.ErrorLog;
import br.com.webinside.runtime.util.I18N;
import br.com.webinside.runtime.util.WIMap;

/**
 * Classe abstrata que serve de base aos componentes
 *
 * @author Luiz Ruiz
 */
public abstract class CoreCommon implements CoreCommonInterface {
    /** DOCUMENT ME! */
    protected AbstractActionElement element;
    /** DOCUMENT ME! */
    protected WIMap wiMap;
    /** DOCUMENT ME! */
    protected ExecuteParams wiParams;
    /** DOCUMENT ME! */
    protected long initialTime;

    /**
     * Creates a new CoreCommon object.
     */
    public CoreCommon() {
        initialTime = new Date().getTime();
    }

    /**
     * Valida a condição do elemento
     *
     * @return Resultado da avaliação da condição
     */
    public boolean isValidCondition() {
    	boolean ret = false;
        if (!isDisabledCondition()) {
	        if (wiParams != null && element != null) {
		        wiMap = wiParams.getWIMap();
	            String cond = element.getCondition();
	            ret = new Condition(wiMap, cond).execute();
	        }
	        if (wiMap != null && ret == false) {
	        	wiMap.put("wi.debug.core.status", "condition false");
	        }
        }
        return ret;
    }	

    /**
     * Valida se a condição está desativada em função do validation
     *
     * @return Resultado da checagem da condição estar desativada
     */
    protected boolean isDisabledCondition() {
    	boolean ret = false;
        if (wiParams != null && element != null) {
	        wiMap = wiParams.getWIMap();
	        ret = Validator.isDisabledCondition(wiMap, element.getCondition());
	        if (ret) {
	        	if (wiMap.get("wi.block.cond").equals("false")) {
		        	wiMap.put("wi.debug.core.status", "disabled by block");
	        	} else {
		        	wiMap.put("wi.debug.core.status", "disabled by invalid");
	        	}
	        }
        }
        return ret;
    }
    
    /**
     * Grava o log do elemento
     */
    protected void writeLog() {
        String engineLog = wiParams.getProject().getRequestLog();
        if (engineLog.trim().equals("")) {
            return;
        }
        String logDir = wiParams.getErrorLog().getParentDir();
        LogsGenerator log = LogsGenerator.getInstance(logDir, "engine.log");
        Map<String, String> logAttrs = new LinkedHashMap<String, String>();
		logAttrs.put("PAGE", wiParams.getPage().getId());
		logAttrs.put("ELEMENT", element.getDescription());
		logAttrs.put("TIME", (new Date().getTime() - initialTime) + " ms");
        log.write(logAttrs, null);
    }
    
    /**
     * Tratamento para quando ocorre uma Exceção na consulta
     */
    protected void queryException(Exception ex, 
    		DatabaseHandler db, String desc) {
	    wiMap.put("wi.sql.query", db.getExecutedSQL());
	    wiMap.put("wi.sql.error", db.getErrorMessage());
	    String jspFile = wiMap.get("wi.jsp.filename");
        String msgDetail = db.getErrorMessage() + 
        	"\r\n--- SQL ---\r\n" + db.getExecutedSQL();
        WIMap psMap = db.getExecutedSQLParams(wiMap); 
        if (psMap.keySet().size() > 0) {
        	msgDetail += "\r\n--- PARAMS ---\r\n";
        	msgDetail += psMap.toString();
        }	
        wiParams.getErrorLog().write("Page: " + jspFile, desc, msgDetail);
	    boolean abort = false;
        Database database = 
        	wiParams.getProject().getDatabases().getDatabase(db.getId());
	    String qtMessage = database.getQTMessage();
	    if (ex != null && !qtMessage.trim().equals("")
	    		&& ex.getMessage().trim().indexOf(qtMessage.trim()) > -1) {
	    	abort = true;
	        String label = 
	        	new I18N().get("Tempo de consulta excedido");
	    	ex = new QueryTimeoutException(label + " (" + db.getId() + ")", ex);
	    }
	    if (!wiParams.getPage().getErrorPageName().equals("") || abort) {
	        wiParams.setRequestAttribute("wiException", ex);
	    }
    }    

    protected void dbTime(DatabaseHandler db, WIMap wiMap, 
    		long ini, long fim, String msg) {
    	Databases dbs = wiParams.getProject().getDatabases(); 
    	Database database = dbs.getDatabase(db.getId());    	
	    if ((fim - ini) > (1000 * database.getDBTimeLog())) {
	        String logId =
	            wiParams.getProject().getId() + ":" 
	            + wiParams.getPage().getId();
	        logId = logId + " in " + msg;
	        String logDir = wiParams.getErrorLog().getParentDir();
	        ErrorLog log = ErrorLog.getInstance(logDir, "dbtime.log");
	        String msgId = logId + " (" + (fim - ini) + " ms)";
	        String msgDetail = db.getExecutedSQL();
	        WIMap psMap = db.getExecutedSQLParams(wiMap); 
	        if (psMap.keySet().size() > 0) {
	        	msgDetail += "\r\n--- PARAMS ---\r\n";
	        	msgDetail += psMap.toString();
	        }	
	        log.write(getClass().getSimpleName(), msgId, msgDetail); 
	    }
    }    
    
}
