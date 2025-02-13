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

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import br.com.webinside.runtime.component.Page;
import br.com.webinside.runtime.core.Context;
import br.com.webinside.runtime.core.ExecuteParams;
import br.com.webinside.runtime.core.ExecuteParamsEnum;
import br.com.webinside.runtime.util.WIMap;

/**
 * Classe que implementa um TagLib para inicializar uma p�gina.
 *
 * @author Geraldo Moraes
 * @version $Revision: 1.1 $
 */
public class InitPage extends TagSupport {

	private static final long serialVersionUID = 1L;
	private String name;
    private boolean exit;
    private ExecuteParams wiParams;

    /**
     * Executa a fun��o.
     *
     * @return a flag para n�o processar o body
     *
     * @throws JspException em caso de uma exce��o jsp.
     */
    public int doStartTag() throws JspException {
        exit = true;
        wiParams = null;
        try {
            ServletRequest req = pageContext.getRequest();
            Object obj = req.getAttribute("wiParams");            
            if (obj instanceof ExecuteParams) {
                wiParams = (ExecuteParams) obj;
                if (wiParams.getRequestAttribute("wiGrid") == null) {
	                wiParams.setParameter(ExecuteParamsEnum.OUT_WRITER,
	                    pageContext.getOut());   
                }
                if ((getName() != null) && (wiParams.getPage() == null)) {
                	// usado para inicializar normalmente uma pagina
                    Page page = new Page(getName());
                    wiParams.getProject().getPages().putElement(page);
                    pageContext.setAttribute("wipage", page);
                    wiParams.setParameter(ExecuteParamsEnum.PAGE, page);
                    return EVAL_BODY_INCLUDE;
                }
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
     * @return se a p�gina deve ser processada.
     *
     * @throws JspException em caso de uma exce��o jsp.
     */
    public int doEndTag() throws JspException {
    	if ((wiParams != null) && (wiParams.getWIMap() == null)) {
    		try {	
    			initWIMap();
    		} catch (Exception err) {
    			throw new JspException(err);
    		}
    	}    
    	if (!exit) {
        	if (wiParams != null) {
        		WIMap wiMap = wiParams.getWIMap();
                pageContext.getRequest().setAttribute("wiMap", wiMap);
        	}
    		return EVAL_PAGE;
    	}
    	return SKIP_PAGE;
    }

    private void initWIMap() throws Exception {
		WIMap wiMap = new Context(wiParams).getWIMap(true);
        wiParams.setParameter(ExecuteParamsEnum.WI_MAP, wiMap);
        String sqlLog = wiParams.getProject().getSqlLog();
        wiParams.getDatabaseAliases().setLog(sqlLog);
        wiParams.getDatabaseAliases().loadDatabases(wiParams.getProject());
    }
    
    /**
     * Nome da p�gina.
     *
     * @return o nome da p�gina.
     */
    public String getName() {
        return name;
    }

    /**
     * Nome da p�gina.
     *
     * @param name indica o id da p�gina.
     */
    public void setName(String name) {
        this.name = name;
    }
}
