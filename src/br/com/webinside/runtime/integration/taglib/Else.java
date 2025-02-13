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

package br.com.webinside.runtime.integration.taglib;

import javax.servlet.jsp.JspException;

/**
 * Classe que implementa um TagLib para a função ELSE.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.1 $
 */
public class Else extends If {

	private static final long serialVersionUID = 1L;

	/**
     * Testa a condição e valida se o body deve ser mostrado.
     *
     * @return a flag para não processar o body
     *
     * @throws JspException em caso de uma exceção jsp.
     */
    public int doStartTag() throws JspException {
        Integer ifSeq = (Integer) pageContext.getAttribute("wi_tag_if_level");
        ifSeq = (ifSeq == null) ? 1 : ifSeq + 1;
        String tag = "wi_tag_else_" + ifSeq;
        Boolean condElse = (Boolean) pageContext.getAttribute(tag);
        if (condElse != null && condElse == false) {
        	if (getTest() == null) setTest("true");
        	return super.doStartTag();
        } else {
            reset();
            pageContext.setAttribute("wi_tag_if_level", ifSeq);                
        	return SKIP_BODY;
        }
    }
    
}
