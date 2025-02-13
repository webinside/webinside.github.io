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
     * @param session a sess�o Http.
     */
    public WISession(HttpSession session) {
        this.session = session;
    }

    /**
     * Retorna a sess�o Http utilizada.
     *
     * @return a sess�o Http.
     */
    public HttpSession getHttpSession() {
        return session;
    }

    /**
     * Retorna o Id da sess�o.
     *
     * @return o Id da sess�o ou vazio se n�o houver.
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
     * na sess�o Http.
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
     * na sess�o Http.
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
     * na sess�o Http.
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
     * Cria uma nova sess�o utilizando a requisi��o Http.
     */
    public void makeSession() {
        if (request != null) {
            session = request.getSession();
        }
    }

    /**
     * Invalida a sess�o.
     */
    public void invalidate() {
        if (session != null) {
            session.invalidate();
        }
    }

    /**
     * Retorna o estado da sess�o.
     *
     * @return o estado da sess�o.
     */
    public boolean isValid() {
        if (session != null) {
            try {
            	session.isNew();
                return true;
            } catch (Exception err) { 
            	// D� uma excessao se ela tivar sido invalidada
            }
        }
        return false;
    }

    /**
     * Retorna se a sess�o � nova.
     *
     * @return se a sess�o � nova.
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
     * Define o tempo m�ximo de inatividade da sess�o.
     *
     * @param seconds o tempo em segundos.
     */
    public void setMaxInactiveInterval(int seconds) {
        if (request != null) {
            session.setMaxInactiveInterval(seconds);
        }
    }
        
}
