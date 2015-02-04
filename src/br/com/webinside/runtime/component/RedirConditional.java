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

package br.com.webinside.runtime.component;

import org.jdom.*;

/**
 * DOCUMENT ME!
 *
 * @author $author$
 * @version $Revision: 1.1 $
 */
public class RedirConditional extends AbstractRedir {

	private static final long serialVersionUID = 1L;

    /**
     * Creates a new RedirConditional object.
     */
    public RedirConditional() {
        this.setSeq("");
    }

    /**
     * Creates a new RedirConditional object.
     *
     * @param seq DOCUMENT ME!
     */
    public RedirConditional(String seq) {
        this.setSeq(seq);
    }

    /**
     * Creates a new RedirConditional object.
     *
     * @param element DOCUMENT ME!
     */
    public RedirConditional(Element element) {
        if ((element == null) || (!element.getName().equals("REDIR"))) {
            element = new Element("REDIR");
        }
        this.redir = element;
    }

    /**
     * DOCUMENT ME!
     *
     * @return DOCUMENT ME!
     */
    public AbstractActionElement cloneMe() {
        RedirConditional obj = new RedirConditional((Element) redir.clone());
        return obj;
    }
}
