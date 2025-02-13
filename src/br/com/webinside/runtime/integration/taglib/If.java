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
import javax.servlet.jsp.tagext.TagSupport;

import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.integration.Condition;
import br.com.webinside.runtime.integration.Validator;

/**
 * Classe que implementa um TagLib para a fun��o IF.
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
     * Testa a condi��o e valida se o body deve ser mostrado.
     *
     * @return a flag para n�o processar o body
     *
     * @throws JspException em caso de uma exce��o jsp.
     */
    public int doStartTag() throws JspException {
        int ret = SKIP_BODY;
        Integer ifSeq = (Integer) pageContext.getAttribute("wi_tag_if_level");
        ifSeq = (ifSeq == null) ? 1 : ifSeq + 1;
        pageContext.setAttribute("wi_tag_if_level", ifSeq);                
        // Inicio do contexto WI
        Object obj = pageContext.getRequest().getAttribute("wiParams");
        if (obj instanceof ExecuteParams) {
            ExecuteParams wiParams = (ExecuteParams) obj;
            try {
            	// validation utilizado na validacao de campos do formulario
                if (validation != null &&
                		validation.equalsIgnoreCase("true")) {
                    if (Validator.isDisabledCondition(wiParams.getWIMap(), test)) {
                        reset();
                        return SKIP_BODY;
                    }
                }
                Condition cond = new Condition(wiParams.getWIMap(), getTest());                
                boolean status = cond.execute();                
                if (status) ret = EVAL_BODY_INCLUDE;
                pageContext.setAttribute("wi_tag_else_" + ifSeq, status);
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
    
    @Override
	public int doEndTag() throws JspException {
        Integer ifSeq = (Integer) pageContext.getAttribute("wi_tag_if_level");
        pageContext.setAttribute("wi_tag_if_level", ifSeq - 1);                
		return super.doEndTag();
	}
    
	protected void reset() {
    	test = null;
    	var = null;
    	validation = null;
    }
    
    /**
     * Retorna condi��o utilizada.
     *
     * @return condi��o utilizada.
     */
    public String getTest() {
        return test;
    }

    /**
     * Define a condi��o a ser utilizada.
     *
     * @param s condi��o a ser utilizada.
     */
    public void setTest(String s) {
        test = s;
    }

	/**
	 * Retorna a vari�vel para armazenar o resultado.
	 *
	 * @return a vari�vel para armazenar o resultado.
	 */
	public String getVar() {
		return var;
	}

	/**
	 * Define a vari�vel para armazenar o resultado.
	 *
	 * @param s a vari�vel para armazenar o resultado.
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
