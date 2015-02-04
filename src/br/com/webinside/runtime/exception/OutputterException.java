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

package br.com.webinside.runtime.exception;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class OutputterException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	/**
     * Creates a new UserException object.
     */
    public OutputterException() {
        super();
    }

    /**
     * Creates a new UserException object.
     *
     * @param message DOCUMENT ME!
     */
    public OutputterException(String message) {
        super(message);
    }

    /**
     * Creates a new UserException object.
     *
     * @param message DOCUMENT ME!
     * @param cause DOCUMENT ME!
     */
    public OutputterException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Creates a new UserException object.
     *
     * @param cause DOCUMENT ME!
     */
    public OutputterException(Throwable cause) {
        super(cause);
    }
}
