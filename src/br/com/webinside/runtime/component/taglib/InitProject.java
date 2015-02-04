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

package br.com.webinside.runtime.component.taglib;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import br.com.webinside.runtime.component.Project;
import br.com.webinside.runtime.core.ExecuteParams;

/**
 * Classe que implementa um TagLib para o projeto.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.1 $
 */
public class InitProject extends TagSupport {

	private static final long serialVersionUID = 1L;
    private boolean exit;

    /**
     * Executa a função.
     *
     * @return a flag para não processar o body
     *
     * @throws JspException em caso de uma exceção jsp.
     */
    public int doStartTag() throws JspException {
        exit = true;
        try {
            Object obj = pageContext.getRequest().getAttribute("wiParams");
            ServletRequest req = pageContext.getRequest();
            String projId = (String) req.getAttribute("wiProject");
            if ((projId != null) && obj instanceof ExecuteParams) {
                ExecuteParams wiParams = (ExecuteParams) obj;
                Object cLoader = 
                	pageContext.getServletContext().getAttribute("classloader");
                if (cLoader == null) {
                	cLoader = getClass().getClassLoader();
                }
                wiParams.setParameter(ExecuteParams.CLASSLOADER, cLoader);
                Project project = new Project(projId);
                wiParams.setParameter(ExecuteParams.PROJECT, project);
                pageContext.setAttribute("project", project);
                exit = false;
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
     * @return se a página deve ser processada.
     *
     * @throws JspException em caso de uma exceção jsp.
     */
    public int doEndTag() throws JspException {
        return ((exit) ? SKIP_PAGE
                       : EVAL_PAGE);
    }
}
