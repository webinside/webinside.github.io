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
import br.com.webinside.runtime.integration.Condition;
import br.com.webinside.runtime.integration.Validator;

/**
 * Classe que implementa um TagLib para a função IF.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.1 $
 */
public class If extends TagSupport {

	private static final long serialVersionUID = 1L;
    private String test;
	private String var;
    private String validation;

    /**
     * Testa a condição e valida se o body deve ser mostrado.
     *
     * @return a flag para não processar o body
     *
     * @throws JspException em caso de uma exceção jsp.
     */
    public int doStartTag() throws JspException {
        int ret = SKIP_BODY;
        Object obj = pageContext.getRequest().getAttribute("wiParams");
        if (obj instanceof ExecuteParams) {
            ExecuteParams wiParams = (ExecuteParams) obj;
            try {
                if (validation != null &&
                		validation.equalsIgnoreCase("true")) {
                    if (Validator.isDisabledCondition(wiParams.getWIMap(), test)) {
                        reset();
                        return SKIP_BODY;
                    }
                }
                Condition cond =
                    new Condition(wiParams.getWIMap(), getTest());                
                boolean status = cond.execute();                
                if (status) {
                    ret = EVAL_BODY_INCLUDE;
                }
                if (getVar() != null) {
                  wiParams.getWIMap().put(getVar(), status + "");
                }
            } catch (Exception err) {
            	wiParams.getErrorLog().write("If", "taglib", err);
            	throw new JspException(err);
            }
        }
        reset();
        return ret;
    }

    private void reset() {
    	test = null;
    	var = null;
    	validation = null;
    }
    
    /**
     * Retorna condição utilizada.
     *
     * @return condição utilizada.
     */
    public String getTest() {
        return test;
    }

    /**
     * Define a condição a ser utilizada.
     *
     * @param s condição a ser utilizada.
     */
    public void setTest(String s) {
        test = s;
    }

	/**
	 * Retorna a variável para armazenar o resultado.
	 *
	 * @return a variável para armazenar o resultado.
	 */
	public String getVar() {
		return var;
	}

	/**
	 * Define a variável para armazenar o resultado.
	 *
	 * @param s a variável para armazenar o resultado.
	 */
	public void setVar(String s) {
		var = s;
	}
	
	public String getValidation() {
		return validation;
	}

	public void setValidation(String validation) {
		this.validation = validation;
	}

}
