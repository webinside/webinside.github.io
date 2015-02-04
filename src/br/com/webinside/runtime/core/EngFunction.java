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

import java.io.IOException;
import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import br.com.webinside.runtime.component.AbstractProject;
import br.com.webinside.runtime.component.ComboRef;
import br.com.webinside.runtime.component.GridRef;
import br.com.webinside.runtime.component.GridSql;
import br.com.webinside.runtime.component.GridXmlOut;
import br.com.webinside.runtime.component.Host;
import br.com.webinside.runtime.component.Page;
import br.com.webinside.runtime.component.WIObjectGrid;
import br.com.webinside.runtime.exception.DatabaseConnectionException;
import br.com.webinside.runtime.exception.GenericConnectionException;
import br.com.webinside.runtime.exception.HostConnectionException;
import br.com.webinside.runtime.integration.JavaParameter;
import br.com.webinside.runtime.integration.Producer;
import br.com.webinside.runtime.integration.ProducerParam;
import br.com.webinside.runtime.net.NetFunction;
import br.com.webinside.runtime.util.I18N;
import br.com.webinside.runtime.util.StringA;
import br.com.webinside.runtime.util.WIMap;

/**
 * Classe de funções utilitárias ao engine
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.3 $
 */
public class EngFunction {

	private EngFunction() { }
	
    /**
     * Armazena na requisição uma exceção DatabaseConnectionException.
     *
     * @param wiParams os parâmetros do WI.
     * @param id o identificador do database que deu erro de conexão.
     */
    public static void databaseError(ExecuteParams wiParams, String id) {
        String msg = "Erro de conexão com o banco de dados";
        msg = new I18N().get(msg);
        String fullmsg = msg + " (" + id + ")";
        invalidateTransaction(wiParams.getWIMap(), fullmsg);
        Exception ex = new DatabaseConnectionException(fullmsg);
        wiParams.setRequestAttribute("wiException", ex);
    }
    
    /**
     * Armazena na requisição uma exceção HostConnectionException.
     *
     * @param wiParams os parâmetros do WI.
     * @param id o identificador do host que deu erro de conexão.
     */
    public static void hostError(ExecuteParams wiParams, String id) {
        String msg = "Erro de conexão";
        msg = new I18N().get(msg);
        String fullmsg = msg + " (" + id + ")";
        invalidateTransaction(wiParams.getWIMap(), fullmsg);
        Exception ex = new HostConnectionException(fullmsg);
        wiParams.setRequestAttribute("wiException", ex);
    }

    /**
     * Invalida a transação em caso de erro.
     *
     */
    public static void invalidateTransaction(WIMap wiMap, String msg) {
    	String stsKey = "wi.transaction.status";
    	if (wiMap != null && wiMap.get(stsKey).equals("true")) {
    		wiMap.remove("wi.transaction.none");
    		wiMap.put(stsKey, msg);
    	}	
    }

    /**
     * Remove os espaços e caracateres de retorno ("\r\n") de um texto.
     *
     * @param txt o texto a ser processado.
     *
     * @return o texto sem espaços
     */
    public static String cleanSpace(String txt) {
        StringA resp = new StringA();
        if (txt == null) {
            txt = new String();
        }
        for (int i = 0; i < txt.length(); i++) {
            char let = txt.charAt(i);
            if ((let != ' ') && (let != '\r') && (let != '\n')) {
                resp.append(let);
            }
        }
        return resp.toString();
    }

    /**
     * Retorna uma lista dos pipes encontrados no texto.
     *
     * @param text o texto a ser processado.
     *
     * @return a lista dos pipes.
     */
    public static List listPipeNames(String text) {
        List response = new ArrayList();
        int from = 0;
        int pos = 0;
        while ((pos = text.indexOf("|", from)) > -1) {
            int end = -1;
            if (StringA.mid(text, pos + 1, pos + 1).equals("$")) {
                end = text.indexOf("$|", pos + 1);
                if (end > -1) {
                    end = end + 1;
                }
            } else {
                end = text.indexOf("|", pos + 1);
            }
            if (end == -1) {
                end = text.length();
            }
            String id = StringA.mid(text, pos + 1, end - 1);
            response.add(id);
            from = end + 1;
        }
        return response;
    }

    /**
     * Popula a coleção de variáveis do WI utilizando um  array de parametros
     * java.
     *
     * @param wiMap a coleção de variáveis do WI
     * @param jParams um array de parametros java.
     */
    public static void putJavaParameters(WIMap wiMap, JavaParameter[] jParams) {
        ProducerParam prod = new ProducerParam();
        prod.setWIMap(wiMap);
        for (int i = 0; i < jParams.length; i++) {
            JavaParameter param = jParams[i];
            String value = param.getValue();
            prod.setInput(value);
            new Producer(prod).execute();
            value = prod.getOutput();
            if (!value.equals("")) {
                wiMap.put(param.getVarId(), value);
            }
        }
    }

    /**
     * Retorna o nome do servidor tentando utilizar o header host.
     *
     * @param request a requisição http.
     *
     * @return o nome do servidor.
     */
    public static String getServerName(HttpServletRequest request) {
        String srvname = request.getHeader("host");
        if (srvname != null) {
            srvname = StringA.piece(srvname, ":", 1);
        } else {
            srvname = request.getServerName();
        }
        String fwdHost = request.getHeader("x-forwarded-host");
        if (fwdHost != null) {
        	srvname = fwdHost; 
        }
        if (srvname == null) {
        	return "";
        }
        return srvname;
    }

    /**
     * Retorna a protocolo do servidor.
     *
     * @param request a requisição http.
     *
     * @return a protocolo do servidor.
     */
    public static String getServerProt(HttpServletRequest request, int port) {
    	String prot = "http";
    	String cert = request.getHeader("CERT_KEYSIZE");
        if (request.isSecure() || port == 443 || cert != null) {
        	prot = "https";
        }
        return prot + "://";
    }
    
    /**
     * Retorna a porta do servidor tentando utilizar o header host.
     *
     * @param request a requisição http.
     *
     * @return a porta do servidor.
     */
    public static int getServerPort(HttpServletRequest request) {
        int srvport = request.getServerPort();
        String host = request.getHeader("host");
        if (host != null) {
            try {
                srvport = Integer.parseInt(StringA.piece(host, ":", 2));
            } catch (NumberFormatException err) {
                // não deve ocorrer
            }
        }
        return srvport;
    }

    /**
     * Gera uma combo.
     *
     * @param wiParams os parametros do WI.
     * @param comboId o identificador da combo a ser gerada.
     */
    public static void generateCombo(ExecuteParams wiParams, String comboId) {
    	generateCombo(wiParams, comboId, true);
    }

    /**
     * Gera uma combo.
     *
     * @param wiParams os parametros do WI.
     * @param comboId o identificador da combo a ser gerada.
     * @param out indica se deve ser dada a saida na tela.
     */
    public static void generateCombo(ExecuteParams wiParams, 
    		String comboId, boolean out) {
    	if (!wiParams.getWIMap().containsKey("combo." + comboId)) {
    		ComboRef cbo = new ComboRef();
    		cbo.setId(comboId);
    		cbo.setCondition("true");
    		new CoreCombo(wiParams, cbo).execute();
    	} 
    	String combo = wiParams.getWIMap().get("combo." + comboId).trim(); 
    	if (!combo.equals("") && out) {
    		wiParams.getWriter().print(combo);
    	}
    }

    /**
     * Gera um grid.
     *
     * @param wiParams os parametros do WI.
     * @param gridId o identificador do grid a ser gerado.
     *
     * @throws Exception caso ocorra alguma exceção.
     */
    public static void generateGrid(ExecuteParams wiParams, String gridId) 
    		throws Exception {
    	generateGrid(wiParams, gridId, true);
    }

    /**
     * Gera um grid.
     *
     * @param wiParams os parametros do WI.
     * @param gridId o identificador do grid a ser gerado.
     * @param out indica se deve ser dada a saida na tela.
     *
     * @throws Exception caso ocorra alguma exceção.
     */
    public static void generateGrid(ExecuteParams wiParams, 
    		String gridId, boolean out) throws Exception {
        if (!wiParams.getWIMap().containsKey("grid." + gridId)) {
	        int type = 0;
	        AbstractProject proj = wiParams.getProject();
	        if (proj != null) {
	            if (!proj.getGrids().containsKey(gridId)) {
	                wiParams.includeCode("/grids/" + gridId + "/grid.jsp");
	            }
	            if (proj.getGrids().getElement(gridId) instanceof GridSql) {
	                type = 1;
	            }
	            if (proj.getGrids().getElement(gridId) instanceof GridXmlOut) {
	                type = 2;
	            }
	        }
	        if ((type == 1) || (type == 2)) {
	            // SQL
	            GridRef ele = new GridRef();
	            ele.setId(gridId);
	            ele.setCondition("true");
	            boolean generateInPage = true;
	            String gen = wiParams.getWIMap().get("grid.generateInPage"); 
	            if (!gen.equals("")) {
	            	generateInPage = new Boolean(gen).booleanValue();
	            }
	            ele.setGenerateInPage(generateInPage);
	            CoreGrid core = new CoreGrid(wiParams, ele);
	            core.execute();
	        } else {
	            // WIObject
	            WIObjectGrid ele = new WIObjectGrid();
	            ele.setGridId(gridId);
	            String id = wiParams.getWIMap().get("id");
	            wiParams.getWIMap().remove("id");
	            ele.setWIObjName(id);
	            ele.setCondition("true");
	            CoreWIObjectGrid core = new CoreWIObjectGrid(wiParams, ele);
	            core.execute();
	        }
	        if (wiParams.getRequestAttribute("wiException") != null) {
	            Exception ex =
	                (Exception) wiParams.getRequestAttribute("wiException");
	            if (!(ex instanceof GenericConnectionException)) {
	                throw ex;
	            }
	        }
        }
    	String grid = wiParams.getWIMap().get("grid." + gridId).trim(); 
    	if (!gridId.equals("") && out) {
    		wiParams.getWriter().print(grid);
    	}
    }

    /**
     * Faz uma requisição a uma Url.
     *
     * @param url que receberá a requisição.
     *
     * @return o código de resposta http ou zero em caso de exceção.
     */
    public static int callUrl(String url) {
        try {
            HttpURLConnection urlcon = NetFunction.openConnection(url);
            urlcon.setInstanceFollowRedirects(false);
            urlcon.connect();
            urlcon.disconnect();
            return urlcon.getResponseCode();
        } catch (IOException err) {
            // não deve ocorrer
            return 0;
        }
    }

    /**
     * Indica se a página pode ser executada de acordo com o controle de login.
     *
     * @param wiParams os parametros do wi.
     *
     * @return se a página pode ser executada.
     */
    public static boolean checkLoginForPosPage(ExecuteParams wiParams) {
        if (wiParams.getProject().getLoginActive().equals("ON")) {
            String loginPage =
                StringA.piece(wiParams.getProject().getLoginPage(), ".wsp", 1);
            Page page = wiParams.getPage();
            if (page == null || page.getSysPage().equals("ON")) return false;
            if (page.getId().equals(loginPage)) {
            	// So fazer se a sessão não expirou
                if (wiParams.getWIMap().get("pvt.login.accept").equals("true")) {
                    return true;
                }
                return false;
            }
            String loginIp = wiParams.getWIMap().get("pvt.wilogin");
            String addr = wiParams.getHttpRequest().getRemoteAddr();
            if (loginIp.equals(addr) || page.getNoLogin().equals("ON")) {
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * Gera uma URL endpoint para um WebService.
     *
     * @param host o servidor a ser utilizado.
     * @param service o serviço a ser utilizado.
     * @param wiParams os parametros do engine.
     *
     * @return a URL produzida.
     */
    public static String getEndpoint(Host host, String service,
        ExecuteParams wiParams) {
        String strhost = host.getAddress();
        if (wiParams != null) {
            ProducerParam prod = new ProducerParam();
            prod.setWIMap(wiParams.getWIMap());
            prod.setInput(host.getAddress());
            wiParams.getProducer().setParam(prod);
            wiParams.getProducer().execute();
            strhost = prod.getOutput().trim();
            strhost = StringA.change(strhost, "//", "/");
            strhost = StringA.change(strhost, ":/", "://");
        }
        if (!strhost.startsWith("http://") && !strhost.startsWith("https://")) {
            strhost = "http://" + strhost;
        }
        if (!strhost.endsWith("/") && !service.equals("")) {
            strhost = strhost + "/";
        }
        if (service.startsWith("/")) {
            service = StringA.mid(service, 1, service.length());
        }
        return strhost + service;
    }
    
    /**
     * Recupera o id para uso do SingleSignOn.
     *
     * @param wiParams os parametros do engine.
     *
     * @return o id para uso do SingleSignOn.
     */
    public static String getSingleSignOnId(ExecuteParams wiParams) {
    	Cookie[] cookies = wiParams.getHttpRequest().getCookies(); 
        for (int i = 0; cookies != null && i < cookies.length; i++) {
			if (cookies[i].getName().equals("JSSOID")) {
				return cookies[i].getValue(); 
			}
		}
        return "";
	}

    public static String wiContext(WIMap wiMap, String expr) {
        String aux = StringA.piece(expr.toLowerCase(), "(", 2);
        aux = StringA.piece(aux, ")", 1);
        String mask = Producer.execute(wiMap, aux).trim().toLowerCase();
        if (mask.trim().equals("")) mask = "*";
    	StringBuffer resp = new StringBuffer();
    	StringTokenizer st = new StringTokenizer(mask, ",");
    	while (st.hasMoreTokens()) {
    		String token = st.nextToken().trim().toLowerCase();
    		resp.append(wiMap.getAsText(token, false));
    	}
        return resp.toString().trim();
    }
    
    public static int errorCodeSQL(Exception ex) {
        int result = -1;
        if (ex instanceof SQLException) {
            result = ((SQLException) ex).getErrorCode();
            if (result > 0) {
                result = -result;
            }
            if (result == 0) {
                result = -1;
            }
        }
        return result;
    }
    
}
