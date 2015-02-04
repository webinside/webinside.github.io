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

package br.com.webinside.modules.search;

/**
 * Highlights arbitrary terms.
 *
 * @author Maik Schreiber
 */
public interface TermHighlighter {
    /**
     * Highlight an arbitrary term. For example, an HTML TermHighlighter could
     * simply do:
     * 
     * <p>
     * 
     * <dl>
     * <dd>
     * <code>return "&lt;b&gt;" + term + "&lt;/b&gt;";</code>
     * </dd>
     * </dl>
     * </p>
     *
     * @param term term text to highlight
     *
     * @return highlighted term text
     */
    String highlightTerm(String term);
}
