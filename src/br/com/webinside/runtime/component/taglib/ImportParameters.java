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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.integration.IntFunction;

/**
 * Classe que implementa a execução da importação de variáveis.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.1 $
 */
public class ImportParameters extends TagSupport {

	private static final long serialVersionUID = 1L;
	private String expr;

    /**
     * Executa a função.
     *
     * @return a flag para não processar o body
     *
     * @throws JspException em caso de uma exceção jsp.
     */
    public int doStartTag() throws JspException {
        Object obj = pageContext.getRequest().getAttribute("wiParams");
        if (obj instanceof ExecuteParams) {
            ExecuteParams wiParams = (ExecuteParams) obj;
            try {
            	IntFunction.importParameters(wiParams.getWIMap(), getExpr());
            } catch (Exception err) {
            	wiParams.getErrorLog().write("ImportParameters", "taglib", err);
            	throw new JspException(err);
            }
        }
        return SKIP_BODY;
    }
    
    /**
     * Expr indica a expressão de variáveis.
     *
     * @return a expressão de variáveis.
     */
    public String getExpr() {
        return expr;
    }

    /**
     * Define a expressão de variáveis.
     *
     * @param expr indica a expressão de variáveis.
     */
    public void setExpr(String expr) {
        this.expr = expr;
    }

}
