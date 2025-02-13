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

import java.util.*;
import javax.servlet.http.*;

/**
 * Classe utilizando HttpSession para uso do WI.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.4 $
 *
 * @since 3.0
 */
public class WISession {

	public static final String SESSION_KEY = "webinside";
	public static final String SESSION_KEY_OLD = "webintegrator";
	
	private HttpServletRequest request;
    private HttpSession session;

    /**
     * Cria um novo WISession.
     *
     * @param request o request.
     */
    public WISession(HttpServletRequest request) {
        this.request = request;
        this.session = request.getSession(false);
    }

    /**
     * Cria um novo WISession.
     *
     * @param session a sessão Http.
     */
    public WISession(HttpSession session) {
        this.session = session;
    }

    /**
     * Retorna a sessão Http utilizada.
     *
     * @return a sessão Http.
     */
    public HttpSession getHttpSession() {
        return session;
    }

    /**
     * Retorna o Id da sessão.
     *
     * @return o Id da sessão ou vazio se não houver.
     */
    public String getId() {
        String id = "";
        if (session != null) {
            id = session.getId();
        }
        return id;
    }

    /**
     * Armazena um atributo dentro de um Map identificado
     * na sessão Http.
     *
     * @param key a chave.
     * @param obj o valor.
     */
    public void setAttribute(String key, Object obj) {
        if ((session != null) && (key != null) && (obj != null)
                    && !key.equals("")) {
            Map sessionMap = null;
            Object wiObjSession = session.getAttribute(SESSION_KEY);
            if (wiObjSession == null) {
            	sessionMap = Collections.synchronizedMap(new HashMap());
            } else {
            	sessionMap = (Map) wiObjSession;
            }
            sessionMap.put(key, obj);
            session.setAttribute(SESSION_KEY, sessionMap);
            // adicionado para compatibilidade com legado webintegrator
            session.setAttribute(SESSION_KEY_OLD, sessionMap);
        }
    }

    /**
     * Retorna um atributo dentro de um Map identificado
     * na sessão Http.
     *
     * @param key a chave.
     *
     * @return o objeto.
     */
    public Object getAttribute(String key) {
        if ((session == null) || (key == null) || key.equals("")) {
            return null;
        }
        Object wiObjSession = null;
        try {
        	wiObjSession = session.getAttribute(SESSION_KEY);
        } catch (IllegalStateException isex) { }
        if (wiObjSession == null) return null;
        Map wiSession = (Map) wiObjSession;
        return wiSession.get(key);
    }

    /**
     * Remove um atributo dentro de um Map identificado
     * na sessão Http.
     *
     * @param key a chave.
     */
    public void removeAttribute(String key) {
        if ((session != null) && (key != null) && !key.equals("")) {
            Map sessionMap = null;
            Object wiObjSession = session.getAttribute(SESSION_KEY);
            if (wiObjSession == null) {
            	sessionMap = Collections.synchronizedMap(new HashMap());
            } else {
            	sessionMap = (Map) wiObjSession;
            }
            sessionMap.remove(key);
            session.setAttribute(SESSION_KEY, sessionMap);
            // adicionado para compatibilidade com legado webintegrator
            session.setAttribute(SESSION_KEY_OLD, sessionMap);
        }
    }

    /**
     * Indica se existe alguma chave nos atributos iniciando por um dado
     * prefixo.
     *
     * @param prefix o prefixo a ser localizado.
     *
     * @return indica se existe chave com o prefixo.
     */
    public boolean hasAttributeWithPrefix(String prefix) {
        if ((session == null) || (prefix == null) || prefix.equals("")) {
            return false;
        }
        Object wiObjSession = session.getAttribute(SESSION_KEY);
        if (wiObjSession == null) {
            return false;
        }
        boolean has = false;
        Map wiSession = (Map) wiObjSession;
        Iterator it = wiSession.keySet().iterator();
        while (it.hasNext() && !has) {
            Object oKey = it.next();
            if (oKey instanceof String) {
                String key = (String) oKey;
                if (key.startsWith(prefix)) {
                    has = true;
                }
            }
        }
        return has;
    }

    /**
     * Cria uma nova sessão utilizando a requisição Http.
     */
    public void makeSession() {
        if (request != null) {
            session = request.getSession();
        }
    }

    /**
     * Invalida a sessão.
     */
    public void invalidate() {
        if (session != null) {
            session.invalidate();
        }
    }

    /**
     * Retorna o estado da sessão.
     *
     * @return o estado da sessão.
     */
    public boolean isValid() {
        if (session != null) {
            try {
            	session.isNew();
                return true;
            } catch (Exception err) { 
            	// Dá uma excessao se ela tivar sido invalidada
            }
        }
        return false;
    }

    /**
     * Retorna se a sessão é nova.
     *
     * @return se a sessão é nova.
     */
    public boolean isNew() {
        if (session != null) {
        	if (session.isNew() || 
        			session.getAttribute(SESSION_KEY) == null) {
        		return true;
        	}
        	return false;	
        }
        throw new RuntimeException("HttpSession null in WISession");
    }    
    /**
     * Define o tempo máximo de inatividade da sessão.
     *
     * @param seconds o tempo em segundos.
     */
    public void setMaxInactiveInterval(int seconds) {
        if (request != null) {
            session.setMaxInactiveInterval(seconds);
        }
    }
        
}
