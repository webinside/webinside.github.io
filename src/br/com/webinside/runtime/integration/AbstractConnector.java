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

package br.com.webinside.runtime.integration;

import java.io.*;

import br.com.webinside.runtime.core.*;
import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.util.*;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
 */
public abstract class AbstractConnector implements InterfaceConnector {
    private ExecuteParams params;

    /**
     * DOCUMENT ME!
     *
     * @param wiParams DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public void execute(ExecuteParams wiParams) throws UserException {
        this.params = wiParams;
        WIMap wiMap = wiParams.getWIMap();
        DatabaseAliases databases = wiParams.getDatabaseAliases();
        RtmHeaders headers = new RtmHeaders(wiParams);
        execute(wiMap, databases, headers);
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public boolean exit() {
        return false;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public PrintStream getOutputStream() {
        return params.getOutputStream();
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public PrintWriter getWriter() {
        return params.getWriter();
    }

	/**
	 * DOCUMENT ME!
	 *
	 * @return DOCUMENT ME!
	 */
	public ExecuteParams getParams() {
		return params;
	}

    /**
     * DOCUMENT ME!
     *
     * @param wiMap DOCUMENT ME!
     * @param databases DOCUMENT ME!
     * @param headers DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public abstract void execute(WIMap wiMap, DatabaseAliases databases,
        InterfaceHeaders headers) throws UserException;
}
