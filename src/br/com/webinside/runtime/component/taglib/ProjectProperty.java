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

import br.com.webinside.runtime.component.AbstractProject;
import br.com.webinside.runtime.core.ExecuteParams;

/**
 * Classe que adiciona uma propriedade do projeto gerenciada como coleção.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.2 $
 */
public class ProjectProperty extends TagSupport {

	private static final long serialVersionUID = 1L;
	private String type;
    private String name;
    private String value;

    /**
     * Executa a transformação.
     *
     * @return a flag para não processar o body
     *
     * @throws JspException em caso de uma exceção jsp.
     */
    public int doStartTag() throws JspException {
        Object obj = pageContext.getRequest().getAttribute("wiParams");
        if (obj instanceof ExecuteParams) {
            ExecuteParams wiParams = (ExecuteParams) obj;
            AbstractProject proj = wiParams.getProject();
            if (getType().equals("InitParam")) {
                if (wiParams.getWISession().isNew()) {
                    proj.addInitParam(getName(), getValue());
                }
            } else if (getType().equals("Function")) {
                proj.addFunction(getName(), getValue());
            } else if (getType().equals("SecureVar")) {
                proj.addSecureVar(getName());
            } else if (getType().equals("DBLogColumn")) {
                proj.addDBLogColumn(getName(), getValue());
            }
        }
        return SKIP_BODY;
    }

    /**
     * Indica o nome a ser armazenado.
     *
     * @return o nome a ser armazenado.
     */
    public String getName() {
        return name;
    }

    /**
     * Define o nome a ser armazenado
     *
     * @param name o nome a ser armazenado.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Indica o tipo de propriedade.
     *
     * @return o tipo de propriedade.
     */
    public String getType() {
        return type;
    }

    /**
     * Define o tipo de propriedade.
     *
     * @param type o tipo de propriedade.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Indica o valor a ser armazenado.
     *
     * @return o valor a ser armazenado.
     */
    public String getValue() {
        return value;
    }

    /**
     * Define o valor a ser armazenado.
     *
     * @param value o valor a ser armazenado.
     */
    public void setValue(String value) {
        this.value = value;
    }
}
