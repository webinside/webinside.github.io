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

import java.io.PrintWriter;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.core.ExecuteParamsEnum;
import br.com.webinside.runtime.core.RtmFunction;

/**
 * Classe que implementa um TagLib para gerar uma Combo.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.3 $
 */
public class GenerateCombo extends TagSupport {

	private static final long serialVersionUID = 1L;
	private String name;

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
            PrintWriter outWriter = wiParams.getWriter(false);
            wiParams.setParameter(ExecuteParamsEnum.OUT_WRITER, pageContext.getOut());
            try {
                RtmFunction.generateCombo(wiParams, getName());                
            } catch (Exception err) {
            	wiParams.getErrorLog().write("GenerateCombo", "taglib", err);
            	throw new JspException(err);
            }
            wiParams.setParameter(ExecuteParamsEnum.OUT_WRITER, outWriter);
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
