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
import javax.servlet.jsp.tagext.TagSupport;

import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.integration.IntFunction;

/**
 * Classe que implementa um TagLib para a execução de uma função.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.1 $
 */
public class Function extends TagSupport {

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
	            String resp = 
	            	IntFunction.executeFunctionWI(expr, wiParams, null);
	            pageContext.getOut().print(resp);
            } catch (Exception err) {
            	wiParams.getErrorLog().write("Function", "taglib", err);
            	throw new JspException(err);
            }
        }
        return SKIP_BODY;
    }

    /**
     * Retorna expressão utilizada.
     *
     * @return expressão utilizada.
     */
    public String getExpr() {
        return expr;
    }

    /**
     * Define a expressão a ser utilizada.
     *
     * @param s expressão a ser utilizada.
     */
    public void setExpr(String s) {
        expr = s;
    }
}
