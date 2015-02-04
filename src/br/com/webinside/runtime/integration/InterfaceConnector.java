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

package br.com.webinside.runtime.integration;

import br.com.webinside.runtime.core.*;
import br.com.webinside.runtime.exception.UserException;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public interface InterfaceConnector {
    /**
     * DOCUMENT ME!
     *
     * @param wiParams DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void execute(ExecuteParams wiParams) throws UserException;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean exit();
}
