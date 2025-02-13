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

/**
 * Classe que remove uma vari�vel do wi.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.2 $
 */
public class Remove extends TagSupport {

	private static final long serialVersionUID = 1L;
    private String var;

    /**
     * Executa a transforma��o.
     *
     * @return a flag para n�o processar o body
     *
     * @throws JspException em caso de uma exce��o jsp.
     */
    public int doStartTag() throws JspException {
        Object obj = pageContext.getRequest().getAttribute("wiParams");
        if (obj instanceof ExecuteParams) {
            ExecuteParams wiParams = (ExecuteParams) obj;
            try {
                var = getVar().toLowerCase().trim();
                if (!var.startsWith("wi.")) {
                    wiParams.getWIMap().remove(var);
                }
                if (var.equals("wi.session.id")) {
                	wiParams.getWIMap().put("wi.session.id", "invalidate");;
                }
            } catch (Exception err) {
                wiParams.getErrorLog().write("Remove", "taglib", err);
                throw new JspException(err);
            }
        }
        return SKIP_BODY;
    }


    /**
     * Retorna a vari�vel java que ser� removida.
     *
     * @return a vari�vel java que ser� removida.
     */
    public String getVar() {
        return var;
    }

    /**
     * Define a vari�vel java que ser� removida.
     *
     * @param string a vari�vel java que ser� removida.
     */
    public void setVar(String string) {
        var = string;
    }

}
