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

import java.io.PrintWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import br.com.webinside.runtime.core.EngFunction;
import br.com.webinside.runtime.core.ExecuteParams;

/**
 * Classe que implementa um TagLib para gerar um Grid.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.1 $
 */
public class GenerateGrid extends TagSupport {

	private static final long serialVersionUID = 1L;
	private String name;

    /**
     * Executa a fun��o.
     *
     * @return a flag para n�o processar o body
     *
     * @throws JspException em caso de uma exce��o jsp.
     */
    public int doStartTag() throws JspException {
        Object obj = pageContext.getRequest().getAttribute("wiParams");
        if (obj instanceof ExecuteParams) {
            ExecuteParams wiParams = (ExecuteParams) obj;
            PrintWriter outWriter = wiParams.getWriter(false);
            if (wiParams.getRequestAttribute("wiGrid") == null) {
	            wiParams.setParameter(ExecuteParams.OUT_WRITER, 
	            		pageContext.getOut());
            }
            try {
                EngFunction.generateGrid(wiParams, getName());                
            } catch (Exception err) {
            	wiParams.getErrorLog().write("GenerateGrid", "taglib", err);
            	wiParams.removeRequestAttribute("wiException");
                throw new JspException(err);
            }
            wiParams.setParameter(ExecuteParams.OUT_WRITER, outWriter);
        }
        return SKIP_BODY;
    }
    
    /**
     * Name indica o nome do objeto a ser utilizado.
     *
     * @return o nome do objeto a ser utilizado.
     */
    public String getName() {
        return name;
    }

    /**
     * Name indica o nome do objeto a ser utilizado.
     *
     * @param name indica o nome do objeto a ser utilizado.
     */
    public void setName(String name) {
        this.name = name;
    }

}
