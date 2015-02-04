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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import br.com.webinside.runtime.integration.AbstractFunction;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.2 $
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
        File arq = new File(fromFile);
        if (!arq.isFile()) return false;
        arq = new File(toFile);
        if (arq.isFile()) arq.delete();
        if ((createDir) && (!new File(arq.getParent()).exists())) {
            new File(arq.getParent()).mkdirs();
        }
        byte[] trecho = new byte[10240];
        int quant = 0;
        try {
            BufferedInputStream in =
                new BufferedInputStream(new FileInputStream(fromFile));
            BufferedOutputStream out =
                new BufferedOutputStream(new FileOutputStream(toFile));
            while ((quant = in.read(trecho)) > -1) {
                out.write(trecho, 0, quant);
            }
            out.close();
            in.close();
        } catch (IOException err) {
            return false;
        }
        return true;
    }
}
