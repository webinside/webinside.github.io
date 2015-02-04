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

import java.io.IOException;
import java.io.Writer;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyContent;
import javax.servlet.jsp.tagext.BodyTagSupport;

import br.com.webinside.runtime.core.ExecuteParams;

/**
 * Classe que implementa um TagLib para gerar um elemento do Grid.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.1 $
 */
public class GridElement extends BodyTagSupport {

	private static final long serialVersionUID = 1L;
	private ExecuteParams wiParams;
    private Writer responseWriter;

    /**
     * Executa a fun��o.
     *
     * @return a flag para n�o processar o body
     *
     * @throws JspException em caso de uma exce��o jsp.
     */
    public int doStartTag() throws JspException {
        responseWriter = null;
        try {
            Object obj = pageContext.getRequest().getAttribute("wiParams");
            if (obj instanceof ExecuteParams) {
                wiParams = (ExecuteParams) obj;
                responseWriter = wiParams.getWriter();
            } else {
                HttpServletResponse response =
                    (HttpServletResponse) pageContext.getResponse();
                (response).sendError(HttpServletResponse.SC_FORBIDDEN);
                return SKIP_BODY;
            }
        } catch (Exception err) {
            throw new JspException(err);
        }
        return EVAL_BODY_BUFFERED;
    }

    /**
     * Ajusta o Writer do wiParams para o mesmo do pageContext
     *
     * @param arg0 o bodyContent criado.
     */
    public void setBodyContent(BodyContent arg0) {
        super.setBodyContent(arg0);
        wiParams.setParameter(ExecuteParams.OUT_WRITER, pageContext.getOut());
    }

    /**
     * Finaliza a tag.
     *
     * @return se a p�gina deve ser processada.
     *
     * @throws JspException em caso de uma exce��o jsp.
     */
    public int doEndTag() throws JspException {
        try {
            getBodyContent().writeOut(responseWriter);
        } catch (IOException err) {
            throw new JspException(err);
        }
        return SKIP_PAGE;
    }
}
