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

package br.com.webinside.runtime.function;

import java.io.File;

import br.com.webinside.runtime.integration.AbstractFunction;
import br.com.webinside.runtime.util.Function;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.4 $
 */
public class FileCopy extends AbstractFunction {
    /**
     * Creates a new FileCopy object.
     */
    public FileCopy() {
    }

    /**
     * DOCUMENT ME!
     *
     * @param args DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public String execute(String[] args) {
        if (args.length < 1) return "";
        String from = args[0];
        if (args.length == 1) {
        	return new File(from).isFile() + "";
        }
        String to = args[1];
        boolean ret = copyFile(from, to, true);
        return ret + "";
    }

    private boolean copyFile(String fromFile, String toFile, boolean createDir) {
        return Function.copyFile(fromFile, toFile, createDir);
    }
}
