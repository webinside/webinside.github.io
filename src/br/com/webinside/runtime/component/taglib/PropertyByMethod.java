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

import br.com.webinside.runtime.component.ReportRef;
import br.com.webinside.runtime.component.TreeViewElement;
import br.com.webinside.runtime.component.UpdateElement;
import br.com.webinside.runtime.util.WIMap;

/**
 * Classe que define uma propriedade generica baseada no metodo.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.1 $
 */
public class PropertyByMethod extends TagSupport {

	private static final long serialVersionUID = 1L;
	private String name;
    private String method;
    private String arg1;
    private String arg2;

    /**
     * Executa a transformação.
     *
     * @return a flag para não processar o body
     *
     * @throws JspException em caso de uma exceção jsp.
     */
    public int doStartTag() throws JspException {
        Object obj = pageContext.getAttribute(getName());
        if (getMethod().equals("setMessageFalse")) {
            UpdateElement upd = (UpdateElement) obj;
            upd.setMessageFalse(getArg1(), getArg2());
        }
        if (getMethod().equals("addContent")) {
        	TreeViewElement tv = (TreeViewElement) obj;
        	tv.addContent(getArg1(), getArg2());
        }
        if (getMethod().equals("reportParameter")) {
        	ReportRef rep = (ReportRef) obj;
        	WIMap params = rep.getParameters();
        	if (params == null) {
        		params = new WIMap();
        	}
        	params.put(getArg1(), getArg2());
    		rep.setParameters(params);
        }
        return SKIP_BODY;
    }

    /**
     * Nome da variável no PageContext.
     *
     * @return indica o nome da variável no PageContext.
     */
    public String getName() {
        return name;
    }

    /**
     * Nome da variável no PageContext.
     *
     * @param name o nome da variável no PageContext.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the arg1.
     */
    public String getArg1() {
        return arg1;
    }

    /**
     * DOCUMENT ME!
     *
     * @param arg1 The arg1 to set.
     */
    public void setArg1(String arg1) {
        this.arg1 = arg1;
    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the arg2.
     */
    public String getArg2() {
        return arg2;
    }

    /**
     * DOCUMENT ME!
     *
     * @param arg2 The arg2 to set.
     */
    public void setArg2(String arg2) {
        this.arg2 = arg2;
    }

    /**
     * DOCUMENT ME!
     *
     * @return Returns the method.
     */
    public String getMethod() {
        return method;
    }

    /**
     * DOCUMENT ME!
     *
     * @param method The method to set.
     */
    public void setMethod(String method) {
        this.method = method;
    }
}
