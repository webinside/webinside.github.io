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

package br.com.webinside.runtime.function;

import br.com.webinside.runtime.integration.*;
import br.com.webinside.runtime.util.*;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class ExecuteSqlThread extends Thread {
    private DatabaseHandler dbgen;
    private String query;
    private WIMap wiMap;

    /**
     * Creates a new ExecuteSqlThread object.
     *
     * @param dbgen DOCUMENT ME!
     * @param query DOCUMENT ME!
     * @param wiMap DOCUMENT ME!
     */
    public ExecuteSqlThread(DatabaseHandler dbgen, String query, WIMap wiMap) {
		super("WI-ExecuteSQLThread");
        this.dbgen = dbgen;
        this.query = query;
        this.wiMap = wiMap;
    }

    /**
     * DOCUMENT ME!
     */
    public void run() {
        if (dbgen == null) {
            return;
        }
        dbgen.connect();
        try {
            dbgen.executeUpdate(query, wiMap);
        } catch (Exception err) {
            System.err.println(getClass().getName() + ": " + err);
        }
        dbgen.close();
    }
}
