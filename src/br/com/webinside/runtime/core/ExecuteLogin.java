/*
 * WEBINSIDE - Ferramenta de produtividade Java
 * Copyright (c) 2011-2012 LINEWEB Solu��es Tecnol�gicas Ltda.
 * Copyright (c) 2009-2010 Inc�gnita Intelig�ncia Digital Ltda.
 *
 * Este programa � software livre; voc� pode redistribu�-lo e/ou modific�-lo 
 * sob os termos da GNU LESSER GENERAL PUBLIC LICENSE (LGPL) conforme publicada 
 * pela Free Software Foundation; vers�o 2.1 da Licen�a.
 * Este programa � distribu�do na expectativa de que seja �til, por�m, SEM 
 * NENHUMA GARANTIA; nem mesmo a garantia impl�cita de COMERCIABILIDADE OU 
 * ADEQUA��O A UMA FINALIDADE ESPEC�FICA.
 * 
 * Consulte a GNU LGPL para mais detalhes.
 * Voc� deve ter recebido uma c�pia da GNU LGPL junto com este programa; se n�o, 
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
import br.com.webinside.runtime.util.CrossContext;
import br.com.webinside.runtime.util.CrossContextFactory;
import br.com.webinside.runtime.util.Crypto;
import br.com.webinside.runtime.util.I18N;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.11 $
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
        // pagina de login sempre � valida 
        String loginPage = StringA.piece(project.getLoginPage(), ".wsp", 1);
        if (wiParams.getPage().getId().equals(loginPage)) {
            return true;
        }
        if (wiParams.isJspInclude()) {
        	return true;
        }
        String addr = wiParams.getHttpRequest().getRequestedSessionId();
        checkSingleSignOn(wiMap, addr);
        String logip = wiMap.get("pvt.wilogin");
        if (logip.equals(addr)) return true;
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
            RtmFunction.databaseError(wiParams, dbalias);
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
                    addr = wiParams.getHttpRequest().getRequestedSessionId();
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
            RtmFunction.databaseError(wiParams, dbalias);
            return false;
        }
        if (!db.isConnected()) {
            invalidLogin();
            return false;
        }
        WIMap wiMap = wiParams.getWIMap();
        wiMap.put("pvt.wilogin", wiParams.getHttpRequest().getRequestedSessionId());
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
        	if (!loginPage.endsWith(".wsp")) loginPage += ".wsp";
        	WIMap wiMap = wiParams.getWIMap();
        	wiMap.remove("tmp.msgsecurevar");
            wiParams.sendRedirect(loginPage, wiMap, true);
        } else {
            new RtmExport(wiParams).showMessage(i18n("Login Inv�lido"));
        }
    }

    private String i18n(String text) {
        return new I18N().get(text);
    }
    
    private void checkSingleSignOn(WIMap wiMap, String addr) {
        String ssoId = RtmFunction.getSingleSignOnId(wiParams); 
        if (!ssoId.equals("")) {
        	Map map = null;        	
        	String ssoVar = wiMap.get("pvt.wilogin.singlesignon");
        	CrossContext cross = CrossContextFactory.getInstance();
        	if (cross != null) map = cross.getSSO(ssoId + "-" + addr);
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
