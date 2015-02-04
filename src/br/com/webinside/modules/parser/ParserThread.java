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

package br.com.webinside.modules.parser;

import java.io.*;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
class ParserThread extends Thread {
    /** DOCUMENT ME! */
    HTMLParser parser;

    /**
     * Creates a new ParserThread object.
     *
     * @param p DOCUMENT ME!
     */
    public ParserThread(HTMLParser p) {
		super("WI-ParserThread");
        parser = p;
    }

    /**
     * DOCUMENT ME!
     */
    public void run() { // convert pipeOut to pipeIn
        try {
            try { // parse document to pipeOut
                parser.HTMLDocument();
            } catch (ParseException e) {
                System.err.println("Parse Aborted: " + e.getMessage());
            } catch (TokenMgrError e) {
                System.err.println("Parse Aborted: " + e.getMessage());
            } finally {
                parser.pipeOut.close();
                synchronized (parser) {
                    parser.summary.setLength(HTMLParser.SUMMARY_LENGTH);
                    parser.titleComplete = true;
                    parser.notifyAll();
                }
            }
        } catch (IOException e) {
        }
    }
}
