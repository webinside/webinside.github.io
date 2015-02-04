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

import java.util.Map;

import br.com.webinside.runtime.exception.UserException;
import br.com.webinside.runtime.util.WIMap;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public interface InterfaceGrid {
    // todos os registros
    /** DOCUMENT ME! */
    public static final int COMPLETE = 1;

    // parte dos registros com continuação
    /** DOCUMENT ME! */
    public static final int HAS_MORE_ROWS = 0;

    // parte dos registros sem continuação
    /** DOCUMENT ME! */
    public static final int NO_MORE_ROWS = -1;

    /**
     * DOCUMENT ME!
     *
     * @param wiMap DOCUMENT ME!
     * @param databases DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     *
     * @throws Exception DOCUMENT ME!
     */
    public Map[] execute(WIMap wiMap, DatabaseAliases databases)
        throws UserException;

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public int returnType();
}
