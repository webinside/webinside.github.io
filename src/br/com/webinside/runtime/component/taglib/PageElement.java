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

package br.com.webinside.runtime.component.taglib;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import br.com.webinside.runtime.core.ExecuteParams;

/**
 * Classe que implementa um TagLib para um elemento da p�gina.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.1 $
 */
public class PageElement extends TagSupport {

	private static final long serialVersionUID = 1L;
	private String type;
    private String name;
    private boolean exit;

    /**
     * Executa a fun��o.
     *
     * @return a flag para n�o processar o body
     *
     * @throws JspException em caso de uma exce��o jsp.
     */
    public int doStartTag() throws JspException {
    	exit = true;
        try {
            Object obj = pageContext.getRequest().getAttribute("wiParams");
            if (obj instanceof ExecuteParams) {
                exit = false;
            	Class cl = Class.forName("br.com.webinside.runtime.component." + getType());
            	pageContext.setAttribute(getName(), cl.getConstructor().newInstance());
            } else {
                HttpServletResponse response =
                    (HttpServletResponse) pageContext.getResponse();
                (response).sendError(HttpServletResponse.SC_FORBIDDEN);
            }
        } catch (Exception err) {
        	throw new JspException(err);
        }
        return SKIP_BODY;
    }
    
    /**
     * Finaliza a tag.
     *
     * @return se a p�gina deve ser processada.
     *
     * @throws JspException em caso de uma exce��o jsp.
     */
    public int doEndTag() throws JspException {
        return ((exit) ? SKIP_PAGE
                       : EVAL_PAGE);
    }

    /**
     * Name do elemento da p�gina.
     *
     * @return o nome do elemento da p�gina.
     */
    public String getName() {
        return name;
    }

    /**
     * Name do elemento da p�gina.
     *
     * @param name indica o id do elemento da p�gina.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Tipo de elemento da p�gina.
     *
     * @return o tipo de elemento da p�gina.
     */
    public String getType() {
        return type;
    }

    /**
     * Tipo de elemento da p�gina.
     *
     * @param type indica o tipo de elemento da p�gina.
     */
    public void setType(String type) {
        this.type = type;
    }
}
