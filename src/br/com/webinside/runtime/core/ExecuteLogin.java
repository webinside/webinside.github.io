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

import java.util.Map;

import br.com.webinside.runtime.component.AbstractProject;
import br.com.webinside.runtime.database.ResultSet;
import br.com.webinside.runtime.integration.DatabaseHandler;
import br.com.webinside.runtime.integration.IntFunction;
import br.com.webinside.runtime.integration.Producer;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.util.Crypto;
import br.com.webinside.runtime.util.I18N;
import br.com.webinside.runtime.util.SingleSignOnRepository;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.8 $
 */
public class ExecuteLogin {
    private ExecuteParams wiParams;
    /** DOCUMENT ME! */
    AbstractProject project;

    /**
     * Creates a new ExecuteLogin object.
     *
     * @param wiParams DOCUMENT ME!
     */
    public ExecuteLogin(ExecuteParams wiParams) {
        this.wiParams = wiParams;
        this.project = wiParams.getProject();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean execute(boolean doLogin) {
        WIMap wiMap = wiParams.getWIMap();
        wiMap.put("wi.login.accept", doLogin + "");
        String keyMD5 = wiMap.get("wi.pwd.md5");
        if (project.getLoginCrypto().equals("MD5")) {
        	wiMap.remove("wi.pwd.md5");
        }
        String keySHA1 = wiMap.get("wi.pwd.sha1");
        if (project.getLoginCrypto().equals("SHA1")) {
        	wiMap.remove("wi.pwd.sha1");
        }
        // pagina de login sempre é valida 
        String loginPage = StringA.piece(project.getLoginPage(), ".wsp", 1);
        if (wiParams.getPage().getId().equals(loginPage)) {
            return true;
        }
        if (wiParams.isJspInclude()) {
        	return true;
        }
        String addr = wiParams.getHttpRequest().getRemoteAddr();
        checkSingleSignOn(wiMap, addr);
        String logip = wiMap.get("pvt.wilogin");
        if (logip.equals(addr)) {
            return true;
        }
        if (!doLogin) {
        	if (!wiParams.getPage().getNoLogin().equals("ON")) {
                invalidLogin();
                return false;
        	} else {
        		return true;
        	}
        }
        // tenta efetuar o login
        if (project.getLoginType().equals("DBUSER")) {
            return loginDBUser();
        }

        // Abaixo segue o login convencional
        String pjsql = StringA.changeChars(project.getLoginSql(), "\r\n", "  ");
        if (pjsql.trim().equals("")) {
            invalidLogin();
            return false;
        }
        String dbalias = project.getLoginDatabase();
        DatabaseHandler db = wiParams.getDatabaseAliases().get(dbalias);
        if ((db == null) || (!db.isConnected())) {
            EngFunction.databaseError(wiParams, dbalias);
            return false;
        }
        db.setCharFilter(project.getLoginSqlFilter(), "");
        ResultSet rs = null;
        try {
            rs = db.execute(project.getLoginSql(), wiMap, 1, 1);
        } catch (Exception err) {
        	wiParams.getErrorLog().write("ExecuteLogin", "SqlLogin", err);
        }
        if (rs != null) {
            boolean ok = false;
            int pos = rs.next();
            if (pos == 1) {
            	int validPos = 1;
                if (project.getLoginCrypto().equals("MD5")) {
                    String dbpass = rs.column(validPos);
                    String encoded = Crypto.encodeMD5(dbpass + keyMD5);
                    if (encoded.equals(wiMap.get("tmp.pass"))) ok = true;
                } else if (project.getLoginCrypto().equals("SHA1")) {
                    String dbpass = rs.column(validPos);
                    String encoded = Crypto.encodeSHA1(dbpass + keySHA1);
                    if (encoded.equals(wiMap.get("tmp.pass"))) ok = true;
                } else {
                	validPos = 0;
                    ok = true;
                }
                if (ok) {
                    addr = wiParams.getHttpRequest().getRemoteAddr();
                    wiMap.put("pvt.wilogin", addr);
                    wiMap.remove("pvt.login.accept");
                    // populando colunas adicionais
                    String[] names = rs.columnNames();
                    for (int i=validPos; i< names.length; i++) {
                    	String name = names[i];
                    	wiMap.put("pvt.login." + name, rs.column(i+1));
                    }
                	IntFunction.loginRoles(wiParams);
                }
            }
            if (!ok) {
                invalidLogin();
                return false;
            }
        } else {
            wiMap.put("wi.sql.query", db.getExecutedSQL());
            String sqlmsg = db.getErrorMessage();
            wiMap.put("wi.sql.error", sqlmsg.toString());
            wiMap.put("wi.sql.msg", StringA.piece(sqlmsg, ")", 2, 0).trim());
            invalidLogin();
            return false;
        }
        return true;
    }

    private boolean loginDBUser() {
        String dbalias = project.getLoginDatabase();
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiParams.getWIMap());
        DatabaseHandler db = wiParams.getDatabaseAliases().get(dbalias);
        if (db == null) {
            EngFunction.databaseError(wiParams, dbalias);
            return false;
        }
        if (!db.isConnected()) {
            invalidLogin();
            return false;
        }
        WIMap wiMap = wiParams.getWIMap();
        wiMap.put("pvt.wilogin", wiParams.getHttpRequest().getRemoteAddr());
        wiMap.remove("pvt.login.accept");
    	IntFunction.loginRoles(wiParams);
        return true;
    }

    private void invalidLogin() {
    	String msg = project.getLoginMessage().trim();
        String resp = Producer.execute(wiParams.getWIMap(), msg);
        if (!wiParams.getWIMap().get("wi.login.accept").equals("true")) {
        	resp = "Login Expirado";
        }
        if (!resp.equals("")) {
        	wiParams.getWIMap().put("tmp.msglogin", resp);
        }	
        String loginPage = project.getLoginPage();
        if (!loginPage.trim().equals("")) {
        	WIMap wiMap = wiParams.getWIMap();
        	wiMap.remove("tmp.msgsecurevar");
            wiParams.sendRedirect(loginPage + ".wsp", wiMap, true);
        } else {
            new Export(wiParams).showMessage(i18n("Login Inválido"));
        }
    }

    private String i18n(String text) {
        return new I18N().get(text);
    }
    
    private void checkSingleSignOn(WIMap wiMap, String addr) {
        String ssoId = EngFunction.getSingleSignOnId(wiParams); 
        if (!ssoId.equals("")) {
        	String ssoVar = wiMap.get("pvt.wilogin.singlesignon");
        	Map map = SingleSignOnRepository.getToken(ssoId + "-" + addr);
        	if (map != null) {
        		wiMap.put("pvt.wilogin", addr);
        		wiMap.put("pvt.wilogin.singlesignon", "true");
        		wiMap.putAll(map);
            	IntFunction.loginRoles(wiParams);
        	} else if (ssoVar.equalsIgnoreCase("true")) {
        		removeLogin(wiMap);
        	}	
        }
    }
    
    protected static void removeLogin(WIMap wiMap) {
    	wiMap.remove("pvt.wilogin");
		wiMap.remove("pvt.wilogin.singlesignon");
    	wiMap.remove("pvt.wimenu");
		wiMap.remove("pvt.login.");
    }
        
}
