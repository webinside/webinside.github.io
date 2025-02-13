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

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import br.com.webinside.runtime.function.SecureLinkManager;

/**
 * Classe utilizada como Listener da sess�o no WI.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.2 $
 *
 * @since 3.0
 */
public class WISessionListener implements HttpSessionListener {
    /**
     * M�todo executado ao ser criada uma nova sess�o.
     *
     * @param event o evento da sess�o http.
     */
    public void sessionCreated(HttpSessionEvent event) {
    }

    /**
     * M�todo executado ao ser invalidada uma sess�o.
     *
     * @param event o evento da sess�o http.
     */
    public void sessionDestroyed(HttpSessionEvent event) {
        // remove os arquivos tempor�rios da sess�o
    	String id = event.getSession().getId();
        Function.removeDir(Function.tmpDir() + ".wi/" + id, true);
        // remove a sess�o do securelink
        SecureLinkManager.removeSession(id);
    }
}
