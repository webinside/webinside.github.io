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

package br.com.webinside.runtime.integration.taglib;

import javax.servlet.jsp.JspException;

/**
 * Classe que implementa um TagLib para a fun��o ELSE.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.1 $
 */
public class Else extends If {

	private static final long serialVersionUID = 1L;

	/**
     * Testa a condi��o e valida se o body deve ser mostrado.
     *
     * @return a flag para n�o processar o body
     *
     * @throws JspException em caso de uma exce��o jsp.
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
