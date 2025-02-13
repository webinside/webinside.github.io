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

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import br.com.webinside.runtime.function.SecureLinkManager;

/**
 * Classe utilizada como Listener da sessão no WI.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.2 $
 *
 * @since 3.0
 */
public class WISessionListener implements HttpSessionListener {
    /**
     * Método executado ao ser criada uma nova sessão.
     *
     * @param event o evento da sessão http.
     */
    public void sessionCreated(HttpSessionEvent event) {
    }

    /**
     * Método executado ao ser invalidada uma sessão.
     *
     * @param event o evento da sessão http.
     */
    public void sessionDestroyed(HttpSessionEvent event) {
        // remove os arquivos temporários da sessão
    	String id = event.getSession().getId();
        Function.removeDir(Function.tmpDir() + ".wi/" + id, true);
        // remove a sessão do securelink
        SecureLinkManager.removeSession(id);
    }
}
